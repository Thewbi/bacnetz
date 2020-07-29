package de.bacnetz.controller;

import java.nio.ByteBuffer;
import java.util.BitSet;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.bacnet.factory.MessageType;
import de.bacnetz.common.Utils;
import de.bacnetz.common.utils.NetworkUtils;
import de.bacnetz.devices.DefaultDevice;
import de.bacnetz.devices.DeviceProperty;
import de.bacnetz.factory.MessageFactory;
import de.bacnetz.stack.APDU;
import de.bacnetz.stack.BACnetProtocolObjectTypesSupportedBitString;
import de.bacnetz.stack.BACnetServicesSupportedBitString;
import de.bacnetz.stack.NPDU;
import de.bacnetz.stack.ObjectIdentifierServiceParameter;
import de.bacnetz.stack.PDUType;
import de.bacnetz.stack.ServiceChoice;
import de.bacnetz.stack.ServiceParameter;
import de.bacnetz.stack.TagClass;
import de.bacnetz.stack.VirtualLinkControl;

public class DefaultMessageController implements MessageController {

	private static final int AMOUNT_OF_OBJECTS = 15;

	private static final Logger LOG = LogManager.getLogger(DefaultMessageController.class);

	private final DefaultDevice device = new DefaultDevice();

	private Map<Integer, String> vendorMap = new HashMap<>();

	private final MessageFactory messageFactory = new MessageFactory();

	@Override
	public Message processMessage(final Message message) {
		if (message.getApdu() == null) {
			return processNonAPDUMessage(message);
		} else {
			return processAPDUMessage(message);
		}
	}

	private Message processNonAPDUMessage(final Message message) {

		LOG.warn("<<< Not handling: " + message.getNpdu().getNetworkLayerMessageType().name());

		switch (message.getNpdu().getNetworkLayerMessageType()) {
		case WHO_IS_ROUTER_TO_NETWORK:
		case I_AM_ROUTER_TO_NETWORK:
		case I_COULD_BE_ROUTER_TO_NETWORK:
		case REJECT_MESSAGE_TO_NETWORK:
		case ROUTER_BUSY_TO_NETWORK:
		case ROUTER_AVAILABLE_TO_NETWORK:
		case INITIALIZE_ROUTING_TABLE:
		case INITIALIZE_ROUTING_TABLE_ACK:
		case ESTABLISH_CONNECTION_TO_NETWORK:
		case DISCONNECT_CONNECTION_TO_NETWORK:
		case CHALLENGE_REQUEST:
		case SECURITY_PAYLOAD:
		case SECURITY_RESPONSE:
		case REQUEST_KEY_UPDATE:
		case UPDATE_KEY_SET:
		case UPDATE_DISTRIBUTION_KEY:
		case REQUEST_MASTER_KEY:
		case SET_MASTER_KEY:
		case WHAT_IS_NETWORK_NUMBER:
		case NETWORK_NUMBER_IS:
			return null;

		default:
			LOG.warn("<<< Unknown message: " + message.getNpdu().getNetworkLayerMessageType());
			return null;
		}
	}

	private Message processAPDUMessage(final Message message) {

		switch (message.getApdu().getServiceChoice()) {

		case I_AM:
			LOG.info(">>> I_AM received!");
			return processIAMMessage(message);

		/** 20.1.3 BACnet-Unconfirmed-Request-PDU */
		case I_HAVE:
			throw new RuntimeException(">>> Not implemented yet! Message: " + message.getApdu().getServiceChoice());

		/** 20.1.4 BACnet-SimpleACK-PDU */
		case UNCONFIRMED_COV_NOTIFICATION:
			throw new RuntimeException(">>> Not implemented yet! Message: " + message.getApdu().getServiceChoice());

		/** 20.1.5 BACnet-ComplexACK-PDU */
		case UNCONFIRMED_EVENT_NOTIFICATION:
			throw new RuntimeException(">>> Not implemented yet! Message: " + message.getApdu().getServiceChoice());

		/** 20.1.6 BACnet-SegmentACK-PDU */
		case UNCONFIRMED_PRIVATE_TRANSFER:
			throw new RuntimeException(">>> Not implemented yet! Message: " + message.getApdu().getServiceChoice());

		/** 20.1.7 BACnet-Error-PDU */
		case UNCONFIRMED_TEXT_MESSAGE:
			throw new RuntimeException(">>> Not implemented yet! Message: " + message.getApdu().getServiceChoice());

		/** 20.1.8 BACnet-Reject-PDU */
		case TIME_SYNCHRONIZATION:
			throw new RuntimeException(">>> Not implemented yet! Message: " + message.getApdu().getServiceChoice());

		/** 20.1.9 BACnet-Abort-PDU */
		case WHO_HAS:
			throw new RuntimeException(">>> Not implemented yet! Message: " + message.getApdu().getServiceChoice());

		/** 20.1.2 BACnet-Confirmed-Request-PDU */
		case WHO_IS:
			LOG.info(">>> WHO_IS received!");
			return processWhoIsMessage(message);

		case UTC_TIME_SYNCHRONIZATION:
			throw new RuntimeException(">>> Not implemented yet! Message: " + message.getApdu().getServiceChoice());

		case WRITE_GROUP:
			throw new RuntimeException(">>> Not implemented yet! Message: " + message.getApdu().getServiceChoice());

		case READ_PROPERTY:
			LOG.info(">>> READ_PROPERTY received!");
			return processReadProperty(message);

		case READ_PROPERTY_MULTIPLE:
			LOG.info(">>> READ_PROPERTY_MULTIPLE received!");
			return processReadPropertyMultiple(message);

		case WRITE_PROPERTY:
			LOG.info(">>> WRITE_PROPERTY received!");
			return processWriteProperty(message);

		case DEVICE_COMMUNICATION_CONTROL:
			LOG.info(">>> DEVICE_COMMUNICATION_CONTROL received!");
			return processDeviceCommunicationControl(message);

		case REINITIALIZE_DEVICE:
			LOG.info(">>> REINITIALIZE_DEVICE received!");
			return processReinitializeDevice(message);

		default:
			LOG.warn(">>> Unknown message: " + message.getApdu().getServiceChoice());
			return null;
		}
	}

	private Message processReinitializeDevice(final Message requestMessage) {

		// TODO check for the correct password in the second service parameter

		final VirtualLinkControl virtualLinkControl = new VirtualLinkControl();
		virtualLinkControl.setType(0x81);
		virtualLinkControl.setFunction(0x0A);
		virtualLinkControl.setLength(0x00);

		final NPDU npdu = new NPDU();
		npdu.setVersion(0x01);

		// no additional information
		// this works, if the cp is connected to the device directly via 192.168.2.1
		npdu.setControl(0x00);

		if (NetworkUtils.ADD_ADDITIONAL_NETWORK_INFORMATION) {

			// destination network information
			npdu.setControl(0x20);
			npdu.setDestinationNetworkNumber(302);
			npdu.setDestinationMACLayerAddressLength(3);
			npdu.setDestinationMac(NetworkUtils.DEVICE_MAC_ADDRESS);

			npdu.setDestinationHopCount(255);
		}

		final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
		objectIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		objectIdentifierServiceParameter.setTagNumber(0x00);
		objectIdentifierServiceParameter.setLengthValueType(4);
		objectIdentifierServiceParameter.setObjectType(ObjectIdentifierServiceParameter.OBJECT_TYPE_DEVICE);
		objectIdentifierServiceParameter.setInstanceNumber(NetworkUtils.DEVICE_INSTANCE_NUMBER);

//		final ServiceParameter propertyIdentifierServiceParameter = new ServiceParameter();
//		propertyIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
//		propertyIdentifierServiceParameter.setTagNumber(0x01);
//		propertyIdentifierServiceParameter.setLengthValueType(0x01);
//		propertyIdentifierServiceParameter.setPayload(new byte[] { (byte) propertyIdentifierCode });

		final APDU apdu = new APDU();
		apdu.setPduType(PDUType.SIMPLE_ACK_PDU);
		apdu.setInvokeId(requestMessage.getApdu().getInvokeId());
		apdu.setServiceChoice(ServiceChoice.REINITIALIZE_DEVICE);
		apdu.setVendorMap(vendorMap);
//		apdu.getServiceParameters().add(objectIdentifierServiceParameter);
//		apdu.getServiceParameters().add(propertyIdentifierServiceParameter);
//		apdu.getServiceParameters().add(openingTagServiceParameter3);
//		apdu.getServiceParameters().add(openingTagServiceParameter2);
//		apdu.getServiceParameters().add(dateServiceParameter);
//		apdu.getServiceParameters().add(timeServiceParameter);
//		apdu.getServiceParameters().add(closingTagServiceParameter2);
//		apdu.getServiceParameters().add(closingTagServiceParameter3);

		final DefaultMessage result = new DefaultMessage();
		result.setVirtualLinkControl(virtualLinkControl);
		result.setNpdu(npdu);
		result.setApdu(apdu);

		virtualLinkControl.setLength(result.getDataLength());

//		final byte[] bytes = result.getBytes();
//		LOG.info(Utils.byteArrayToStringNoPrefix(bytes));

		return result;
	}

	private Message processDeviceCommunicationControl(final Message requestMessage) {
		throw new RuntimeException("Not implemented!");
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

			// find lower bound as integer
			int lowerBound = 0;
			if (lowerBoundServiceParameter.getPayload().length == 1) {
				lowerBound = lowerBoundServiceParameter.getPayload()[0];
			} else {
				lowerBound = Utils.bytesToUnsignedShort(lowerBoundServiceParameter.getPayload()[0],
						lowerBoundServiceParameter.getPayload()[1], bigEndian);
			}

			// find upper bound as integer
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
			if ((lowerBound > NetworkUtils.DEVICE_INSTANCE_NUMBER)
					|| (NetworkUtils.DEVICE_INSTANCE_NUMBER > upperBound)) {

				LOG.trace("Ignoring Who-Is! DeviceID: {}, [{} - {}]", NetworkUtils.DEVICE_INSTANCE_NUMBER, lowerBound,
						upperBound);
				return null;
			}
		}

		LOG.trace("WHO_IS received!");

		// return Unconfirmed request i-Am device,10001
		final int deviceInstanceNumber = NetworkUtils.DEVICE_INSTANCE_NUMBER;

		final VirtualLinkControl virtualLinkControl = new VirtualLinkControl();
		virtualLinkControl.setType(0x81);
		virtualLinkControl.setFunction(0x0B);
		virtualLinkControl.setLength(0x00);

//		// simple NPDU
//		final NPDU npdu = new NPDU();
//		npdu.setVersion(0x01);
//		npdu.setControl(0x00);

		// NPDU including destination network information
		final NPDU npdu = new NPDU();
		npdu.setVersion(0x01);
		npdu.setControl(0x20);
		npdu.setDestinationNetworkNumber(0xFFFF);
		// indicates broadcast on destination network
		npdu.setDestinationMACLayerAddressLength(0);
		npdu.setDestinationHopCount(255);

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

		// 0xB2 = 178d = loytec
//		byte[] vendorIdBuffer = new byte[] { (byte) 0xB2 };

		// 0x021A = 538d = GEZE
		final byte[] vendorIdBuffer = new byte[] { (byte) 0x02, (byte) 0x1A };

		vendorIdServiceParameter.setLengthValueType(vendorIdBuffer.length);
		vendorIdServiceParameter.setPayload(vendorIdBuffer);

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

		virtualLinkControl.setLength(virtualLinkControl.getDataLength() + npdu.getDataLength() + apdu.getDataLength());

