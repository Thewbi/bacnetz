package de.bacnetz.threads;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.bacnetz.common.utils.NetworkUtils;
import de.bacnetz.common.utils.Utils;
import de.bacnetz.configuration.ConfigurationManager;
import de.bacnetz.controller.Message;
import de.bacnetz.controller.MessageController;
import de.bacnetz.conversion.ByteArrayToMessageConverter;
import de.bacnetz.services.CommunicationService;
import de.bacnetz.stack.ConfirmedServiceChoice;
import de.bacnetz.stack.UnconfirmedServiceChoice;

public class MulticastListenerReaderThread implements Runnable, CommunicationService {

    private static final Logger LOG = LogManager.getLogger(MulticastListenerReaderThread.class);

    @Autowired
    private ConfigurationManager configurationManager;

    private boolean running;

    private MulticastSocket multicastSocket;

    private DatagramSocket broadcastDatagramSocket;

    private int bindPort;

    private Map<Integer, String> vendorMap = new HashMap<>();

    private final List<MessageController> messageControllers = new ArrayList<>();

    @Override
    public void run() {

        LOG.info("Start MulticastListener thread!");

        running = true;

        try {
//			runMultiCastListener();
            runBroadCastListener();
        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
        }

        LOG.info("Reader MulticastListener end!");
    }

    private void runBroadCastListener() throws IOException {

        try {
            openBroadCastSocket();

//            LOG.info("Broadcast listener on " + NetworkUtils.LOCAL_BIND_IP + ":" + NetworkUtils.DEFAULT_PORT
//                    + " started.");

//            LOG.info("Broadcast listener on "
//                    + configurationManager.getPropertyAsString(ConfigurationManager.LOCAL_IP_CONFIG_KEY) + ":"
//                    + configurationManager.getPropertyAsString(ConfigurationManager.PORT_CONFIG_KEY) + " started.");

        } catch (final SocketException e) {
            LOG.error(e.getMessage(), e);

            LOG.error(
                    "Cannot bind! Please close all other bacnet applications (Yabe, VTS, ...) that might be running on this PC!");
            return;
        }

        while (running) {

            final byte[] data = new byte[1024];
            final DatagramPacket datagramPacket = new DatagramPacket(data, data.length);

            // blocking call
            broadcastDatagramSocket.receive(datagramPacket);
            final int bytesReceived = datagramPacket.getLength();

            if (bytesReceived >= data.length) {
                throw new RuntimeException("Buffer too small. Might have been truncated!");
            }

            final InetAddress datagramPacketAddress = datagramPacket.getAddress();
            LOG.trace(datagramPacketAddress + "isAnyLocalAddress(): " + datagramPacketAddress.isAnyLocalAddress());
            LOG.trace(datagramPacketAddress + "isLinkLocalAddress(): " + datagramPacketAddress.isLinkLocalAddress());
            LOG.trace(datagramPacketAddress + "isLoopbackAddress(): " + datagramPacketAddress.isLoopbackAddress());

            final SocketAddress datagramPacketSocketAddress = datagramPacket.getSocketAddress();

            // do not process your own broadcast messages
            final String localIp = configurationManager.getPropertyAsString(ConfigurationManager.LOCAL_IP_CONFIG_KEY);
            if (datagramPacketAddress.equals(InetAddress.getByName(localIp))) {
                continue;
            }

            // Debug
            LOG.trace("<<< Received from inetAddress: " + datagramPacketAddress + " From socketAddress "
                    + datagramPacketSocketAddress + " Data: "
                    + Utils.byteArrayToStringNoPrefix(datagramPacket.getData()));
            LOG.trace("<<< " + Utils.byteArrayToStringNoPrefix(data));

            List<Message> response = null;
            Message request = null;

            // parse and process the request message and return a response message
            try {

                // parse the incoming data into a BACnet request message
                request = parseBuffer(data, bytesReceived);
                request.setSourceInetSocketAddress((InetSocketAddress) datagramPacketSocketAddress);

                // tell the controller to compute a response from the request
                response = sendMessageToController(request);

            } catch (final Exception e) {
                LOG.error("Cannot process buffer: {}", Utils.byteArrayToStringNoPrefix(data));
                LOG.error(e.getMessage(), e);
            }

            if (CollectionUtils.isNotEmpty(response)) {
                // send answer to the network
                response.stream().forEach(m -> sendMessage(datagramPacketAddress, m));
            } else {
                LOG.trace("Controller returned a null message!");
            }

            LOG.trace("done");
        }
    }

