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
import de.bacnetz.controller.DefaultMessage;
import de.bacnetz.controller.Message;
import de.bacnetz.controller.MessageController;
import de.bacnetz.conversion.BACnetIPByteArrayToMessageConverter;
import de.bacnetz.services.BaseCommunicationService;
import de.bacnetz.stack.ConfirmedServiceChoice;
import de.bacnetz.stack.UnconfirmedServiceChoice;

public class MulticastListenerReaderThread extends BaseCommunicationService implements Runnable {

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

        LOG.trace("Start MulticastListener thread!");

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
            LOG.trace("Receiving ...");
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

            // DEBUG - output the hex buffer
            if (LOG.isTraceEnabled()) {
	            LOG.trace(">>> Received from inetAddress: " + datagramPacketAddress + " From socketAddress "
	                    + datagramPacketSocketAddress + " Data: "
	                    + Utils.byteArrayToStringNoPrefix(datagramPacket.getData()));
	            LOG.trace(">>> " + Utils.byteArrayToStringNoPrefix(data));
            }

            List<Message> responseMessages = null;
            Message request = null;

            // parse and process the request message and return a response message
            try {

                LOG.trace("datagramPacketSocketAddress: {}, bytesReceived: {}, data: {}", datagramPacketSocketAddress,
                        bytesReceived, Utils.bytesToHex(data));

                // parse the incoming data into a BACnet request message

                LOG.trace("calling parseBuffer() ...");
                request = parseBuffer(data, bytesReceived);
                LOG.trace("calling parseBuffer() done.");

                LOG.trace("calling setSourceInetSocketAddress() request: {} datagramPacketSocketAddress: {} ...",
                        request, datagramPacketSocketAddress);
                request.setSourceInetSocketAddress((InetSocketAddress) datagramPacketSocketAddress);
                LOG.trace("calling setSourceInetSocketAddress() done.");

                // tell the controller to compute a response from the request
                LOG.trace("calling sendMessageToController() ...");
                responseMessages = sendMessageToController(request);
                LOG.trace("calling sendMessageToController() done.");

            } catch (final Exception e) {
                LOG.error("Cannot process buffer: {}", Utils.byteArrayToStringNoPrefix(data));
                LOG.error(e.getMessage(), e);
            }

            if (CollectionUtils.isNotEmpty(responseMessages)) {
                // send answer to the network
                for (final Message response : responseMessages) {
                    sendMessage(datagramPacketAddress, request, response);
                }
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
        LOG.info("Opening broadcast socket on port: {}", ConfigurationManager.BACNET_PORT_DEFAULT_VALUE);
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

    private void sendMessage(final InetAddress datagramPacketAddress, final Message requestMessage,
            final Message responseMessage) {

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

            if (broadcast) {

                LOG.trace(">>> BroadCast");

                // broadcast response to the bacnet default port
                broadcastMessage(responseMessage);

            } else {

                LOG.trace(">>> PointToPoint");

                // point to point response
                pointToPointMessage(requestMessage, responseMessage, datagramPacketAddress);

            }

        } catch (final IOException e) {
            LOG.info(e.getMessage(), e);
        }
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

        LOG.info(
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
    @SuppressWarnings("unused")
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

        final BACnetIPByteArrayToMessageConverter converter = new BACnetIPByteArrayToMessageConverter();
        converter.setPayloadLength(payloadLength);
        converter.setVendorMap(vendorMap);

        final DefaultMessage defaultMessage = converter.convert(data);

        if (defaultMessage.getApdu() == null) {
            return null;
        }

        // After all segments have been reassembled...
        //
        // process service parameters inside the APDU. The APDU will parse the service
        // parameters, dump them to the console and store them in it's service parameter
        // list for further processing
        final byte[] payload = defaultMessage.getApdu().getPayload();
        final int startIndex = 0;
        final int offset = 0;
        defaultMessage.getApdu().processPayload(payload, startIndex, payload.length, offset);

        return defaultMessage;
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

    @SuppressWarnings("unused")
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

    @Override
    protected DatagramSocket getDatagramSocket() {
        return broadcastDatagramSocket;
    }

}