		return result;
	}

	private Message processWriteProperty(final Message requestMessage) {

		LOG.info("processWriteProperty()");

		final int propertyIdentifier = requestMessage.getApdu().getPropertyIdentifier();

		LOG.info("Property Identifier: {}", propertyIdentifier);

		final int propertyIdentifierCode = propertyIdentifier;

		switch (propertyIdentifier) {

		// 0xCA = 202d
		case 0xCA:
			LOG.info("<<< WRITE_PROP: restart notification recipients ({})", propertyIdentifierCode);
			return processWriteRestartNotificationRecipientsProperty(propertyIdentifierCode, requestMessage);

		default:
			throw new NotImplementedException("Unknown property! PropertyIdentifier = " + propertyIdentifier);
		}
	}

	private Message processWriteRestartNotificationRecipientsProperty(final int propertyIdentifierCode,
			final Message requestMessage) {

		final VirtualLinkControl virtualLinkControl = new VirtualLinkControl();
		virtualLinkControl.setType(0x81);
		virtualLinkControl.setFunction(0x0A);
		virtualLinkControl.setLength(0x00);

		final NPDU npdu = new NPDU();
		npdu.setVersion(0x01);

		// no additional information
		// this works, if the cp is connected to the device directly via 192.168.2.1
		npdu.setControl(0x00);

		if (NetworkUtils.ADD_ADDITIONAL_NETWORK_INFORMATION) {

			// destination network information
			npdu.setControl(0x20);
			npdu.setDestinationNetworkNumber(302);
			npdu.setDestinationMACLayerAddressLength(3);
			npdu.setDestinationMac(NetworkUtils.DEVICE_MAC_ADDRESS);

			npdu.setDestinationHopCount(255);
		}

		final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
		objectIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		objectIdentifierServiceParameter.setTagNumber(0x00);
		objectIdentifierServiceParameter.setLengthValueType(4);
		objectIdentifierServiceParameter.setObjectType(ObjectIdentifierServiceParameter.OBJECT_TYPE_DEVICE);
		objectIdentifierServiceParameter.setInstanceNumber(NetworkUtils.DEVICE_INSTANCE_NUMBER);

		final ServiceParameter propertyIdentifierServiceParameter = new ServiceParameter();
		propertyIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		propertyIdentifierServiceParameter.setTagNumber(0x01);
		propertyIdentifierServiceParameter.setLengthValueType(0x01);
		propertyIdentifierServiceParameter.setPayload(new byte[] { (byte) propertyIdentifierCode });

		final APDU apdu = new APDU();
		apdu.setPduType(PDUType.SIMPLE_ACK_PDU);
		apdu.setInvokeId(requestMessage.getApdu().getInvokeId());
		apdu.setServiceChoice(ServiceChoice.WRITE_PROPERTY);
		apdu.setVendorMap(vendorMap);
//		apdu.getServiceParameters().add(objectIdentifierServiceParameter);
//		apdu.getServiceParameters().add(propertyIdentifierServiceParameter);
//		apdu.getServiceParameters().add(openingTagServiceParameter3);
//		apdu.getServiceParameters().add(openingTagServiceParameter2);
//		apdu.getServiceParameters().add(dateServiceParameter);
//		apdu.getServiceParameters().add(timeServiceParameter);
//		apdu.getServiceParameters().add(closingTagServiceParameter2);
//		apdu.getServiceParameters().add(closingTagServiceParameter3);

		final DefaultMessage result = new DefaultMessage();
		result.setVirtualLinkControl(virtualLinkControl);
		result.setNpdu(npdu);
		result.setApdu(apdu);

		virtualLinkControl.setLength(result.getDataLength());

//		final byte[] bytes = result.getBytes();
//		LOG.info(Utils.byteArrayToStringNoPrefix(bytes));

		return result;

	}

	private Message processReadProperty(final Message requestMessage) {

		LOG.trace("processReadProperty()");

		final int propertyIdentifier = requestMessage.getApdu().getPropertyIdentifier();

		LOG.info("Property Identifier: {}", propertyIdentifier);

		final int propertyIdentifierCode = propertyIdentifier;

		final DeviceProperty deviceProperty = device.getProperties().get(propertyIdentifier);
		if (deviceProperty != null) {

			LOG.info("<<< READ_PROP: {} ({})", deviceProperty.getPropertyName(), propertyIdentifierCode);
			return messageFactory.create(deviceProperty.getMessageType(), NetworkUtils.DEVICE_INSTANCE_NUMBER,
					requestMessage.getApdu().getInvokeId(), deviceProperty.getPropertyKey(), deviceProperty.getValue());

		} else {

			String msg;

			switch (propertyIdentifier) {

			// Supported Services Property
			// 0x61 = 97d
			case 0x61:
				LOG.info("<<< READ_PROP: Supported Services Property ({})", propertyIdentifierCode);
				return processSupportedServicesProperty(propertyIdentifierCode, requestMessage);

			// 0x60 = 96d protocol-services-supported
			//
			// H.5.2.13 Protocol_Object_Types_Supported
			// This property indicates the BACnet protocol object types supported by this
			// device. See 12.10.15. The protocol object
			// types supported shall be at least Analog Input, Analog Output, Analog Value,
			// Binary Input, Binary Output, and Binary
			// Value.
			case 0x60:
				LOG.info("<<< READ_PROP: protocol-object-types-supported Property ({})", propertyIdentifierCode);
				return processProtocolObjectTypesSupportedServicesProperty(propertyIdentifierCode, requestMessage);

			// 0x4c = 76d (0x4c = 76d) object list
			case 0x4c:
				LOG.info("<<< READ_PROP: object list ({})", propertyIdentifierCode);
				return processObjectListRequest(propertyIdentifierCode, requestMessage);

			// 0x4d = 77d (0x4d = 77d) object name
			case 0x4d:
				LOG.info("<<< READ_PROP: object name ({})", propertyIdentifierCode);
				return processObjectNameProperty(propertyIdentifierCode, requestMessage);

			// 0xD1 = 209 (0xD1 = 209d) structured object list
			case 0xD1:
				LOG.info("<<< READ_PROP: structured object list ({})", propertyIdentifierCode);
				return processStructuredObjectListProperty(propertyIdentifierCode, requestMessage);

			// 0x0173 = 371d property list
			case 0x0173:
				LOG.info("<<< READ_PROP: property list ({})", propertyIdentifierCode);
				return processPropertyListProperty(propertyIdentifierCode, requestMessage);

			// 0xCA = 202d
			case 0xCA:
				LOG.info("<<< READ_PROP: restart notification recipients ({})", propertyIdentifierCode);
				return processRestartNotificationRecipientsProperty(propertyIdentifierCode, requestMessage);

			// 0x98 = 152d
			case 0x98:
				LOG.info("<<< READ_PROP: active-cov-subscriptions ({})", propertyIdentifierCode);
				return processActiveCovSubscriptionsProperty(propertyIdentifierCode, requestMessage);

			// 0xCB = 203d
			case 0xCB:
				LOG.info("<<< READ_PROP: time-of-device-restart ({})", propertyIdentifierCode);
				return processTimeOfDeviceRestartProperty(propertyIdentifierCode, requestMessage, true, true,
						new Date());

			// 0x9D = 157d
			case 0x9D:
				LOG.info("<<< READ_PROP: last-restore-time ({})", propertyIdentifierCode);
				return processTimeOfDeviceRestartProperty(propertyIdentifierCode, requestMessage, false, true,
						new Date());

			// 0xCE = 206d
			case 0xCE:
				LOG.info("<<< READ_PROP: UTC-time-synchronization-recipients ({})", propertyIdentifierCode);
				return processUTCTimeSynchronizationRecipientsProperty(propertyIdentifierCode, requestMessage);

			// 0x74 = 116d
			case 0x74:
				LOG.info("<<< READ_PROP: time-synchronization-recipients ({})", propertyIdentifierCode);
				return processTimeSynchronizationRecipientsProperty(propertyIdentifierCode, requestMessage);

			// 0x1C = 28d
			case 0x1C:
				LOG.info("<<< READ_PROP: description ({})", propertyIdentifierCode);
				return processStringProperty(propertyIdentifierCode, requestMessage, NetworkUtils.OBJECT_DESCRIPTION);

			// 0x3A = 58d
			case 0x3A:
				LOG.info("<<< READ_PROP: location ({})", propertyIdentifierCode);
				return processStringProperty(propertyIdentifierCode, requestMessage, NetworkUtils.OBJECT_LOCATION);

			// 0x46 = 70d
			case 0x46:
				LOG.info("<<< READ_PROP: model-name ({})", propertyIdentifierCode);
				return processStringProperty(propertyIdentifierCode, requestMessage, NetworkUtils.MODEL_NAME);

			// 0x4B = 75d
			case 0x4B:
				LOG.info("<<< READ_PROP: object-identifier ({})", propertyIdentifierCode);
				return processStringProperty(propertyIdentifierCode, requestMessage, NetworkUtils.OBJECT_IDENTIFIER);

			// 0x79 = 121d
			case 0x79:
				LOG.info("<<< READ_PROP: vendor-name ({})", propertyIdentifierCode);
				return processStringProperty(propertyIdentifierCode, requestMessage, NetworkUtils.VENDOR_NAME);

			// 0xA8 = 168d
			case 0xA8:
				LOG.info("<<< READ_PROP: profile-name ({})", propertyIdentifierCode);
				return processStringProperty(propertyIdentifierCode, requestMessage, NetworkUtils.PROFILE_NAME);

			// 0x1E = 30d
			case 0x1E:
				LOG.info("<<< READ_PROP: device-address-binding ({})", propertyIdentifierCode);
				return processAddressBindingProperty(propertyIdentifierCode, requestMessage);

			// 0xAB = 171d
			case 0xAB:
				LOG.info("<<< READ_PROP: slave-address-binding ({})", propertyIdentifierCode);
				return processAddressBindingProperty(propertyIdentifierCode, requestMessage);

			// 0xAA = 170d
			case 0xAA:
				LOG.info("<<< READ_PROP: manual-slave-address-binding ({})", propertyIdentifierCode);
				return processAddressBindingProperty(propertyIdentifierCode, requestMessage);

			// 0x27 = 39d
			case 0x27:
				LOG.info("<<< READ_PROP: fault-values ({})", propertyIdentifierCode);
				msg = "Unknown property! PropertyIdentifier = " + propertyIdentifier;
				LOG.error(msg);
				throw new NotImplementedException(msg);

			// 0x38 = 56d
			case 0x38:
				LOG.info("<<< READ_PROP: local-date ({})", propertyIdentifierCode);
//				msg = "Unknown property! PropertyIdentifier = " + propertyIdentifier;
//				LOG.error(msg);
//				throw new NotImplementedException(msg);
				return processTimeOfDeviceRestartProperty(propertyIdentifierCode, requestMessage, true, false,
						new Date());

			// 0x39 = 57d
			case 0x39:
				LOG.info("<<< READ_PROP: local-time ({})", propertyIdentifierCode);
//								msg = "Unknown property! PropertyIdentifier = " + propertyIdentifier;
//								LOG.error(msg);
//								throw new NotImplementedException(msg);
				return processTimeOfDeviceRestartProperty(propertyIdentifierCode, requestMessage, false, true,
						new Date());

			// 0x2C = 44d
			case 0x2C:
				LOG.info("<<< READ_PROP: firmware-revision ({})", propertyIdentifierCode);
				return processFirmwareRevisionProperty(propertyIdentifierCode, requestMessage);

//			// Segmentation supported
//			// 0x6B = 107d
//			case 0x6B:
//				LOG.info("<<< READ_PROP: Segmentation supported ({})", propertyIdentifierCode);
//				return processSegmentationSupportedProperty(propertyIdentifierCode, requestMessage);

//			// max-apdu-length-accepted
//			// 0x3E = 62d
//			case 0x3E:
//				LOG.info("<<< READ_PROP: max-apdu-length-accepted ({})", propertyIdentifierCode);
//				return processMaxAPDULengthAcceptedProperty(propertyIdentifierCode, requestMessage);

//			// max-segments-accepted
//			// 0xA7 = 167d
//			case 0xA7:
//				LOG.info("<<< READ_PROP: max-segments-accepted ({})", propertyIdentifierCode);
//				return processMaxSegmentsAcceptedProperty(propertyIdentifierCode, requestMessage);

//			// 0x0A = 10d APDU-Segment-Timeout
//			case 0x0A:
//				LOG.info("<<< READ_PROP: APDU-Segment-Timeout ({})", propertyIdentifierCode);
//				return processAPDUSegmentTimeoutProperty(propertyIdentifierCode, requestMessage);
//
//			// 0x0B = 11d APDU-Timeout
//			case 0x0B:
//				LOG.info("<<< READ_PROP: APDU-Timeout ({})", propertyIdentifierCode);
//				return processAPDUTimeoutProperty(propertyIdentifierCode, requestMessage);
//
//			// 0x9B = 155d database-revision (155d = 0x9B) defined in ASHRAE on page 696
//			case 0x9B:
//				LOG.info("<<< READ_PROP: database-revision ({})", propertyIdentifierCode);
//				return processDatabaseRevisionProperty(propertyIdentifierCode, requestMessage);
//
//			// 0x8B = 139d protocol-revision (0x8B = 139d)
//			case 0x8B:
//				LOG.info("<<< READ_PROP: protocol-revision ({})", propertyIdentifierCode);
//				return processProtocolRevisionProperty(propertyIdentifierCode, requestMessage);
//
//			// 0x62 = 98d protocol-version
//			case 0x62:
//				LOG.info("<<< READ_PROP: protocol-version ({})", propertyIdentifierCode);
//				return processProtocolVersionProperty(propertyIdentifierCode, requestMessage);

//			case DeviceProperty.LAST_RESTART_REASON:
//				LOG.info("<<< READ_PROP: last-restart-reason ({})", propertyIdentifierCode);
//				return processLastRestartReasonProperty(propertyIdentifierCode, requestMessage);

//			// 0x70 = 112
//			case 0x70:
//				LOG.info("<<< READ_PROP: system status ({})", propertyIdentifierCode);
//				return processSystemStatusProperty(propertyIdentifierCode, requestMessage);

//			// 0x0C = 12
//			case 0x0C:
//				LOG.info("<<< READ_PROP: application-software-version ({})", propertyIdentifierCode);
//				return processApplicationSoftwareVersionProperty(propertyIdentifierCode, requestMessage);

//			// 0x18 = 24
//			case 0x18:
//				LOG.info("<<< READ_PROP: daylight-savings-status ({})", propertyIdentifierCode);
//				return processDaylightSavingsStatusProperty(propertyIdentifierCode, requestMessage);

			// // 0x14 = 20
			// case 0x14:
			// LOG.info("<<< READ_PROP: ??? ({})", propertyIdentifierCode);
			// return process???Property(propertyIdentifierCode, requestMessage);

			default:
				msg = "Unknown property! PropertyIdentifier = " + propertyIdentifier;
				LOG.error(msg);
//				throw new NotImplementedException(msg);

				return error(requestMessage.getApdu().getInvokeId());
			}
		}
	}

	private Message processAddressBindingProperty(final int propertyIdentifierCode, final Message requestMessage) {

		final VirtualLinkControl virtualLinkControl = new VirtualLinkControl();
		virtualLinkControl.setType(0x81);
		virtualLinkControl.setFunction(0x0A);
		virtualLinkControl.setLength(0x00);

		final NPDU npdu = new NPDU();
		npdu.setVersion(0x01);

		// no additional information
		// this works, if the cp is connected to the device directly via 192.168.2.1
		npdu.setControl(0x00);

		if (NetworkUtils.ADD_ADDITIONAL_NETWORK_INFORMATION) {

			// destination network information
			npdu.setControl(0x20);
			npdu.setDestinationNetworkNumber(302);
			npdu.setDestinationMACLayerAddressLength(3);
			npdu.setDestinationMac(NetworkUtils.DEVICE_MAC_ADDRESS);

			npdu.setDestinationHopCount(255);
		}

		// this object identifier has to be context specific. I do not know why
		final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
		objectIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		objectIdentifierServiceParameter.setTagNumber(0x00);
		objectIdentifierServiceParameter.setLengthValueType(4);
		objectIdentifierServiceParameter.setObjectType(ObjectIdentifierServiceParameter.OBJECT_TYPE_DEVICE);
		objectIdentifierServiceParameter.setInstanceNumber(NetworkUtils.DEVICE_INSTANCE_NUMBER);

		final ServiceParameter propertyIdentifierServiceParameter = new ServiceParameter();
		propertyIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		propertyIdentifierServiceParameter.setTagNumber(0x01);
		propertyIdentifierServiceParameter.setLengthValueType(0x01);
		propertyIdentifierServiceParameter.setPayload(new byte[] { (byte) propertyIdentifierCode });

		final ServiceParameter openingTagServiceParameter3 = new ServiceParameter();
		openingTagServiceParameter3.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		openingTagServiceParameter3.setTagNumber(0x03);
		openingTagServiceParameter3.setLengthValueType(ServiceParameter.OPENING_TAG_CODE);

		final ServiceParameter closingTagServiceParameter3 = new ServiceParameter();
		closingTagServiceParameter3.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		closingTagServiceParameter3.setTagNumber(0x03);
		closingTagServiceParameter3.setLengthValueType(ServiceParameter.CLOSING_TAG_CODE);

		final APDU apdu = new APDU();
		apdu.setPduType(PDUType.COMPLEX_ACK_PDU);
		apdu.setInvokeId(requestMessage.getApdu().getInvokeId());
		apdu.setServiceChoice(ServiceChoice.READ_PROPERTY);
		apdu.setVendorMap(vendorMap);
		apdu.getServiceParameters().add(objectIdentifierServiceParameter);
		apdu.getServiceParameters().add(propertyIdentifierServiceParameter);
		apdu.getServiceParameters().add(openingTagServiceParameter3);
		// no data
		apdu.getServiceParameters().add(closingTagServiceParameter3);

		final DefaultMessage result = new DefaultMessage();
		result.setVirtualLinkControl(virtualLinkControl);
		result.setNpdu(npdu);
		result.setApdu(apdu);

		virtualLinkControl.setLength(result.getDataLength());

//		final byte[] bytes = result.getBytes();
//		LOG.info(Utils.byteArrayToStringNoPrefix(bytes));

		return result;
	}

	private Message processStringProperty(final int propertyIdentifierCode, final Message requestMessage,
			final String data) {

		final VirtualLinkControl virtualLinkControl = new VirtualLinkControl();
		virtualLinkControl.setType(0x81);
		virtualLinkControl.setFunction(0x0A);
		virtualLinkControl.setLength(0x00);

		final NPDU npdu = new NPDU();
		npdu.setVersion(0x01);

		// no additional information
		// this works, if the cp is connected to the device directly via 192.168.2.1
		npdu.setControl(0x00);

		if (NetworkUtils.ADD_ADDITIONAL_NETWORK_INFORMATION) {

			// destination network information
			npdu.setControl(0x20);
			npdu.setDestinationNetworkNumber(302);
			npdu.setDestinationMACLayerAddressLength(3);
			npdu.setDestinationMac(NetworkUtils.DEVICE_MAC_ADDRESS);

			npdu.setDestinationHopCount(255);
		}

		// this object identifier has to be context specific. I do not know why
		final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
		objectIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		objectIdentifierServiceParameter.setTagNumber(0x00);
		objectIdentifierServiceParameter.setLengthValueType(4);
		objectIdentifierServiceParameter.setObjectType(ObjectIdentifierServiceParameter.OBJECT_TYPE_DEVICE);
		objectIdentifierServiceParameter.setInstanceNumber(NetworkUtils.DEVICE_INSTANCE_NUMBER);

		final ServiceParameter propertyIdentifierServiceParameter = new ServiceParameter();
		propertyIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		propertyIdentifierServiceParameter.setTagNumber(0x01);
		propertyIdentifierServiceParameter.setLengthValueType(0x01);
		propertyIdentifierServiceParameter.setPayload(new byte[] { (byte) propertyIdentifierCode });

		final ServiceParameter openingTagServiceParameter3 = new ServiceParameter();
		openingTagServiceParameter3.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		openingTagServiceParameter3.setTagNumber(0x03);
		openingTagServiceParameter3.setLengthValueType(ServiceParameter.OPENING_TAG_CODE);

//		final String description = "CU 420";
//		final byte payload[] = new byte[description.length() + 2];
//		System.arraycopy(description.getBytes(), 0, payload, 2, description.length());
//		payload[0] = (byte) description.length();
//		payload[1] = 0x00;

		final ServiceParameter descriptionServiceParameter = new ServiceParameter();
		descriptionServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
		descriptionServiceParameter.setTagNumber(ServiceParameter.APPLICATION_TAG_NUMBER_CHARACTER_STRING);
		descriptionServiceParameter.setLengthValueType(ServiceParameter.EXTENDED_TAG_CODE);
		descriptionServiceParameter.setPayload(retrieveAsString(data));

		final ServiceParameter closingTagServiceParameter3 = new ServiceParameter();
		closingTagServiceParameter3.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		closingTagServiceParameter3.setTagNumber(0x03);
		closingTagServiceParameter3.setLengthValueType(ServiceParameter.CLOSING_TAG_CODE);

		final APDU apdu = new APDU();
		apdu.setPduType(PDUType.COMPLEX_ACK_PDU);
		apdu.setInvokeId(requestMessage.getApdu().getInvokeId());
		apdu.setServiceChoice(ServiceChoice.READ_PROPERTY);
		apdu.setVendorMap(vendorMap);
		apdu.getServiceParameters().add(objectIdentifierServiceParameter);
		apdu.getServiceParameters().add(propertyIdentifierServiceParameter);
		apdu.getServiceParameters().add(openingTagServiceParameter3);

		apdu.getServiceParameters().add(descriptionServiceParameter);

		apdu.getServiceParameters().add(closingTagServiceParameter3);

		final DefaultMessage result = new DefaultMessage();
		result.setVirtualLinkControl(virtualLinkControl);
		result.setNpdu(npdu);
		result.setApdu(apdu);

		virtualLinkControl.setLength(result.getDataLength());

//		final byte[] bytes = result.getBytes();
//		LOG.info(Utils.byteArrayToStringNoPrefix(bytes));

		return result;
	}

	private Message processDaylightSavingsStatusProperty(final int propertyIdentifierCode,
			final Message requestMessage) {
		return messageFactory.create(MessageType.BOOLEAN_PROPERTY, NetworkUtils.DEVICE_INSTANCE_NUMBER,
				requestMessage.getApdu().getInvokeId(), propertyIdentifierCode, new byte[] { (byte) 0x01 });
	}

	private Message processApplicationSoftwareVersionProperty(final int propertyIdentifierCode,
			final Message requestMessage) {
		return messageFactory.create(MessageType.INTEGER_PROPERTY, NetworkUtils.DEVICE_INSTANCE_NUMBER,
				requestMessage.getApdu().getInvokeId(), propertyIdentifierCode, new byte[] { (byte) 0x01 });
	}

	private Message processSystemStatusProperty(final int propertyIdentifierCode, final Message requestMessage) {

		// 0x00 == operational
		final int systemStatus = 0x00;

		return messageFactory.create(MessageType.ENUMERATED, NetworkUtils.DEVICE_INSTANCE_NUMBER,
				requestMessage.getApdu().getInvokeId(), propertyIdentifierCode, new byte[] { (byte) systemStatus });
	}

	/**
	 * The bacnet partner can issue two different requests for the object list. See
	 * bacnet_whois_iam_readProperty.pcapng (messages 1117 and 1123)
	 * 
	 * One of the requests (message 1117) is about the length of the object list.
	 * The response is an unsigned integer.
	 * 
	 * The other request (message 1123) asks for the actual object list. The
	 * response is a complex ack with all objects as service paramters.
	 * 
	 * @param propertyIdentifierCode
	 * @param requestMessage
	 * @return
	 */
	private Message processObjectListRequest(final int propertyIdentifierCode, final Message requestMessage) {

		final List<ServiceParameter> serviceParameters = requestMessage.getApdu().getServiceParameters();

		final int serviceParamterSize = serviceParameters.size();

		LOG.info("serviceParameters.size() = {}", serviceParamterSize);

		if (serviceParameters.size() == 3) {

			final ServiceParameter serviceParameter = serviceParameters.get(2);

			if (serviceParameter.getPayload()[0] == 0) {

				// The query want's to know about the Array Index 0.
				// In bacnet the first element of an array always contains the amount of
				// elements/objects inside the array/object list
				LOG.info("The query want's to know about the Array Index 0 = the amount of objects in the object list");
				return processObjectListLengthProperty(propertyIdentifierCode, requestMessage);

			} else {
				throw new RuntimeException("unknown query!");
			}
		}

		return processObjectListProperty(propertyIdentifierCode, requestMessage);
	}

	private Message processTimeSynchronizationRecipientsProperty(final int propertyIdentifierCode,
			final Message requestMessage) {

		final VirtualLinkControl virtualLinkControl = new VirtualLinkControl();
		virtualLinkControl.setType(0x81);
		virtualLinkControl.setFunction(0x0A);
		virtualLinkControl.setLength(0x00);

		final NPDU npdu = new NPDU();
		npdu.setVersion(0x01);

		// no additional information
		// this works, if the cp is connected to the device directly via 192.168.2.1
		npdu.setControl(0x00);

		if (NetworkUtils.ADD_ADDITIONAL_NETWORK_INFORMATION) {

			// destination network information
			npdu.setControl(0x20);
			npdu.setDestinationNetworkNumber(302);
			npdu.setDestinationMACLayerAddressLength(3);
			npdu.setDestinationMac(NetworkUtils.DEVICE_MAC_ADDRESS);

			npdu.setDestinationHopCount(255);
		}

		// this object identifier has to be context specific. I do not know why
		final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
		objectIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		objectIdentifierServiceParameter.setTagNumber(0x00);
		objectIdentifierServiceParameter.setLengthValueType(4);
		objectIdentifierServiceParameter.setObjectType(ObjectIdentifierServiceParameter.OBJECT_TYPE_DEVICE);
		objectIdentifierServiceParameter.setInstanceNumber(NetworkUtils.DEVICE_INSTANCE_NUMBER);

		final ServiceParameter propertyIdentifierServiceParameter = new ServiceParameter();
		propertyIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		propertyIdentifierServiceParameter.setTagNumber(0x01);
		propertyIdentifierServiceParameter.setLengthValueType(0x01);
		propertyIdentifierServiceParameter.setPayload(new byte[] { (byte) propertyIdentifierCode });

		final ServiceParameter openingTagServiceParameter3 = new ServiceParameter();
		openingTagServiceParameter3.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		openingTagServiceParameter3.setTagNumber(0x03);
		openingTagServiceParameter3.setLengthValueType(ServiceParameter.OPENING_TAG_CODE);

		final ServiceParameter closingTagServiceParameter3 = new ServiceParameter();
		closingTagServiceParameter3.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		closingTagServiceParameter3.setTagNumber(0x03);
		closingTagServiceParameter3.setLengthValueType(ServiceParameter.CLOSING_TAG_CODE);

		final APDU apdu = new APDU();
		apdu.setPduType(PDUType.COMPLEX_ACK_PDU);
		apdu.setInvokeId(requestMessage.getApdu().getInvokeId());
		apdu.setServiceChoice(ServiceChoice.READ_PROPERTY);
		apdu.setVendorMap(vendorMap);
		apdu.getServiceParameters().add(objectIdentifierServiceParameter);
		apdu.getServiceParameters().add(propertyIdentifierServiceParameter);
		apdu.getServiceParameters().add(openingTagServiceParameter3);

		// there are currently no recipients configured on the device.
		// The cp (communication partner) will now possibly write itself into the list
		// using write property

		apdu.getServiceParameters().add(closingTagServiceParameter3);

		final DefaultMessage result = new DefaultMessage();
		result.setVirtualLinkControl(virtualLinkControl);
		result.setNpdu(npdu);
		result.setApdu(apdu);

		virtualLinkControl.setLength(result.getDataLength());

//		final byte[] bytes = result.getBytes();
//		LOG.info(Utils.byteArrayToStringNoPrefix(bytes));

		return result;
	}

	private Message processUTCTimeSynchronizationRecipientsProperty(final int propertyIdentifierCode,
			final Message requestMessage) {

		final VirtualLinkControl virtualLinkControl = new VirtualLinkControl();
		virtualLinkControl.setType(0x81);
		virtualLinkControl.setFunction(0x0A);
		virtualLinkControl.setLength(0x00);

		final NPDU npdu = new NPDU();
		npdu.setVersion(0x01);

		// no additional information
		// this works, if the cp is connected to the device directly via 192.168.2.1
		npdu.setControl(0x00);

		if (NetworkUtils.ADD_ADDITIONAL_NETWORK_INFORMATION) {

			// destination network information
			npdu.setControl(0x20);
			npdu.setDestinationNetworkNumber(302);
			npdu.setDestinationMACLayerAddressLength(3);
			npdu.setDestinationMac(NetworkUtils.DEVICE_MAC_ADDRESS);

			npdu.setDestinationHopCount(255);
		}

		// this object identifier has to be context specific. I do not know why
		final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
		objectIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		objectIdentifierServiceParameter.setTagNumber(0x00);
		objectIdentifierServiceParameter.setLengthValueType(4);
		objectIdentifierServiceParameter.setObjectType(ObjectIdentifierServiceParameter.OBJECT_TYPE_DEVICE);
		objectIdentifierServiceParameter.setInstanceNumber(NetworkUtils.DEVICE_INSTANCE_NUMBER);

		final ServiceParameter propertyIdentifierServiceParameter = new ServiceParameter();
		propertyIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		propertyIdentifierServiceParameter.setTagNumber(0x01);
		propertyIdentifierServiceParameter.setLengthValueType(0x01);
		propertyIdentifierServiceParameter.setPayload(new byte[] { (byte) propertyIdentifierCode });

		final ServiceParameter openingTagServiceParameter3 = new ServiceParameter();
		openingTagServiceParameter3.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		openingTagServiceParameter3.setTagNumber(0x03);
		openingTagServiceParameter3.setLengthValueType(ServiceParameter.OPENING_TAG_CODE);

		final ServiceParameter closingTagServiceParameter3 = new ServiceParameter();
		closingTagServiceParameter3.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		closingTagServiceParameter3.setTagNumber(0x03);
		closingTagServiceParameter3.setLengthValueType(ServiceParameter.CLOSING_TAG_CODE);

		final APDU apdu = new APDU();
		apdu.setPduType(PDUType.COMPLEX_ACK_PDU);
		apdu.setInvokeId(requestMessage.getApdu().getInvokeId());
		apdu.setServiceChoice(ServiceChoice.READ_PROPERTY);
		apdu.setVendorMap(vendorMap);
		apdu.getServiceParameters().add(objectIdentifierServiceParameter);
		apdu.getServiceParameters().add(propertyIdentifierServiceParameter);
		apdu.getServiceParameters().add(openingTagServiceParameter3);

		// there are currently no recipients configured on the device.
		// The cp (communication partner) will now possibly write itself into the list
		// using write property

		apdu.getServiceParameters().add(closingTagServiceParameter3);

		final DefaultMessage result = new DefaultMessage();
		result.setVirtualLinkControl(virtualLinkControl);
		result.setNpdu(npdu);
		result.setApdu(apdu);

		virtualLinkControl.setLength(result.getDataLength());

//		final byte[] bytes = result.getBytes();
//		LOG.info(Utils.byteArrayToStringNoPrefix(bytes));

		return result;
	}

	private Message processActiveCovSubscriptionsProperty(final int propertyIdentifierCode,
			final Message requestMessage) {

		final VirtualLinkControl virtualLinkControl = new VirtualLinkControl();
		virtualLinkControl.setType(0x81);
		virtualLinkControl.setFunction(0x0A);
		virtualLinkControl.setLength(0x00);

		final NPDU npdu = new NPDU();
		npdu.setVersion(0x01);

		// no additional information
		// this works, if the cp is connected to the device directly via 192.168.2.1
		npdu.setControl(0x00);

		if (NetworkUtils.ADD_ADDITIONAL_NETWORK_INFORMATION) {

			// destination network information
			npdu.setControl(0x20);
			npdu.setDestinationNetworkNumber(302);
			npdu.setDestinationMACLayerAddressLength(3);
			npdu.setDestinationMac(NetworkUtils.DEVICE_MAC_ADDRESS);

			npdu.setDestinationHopCount(255);
		}

		// this object identifier has to be context specific. I do not know why
		final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
		objectIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		objectIdentifierServiceParameter.setTagNumber(0x00);
		objectIdentifierServiceParameter.setLengthValueType(4);
		objectIdentifierServiceParameter.setObjectType(ObjectIdentifierServiceParameter.OBJECT_TYPE_DEVICE);
		objectIdentifierServiceParameter.setInstanceNumber(NetworkUtils.DEVICE_INSTANCE_NUMBER);

		final ServiceParameter propertyIdentifierServiceParameter = new ServiceParameter();
		propertyIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		propertyIdentifierServiceParameter.setTagNumber(0x01);
		propertyIdentifierServiceParameter.setLengthValueType(0x01);
		propertyIdentifierServiceParameter.setPayload(new byte[] { (byte) propertyIdentifierCode });

		final ServiceParameter openingTagServiceParameter3 = new ServiceParameter();
		openingTagServiceParameter3.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		openingTagServiceParameter3.setTagNumber(0x03);
		openingTagServiceParameter3.setLengthValueType(ServiceParameter.OPENING_TAG_CODE);

		final ServiceParameter closingTagServiceParameter3 = new ServiceParameter();
		closingTagServiceParameter3.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		closingTagServiceParameter3.setTagNumber(0x03);
		closingTagServiceParameter3.setLengthValueType(ServiceParameter.CLOSING_TAG_CODE);

		final APDU apdu = new APDU();
		apdu.setPduType(PDUType.COMPLEX_ACK_PDU);
		apdu.setInvokeId(requestMessage.getApdu().getInvokeId());
		apdu.setServiceChoice(ServiceChoice.READ_PROPERTY);
		apdu.setVendorMap(vendorMap);
		apdu.getServiceParameters().add(objectIdentifierServiceParameter);
		apdu.getServiceParameters().add(propertyIdentifierServiceParameter);
		apdu.getServiceParameters().add(openingTagServiceParameter3);

		// there are currently no recipients configured on the device.
		// The cp (communication partner) will now possibly write itself into the list
		// using write property

		apdu.getServiceParameters().add(closingTagServiceParameter3);

		final DefaultMessage result = new DefaultMessage();
		result.setVirtualLinkControl(virtualLinkControl);
		result.setNpdu(npdu);
		result.setApdu(apdu);

		virtualLinkControl.setLength(result.getDataLength());

//		final byte[] bytes = result.getBytes();
//		LOG.info(Utils.byteArrayToStringNoPrefix(bytes));

		return result;
	}

	private Message processRestartNotificationRecipientsProperty(final int propertyIdentifierCode,
			final Message requestMessage) {

		final VirtualLinkControl virtualLinkControl = new VirtualLinkControl();
		virtualLinkControl.setType(0x81);
		virtualLinkControl.setFunction(0x0A);
		virtualLinkControl.setLength(0x00);

		final NPDU npdu = new NPDU();
		npdu.setVersion(0x01);

		// no additional information
		// this works, if the cp is connected to the device directly via 192.168.2.1
		npdu.setControl(0x00);

		if (NetworkUtils.ADD_ADDITIONAL_NETWORK_INFORMATION) {

			// destination network information
			npdu.setControl(0x20);
			npdu.setDestinationNetworkNumber(302);
			npdu.setDestinationMACLayerAddressLength(3);
			npdu.setDestinationMac(NetworkUtils.DEVICE_MAC_ADDRESS);

			npdu.setDestinationHopCount(255);
		}

		// this object identifier has to be context specific. I do not know why
		final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
		objectIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		objectIdentifierServiceParameter.setTagNumber(0x00);
		objectIdentifierServiceParameter.setLengthValueType(4);
		objectIdentifierServiceParameter.setObjectType(ObjectIdentifierServiceParameter.OBJECT_TYPE_DEVICE);
		objectIdentifierServiceParameter.setInstanceNumber(NetworkUtils.DEVICE_INSTANCE_NUMBER);

		final ServiceParameter propertyIdentifierServiceParameter = new ServiceParameter();
		propertyIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		propertyIdentifierServiceParameter.setTagNumber(0x01);
		propertyIdentifierServiceParameter.setLengthValueType(0x01);
		propertyIdentifierServiceParameter.setPayload(new byte[] { (byte) propertyIdentifierCode });

		final ServiceParameter openingTagServiceParameter3 = new ServiceParameter();
		openingTagServiceParameter3.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		openingTagServiceParameter3.setTagNumber(0x03);
		openingTagServiceParameter3.setLengthValueType(ServiceParameter.OPENING_TAG_CODE);

		final ServiceParameter closingTagServiceParameter3 = new ServiceParameter();
		closingTagServiceParameter3.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		closingTagServiceParameter3.setTagNumber(0x03);
		closingTagServiceParameter3.setLengthValueType(ServiceParameter.CLOSING_TAG_CODE);

		final APDU apdu = new APDU();
		apdu.setPduType(PDUType.COMPLEX_ACK_PDU);
		apdu.setInvokeId(requestMessage.getApdu().getInvokeId());
		apdu.setServiceChoice(ServiceChoice.READ_PROPERTY);
		apdu.setVendorMap(vendorMap);
		apdu.getServiceParameters().add(objectIdentifierServiceParameter);
		apdu.getServiceParameters().add(propertyIdentifierServiceParameter);
		apdu.getServiceParameters().add(openingTagServiceParameter3);

		// there are currently no recipients configured on the device.
		// The cp (communication partner) will now possibly write itself into the list
		// using write property

		apdu.getServiceParameters().add(closingTagServiceParameter3);

		final DefaultMessage result = new DefaultMessage();
		result.setVirtualLinkControl(virtualLinkControl);
		result.setNpdu(npdu);
		result.setApdu(apdu);

		virtualLinkControl.setLength(result.getDataLength());

//		final byte[] bytes = result.getBytes();
//		LOG.info(Utils.byteArrayToStringNoPrefix(bytes));

		return result;
	}

	private Message processTimeOfDeviceRestartProperty(final int propertyIdentifierCode, final Message requestMessage,
			final boolean addDate, final boolean addTime, final Date date) {

		final VirtualLinkControl virtualLinkControl = new VirtualLinkControl();
		virtualLinkControl.setType(0x81);
		virtualLinkControl.setFunction(0x0A);
		virtualLinkControl.setLength(0x00);

		final NPDU npdu = new NPDU();
		npdu.setVersion(0x01);

		// no additional information
		// this works, if the cp is connected to the device directly via 192.168.2.1
		npdu.setControl(0x00);

		if (NetworkUtils.ADD_ADDITIONAL_NETWORK_INFORMATION) {

			// destination network information
			npdu.setControl(0x20);
			npdu.setDestinationNetworkNumber(302);
			npdu.setDestinationMACLayerAddressLength(3);
			npdu.setDestinationMac(NetworkUtils.DEVICE_MAC_ADDRESS);

			npdu.setDestinationHopCount(255);
		}

		// this object identifier has to be context specific. I do not know why
		final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
//				objectIdentifierServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
		objectIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		// who are context tag numbers determined???
//				objectIdentifierServiceParameter.setTagNumber(ServiceParameter.BACNET_OBJECT_IDENTIFIER);
		objectIdentifierServiceParameter.setTagNumber(0x00);
		objectIdentifierServiceParameter.setLengthValueType(4);
		objectIdentifierServiceParameter.setObjectType(ObjectIdentifierServiceParameter.OBJECT_TYPE_DEVICE);
		objectIdentifierServiceParameter.setInstanceNumber(NetworkUtils.DEVICE_INSTANCE_NUMBER);

		final ServiceParameter propertyIdentifierServiceParameter = new ServiceParameter();
		propertyIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		propertyIdentifierServiceParameter.setTagNumber(0x01);
		propertyIdentifierServiceParameter.setLengthValueType(0x01);
		propertyIdentifierServiceParameter.setPayload(new byte[] { (byte) propertyIdentifierCode });
//
		final ServiceParameter openingTagServiceParameter3 = new ServiceParameter();
		openingTagServiceParameter3.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		openingTagServiceParameter3.setTagNumber(0x03);
		openingTagServiceParameter3.setLengthValueType(ServiceParameter.OPENING_TAG_CODE);

		final ServiceParameter openingTagServiceParameter2 = new ServiceParameter();
		openingTagServiceParameter2.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		openingTagServiceParameter2.setTagNumber(0x02);
		openingTagServiceParameter2.setLengthValueType(ServiceParameter.OPENING_TAG_CODE);

		final ServiceParameter dateServiceParameter = new ServiceParameter();
		dateServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
		dateServiceParameter.setTagNumber(ServiceParameter.DATE);
		dateServiceParameter.setLengthValueType(0x04);
		dateServiceParameter.setPayload(new byte[] { (byte) 0x78, (byte) 0x07, (byte) 0x09, (byte) 0x04 });

		final ServiceParameter timeServiceParameter = new ServiceParameter();
		timeServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
		timeServiceParameter.setTagNumber(ServiceParameter.TIME);
		timeServiceParameter.setLengthValueType(0x04);
		timeServiceParameter.setPayload(new byte[] { (byte) 0x12, (byte) 0x0e, (byte) 0x16, (byte) 0x15 });

		final ServiceParameter closingTagServiceParameter2 = new ServiceParameter();
		closingTagServiceParameter2.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		closingTagServiceParameter2.setTagNumber(0x02);
		closingTagServiceParameter2.setLengthValueType(ServiceParameter.CLOSING_TAG_CODE);

		final ServiceParameter closingTagServiceParameter3 = new ServiceParameter();
		closingTagServiceParameter3.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		closingTagServiceParameter3.setTagNumber(0x03);
		closingTagServiceParameter3.setLengthValueType(ServiceParameter.CLOSING_TAG_CODE);

		final APDU apdu = new APDU();
		apdu.setPduType(PDUType.COMPLEX_ACK_PDU);
		apdu.setInvokeId(requestMessage.getApdu().getInvokeId());
		apdu.setServiceChoice(ServiceChoice.READ_PROPERTY);
		apdu.setVendorMap(vendorMap);
		apdu.getServiceParameters().add(objectIdentifierServiceParameter);
		apdu.getServiceParameters().add(propertyIdentifierServiceParameter);
		apdu.getServiceParameters().add(openingTagServiceParameter3);
		apdu.getServiceParameters().add(openingTagServiceParameter2);

		if (addDate) {
			apdu.getServiceParameters().add(dateServiceParameter);
		}
		if (addTime) {
			apdu.getServiceParameters().add(timeServiceParameter);
		}
		apdu.getServiceParameters().add(closingTagServiceParameter2);
		apdu.getServiceParameters().add(closingTagServiceParameter3);

		final DefaultMessage result = new DefaultMessage();
		result.setVirtualLinkControl(virtualLinkControl);
		result.setNpdu(npdu);
		result.setApdu(apdu);

		virtualLinkControl.setLength(result.getDataLength());

//		final byte[] bytes = result.getBytes();
//		LOG.info(Utils.byteArrayToStringNoPrefix(bytes));

		return result;
	}

	private Message processPropertyListProperty(final int propertyIdentifierCode, final Message requestMessage) {

		throw new RuntimeException("Not implemented!");

//		LOG.info("done");
//		return null;
	}

	private Message processObjectNameProperty(final int propertyKey, final Message requestMessage) {

		final VirtualLinkControl virtualLinkControl = new VirtualLinkControl();
		virtualLinkControl.setType(0x81);
		virtualLinkControl.setFunction(0x0A);
		virtualLinkControl.setLength(0x00);

		final NPDU npdu = new NPDU();
		npdu.setVersion(0x01);

		// no additional information
		// this works, if the cp is connected to the device directly via 192.168.2.1
		npdu.setControl(0x00);

		if (NetworkUtils.ADD_ADDITIONAL_NETWORK_INFORMATION) {

			// destination network information
			npdu.setControl(0x20);
			npdu.setDestinationNetworkNumber(302);
			npdu.setDestinationMACLayerAddressLength(3);
			npdu.setDestinationMac(NetworkUtils.DEVICE_MAC_ADDRESS);

			npdu.setDestinationHopCount(255);
		}

		// this object identifier has to be context specific. I do not know why
		final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
//				objectIdentifierServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
		objectIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		// who are context tag numbers determined???
//				objectIdentifierServiceParameter.setTagNumber(ServiceParameter.BACNET_OBJECT_IDENTIFIER);
		objectIdentifierServiceParameter.setTagNumber(0x00);
		objectIdentifierServiceParameter.setLengthValueType(4);
		objectIdentifierServiceParameter.setObjectType(ObjectIdentifierServiceParameter.OBJECT_TYPE_DEVICE);
		objectIdentifierServiceParameter.setInstanceNumber(NetworkUtils.DEVICE_INSTANCE_NUMBER);

		final ServiceParameter propertyIdentifierServiceParameter = new ServiceParameter();
		propertyIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		propertyIdentifierServiceParameter.setTagNumber(0x01);
		propertyIdentifierServiceParameter.setLengthValueType(0x01);
		propertyIdentifierServiceParameter.setPayload(new byte[] { (byte) propertyKey });

		final ServiceParameter openingTagServiceParameter = new ServiceParameter();
		openingTagServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		openingTagServiceParameter.setTagNumber(0x03);
		openingTagServiceParameter.setLengthValueType(ServiceParameter.OPENING_TAG_CODE);

		final ServiceParameter objectNameServiceParameter = new ServiceParameter();
		objectNameServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
		objectNameServiceParameter.setTagNumber(ServiceParameter.APPLICATION_TAG_NUMBER_CHARACTER_STRING);
		// 0x05 = extended value
		objectNameServiceParameter.setLengthValueType(ServiceParameter.EXTENDED_VALUE);
//		objectNameServiceParameter.setLengthValueType(13);
		objectNameServiceParameter.setPayload(retrieveAsString(NetworkUtils.OBJECT_NAME));

		final ServiceParameter closingTagServiceParameter = new ServiceParameter();
		closingTagServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		closingTagServiceParameter.setTagNumber(0x03);
		closingTagServiceParameter.setLengthValueType(ServiceParameter.CLOSING_TAG_CODE);

		final APDU apdu = new APDU();
		apdu.setPduType(PDUType.COMPLEX_ACK_PDU);
		apdu.setInvokeId(requestMessage.getApdu().getInvokeId());
		apdu.setServiceChoice(ServiceChoice.READ_PROPERTY);
		apdu.setVendorMap(vendorMap);
		apdu.getServiceParameters().add(objectIdentifierServiceParameter);
		apdu.getServiceParameters().add(propertyIdentifierServiceParameter);
		apdu.getServiceParameters().add(openingTagServiceParameter);
		apdu.getServiceParameters().add(objectNameServiceParameter);
		apdu.getServiceParameters().add(closingTagServiceParameter);

		final DefaultMessage result = new DefaultMessage();
		result.setVirtualLinkControl(virtualLinkControl);
		result.setNpdu(npdu);
		result.setApdu(apdu);

		virtualLinkControl.setLength(result.getDataLength());

//		final byte[] bytes = result.getBytes();
//		LOG.info(Utils.byteArrayToStringNoPrefix(bytes));

		return result;
	}

	public static byte[] retrieveAsString(final String data) {

		final int dataLength = data.getBytes().length;

		// +1 for the leading zero (maybe this is the encoding code 0x00 for the
		// encoding ANSI X3.4 / UTF-8 (since 2010))
		final byte[] result = new byte[dataLength + 1];

		System.arraycopy(data.getBytes(), 0, result, 1, dataLength);

		// add a leading zero
		result[0] = 0;

		return result;
	}

	private Message processStructuredObjectListProperty(final int propertyKey, final Message requestMessage) {
		// return error
		return error(requestMessage.getApdu().getInvokeId());
	}

	private Message error(final int invokeId) {

		final VirtualLinkControl virtualLinkControl = new VirtualLinkControl();
		virtualLinkControl.setType(0x81);
		virtualLinkControl.setFunction(0x0A);
		virtualLinkControl.setLength(0x00);

		final NPDU npdu = new NPDU();
		npdu.setVersion(0x01);
		npdu.setControl(0x00);

		// no additional information
		// this works, if the cp is connected to the device directly via 192.168.2.1
		npdu.setControl(0x00);

		if (NetworkUtils.ADD_ADDITIONAL_NETWORK_INFORMATION) {

			// destination network information
			npdu.setControl(0x20);
			npdu.setDestinationNetworkNumber(302);
			npdu.setDestinationMACLayerAddressLength(3);
			npdu.setDestinationMac(NetworkUtils.DEVICE_MAC_ADDRESS);

			npdu.setDestinationHopCount(255);
		}

//		npdu.setControl(0x08);
//		npdu.setSourceNetworkAddress(999);
//		npdu.setSourceMacLayerAddressLength(2);
//		npdu.setSourceMac(NetworkUtils.DEVICE_INSTANCE_NUMBER);

		final ServiceParameter errorClassServiceParameter = new ServiceParameter();
		errorClassServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
		errorClassServiceParameter.setTagNumber(ServiceParameter.ENUMERATED_CODE);
		errorClassServiceParameter.setLengthValueType(0x01);
		errorClassServiceParameter.setPayload(new byte[] { (byte) 0x02 });

		final ServiceParameter errorCodeServiceParameter = new ServiceParameter();
		errorCodeServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
		errorCodeServiceParameter.setTagNumber(ServiceParameter.ENUMERATED_CODE);
		errorCodeServiceParameter.setLengthValueType(0x01);
		errorCodeServiceParameter.setPayload(new byte[] { (byte) 0x20 });

		final APDU apdu = new APDU();
		apdu.setPduType(PDUType.ERROR_PDU);
		apdu.setInvokeId(invokeId);
		apdu.setServiceChoice(ServiceChoice.READ_PROPERTY);
		apdu.setVendorMap(vendorMap);
		apdu.getServiceParameters().add(errorClassServiceParameter);
		apdu.getServiceParameters().add(errorCodeServiceParameter);

		final DefaultMessage result = new DefaultMessage();
		result.setVirtualLinkControl(virtualLinkControl);
		result.setNpdu(npdu);
		result.setApdu(apdu);

		virtualLinkControl.setLength(result.getDataLength());

//		final byte[] bytes = result.getBytes();
//		LOG.info(Utils.byteArrayToStringNoPrefix(bytes));

		return result;
	}

	@SuppressWarnings("unused")
	private Message processObjectListLengthProperty(final int propertyIdentifierCode, final Message requestMessage) {

		final VirtualLinkControl virtualLinkControl = new VirtualLinkControl();
		virtualLinkControl.setType(0x81);
		virtualLinkControl.setFunction(0x0A);
		virtualLinkControl.setLength(0x00);

//		// simple NPDU
//		final NPDU npdu = new NPDU();
//		npdu.setVersion(0x01);
//		npdu.setControl(0x00);

		// NPDU including destination network information
		final NPDU npdu = new NPDU();
		npdu.setVersion(0x01);
//		npdu.setControl(0x00);

		// no additional information
		// this works, if the cp is connected to the device directly via 192.168.2.1
		npdu.setControl(0x00);

		if (NetworkUtils.ADD_ADDITIONAL_NETWORK_INFORMATION) {

			// destination network information
			npdu.setControl(0x20);
			npdu.setDestinationNetworkNumber(302);
			npdu.setDestinationMACLayerAddressLength(3);
			npdu.setDestinationMac(NetworkUtils.DEVICE_MAC_ADDRESS);

			npdu.setDestinationHopCount(255);
		}

		// this object identifier has to be context specific. I do not know why
		final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
//		objectIdentifierServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
		objectIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		// who are context tag numbers determined???
//		objectIdentifierServiceParameter.setTagNumber(ServiceParameter.BACNET_OBJECT_IDENTIFIER);
		objectIdentifierServiceParameter.setTagNumber(0x00);
		objectIdentifierServiceParameter.setLengthValueType(4);
		objectIdentifierServiceParameter.setObjectType(ObjectIdentifierServiceParameter.OBJECT_TYPE_DEVICE);
		objectIdentifierServiceParameter.setInstanceNumber(NetworkUtils.DEVICE_INSTANCE_NUMBER);

		final ServiceParameter propertyIdentifierServiceParameter = new ServiceParameter();
		propertyIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		propertyIdentifierServiceParameter.setTagNumber(0x01);
		propertyIdentifierServiceParameter.setLengthValueType(0x01);
		propertyIdentifierServiceParameter.setPayload(new byte[] { (byte) propertyIdentifierCode });

		final ServiceParameter propertyArrayIndexServiceParameter = new ServiceParameter();
		propertyArrayIndexServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		propertyArrayIndexServiceParameter.setTagNumber(0x02);
		propertyArrayIndexServiceParameter.setLengthValueType(0x01);
		propertyArrayIndexServiceParameter.setPayload(new byte[] { (byte) 0x00 });

//		final ServiceParameter protocolServicesSupportedServiceParameter = new ServiceParameter();
//		protocolServicesSupportedServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
//		// who are context tag numbers determined???
////		protocolServicesSupportedServiceParameter.setTagNumber(ServiceParameter.UNKOWN_TAG_NUMBER);
//		protocolServicesSupportedServiceParameter.setTagNumber(0x01);
//		protocolServicesSupportedServiceParameter.setLengthValueType(1);
//		// 0x61 = 97d = Protocol Identifier: protocol-services-supported
//		protocolServicesSupportedServiceParameter.setPayload(new byte[] { (byte) propertyKey });

//		final ObjectIdentifierServiceParameter objectIdentifierServiceParameterTwo = new ObjectIdentifierServiceParameter();
//		objectIdentifierServiceParameterTwo.setTagClass(TagClass.APPLICATION_TAG);
//		objectIdentifierServiceParameterTwo.setTagNumber(ServiceParameter.BACNET_OBJECT_IDENTIFIER);
//		objectIdentifierServiceParameterTwo.setLengthValueType(0x04);
//		objectIdentifierServiceParameterTwo.setObjectType(ObjectIdentifierServiceParameter.OBJECT_TYPE_DEVICE);
//		objectIdentifierServiceParameterTwo.setInstanceNumber(NetworkUtils.DEVICE_INSTANCE_NUMBER);

		final ServiceParameter openingTagServiceParameter = new ServiceParameter();
		openingTagServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		openingTagServiceParameter.setTagNumber(0x03);
		openingTagServiceParameter.setLengthValueType(ServiceParameter.OPENING_TAG_CODE);

		final ServiceParameter objectListLengthServiceParameter = new ServiceParameter();
		objectListLengthServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
		objectListLengthServiceParameter.setTagNumber(0x02);
		objectListLengthServiceParameter.setLengthValueType(0x01);
		objectListLengthServiceParameter.setPayload(new byte[] { (byte) AMOUNT_OF_OBJECTS });

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
		apdu.getServiceParameters().add(propertyIdentifierServiceParameter);
		apdu.getServiceParameters().add(propertyArrayIndexServiceParameter);

		apdu.getServiceParameters().add(openingTagServiceParameter);

		apdu.getServiceParameters().add(objectListLengthServiceParameter);

		apdu.getServiceParameters().add(closingTagServiceParameter);

		final DefaultMessage result = new DefaultMessage();
		result.setVirtualLinkControl(virtualLinkControl);
		result.setNpdu(npdu);
		result.setApdu(apdu);

		virtualLinkControl.setLength(result.getDataLength());

//		final byte[] bytes = result.getBytes();
//		LOG.info(Utils.byteArrayToStringNoPrefix(bytes));

		return result;
	}

	private Message processObjectListProperty(final int propertyKey, final Message requestMessage) {

		final VirtualLinkControl virtualLinkControl = new VirtualLinkControl();
		virtualLinkControl.setType(0x81);
		virtualLinkControl.setFunction(0x0A);
		virtualLinkControl.setLength(0x00);

//		// simple NPDU
//		final NPDU npdu = new NPDU();
//		npdu.setVersion(0x01);
//		npdu.setControl(0x00);

		// NPDU including destination network information
		final NPDU npdu = new NPDU();
		npdu.setVersion(0x01);
//		npdu.setControl(0x00);

		// no additional information
		// this works, if the cp is connected to the device directly via 192.168.2.1
		npdu.setControl(0x00);

		if (NetworkUtils.ADD_ADDITIONAL_NETWORK_INFORMATION) {

			// destination network information
			npdu.setControl(0x20);
			npdu.setDestinationNetworkNumber(302);
			npdu.setDestinationMACLayerAddressLength(3);
			npdu.setDestinationMac(NetworkUtils.DEVICE_MAC_ADDRESS);

			npdu.setDestinationHopCount(255);
		}

//		npdu.setControl(0x08);
//		npdu.setSourceNetworkAddress(999);
//		npdu.setSourceMacLayerAddressLength(2);
//		npdu.setSourceMac(NetworkUtils.DEVICE_INSTANCE_NUMBER);

//		npdu.setControl(0x08);
//		npdu.setDestinationNetworkNumber(0xFFFF);
//		// indicates broadcast on destination network
//		npdu.setDestinationMACLayerAddressLength(0);
//		npdu.setDestinationHopCount(255);

//		npdu.setControl(0x20);
//		npdu.setDestinationNetworkNumber(0xFFFF);
//		// indicates broadcast on destination network
//		npdu.setDestinationMACLayerAddressLength(0);
//		npdu.setDestinationHopCount(255);

		// this object identifier has to be context specific. I do not know why
		final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
//		objectIdentifierServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
		objectIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		// who are context tag numbers determined???
//		objectIdentifierServiceParameter.setTagNumber(ServiceParameter.BACNET_OBJECT_IDENTIFIER);
		objectIdentifierServiceParameter.setTagNumber(0x00);
		objectIdentifierServiceParameter.setLengthValueType(4);
		objectIdentifierServiceParameter.setObjectType(ObjectIdentifierServiceParameter.OBJECT_TYPE_DEVICE);
		objectIdentifierServiceParameter.setInstanceNumber(NetworkUtils.DEVICE_INSTANCE_NUMBER);

		final ServiceParameter propertyIdentifierServiceParameter = new ServiceParameter();
		propertyIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		propertyIdentifierServiceParameter.setTagNumber(0x01);
		propertyIdentifierServiceParameter.setLengthValueType(0x01);
		propertyIdentifierServiceParameter.setPayload(new byte[] { (byte) propertyKey });

//		final ServiceParameter protocolServicesSupportedServiceParameter = new ServiceParameter();
//		protocolServicesSupportedServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
//		// who are context tag numbers determined???
////		protocolServicesSupportedServiceParameter.setTagNumber(ServiceParameter.UNKOWN_TAG_NUMBER);
//		protocolServicesSupportedServiceParameter.setTagNumber(0x01);
//		protocolServicesSupportedServiceParameter.setLengthValueType(1);
//		// 0x61 = 97d = Protocol Identifier: protocol-services-supported
//		protocolServicesSupportedServiceParameter.setPayload(new byte[] { (byte) propertyKey });

		final ObjectIdentifierServiceParameter objectIdentifierServiceParameterTwo = new ObjectIdentifierServiceParameter();
		objectIdentifierServiceParameterTwo.setTagClass(TagClass.APPLICATION_TAG);
		objectIdentifierServiceParameterTwo.setTagNumber(ServiceParameter.BACNET_OBJECT_IDENTIFIER);
		objectIdentifierServiceParameterTwo.setLengthValueType(0x04);
		objectIdentifierServiceParameterTwo.setObjectType(ObjectIdentifierServiceParameter.OBJECT_TYPE_DEVICE);
		objectIdentifierServiceParameterTwo.setInstanceNumber(NetworkUtils.DEVICE_INSTANCE_NUMBER);

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
		protocolServicesSupportedBitStringServiceParameter.setPayload(getSupportedServicesPayload());

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

		// a sub device
		apdu.getServiceParameters().add(objectIdentifierServiceParameter);

		apdu.getServiceParameters().add(propertyIdentifierServiceParameter);
//		apdu.getServiceParameters().add(protocolServicesSupportedServiceParameter);

		apdu.getServiceParameters().add(openingTagServiceParameter);

		// does the simulated device have to list this object identifier again?
		apdu.getServiceParameters().add(objectIdentifierServiceParameterTwo);

		// 1
		apdu.getServiceParameters().add(createMultiStateValueServiceParameter(1));
		// 2
		apdu.getServiceParameters().add(createMultiStateValueServiceParameter(2));
		// 3
		apdu.getServiceParameters().add(binaryInputServiceParameter(1));
		// 4
		apdu.getServiceParameters().add(binaryInputServiceParameter(2));
		// 5
		apdu.getServiceParameters().add(createMultiStateValueServiceParameter(3));
		// 6
		apdu.getServiceParameters().add(createMultiStateValueServiceParameter(4));
		// 7
		apdu.getServiceParameters().add(createNotificationClassServiceParameter(50));
		// 8
		apdu.getServiceParameters().add(createMultiStateValueServiceParameter(5));
		// 9
		apdu.getServiceParameters().add(createMultiStateValueServiceParameter(6));
		// 10
		apdu.getServiceParameters().add(binaryInputServiceParameter(3));
		// 11
		apdu.getServiceParameters().add(binaryInputServiceParameter(4));
		// 12
		apdu.getServiceParameters().add(createMultiStateValueServiceParameter(7));
		// 13
		apdu.getServiceParameters().add(createMultiStateValueServiceParameter(8));
		// 14
		apdu.getServiceParameters().add(createMultiStateValueServiceParameter(9));
		// 15
		apdu.getServiceParameters().add(createMultiStateValueServiceParameter(10));

		apdu.getServiceParameters().add(closingTagServiceParameter);

		final DefaultMessage result = new DefaultMessage();
		result.setVirtualLinkControl(virtualLinkControl);
		result.setNpdu(npdu);
		result.setApdu(apdu);

		virtualLinkControl.setLength(result.getDataLength());

//		final byte[] bytes = result.getBytes();
//		LOG.info(Utils.byteArrayToStringNoPrefix(bytes));

		return result;
	}

	private ServiceParameter createNotificationClassServiceParameter(final int id) {

		final ObjectIdentifierServiceParameter notificationServiceParameter = new ObjectIdentifierServiceParameter();
		notificationServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
		notificationServiceParameter.setTagNumber(ServiceParameter.BACNET_OBJECT_IDENTIFIER);
		notificationServiceParameter.setLengthValueType(0x04);
		notificationServiceParameter.setObjectType(ObjectIdentifierServiceParameter.OBJECT_TYPE_NOTIFICATION_CLASS);
		notificationServiceParameter.setInstanceNumber(id);

		return notificationServiceParameter;
	}

	private ServiceParameter binaryInputServiceParameter(final int objectId) {

		final ObjectIdentifierServiceParameter binaryInputServiceParameter = new ObjectIdentifierServiceParameter();
		binaryInputServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
		binaryInputServiceParameter.setTagNumber(ServiceParameter.BACNET_OBJECT_IDENTIFIER);
		binaryInputServiceParameter.setLengthValueType(0x04);
		binaryInputServiceParameter.setObjectType(ObjectIdentifierServiceParameter.OBJECT_TYPE_BINARY_INPUT);
		binaryInputServiceParameter.setInstanceNumber(objectId);

		return binaryInputServiceParameter;
	}

	private ServiceParameter createMultiStateValueServiceParameter(final int objectId) {

		final ObjectIdentifierServiceParameter multiStateValueServiceParameter = new ObjectIdentifierServiceParameter();
		multiStateValueServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
		multiStateValueServiceParameter.setTagNumber(ServiceParameter.BACNET_OBJECT_IDENTIFIER);
		multiStateValueServiceParameter.setLengthValueType(0x04);
		multiStateValueServiceParameter.setObjectType(ObjectIdentifierServiceParameter.OBJECT_TYPE_MULTI_STATE_VALUE);
		multiStateValueServiceParameter.setInstanceNumber(objectId);

		return multiStateValueServiceParameter;
	}

	private Message processLastRestartReasonProperty(final int propertyKey, final Message requestMessage) {
		// coldstart 1
		return messageFactory.create(MessageType.ENUMERATED, NetworkUtils.DEVICE_INSTANCE_NUMBER,
				requestMessage.getApdu().getInvokeId(), propertyKey, new byte[] { (byte) 0x01 });
	}

	private Message processProtocolVersionProperty(final int propertyKey, final Message requestMessage) {
		// protocol version 1
		return messageFactory.create(MessageType.INTEGER_PROPERTY, NetworkUtils.DEVICE_INSTANCE_NUMBER,
				requestMessage.getApdu().getInvokeId(), propertyKey, new byte[] { (byte) 0x01 });
	}

	private Message processProtocolRevisionProperty(final int propertyKey, final Message requestMessage) {
		// protocol revision 0x0C = 12d
		return messageFactory.create(MessageType.INTEGER_PROPERTY, NetworkUtils.DEVICE_INSTANCE_NUMBER,
				requestMessage.getApdu().getInvokeId(), propertyKey, new byte[] { (byte) 0x0C });
	}

	private Message processFirmwareRevisionProperty(final int propertyKey, final Message requestMessage) {
		// protocol revision 0x0C = 12d
		return messageFactory.create(MessageType.INTEGER_PROPERTY, NetworkUtils.DEVICE_INSTANCE_NUMBER,
				requestMessage.getApdu().getInvokeId(), propertyKey, new byte[] { (byte) 0x01 });
	}

	private Message processDatabaseRevisionProperty(final int propertyKey, final Message requestMessage) {
		// database revivion 3
		return messageFactory.create(MessageType.INTEGER_PROPERTY, NetworkUtils.DEVICE_INSTANCE_NUMBER,
				requestMessage.getApdu().getInvokeId(), propertyKey, new byte[] { (byte) 0x03 });
	}

	private Message processAPDUSegmentTimeoutProperty(final int propertyKey, final Message requestMessage) {

		// APDU Segment-Timeout:
		// Dieser Wert in Millisekunden legt fest, nach welcher Zeitspanne ein
		// quittierpflichtiges, segmentiertes Telegramm als fehlgeschlagen gewertet
		// wird, wenn die Segmentbestätigung ausbleibt. Der Standardwert beträgt
		// 2000 Millisekunden.
		// 2000d == 0x07D0
		return messageFactory.create(MessageType.INTEGER_PROPERTY, NetworkUtils.DEVICE_INSTANCE_NUMBER,
				requestMessage.getApdu().getInvokeId(), propertyKey, new byte[] { (byte) 0x07, (byte) 0xD0 });
	}

	private Message processMaxSegmentsAcceptedProperty(final int propertyKey, final Message requestMessage) {

		// APDU Max Segments Accepted:
		// Legt fest, wie viele Segmente maximal akzeptiert werden.
		return messageFactory.create(MessageType.INTEGER_PROPERTY, NetworkUtils.DEVICE_INSTANCE_NUMBER,
				requestMessage.getApdu().getInvokeId(), propertyKey, new byte[] { (byte) 0x01 });
	}

	private Message processAPDUTimeoutProperty(final int propertyKey, final Message requestMessage) {

		// ADPU Timeout:
		// Dieser Wert in Millisekunden legt fest, nach welcher Zeitspanne ein
		// quittierpflichtiges Telegramm als fehlgeschlagen gewertet wird, wenn die
		// Bestätigung ausbleibt. Der Standardwert beträgt 3000 ms.
		// 3000d == 0x0BB8
		return messageFactory.create(MessageType.INTEGER_PROPERTY, NetworkUtils.DEVICE_INSTANCE_NUMBER,
				requestMessage.getApdu().getInvokeId(), propertyKey, new byte[] { (byte) 0x0B, (byte) 0xB8 });
	}

	private Message processMaxAPDULengthAcceptedProperty(final int propertyKey, final Message requestMessage) {

		// Maximum APDU Length is dependent on the physical layer used, for example the
		// maximum APDU size for BACnet/IP is 1497 octets, but for BACnet MS/TP
		// segments, the maximum APDU size is only 480 octets.
		//
		// 1497d = 0x05D9
		// 62d = 0x3E
		return messageFactory.create(MessageType.INTEGER_PROPERTY, NetworkUtils.DEVICE_INSTANCE_NUMBER,
				requestMessage.getApdu().getInvokeId(), propertyKey, new byte[] { (byte) 0x05, (byte) 0xD9 });
	}

	private Message processSegmentationSupportedProperty(final int propertyKey, final Message requestMessage) {

		// segmented-both (0)
		// segmented-transmit (1)
		// segmented-receive (2)
		// no-segmentation (3)
		return messageFactory.create(MessageType.ENUMERATED, NetworkUtils.DEVICE_INSTANCE_NUMBER,
				requestMessage.getApdu().getInvokeId(), propertyKey, new byte[] { (byte) 0x00 });
	}

	private Message processProtocolObjectTypesSupportedServicesProperty(final int propertyIdentifierCode,
			final Message requestMessage) {

		final int deviceInstanceNumber = NetworkUtils.DEVICE_INSTANCE_NUMBER;

		final VirtualLinkControl virtualLinkControl = new VirtualLinkControl();
		virtualLinkControl.setType(0x81);
		virtualLinkControl.setFunction(0x0A);
		virtualLinkControl.setLength(0x00);

		final NPDU npdu = new NPDU();
		npdu.setVersion(0x01);

		// no additional information
		// this works, if the cp is connected to the device directly via 192.168.2.1
		npdu.setControl(0x00);

		if (NetworkUtils.ADD_ADDITIONAL_NETWORK_INFORMATION) {

			// destination network information
			npdu.setControl(0x20);
			npdu.setDestinationNetworkNumber(302);
			npdu.setDestinationMACLayerAddressLength(3);
			npdu.setDestinationMac(NetworkUtils.DEVICE_MAC_ADDRESS);

			npdu.setDestinationHopCount(255);
		}

		final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
		objectIdentifierServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
		// who are context tag numbers determined???
//		objectIdentifierServiceParameter.setTagNumber(ServiceParameter.BACNET_OBJECT_IDENTIFIER);
		objectIdentifierServiceParameter.setTagNumber(0x00);
		objectIdentifierServiceParameter.setLengthValueType(4);
		objectIdentifierServiceParameter.setObjectType(ObjectIdentifierServiceParameter.OBJECT_TYPE_DEVICE);
		objectIdentifierServiceParameter.setInstanceNumber(deviceInstanceNumber);

		final ServiceParameter protocolObjectTypesSupportedServiceParameter = new ServiceParameter();
		protocolObjectTypesSupportedServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		// who are context tag numbers determined???
//		protocolServicesSupportedServiceParameter.setTagNumber(ServiceParameter.UNKOWN_TAG_NUMBER);
		protocolObjectTypesSupportedServiceParameter.setTagNumber(0x01);
		protocolObjectTypesSupportedServiceParameter.setLengthValueType(1);
		// 0x61 = 97d = Protocol Identifier: protocol-services-supported
		protocolObjectTypesSupportedServiceParameter.setPayload(new byte[] { (byte) propertyIdentifierCode });

		final ServiceParameter openingTagServiceParameter = new ServiceParameter();
		openingTagServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		openingTagServiceParameter.setTagNumber(0x03);
		openingTagServiceParameter.setLengthValueType(ServiceParameter.OPENING_TAG_CODE);

		final ServiceParameter protocolObjectTypesSupportedBitStringServiceParameter = new ServiceParameter();
		protocolObjectTypesSupportedBitStringServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
		protocolObjectTypesSupportedBitStringServiceParameter
				.setTagNumber(ServiceParameter.APPLICATION_TAG_NUMBER_BIT_STRING);
		// 0x05 = extended value
		protocolObjectTypesSupportedBitStringServiceParameter.setLengthValueType(ServiceParameter.EXTENDED_VALUE);
		protocolObjectTypesSupportedBitStringServiceParameter.setPayload(getSupportedProtocolObjectTypesPayload());

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
		apdu.getServiceParameters().add(protocolObjectTypesSupportedServiceParameter);
		apdu.getServiceParameters().add(openingTagServiceParameter);
		apdu.getServiceParameters().add(protocolObjectTypesSupportedBitStringServiceParameter);
		apdu.getServiceParameters().add(closingTagServiceParameter);

		final DefaultMessage result = new DefaultMessage();
		result.setVirtualLinkControl(virtualLinkControl);
		result.setNpdu(npdu);
		result.setApdu(apdu);

		virtualLinkControl.setLength(result.getDataLength());

//		final byte[] bytes = result.getBytes();
//		LOG.info(Utils.byteArrayToStringNoPrefix(bytes));

		return result;
	}

	private DefaultMessage processSupportedServicesProperty(final int propertyKey, final Message requestMessage) {

		final int deviceInstanceNumber = NetworkUtils.DEVICE_INSTANCE_NUMBER;

		final VirtualLinkControl virtualLinkControl = new VirtualLinkControl();
		virtualLinkControl.setType(0x81);
		virtualLinkControl.setFunction(0x0A);
		virtualLinkControl.setLength(0x00);

		final NPDU npdu = new NPDU();
		npdu.setVersion(0x01);

		// no additional information
		// this works, if the cp is connected to the device directly via 192.168.2.1
		npdu.setControl(0x00);

		if (NetworkUtils.ADD_ADDITIONAL_NETWORK_INFORMATION) {

			// destination network information
			npdu.setControl(0x20);
			npdu.setDestinationNetworkNumber(302);
			npdu.setDestinationMACLayerAddressLength(3);
			npdu.setDestinationMac(NetworkUtils.DEVICE_MAC_ADDRESS);

			npdu.setDestinationHopCount(255);
		}

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
		protocolServicesSupportedBitStringServiceParameter.setPayload(getSupportedServicesPayload());

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

//		final byte[] bytes = result.getBytes();
//		LOG.info(Utils.byteArrayToStringNoPrefix(bytes));

		return result;
	}

	private byte[] getSupportedServicesPayload() {

		// retrieve the bits that describe which services are supported by this device
//		final BACnetServicesSupportedBitString bacnetServicesSupportedBitString = retrieveLoytecRouterServicesSupported();
		final BACnetServicesSupportedBitString bacnetServicesSupportedBitString = retrieveIO420ServicesSupported();
		final BitSet bitSet = bacnetServicesSupportedBitString.getBitSet();
		final byte[] bitSetByteArray = bitSet.toByteArray();

		// this is the result payload
		final byte[] result = new byte[7];

		// length value is 6 byte
		result[0] = (byte) 0x06;
		// first byte is an unused zero byte
		// there is an unused zero byte at the beginning for some reason
		result[1] = (byte) 0x00;
		// the last 5 byte contain the bit set of all available services of this device
		System.arraycopy(bitSetByteArray, 0, result, 2, bitSetByteArray.length);

//		LOG.trace(Utils.byteArrayToStringNoPrefix(result));

		return result;
	}

	private byte[] getSupportedProtocolObjectTypesPayload() {

		final BACnetProtocolObjectTypesSupportedBitString bitString = retrieveIO420ProtocolObjectTypesSupported();
		final BitSet bitSet = bitString.getBitSet();
		final byte[] bitSetByteArray = bitSet.toByteArray();

		// this is the result payload
		final byte[] result = new byte[6];

//		// length value is 6 byte
//		result[0] = (byte) 0x06;
		// first byte is an unused zero byte
		// there is an unused zero byte at the beginning for some reason
		result[0] = (byte) 0x02;
		// the last 5 byte contain the bit set of all available services of this device
		System.arraycopy(bitSetByteArray, 0, result, 1, bitSetByteArray.length);

//		LOG.trace(Utils.byteArrayToStringNoPrefix(result));

		return result;
	}

	private BACnetProtocolObjectTypesSupportedBitString retrieveIO420ProtocolObjectTypesSupported() {

		final BACnetProtocolObjectTypesSupportedBitString bacnetProtocolObjectTypesSupportedBitString = new BACnetProtocolObjectTypesSupportedBitString();

		bacnetProtocolObjectTypesSupportedBitString.setAnalogInput(false);
		bacnetProtocolObjectTypesSupportedBitString.setAnalogOutput(false);
		bacnetProtocolObjectTypesSupportedBitString.setAnalogValue(false);

		bacnetProtocolObjectTypesSupportedBitString.setBinaryInput(true);
		bacnetProtocolObjectTypesSupportedBitString.setBinaryOutput(false);
		bacnetProtocolObjectTypesSupportedBitString.setBinaryValue(false);

		bacnetProtocolObjectTypesSupportedBitString.setCalendar(false);
		bacnetProtocolObjectTypesSupportedBitString.setCommand(false);

		bacnetProtocolObjectTypesSupportedBitString.setDevice(true);
		bacnetProtocolObjectTypesSupportedBitString.setEventEnrollment(false);

		bacnetProtocolObjectTypesSupportedBitString.setFile(false);
		bacnetProtocolObjectTypesSupportedBitString.setGroup(false);
		bacnetProtocolObjectTypesSupportedBitString.setLoop(false);

		bacnetProtocolObjectTypesSupportedBitString.setMultiStateInput(false);
		bacnetProtocolObjectTypesSupportedBitString.setMultiStateOutput(false);

		bacnetProtocolObjectTypesSupportedBitString.setNotificationClass(true);

		bacnetProtocolObjectTypesSupportedBitString.setProgram(false);
		bacnetProtocolObjectTypesSupportedBitString.setSchedule(false);
		bacnetProtocolObjectTypesSupportedBitString.setAveraging(false);
		bacnetProtocolObjectTypesSupportedBitString.setMultiStateValue(true);

		bacnetProtocolObjectTypesSupportedBitString.setTrendLog(false);

		bacnetProtocolObjectTypesSupportedBitString.setLifeSafetyPoint(false);
		bacnetProtocolObjectTypesSupportedBitString.setLifeSafetyZone(false);

		bacnetProtocolObjectTypesSupportedBitString.setAccumulator(false);
		bacnetProtocolObjectTypesSupportedBitString.setPulseConverter(false);
		bacnetProtocolObjectTypesSupportedBitString.setEventLog(false);
		bacnetProtocolObjectTypesSupportedBitString.setGlobalGroup(false);
		bacnetProtocolObjectTypesSupportedBitString.setTrendLogMultiple(false);
		bacnetProtocolObjectTypesSupportedBitString.setLoadControl(false);
		bacnetProtocolObjectTypesSupportedBitString.setStructuredView(false);
		bacnetProtocolObjectTypesSupportedBitString.setAccessDoor(false);
		bacnetProtocolObjectTypesSupportedBitString.setTimer(false);
		bacnetProtocolObjectTypesSupportedBitString.setAccessCredential(false);
		bacnetProtocolObjectTypesSupportedBitString.setAccessPoint(false);
		bacnetProtocolObjectTypesSupportedBitString.setAccessRights(false);
		bacnetProtocolObjectTypesSupportedBitString.setAccessUser(false);
		bacnetProtocolObjectTypesSupportedBitString.setAccessZone(false);
		bacnetProtocolObjectTypesSupportedBitString.setCredentialDataInput(false);

		return bacnetProtocolObjectTypesSupportedBitString;
	}

	/**
	 * <pre>
	 * Loytex:
	 * protocol-services-supported: (Bit String) (FTFFFFTTTTFFTFTTTTFFTFFFFFTFTFFFTTTTTFTF)
	 * 
	 * IO-420:
	 * protocol-services-supported: (Bit String) (TFFT TTTT TTFF TFTT TTTF TFFF FFTT FFTF TTTF TFFT)
	 * </pre>
	 * 
	 * @return
	 */
	@SuppressWarnings("unused")
	private BACnetServicesSupportedBitString retrieveLoytecRouterServicesSupported() {

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
//		bacnetServicesSupportedBitString.setReadPropertyConditional(false);
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

//		bacnetServicesSupportedBitString.setAuthenticate();
//		bacnetServicesSupportedBitString.setRequestKey();

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

		return bacnetServicesSupportedBitString;
	}

	/**
	 * <pre>
	 * Loytex:
	 * protocol-services-supported: (Bit String) (FTFFFFTTTTFFTFTTTTFFTFFFFFTFTFFFTTTTTFTF)
	 * 
	 * IO-420:
	 * protocol-services-supported: (Bit String) (TFFT TTTT TTFF TFTT TTTF TFFF FFTT FFTF TTTF TFFT)
	 * </pre>
	 * 
	 * @return
	 */
	private BACnetServicesSupportedBitString retrieveIO420ServicesSupported() {

		final BACnetServicesSupportedBitString bacnetServicesSupportedBitString = new BACnetServicesSupportedBitString();

		bacnetServicesSupportedBitString.setAcknowledgeAlarm(true);
		bacnetServicesSupportedBitString.setConfirmedCOVNotification(false);
		bacnetServicesSupportedBitString.setConfirmedEventNotification(false);

		bacnetServicesSupportedBitString.setGetAlarmSummary(true);
		bacnetServicesSupportedBitString.setGetEnrollmentSummary(true);

		bacnetServicesSupportedBitString.setSubscribeCOV(true);

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
		bacnetServicesSupportedBitString.setConfirmedPrivateTransfer(true);
		bacnetServicesSupportedBitString.setConfirmedTextMessage(false);
		bacnetServicesSupportedBitString.setReinitializeDevice(true);

		bacnetServicesSupportedBitString.setVtOpen(false);
		bacnetServicesSupportedBitString.setVtClose(false);
		bacnetServicesSupportedBitString.setVtData(false);

		bacnetServicesSupportedBitString.setiAm(true);
		bacnetServicesSupportedBitString.setiHave(true);

		bacnetServicesSupportedBitString.setUnconfirmedCOVNotification(false);
		bacnetServicesSupportedBitString.setUnconfirmedEventNotification(false);
		bacnetServicesSupportedBitString.setUnconfirmedPrivateTransfer(true);
		bacnetServicesSupportedBitString.setUnconfirmedTextMessage(false);

		bacnetServicesSupportedBitString.setTimeSynchronization(true);

		bacnetServicesSupportedBitString.setWhoHas(true);
		bacnetServicesSupportedBitString.setWhoIs(true);

		bacnetServicesSupportedBitString.setReadRange(false);
		bacnetServicesSupportedBitString.setUtcTimeSynchronization(true);
		bacnetServicesSupportedBitString.setLifeSafetyOperation(false);
		bacnetServicesSupportedBitString.setSubscribeCOVProperty(false);
		bacnetServicesSupportedBitString.setGetEventInformation(true);

		return bacnetServicesSupportedBitString;
	}

	private Message processReadPropertyMultiple(final Message requestMessage) {

		final VirtualLinkControl virtualLinkControl = new VirtualLinkControl();
		virtualLinkControl.setType(0x81);
		virtualLinkControl.setFunction(0x0A);
		virtualLinkControl.setLength(0x00);

		final NPDU npdu = new NPDU();
		npdu.setVersion(0x01);

		// no additional information
		// this works, if the cp is connected to the device directly via 192.168.2.1
		npdu.setControl(0x00);

		if (NetworkUtils.ADD_ADDITIONAL_NETWORK_INFORMATION) {

			// destination network information
			npdu.setControl(0x20);
			npdu.setDestinationNetworkNumber(302);
			npdu.setDestinationMACLayerAddressLength(3);
			npdu.setDestinationMac(NetworkUtils.DEVICE_MAC_ADDRESS);

			npdu.setDestinationHopCount(255);
		}

		final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
		objectIdentifierServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
		// who are context tag numbers determined???
//		objectIdentifierServiceParameter.setTagNumber(ServiceParameter.BACNET_OBJECT_IDENTIFIER);
		objectIdentifierServiceParameter.setTagNumber(0x00);
		objectIdentifierServiceParameter.setLengthValueType(4);
		objectIdentifierServiceParameter.setObjectType(ObjectIdentifierServiceParameter.OBJECT_TYPE_DEVICE);
		objectIdentifierServiceParameter.setInstanceNumber(NetworkUtils.DEVICE_INSTANCE_NUMBER);

		final APDU apdu = new APDU();
		apdu.setPduType(PDUType.COMPLEX_ACK_PDU);
		apdu.setInvokeId(requestMessage.getApdu().getInvokeId());
		apdu.setServiceChoice(ServiceChoice.READ_PROPERTY_MULTIPLE);
		apdu.setVendorMap(vendorMap);
//		apdu.setObjectIdentifierServiceParameter(objectIdentifierServiceParameter);
		apdu.getServiceParameters().add(objectIdentifierServiceParameter);

		// opening {[1]
		final ServiceParameter openingTagServiceParameter = new ServiceParameter();
		openingTagServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		openingTagServiceParameter.setTagNumber(0x01);
		openingTagServiceParameter.setLengthValueType(ServiceParameter.OPENING_TAG_CODE);
		apdu.getServiceParameters().add(openingTagServiceParameter);
		LOG.info(openingTagServiceParameter);

		final List<ServiceParameter> serviceParameters = requestMessage.getApdu().getServiceParameters();
		if (CollectionUtils.isEmpty(serviceParameters)) {
			LOG.warn("No service parameters in readPropertyMultiple request");
		}

		boolean withinRequestedProperties = false;
		for (final ServiceParameter serviceParameter : serviceParameters) {

//			LOG.info(serviceParameter);

			// opening tag
			if (serviceParameter.getLengthValueType() == 0x06) {
				withinRequestedProperties = true;
				continue;
			}

			// closing tag
			if (serviceParameter.getLengthValueType() == 0x07) {
				withinRequestedProperties = false;
				continue;
			}

			if (!withinRequestedProperties) {
				continue;
			}

			// 'all' service property
			if (serviceParameter.getPayload()[0] == DeviceProperty.ALL) {

				for (final DeviceProperty deviceProperty : device.getProperties().values()) {

					LOG.info("Adding ServiceParameter for DeviceProperty: " + deviceProperty + " ...");

					// add the property identifier
					final ServiceParameter propertyIdentifierServiceParameter = new ServiceParameter();
					propertyIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
					propertyIdentifierServiceParameter.setTagNumber(2);
					propertyIdentifierServiceParameter.setLengthValueType(1);
					propertyIdentifierServiceParameter
							.setPayload(new byte[] { (byte) deviceProperty.getPropertyKey() });
					apdu.getServiceParameters().add(propertyIdentifierServiceParameter);
					LOG.info(propertyIdentifierServiceParameter);

					// add the property value
					addPropertyValue(apdu, deviceProperty);

					LOG.info("Adding ServiceParameter for DeviceProperty: " + deviceProperty + " done.");
				}

			} else if (serviceParameter.getPayload()[0] == DeviceProperty.SYSTEM_STATUS) {

				// add the service property identifier
				apdu.getServiceParameters().add(serviceParameter);
				addPropertyValue(apdu, device.getProperties().get(DeviceProperty.SYSTEM_STATUS));

			} else {

				throw new RuntimeException("NotImplemented! " + serviceParameter.getPayload()[0]);

			}
		}

		// closing }[1]
		final ServiceParameter closingTagServiceParameter = new ServiceParameter();
		closingTagServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		closingTagServiceParameter.setTagNumber(0x01);
		closingTagServiceParameter.setLengthValueType(ServiceParameter.CLOSING_TAG_CODE);
		apdu.getServiceParameters().add(closingTagServiceParameter);
		LOG.info(closingTagServiceParameter);

		final DefaultMessage result = new DefaultMessage();
		result.setVirtualLinkControl(virtualLinkControl);
		result.setNpdu(npdu);
		result.setApdu(apdu);

		LOG.info("All service parameters ...");

		if (CollectionUtils.isNotEmpty(apdu.getServiceParameters())) {

			for (final ServiceParameter serviceParameter : apdu.getServiceParameters()) {

				LOG.info(serviceParameter);
			}
		}

		LOG.info("All service parameters done.");

		virtualLinkControl.setLength(result.getDataLength());

//		final byte[] bytes = result.getBytes();
//		LOG.info(Utils.byteArrayToStringNoPrefix(bytes));

		return result;
	}

	private void addPropertyValue(final APDU apdu, final DeviceProperty deviceProperty) {

		// opening tag {[4]
		final ServiceParameter openingTagServiceParameter = new ServiceParameter();
		openingTagServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		openingTagServiceParameter.setTagNumber(0x04);
		openingTagServiceParameter.setLengthValueType(ServiceParameter.OPENING_TAG_CODE);
		apdu.getServiceParameters().add(openingTagServiceParameter);
		LOG.info(openingTagServiceParameter);

		final ServiceParameter valueServiceParameter = new ServiceParameter();
		valueServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
		valueServiceParameter.setTagNumber(deviceProperty.getMessageType().getValue());
		valueServiceParameter.setLengthValueType(deviceProperty.getValue().length);
		valueServiceParameter.setPayload(deviceProperty.getValue());
		apdu.getServiceParameters().add(valueServiceParameter);
		LOG.info(valueServiceParameter);

		// closing tag }[4]
		final ServiceParameter closingTagServiceParameter = new ServiceParameter();
		closingTagServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		closingTagServiceParameter.setTagNumber(0x04);
		closingTagServiceParameter.setLengthValueType(ServiceParameter.CLOSING_TAG_CODE);
		apdu.getServiceParameters().add(closingTagServiceParameter);
		LOG.info(closingTagServiceParameter);
	}

	private Message processSystemStatusMessage(final Message message) {

		final DefaultMessage defaultMessage = new DefaultMessage(message);

		// TODO: copy message.VirtualLinkControl

		// TODO: copy message.NPDU including all service parameters
		// TODO: change NPDU.control to contain a destination specifier
		defaultMessage.getNpdu().setControl(0x20);
		defaultMessage.getNpdu().setDestinationNetworkNumber(302);
		defaultMessage.getNpdu().setDestinationMACLayerAddressLength(3);
		defaultMessage.getNpdu().setDestinationMac(NetworkUtils.DEVICE_MAC_ADDRESS);
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

		final APDU apdu = message.getApdu();

		final ServiceParameter objectIdentifierServiceParameter = apdu.getServiceParameters().get(0);

		final ServiceParameter vendorServiceParameter = apdu.getServiceParameters().get(3);
		final byte[] payload = vendorServiceParameter.getPayload();

		int vendorId = -1;
		if (payload.length == 1) {
			vendorId = payload[0] & 0xFF;
		} else if (payload.length == 2) {
			final ByteBuffer byteBuffer = ByteBuffer.wrap(payload);
			vendorId = byteBuffer.getShort();
		} else if (payload.length == 4) {
			final ByteBuffer byteBuffer = ByteBuffer.wrap(payload);
			vendorId = byteBuffer.getInt();
		}

		LOG.info(">>> processIAMMessage: InstanceNumber: {} VendorId: {} VendorName: {}",
				objectIdentifierServiceParameter.getInstanceNumber(), vendorId, vendorMap.get(vendorId));

		return null;
	}

	public Map<Integer, String> getVendorMap() {
		return vendorMap;
	}

	public void setVendorMap(final Map<Integer, String> vendorMap) {
		this.vendorMap = vendorMap;
		messageFactory.setVendorMap(vendorMap);
	}

}
