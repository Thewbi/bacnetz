package de.bacnetz.stack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.bacnetz.common.Utils;

/**
 * Network Layer Protocol Data Unit
 * 
 * Structure Definition in 6.2 Network Layer PDU Structure in ANSI/ASHRAE
 * Standard 135-2012 on page 55
 * 
 * 
 * <pre>
 * Bit 7: 1 indicates that the NSDU conveys a network layer message. Message Type field is present.
 * 0 indicates that the NSDU contains a BACnet APDU. Message Type field is absent.
 * 
 * Bit 6: Reserved. Shall be zero.
 * 
 * Bit 5: Destination specifier where:
 * 0 = DNET, DLEN, DADR, and Hop Count absent
 * 1 = DNET, DLEN, and Hop Count present
 * DLEN = 0 denotes broadcast MAC DADR and DADR field is absent
 * DLEN > 0 specifies length of DADR field
 * 
 * Bit 4: Reserved. Shall be zero.
 * 
 * Bit 3: Source specifier where:
 * 0 = SNET, SLEN, and SADR absent
 * 1 = SNET, SLEN, and SADR present
 * SLEN = 0 Invalid
 * SLEN > 0 specifies length of SADR field
 * 
 * Bit 2: The value of this bit corresponds to the data_expecting_reply parameter in the N-UNITDATA primitives.
 * 1 indicates that a BACnet-Confirmed-Request-PDU, a segment of a BACnet-ComplexACK-PDU, or a network
 * layer message expecting a reply is present.
 * 0 indicates that other than a BACnet-Confirmed-Request-PDU, a segment of a BACnet-ComplexACK-PDU, or a
 * network layer message expecting a reply is present.
 * 
 * Bits 1,0: Network priority where:
 * B'11' = Life Safety message
 * B'10' = Critical Equipment message
 * B'01' = Urgent message
 * B '00' = Normal message
 * </pre>
 */
public class NPDU {

	private static final Logger LOG = LogManager.getLogger(NPDU.class);

	/** eight bit unsigned version number */
	private int version;

	private int control;

	private int destinationNetworkNumber;

	/** 0 indicates Broadcast on Destination Network */
	private int destinationMACLayerAddressLength;

	private int destinationMac;

	private int destinationHopCount;

	private int sourceNetworkAddress;

	private int sourceMacLayerAddressLength;

	private int sourceMac;

	private int structureLength;

	private NetworkLayerMessageType networkLayerMessageType;

	public NPDU() {

	}

	public NPDU(final NPDU other) {
		this.version = other.version;
		this.control = other.control;
		this.destinationNetworkNumber = other.destinationNetworkNumber;
		this.destinationMACLayerAddressLength = other.destinationMACLayerAddressLength;
		this.destinationHopCount = other.destinationHopCount;
		this.sourceNetworkAddress = other.sourceNetworkAddress;
		this.sourceMacLayerAddressLength = other.sourceMacLayerAddressLength;
		this.sourceMac = other.sourceMac;
		this.structureLength = other.structureLength;
	}

	public void fromBytes(final byte[] data, final int startIndex) {

		int offset = 0;
		structureLength = 0;

		version = data[startIndex + offset++];
		structureLength++;

		control = data[startIndex + offset++];
		structureLength++;

		if (isDestinationSpecifierPresent()) {

			destinationNetworkNumber = Utils.bytesToUnsignedShort(data[startIndex + offset++],
					data[startIndex + offset++], true);
			structureLength += 2;

			destinationMACLayerAddressLength = data[startIndex + offset++] & 0xFF;
			structureLength += 1;

			for (int i = 0; i < destinationMACLayerAddressLength; i++) {
				if (i > 0) {
					destinationMac <<= 8;
				}
				destinationMac |= (data[startIndex + offset++] & 0xFF);
			}
			structureLength += destinationMACLayerAddressLength;

		} else {
			LOG.trace("No destination network information is present!");
		}

		if (isSourceSpecifierPresent()) {

			sourceNetworkAddress = Utils.bytesToUnsignedShort(data[startIndex + offset++], data[startIndex + offset++],
					true);
			structureLength += 2;

			sourceMacLayerAddressLength = data[startIndex + offset++] & 0xFF;
			structureLength += 1;

			for (int i = 0; i < sourceMacLayerAddressLength; i++) {
				if (i > 0) {
					sourceMac <<= 8;
				}
				sourceMac |= (data[startIndex + offset++] & 0xFF);
			}

			structureLength += sourceMacLayerAddressLength;
		}

		// when is there a destination hop count?????
		if (isDestinationSpecifierPresent()) {
			destinationHopCount = data[startIndex + offset++] & 0xFF;
			structureLength += 1;
		}

		if (!isAPDUMessage()) {
			LOG.trace("Request does not contain a APDU!");
		}
		if (isNetworkLayerMessage()) {
			// next byte is network layer message type
			networkLayerMessageType = NetworkLayerMessageType.fromInt(data[startIndex + offset++] & 0xFF);
			structureLength += 1;
		}

		// reply is expected
		if (isConfirmedRequestPDUPresent()) {
			LOG.trace("Reply is expected!");
		}
	}

	public int getDataLength() {

		// two byte minimum for version and control
		int dataLength = 2;

		// destination specifier has four byte
		if (isDestinationSpecifierPresent()) {

			// 2 byte: destination network address
			dataLength += 2;

			// 1 byte: mac layer address length
			dataLength += 1;

			// n bytes: for the mac itself
			dataLength += destinationMACLayerAddressLength;

			// 1 byte: hopCount
			dataLength += 1;
		}

		return dataLength;
	}

