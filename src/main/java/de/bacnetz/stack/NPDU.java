package de.bacnetz.stack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.bacnetz.common.Utils;

/**
 * Network Layer Protocol Data Unit
 * 
 * Structure Definition in 6.2 Network Layer PDU Structure in ANSI/ASHRAE
 * Standard 135-2012.
 */
public class NPDU {

	private static final Logger LOG = LogManager.getLogger(NPDU.class);

	/** eight bit unsigned version number */
	private int version;

	private int control;

	private int destinationNetworkNumber;

	/** 0 indicates Broadcast on Destination Network */
	private int destinationMACLayerAddressLength;

	private int destinationHopCount;

	private int structureLength;

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
			destinationMACLayerAddressLength = data[startIndex + offset++] & 0xFF;
			destinationHopCount = data[startIndex + offset++] & 0xFF;
			structureLength += 4;
		} else {
//			throw new RuntimeException("Not implemented yet!");
			LOG.info("No destination network information is present!");
		}

		if (isSourceSpecifierPresent()) {
			throw new RuntimeException("Not implemented yet!");
		}

		if (!isAPDUMessage()) {
			throw new RuntimeException("Not implemented yet!");
		}

		if (isConfirmedRequestPDUPresent()) {
			throw new RuntimeException("Not implemented yet!");
		}
	}

	public int getDataLength() {

		// two byte minimum for version and control
		int dataLength = 2;

		// destination specifier has four byte
		if (isDestinationSpecifierPresent()) {
			dataLength += 4;
		}

		return dataLength;
	}

	public void toBytes(final byte[] data, final int offset) {

		int index = 0;

		data[offset + index++] = (byte) version;
		data[offset + index++] = (byte) control;

		if (isDestinationSpecifierPresent()) {
			Utils.addShortToBuffer(data, offset + index, (short) destinationNetworkNumber);
			index += 2;
			data[offset + index++] = (byte) destinationMACLayerAddressLength;
			data[offset + index++] = (byte) destinationHopCount;
		}
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
		// 0x80 => 1000 0000
		return 0 == (control & 0x80);
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

}