    /**
     * A broadcast socket is needed to receive WHO-IS and other broadcasted bacnet
     * messages.
     * 
     * @throws SocketException
     */
    public void openBroadCastSocket() throws SocketException {
        if (broadcastDatagramSocket != null) {
            return;
        }

        // this will open the broadcast socket on 127.0.0.1 or even 0.0.0.0
        broadcastDatagramSocket = new DatagramSocket(ConfigurationManager.BACNET_PORT_DEFAULT_VALUE);
        broadcastDatagramSocket.setBroadcast(true);
    }

    public void closeBroadCastSocket() throws SocketException {
        if (broadcastDatagramSocket == null) {
            return;
        }
        broadcastDatagramSocket.close();
        broadcastDatagramSocket = null;
    }

    private void sendMessage(final InetAddress datagramPacketAddress, final Message responseMessage) {

        try {

            final UnconfirmedServiceChoice unconfirmedServiceChoice = responseMessage.getApdu()
                    .getUnconfirmedServiceChoice();
            final ConfirmedServiceChoice confirmedServiceChoice = responseMessage.getApdu().getConfirmedServiceChoice();

            boolean broadcast = false;

            if (unconfirmedServiceChoice != null) {
                LOG.trace(">>> ServiceChoice: {}", responseMessage.getApdu().getUnconfirmedServiceChoice().name());

                broadcast = responseMessage.getApdu().getUnconfirmedServiceChoice() == UnconfirmedServiceChoice.I_AM;
                broadcast |= responseMessage.getApdu().getUnconfirmedServiceChoice() == UnconfirmedServiceChoice.WHO_IS;
            }
            if (confirmedServiceChoice != null) {
                LOG.trace(">>> ServiceChoice: {}", responseMessage.getApdu().getConfirmedServiceChoice().name());
            }

//		final byte[] bytes = responseMessage.getBytes();
//		LOG.trace(">>> " + Utils.byteArrayToStringNoPrefix(bytes));

            if (broadcast) {

                LOG.trace(">>> BroadCast");

                // broadcast response to the bacnet default port
                broadcastMessage(responseMessage);

            } else {

                LOG.trace(">>> PointToPoint");

                // point to point response
                pointToPointMessage(responseMessage, datagramPacketAddress);

            }

        } catch (final IOException e) {
            LOG.info(e.getMessage(), e);
        }
    }

    @Override
    public void pointToPointMessage(final Message responseMessage, final InetAddress datagramPacketAddress)
            throws IOException {

        final byte[] bytes = responseMessage.getBytes();

        if (responseMessage.getVirtualLinkControl().getLength() != bytes.length) {
            throw new RuntimeException(
                    "Message is invalid! The length in the virtual link control does not match the real data length!");
        }

        final InetAddress destinationAddress = datagramPacketAddress;
        final DatagramPacket responseDatagramPacket = new DatagramPacket(bytes, bytes.length, destinationAddress,
                ConfigurationManager.BACNET_PORT_DEFAULT_VALUE);

        broadcastDatagramSocket.send(responseDatagramPacket);
    }

    private void broadcastMessage(final Message message) throws IOException {

        final byte[] bytes = message.getBytes();

        if (message.getVirtualLinkControl().getLength() != bytes.length) {
            throw new RuntimeException(
                    "Message is invalid! The length in the virtual link control does not match the real data length!");
        }

        final int port = configurationManager.getPropertyAsInt(ConfigurationManager.PORT_CONFIG_KEY);
        final String multicastIP = configurationManager
                .getPropertyAsString(ConfigurationManager.MULTICAST_IP_CONFIG_KEY);

        LOG.trace(
                ">>> Broadcast Sending to " + multicastIP + ":" + port + ": " + Utils.byteArrayToStringNoPrefix(bytes));

        final SocketAddress socketAddress = new InetSocketAddress(multicastIP, port);
        final DatagramPacket responseDatagramPacket = new DatagramPacket(bytes, bytes.length, socketAddress);

        broadcastDatagramSocket.send(responseDatagramPacket);
    }

