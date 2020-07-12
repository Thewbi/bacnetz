package de.bacnetz.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.bacnetz.common.Utils;
import de.bacnetz.stack.APDU;
import de.bacnetz.stack.NPDU;
import de.bacnetz.stack.ObjectIdentifierServiceParameter;
import de.bacnetz.stack.PDUType;
import de.bacnetz.stack.ServiceChoice;
import de.bacnetz.stack.ServiceParameter;
import de.bacnetz.stack.TagClass;
import de.bacnetz.stack.VirtualLinkControl;

public class DefaultMessageController implements MessageController {

	private static final Logger LOG = LogManager.getLogger(DefaultMessageController.class);

	private Map<Integer, String> vendorMap = new HashMap<>();

	@Override
	public Message processMessage(final Message message) {

		switch (message.getApdu().getServiceChoice()) {
		case I_AM:
			return processIAMMessage(message);

		/** 20.1.3 BACnet-Unconfirmed-Request-PDU */
		case I_HAVE:
			throw new RuntimeException("Not implemented yet! Message: " + message.getApdu().getServiceChoice());

		/** 20.1.4 BACnet-SimpleACK-PDU */
		case UNCONFIRMED_COV_NOTIFICATION:
			throw new RuntimeException("Not implemented yet! Message: " + message.getApdu().getServiceChoice());

		/** 20.1.5 BACnet-ComplexACK-PDU */
		case UNCONFIRMED_EVENT_NOTIFICATION:
			throw new RuntimeException("Not implemented yet! Message: " + message.getApdu().getServiceChoice());

		/** 20.1.6 BACnet-SegmentACK-PDU */
		case UNCONFIRMED_PRIVATE_TRANSFER:
			throw new RuntimeException("Not implemented yet! Message: " + message.getApdu().getServiceChoice());

		/** 20.1.7 BACnet-Error-PDU */
		case UNCONFIRMED_TEXT_MESSAGE:
			throw new RuntimeException("Not implemented yet! Message: " + message.getApdu().getServiceChoice());

		/** 20.1.8 BACnet-Reject-PDU */
		case TIME_SYNCHRONIZATION:
			throw new RuntimeException("Not implemented yet! Message: " + message.getApdu().getServiceChoice());

		/** 20.1.9 BACnet-Abort-PDU */
		case WHO_HAS:
			throw new RuntimeException("Not implemented yet! Message: " + message.getApdu().getServiceChoice());

		/** 20.1.2 BACnet-Confirmed-Request-PDU */
		case WHO_IS:
			throw new RuntimeException("Not implemented yet! Message: " + message.getApdu().getServiceChoice());

		case UTC_TIME_SYNCHRONIZATION:
			throw new RuntimeException("Not implemented yet! Message: " + message.getApdu().getServiceChoice());

		case WRITE_GROUP:
			throw new RuntimeException("Not implemented yet! Message: " + message.getApdu().getServiceChoice());

		case READ_PROPERTY_MULTIPLE:
			return processReadPropertyMultiple(message);

		default:
			throw new RuntimeException("Unknown message: " + message.getApdu().getServiceChoice());
		}
	}

	private Message processReadPropertyMultiple(final Message message) {

		LOG.info("processReadPropertyMultiple()");

		final ServiceParameter serviceParameter = message.getApdu().getServiceParameters().get(1);
		switch (serviceParameter.getPayload()[0]) {

		case APDU.SYSTEM_STATUS:
			LOG.info("System Status: 112");
			return processSystemStatusMessage(message);
		}

		return null;
	}

