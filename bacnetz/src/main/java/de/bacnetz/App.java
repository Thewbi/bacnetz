package de.bacnetz;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
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

import de.bacnetz.common.utils.NetworkUtils;
import de.bacnetz.common.utils.Utils;
import de.bacnetz.controller.DefaultMessageController;
import de.bacnetz.controller.Message;
import de.bacnetz.devices.BinaryInputDevice;
import de.bacnetz.devices.DefaultDevice;
import de.bacnetz.devices.DefaultDeviceFactory;
import de.bacnetz.devices.Device;
import de.bacnetz.devices.ObjectType;
import de.bacnetz.factory.DefaultMessageFactory;
import de.bacnetz.factory.MessageFactory;
import de.bacnetz.factory.MessageType;
import de.bacnetz.stack.IPv4Packet;
import de.bacnetz.stack.ObjectIdentifierServiceParameter;
import de.bacnetz.stack.UDPPacket;
import de.bacnetz.threads.MulticastListenerReaderThread;
import de.bacnetz.threads.ToogleDoorOpenStateThread;

/**
 * <pre>
 * bacnet and ((ip.dst == 192.168.2.1) or (ip.src == 192.168.2.1))
 * bacnet and ((ip.dst == 192.168.2.255) or (ip.dst == 192.168.2.1) or (ip.src == 192.168.2.1))
 * </pre>
 * 
 * TODO
 * <ol>
 * <li />Model a DevicePropertyType with value and meta information about the
 * value's data type. when a device property is queried, retrieve that
 * DevicePropertyType and have a converter that converts it to a response
 * message or a service parameter. Model a map from property name to
 * DevicePropertyType in DefaultDevice. Assign correct DevicePropertyTypeS to
 * the parent device IO 420 and it's children.
 * <li />Add ui.
 * <li />Add scripting language with debugging functionality via the UI.
 * <li />Only send value changes (COV) when COV subscription is there.
 * </ol>
 */
public class App {

//    private static final boolean RUN_TOGGLE_DOOR_THREAD = false;
    private static final boolean RUN_TOGGLE_DOOR_THREAD = true;

    private static final Logger LOG = LogManager.getLogger(App.class);

    public static void main(final String[] args) throws IOException {

        // https://github.com/apache/dubbo/issues/2423
        //
        // on a macbook, the JVM prioritizes IPv6 interfaces over
        // IPv4 interfaces. Force the JVM to use IPv4.
        System.setProperty("java.net.preferIPv4Stack", "true");

        // DEBUG
        final List<InetAddress> listAllBroadcastAddresses = listAllBroadcastAddresses();
        LOG.info(listAllBroadcastAddresses);

        runMain();
//		runWhoIsThread();
//		runFixVendorCSV();
//		runMainOld();
    }

    @SuppressWarnings("unused")
    private static void runWhoIsThread() {

        new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    try {
                        runBroadcast();
                    } catch (final SocketException e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(10000);
                    } catch (final InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).run();
    }

    private static void runBroadcast() throws SocketException {

        LOG.info("runBroadcast() ...");

        final MessageFactory messageFactory = new DefaultMessageFactory();
//		final Message whoIsMessage = messageFactory.create(MessageType.WHO_IS, 25, 25);
        final Message whoIsMessage = messageFactory.create(MessageType.WHO_IS);

        final List<InetAddress> listAllBroadcastAddresses = listAllBroadcastAddresses();

        // DEBUG
        LOG.info(listAllBroadcastAddresses);

        listAllBroadcastAddresses.stream().forEach(a -> {
            try {
                broadcast(whoIsMessage.getBytes(), a);
            } catch (final IOException e) {
                e.printStackTrace();
            }
        });

        LOG.info("runBroadcast() done.");
    }

