package de.bacnetz.stack;

import de.bacnetz.common.Utils;

public class IPv4Packet {

	public byte version;

	public byte service;

	public short totalLength;

	public short identification;

	public short flags;

	public byte timeToLive;

	/** the protocol code of the wrapped package */
	public byte protocol;

	public short checksum;

	public byte[] sourceIP = new byte[4];

	public byte[] targetIP = new byte[4];

	public UDPPacket udpPacket;

	public void computeIPv4Checksum() {

		final byte buffer[] = new byte[20];

		int idx = 0;

		buffer[idx] = this.version;
		idx++;

		buffer[idx] = this.service;
		idx++;

		idx = Utils.addShortToBuffer(buffer, idx, totalLength);
		idx = Utils.addShortToBuffer(buffer, idx, identification);
		idx = Utils.addShortToBuffer(buffer, idx, flags);

		buffer[idx] = this.timeToLive;
		idx++;

		buffer[idx] = this.protocol;
		idx++;

		// checksum - not known yet
		buffer[idx] = 0x00;
		idx++;
		buffer[idx] = 0x00;
		idx++;

		// source IP
		for (int i = 0; i < 4; i++) {
			buffer[idx] = sourceIP[i];
			idx++;
		}

		// target IP
		for (int i = 0; i < 4; i++) {
			buffer[idx] = targetIP[i];
			idx++;
		}

//		// 4500 003C 1C46 4000 4006 B1E6 AC10 0A63 AC10 0A0C
//		// 4500 003c 1c46 4000 4006 b1e6 ac10 0a63 ac10 0a0c
//		buffer[0] = (byte) 0x45;
//		buffer[1] = (byte) 0x00; 
//		buffer[2] = (byte) 0x00;
//		buffer[3] = (byte) 0x3c; 
//		buffer[4] = (byte) 0x1c;
//		buffer[5] = (byte) 0x46;
//		buffer[6] = (byte) 0x40;
//		buffer[7] = (byte) 0x00;
//		buffer[8] = (byte) 0x40;
//		buffer[9] = (byte) 0x06;
////		buffer[10] = (byte) 0xb1;
////		buffer[11] = (byte) 0xe6;
//		buffer[10] = (byte) 0x00;
//		buffer[11] = (byte) 0x00;
//		buffer[12] = (byte) 0xac;
//		buffer[13] = (byte) 0x10;
//		buffer[14] = (byte) 0x0a;
//		buffer[15] = (byte) 0x63;
//		buffer[16] = (byte) 0xac;
//		buffer[17] = (byte) 0x10;
//		buffer[18] = (byte) 0x0a;
//		buffer[19] = (byte) 0x0c;

		idx = 20;

		// System.out.println(Util.bytesToHex(buffer));

		final int amountOfShorts = idx / 2;

		int operandA = 0;
		int operandB = 0;
		for (int i = 0; i < amountOfShorts; i++) {

			operandB = Utils.bufferToShort(buffer, i * 2);

			// System.out.println(String.format("0x%08X", operandA));
			// System.out.println(String.format("0x%08X", operandB));

			final int sum = operandA + operandB;

			if (sum > 65535) {
				operandA = (sum & 0xFFFF);
				operandA += 1;
			} else {
				operandA = sum;
			}

			// System.out.println(operandA);
			// System.out.println(String.format("0x%08X", operandA));
			// System.out.println("");
		}

		// System.out.println(operandA);

		// https://ncalculators.com/digital-computation/1s-2s-complement-calculator.htm

		// 0x4E19 = 0100 1110 0001 1001
		// 0xB1E6 = 1011 0001 1110 0110

		// operandA = onesComplement(operandA);
		operandA = ~operandA;
		operandA = operandA & 0xFFFF;
		// System.out.println("OnesComplement: " + String.format("0x%08X", operandA));

		// System.out.println("OnesComplement: " + String.format("0x%08X", operandA));

		// System.out.println(operandA);

		// TODO: write unit test that verifies that operandA is 0x0000F940 = 63808d
		checksum = (short) (operandA & 0xFFFF);

	}

	public void computeUDPChecksum() {

		// pseudoHeader Size
		// Source IP (From IP Header) (4 byte),
		// Destination IP (From IP Header) (4 byte),
		// Reserved (= a Byte of zeroes) (1 byte),
		// Protocol (From IP Header) (1 Byte),
		// Padding (= Byte of zeroes) (1 Byte),
		// UDP Datagram Length (FROM UDP header) (1 byte)
		final int pseudoHeaderSize = 4 + 4 + 1 + 1 + 1 + 1;

		final int udpHeaderSizeWithoutChecksum = 2 + 2 + 2 + 2;

		final int bufferSize = pseudoHeaderSize + udpHeaderSizeWithoutChecksum + udpPacket.payloadLength;

		final byte buffer[] = new byte[bufferSize];

		int idx = 0;

		// source IP
		for (int i = 0; i < 4; i++) {
			buffer[idx] = sourceIP[i];
			idx++;
		}

		// target IP
		for (int i = 0; i < 4; i++) {
			buffer[idx] = targetIP[i];
			idx++;
		}

		// reserved
		buffer[idx] = 0x00;
		idx++;

		// protocol
		buffer[idx] = protocol;
		idx++;

//		// padding
//		buffer[idx] = 0x00;
//		idx++;

		// udp packet length
		idx = Utils.addShortToBuffer(buffer, idx, udpPacket.length);

		// normal UDP header
		idx = Utils.addShortToBuffer(buffer, idx, udpPacket.sourcePort);
		idx = Utils.addShortToBuffer(buffer, idx, udpPacket.destinationPort);
		idx = Utils.addShortToBuffer(buffer, idx, udpPacket.length);

		for (int i = 0; i < udpPacket.payloadLength; i++) {
			buffer[idx] = udpPacket.payload[i];
			idx++;
		}

		// System.out.println(Util.bytesToHex(buffer));

		final int amountOfShorts = idx / 2;

		int operandA = 0;
		int operandB = 0;
		for (int i = 0; i < amountOfShorts; i++) {

			operandB = Utils.bufferToShort(buffer, i * 2);

			final int sum = operandA + operandB;

			if (sum > 65535) {
				operandA = (sum & 0xFFFF);
				operandA += 1;
			} else {
				operandA = sum;
			}

//			System.out.println(operandA);
//			System.out.println(String.format("0x%08X", operandA));
//			System.out.println("");
		}

		// System.out.println(operandA);

		operandA = Utils.onesComplement(operandA);

		// System.out.println(operandA);

		// TODO: write a unit test that this checksum is now 13765
		udpPacket.checksum = (short) (operandA & 0xFFFF);
	}

}
