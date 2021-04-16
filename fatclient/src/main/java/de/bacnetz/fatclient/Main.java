package de.bacnetz.fatclient;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pivot.wtk.DesktopApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import de.bacnetz.common.utils.NetworkUtils;
import de.bacnetz.common.utils.Utils;
import de.bacnetz.configuration.ConfigurationManager;
import de.bacnetz.controller.DefaultMessage;
import de.bacnetz.controller.Message;
import de.bacnetz.conversion.ByteArrayToMessageConverter;
import de.bacnetz.devices.DeviceProperty;
import de.bacnetz.devices.DevicePropertyType;
import de.bacnetz.devices.ObjectType;
import de.bacnetz.factory.DefaultMessageFactory;
import de.bacnetz.factory.MessageFactory;
import de.bacnetz.stack.APDU;
import de.bacnetz.stack.ConfirmedServiceChoice;
import de.bacnetz.stack.NPDU;
import de.bacnetz.stack.ObjectIdentifierServiceParameter;
import de.bacnetz.stack.PDUType;
import de.bacnetz.stack.ServiceParameter;
import de.bacnetz.stack.TagClass;
import de.bacnetz.stack.VirtualLinkControl;
import de.bacnetz.threads.WhoIsRunnable;
import de.bacnetz.vendor.VendorMap;

/**
 * This class contains the main method to start the fatclient. To start the
 * server, use de.bacnetz.App in the bacnetz project.
 * 
 * <h1>Build order</h1>
 * <ol>
 * <li />common
 * <li />api
 * <li />bacnetz
 * <li />jsonrpc
 * <li />fatclient
 * <li />server (Takes very, very long to build because it packages the angular
 * app. It takes about 15 minutes.)
 * </ol>
 * 
 * <h1>wireshark display filter</h1>
 * 
 * <pre>
 * ip.src == 192.168.2.1 or ip.src == 192.168.2.2
 * bacnet || bvlc || bacapp
 * </pre>
 */
public class Main {

    private static final Logger LOG = LogManager.getLogger(Main.class);

    private static final class WhoIsBroadcastListener implements Runnable {

        private boolean done = false;

        private BroadcastDataListener broadcastDataListener;

        @Override
        public void run() {

            try {

                // this will open the broadcast socket on 127.0.0.1 or even 0.0.0.0
                final DatagramSocket broadcastDatagramSocket = new DatagramSocket(
                        ConfigurationManager.BACNET_PORT_DEFAULT_VALUE);
                broadcastDatagramSocket.setBroadcast(true);
                broadcastDatagramSocket.setSoTimeout(1000);

                final byte[] data = new byte[1024];
                final DatagramPacket datagramPacket = new DatagramPacket(data, data.length);

                while (!done) {

                    // blocking call
                    LOG.info("Receiving ...");
                    try {
                        broadcastDatagramSocket.receive(datagramPacket);
                    } catch (final SocketTimeoutException e) {
                        continue;
                    }
                    LOG.info("Receiving done.");

                    final int bytesReceived = datagramPacket.getLength();

                    if (bytesReceived >= data.length) {
                        throw new RuntimeException("Buffer too small. Might have been truncated!");
                    }

                    final InetAddress datagramPacketAddress = datagramPacket.getAddress();
                    LOG.trace(datagramPacketAddress + "isAnyLocalAddress(): "
                            + datagramPacketAddress.isAnyLocalAddress());
                    LOG.trace(datagramPacketAddress + "isLinkLocalAddress(): "
                            + datagramPacketAddress.isLinkLocalAddress());
                    LOG.trace(datagramPacketAddress + "isLoopbackAddress(): "
                            + datagramPacketAddress.isLoopbackAddress());

                    final SocketAddress datagramPacketSocketAddress = datagramPacket.getSocketAddress();

                    // do not process your own broadcast messages
                    final String localIp = "192.168.2.2";
                    if (datagramPacketAddress.equals(InetAddress.getByName(localIp))) {
                        LOG.info("Ignoring message from own IP!");
                        continue;
                    }

                    // DEBUG
                    LOG.info("<<< Received from inetAddress: " + datagramPacketAddress + " From socketAddress "
                            + datagramPacketSocketAddress + " Data: "
                            + Utils.byteArrayToStringNoPrefix(datagramPacket.getData()));
                    LOG.info("<<< " + Utils.byteArrayToStringNoPrefix(data));

                    broadcastDataListener.process(data);

                }

                broadcastDatagramSocket.close();
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }

        public void stop() {
            done = true;
        }

        public boolean isDone() {
            return done;
        }

        public void setDone(final boolean done) {
            this.done = done;
        }

        public BroadcastDataListener getBroadcastDataListener() {
            return broadcastDataListener;
        }

        public void setBroadcastDataListener(final BroadcastDataListener broadcastDataListener) {
            this.broadcastDataListener = broadcastDataListener;
        }
    }

