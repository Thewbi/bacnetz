package de.bacnetz.stack;

import de.bacnetz.common.utils.Utils;

public class UDPPacket {
	
	public short sourcePort;
	
	public short destinationPort;
	
	public short length;
	
	public short checksum;
	
	public int payloadLength;
	
	public byte[] payload = new byte[512];
	
	public byte[] getBytesTotal() {
		
		int idx = 0;
		
		final int bufferLength = 2 + 2 + 2 + 2 + payloadLength;
		byte[] result = new byte[bufferLength];
		
		// copy UDP header
		idx = Utils.addShortToBuffer(result, idx, sourcePort);
		idx = Utils.addShortToBuffer(result, idx, destinationPort);
		idx = Utils.addShortToBuffer(result, idx, length);
		idx = Utils.addShortToBuffer(result, idx, checksum);
		
		// copy payload
		byte[] sourceArray = payload;
		int sourceStartIndex = 0;
		byte[] targetArray = result;
		int targetStartIndex = idx;
		
		System.arraycopy(sourceArray, 
                sourceStartIndex,
                targetArray,
                targetStartIndex,
                payloadLength);
		
		return result;
	}

	public byte[] getBytesPayloadOnly() {
		
		int idx = 0;
		
		final int bufferLength = payloadLength;
		byte[] result = new byte[bufferLength];
		
		// copy payload
		byte[] sourceArray = payload;
		int sourceStartIndex = 0;
		byte[] targetArray = result;
		int targetStartIndex = idx;
		
		System.arraycopy(sourceArray, 
                sourceStartIndex,
                targetArray,
                targetStartIndex,
                payloadLength);
		
		return result;
	}

}
