package de.bacnetz.controller;

import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.bacnetz.common.Utils;
import de.bacnetz.stack.APDU;
import de.bacnetz.stack.BACnetServicesSupportedBitString;
import de.bacnetz.stack.NPDU;
import de.bacnetz.stack.ObjectIdentifierServiceParameter;
import de.bacnetz.stack.PDUType;
import de.bacnetz.stack.ServiceChoice;
import de.bacnetz.stack.ServiceParameter;
import de.bacnetz.stack.TagClass;
import de.bacnetz.stack.VirtualLinkControl;

public class DefaultMessageController implements MessageController {

	private static final int DEVICE_INSTANCE_NUMBER = 10001;

	private static final Logger LOG = LogManager.getLogger(DefaultMessageController.class);

	private Map<Integer, String> vendorMap = new HashMap<>();

	@Override
	public Message processMessage(final Message message) {
		if (message.getApdu() == null) {
			return processNonAPDUMessage(message);
		} else {
			return processAPDUMessage(message);
		}
	}

	private Message processNonAPDUMessage(final Message message) {
		switch (message.getNpdu().getNetworkLayerMessageType()) {

		case WHO_IS_ROUTER_TO_NETWORK:
			return null;

		case I_AM_ROUTER_TO_NETWORK:
			return null;

		case WHAT_IS_NETWORK_NUMBER:
			return null;

		default:
			LOG.warn("Unknown message: " + message.getApdu().getServiceChoice());
			return null;
		}
	}

	private Message processAPDUMessage(final Message message) {
		switch (message.getApdu().getServiceChoice()) {
		case I_AM:
			LOG.info("I_AM received!");
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
			LOG.trace("WHO_IS received!");
			return processWhoIsMessage(message);

		case UTC_TIME_SYNCHRONIZATION:
			throw new RuntimeException("Not implemented yet! Message: " + message.getApdu().getServiceChoice());

		case WRITE_GROUP:
			throw new RuntimeException("Not implemented yet! Message: " + message.getApdu().getServiceChoice());

		case READ_PROPERTY:
			LOG.trace("READ_PROPERTY received!");
			return processReadProperty(message);

		case READ_PROPERTY_MULTIPLE:
			LOG.info("READ_PROPERTY_MULTIPLE received!");
			return processReadPropertyMultiple(message);

		default:
//			throw new RuntimeException("Unknown message: " + message.getApdu().getServiceChoice());
			LOG.warn("Unknown message: " + message.getApdu().getServiceChoice());
			return null;
		}
	}