    public static void broadcast(final byte[] buffer, final InetAddress address) throws IOException {

        LOG.info(">>> broadcast: " + Utils.byteArrayToStringNoPrefix(buffer));

        // this socket does not bind on a specific port
        final DatagramSocket socket = new DatagramSocket();
        socket.setBroadcast(true);
        final DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, NetworkUtils.DEFAULT_PORT);
        socket.send(packet);
        socket.close();
    }

    private static List<InetAddress> listAllBroadcastAddresses() throws SocketException {

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

        LOG.info("All");
        for (final Map.Entry<NetworkInterface, List<InetAddress>> entry : allMap.entrySet()) {

            LOG.info(entry.getKey() + " -> IP: " + entry.getValue());
        }

        LOG.info("Broadcast");
        for (final Map.Entry<NetworkInterface, List<InetAddress>> entry : broadcastMap.entrySet()) {

            LOG.info(entry.getKey() + " -> Broadcast: " + entry.getValue());
        }

        return broadcastList;
    }

    @SuppressWarnings("unused")
    private static void runFixVendorCSV() throws IOException {

        boolean firstLine = true;

        final File file = new File("C:\\Temp\\BACnetVerndors_fix.csv");
        final FileOutputStream fileOutputStream = new FileOutputStream(file);

        final BufferedWriter bufferedWriter = new BufferedWriter(
                new OutputStreamWriter(fileOutputStream, Utils.ENCODING_UTF_8));

        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader("C:\\Temp\\BACnetVendors.csv"));
            String line = reader.readLine();
            line = StringUtils.trim(line);

            StringBuffer stringBuffer = new StringBuffer();

            while (line != null) {

                System.out.println(line);

                if (line.startsWith(";;;")) {
                    stringBuffer.append(" ").append(line.substring(3));
                } else {

                    if (!firstLine) {
                        stringBuffer.append("\n");
                        final String outString = stringBuffer.toString();
                        bufferedWriter.write(outString);
                        stringBuffer = new StringBuffer();
                        stringBuffer.append(line);
                    } else {
                        stringBuffer.append(line);
                    }

                    firstLine = false;
                }

                // read next line
                line = reader.readLine();
                line = StringUtils.trim(line);
            }
            reader.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }

        bufferedWriter.close();
    }

    private static void runMain() throws SocketException, UnknownHostException, IOException {

        final Map<Integer, String> vendorMap = readVendorMap("src/main/resources/BACnetVendors.csv");

        final DefaultDeviceFactory defaultDeviceFactory = new DefaultDeviceFactory();
        final Device device = defaultDeviceFactory.create(vendorMap);

        final DefaultMessageController defaultMessageController = new DefaultMessageController();
        defaultMessageController.setDevice(device);
        defaultMessageController.setVendorMap(vendorMap);

        final MulticastListenerReaderThread multicastListenerReaderThread = new MulticastListenerReaderThread();
        multicastListenerReaderThread.setVendorMap(vendorMap);
        multicastListenerReaderThread.setBindPort(NetworkUtils.DEFAULT_PORT);
        multicastListenerReaderThread.getMessageControllers().add(defaultMessageController);

        final Device door1CloseStateBinaryInput = device.findDevice(
                ObjectIdentifierServiceParameter.createFromTypeAndInstanceNumber(ObjectType.BINARY_INPUT, 1));

        final ToogleDoorOpenStateThread toggleDoorOpenStateThread = new ToogleDoorOpenStateThread();
        toggleDoorOpenStateThread.setParentDevice(device);
        toggleDoorOpenStateThread.setChildDevice(door1CloseStateBinaryInput);
        toggleDoorOpenStateThread.setVendorMap(vendorMap);
        toggleDoorOpenStateThread.setCommunicationService(multicastListenerReaderThread);

        multicastListenerReaderThread.openBroadCastSocket();

        new Thread(multicastListenerReaderThread).start();

        if (RUN_TOGGLE_DOOR_THREAD) {
//            new Thread(toggleDoorOpenStateThread).start();

            while (true) {

                final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                LOG.info("Hit Enter to send message");

                final String s = br.readLine();
                LOG.info("Sending message ...");

                // toggle
                door1CloseStateBinaryInput.setPresentValue(!(Boolean) door1CloseStateBinaryInput.getPresentValue());

                LOG.info("Door is now "
                        + (((Boolean) door1CloseStateBinaryInput.getPresentValue()) ? "locked" : "unlocked"));

                ToogleDoorOpenStateThread.sendCOV(device, door1CloseStateBinaryInput, vendorMap,
                        multicastListenerReaderThread);

                LOG.info("Sending message done.");
            }
        }
    }

    private static Device createDevice(final Map<Integer, String> vendorMap) {

        final Device device = new DefaultDevice();
        device.setId(NetworkUtils.DEVICE_INSTANCE_NUMBER);
        device.setName(NetworkUtils.OBJECT_NAME);
        device.setVendorMap(vendorMap);
        device.setObjectType(ObjectType.DEVICE);

        Device childDevice = null;

        // 1
        // apdu.getServiceParameters().add(createMultiStateValueServiceParameter(1));
        childDevice = new DefaultDevice();
        childDevice.setId(1);
        childDevice.setName("module_type");
        childDevice.setVendorMap(vendorMap);
        childDevice.setObjectType(ObjectType.MULTI_STATE_VALUE);
        device.getChildDevices().add(childDevice);

        // 2
//		apdu.getServiceParameters().add(createMultiStateValueServiceParameter(2));
        childDevice = new DefaultDevice();
        childDevice.setId(2);
        childDevice.setName("alarm_type");
        childDevice.setVendorMap(vendorMap);
        childDevice.setObjectType(ObjectType.MULTI_STATE_VALUE);
        device.getChildDevices().add(childDevice);

        // 3
//		apdu.getServiceParameters().add(binaryInputServiceParameter(1));
        childDevice = new BinaryInputDevice();
        childDevice.setId(1);
        childDevice.setName("door1_close_state");
        childDevice.setVendorMap(vendorMap);
        childDevice.setObjectType(ObjectType.BINARY_INPUT);
        device.getChildDevices().add(childDevice);

        // 4
//		apdu.getServiceParameters().add(binaryInputServiceParameter(2));
        childDevice = new BinaryInputDevice();
        childDevice.setId(2);
        childDevice.setName("door2_close_state");
        childDevice.setVendorMap(vendorMap);
        childDevice.setObjectType(ObjectType.BINARY_INPUT);
        device.getChildDevices().add(childDevice);

        // 5
//		apdu.getServiceParameters().add(createMultiStateValueServiceParameter(3));
        childDevice = new DefaultDevice();
        childDevice.setId(3);
        childDevice.setName("door1_state");
        childDevice.setVendorMap(vendorMap);
        childDevice.setObjectType(ObjectType.MULTI_STATE_VALUE);
        device.getChildDevices().add(childDevice);

        // 6
//		apdu.getServiceParameters().add(createMultiStateValueServiceParameter(4));
        childDevice = new DefaultDevice();
        childDevice.setId(4);
        childDevice.setName("door1_command");
        childDevice.setVendorMap(vendorMap);
        childDevice.setObjectType(ObjectType.MULTI_STATE_VALUE);
        device.getChildDevices().add(childDevice);

        // 7
//		apdu.getServiceParameters().add(createNotificationClassServiceParameter(50));
        childDevice = new DefaultDevice();
        childDevice.setId(50);
        childDevice.setName("notificaton_class_object");
        childDevice.setVendorMap(vendorMap);
        childDevice.setObjectType(ObjectType.NOTIFICATION_CLASS);
        device.getChildDevices().add(childDevice);

        // 8
//		apdu.getServiceParameters().add(createMultiStateValueServiceParameter(5));
        childDevice = new DefaultDevice();
        childDevice.setId(5);
        childDevice.setName("door2_state");
        childDevice.setVendorMap(vendorMap);
        childDevice.setObjectType(ObjectType.MULTI_STATE_VALUE);
        device.getChildDevices().add(childDevice);

        // 9
//		apdu.getServiceParameters().add(createMultiStateValueServiceParameter(6));
        childDevice = new DefaultDevice();
        childDevice.setId(6);
        childDevice.setName("door2_command");
        childDevice.setObjectType(ObjectType.MULTI_STATE_VALUE);
        device.getChildDevices().add(childDevice);

        // 10
//		apdu.getServiceParameters().add(binaryInputServiceParameter(3));
        childDevice = new BinaryInputDevice();
        childDevice.setId(3);
        childDevice.setName("door3_close_state");
        childDevice.setVendorMap(vendorMap);
        childDevice.setObjectType(ObjectType.BINARY_INPUT);
        device.getChildDevices().add(childDevice);

        // 11
//		apdu.getServiceParameters().add(binaryInputServiceParameter(4));
        childDevice = new BinaryInputDevice();
        childDevice.setId(4);
        childDevice.setName("door4_close_state");
        childDevice.setObjectType(ObjectType.BINARY_INPUT);
        device.getChildDevices().add(childDevice);

        // 12
//		apdu.getServiceParameters().add(createMultiStateValueServiceParameter(7));
        childDevice = new DefaultDevice();
        childDevice.setId(7);
        childDevice.setName("door3_state");
        childDevice.setVendorMap(vendorMap);
        childDevice.setObjectType(ObjectType.MULTI_STATE_VALUE);
        device.getChildDevices().add(childDevice);

        // 13
//		apdu.getServiceParameters().add(createMultiStateValueServiceParameter(8));
        childDevice = new DefaultDevice();
        childDevice.setId(8);
        childDevice.setName("door3_command");
        childDevice.setObjectType(ObjectType.MULTI_STATE_VALUE);
        device.getChildDevices().add(childDevice);

        // 14
//		apdu.getServiceParameters().add(createMultiStateValueServiceParameter(9));
        childDevice = new DefaultDevice();
        childDevice.setId(9);
        childDevice.setName("door4_state");
        childDevice.setVendorMap(vendorMap);
        childDevice.setObjectType(ObjectType.MULTI_STATE_VALUE);
        device.getChildDevices().add(childDevice);

        // 15
//		apdu.getServiceParameters().add(createMultiStateValueServiceParameter(10));
        childDevice = new DefaultDevice();
        childDevice.setId(10);
        childDevice.setName("door4_command");
        childDevice.setVendorMap(vendorMap);
        childDevice.setObjectType(ObjectType.MULTI_STATE_VALUE);
        device.getChildDevices().add(childDevice);

        return device;
    }

    private static Map<Integer, String> readVendorMap(final String filename) throws IOException {

        final File file = new File(filename);
        LOG.info(file.getAbsoluteFile());

        final Map<Integer, String> map = new HashMap<>();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filename));
            String line = null;
            while ((line = reader.readLine()) != null) {

                line = StringUtils.trim(line);

                final String[] split = line.split(";");

                final int vendorId = Integer.parseInt(split[0]);
                String vendorName = split[1];

                if (StringUtils.isBlank(vendorName)) {
                    vendorName = "";
                    for (int i = 2; i < split.length; i++) {
                        if (i > 2) {
                            vendorName += " ";
                        }
                        vendorName += split[i];
                    }
                }

                map.put(vendorId, vendorName);
            }

        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }

        return map;
    }

    @SuppressWarnings("unused")
    private static void runMainOld() throws SocketException, UnknownHostException, IOException {

        final DatagramSocket serverDatagramSocket = new DatagramSocket(NetworkUtils.DEFAULT_PORT);
        serverDatagramSocket.setBroadcast(true);

        new Thread(new Runnable() {

            @Override
            public void run() {

                final boolean running = true;
                while (running) {

                    final byte[] serverBuffer = new byte[256];
                    final DatagramPacket packet = new DatagramPacket(serverBuffer, serverBuffer.length);
                    try {
                        serverDatagramSocket.receive(packet);
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }

                    System.out.println("");
                    System.out.println("Received");
                    System.out.println("Offset: " + packet.getOffset());
                    System.out.println("Length: " + packet.getLength());
                    System.out.println(Utils.bytesToHex(packet.getData(), packet.getOffset(), packet.getLength()));

                    final InetAddress address = packet.getAddress();
                    System.out.println("Address: " + address);

                    final SocketAddress socketAddress = packet.getSocketAddress();
                    System.out.println("SocketAddress: " + socketAddress);

//	                int port = packet.getPort();
//	                String received = new String(packet.getData(), 0, packet.getLength());

//	                System.out.println(port);
//	                System.out.println(received);

//	                packet = new DatagramPacket(serverBuffer, serverBuffer.length, address, port);
//	                String received = new String(packet.getData(), 0, packet.getLength());
//	                 
//	                if (received.equals("end")) {
//	                    running = false;
//	                    continue;
//	                }
//	                socket.send(packet);
                }

//				serverDatagramSocket.close();
            }
        }).start();

        // Test Data: 88 53 2E 50 9D 3F 08 00 27 40 38 EF 08 00 45 00 00 1E 00 01 00 00
        // 40 11 F9 40 C0 A8 00 1F C0 A8 00 1E 00 14 00 0A 00 0A 35 C5 48 69 00 00 00 00
        // 00 00 00 00 00 00 00 00 00 00 00 00

        final IPv4Packet ipv4Packet = new IPv4Packet();
        ipv4Packet.version = 0x45;
        ipv4Packet.service = 0x00;
        ipv4Packet.totalLength = 0x1E;
        ipv4Packet.identification = 0x01;
        ipv4Packet.flags = 0x00;
        ipv4Packet.timeToLive = 0x40;
        ipv4Packet.protocol = 0x11;
        ipv4Packet.checksum = 0x00; // unknown yet

//		ipv4Packet.sourceIP[0] = (byte) 0xC0;
//		ipv4Packet.sourceIP[1] = (byte) 0xA8;
//		ipv4Packet.sourceIP[2] = (byte) 0x00;
//		ipv4Packet.sourceIP[3] = (byte) 0x1F;
//		
//		ipv4Packet.targetIP[0] = (byte) 0xC0;
//		ipv4Packet.targetIP[1] = (byte) 0xA8;
//		ipv4Packet.targetIP[2] = (byte) 0x00;
//		ipv4Packet.targetIP[3] = (byte) 0x1E;
//		
//		UDPPacket udpPacket = new UDPPacket();
//		udpPacket.sourcePort = 0x14;
//		udpPacket.destinationPort = 0x0A;
//		udpPacket.length = 0x0A;
//		udpPacket.checksum = 0x00; // unknown yet
//		udpPacket.payloadLength = 2;
//		udpPacket.payload = new byte[2];
//		udpPacket.payload[0] = 0x48;
//		udpPacket.payload[1] = 0x69;

        ipv4Packet.sourceIP[0] = (byte) 0xC0;
        ipv4Packet.sourceIP[1] = (byte) 0xA8;
        ipv4Packet.sourceIP[2] = (byte) 0x00;
        ipv4Packet.sourceIP[3] = (byte) 0xEA;

        ipv4Packet.targetIP[0] = (byte) 0xC0;
        ipv4Packet.targetIP[1] = (byte) 0xA8;
        ipv4Packet.targetIP[2] = (byte) 0x00;
        ipv4Packet.targetIP[3] = (byte) 0xFF;

        final UDPPacket udpPacket = new UDPPacket();
        udpPacket.sourcePort = (short) 0xBAC0;
        udpPacket.destinationPort = (short) 0xBAC0;
        udpPacket.length = (short) 0x14;
        udpPacket.checksum = 0x00; // unknown yet
        // payload
        udpPacket.payloadLength = 12;
        udpPacket.payload = new byte[udpPacket.payloadLength];
        // BVLC
        udpPacket.payload[0] = (byte) 0x81;
        udpPacket.payload[1] = (byte) 0x0B;
        udpPacket.payload[2] = (byte) 0x00;
        udpPacket.payload[3] = (byte) 0x0C;
        // BACNET
        udpPacket.payload[4] = (byte) 0x01;
        udpPacket.payload[5] = (byte) 0x20;
        udpPacket.payload[6] = (byte) 0xFF;
        udpPacket.payload[7] = (byte) 0xFF;
        udpPacket.payload[8] = (byte) 0x00;
        udpPacket.payload[9] = (byte) 0xFF;
        // BACAPP
        udpPacket.payload[10] = (byte) 0x10;
        udpPacket.payload[11] = (byte) 0x08;

        ipv4Packet.udpPacket = udpPacket;

        ipv4Packet.computeUDPChecksum();
        ipv4Packet.computeIPv4Checksum();

        // correct: FF FF FF FF FF FF 6C 40 08 97 D1 12 08 00 45 00 00 28 0A 32 00 00 40
        // 11 ED 59 C0 A8 00 EA C0 A8 00 FF BA C0 BA C0 00 14 73 CC 81 0B 00 0C 01 20 FF
        // FF 00 FF 10 08
        // Ethernet: FF FF FF FF FF FF 6C 40 08 97 D1 12 08 00
        // IP: 45 00 00 28 0A 32 00 00 40 11 ED 59 C0 A8 00 EA C0 A8 00 FF
        // UDP: BA C0 BA C0 00 14 73 CC 81 0B 00 0C 01 20 FF FF 00 FF 10 08

        // incorrect: FF FF FF FF FF FF 6C 40 08 97 D1 12 08 00 45 00 00 30 15 C7 00 00
        // 40 11 E1 BC C0 A8 00 EA C0 A8 00 FF F6 A6 BA C0 00 1C 4E 74 BA C0 BA C0 00 14
        // 73 CC 81 0B 00 0C 01 20 FF FF 00 FF 10 08
        // Ethernet: FF FF FF FF FF FF 6C 40 08 97 D1 12 08 00
        // IP: 45 00 00 30 15 C7 00 00 40 11 E1 BC C0 A8 00 EA C0 A8 00 FF
        // UDP: F6 A6 BA C0 00 1C 4E 74
        // BA C0 BA C0 00 14 73 CC 81 0B 00 0C 01 20 FF FF 00 FF 10 08

//		try (DatagramSocket datagramSocket = new DatagramSocket(IpNetwork.DEFAULT_PORT))
//		{
        // payload
//			byte[] buf = udpPacket.getBytesTotal();

        // because the Java DatagramPacket class constructs the UDP header for us, we do
        // only need to supply the UDP payload
        final byte[] buf = udpPacket.getBytesPayloadOnly();

        // address
        final String broadcastIP = Utils.retrieveNetworkInterfaceBroadcastIPs().get(0);
//			InetAddress targetInetAddress = InetAddress.getByName("localhost");
        final InetAddress targetInetAddress = InetAddress.getByName(broadcastIP);
        final int targetPort = NetworkUtils.DEFAULT_PORT;

        final DatagramPacket packet = new DatagramPacket(buf, 0, buf.length, targetInetAddress, targetPort);

        // broadcast - the port from which this broadcast is sent out is not specified!
//			datagramSocket.setBroadcast(true);
//			serverDatagramSocket.setBroadcast(true);

        System.out.println("");
        System.out.println("Sending");
        System.out.println(Utils.bytesToHex(buf));
//			datagramSocket.send(packet);
        serverDatagramSocket.send(packet);

        serverDatagramSocket.close();
//		}
    }
}