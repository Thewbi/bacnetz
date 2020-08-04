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
		final byte[] result = new byte[bufferLength];

		// copy UDP header
		idx = Utils.addShortToBuffer(result, idx, sourcePort);
		idx = Utils.addShortToBuffer(result, idx, destinationPort);
		idx = Utils.addShortToBuffer(result, idx, length);
		idx = Utils.addShortToBuffer(result, idx, checksum);

		// copy payload
		final byte[] sourceArray = payload;
		final int sourceStartIndex = 0;
		final byte[] targetArray = result;
		final int targetStartIndex = idx;

		System.arraycopy(sourceArray, sourceStartIndex, targetArray, targetStartIndex, payloadLength);

		return result;
	}

	public byte[] getBytesPayloadOnly() {

		final int idx = 0;

		final int bufferLength = payloadLength;
		final byte[] result = new byte[bufferLength];

		// copy payload
		final byte[] sourceArray = payload;
		final int sourceStartIndex = 0;
		final byte[] targetArray = result;
		final int targetStartIndex = idx;

		System.arraycopy(sourceArray, sourceStartIndex, targetArray, targetStartIndex, payloadLength);

		return result;
	}

}