	/**
	 * Answer Who-Is with I-Am.
	 * 
	 * @param message
	 * @return
	 */
	private Message processWhoIsMessage(final Message message) {

		final List<ServiceParameter> serviceParameters = message.getApdu().getServiceParameters();
		if (CollectionUtils.isNotEmpty(serviceParameters)) {

			final ServiceParameter lowerBoundServiceParameter = serviceParameters.get(0);
			final ServiceParameter upperBoundServiceParameter = serviceParameters.get(1);

			final boolean bigEndian = true;
			int lowerBound = 0;
			if (lowerBoundServiceParameter.getPayload().length == 1) {
				lowerBound = lowerBoundServiceParameter.getPayload()[0];
			} else {
				lowerBound = Utils.bytesToUnsignedShort(lowerBoundServiceParameter.getPayload()[0],
						lowerBoundServiceParameter.getPayload()[1], bigEndian);
			}

			int upperBound = 0;
			if (upperBoundServiceParameter.getPayload().length == 1) {
				upperBound = upperBoundServiceParameter.getPayload()[0];
			} else {
				upperBound = Utils.bytesToUnsignedShort(upperBoundServiceParameter.getPayload()[0],
						upperBoundServiceParameter.getPayload()[1], bigEndian);
			}

			LOG.trace("Who-Is lower-bound: {} ({})",
					Utils.byteArrayToStringNoPrefix(lowerBoundServiceParameter.getPayload()), lowerBound);
			LOG.trace("Who-Is upper-bound: {} ({})",
					Utils.byteArrayToStringNoPrefix(upperBoundServiceParameter.getPayload()), upperBound);

			// do not process message if it is bounded and the device's id is out of bounds!
			if ((lowerBound > DEVICE_INSTANCE_NUMBER) || (DEVICE_INSTANCE_NUMBER > upperBound)) {

				LOG.trace("Ignoring Who-Is! DeviceID: {}, [{} - {}]", DEVICE_INSTANCE_NUMBER, lowerBound, upperBound);
				return null;
			}
		}

		LOG.trace("WHO_IS received!");

		// return Unconfirmed request i-Am device,10001
		final int deviceInstanceNumber = DEVICE_INSTANCE_NUMBER;

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
		objectIdentifierServiceParameter.setInstanceNumber(deviceInstanceNumber);

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

	private Message processReadProperty(final Message requestMessage) {

		LOG.trace("processReadProperty()");

		final int propertyIdentifier = requestMessage.getApdu().getPropertyIdentifier();
		switch (propertyIdentifier) {

		// Supported Services Property
		// 0x1961 = 6497d
		case 0x1961:
			LOG.info("Supported Services Property");
			return processSupportedServicesProperty(0x61, requestMessage);

		// Segmentation supported
		// 0x196B = 6507d
		case 0x196B:
			LOG.info("Segmentation supported");
			return processSegmentationSupportedProperty(0x6B, requestMessage);

		// max-apdu-length-accepted
		// 0x193E = 6462d
		case 0x193E:
			LOG.info("max-apdu-length-accepted");
			return processMaxAPDULengthAcceptedProperty(0x3E, requestMessage);

		// max-segments-accepted
		// 0x19A7 = 6567d
		case 0x19A7:
			LOG.info("max-segments-accepted");
			return processMaxSegmentsAcceptedProperty(0xA7, requestMessage);

		// 0x190A = 6410d APDU-Segment-Timeout
		case 0x190A:
			LOG.info("APDU-Segment-Timeout");
			return processAPDUSegmentTimeoutProperty(0x0A, requestMessage);

		// 0x190B = 6411d APDU-Timeout
		case 0x190B:
			LOG.info("APDU-Timeout");
			return processAPDUTimeoutProperty(0x0B, requestMessage);

		// 0x199B = 6555d database-revision (155d = 0x9B) defined in ASHRAE on page 696
		case 0x199B:
			LOG.info("database-revision");
			return processDatabaseRevisionProperty(0x9B, requestMessage);

		// 0x198B = 6539d protocol-revision
		case 0x198B:
			LOG.info("protocol-revision");
			return processProtocolRevisionProperty(0x8B, requestMessage);

		// 0x1962 = 6498d protocol-version
		case 0x1962:
			LOG.info("protocol-version");
			return processProtocolVersionProperty(0x62, requestMessage);

		// 0x19C4 = 6596 (0xC4 = 196d) last-restart-reason
		case 0x19C4:
			LOG.info("last-restart-reason");
			return processLastRestartReasonProperty(0xC4, requestMessage);

		// 0x194c = 6476d (0x4c = 76) object list
		case 0x194c:
			LOG.info("object list");
			return processObjectListProperty(0x4c, requestMessage);

		default:
			throw new NotImplementedException("Unknown property! PropertyIdentifier = " + propertyIdentifier);
		}
	}

	private Message processObjectListProperty(final int propertyKey, final Message requestMessage) {
		// 0x02 = ????
		return returnIntegerProperty(requestMessage.getApdu().getInvokeId(), propertyKey, new byte[] { (byte) 0x02 });
	}

	private Message processLastRestartReasonProperty(final int propertyKey, final Message requestMessage) {
		// coldstart 1
		return returnIntegerProperty(requestMessage.getApdu().getInvokeId(), propertyKey, new byte[] { (byte) 0x01 });
	}

	private Message processProtocolVersionProperty(final int propertyKey, final Message requestMessage) {
		// protocol version 1
		return returnIntegerProperty(requestMessage.getApdu().getInvokeId(), propertyKey, new byte[] { (byte) 0x01 });
	}

	private Message processProtocolRevisionProperty(final int propertyKey, final Message requestMessage) {
		// database revision 12d = 0x0C
		return returnIntegerProperty(requestMessage.getApdu().getInvokeId(), propertyKey, new byte[] { (byte) 0x0C });
	}

	private Message processDatabaseRevisionProperty(final int propertyKey, final Message requestMessage) {
		// database revivion 3
		return returnIntegerProperty(requestMessage.getApdu().getInvokeId(), propertyKey, new byte[] { (byte) 0x03 });
	}

	private Message processAPDUSegmentTimeoutProperty(final int propertyKey, final Message requestMessage) {

		// APDU Segment-Timeout:
		// Dieser Wert in Millisekunden legt fest, nach welcher Zeitspanne ein
		// quittierpflichtiges, segmentiertes Telegramm als fehlgeschlagen gewertet
		// wird, wenn die Segmentbestätigung ausbleibt. Der Standardwert beträgt
		// 2000 Millisekunden.
		// 2000d == 0x07D0
		return returnIntegerProperty(requestMessage.getApdu().getInvokeId(), propertyKey,
				new byte[] { (byte) 0x07, (byte) 0xD0 });
//		final int deviceInstanceNumber = DEVICE_INSTANCE_NUMBER;
//
//		final VirtualLinkControl virtualLinkControl = new VirtualLinkControl();
//		virtualLinkControl.setType(0x81);
//		virtualLinkControl.setFunction(0x0A);
//		virtualLinkControl.setLength(0x00);
//
//		final NPDU npdu = new NPDU();
//		npdu.setVersion(0x01);
//		npdu.setControl(0x00);
////		npdu.setSourceNetworkAddress(requestMessage.getNpdu().getDestinationNetworkNumber());
////		npdu.setDestinationMACLayerAddressLength(requestMessage.getNpdu().getDestinationMACLayerAddressLength());
////		npdu.setDestinationMac(requestMessage.getNpdu().getDestinationMac());
//
//		final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
//		objectIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
//		// who are context tag numbers determined???
////		objectIdentifierServiceParameter.setTagNumber(ServiceParameter.BACNET_OBJECT_IDENTIFIER);
//		objectIdentifierServiceParameter.setTagNumber(0x00);
//		objectIdentifierServiceParameter.setLengthValueType(4);
//		objectIdentifierServiceParameter.setObjectType(ObjectIdentifierServiceParameter.OBJECT_TYPE_DEVICE);
//		objectIdentifierServiceParameter.setInstanceNumber(deviceInstanceNumber);
//
//		final ServiceParameter propertyIdentifierServiceParameter = new ServiceParameter();
//		propertyIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
//		// who are context tag numbers determined???
////		protocolServicesSupportedServiceParameter.setTagNumber(ServiceParameter.UNKOWN_TAG_NUMBER);
//		propertyIdentifierServiceParameter.setTagNumber(0x01);
//		propertyIdentifierServiceParameter.setLengthValueType(1);
//		// 0x61 = 97d = Property Identifier: protocol-services-supported
//		// 0x6B = 107d = Property Identifier: segmentation-supported
//		// 0x3E = 62d = Property Identifier: max-apdu-length-accepted
//		propertyIdentifierServiceParameter.setPayload(new byte[] { (byte) 0x3E });
//
//		final ServiceParameter openingTagServiceParameter = new ServiceParameter();
//		openingTagServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
//		openingTagServiceParameter.setTagNumber(0x03);
//		openingTagServiceParameter.setLengthValueType(ServiceParameter.OPENING_TAG_CODE);
//
//		final ServiceParameter valueServiceParameter = new ServiceParameter();
//		valueServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
//		valueServiceParameter.setTagNumber(ServiceParameter.UNSIGNED_INTEGER_CODE);
//		valueServiceParameter.setLengthValueType(0x02);
//		// APDU Segment-Timeout:
//		// Dieser Wert in Millisekunden legt fest, nach welcher Zeitspanne ein
//		// quittierpflichtiges, segmentiertes Telegramm als fehlgeschlagen gewertet
//		// wird, wenn die Segmentbestätigung ausbleibt. Der Standardwert beträgt
//		// 2000 Millisekunden.
//		// 2000d == 0x07D0
//		valueServiceParameter.setPayload(new byte[] { (byte) 0x07, (byte) 0xD0 });
//
//		final ServiceParameter closingTagServiceParameter = new ServiceParameter();
//		closingTagServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
//		closingTagServiceParameter.setTagNumber(0x03);
//		closingTagServiceParameter.setLengthValueType(ServiceParameter.CLOSING_TAG_CODE);
//
//		final APDU apdu = new APDU();
//		apdu.setPduType(PDUType.COMPLEX_ACK_PDU);
//		apdu.setInvokeId(requestMessage.getApdu().getInvokeId());
//		apdu.setServiceChoice(ServiceChoice.READ_PROPERTY);
//		apdu.setVendorMap(vendorMap);
////		apdu.setObjectIdentifierServiceParameter(objectIdentifierServiceParameter);
//		apdu.getServiceParameters().add(objectIdentifierServiceParameter);
//		apdu.getServiceParameters().add(propertyIdentifierServiceParameter);
//		apdu.getServiceParameters().add(openingTagServiceParameter);
//		apdu.getServiceParameters().add(valueServiceParameter);
//		apdu.getServiceParameters().add(closingTagServiceParameter);
//
//		final DefaultMessage result = new DefaultMessage();
//		result.setVirtualLinkControl(virtualLinkControl);
//		result.setNpdu(npdu);
//		result.setApdu(apdu);
//
//		virtualLinkControl.setLength(result.getDataLength());
//
//		final byte[] bytes = result.getBytes();
//		LOG.info(Utils.byteArrayToStringNoPrefix(bytes));
//
//		return result;
	}

	private Message processMaxSegmentsAcceptedProperty(final int propertyKey, final Message requestMessage) {

		// APDU Max Segments Accepted:
		// Legt fest, wie viele Segmente maximal akzeptiert werden.
		return returnIntegerProperty(requestMessage.getApdu().getInvokeId(), propertyKey, new byte[] { (byte) 0x01 });

//		final int deviceInstanceNumber = DEVICE_INSTANCE_NUMBER;
//
//		final VirtualLinkControl virtualLinkControl = new VirtualLinkControl();
//		virtualLinkControl.setType(0x81);
//		virtualLinkControl.setFunction(0x0A);
//		virtualLinkControl.setLength(0x00);
//
//		final NPDU npdu = new NPDU();
//		npdu.setVersion(0x01);
//		npdu.setControl(0x00);
////		npdu.setSourceNetworkAddress(requestMessage.getNpdu().getDestinationNetworkNumber());
////		npdu.setDestinationMACLayerAddressLength(requestMessage.getNpdu().getDestinationMACLayerAddressLength());
////		npdu.setDestinationMac(requestMessage.getNpdu().getDestinationMac());
//
//		final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
//		objectIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
//		// who are context tag numbers determined???
////		objectIdentifierServiceParameter.setTagNumber(ServiceParameter.BACNET_OBJECT_IDENTIFIER);
//		objectIdentifierServiceParameter.setTagNumber(0x00);
//		objectIdentifierServiceParameter.setLengthValueType(4);
//		objectIdentifierServiceParameter.setObjectType(ObjectIdentifierServiceParameter.OBJECT_TYPE_DEVICE);
//		objectIdentifierServiceParameter.setInstanceNumber(deviceInstanceNumber);
//
//		final ServiceParameter propertyIdentifierServiceParameter = new ServiceParameter();
//		propertyIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
//		// who are context tag numbers determined???
////		protocolServicesSupportedServiceParameter.setTagNumber(ServiceParameter.UNKOWN_TAG_NUMBER);
//		propertyIdentifierServiceParameter.setTagNumber(0x01);
//		propertyIdentifierServiceParameter.setLengthValueType(1);
//		// 0x61 = 97d = Property Identifier: protocol-services-supported
//		// 0x6B = 107d = Property Identifier: segmentation-supported
//		// 0x3E = 62d = Property Identifier: max-apdu-length-accepted
//		propertyIdentifierServiceParameter.setPayload(new byte[] { (byte) 0x3E });
//
//		final ServiceParameter openingTagServiceParameter = new ServiceParameter();
//		openingTagServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
//		openingTagServiceParameter.setTagNumber(0x03);
//		openingTagServiceParameter.setLengthValueType(ServiceParameter.OPENING_TAG_CODE);
//
//		final ServiceParameter valueServiceParameter = new ServiceParameter();
//		valueServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
//		valueServiceParameter.setTagNumber(ServiceParameter.UNSIGNED_INTEGER_CODE);
//		valueServiceParameter.setLengthValueType(0x01);
//		// APDU Max Segments Accepted:
//		// Legt fest, wie viele Segmente maximal akzeptiert werden.
//		valueServiceParameter.setPayload(new byte[] { (byte) 0x01 });
//
//		final ServiceParameter closingTagServiceParameter = new ServiceParameter();
//		closingTagServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
//		closingTagServiceParameter.setTagNumber(0x03);
//		closingTagServiceParameter.setLengthValueType(ServiceParameter.CLOSING_TAG_CODE);
//
//		final APDU apdu = new APDU();
//		apdu.setPduType(PDUType.COMPLEX_ACK_PDU);
//		apdu.setInvokeId(requestMessage.getApdu().getInvokeId());
//		apdu.setServiceChoice(ServiceChoice.READ_PROPERTY);
//		apdu.setVendorMap(vendorMap);
////		apdu.setObjectIdentifierServiceParameter(objectIdentifierServiceParameter);
//		apdu.getServiceParameters().add(objectIdentifierServiceParameter);
//		apdu.getServiceParameters().add(propertyIdentifierServiceParameter);
//		apdu.getServiceParameters().add(openingTagServiceParameter);
//		apdu.getServiceParameters().add(valueServiceParameter);
//		apdu.getServiceParameters().add(closingTagServiceParameter);
//
//		final DefaultMessage result = new DefaultMessage();
//		result.setVirtualLinkControl(virtualLinkControl);
//		result.setNpdu(npdu);
//		result.setApdu(apdu);
//
//		virtualLinkControl.setLength(result.getDataLength());
//
//		final byte[] bytes = result.getBytes();
//		LOG.info(Utils.byteArrayToStringNoPrefix(bytes));
//
//		return result;
	}

	private Message processAPDUTimeoutProperty(final int propertyKey, final Message requestMessage) {

		// ADPU Timeout:
		// Dieser Wert in Millisekunden legt fest, nach welcher Zeitspanne ein
		// quittierpflichtiges Telegramm als fehlgeschlagen gewertet wird, wenn die
		// Bestätigung ausbleibt. Der Standardwert beträgt 3000 ms.
		// 3000d == 0x0BB8
		return returnIntegerProperty(requestMessage.getApdu().getInvokeId(), propertyKey,
				new byte[] { (byte) 0x0B, (byte) 0xB8 });

//		final int deviceInstanceNumber = DEVICE_INSTANCE_NUMBER;
//
//		final VirtualLinkControl virtualLinkControl = new VirtualLinkControl();
//		virtualLinkControl.setType(0x81);
//		virtualLinkControl.setFunction(0x0A);
//		virtualLinkControl.setLength(0x00);
//
//		final NPDU npdu = new NPDU();
//		npdu.setVersion(0x01);
//		npdu.setControl(0x00);
////		npdu.setSourceNetworkAddress(requestMessage.getNpdu().getDestinationNetworkNumber());
////		npdu.setDestinationMACLayerAddressLength(requestMessage.getNpdu().getDestinationMACLayerAddressLength());
////		npdu.setDestinationMac(requestMessage.getNpdu().getDestinationMac());
//
//		final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
//		objectIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
//		// who are context tag numbers determined???
////		objectIdentifierServiceParameter.setTagNumber(ServiceParameter.BACNET_OBJECT_IDENTIFIER);
//		objectIdentifierServiceParameter.setTagNumber(0x00);
//		objectIdentifierServiceParameter.setLengthValueType(4);
//		objectIdentifierServiceParameter.setObjectType(ObjectIdentifierServiceParameter.OBJECT_TYPE_DEVICE);
//		objectIdentifierServiceParameter.setInstanceNumber(deviceInstanceNumber);
//
//		final ServiceParameter propertyIdentifierServiceParameter = new ServiceParameter();
//		propertyIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
//		// who are context tag numbers determined???
////		protocolServicesSupportedServiceParameter.setTagNumber(ServiceParameter.UNKOWN_TAG_NUMBER);
//		propertyIdentifierServiceParameter.setTagNumber(0x01);
//		propertyIdentifierServiceParameter.setLengthValueType(1);
//		// 0x61 = 97d = Property Identifier: protocol-services-supported
//		// 0x6B = 107d = Property Identifier: segmentation-supported
//		// 0x3E = 62d = Property Identifier: max-apdu-length-accepted
//		propertyIdentifierServiceParameter.setPayload(new byte[] { (byte) 0x3E });
//
//		final ServiceParameter openingTagServiceParameter = new ServiceParameter();
//		openingTagServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
//		openingTagServiceParameter.setTagNumber(0x03);
//		openingTagServiceParameter.setLengthValueType(ServiceParameter.OPENING_TAG_CODE);
//
//		final ServiceParameter valueServiceParameter = new ServiceParameter();
//		valueServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
//		valueServiceParameter.setTagNumber(ServiceParameter.UNSIGNED_INTEGER_CODE);
//		valueServiceParameter.setLengthValueType(0x02);
//		// ADPU Timeout:
//		// Dieser Wert in Millisekunden legt fest, nach welcher Zeitspanne ein
//		// quittierpflichtiges Telegramm als fehlgeschlagen gewertet wird, wenn die
//		// Bestätigung ausbleibt. Der Standardwert beträgt 3000 ms.
//		// 3000d == 0x0BB8
//		valueServiceParameter.setPayload(new byte[] { (byte) 0x0B, (byte) 0xB8 });
//
//		final ServiceParameter closingTagServiceParameter = new ServiceParameter();
//		closingTagServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
//		closingTagServiceParameter.setTagNumber(0x03);
//		closingTagServiceParameter.setLengthValueType(ServiceParameter.CLOSING_TAG_CODE);
//
//		final APDU apdu = new APDU();
//		apdu.setPduType(PDUType.COMPLEX_ACK_PDU);
//		apdu.setInvokeId(requestMessage.getApdu().getInvokeId());
//		apdu.setServiceChoice(ServiceChoice.READ_PROPERTY);
//		apdu.setVendorMap(vendorMap);
////		apdu.setObjectIdentifierServiceParameter(objectIdentifierServiceParameter);
//		apdu.getServiceParameters().add(objectIdentifierServiceParameter);
//		apdu.getServiceParameters().add(propertyIdentifierServiceParameter);
//		apdu.getServiceParameters().add(openingTagServiceParameter);
//		apdu.getServiceParameters().add(valueServiceParameter);
//		apdu.getServiceParameters().add(closingTagServiceParameter);
//
//		final DefaultMessage result = new DefaultMessage();
//		result.setVirtualLinkControl(virtualLinkControl);
//		result.setNpdu(npdu);
//		result.setApdu(apdu);
//
//		virtualLinkControl.setLength(result.getDataLength());
//
//		final byte[] bytes = result.getBytes();
//		LOG.info(Utils.byteArrayToStringNoPrefix(bytes));
//
//		return result;
	}

	private Message processMaxAPDULengthAcceptedProperty(final int propertyKey, final Message requestMessage) {

		// Maximum APDU Length is dependent on the physical layer used, for example the
		// maximum APDU size for BACnet/IP is 1497 octets, but for BACnet MS/TP
		// segments, the maximum APDU size is only 480 octets.
		//
		// 1497d = 0x05D9
		// 62d = 0x3E
		return returnIntegerProperty(requestMessage.getApdu().getInvokeId(), propertyKey, new byte[] { (byte) 0x05D9 });
//		return returnIntegerProperty(requestMessage.getApdu().getInvokeId(), propertyKey, new byte[] { (byte) 0x3E });

//		final int deviceInstanceNumber = DEVICE_INSTANCE_NUMBER;
//
//		final VirtualLinkControl virtualLinkControl = new VirtualLinkControl();
//		virtualLinkControl.setType(0x81);
//		virtualLinkControl.setFunction(0x0A);
//		virtualLinkControl.setLength(0x00);
//
//		final NPDU npdu = new NPDU();
//		npdu.setVersion(0x01);
//		npdu.setControl(0x00);
////		npdu.setSourceNetworkAddress(requestMessage.getNpdu().getDestinationNetworkNumber());
////		npdu.setDestinationMACLayerAddressLength(requestMessage.getNpdu().getDestinationMACLayerAddressLength());
////		npdu.setDestinationMac(requestMessage.getNpdu().getDestinationMac());
//
//		final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
//		objectIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
//		// who are context tag numbers determined???
////		objectIdentifierServiceParameter.setTagNumber(ServiceParameter.BACNET_OBJECT_IDENTIFIER);
//		objectIdentifierServiceParameter.setTagNumber(0x00);
//		objectIdentifierServiceParameter.setLengthValueType(4);
//		objectIdentifierServiceParameter.setObjectType(ObjectIdentifierServiceParameter.OBJECT_TYPE_DEVICE);
//		objectIdentifierServiceParameter.setInstanceNumber(deviceInstanceNumber);
//
//		final ServiceParameter propertyIdentifierServiceParameter = new ServiceParameter();
//		propertyIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
//		// who are context tag numbers determined???
////		protocolServicesSupportedServiceParameter.setTagNumber(ServiceParameter.UNKOWN_TAG_NUMBER);
//		propertyIdentifierServiceParameter.setTagNumber(0x01);
//		propertyIdentifierServiceParameter.setLengthValueType(1);
//		// 0x61 = 97d = Property Identifier: protocol-services-supported
//		// 0x6B = 107d = Property Identifier: segmentation-supported
//		// 0x3E = 62d = Property Identifier: max-apdu-length-accepted
//		propertyIdentifierServiceParameter.setPayload(new byte[] { (byte) 0x3E });
//
//		final ServiceParameter openingTagServiceParameter = new ServiceParameter();
//		openingTagServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
//		openingTagServiceParameter.setTagNumber(0x03);
//		openingTagServiceParameter.setLengthValueType(ServiceParameter.OPENING_TAG_CODE);
//
//		final ServiceParameter valueServiceParameter = new ServiceParameter();
//		valueServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
//		valueServiceParameter.setTagNumber(ServiceParameter.UNSIGNED_INTEGER_CODE);
//		valueServiceParameter.setLengthValueType(0x02);
//		// Maximum APDU Length is dependent on the physical layer used, for example the
//		// maximum APDU size for BACnet/IP is 1497 octets, but for BACnet MS/TP
//		// segments, the maximum APDU size is only 480 octets.
//		//
//		// 1497d = 0x05D9
//		valueServiceParameter.setPayload(new byte[] { (byte) 0x05, (byte) 0xD9 });
//
//		final ServiceParameter closingTagServiceParameter = new ServiceParameter();
//		closingTagServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
//		closingTagServiceParameter.setTagNumber(0x03);
//		closingTagServiceParameter.setLengthValueType(ServiceParameter.CLOSING_TAG_CODE);
//
//		final APDU apdu = new APDU();
//		apdu.setPduType(PDUType.COMPLEX_ACK_PDU);
//		apdu.setInvokeId(requestMessage.getApdu().getInvokeId());
//		apdu.setServiceChoice(ServiceChoice.READ_PROPERTY);
//		apdu.setVendorMap(vendorMap);
////		apdu.setObjectIdentifierServiceParameter(objectIdentifierServiceParameter);
//		apdu.getServiceParameters().add(objectIdentifierServiceParameter);
//		apdu.getServiceParameters().add(propertyIdentifierServiceParameter);
//		apdu.getServiceParameters().add(openingTagServiceParameter);
//		apdu.getServiceParameters().add(valueServiceParameter);
//		apdu.getServiceParameters().add(closingTagServiceParameter);
//
//		final DefaultMessage result = new DefaultMessage();
//		result.setVirtualLinkControl(virtualLinkControl);
//		result.setNpdu(npdu);
//		result.setApdu(apdu);
//
//		virtualLinkControl.setLength(result.getDataLength());
//
//		final byte[] bytes = result.getBytes();
//		LOG.info(Utils.byteArrayToStringNoPrefix(bytes));
//
//		return result;
	}

	private Message processSegmentationSupportedProperty(final int propertyKey, final Message requestMessage) {

		// segmented-both (0)
		// segmented-transmit (1)
		// segmented-receive (2)
		// no-segmentation (3)
		return returnIntegerProperty(requestMessage.getApdu().getInvokeId(), propertyKey, new byte[] { (byte) 0x00 });

//		final int deviceInstanceNumber = DEVICE_INSTANCE_NUMBER;
//
//		final VirtualLinkControl virtualLinkControl = new VirtualLinkControl();
//		virtualLinkControl.setType(0x81);
//		virtualLinkControl.setFunction(0x0A);
//		virtualLinkControl.setLength(0x00);
//
//		final NPDU npdu = new NPDU();
//		npdu.setVersion(0x01);
//		npdu.setControl(0x00);
////		npdu.setSourceNetworkAddress(requestMessage.getNpdu().getDestinationNetworkNumber());
////		npdu.setDestinationMACLayerAddressLength(requestMessage.getNpdu().getDestinationMACLayerAddressLength());
////		npdu.setDestinationMac(requestMessage.getNpdu().getDestinationMac());
//
//		final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
//		objectIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
//		// who are context tag numbers determined???
////		objectIdentifierServiceParameter.setTagNumber(ServiceParameter.BACNET_OBJECT_IDENTIFIER);
//		objectIdentifierServiceParameter.setTagNumber(0x00);
//		objectIdentifierServiceParameter.setLengthValueType(4);
//		objectIdentifierServiceParameter.setObjectType(ObjectIdentifierServiceParameter.OBJECT_TYPE_DEVICE);
//		objectIdentifierServiceParameter.setInstanceNumber(deviceInstanceNumber);
//
//		final ServiceParameter propertyIdentifierServiceParameter = new ServiceParameter();
//		propertyIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
//		// who are context tag numbers determined???
////		protocolServicesSupportedServiceParameter.setTagNumber(ServiceParameter.UNKOWN_TAG_NUMBER);
//		propertyIdentifierServiceParameter.setTagNumber(0x01);
//		propertyIdentifierServiceParameter.setLengthValueType(1);
//		// 0x61 = 97d = Protocol Identifier: protocol-services-supported
//		// 0x6B = 107d = Protocol Identifier: segmentation-supported
//		propertyIdentifierServiceParameter.setPayload(new byte[] { (byte) 0x6B });
//
//		final ServiceParameter openingTagServiceParameter = new ServiceParameter();
//		openingTagServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
//		openingTagServiceParameter.setTagNumber(0x03);
//		openingTagServiceParameter.setLengthValueType(ServiceParameter.OPENING_TAG_CODE);
//
//		final ServiceParameter valueServiceParameter = new ServiceParameter();
//		valueServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
//		valueServiceParameter.setTagNumber(ServiceParameter.UNSIGNED_INTEGER_CODE);
//		valueServiceParameter.setLengthValueType(0x01);
//		// segmented-both (0)
//		// segmented-transmit (1)
//		// segmented-receive (2)
//		// no-segmentation (3)
//		valueServiceParameter.setPayload(new byte[] { (byte) 0x03 });
//
//		final ServiceParameter closingTagServiceParameter = new ServiceParameter();
//		closingTagServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
//		closingTagServiceParameter.setTagNumber(0x03);
//		closingTagServiceParameter.setLengthValueType(ServiceParameter.CLOSING_TAG_CODE);
//
//		final APDU apdu = new APDU();
//		apdu.setPduType(PDUType.COMPLEX_ACK_PDU);
//		apdu.setInvokeId(requestMessage.getApdu().getInvokeId());
//		apdu.setServiceChoice(ServiceChoice.READ_PROPERTY);
//		apdu.setVendorMap(vendorMap);
////		apdu.setObjectIdentifierServiceParameter(objectIdentifierServiceParameter);
//		apdu.getServiceParameters().add(objectIdentifierServiceParameter);
//		apdu.getServiceParameters().add(propertyIdentifierServiceParameter);
//		apdu.getServiceParameters().add(openingTagServiceParameter);
//		apdu.getServiceParameters().add(valueServiceParameter);
//		apdu.getServiceParameters().add(closingTagServiceParameter);
//
//		final DefaultMessage result = new DefaultMessage();
//		result.setVirtualLinkControl(virtualLinkControl);
//		result.setNpdu(npdu);
//		result.setApdu(apdu);
//
//		virtualLinkControl.setLength(result.getDataLength());
//
//		final byte[] bytes = result.getBytes();
//		LOG.info(Utils.byteArrayToStringNoPrefix(bytes));
//
//		return result;
	}

	private DefaultMessage processSupportedServicesProperty(final int propertyKey, final Message requestMessage) {

		// return Unconfirmed request i-Am device,10001
		final int deviceInstanceNumber = DEVICE_INSTANCE_NUMBER;

		final VirtualLinkControl virtualLinkControl = new VirtualLinkControl();
		virtualLinkControl.setType(0x81);
		virtualLinkControl.setFunction(0x0A);
		virtualLinkControl.setLength(0x00);

		final NPDU npdu = new NPDU();
		npdu.setVersion(0x01);
		npdu.setControl(0x00);
//		npdu.setControl(0x20);
//		npdu.setDestinationNetworkNumber(302);
//		npdu.setDestinationMACLayerAddressLength(3);
//		npdu.setDestinationMac(0x001268);

		final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
		objectIdentifierServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
		// who are context tag numbers determined???
//		objectIdentifierServiceParameter.setTagNumber(ServiceParameter.BACNET_OBJECT_IDENTIFIER);
		objectIdentifierServiceParameter.setTagNumber(0x00);
		objectIdentifierServiceParameter.setLengthValueType(4);
		objectIdentifierServiceParameter.setObjectType(ObjectIdentifierServiceParameter.OBJECT_TYPE_DEVICE);
		objectIdentifierServiceParameter.setInstanceNumber(deviceInstanceNumber);

		final ServiceParameter protocolServicesSupportedServiceParameter = new ServiceParameter();
		protocolServicesSupportedServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		// who are context tag numbers determined???
//		protocolServicesSupportedServiceParameter.setTagNumber(ServiceParameter.UNKOWN_TAG_NUMBER);
		protocolServicesSupportedServiceParameter.setTagNumber(0x01);
		protocolServicesSupportedServiceParameter.setLengthValueType(1);
		// 0x61 = 97d = Protocol Identifier: protocol-services-supported
		protocolServicesSupportedServiceParameter.setPayload(new byte[] { (byte) propertyKey });

		final ServiceParameter openingTagServiceParameter = new ServiceParameter();
		openingTagServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		openingTagServiceParameter.setTagNumber(0x03);
		openingTagServiceParameter.setLengthValueType(ServiceParameter.OPENING_TAG_CODE);

		final ServiceParameter protocolServicesSupportedBitStringServiceParameter = new ServiceParameter();
		protocolServicesSupportedBitStringServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
		protocolServicesSupportedBitStringServiceParameter
				.setTagNumber(ServiceParameter.APPLICATION_TAG_NUMBER_BIT_STRING);
		// 0x05 = extended value
		protocolServicesSupportedBitStringServiceParameter.setLengthValueType(ServiceParameter.EXTENDED_VALUE);
		protocolServicesSupportedBitStringServiceParameter.setPayload(getPayload());

		final ServiceParameter closingTagServiceParameter = new ServiceParameter();
		closingTagServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		closingTagServiceParameter.setTagNumber(0x03);
		closingTagServiceParameter.setLengthValueType(ServiceParameter.CLOSING_TAG_CODE);

		final APDU apdu = new APDU();
		apdu.setPduType(PDUType.COMPLEX_ACK_PDU);
		apdu.setInvokeId(requestMessage.getApdu().getInvokeId());
		apdu.setServiceChoice(ServiceChoice.READ_PROPERTY);
		apdu.setVendorMap(vendorMap);
//		apdu.setObjectIdentifierServiceParameter(objectIdentifierServiceParameter);
		apdu.getServiceParameters().add(objectIdentifierServiceParameter);
		apdu.getServiceParameters().add(protocolServicesSupportedServiceParameter);
		apdu.getServiceParameters().add(openingTagServiceParameter);
		apdu.getServiceParameters().add(protocolServicesSupportedBitStringServiceParameter);
		apdu.getServiceParameters().add(closingTagServiceParameter);

		final DefaultMessage result = new DefaultMessage();
		result.setVirtualLinkControl(virtualLinkControl);
		result.setNpdu(npdu);
		result.setApdu(apdu);

		virtualLinkControl.setLength(result.getDataLength());

		final byte[] bytes = result.getBytes();
		LOG.info(Utils.byteArrayToStringNoPrefix(bytes));

		return result;
	}

	private byte[] getPayload() {

		final BACnetServicesSupportedBitString bacnetServicesSupportedBitString = new BACnetServicesSupportedBitString();

		bacnetServicesSupportedBitString.setAcknowledgeAlarm(false);
		bacnetServicesSupportedBitString.setConfirmedCOVNotification(true);
		bacnetServicesSupportedBitString.setConfirmedEventNotification(false);
		bacnetServicesSupportedBitString.setGetAlarmSummary(false);
		bacnetServicesSupportedBitString.setGetEnrollmentSummary(false);
		bacnetServicesSupportedBitString.setSubscribeCOV(false);
		bacnetServicesSupportedBitString.setAtomicReadFile(true);
		bacnetServicesSupportedBitString.setAtomicWriteFile(true);

		bacnetServicesSupportedBitString.setAddListElement(true);
		bacnetServicesSupportedBitString.setRemoveListElement(true);
		bacnetServicesSupportedBitString.setCreateObject(false);
		bacnetServicesSupportedBitString.setDeleteObject(false);
		bacnetServicesSupportedBitString.setReadProperty(true);
		bacnetServicesSupportedBitString.setReadPropertyMultiple(true);
		bacnetServicesSupportedBitString.setWriteProperty(true);

		bacnetServicesSupportedBitString.setWritePropertyMultiple(true);
		bacnetServicesSupportedBitString.setDeviceCommunicationControl(true);
		bacnetServicesSupportedBitString.setConfirmedPrivateTransfer(false);
		bacnetServicesSupportedBitString.setConfirmedTextMessage(false);
		bacnetServicesSupportedBitString.setReinitializeDevice(true);
		bacnetServicesSupportedBitString.setVtOpen(false);
		bacnetServicesSupportedBitString.setVtClose(false);
		bacnetServicesSupportedBitString.setVtData(false);

		bacnetServicesSupportedBitString.setiAm(true);
		bacnetServicesSupportedBitString.setiHave(false);
		bacnetServicesSupportedBitString.setUnconfirmedCOVNotification(true);
		bacnetServicesSupportedBitString.setUnconfirmedEventNotification(false);
		bacnetServicesSupportedBitString.setUnconfirmedPrivateTransfer(false);
		bacnetServicesSupportedBitString.setUnconfirmedTextMessage(false);

		bacnetServicesSupportedBitString.setTimeSynchronization(true);
		bacnetServicesSupportedBitString.setWhoHas(true);
		bacnetServicesSupportedBitString.setWhoIs(true);
		bacnetServicesSupportedBitString.setReadRange(true);
		bacnetServicesSupportedBitString.setUtcTimeSynchronization(true);
		bacnetServicesSupportedBitString.setLifeSafetyOperation(false);
		bacnetServicesSupportedBitString.setSubscribeCOVProperty(true);
		bacnetServicesSupportedBitString.setGetEventInformation(false);

		final BitSet bitSet = bacnetServicesSupportedBitString.getBitSet();
		final byte[] byteArray = bitSet.toByteArray();

//		final byte[] result = new byte[8];
		final byte[] result = new byte[7];

		// there is an unused zero byte at the beginning for some reason
		System.arraycopy(byteArray, 0, result, 2, byteArray.length);

		// length value is 6 byte
		// first byte is an unused zero byte
		// the last 5 byte contain the bit set of all available services of this device
//		result[0] = (byte) 0x85;
//		result[1] = (byte) 0x06;
//		result[2] = (byte) 0x00;

		result[0] = (byte) 0x06;
		result[1] = (byte) 0x00;

		LOG.info(Utils.byteArrayToStringNoPrefix(result));

		return result;
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

		return defaultMessage;
	}

	/**
	 * Answer I-Am with nothing
	 * 
	 * @param message
	 * @return
	 */
	private Message processIAMMessage(final Message message) {
		return null;
	}

	private Message returnIntegerProperty(final int invokeId, final int propertyId, final byte[] payload) {

		final int deviceInstanceNumber = DEVICE_INSTANCE_NUMBER;

		final VirtualLinkControl virtualLinkControl = new VirtualLinkControl();
		virtualLinkControl.setType(0x81);
		virtualLinkControl.setFunction(0x0A);
		virtualLinkControl.setLength(0x00);

		final NPDU npdu = new NPDU();
		npdu.setVersion(0x01);
		npdu.setControl(0x00);
//		npdu.setSourceNetworkAddress(requestMessage.getNpdu().getDestinationNetworkNumber());
//		npdu.setDestinationMACLayerAddressLength(requestMessage.getNpdu().getDestinationMACLayerAddressLength());
//		npdu.setDestinationMac(requestMessage.getNpdu().getDestinationMac());

		final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
		objectIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		// who are context tag numbers determined???
//		objectIdentifierServiceParameter.setTagNumber(ServiceParameter.BACNET_OBJECT_IDENTIFIER);
		objectIdentifierServiceParameter.setTagNumber(0x00);
		objectIdentifierServiceParameter.setLengthValueType(4);
		objectIdentifierServiceParameter.setObjectType(ObjectIdentifierServiceParameter.OBJECT_TYPE_DEVICE);
		objectIdentifierServiceParameter.setInstanceNumber(deviceInstanceNumber);

		final ServiceParameter propertyIdentifierServiceParameter = new ServiceParameter();
		propertyIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		// who are context tag numbers determined???
//		protocolServicesSupportedServiceParameter.setTagNumber(ServiceParameter.UNKOWN_TAG_NUMBER);
		propertyIdentifierServiceParameter.setTagNumber(0x01);
		propertyIdentifierServiceParameter.setLengthValueType(1);
		propertyIdentifierServiceParameter.setPayload(new byte[] { (byte) propertyId });

		final ServiceParameter openingTagServiceParameter = new ServiceParameter();
		openingTagServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		openingTagServiceParameter.setTagNumber(0x03);
		openingTagServiceParameter.setLengthValueType(ServiceParameter.OPENING_TAG_CODE);

		final ServiceParameter valueServiceParameter = new ServiceParameter();
		valueServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
		valueServiceParameter.setTagNumber(ServiceParameter.UNSIGNED_INTEGER_CODE);
		valueServiceParameter.setLengthValueType(payload.length);
		valueServiceParameter.setPayload(payload);

		final ServiceParameter closingTagServiceParameter = new ServiceParameter();
		closingTagServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		closingTagServiceParameter.setTagNumber(0x03);
		closingTagServiceParameter.setLengthValueType(ServiceParameter.CLOSING_TAG_CODE);

		final APDU apdu = new APDU();
		apdu.setPduType(PDUType.COMPLEX_ACK_PDU);
		apdu.setInvokeId(invokeId);
		apdu.setServiceChoice(ServiceChoice.READ_PROPERTY);
		apdu.setVendorMap(vendorMap);
//		apdu.setObjectIdentifierServiceParameter(objectIdentifierServiceParameter);
		apdu.getServiceParameters().add(objectIdentifierServiceParameter);
		apdu.getServiceParameters().add(propertyIdentifierServiceParameter);
		apdu.getServiceParameters().add(openingTagServiceParameter);
		apdu.getServiceParameters().add(valueServiceParameter);
		apdu.getServiceParameters().add(closingTagServiceParameter);

		final DefaultMessage result = new DefaultMessage();
		result.setVirtualLinkControl(virtualLinkControl);
		result.setNpdu(npdu);
		result.setApdu(apdu);

		virtualLinkControl.setLength(result.getDataLength());

		final byte[] bytes = result.getBytes();
		LOG.info(Utils.byteArrayToStringNoPrefix(bytes));

		return result;
	}

	public Map<Integer, String> getVendorMap() {
		return vendorMap;
	}

	public void setVendorMap(final Map<Integer, String> vendorMap) {
		this.vendorMap = vendorMap;
	}

}
