package de.bacnetz;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.bacnetz.common.Utils;
import de.bacnetz.common.utils.NetworkUtils;
import de.bacnetz.controller.DefaultMessageController;
import de.bacnetz.stack.IPv4Packet;
import de.bacnetz.stack.MulticastListenerReaderThread;
import de.bacnetz.stack.UDPPacket;

/**
 *
 */
public class App {

	private static final Logger LOG = LogManager.getLogger(App.class);

//	private static final String ENCODING = "Cp1252";

//	private static final String ENCODING = "ISO-8859-1";

	private static final String ENCODING = "UTF-8";

	public static void main(final String[] args) throws IOException {
//		runMainOld();
		runMain();
//		runFixVendorCSV();
	}

	private static void runFixVendorCSV() throws IOException {

		boolean firstLine = true;

		final File file = new File("C:\\Temp\\BACnetVerndors_fix.csv");
		final FileOutputStream fileOutputStream = new FileOutputStream(file);

		final BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream, ENCODING));

		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader("C:\\Temp\\BACnetVerndors.csv"));
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

		final DefaultMessageController defaultMessageController = new DefaultMessageController();
		defaultMessageController.setVendorMap(vendorMap);

		final MulticastListenerReaderThread multicastListenerReaderThread = new MulticastListenerReaderThread();
		multicastListenerReaderThread.setVendorMap(vendorMap);
		multicastListenerReaderThread.setBindPort(NetworkUtils.DEFAULT_PORT);
		multicastListenerReaderThread.getMessageControllers().add(defaultMessageController);

		new Thread(multicastListenerReaderThread).start();
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

		// datagramSocket.close();
//		}
	}
}
