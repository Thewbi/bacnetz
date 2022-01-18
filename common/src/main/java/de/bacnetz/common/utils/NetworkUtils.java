package de.bacnetz.common.utils;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    public static final boolean ADD_ADDITIONAL_NETWORK_INFORMATION = true;
//    public static final boolean ADD_ADDITIONAL_NETWORK_INFORMATION = false;

//    public static final int DESTINATION_NETWORK_NUMBER = 302;
    public static final int DESTINATION_NETWORK_NUMBER = 801;

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

    public static List<InetAddress> listAllBroadcastAddresses() throws SocketException {

        final List<InetAddress> broadcastList = new ArrayList<>();

        final Map<NetworkInterface, List<InetAddress>> allMap = new HashMap<>();
        final Map<NetworkInterface, List<InetAddress>> broadcastMap = new HashMap<>();

        final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        int interfaceCount = 0;
        int networkedInterfaceCount = 0;
        while (interfaces.hasMoreElements()) {

            interfaceCount++;

            final NetworkInterface networkInterface = interfaces.nextElement();

            if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                continue;
            }

            networkedInterfaceCount++;

            final List<InetAddress> interfaceBroadcastList = new ArrayList<>();
            broadcastMap.put(networkInterface, interfaceBroadcastList);

            final List<InetAddress> interfaceList = new ArrayList<>();
            allMap.put(networkInterface, interfaceList);

            networkInterface.getInterfaceAddresses().stream().map(a -> a.getAddress()).filter(Objects::nonNull)
                    .forEach(interfaceList::add);
            networkInterface.getInterfaceAddresses().stream().map(a -> a.getBroadcast()).filter(Objects::nonNull)
                    .forEach(interfaceBroadcastList::add);

            networkInterface.getInterfaceAddresses().stream().map(a -> a.getBroadcast()).filter(Objects::nonNull)
                    .forEach(broadcastList::add);
        }

        if (interfaceCount == 0 || networkedInterfaceCount == 0) {
            throw new RuntimeException("No interfaces found that are connected to a network!");
        }

        // DEBUG
        LOG.info("All");
        for (final Map.Entry<NetworkInterface, List<InetAddress>> entry : allMap.entrySet()) {
            LOG.info(entry.getKey() + " -> IP: " + entry.getValue());
        }

        // DEBUG
        LOG.info("Broadcast");
        for (final Map.Entry<NetworkInterface, List<InetAddress>> entry : broadcastMap.entrySet()) {
            LOG.info(entry.getKey() + " -> Broadcast: " + entry.getValue());
        }

        return broadcastList;
    }

    public static List<InetAddress> getBroadcastAddressesByName(final String name) throws SocketException {

        final List<InetAddress> broadcastList = new ArrayList<>();

        final Map<String, List<InetAddress>> broadcastMap = new HashMap<>();

        final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {

            final NetworkInterface networkInterface = interfaces.nextElement();

            if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                continue;
            }

            final List<InetAddress> interfaceBroadcastList = new ArrayList<>();
            networkInterface.getInterfaceAddresses().stream().map(a -> a.getBroadcast()).filter(Objects::nonNull)
                    .forEach(interfaceBroadcastList::add);

            broadcastMap.put(networkInterface.getName(), interfaceBroadcastList);
        }

        return broadcastMap.get(name);
    }

}
