package de.bacnetz.stack;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.bacnetz.common.Utils;
import de.bacnetz.common.utils.NetworkUtils;
import de.bacnetz.controller.DefaultMessage;
import de.bacnetz.controller.Message;
import de.bacnetz.controller.MessageController;
import de.bacnetz.conversion.ByteArrayToMessageConverter;
import de.bacnetz.factory.MessageFactory;
import de.bacnetz.factory.MessageType;

public class MulticastListenerReaderThread implements Runnable {

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

		final MessageFactory messageFactory = new MessageFactory();
		final Message whoIsMessage = messageFactory.create(MessageType.WHO_IS, 25, 25);

//		sendViaMulticastSocket(whoIsMessage);

		broadcastDatagramSocket = new DatagramSocket(NetworkUtils.DEFAULT_PORT);
		broadcastDatagramSocket.setBroadcast(true);

//		LOG.info(">>> Sending who is ...");
//		final byte[] buffer = whoIsMessage.getBytes();
//		final InetAddress broadcastInetAddress = InetAddress.getByName("192.168.2.1");
//		final DatagramPacket whoIsDatagramPacket = new DatagramPacket(buffer, buffer.length, broadcastInetAddress,
//				NetworkUtils.DEFAULT_PORT);
//		broadcastDatagramSocket.send(whoIsDatagramPacket);
//		LOG.info(">>> Sending who is done.");

//		sendMessage(null, whoIsMessage);

		LOG.info("Broadcast listener on Port " + NetworkUtils.DEFAULT_PORT + " started.");

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
			if (datagramPacketAddress.equals(InetAddress.getByName("192.168.2.1"))) {
				continue;
			}

			// Debug
			LOG.info("<<< Received from inetAddress: " + datagramPacketAddress + " From socketAddress "
					+ datagramPacketSocketAddress + " Data: "
					+ Utils.byteArrayToStringNoPrefix(datagramPacket.getData()));

			// parse and process the request message and return a response message
			final Message response = parseBuffer(data, bytesReceived);
			if (response != null) {
				sendMessage(datagramPacketAddress, response);
			} else {
				LOG.trace("Controller returned a null message!");
			}

			LOG.trace("done");
		}
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

	private void sendMessage(final InetAddress datagramPacketAddress, final Message message) throws IOException {

		LOG.info(">>> ServiceChoice: {}", message.getApdu().getServiceChoice().name());

		boolean broadcast = message.getApdu().getServiceChoice() == ServiceChoice.I_AM;
		broadcast |= message.getApdu().getServiceChoice() == ServiceChoice.WHO_IS;

		if (broadcast) {

			LOG.info(">>> BroadCast");

			// broadcast response to the bacnet default port
			broadcastMessage(message);

		} else {

			LOG.info(">>> PointToPoint");

			// point to point response
			pointToPointMessage(message, datagramPacketAddress);

		}
	}

	private void pointToPointMessage(final Message message, final InetAddress datagramPacketAddress)
			throws IOException {

		final byte[] bytes = message.getBytes();

		final InetAddress destinationAddress = datagramPacketAddress;
		final DatagramPacket responseDatagramPacket = new DatagramPacket(bytes, bytes.length, destinationAddress,
				NetworkUtils.DEFAULT_PORT);
		broadcastDatagramSocket.send(responseDatagramPacket);
	}

	private void broadcastMessage(final Message message) throws IOException {

		final byte[] bytes = message.getBytes();

		LOG.info(">>> Broadcast Sending: " + Utils.byteArrayToStringNoPrefix(bytes));

		final SocketAddress socketAddress = new InetSocketAddress(NetworkUtils.BACNET_MULTICAST_IP,
				NetworkUtils.DEFAULT_PORT);
		final DatagramPacket responseDatagramPacket = new DatagramPacket(bytes, bytes.length, socketAddress);
		broadcastDatagramSocket.send(responseDatagramPacket);
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

		final DefaultMessage defaultMessage = converter.convert(data);

		// find a controller that is able to create a response matching the request
		if (CollectionUtils.isNotEmpty(messageControllers)) {

			for (final MessageController messageController : messageControllers) {

				return messageController.processMessage(defaultMessage);
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
