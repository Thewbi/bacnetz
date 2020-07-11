package de.bacnetz.stack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.bacnetz.common.Utils;

/**
 * Application Layer Protocol Data Unit (APDU)
 * 
 * 20.1.2.11 Format of the BACnet-Confirmed-Request-PDU (page 617)
 * 
 * The encoding of the payload within a APDU is given in
 * 
 * 20.2 Encoding the Variable Part of BACnet APDUs (page 625)
 * 
 */
public class APDU {

	private static final Logger LOG = LogManager.getLogger(APDU.class);

	private PDUType pduType;

	private boolean segmentation;

	private boolean moreSegmentsFollow;

	private boolean segmentedResponseAccepted;

	private ServiceChoice serviceChoice;

	private final List<ServiceParameter> serviceParameters = new ArrayList<>();

	private int structureLength;

	private Map<Integer, String> vendorMap = new HashMap<>();

	public int getDataLength() {
		throw new RuntimeException("Not implemented exception!");
	}

	public void toBytes(final byte[] data, final int offset) {
		throw new RuntimeException("Not implemented exception!");
	}

	public void fromBytes(final byte[] data, final int startIndex) {

		int offset = 0;
		structureLength = 0;

		// bits 7-4 are the PDU type
		final int pduTypeBits = (data[startIndex + offset] & 0xF0) >> 4;
		pduType = PDUType.fromInt(pduTypeBits);

		// bit 3 is the segmentation bit
		segmentation = 0 < (data[startIndex + offset] & 0x08);
		if (segmentation) {
			// TODO: if there is segmentation, there are special segmentation bytes present
			// in the APDU that have to be parsed.
			throw new RuntimeException("Not implemented yet!");
		}

		// bit 2 is the moreSegmentsFollow bit
		moreSegmentsFollow = 0 < (data[startIndex + offset] & 0x04);
		if (moreSegmentsFollow) {
			throw new RuntimeException("Not implemented yet!");
		}

		// bit 1 is the segmentedResponseAccepted bit
		segmentedResponseAccepted = 0 < (data[startIndex + offset] & 0x02);
		if (segmentedResponseAccepted) {
			throw new RuntimeException("Not implemented yet!");
		}

		offset++;
		structureLength++;

		// service choice
		final int serviceChoiceCode = data[startIndex + offset] & 0xFF;
		serviceChoice = ServiceChoice.fromInt(serviceChoiceCode);

		offset++;
		structureLength++;

		switch (serviceChoice) {

		case WHO_IS:
			structureLength += processWhoIs(startIndex + offset, data);
			break;

		case I_AM:
			structureLength += processIAm(startIndex + offset, data);
			break;

		default:
			throw new RuntimeException("Not implemented: " + serviceChoice);
		}
	}

	/**
	 * ANSI/ASHRAE Standard 135-2012 (page 651)
	 * 
	 * The grammar defines the I-Am Request as a sequence of four Service
	 * Parameters:
	 * 
	 * <pre>
	 * ******************* Unconfirmed Remote Device Management Services ********************
	 *     I-Am-Request ::= SEQUENCE {
	 *     iAmDeviceIdentifier BACnetObjectIdentifier,
	 *     maxAPDULengthAccepted Unsigned,
	 *     segmentationSupported BACnetSegmentation,
	 *     vendorID Unsigned16
	 * }
	 * </pre>
	 * 
	 * @param offset
	 * @param data
	 * @return
	 */
	private int processIAm(int offset, final byte[] data) {

		final int structureLength = 0;
		ServiceParameter serviceParameter = null;

		// iAmDeviceIdentifier
		serviceParameter = new ServiceParameter();
		offset += serviceParameter.fromBytes(data, offset);
		serviceParameters.add(serviceParameter);

		// maxAPDULengthAccepted
		serviceParameter = new ServiceParameter();
		offset += serviceParameter.fromBytes(data, offset);
		serviceParameters.add(serviceParameter);
		LOG.info("maxAPDULengthAccepted: "
				+ Utils.bytesToUnsignedShort(serviceParameter.getPayload()[0], serviceParameter.getPayload()[1], true));

		// segmentationSupported
		serviceParameter = new ServiceParameter();
		offset += serviceParameter.fromBytes(data, offset);
		serviceParameters.add(serviceParameter);

		// vendorID
		serviceParameter = new ServiceParameter();
		offset += serviceParameter.fromBytes(data, offset);
		serviceParameters.add(serviceParameter);

		final int vendorId = (serviceParameter.getPayload()[0] & 0xFF);
		LOG.info("VendorId: " + vendorMap.get(vendorId));

//		if (offset < data.length) {
//
//			// parse optional service parameters
//			boolean parameterParsingDone = false;
//			while (!parameterParsingDone) {
//
//				final ServiceParameter serviceParameter = new ServiceParameter();
//
//				// parse service parameters until the list ends
//				final int bytesProcessed = serviceParameter.fromBytes(data, offset);
//				structureLength += bytesProcessed;
//				if (0 == bytesProcessed) {
//					parameterParsingDone = true;
//					break;
//				}
//
//				serviceParameters.add(serviceParameter);
//
//				offset += bytesProcessed;
//				if ((offset) >= data.length) {
//					parameterParsingDone = true;
//					break;
//				}
//			}
//		}

		return structureLength;
	}

	private int processWhoIs(int offset, final byte[] data) {

		int structureLength = 0;

		if (offset < data.length) {

			// parse optional service parameters
			boolean parameterParsingDone = false;
			while (!parameterParsingDone) {

				final ServiceParameter serviceParameter = new ServiceParameter();

				// parse service parameters until the list ends
				final int bytesProcessed = serviceParameter.fromBytes(data, offset);
				structureLength += bytesProcessed;
				if (0 == bytesProcessed) {
					parameterParsingDone = true;
					break;
				}

				serviceParameters.add(serviceParameter);

				offset += bytesProcessed;
				if ((offset) >= data.length) {
					parameterParsingDone = true;
					break;
				}
			}
		}

		return structureLength;
	}

	@Override
	public String toString() {

		final StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append(pduType.toString()).append(" ").append(serviceChoice);
		for (final ServiceParameter serviceParameter : serviceParameters) {
			stringBuffer.append("\n").append(serviceParameter.toString());
		}

		return stringBuffer.toString();
	}

	public PDUType getPduType() {
		return pduType;
	}

	public void setPduType(final PDUType pduType) {
		this.pduType = pduType;
	}

	public boolean isSegmentation() {
		return segmentation;
	}

	public void setSegmentation(final boolean segmentation) {
		this.segmentation = segmentation;
	}

	public boolean isMoreSegmentsFollow() {
		return moreSegmentsFollow;
	}

	public void setMoreSegmentsFollow(final boolean moreSegmentsFollow) {
		this.moreSegmentsFollow = moreSegmentsFollow;
	}

	public boolean isSegmentedResponseAccepted() {
		return segmentedResponseAccepted;
	}

	public void setSegmentedResponseAccepted(final boolean segmentedResponseAccepted) {
		this.segmentedResponseAccepted = segmentedResponseAccepted;
	}

	public ServiceChoice getServiceChoice() {
		return serviceChoice;
	}

	public void setServiceChoice(final ServiceChoice serviceChoice) {
		this.serviceChoice = serviceChoice;
	}

	public List<ServiceParameter> getServiceParameters() {
		return serviceParameters;
	}

	public int getStructureLength() {
		return structureLength;
	}

	public Map<Integer, String> getVendorMap() {
		return vendorMap;
	}

	public void setVendorMap(final Map<Integer, String> vendorMap) {
		this.vendorMap = vendorMap;
	}

}