	private Message processSystemStatusMessage(final Message message) {

		final DefaultMessage defaultMessage = new DefaultMessage(message);

		// TODO: copy message.VirtualLinkControl

		// TODO: copy message.NPDU including all service parameters
		// TODO: change NPDU.control to contain a destination specifier
		defaultMessage.getNpdu().setControl(0x20);
		defaultMessage.getNpdu().setDestinationNetworkNumber(302);
		defaultMessage.getNpdu().setDestinationMACLayerAddressLength(3);
		defaultMessage.getNpdu().setDestinationMac(0x001268);
		// TODO: copy NPDU
		// TODO: add hopCount, set it to 255 0xFF
		defaultMessage.getNpdu().setDestinationHopCount(0xFF);

		// APDU
		defaultMessage.getApdu().setPduType(PDUType.COMPLEX_ACK_PDU);

		// TODO: add new service parameters into the APDU
		// opening bracket
		// system status operational
		// closing bracket
		final ServiceParameter openingTagServiceParameter = new ServiceParameter();
		openingTagServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		openingTagServiceParameter.setTagNumber(0x04);
		openingTagServiceParameter.setLengthValueType(ServiceParameter.OPENING_TAG_CODE);
		defaultMessage.getApdu().getServiceParameters().add(2, openingTagServiceParameter);

//		final ServiceParameter systemStatusTagServiceParameter = new ServiceParameter();
//		systemStatusTagServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
//		systemStatusTagServiceParameter.setTagNumber(0x04);
//		systemStatusTagServiceParameter.setLengthValueType(ServiceParameter.ENUMERATED_CODE);
//		defaultMessage.getApdu().getServiceParameters().add(3, systemStatusTagServiceParameter);

		final ServiceParameter segmentationSupportedServiceParameter = new ServiceParameter();
		segmentationSupportedServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
		segmentationSupportedServiceParameter.setTagNumber(ServiceParameter.ENUMERATED_CODE);
		segmentationSupportedServiceParameter.setLengthValueType(1);
		segmentationSupportedServiceParameter.setPayload(new byte[] { (byte) 0x00 }); // system-status: operational
		defaultMessage.getApdu().getServiceParameters().add(3, segmentationSupportedServiceParameter);

		final ServiceParameter closingTagServiceParameter = new ServiceParameter();
		closingTagServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		closingTagServiceParameter.setTagNumber(0x04);
		closingTagServiceParameter.setLengthValueType(ServiceParameter.CLOSING_TAG_CODE);
		defaultMessage.getApdu().getServiceParameters().add(4, closingTagServiceParameter);

		// TODO: set message.VirtualLinkControl.size to the size of the entire message
		defaultMessage.getVirtualLinkControl().setLength(defaultMessage.getDataLength());

		LOG.info(Utils.byteArrayToStringNoPrefix(defaultMessage.getVirtualLinkControl().getBytes()));
		LOG.info(Utils.byteArrayToStringNoPrefix(defaultMessage.getNpdu().getBytes()));
		LOG.info(Utils.byteArrayToStringNoPrefix(defaultMessage.getApdu().getBytes()));

		LOG.info(Utils.byteArrayToStringNoPrefix(defaultMessage.getBytes()));

		// Expected: 30 98 0e 0c 02 00 27 10 1e 29 70 4e 91 00 4f 1f
		// Actual: 81 0A 00 2E 01 20 00 00 00 FF 10 00 1E 00 00 00 00 00 00 09 70 4E 00
		// 00 00 00 00 00 91 00 4F 00 00 00 00 00 00 00 1F

		return defaultMessage;
	}

	private Message processIAMMessage(final Message message) {

		final VirtualLinkControl virtualLinkControl = new VirtualLinkControl();
		virtualLinkControl.setType(0x81);
		virtualLinkControl.setFunction(0x0B);
		virtualLinkControl.setLength(0x14);

		final NPDU npdu = new NPDU();
		npdu.setVersion(0x01);
		npdu.setControl(0x00);

		final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
		objectIdentifierServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
		objectIdentifierServiceParameter.setTagNumber(ServiceParameter.BACNET_OBJECT_IDENTIFIER);
		objectIdentifierServiceParameter.setLengthValueType(4);
		objectIdentifierServiceParameter.setObjectType(ObjectIdentifierServiceParameter.OBJECT_TYPE_DEVICE);
		objectIdentifierServiceParameter.setInstanceNumber(10001);

		final ServiceParameter maximumAPDUServiceParameter = new ServiceParameter();
		maximumAPDUServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
		maximumAPDUServiceParameter.setTagNumber(ServiceParameter.UNSIGNED_INTEGER_CODE);
		maximumAPDUServiceParameter.setLengthValueType(2);
		maximumAPDUServiceParameter.setPayload(new byte[] { (byte) 0x01, (byte) 0xE0 }); // 0x01E0 = 480

		final ServiceParameter segmentationSupportedServiceParameter = new ServiceParameter();
		segmentationSupportedServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
		segmentationSupportedServiceParameter.setTagNumber(ServiceParameter.ENUMERATED_CODE);
		segmentationSupportedServiceParameter.setLengthValueType(1);
		segmentationSupportedServiceParameter.setPayload(new byte[] { (byte) 0x00 }); // segmented-both

		final ServiceParameter vendorIdServiceParameter = new ServiceParameter();
		vendorIdServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
		vendorIdServiceParameter.setTagNumber(ServiceParameter.UNSIGNED_INTEGER_CODE);
		vendorIdServiceParameter.setLengthValueType(1);
		vendorIdServiceParameter.setPayload(new byte[] { (byte) 0xB2 }); // 0xB2 = 178d = loytec

		final APDU apdu = new APDU();
//		apdu.setMoreSegmentsFollow(moreSegmentsFollow);
		apdu.setPduType(PDUType.UNCONFIRMED_SERVICE_REQUEST_PDU);
//		apdu.setSegmentation(segmentation);
//		apdu.setSegmentedResponseAccepted(segmentedResponseAccepted);
		apdu.setServiceChoice(ServiceChoice.I_AM);
		apdu.setVendorMap(vendorMap);
		apdu.getServiceParameters().add(objectIdentifierServiceParameter);
		apdu.getServiceParameters().add(maximumAPDUServiceParameter);
		apdu.getServiceParameters().add(segmentationSupportedServiceParameter);
		apdu.getServiceParameters().add(vendorIdServiceParameter);

		final DefaultMessage result = new DefaultMessage();
		result.setVirtualLinkControl(virtualLinkControl);
		result.setNpdu(npdu);
		result.setApdu(apdu);

		return result;
	}

	public Map<Integer, String> getVendorMap() {
		return vendorMap;
	}

	public void setVendorMap(final Map<Integer, String> vendorMap) {
		this.vendorMap = vendorMap;
	}

}
