package de.bacnetz.stack;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketAddress;
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

		broadcastDatagramSocket = new DatagramSocket(NetworkUtils.DEFAULT_PORT);
		broadcastDatagramSocket.setBroadcast(true);

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

//			// open point to point connection to sender
//			final DatagramSocket ptpSenderSocket = new DatagramSocket();

			// Debug
			LOG.info("Received from inetAddress: " + datagramPacketAddress + " From socketAddress "
					+ datagramPacketSocketAddress + " Data: "
					+ Utils.byteArrayToStringNoPrefix(datagramPacket.getData()));

			// parse and process the request message and return a response message
			final Message response = parseBuffer(data, bytesReceived);
			if (response != null) {

				final byte[] bytes = response.getBytes();

				LOG.info("ServiceChoice: {}", response.getApdu().getServiceChoice().name());

				final boolean broadcast = response.getApdu().getServiceChoice() == ServiceChoice.I_AM;
				if (broadcast) {

					LOG.info("BroadCast");

					// broadcast response
					final SocketAddress socketAddress = new InetSocketAddress(NetworkUtils.BACNET_MULTICAST_IP,
							NetworkUtils.DEFAULT_PORT);
					final DatagramPacket responseDatagramPacket = new DatagramPacket(bytes, bytes.length,
							socketAddress);
					broadcastDatagramSocket.send(responseDatagramPacket);

				} else {

					LOG.info("PointToPoint");

					// point to point response
					final InetAddress destinationAddress = datagramPacketAddress;
					final DatagramPacket responseDatagramPacket = new DatagramPacket(bytes, bytes.length,
							destinationAddress, NetworkUtils.DEFAULT_PORT);
//					ptpSenderSocket.send(responseDatagramPacket);
					broadcastDatagramSocket.send(responseDatagramPacket);

				}

			} else {

				LOG.trace("Controller returned a null message!");

			}

//			if (ptpSenderSocket != null) {
//				ptpSenderSocket.close();
//			}

			LOG.trace("done");
		}
	}

	/**
	 * @param data
	 */
	public Message parseBuffer(final byte[] data, final int payloadLength) {

		int offset = 0;

		// deserialize the virtual link control part of the message
		final VirtualLinkControl virtualLinkControl = new VirtualLinkControl();
		virtualLinkControl.fromBytes(data, 0);
		offset += virtualLinkControl.getStructureLength();

		// deserialize the NPDU part of the message
		final NPDU npdu = new NPDU();
		npdu.fromBytes(data, offset);
		offset += npdu.getStructureLength();

		APDU apdu = null;
		if (npdu.isAPDUMessage()) {
			// deserialize the APDU part of the message
			apdu = new APDU();
			apdu.setVendorMap(vendorMap);
			apdu.fromBytes(data, offset, payloadLength);
			offset += apdu.getStructureLength();
		}

		// find a controller that is able to create a response matching the request
		if (CollectionUtils.isNotEmpty(messageControllers)) {

			for (final MessageController messageController : messageControllers) {

				final DefaultMessage defaultMessage = new DefaultMessage();
				defaultMessage.setVirtualLinkControl(virtualLinkControl);
				defaultMessage.setNpdu(npdu);
				defaultMessage.setApdu(apdu);

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