    /**
     * Next steps: When the DefaultMessageController receives a I_AM response in
     * processAPDUMessage(), then call a listener and hand over the device that
     * answered. The listener has to insert a node into the GUI device tree for that
     * device.
     * 
     * <h1>wireshark filters</h1>
     * 
     * <pre>
     * ip.dst == 192.168.2.1
     * (ip.src == 192.168.2.1 || ip.dst == 192.168.2.1) && ( ip.src == 192.168.2.2 || ip.dst == 192.168.2.2)
     * </pre>
     * 
     * @param args
     * @throws FileNotFoundException
     * @throws IOException
     * @throws InterruptedException
     */
    public static void main(final String[] args) throws FileNotFoundException, IOException, InterruptedException {

//        final String sourceIP = "127.0.0.1";
        final String sourceIP = "192.168.2.2";

//        final int sourcePort = 50212;
        final int sourcePort = 47808;

//        final String destinationIP = "127.0.0.1";
        final String destinationIP = "192.168.2.1";
//        final String destinationIP = "192.168.0.108";
//        final String destinationIP = "0.0.0.0";

        final int destinationPort = 47808;

//        final int bacnetID = 3711630;
//        final int bacnetID = 44;
        final int bacnetID = 0;
//        final int bacnetID = 10000;
//        final int bacnetID = 1;

//        broadcastWhoIs();

//        requestObjectListSize(destinationIP, destinationPort, bacnetID);

        requestObjectList(sourceIP, sourcePort, destinationIP, destinationPort, ObjectType.DEVICE, bacnetID);
//        requestObjectList(sourceIP, sourcePort, destinationIP, destinationPort, ObjectType.ANALOG_VALUE, bacnetID);

//        requestPropertiesMultipleSystemStatus(destinationIP, destinationPort, ObjectType.DEVICE, bacnetID);

//        requestPropertiesMultipleAll(sourceIP, sourcePort, destinationIP, destinationPort, ObjectType.DEVICE, bacnetID);
//        requestPropertiesMultipleAll(sourceIP, sourcePort, destinationIP, destinationPort, ObjectType.ANALOG_VALUE,
//                bacnetID);

        // TODO: keep a listener open on the port to actually retrieve the COV
        // notifications
        // subscribeCOV(sourceIP, sourcePort, destinationIP, destinationPort);

//        startFatClient(args);
    }