	public void toBytes(final byte[] data, final int offset) {

		int index = 0;

		data[offset + index++] = (byte) version;
		data[offset + index++] = (byte) control;

		if (isDestinationSpecifierPresent()) {

			// 2 byte network number
			Utils.addShortToBuffer(data, offset + index, (short) destinationNetworkNumber);
			index += 2;
			data[offset + index++] = (byte) destinationMACLayerAddressLength;

			if (destinationMACLayerAddressLength > 0) {

				for (int i = 0; i < destinationMACLayerAddressLength; i++) {

					data[offset + index++] = (byte) ((destinationMac >> (8 * (destinationMACLayerAddressLength - 1 - i))
							& 0xFF));
				}
			}

			data[offset + index++] = (byte) destinationHopCount;
		}
	}

	public byte[] getBytes() {

		final byte[] result = new byte[getDataLength()];
		toBytes(result, 0);

		return result;
	}

	/**
	 * Bit 7 of the control byte:
	 * <ul>
	 * <li />1 indicates that the NSDU conveys a network layer message. Message Type
	 * field is present.
	 * <li />0 indicates that the NSDU contains a BACnet APDU. Message Type field is
	 * absent.
	 * </ul>
	 *
	 * @return true -> APDU is present, 0 --> it is a Network message
	 */
	public boolean isAPDUMessage() {
		// bit 7 is zero => 0xxx xxxx
		return 0 == (control & 0x80);
	}

	public boolean isNetworkLayerMessage() {
		// bit 7 is one => 1xxx xxxx
		return !isAPDUMessage();
	}

	/**
	 * Destination specifier where:
	 * <ul>
	 * <li />0 = DNET, DLEN, DADR, and Hop Count absent
	 * <li />1 = DNET, DLEN, and Hop Count present DLEN = 0 denotes broadcast MAC
	 * DADR and DADR field is absent DLEN > 0 specifies length of DADR field
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isDestinationSpecifierPresent() {
		// 0x20 => 0010 0000
		return 0 < (control & 0x20);
	}

	/**
	 * Source specifier where:
	 * <ul>
	 * <li />0 = SNET, SLEN, and SADR absent
	 * <li />1 = SNET, SLEN, and SADR present SLEN = 0 Invalid SLEN > 0 specifies
	 * length of SADR field
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isSourceSpecifierPresent() {
		// 0x08 => 0000 1000
		return 0 < (control & 0x08);
	}

	/**
	 * The value of this bit corresponds to the data_expecting_reply parameter in
	 * the N-UNITDATA primitives.
	 * 
	 * <ul>
	 * <li />1 indicates that a BACnet-Confirmed-Request-PDU, a segment of a
	 * BACnet-ComplexACK-PDU, or a network layer message expecting a reply is
	 * present.
	 * <li />0 indicates that other than a BACnet-Confirmed-Request-PDU, a segment
	 * of a BACnet-ComplexACK-PDU, or a network layer message expecting a reply is
	 * present.
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isConfirmedRequestPDUPresent() {
		// 0x04 => 0000 0100
		return 0 < (control & 0x04);
	}

	/**
	 * Bits 1,0: Network priority where:
	 * <ul>
	 * <li />B'11' = Life Safety message
	 * <li />B'10' = Critical Equipment message
	 * <li />B'01' = Urgent message
	 * <li />B'00' = Normal message
	 * </ul>
	 * 
	 * @return
	 */
	public NetworkPriority getNetworkPriority() {
		// 0x03 => 0000 0011
		return NetworkPriority.fromInt(control & 0x03);
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(final int version) {
		this.version = version;
	}

	public int getControl() {
		return control;
	}

	public void setControl(final int control) {
		this.control = control;
	}

	public int getDestinationNetworkNumber() {
		return destinationNetworkNumber;
	}

	public void setDestinationNetworkNumber(final int destinationNetworkNumber) {
		this.destinationNetworkNumber = destinationNetworkNumber;
	}

	public int getDestinationMACLayerAddressLength() {
		return destinationMACLayerAddressLength;
	}

	public void setDestinationMACLayerAddressLength(final int destinationMACLayerAddressLength) {
		this.destinationMACLayerAddressLength = destinationMACLayerAddressLength;
	}

	public int getDestinationHopCount() {
		return destinationHopCount;
	}

	public void setDestinationHopCount(final int destinationHopCount) {
		this.destinationHopCount = destinationHopCount;
	}

	public int getStructureLength() {
		return structureLength;
	}

	public int getSourceNetworkAddress() {
		return sourceNetworkAddress;
	}

	public void setSourceNetworkAddress(final int sourceNetworkAddress) {
		this.sourceNetworkAddress = sourceNetworkAddress;
	}

	public int getSourceMacLayerAddressLength() {
		return sourceMacLayerAddressLength;
	}

	public void setSourceMacLayerAddressLength(final int sourceMacLayerAddressLength) {
		this.sourceMacLayerAddressLength = sourceMacLayerAddressLength;
	}

	public int getSourceMac() {
		return sourceMac;
	}

	public void setSourceMac(final int sourceMac) {
		this.sourceMac = sourceMac;
	}

	public int getDestinationMac() {
		return destinationMac;
	}

	public void setDestinationMac(final int destinationMac) {
		this.destinationMac = destinationMac;
	}

	public NetworkLayerMessageType getNetworkLayerMessageType() {
		return networkLayerMessageType;
	}

	public void setNetworkLayerMessageType(final NetworkLayerMessageType networkLayerMessageType) {
		this.networkLayerMessageType = networkLayerMessageType;
	}

}
