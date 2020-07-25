package de.bacnetz.common;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

public class Utils {

//	public static final int DEVICE_INSTANCE_NUMBER = 26;
	public static final int DEVICE_INSTANCE_NUMBER = 10001;

	public static final String OBJECT_NAME = "Device_IO420";

	public static final String ENCODING_Cp1252 = "Cp1252";

	public static final String ENCODING_ISO_8859_1 = "ISO-8859-1";

	public static final String ENCODING_UTF_8 = "UTF-8";

	public static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

	public static String byteArrayToStringNoPrefix(final byte[] data) {

		if (data == null) {
			return StringUtils.EMPTY;
		}
		final StringBuffer stringBuffer = new StringBuffer();
		for (final byte tempByte : data) {
			stringBuffer.append(String.format("%1$02X", tempByte).toUpperCase(Locale.getDefault())).append(" ");
		}

		return stringBuffer.toString();
	}

	public static List<String> retrieveNetworkInterfaceBroadcastIPs() throws SocketException {

		final List<String> result = new ArrayList<>();

		final Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();

		while (networkInterfaceEnumeration.hasMoreElements()) {

			final NetworkInterface networkInterface = networkInterfaceEnumeration.nextElement();

			// ignore loopback interfaces
			if (networkInterface.isLoopback()) {
				continue;
			}

			final Iterator<InterfaceAddress> interfaceAddressIterator = networkInterface.getInterfaceAddresses()
					.iterator();
			while (interfaceAddressIterator.hasNext()) {

				final InterfaceAddress interfaceAddress = interfaceAddressIterator.next();

				// retrieve the broadcast address that was configured on this device
				final InetAddress broadcast = interfaceAddress.getBroadcast();
				if (broadcast != null) {
					result.add(broadcast.getHostAddress());
				}
			}
		}

		return result;
	}

	public static int nonLeadingZeroComplement(final int i) {

		final int ones = (Integer.highestOneBit(i) << 1) - 1;
		return i ^ ones;
	}

	public static int onesComplement(final int n) {

		// Find number of bits in the
		// given integer
		final int number_of_bits = (int) (Math.floor(Math.log(n) / Math.log(2))) + 1;

		// XOR the given integer with poe(2,
		// number_of_bits-1 and print the result
		return ((1 << number_of_bits) - 1) ^ n;
	}

	public static short bufferToShort(final byte[] buffer, final int idx) {
		return (short) ((short) ((buffer[idx] & 0xff) << 8) + (buffer[idx + 1]));
	}

	public static int bufferToInt(final byte[] buffer, final int idx) {
		return ((buffer[idx] & 0xff) << 24) + ((buffer[idx + 1] & 0xff) << 16) + ((buffer[idx + 2] & 0xff) << 8)
				+ (buffer[idx + 3] & 0xff);
	}

	public static void intToBuffer(final int data, final byte[] buffer, final int offset) {

		final byte a = (byte) ((data >> 24) & 0xff);
		final byte b = (byte) ((data >> 16) & 0xff);
		final byte c = (byte) ((data >> 8) & 0xff);
		final byte d = (byte) ((data >> 0) & 0xff);

		buffer[offset + 0] = a;
		buffer[offset + 1] = b;
		buffer[offset + 2] = c;
		buffer[offset + 3] = d;
	}

	public static int addShortToBuffer(final byte[] buffer, int idx, final short data) {

		buffer[idx] = (byte) (0xFF & (data >> 8));
		idx++;
		buffer[idx] = (byte) (0xFF & (data));
		idx++;

		return idx;
	}

	public static String bytesToHex(final byte[] data) {

		final int stride = 3;
		final char[] hexChars = new char[data.length * stride];
		for (int j = 0; j < data.length; j++) {

			final int v = data[j] & 0xFF;
			hexChars[j * stride + 0] = HEX_ARRAY[v >>> 4];
			hexChars[j * stride + 1] = HEX_ARRAY[v & 0x0F];
			hexChars[j * stride + 2] = ' ';
		}

		return new String(hexChars);
	}

	public static String bytesToHex(final byte[] data, final int offset, final int length) {

		final int stride = 3;
		final char[] hexChars = new char[length * stride];
		for (int j = 0; j < length; j++) {

			final int idx = offset + j;
			final int v = data[idx] & 0xFF;
			hexChars[idx * stride + 0] = HEX_ARRAY[v >>> 4];
			hexChars[idx * stride + 1] = HEX_ARRAY[v & 0x0F];
			hexChars[idx * stride + 2] = ' ';
		}

		return new String(hexChars);
	}

	/**
	 * Example:
	 *
	 * <pre>
	 * final byte[] hexStringToByteArray = Utils.hexStringToByteArray(
	 * 		"360102001101000000c50102d84ce000170c00246d01d80a4b4e582049502042414f5320373737000000000000000000000000000000");
	 * </pre>
	 *
	 * @param s
	 * @return
	 */
	public static byte[] hexStringToByteArray(final String s) {
		final int len = s.length();
		final byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

	public static int bytesToUnsignedShort(final byte byte1, final byte byte2, final boolean bigEndian) {
		if (bigEndian) {
			return (((byte1 & 0xFF) << 8) | (byte2 & 0xFF));
		}
		return (((byte2 & 0xFF) << 8) | (byte1 & 0xFF));
	}
}
