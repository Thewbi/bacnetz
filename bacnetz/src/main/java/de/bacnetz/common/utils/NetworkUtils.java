package de.bacnetz.common.utils;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetworkUtils {

	private static final Logger LOG = LogManager.getLogger(NetworkUtils.class);

	public static final String LOCAL_BIND_IP = "192.168.2.1";
//	final InetAddress inetAddress = InetAddress.getByName("172.18.60.118");
//	final InetAddress inetAddress = InetAddress.getByName("192.168.2.1");
//	final InetAddress inetAddress = InetAddress.getByName("192.168.2.2");
//	final InetAddress inetAddress = InetAddress.getByName("192.168.2.255");

	public static final String BACNET_MULTICAST_IP = "192.168.2.255";
//	public static final String BACNET_MULTICAST_IP = "192.168.7.255";

	/** 0xBAC0 == 47808d */
	public static final int DEFAULT_PORT = 0xBAC0;

	private static String hostAddress = null;

	private NetworkUtils() {
		// no instances of this class
	}

	public static String retrieveLocalIP() throws UnknownHostException, SocketException {

		if (StringUtils.isBlank(hostAddress)) {

			try (final DatagramSocket socket = new DatagramSocket()) {

				socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
				hostAddress = socket.getLocalAddress().getHostAddress();
			}
		}

		LOG.info("Local hostname is: " + hostAddress);

		return hostAddress;
	}
}