    private static void broadcastWhoIs() throws SocketException {

        final WhoIsBroadcastListener whoIsBroadcastListener = new WhoIsBroadcastListener();
        whoIsBroadcastListener.setBroadcastDataListener(new BroadcastDataListener() {

            @Override
            public void process(final byte[] data) {
                try {

                    final ByteArrayToMessageConverter converter = new ByteArrayToMessageConverter();
                    converter.setPayloadLength(data.length);
                    converter.setPayloadOffset(0);
                    converter.setVendorMap(VendorMap.processVendorMap());

                    final DefaultMessage responseDefaultMessage = converter.convert(data);

                    LOG.info(responseDefaultMessage);

                } catch (final Exception e) {
                    LOG.error(e.getMessage(), e);
                }
            }

        });
        final Thread thread = new Thread(whoIsBroadcastListener);

        thread.start();

        final MessageFactory messageFactory = new DefaultMessageFactory();
        final Message whoIsMessage = messageFactory.whoIsMessage();
        final byte[] whoIsMessageBuffer = whoIsMessage.getBytes();

//        final List<InetAddress> listAllBroadcastAddresses = NetworkUtils.listAllBroadcastAddresses();
        final List<InetAddress> listAllBroadcastAddresses = NetworkUtils.getBroadcastAddressesByName("en4");

        // DEBUG
        LOG.trace(listAllBroadcastAddresses);

        listAllBroadcastAddresses.stream().forEach(address -> {

            LOG.info(address);

            try {
                final DatagramSocket socket = new DatagramSocket();
                socket.setBroadcast(true);

                final DatagramPacket packet = new DatagramPacket(whoIsMessageBuffer, whoIsMessageBuffer.length, address,
                        ConfigurationManager.BACNET_PORT_DEFAULT_VALUE);

                LOG.info("Sending ...");
                socket.send(packet);
                LOG.info("Sending done.");

//                LOG.info("Packet receiving ...");
//                final byte[] inBuffer = new byte[512];
//                final DatagramPacket inPacket = new DatagramPacket(inBuffer, inBuffer.length);
//                socket.receive(inPacket);
//                LOG.info("Packet receiving done.");

                socket.close();

                Thread.sleep(3000);

                whoIsBroadcastListener.stop();
            } catch (final IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private static DefaultMessage subscribeCOV(final String sourceIP, final int sourcePort, final String destinationIP,
            final int destinationPort) throws IOException {

        final InetAddress destinationAddress = InetAddress.getByName(destinationIP);
        final DatagramSocket socket = socketByInterfaceIPAndPort(sourceIP, sourcePort);

        final Thread resubscribeThread = new Thread(new Runnable() {

            @Override
            public void run() {

                while (true) {

                    LOG.info("Subscribing ...");

                    int invokeId = 1;
                    final int subscriptionLifetimeInSeconds = 60;

                    final DefaultMessage outMessage = covSubscription(ObjectType.ANALOG_VALUE, 10000, invokeId,
                            subscriptionLifetimeInSeconds);
                    invokeId++;

                    final byte[] outBuffer = outMessage.getBytes();
                    final DatagramPacket outPacket = new DatagramPacket(outBuffer, outBuffer.length, destinationAddress,
                            destinationPort);
                    try {
                        socket.send(outPacket);
                    } catch (final IOException e) {
                        LOG.error(e.getMessage(), e);
                    }

                    try {
                        Thread.sleep(subscriptionLifetimeInSeconds * 1000);
                    } catch (final InterruptedException e) {
                        LOG.error(e.getMessage(), e);
                    }
                }

            }

        });
        resubscribeThread.start();

        //
        // ONLY WORKS WITH UDP!!! ONLY WORKS WITH UDP!!! ONLY WORKS WITH UDP!!! ONLY
        // WORKS WITH UDP!!!
        // ONLY WORKS WITH UDP!!! ONLY WORKS WITH UDP!!! ONLY WORKS WITH UDP!!! ONLY
        // WORKS WITH UDP!!!
        // ONLY WORKS WITH UDP!!! ONLY WORKS WITH UDP!!! ONLY WORKS WITH UDP!!! ONLY
        // WORKS WITH UDP!!!
        //

        //
        // Send
        //

//        final InetAddress destinationAddress = InetAddress.getByName(destinationIP);
//        final DatagramSocket socket = socketByInterfaceIPAndPort(sourceIP, sourcePort);

//        final byte[] outBuffer = outMessage.getBytes();
//        final DatagramPacket outPacket = new DatagramPacket(outBuffer, outBuffer.length, destinationAddress,
//                destinationPort);
//        socket.send(outPacket);

        LOG.info("Packet sent!");

        //
        // Receive
        //

        DefaultMessage responseDefaultMessage = null;

        for (int i = 0; i < 100; i++) {

            LOG.info("Packet receiving ... Waiting for packet '" + i + "' ...");
            final byte[] inBuffer = new byte[512];
            final DatagramPacket inPacket = new DatagramPacket(inBuffer, inBuffer.length);
            socket.receive(inPacket);
            LOG.info("Packet receiving done.");

            final byte[] data = inPacket.getData();
            final int length = inPacket.getLength();
            final int offset = inPacket.getOffset();

            final String bytesToHex = Utils.bytesToHex(data, offset, length);
            LOG.info("Response Packet received as hex bytes: '{}'", bytesToHex);

            final ByteArrayToMessageConverter converter = new ByteArrayToMessageConverter();
            converter.setPayloadLength(length);
            converter.setPayloadOffset(offset);
            converter.setVendorMap(VendorMap.processVendorMap());

            responseDefaultMessage = converter.convert(data);

            LOG.info(responseDefaultMessage);
        }

        LOG.info("done");

        return responseDefaultMessage;
    }

    private static DefaultMessage covSubscription(final ObjectType objectType, final int instanceId, final int invokeId,
            final int subscriptionLifetimeInSeconds) {

        //
        // Virtual Link Control
        //

        final VirtualLinkControl virtualLinkControl = new VirtualLinkControl();
        virtualLinkControl.setType(0x81);
        virtualLinkControl.setFunction(0x0A);
        // is set later, when the full package data was added
        virtualLinkControl.setLength(0x00);

        //
        // NPDU
        //

        // NPDU including destination network information
        final NPDU outNpdu = new NPDU();
        outNpdu.setVersion(0x01);

        // no additional information
        // this works, if the cp is connected to the device directly via 192.168.2.1
        outNpdu.setControl(0x04); // expecting reply
        // npdu.setControl(0x2c);

        //
        // APDU
        //

        // subscriber process id ( 0x01 is set as the id )
        final ServiceParameter subscriberProcessIdServiceParameter = new ServiceParameter();
        subscriberProcessIdServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        subscriberProcessIdServiceParameter.setTagNumber(0x00);
        subscriberProcessIdServiceParameter.setLengthValueType(0x01);
        subscriberProcessIdServiceParameter.setPayload(new byte[] { (byte) 0x01 }); // the id of the subscriber

        // this object identifier has to be context specific. I do not know why
        final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
        objectIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        objectIdentifierServiceParameter.setTagNumber(0x01);
        objectIdentifierServiceParameter.setLengthValueType(0x04);
        objectIdentifierServiceParameter.setObjectType(objectType);
        objectIdentifierServiceParameter.setInstanceNumber(instanceId);

        // issue confirmed notifications (0x00 == FALSE)
        final ServiceParameter confirmedNotificationsServiceParameter = new ServiceParameter();
        confirmedNotificationsServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        confirmedNotificationsServiceParameter.setTagNumber(0x02);
        confirmedNotificationsServiceParameter.setLengthValueType(0x01);
        confirmedNotificationsServiceParameter.setPayload(new byte[] { (byte) 0x00 }); // send unconfirmed notifications

        final ServiceParameter subscriptionLifetimeServiceParameter = new ServiceParameter();
        subscriptionLifetimeServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        subscriptionLifetimeServiceParameter.setTagNumber(0x03);
        subscriptionLifetimeServiceParameter.setLengthValueType(0x01);
        subscriptionLifetimeServiceParameter.setPayload(new byte[] { (byte) subscriptionLifetimeInSeconds }); // 0x78 =
                                                                                                              // 120d =
                                                                                                              // 120
                                                                                                              // seconds
                                                                                                              // = 2
        // min

        final APDU outApdu = new APDU();
        outApdu.setPduType(PDUType.CONFIRMED_SERVICE_REQUEST_PDU);
        outApdu.setInvokeId(invokeId);
        outApdu.setConfirmedServiceChoice(ConfirmedServiceChoice.SUBSCRIBE_COV);
        // max segments, 1 is the exponent to the power of 2, that means 2
        outApdu.setMaxResponseSegmentsAccepted(1);
        outApdu.setSizeOfMaximumAPDUAccepted(5);
//        outApdu.setVendorMap(vendorMap);

        outApdu.getServiceParameters().add(subscriberProcessIdServiceParameter);
        outApdu.getServiceParameters().add(objectIdentifierServiceParameter);
        outApdu.getServiceParameters().add(confirmedNotificationsServiceParameter);
        outApdu.getServiceParameters().add(subscriptionLifetimeServiceParameter);

        final DefaultMessage outMessage = new DefaultMessage();
        outMessage.setVirtualLinkControl(virtualLinkControl);
        outMessage.setNpdu(outNpdu);
        outMessage.setApdu(outApdu);

        virtualLinkControl.setLength(outMessage.getDataLength());

        return outMessage;
    }

    /**
     * 
     * @param objectType
     * @param objectInstanceNumber
     * @throws UnknownHostException
     * @throws SocketException
     * @throws IOException
     */
    private static DefaultMessage requestPropertiesMultipleAll(final String sourceIP, final int sourcePort,
            final String destinationIP, final int destinationPort, final ObjectType objectType,
            final int objectInstanceNumber) throws UnknownHostException, SocketException, IOException {

        final VirtualLinkControl virtualLinkControl = new VirtualLinkControl();
        virtualLinkControl.setType(0x81);
        virtualLinkControl.setFunction(0x0A);
        // is set later, when the full package data was added
        virtualLinkControl.setLength(0x00);

        // NPDU including destination network information
        final NPDU outNpdu = new NPDU();
        outNpdu.setVersion(0x01);

        // no additional information
        // this works, if the cp is connected to the device directly via 192.168.2.1
        outNpdu.setControl(0x00);
        // npdu.setControl(0x2c);

        // this object identifier has to be context specific. I do not know why
        final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
        objectIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        objectIdentifierServiceParameter.setTagNumber(0x00);
        objectIdentifierServiceParameter.setLengthValueType(0x04);
        objectIdentifierServiceParameter.setObjectType(objectType);
        objectIdentifierServiceParameter.setInstanceNumber(objectInstanceNumber);

        final ServiceParameter propertyIdentifierServiceParameter = new ServiceParameter();
        propertyIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        propertyIdentifierServiceParameter.setTagNumber(0x01);
        propertyIdentifierServiceParameter.setLengthValueType(0x01);
        propertyIdentifierServiceParameter.setPayload(new byte[] { (byte) DeviceProperty.OBJECT_LIST });

        // request index 0 which is the length of the array
        final ServiceParameter propertyArrayIndexServiceParameter = new ServiceParameter();
        propertyArrayIndexServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        propertyArrayIndexServiceParameter.setTagNumber(0x02);
        propertyArrayIndexServiceParameter.setLengthValueType(0x01);
        propertyArrayIndexServiceParameter.setPayload(new byte[] { (byte) 0x00 });

        // {[1] opening bracket
        final ServiceParameter openingBracketServiceParameter = new ServiceParameter();
        openingBracketServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        openingBracketServiceParameter.setTagNumber(0x01);
        openingBracketServiceParameter.setLengthValueType(ServiceParameter.OPENING_TAG_CODE);

        // request ALL
        final ServiceParameter systemStatusDevicePropertyServiceParameter = new ServiceParameter();
        systemStatusDevicePropertyServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        systemStatusDevicePropertyServiceParameter.setTagNumber(0x00);
        systemStatusDevicePropertyServiceParameter.setLengthValueType(1);
        systemStatusDevicePropertyServiceParameter.setPayload(new byte[] { (byte) DevicePropertyType.ALL.getCode() });

        // {[1] closeing bracket
        final ServiceParameter closingBracketServiceParameter = new ServiceParameter();
        closingBracketServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        closingBracketServiceParameter.setTagNumber(0x01);
        closingBracketServiceParameter.setLengthValueType(ServiceParameter.CLOSING_TAG_CODE);

        final APDU outApdu = new APDU();
        outApdu.setPduType(PDUType.CONFIRMED_SERVICE_REQUEST_PDU);
        outApdu.setInvokeId(1);
        outApdu.setConfirmedServiceChoice(ConfirmedServiceChoice.READ_PROPERTY_MULTIPLE);
        // max segments, 4 is the exponent to the power of 2, that means 16
        outApdu.setMaxResponseSegmentsAccepted(4);
        outApdu.setSizeOfMaximumAPDUAccepted(3);
//        outApdu.setVendorMap(vendorMap);

        outApdu.getServiceParameters().add(objectIdentifierServiceParameter);
//        outApdu.getServiceParameters().add(propertyIdentifierServiceParameter);
//        outApdu.getServiceParameters().add(propertyArrayIndexServiceParameter);
        outApdu.getServiceParameters().add(openingBracketServiceParameter);
        outApdu.getServiceParameters().add(systemStatusDevicePropertyServiceParameter);
        outApdu.getServiceParameters().add(closingBracketServiceParameter);

        final DefaultMessage outMessage = new DefaultMessage();
        outMessage.setVirtualLinkControl(virtualLinkControl);
        outMessage.setNpdu(outNpdu);
        outMessage.setApdu(outApdu);

        virtualLinkControl.setLength(outMessage.getDataLength());

//        try {
////            192.168.2.1:47808
//            final Socket socket = new Socket("192.168.2.1", destinationPort);
//            final DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
//            outputStream.write(outMessage.getBytes());
//        } catch (final IOException e) {
//            LOG.error(e.getMessage(), e);
//        }

        //
        // ONLY WORKS WITH UDP!!! ONLY WORKS WITH UDP!!! ONLY WORKS WITH UDP!!! ONLY
        // WORKS WITH UDP!!!
        // ONLY WORKS WITH UDP!!! ONLY WORKS WITH UDP!!! ONLY WORKS WITH UDP!!! ONLY
        // WORKS WITH UDP!!!
        // ONLY WORKS WITH UDP!!! ONLY WORKS WITH UDP!!! ONLY WORKS WITH UDP!!! ONLY
        // WORKS WITH UDP!!!
        //

        final InetAddress destinationAddress = InetAddress.getByName(destinationIP);
        final byte[] outBuffer = outMessage.getBytes();

        final DatagramPacket outPacket = new DatagramPacket(outBuffer, outBuffer.length, destinationAddress,
                destinationPort);

        final DatagramSocket socket = socketByInterfaceIPAndPort(sourceIP, sourcePort);

        socket.send(outPacket);
        LOG.info("Packet sent!");

        LOG.info("Packet receiving ...");
        final byte[] inBuffer = new byte[512];
        final DatagramPacket inPacket = new DatagramPacket(inBuffer, inBuffer.length);
        socket.receive(inPacket);
        LOG.info("Packet receiving done.");

        final byte[] data = inPacket.getData();
        final int length = inPacket.getLength();
        final int offset = inPacket.getOffset();

        final String bytesToHex = Utils.bytesToHex(data, offset, length);
        LOG.info("Response Packet received as hex bytes: '{}'", bytesToHex);

        final ByteArrayToMessageConverter converter = new ByteArrayToMessageConverter();
        converter.setPayloadLength(length);
        converter.setPayloadOffset(offset);
        converter.setVendorMap(VendorMap.processVendorMap());

        final DefaultMessage responseDefaultMessage = converter.convert(data);

        LOG.info(responseDefaultMessage);

        LOG.info("done");

        return responseDefaultMessage;
    }

    /**
     * 
     * @param objectType
     * @param objectInstanceNumber
     * @throws UnknownHostException
     * @throws SocketException
     * @throws IOException
     */
    private static void requestPropertiesMultipleSystemStatus(final String destinationIP, final int destinationPort,
            final ObjectType objectType, final int objectInstanceNumber)
            throws UnknownHostException, SocketException, IOException {

        // TODO: send a message

        final VirtualLinkControl virtualLinkControl = new VirtualLinkControl();
        virtualLinkControl.setType(0x81);
        virtualLinkControl.setFunction(0x0A);
        // is set later, when the full package data was added
        virtualLinkControl.setLength(0x00);

        // NPDU including destination network information
        final NPDU outNpdu = new NPDU();
        outNpdu.setVersion(0x01);

        // no additional information
        // this works, if the cp is connected to the device directly via 192.168.2.1
        outNpdu.setControl(0x00);
        // npdu.setControl(0x2c);

//        // this object identifier has to be context specific. I do not know why
        final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
        objectIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        objectIdentifierServiceParameter.setTagNumber(0x00);
        objectIdentifierServiceParameter.setLengthValueType(0x04);
        objectIdentifierServiceParameter.setObjectType(objectType);
        objectIdentifierServiceParameter.setInstanceNumber(objectInstanceNumber);

        final ServiceParameter propertyIdentifierServiceParameter = new ServiceParameter();
        propertyIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        propertyIdentifierServiceParameter.setTagNumber(0x01);
        propertyIdentifierServiceParameter.setLengthValueType(0x01);
        propertyIdentifierServiceParameter.setPayload(new byte[] { (byte) DeviceProperty.OBJECT_LIST });

        // request index 0 which is the length of the array
        final ServiceParameter propertyArrayIndexServiceParameter = new ServiceParameter();
        propertyArrayIndexServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        propertyArrayIndexServiceParameter.setTagNumber(0x02);
        propertyArrayIndexServiceParameter.setLengthValueType(0x01);
        propertyArrayIndexServiceParameter.setPayload(new byte[] { (byte) 0x00 });

        // {[1] opening bracket
        final ServiceParameter openingBracketServiceParameter = new ServiceParameter();
        openingBracketServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        openingBracketServiceParameter.setTagNumber(0x01);
        openingBracketServiceParameter.setLengthValueType(ServiceParameter.OPENING_TAG_CODE);

        // request the system status
        final ServiceParameter systemStatusDevicePropertyServiceParameter = new ServiceParameter();
        systemStatusDevicePropertyServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        systemStatusDevicePropertyServiceParameter.setTagNumber(0x00);
        systemStatusDevicePropertyServiceParameter.setLengthValueType(1);
        systemStatusDevicePropertyServiceParameter
                .setPayload(new byte[] { (byte) DevicePropertyType.SYSTEM_STATUS.getCode() });

        // {[1] closeing bracket
        final ServiceParameter closingBracketServiceParameter = new ServiceParameter();
        closingBracketServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        closingBracketServiceParameter.setTagNumber(0x01);
        closingBracketServiceParameter.setLengthValueType(ServiceParameter.CLOSING_TAG_CODE);

        final APDU outApdu = new APDU();
        outApdu.setPduType(PDUType.CONFIRMED_SERVICE_REQUEST_PDU);
        outApdu.setInvokeId(1);
        outApdu.setConfirmedServiceChoice(ConfirmedServiceChoice.READ_PROPERTY_MULTIPLE);
        // max segments, 4 is the exponent to the power of 2, that means 16
        outApdu.setMaxResponseSegmentsAccepted(4);
        outApdu.setSizeOfMaximumAPDUAccepted(3);
//        outApdu.setVendorMap(vendorMap);

        outApdu.getServiceParameters().add(objectIdentifierServiceParameter);
//        outApdu.getServiceParameters().add(propertyIdentifierServiceParameter);
//        outApdu.getServiceParameters().add(propertyArrayIndexServiceParameter);
        outApdu.getServiceParameters().add(openingBracketServiceParameter);
        outApdu.getServiceParameters().add(systemStatusDevicePropertyServiceParameter);
        outApdu.getServiceParameters().add(closingBracketServiceParameter);

        final DefaultMessage outMessage = new DefaultMessage();
        outMessage.setVirtualLinkControl(virtualLinkControl);
        outMessage.setNpdu(outNpdu);
        outMessage.setApdu(outApdu);

        virtualLinkControl.setLength(outMessage.getDataLength());

//        try {
////            192.168.2.1:47808
//            final Socket socket = new Socket("192.168.2.1", destinationPort);
//            final DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
//            outputStream.write(outMessage.getBytes());
//        } catch (final IOException e) {
//            LOG.error(e.getMessage(), e);
//        }

        //
        // ONLY WORKS WITH UDP!!! ONLY WORKS WITH UDP!!! ONLY WORKS WITH UDP!!! ONLY
        // WORKS WITH UDP!!!
        // ONLY WORKS WITH UDP!!! ONLY WORKS WITH UDP!!! ONLY WORKS WITH UDP!!! ONLY
        // WORKS WITH UDP!!!
        // ONLY WORKS WITH UDP!!! ONLY WORKS WITH UDP!!! ONLY WORKS WITH UDP!!! ONLY
        // WORKS WITH UDP!!!
        //

        final InetAddress address = InetAddress.getByName(destinationIP);
        final byte[] outBuffer = outMessage.getBytes();

        final DatagramPacket outPacket = new DatagramPacket(outBuffer, outBuffer.length, address, destinationPort);

        final DatagramSocket socket = new DatagramSocket();
        socket.send(outPacket);
        LOG.info("Packet sent!");

        LOG.info("Packet receiving ...");
        final byte[] inBuffer = new byte[512];
        final DatagramPacket inPacket = new DatagramPacket(inBuffer, inBuffer.length);
        socket.receive(inPacket);
        LOG.info("Packet receiving done.");

        final byte[] data = inPacket.getData();
        final int length = inPacket.getLength();
        final int offset = inPacket.getOffset();

        final String bytesToHex = Utils.bytesToHex(data, offset, length);
        LOG.info("Response Packet received as hex bytes: '{}'", bytesToHex);

        final ByteArrayToMessageConverter converter = new ByteArrayToMessageConverter();
        converter.setPayloadLength(length);
        converter.setPayloadOffset(offset);
        converter.setVendorMap(VendorMap.processVendorMap());

        final DefaultMessage responseDefaultMessage = converter.convert(data);

        LOG.info(responseDefaultMessage);

        LOG.info("done");
    }

    private static void requestObjectList(final String sourceIP, final int sourcePort, final String destinationIP,
            final int destinationPort, final ObjectType objectType, final int bacnetID) throws IOException {

        final VirtualLinkControl virtualLinkControl = new VirtualLinkControl();
        virtualLinkControl.setType(0x81);
        virtualLinkControl.setFunction(0x0A);
        // not known right now. Length is set later, when the full package data was
        // added
        virtualLinkControl.setLength(0x00);

        // NPDU including destination network information
        final NPDU outNpdu = new NPDU();
        outNpdu.setVersion(0x01);

        // no additional information
        // this works, if the cp is connected to the device directly via 192.168.2.1
        outNpdu.setControl(0x00);
        // npdu.setControl(0x2c);

//        // this object identifier has to be context specific. I do not know why
        final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
        objectIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        objectIdentifierServiceParameter.setTagNumber(0x00);
        objectIdentifierServiceParameter.setLengthValueType(0x04);
        objectIdentifierServiceParameter.setObjectType(objectType);
        objectIdentifierServiceParameter.setInstanceNumber(bacnetID);

        final ServiceParameter propertyIdentifierServiceParameter = new ServiceParameter();
        propertyIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        propertyIdentifierServiceParameter.setTagNumber(0x01);
        propertyIdentifierServiceParameter.setLengthValueType(0x01);
        propertyIdentifierServiceParameter.setPayload(new byte[] { (byte) DeviceProperty.OBJECT_LIST });

//        // request index 0 which is the length of the array
//        final ServiceParameter propertyArrayIndexServiceParameter = new ServiceParameter();
//        propertyArrayIndexServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
//        propertyArrayIndexServiceParameter.setTagNumber(0x02);
//        propertyArrayIndexServiceParameter.setLengthValueType(0x01);
//        propertyArrayIndexServiceParameter.setPayload(new byte[] { (byte) 0x00 });

        final APDU outApdu = new APDU();
        outApdu.setPduType(PDUType.CONFIRMED_SERVICE_REQUEST_PDU);
        outApdu.setInvokeId(1);
        outApdu.setConfirmedServiceChoice(ConfirmedServiceChoice.READ_PROPERTY);

        // allow segmentation for large responses
        outApdu.setSegmentation(true);
        outApdu.setSegmentedResponseAccepted(true);
        outApdu.setMaxResponseSegmentsAccepted(30);
//        outApdu.setVendorMap(vendorMap);

        // page 57 in Standard 135-2012

        outApdu.getServiceParameters().add(objectIdentifierServiceParameter);
        outApdu.getServiceParameters().add(propertyIdentifierServiceParameter);
//        outApdu.getServiceParameters().add(propertyArrayIndexServiceParameter);

        final DefaultMessage outMessage = new DefaultMessage();
        outMessage.setVirtualLinkControl(virtualLinkControl);
        outMessage.setNpdu(outNpdu);
        outMessage.setApdu(outApdu);

        virtualLinkControl.setLength(outMessage.getDataLength());

//        try {
////            192.168.2.1:47808
//            final Socket socket = new Socket(destinationIP, destinationPort);
//            final DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
//            outputStream.write(outMessage.getBytes());
//        } catch (final IOException e) {
//            LOG.error(e.getMessage(), e);
//        }

        //
        // ONLY WORKS WITH UDP!!! ONLY WORKS WITH UDP!!! ONLY WORKS WITH UDP!!! ONLY
        // WORKS WITH UDP!!!
        // ONLY WORKS WITH UDP!!! ONLY WORKS WITH UDP!!! ONLY WORKS WITH UDP!!! ONLY
        // WORKS WITH UDP!!!
        // ONLY WORKS WITH UDP!!! ONLY WORKS WITH UDP!!! ONLY WORKS WITH UDP!!! ONLY
        // WORKS WITH UDP!!!
        //

        final InetAddress address = InetAddress.getByName(destinationIP);
        final byte[] outBuffer = outMessage.getBytes();

        final DatagramPacket outPacket = new DatagramPacket(outBuffer, outBuffer.length, address, destinationPort);

        // final DatagramSocket socket = new DatagramSocket();

        listInterfaces();

//        final DatagramSocket socket = socketByInterfaceNameAndPort("eth7", destinationPort);
//        final DatagramSocket socket = socketByInterfaceIPAndPort(destinationIP, destinationPort);
        final DatagramSocket socket = socketByInterfaceIPAndPort(sourceIP, sourcePort);
        socket.send(outPacket);

        LOG.info("Packet sent to '{}' !", destinationIP);

        DefaultMessage sequencedMessage = null;

        boolean done = false;
        while (!done) {

            LOG.info("Packet receiving ...");
            final byte[] inBuffer = new byte[512];
            final DatagramPacket inPacket = new DatagramPacket(inBuffer, inBuffer.length);
            socket.receive(inPacket);
            LOG.info("Packet receiving done.");

            final byte[] data = inPacket.getData();
            final int length = inPacket.getLength();
            final int offset = inPacket.getOffset();

            final String bytesToHex = Utils.bytesToHex(data, offset, length);
            LOG.info("Response Packet received as hex bytes: '{}'", bytesToHex);

            final ByteArrayToMessageConverter converter = new ByteArrayToMessageConverter();
            converter.setPayloadLength(length);
            converter.setPayloadOffset(offset);
            converter.setVendorMap(VendorMap.processVendorMap());

            final DefaultMessage responseDefaultMessage = converter.convert(data);

            LOG.info(responseDefaultMessage);

            done = true;

            // segmented response
            if (responseDefaultMessage.getApdu().isMoreSegmentsFollow()) {

                done = false;

                // reassemble the sequence, start by storing the first message in the sequence
                // into a local variable
                if (sequencedMessage == null) {
                    sequencedMessage = responseDefaultMessage;
                } else {
                    sequencedMessage.merge(responseDefaultMessage);
                }

            }

            // this segment has to be acknowledged
            if (responseDefaultMessage.getNpdu().isConfirmedRequestPDUPresent()) {
                sendAck(socket, sourceIP, sourcePort, destinationIP, destinationPort,
                        responseDefaultMessage.getApdu().getInvokeId(),
                        responseDefaultMessage.getApdu().getSequenceNumber());
            }
        }

        LOG.info("done");

    }

    private static void sendAck(final DatagramSocket socket, final String sourceIP, final int sourcePort,
            final String destinationIP, final int destinationPort, final int invokeId, final int sequenceNumber)
            throws IOException {

        final VirtualLinkControl virtualLinkControl = new VirtualLinkControl();
        virtualLinkControl.setType(0x81);
        virtualLinkControl.setFunction(0x0A);
        // is set later, when the full package data was added
        virtualLinkControl.setLength(0x00);

        // NPDU including destination network information
        final NPDU outNpdu = new NPDU();
        outNpdu.setVersion(0x01);

        // no additional information
        // this works, if the cp is connected to the device directly via 192.168.2.1
        outNpdu.setControl(0x00);
        // npdu.setControl(0x2c);

        final APDU outApdu = new APDU();
        outApdu.setPduType(PDUType.SEGMENT_ACK_PDU);
        outApdu.setInvokeId(invokeId);
        outApdu.setSequenceNumber(sequenceNumber);
        outApdu.setProposedWindowSize(16);
//        outApdu.setConfirmedServiceChoice(ConfirmedServiceChoice.READ_PROPERTY_MULTIPLE);
        // max segments, 4 is the exponent to the power of 2, that means 16
//        outApdu.setMaxResponseSegmentsAccepted(4);
//        outApdu.setSizeOfMaximumAPDUAccepted(3);
//        outApdu.setVendorMap(vendorMap);

//        outApdu.getServiceParameters().add(objectIdentifierServiceParameter);
//        outApdu.getServiceParameters().add(propertyIdentifierServiceParameter);
//        outApdu.getServiceParameters().add(propertyArrayIndexServiceParameter);
//        outApdu.getServiceParameters().add(openingBracketServiceParameter);
//        outApdu.getServiceParameters().add(systemStatusDevicePropertyServiceParameter);
//        outApdu.getServiceParameters().add(closingBracketServiceParameter);

        final DefaultMessage outMessage = new DefaultMessage();
        outMessage.setVirtualLinkControl(virtualLinkControl);
        outMessage.setNpdu(outNpdu);
        outMessage.setApdu(outApdu);

        virtualLinkControl.setLength(outMessage.getDataLength());

//        try {
////            192.168.2.1:47808
//            final Socket socket = new Socket("192.168.2.1", destinationPort);
//            final DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
//            outputStream.write(outMessage.getBytes());
//        } catch (final IOException e) {
//            LOG.error(e.getMessage(), e);
//        }

        //
        // ONLY WORKS WITH UDP!!! ONLY WORKS WITH UDP!!! ONLY WORKS WITH UDP!!! ONLY
        // WORKS WITH UDP!!!
        // ONLY WORKS WITH UDP!!! ONLY WORKS WITH UDP!!! ONLY WORKS WITH UDP!!! ONLY
        // WORKS WITH UDP!!!
        // ONLY WORKS WITH UDP!!! ONLY WORKS WITH UDP!!! ONLY WORKS WITH UDP!!! ONLY
        // WORKS WITH UDP!!!
        //

        final InetAddress address = InetAddress.getByName(destinationIP);
        final byte[] outBuffer = outMessage.getBytes();

        final DatagramPacket outPacket = new DatagramPacket(outBuffer, outBuffer.length, address, destinationPort);

//        final DatagramSocket socket = new DatagramSocket(sourcePort);
        socket.send(outPacket);

        LOG.info("Packet sent!");
    }

    private static void requestObjectListSize(final String destinationIP, final int destinationPort, final int bacnetID)
            throws UnknownHostException, SocketException, IOException {

        final VirtualLinkControl virtualLinkControl = new VirtualLinkControl();
        virtualLinkControl.setType(0x81);
        virtualLinkControl.setFunction(0x0A);
        // is set later, when the full package data was added
        virtualLinkControl.setLength(0x00);

        // NPDU including destination network information
        final NPDU outNpdu = new NPDU();
        outNpdu.setVersion(0x01);

        // no additional information
        // this works, if the cp is connected to the device directly via 192.168.2.1
        outNpdu.setControl(0x00);
        // npdu.setControl(0x2c);

//        // this object identifier has to be context specific. I do not know why
        final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
        objectIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        objectIdentifierServiceParameter.setTagNumber(0x00);
        objectIdentifierServiceParameter.setLengthValueType(0x04);
        objectIdentifierServiceParameter.setObjectType(ObjectType.DEVICE);
        objectIdentifierServiceParameter.setInstanceNumber(bacnetID);

        final ServiceParameter propertyIdentifierServiceParameter = new ServiceParameter();
        propertyIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        propertyIdentifierServiceParameter.setTagNumber(0x01);
        propertyIdentifierServiceParameter.setLengthValueType(0x01);
        propertyIdentifierServiceParameter.setPayload(new byte[] { (byte) DeviceProperty.OBJECT_LIST });

        // request index 0 which is the length of the array
        final ServiceParameter propertyArrayIndexServiceParameter = new ServiceParameter();
        propertyArrayIndexServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        propertyArrayIndexServiceParameter.setTagNumber(0x02);
        propertyArrayIndexServiceParameter.setLengthValueType(0x01);
        propertyArrayIndexServiceParameter.setPayload(new byte[] { (byte) 0x00 });

        final APDU outApdu = new APDU();
        outApdu.setPduType(PDUType.CONFIRMED_SERVICE_REQUEST_PDU);
        outApdu.setInvokeId(1);
        outApdu.setConfirmedServiceChoice(ConfirmedServiceChoice.READ_PROPERTY);
//        outApdu.setVendorMap(vendorMap);

        outApdu.getServiceParameters().add(objectIdentifierServiceParameter);
        outApdu.getServiceParameters().add(propertyIdentifierServiceParameter);
        outApdu.getServiceParameters().add(propertyArrayIndexServiceParameter);

        final DefaultMessage outMessage = new DefaultMessage();
        outMessage.setVirtualLinkControl(virtualLinkControl);
        outMessage.setNpdu(outNpdu);
        outMessage.setApdu(outApdu);

        virtualLinkControl.setLength(outMessage.getDataLength());

//        try {
////            192.168.2.1:47808
//            final Socket socket = new Socket(destinationIP, destinationPort);
//            final DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
//            outputStream.write(outMessage.getBytes());
//        } catch (final IOException e) {
//            LOG.error(e.getMessage(), e);
//        }

        //
        // ONLY WORKS WITH UDP!!! ONLY WORKS WITH UDP!!! ONLY WORKS WITH UDP!!! ONLY
        // WORKS WITH UDP!!!
        // ONLY WORKS WITH UDP!!! ONLY WORKS WITH UDP!!! ONLY WORKS WITH UDP!!! ONLY
        // WORKS WITH UDP!!!
        // ONLY WORKS WITH UDP!!! ONLY WORKS WITH UDP!!! ONLY WORKS WITH UDP!!! ONLY
        // WORKS WITH UDP!!!
        //

        final InetAddress address = InetAddress.getByName(destinationIP);
        final byte[] outBuffer = outMessage.getBytes();

        final DatagramPacket outPacket = new DatagramPacket(outBuffer, outBuffer.length, address, destinationPort);

        listInterfaces();

        final DatagramSocket socket = socketByInterfaceIPAndPort(destinationIP, destinationPort);
        socket.send(outPacket);

        LOG.info("Packet sent to '{}' !", destinationIP);

        LOG.info("Packet receiving ...");
        final byte[] inBuffer = new byte[512];
        final DatagramPacket inPacket = new DatagramPacket(inBuffer, inBuffer.length);
        socket.receive(inPacket);
        LOG.info("Packet receiving done.");

        final byte[] data = inPacket.getData();
        final int length = inPacket.getLength();
        final int offset = inPacket.getOffset();

        final String bytesToHex = Utils.bytesToHex(data, offset, length);
        LOG.info("Response Packet received as hex bytes: '{}'", bytesToHex);

        final ByteArrayToMessageConverter converter = new ByteArrayToMessageConverter();
        converter.setPayloadLength(length);
        converter.setPayloadOffset(offset);
        converter.setVendorMap(VendorMap.processVendorMap());

        final DefaultMessage responseDefaultMessage = converter.convert(data);

        LOG.info(responseDefaultMessage);

        LOG.info("done");
    }

    private static DatagramSocket socketByInterfaceIPAndPort(final String ip, final int port)
            throws SocketException, UnknownHostException {

        final Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        NetworkInterface networkInterface = null;
        InetAddress inetAddress = null;
        while (networkInterfaces.hasMoreElements()) {

            final NetworkInterface tempNetworkInterface = networkInterfaces.nextElement();
            final Enumeration<InetAddress> networkInterfaceAddresses = tempNetworkInterface.getInetAddresses();

            while (networkInterfaceAddresses.hasMoreElements()) {

                final InetAddress networkInterfaceAddress = networkInterfaceAddresses.nextElement();

                System.out.println(tempNetworkInterface + " -- " + networkInterfaceAddress);

                if (networkInterfaceAddress.equals(InetAddress.getByName(ip))) {
                    networkInterface = tempNetworkInterface;
                    inetAddress = networkInterfaceAddress;
                    break;
                }
            }

            if (networkInterface != null) {
                break;
            }
        }

        if (networkInterface == null) {
            System.err.println("Error getting the Network Interface");
            return null;
        }
        System.out.println("Preparing to using the interface: " + networkInterface.getName());

//        final Enumeration<InetAddress> networkInterfaceAddresses = networkInterface.getInetAddresses();
        final InetSocketAddress inetSocketAddress = new InetSocketAddress(inetAddress, port);

        return new DatagramSocket(inetSocketAddress);
    }

    private static DatagramSocket socketByInterfaceNameAndPort(final String interfaceName, final int port)
            throws SocketException {

        final NetworkInterface networkInterface = NetworkInterface.getByName(interfaceName);
        if (networkInterface == null) {
            System.err.println("Error getting the Network Interface");
            return null;
        }
        System.out.println("Preparing to using the interface: " + networkInterface.getName());
        final Enumeration<InetAddress> networkInterfaceAddresses = networkInterface.getInetAddresses();

        final InetSocketAddress inetSocketAddress = new InetSocketAddress(networkInterfaceAddresses.nextElement(),
                port);

        return new DatagramSocket(inetSocketAddress);
    }

    private static void listInterfaces() throws SocketException {

        final Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        while (networkInterfaces.hasMoreElements()) {

            final NetworkInterface networkInterface = networkInterfaces.nextElement();

            final Enumeration<InetAddress> networkInterfaceAddresses = networkInterface.getInetAddresses();
            while (networkInterfaceAddresses.hasMoreElements()) {

                final InetAddress networkInterfaceAddress = networkInterfaceAddresses.nextElement();

                System.out.println(networkInterface + " -- " + networkInterfaceAddress);
            }
        }
    }

    /**
     * Starts the UI (= FatClient)
     * 
     * @param args
     * @throws InterruptedException
     */
    private static void startFatClient(final String[] args) throws InterruptedException {
        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
                FatClientConfiguration.class);

        // start the Apache Pivot UI Framework
        DesktopApplicationContext.main(App.class, args);

        Thread.sleep(1000);

        runWhoIsThread();
    }

    private static void runWhoIsThread() {
        new Thread(new WhoIsRunnable()).run();
    }
}
