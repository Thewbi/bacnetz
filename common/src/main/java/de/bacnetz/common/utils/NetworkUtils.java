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

    public static final int DEVICE_MAC_ADDRESS = 0x001268;
//	public static final int DEVICE_MAC_ADDRESS = 0x001269;

//    public static final String OBJECT_NAME = "Device_IO420";
    public static final String OBJECT_NAME = "IO 420";

    public static final String OBJECT_LOCATION = "Batcave";

//    public static final String VENDOR_NAME = "GEZE GmbH";

//    public static final String MODEL_NAME = "IO 420";

    public static final String OBJECT_IDENTIFIER = "OBJECT_DEVICE:10001";

//    public static final boolean ADD_ADDITIONAL_NETWORK_INFORMATION = true;
    public static final boolean ADD_ADDITIONAL_NETWORK_INFORMATION = false;

    public static final int DESTINATION_NETWORK_NUMBER = 302;

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

        LOG.trace("Local hostname is: " + hostAddress);

        return hostAddress;
    }
}
