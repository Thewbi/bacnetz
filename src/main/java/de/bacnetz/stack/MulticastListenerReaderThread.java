package de.bacnetz.stack;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
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

			LOG.info(datagramPacket);
			LOG.info(Utils.byteArrayToStringNoPrefix(datagramPacket.getData()));

			final Message response = parseBuffer(data);
			if (response != null) {

				final SocketAddress socketAddress = new InetSocketAddress(NetworkUtils.BACNET_MULTICAST_IP,
						NetworkUtils.DEFAULT_PORT);

				final byte[] bytes = response.getBytes();

				final DatagramPacket responseDatagramPacket = new DatagramPacket(bytes, bytes.length, socketAddress);
				broadcastDatagramSocket.send(responseDatagramPacket);
			}

			LOG.info("done");
		}
	}

	/**
	 * @param data
	 */
	public Message parseBuffer(final byte[] data) {

		int offset = 0;

		final VirtualLinkControl virtualLinkControl = new VirtualLinkControl();
		virtualLinkControl.fromBytes(data, 0);
		offset += virtualLinkControl.getStructureLength();

		final NPDU npdu = new NPDU();
		npdu.fromBytes(data, offset);
		offset += npdu.getStructureLength();

		final APDU apdu = new APDU();
		apdu.setVendorMap(vendorMap);
		apdu.fromBytes(data, offset);
		offset += apdu.getStructureLength();

		LOG.info("\n" + apdu.toString());

		final Message response = null;

		if (CollectionUtils.isNotEmpty(messageControllers)) {

			for (final MessageController messageController : messageControllers) {

				return messageController.processMessage(new DefaultMessage(virtualLinkControl, npdu, apdu));
			}
		}

		return null;
	}

	private void runMultiCastListener() throws IOException {

		final InetSocketAddress inetSocketAddress = new InetSocketAddress(NetworkUtils.retrieveLocalIP(), bindPort);

		multicastSocket = new MulticastSocket(inetSocketAddress);
		multicastSocket.setReuseAddress(true);

//		final InetAddress inetAddress = InetAddress.getByName(NetworkUtils.BACNET_MULTICAST_IP);
//		multicastSocket.joinGroup(inetAddress);

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
