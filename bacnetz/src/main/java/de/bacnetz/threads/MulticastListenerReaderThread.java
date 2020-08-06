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

import de.bacnetz.common.utils.NetworkUtils;
import de.bacnetz.common.utils.Utils;
import de.bacnetz.controller.Message;
import de.bacnetz.controller.MessageController;
import de.bacnetz.conversion.ByteArrayToMessageConverter;
import de.bacnetz.services.CommunicationService;
import de.bacnetz.stack.UnconfirmedServiceChoice;

public class MulticastListenerReaderThread implements Runnable, CommunicationService {

    private static final Logger LOG = LogManager.getLogger(MulticastListenerReaderThread.class);

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

//		final MessageFactory messageFactory = new MessageFactory();
//		final Message whoIsMessage = messageFactory.create(MessageType.WHO_IS, 25, 25);
//		sendViaMulticastSocket(whoIsMessage);

        final InetAddress inetAddress = InetAddress.getByName(NetworkUtils.LOCAL_BIND_IP);

//		broadcastDatagramSocket = new DatagramSocket(NetworkUtils.DEFAULT_PORT, inetAddress);

        try {
            openBroadCastSocket();
        } catch (final SocketException e) {
            LOG.error(e.getMessage(), e);

            LOG.error(
                    "Cannot bind! Please close all other bacnet applications (Yabe, VTS, ...) that might be running on this PC!");
            return;
        }

//		LOG.info(">>> Sending who is ...");
//		final byte[] buffer = whoIsMessage.getBytes();
//		final InetAddress broadcastInetAddress = InetAddress.getByName("192.168.2.1");
//		final DatagramPacket whoIsDatagramPacket = new DatagramPacket(buffer, buffer.length, broadcastInetAddress,
//				NetworkUtils.DEFAULT_PORT);
//		broadcastDatagramSocket.send(whoIsDatagramPacket);
//		LOG.info(">>> Sending who is done.");
//		sendMessage(null, whoIsMessage);

        LOG.info("Broadcast listener on " + NetworkUtils.LOCAL_BIND_IP + ":" + NetworkUtils.DEFAULT_PORT + " started.");

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
            if (datagramPacketAddress.equals(InetAddress.getByName(NetworkUtils.LOCAL_BIND_IP))) {
                continue;
            }

            // Debug
            LOG.trace("<<< Received from inetAddress: " + datagramPacketAddress + " From socketAddress "
                    + datagramPacketSocketAddress + " Data: "
                    + Utils.byteArrayToStringNoPrefix(datagramPacket.getData()));
            LOG.trace("<<< " + Utils.byteArrayToStringNoPrefix(data));

            Message response = null;
            Message request = null;

            // parse and process the request message and return a response message
            try {
                request = parseBuffer(data, bytesReceived);
                response = sendMessageToController(request);
            } catch (final Exception e) {
                LOG.error("Cannot parse buffer: {}", Utils.byteArrayToStringNoPrefix(data));
                LOG.error(e.getMessage(), e);
            }

            if (response != null) {
                // send answer to the network
                sendMessage(datagramPacketAddress, response, request);
            } else {
                LOG.trace("Controller returned a null message!");
            }

            LOG.trace("done");
        }
    }

    public void openBroadCastSocket() throws SocketException {
        if (broadcastDatagramSocket != null) {
            return;
        }
        broadcastDatagramSocket = new DatagramSocket(NetworkUtils.DEFAULT_PORT);

//		final SocketAddress socketAddress = new InetSocketAddress(NetworkUtils.LOCAL_BIND_IP,
//				NetworkUtils.DEFAULT_PORT);
//		broadcastDatagramSocket.bind(socketAddress);

        broadcastDatagramSocket.setBroadcast(true);
    }

    public void closeBroadCastSocket() throws SocketException {
        if (broadcastDatagramSocket == null) {
            return;
        }
        broadcastDatagramSocket.close();
        broadcastDatagramSocket = null;
    }

    private void sendMessage(final InetAddress datagramPacketAddress, final Message responseMessage,
            final Message requestMessage) throws IOException {

        LOG.trace(">>> ServiceChoice: {}", responseMessage.getApdu().getUnconfirmedServiceChoice().name());

        boolean broadcast = responseMessage.getApdu().getUnconfirmedServiceChoice() == UnconfirmedServiceChoice.I_AM;
        broadcast |= responseMessage.getApdu().getUnconfirmedServiceChoice() == UnconfirmedServiceChoice.WHO_IS;

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
                NetworkUtils.DEFAULT_PORT);

        broadcastDatagramSocket.send(responseDatagramPacket);
    }

    private void broadcastMessage(final Message message) throws IOException {

        final byte[] bytes = message.getBytes();

        if (message.getVirtualLinkControl().getLength() != bytes.length) {
            throw new RuntimeException(
                    "Message is invalid! The length in the virtual link control does not match the real data length!");
        }

        LOG.trace(">>> Broadcast Sending to " + NetworkUtils.BACNET_MULTICAST_IP + ":" + NetworkUtils.DEFAULT_PORT
                + ": " + Utils.byteArrayToStringNoPrefix(bytes));

        final SocketAddress socketAddress = new InetSocketAddress(NetworkUtils.BACNET_MULTICAST_IP,
                NetworkUtils.DEFAULT_PORT);
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
        final MulticastSocket socket = new MulticastSocket(NetworkUtils.DEFAULT_PORT);
        socket.joinGroup(group);

        final SocketAddress socketAddress = new InetSocketAddress(NetworkUtils.BACNET_MULTICAST_IP,
                NetworkUtils.DEFAULT_PORT);
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

    public Message sendMessageToController(final Message message) {

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

        LOG.info("Multicast listener on " + NetworkUtils.BACNET_MULTICAST_IP + " started.");

        while (running) {

            final byte[] buf = new byte[1024];
            final DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);

            // blocking call
            multicastSocket.receive(datagramPacket);

            LOG.info(datagramPacket);
        }
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

}