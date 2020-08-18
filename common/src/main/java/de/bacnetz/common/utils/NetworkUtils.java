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

    public static final String TARGET_IP = "192.168.2.2";
//  private static final String TARGET_IP = "192.168.0.108";

//	public static final int DEVICE_INSTANCE_NUMBER = 26;
    public static final int DEVICE_INSTANCE_NUMBER = 10001;

    public static final int DEVICE_MAC_ADDRESS = 0x001268;
//	public static final int DEVICE_MAC_ADDRESS = 0x001269;

//    public static final String OBJECT_NAME = "Device_IO420";
    public static final String OBJECT_NAME = "IO 420";

    public static final String OBJECT_LOCATION = "Batcave";

    public static final String VENDOR_NAME = "GEZE GmbH";

    public static final String MODEL_NAME = "IO 420";

    public static final String OBJECT_IDENTIFIER = "OBJECT_DEVICE:10001";

//    public static final boolean ADD_ADDITIONAL_NETWORK_INFORMATION = true;
    public static final boolean ADD_ADDITIONAL_NETWORK_INFORMATION = false;

    public static final int DESTINATION_NETWORK_NUMBER = 302;

//	// Cockpit in local area network connected via ethernet cable without router (test setup)
//    public static final String LOCAL_BIND_IP = "192.168.2.1";
    public static final String BACNET_MULTICAST_IP = "192.168.2.255";

//	public static final String LOCAL_BIND_IP = "172.18.60.118";
//	public static final String BACNET_MULTICAST_IP = "172.18.63.255";

//	public static final String LOCAL_BIND_IP = "192.168.0.234";
//    public static final String BACNET_MULTICAST_IP = "192.168.0.255";

//	public static final String LOCAL_BIND_IP = "192.168.2.11";
//	public static final String BACNET_MULTICAST_IP = "192.168.0.255";

    // GEZE public WLAN
//	public static final String LOCAL_BIND_IP = "192.168.7.80";
//	public static final String BACNET_MULTICAST_IP = "192.168.7.255";

//    public static final String LOCAL_BIND_IP = "192.168.0.108";
//    public static final String BACNET_MULTICAST_IP = "192.168.0.255";

//	final InetAddress inetAddress = InetAddress.getByName("192.168.2.1");
//	final InetAddress inetAddress = InetAddress.getByName("192.168.2.2");
//	final InetAddress inetAddress = InetAddress.getByName("192.168.2.255");

//	public static final String BACNET_MULTICAST_IP = "192.168.7.255";

    /** 0xBAC0 == 47808d */
    public static final int DEFAULT_PORT = 0xBAC0;

    public static final String OBJECT_DESCRIPTION = "no entry";

    public static final String PROFILE_NAME = "p1";

    public static final int BROADCAST_NETWORK_NUMBER = 0xFFFF;

    private static String hostAddress = null;

    /**
     * ctor
     */
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
