package de.bacnetz.controller;

import java.util.HashMap;
import java.util.Map;

import de.bacnetz.stack.APDU;
import de.bacnetz.stack.NPDU;
import de.bacnetz.stack.ObjectIdentifierServiceParameter;
import de.bacnetz.stack.PDUType;
import de.bacnetz.stack.ServiceChoice;
import de.bacnetz.stack.ServiceParameter;
import de.bacnetz.stack.VirtualLinkControl;

public class DefaultMessageController implements MessageController {

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

		default:
			throw new RuntimeException("Unknown message: " + message.getApdu().getServiceChoice());
		}
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
		objectIdentifierServiceParameter.setClassValue(ServiceParameter.APPLICATION_TAG);
		objectIdentifierServiceParameter.setTagNumber(ServiceParameter.BACNET_OBJECT_IDENTIFIER);
		objectIdentifierServiceParameter.setLengthValueType(4);
		objectIdentifierServiceParameter.setObjectType(ObjectIdentifierServiceParameter.OBJECT_TYPE_DEVICE);
		objectIdentifierServiceParameter.setInstanceNumber(10001);

		final ServiceParameter maximumAPDUServiceParameter = new ServiceParameter();
		maximumAPDUServiceParameter.setClassValue(ServiceParameter.APPLICATION_TAG);
		maximumAPDUServiceParameter.setTagNumber(ServiceParameter.UNSIGNED_INTEGER);
		maximumAPDUServiceParameter.setLengthValueType(2);
		maximumAPDUServiceParameter.setPayload(new byte[] { (byte) 0x01, (byte) 0xE0 }); // 0x01E0 = 480

		final ServiceParameter segmentationSupportedServiceParameter = new ServiceParameter();
		segmentationSupportedServiceParameter.setClassValue(ServiceParameter.APPLICATION_TAG);
		segmentationSupportedServiceParameter.setTagNumber(ServiceParameter.ENUMERATED);
		segmentationSupportedServiceParameter.setLengthValueType(1);
		segmentationSupportedServiceParameter.setPayload(new byte[] { (byte) 0x00 }); // segmented-both

		final ServiceParameter vendorIdServiceParameter = new ServiceParameter();
		vendorIdServiceParameter.setClassValue(ServiceParameter.APPLICATION_TAG);
		vendorIdServiceParameter.setTagNumber(ServiceParameter.UNSIGNED_INTEGER);
		vendorIdServiceParameter.setLengthValueType(1);
		vendorIdServiceParameter.setPayload(new byte[] { (byte) 0xB2 }); // 178 = loytec

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

		return new DefaultMessage(virtualLinkControl, npdu, apdu);
	}

	public Map<Integer, String> getVendorMap() {
		return vendorMap;
	}

	public void setVendorMap(final Map<Integer, String> vendorMap) {
		this.vendorMap = vendorMap;
	}

}
