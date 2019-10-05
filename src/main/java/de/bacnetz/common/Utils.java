package de.bacnetz.common;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

public class Utils {

	public static List<String> retrieveNetworkInterfaceBroadcastIPs() throws SocketException {

		List<String> result = new ArrayList<>();

		Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();

		while (networkInterfaceEnumeration.hasMoreElements()) {

			NetworkInterface networkInterface = networkInterfaceEnumeration.nextElement();

			// ignore loopback interfaces
			if (networkInterface.isLoopback()) {
				continue;
			}

			Iterator<InterfaceAddress> interfaceAddressIterator = networkInterface.getInterfaceAddresses().iterator();
			while (interfaceAddressIterator.hasNext()) {

				InterfaceAddress interfaceAddress = interfaceAddressIterator.next();
				
				// retrieve the broadcast address that was configured on this device
				InetAddress broadcast = interfaceAddress.getBroadcast();
				if (broadcast != null) {
					result.add(broadcast.getHostAddress());
				}
			}
		}

		return result;
	}

	public static int nonLeadingZeroComplement(int i) {
		
	    int ones = (Integer.highestOneBit(i) << 1) - 1;
	    return i ^ ones;
	}
	
	public static int onesComplement(int n) {
		
        // Find number of bits in the  
        // given integer 
        int number_of_bits =  
               (int)(Math.floor(Math.log(n) / 
                             Math.log(2))) + 1; 
  
        // XOR the given integer with poe(2, 
        // number_of_bits-1 and print the result 
        return ((1 << number_of_bits) - 1) ^ n; 
    } 

	public static short bufferToShort(byte[] buffer, int idx) {
		return (short) ((short)((buffer[idx]& 0xff) << 8) + (short)(buffer[idx+1]));
	}
	
	public static int bufferToInt(byte[] buffer, int idx) {
		return (int) ((int)(((int)buffer[idx] & 0xff) << 8) + (int)((int)buffer[idx+1] & 0xff));
	}

	public static int addShortToBuffer(byte[] buffer, int idx, short data) {
		
		buffer[idx] = (byte)(0xFF & (data >> 8));
		idx++;
		buffer[idx] = (byte)(0xFF & (data));
		idx++;
		
		return idx;
	}
	
	public static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
	
	public static String bytesToHex(byte[] data) {
		
		final int stride = 3;
	    char[] hexChars = new char[data.length * stride];
	    for (int j = 0; j < data.length; j++) {
	    	
	        int v = data[j] & 0xFF;
	        hexChars[j * stride + 0] = HEX_ARRAY[v >>> 4];
	        hexChars[j * stride + 1] = HEX_ARRAY[v & 0x0F];
	        hexChars[j * stride + 2] = ' ';
	    }
	    
	    return new String(hexChars);
	}

	public static String bytesToHex(byte[] data, int offset, int length) {
		
		final int stride = 3;
		char[] hexChars = new char[length * stride];
	    for (int j = 0; j < length; j++) {
	    	
	    	int idx = offset+j;
	        int v = data[idx] & 0xFF;
	        hexChars[idx * stride + 0] = HEX_ARRAY[v >>> 4];
	        hexChars[idx * stride + 1] = HEX_ARRAY[v & 0x0F];
	        hexChars[idx * stride + 2] = ' '; 
	    }
	    
	    return new String(hexChars);
	}
}