    /**
     * Throws java.net.SocketException: Not a multicast address when using the
     * address: 192.168.2.255
     * 
     * @param message
     * @throws UnknownHostException
     * @throws IOException
     */
    private void sendViaMulticastSocket(final Message message) throws UnknownHostException, IOException {

//		final InetAddress group = InetAddress.getByName(NetworkUtils.BACNET_MULTICAST_IP);
        final InetAddress group = InetAddress.getByName("224.0.0.0");

        // https://docs.oracle.com/javase/tutorial/networking/datagrams/broadcasting.html
        final MulticastSocket socket = new MulticastSocket(ConfigurationManager.BACNET_PORT_DEFAULT_VALUE);
        socket.joinGroup(group);

        final SocketAddress socketAddress = new InetSocketAddress(
                ConfigurationManager.BACNET_MULTICAST_IP_DEFAULT_VALUE, ConfigurationManager.BACNET_PORT_DEFAULT_VALUE);
        final byte[] bytes = message.getBytes();
        final DatagramPacket multiCastDatagramPacket = new DatagramPacket(bytes, bytes.length, socketAddress);
        socket.send(multiCastDatagramPacket);

        socket.leaveGroup(group);
        socket.close();
    }

    /**
     * Takes the data read via the socket and parses the byte array into a bacnet
     * message (= VirtualLinkControl + NPDU + APDU). This messages is then put into
     * a controller for further processing. The controller returns a result message
     * or a null value. The controllers return value is returned by this function.
     * 
     * @param data
     * @param payloadLength
     */
    public Message parseBuffer(final byte[] data, final int payloadLength) {

        final ByteArrayToMessageConverter converter = new ByteArrayToMessageConverter();
        converter.setPayloadLength(payloadLength);
        converter.setVendorMap(vendorMap);

        return converter.convert(data);
    }

    public List<Message> sendMessageToController(final Message message) {

        // find a controller that is able to create a response matching the request
        if (CollectionUtils.isNotEmpty(messageControllers)) {

            for (final MessageController messageController : messageControllers) {

                return messageController.processMessage(message);
            }
        }

        return null;
    }

    private void runMultiCastListener() throws IOException {

        final InetSocketAddress inetSocketAddress = new InetSocketAddress(NetworkUtils.retrieveLocalIP(), bindPort);

        multicastSocket = new MulticastSocket(inetSocketAddress);
        multicastSocket.setReuseAddress(true);

        LOG.info("Multicast listener on " + ConfigurationManager.BACNET_MULTICAST_IP_DEFAULT_VALUE + " started.");

        while (running) {

            final byte[] buf = new byte[1024];
            final DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);

            // blocking call
            multicastSocket.receive(datagramPacket);

            LOG.info(datagramPacket);
        }
    }

    public void stopBroadCastListener() {
        running = false;
    }

    public int getBindPort() {
        return bindPort;
    }

    public void setBindPort(final int bindPort) {
        this.bindPort = bindPort;
    }

    public Map<Integer, String> getVendorMap() {
        return vendorMap;
    }

    public void setVendorMap(final Map<Integer, String> vendorMap) {
        this.vendorMap = vendorMap;
    }

    public List<MessageController> getMessageControllers() {
        return messageControllers;
    }

    public void setConfigurationManager(final ConfigurationManager configurationManager) {
        this.configurationManager = configurationManager;
    }

    public void setBroadcastDatagramSocket(final DatagramSocket broadcastDatagramSocket) {
        this.broadcastDatagramSocket = broadcastDatagramSocket;
    }

}

//final MessageFactory messageFactory = new MessageFactory();
//final Message whoIsMessage = messageFactory.create(MessageType.WHO_IS, 25, 25);
//sendViaMulticastSocket(whoIsMessage);

//final InetAddress inetAddress = InetAddress.getByName(NetworkUtils.LOCAL_BIND_IP);

//broadcastDatagramSocket = new DatagramSocket(NetworkUtils.DEFAULT_PORT, inetAddress);

//LOG.info(">>> Sending who is ...");
//final byte[] buffer = whoIsMessage.getBytes();
//final InetAddress broadcastInetAddress = InetAddress.getByName("192.168.2.1");
//final DatagramPacket whoIsDatagramPacket = new DatagramPacket(buffer, buffer.length, broadcastInetAddress,
//      NetworkUtils.DEFAULT_PORT);
//broadcastDatagramSocket.send(whoIsDatagramPacket);
//LOG.info(">>> Sending who is done.");
//sendMessage(null, whoIsMessage);
