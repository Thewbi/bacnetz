package de.bacnetz.fatclient;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pivot.wtk.DesktopApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import de.bacnetz.common.utils.Utils;
import de.bacnetz.controller.DefaultMessage;
import de.bacnetz.conversion.ByteArrayToMessageConverter;
import de.bacnetz.devices.DeviceProperty;
import de.bacnetz.devices.DevicePropertyType;
import de.bacnetz.devices.ObjectType;
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

public class Main {

    private static final Logger LOG = LogManager.getLogger(Main.class);

    /**
     * Next steps: When the DefaultMessageController receives a I_AM response in
     * processAPDUMessage(), then call a listener and hand over the device that
     * answered. The listener has to insert a node into the GUI device tree for that
     * device.
     * 
     * @param args
     * @throws FileNotFoundException
     * @throws IOException
     * @throws InterruptedException
     */
    public static void main(final String[] args) throws FileNotFoundException, IOException, InterruptedException {

//        requestObjectListSize();
//        requestPropertiesMultipleSystemStatus(ObjectType.DEVICE, 3711630);
        requestPropertiesMultipleAll(ObjectType.DEVICE, 3711630);

//        startFatClient(args);
    }

    /**
     * 
     * @param objectType
     * @param objectInstanceNumber
     * @throws UnknownHostException
     * @throws SocketException
     * @throws IOException
     */
    private static void requestPropertiesMultipleAll(final ObjectType objectType, final int objectInstanceNumber)
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
//            final Socket socket = new Socket("192.168.2.1", 47808);
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

        final InetAddress address = InetAddress.getByName("192.168.2.1");
        final byte[] outBuffer = outMessage.getBytes();

        final DatagramPacket outPacket = new DatagramPacket(outBuffer, outBuffer.length, address, 47808);

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

    /**
     * 
     * @param objectType
     * @param objectInstanceNumber
     * @throws UnknownHostException
     * @throws SocketException
     * @throws IOException
     */
    private static void requestPropertiesMultipleSystemStatus(final ObjectType objectType,
            final int objectInstanceNumber) throws UnknownHostException, SocketException, IOException {

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
//            final Socket socket = new Socket("192.168.2.1", 47808);
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

        final InetAddress address = InetAddress.getByName("192.168.2.1");
        final byte[] outBuffer = outMessage.getBytes();

        final DatagramPacket outPacket = new DatagramPacket(outBuffer, outBuffer.length, address, 47808);

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

    private static void requestObjectListSize() throws UnknownHostException, SocketException, IOException {
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
        objectIdentifierServiceParameter.setObjectType(ObjectType.DEVICE);
        objectIdentifierServiceParameter.setInstanceNumber(3711630);

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
//            final Socket socket = new Socket("192.168.2.1", 47808);
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

        final InetAddress address = InetAddress.getByName("192.168.2.1");
        final byte[] outBuffer = outMessage.getBytes();

        final DatagramPacket outPacket = new DatagramPacket(outBuffer, outBuffer.length, address, 47808);

        final DatagramSocket socket = new DatagramSocket();
        socket.send(outPacket);
        LOG.info("Packet sent!");

        final byte[] inBuffer = new byte[512];
        final DatagramPacket inPacket = new DatagramPacket(inBuffer, inBuffer.length);
        socket.receive(inPacket);

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

    private static void startFatClient(final String[] args) throws InterruptedException {
        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
                FatClientConfiguration.class);

        DesktopApplicationContext.main(App.class, args);

        Thread.sleep(1000);

        runWhoIsThread();
    }

    private static void runWhoIsThread() {
        new Thread(new WhoIsRunnable()).run();
    }
}
