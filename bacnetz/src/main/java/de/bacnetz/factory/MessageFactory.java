package de.bacnetz.factory;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.bacnet.factory.Factory;
import de.bacnet.factory.MessageType;
import de.bacnetz.common.Utils;
import de.bacnetz.common.utils.NetworkUtils;
import de.bacnetz.controller.DefaultMessage;
import de.bacnetz.controller.Message;
import de.bacnetz.stack.APDU;
import de.bacnetz.stack.NPDU;
import de.bacnetz.stack.ObjectIdentifierServiceParameter;
import de.bacnetz.stack.PDUType;
import de.bacnetz.stack.ServiceChoice;
import de.bacnetz.stack.ServiceParameter;
import de.bacnetz.stack.TagClass;
import de.bacnetz.stack.VirtualLinkControl;

public class MessageFactory implements Factory<Message> {

	private static final Logger LOG = LogManager.getLogger(MessageFactory.class);

	private Map<Integer, String> vendorMap = new HashMap<>();

	@Override
	public Message create(final Object... args) {

		int index = 0;
		int deviceInstanceNumber = -1;
		int invokeId = -1;
		int propertyKey = -1;
		byte[] payload = null;

		final MessageType messageType = (MessageType) args[index++];

		switch (messageType) {

		case WHO_IS:
			if (args.length > 1) {
				final int lowerBound = (int) args[index++];
				final int upperBound = (int) args[index++];
				return whoIsMessage(lowerBound, upperBound);
			} else {
				return whoIsMessage();
			}

		case INTEGER_PROPERTY:
			deviceInstanceNumber = (int) args[index++];
			invokeId = (int) args[index++];
			propertyKey = (int) args[index++];
			payload = (byte[]) args[index++];
			return returnIntegerProperty(deviceInstanceNumber, invokeId, propertyKey, payload);

		case BOOLEAN_PROPERTY:
			deviceInstanceNumber = (int) args[index++];
			invokeId = (int) args[index++];
			propertyKey = (int) args[index++];
			payload = (byte[]) args[index++];
			return returnBooleanProperty(deviceInstanceNumber, invokeId, propertyKey, payload);

		case ENUMERATED:
			deviceInstanceNumber = (int) args[index++];
			invokeId = (int) args[index++];
			propertyKey = (int) args[index++];
			payload = (byte[]) args[index++];
			return returnEnumeratedProperty(deviceInstanceNumber, invokeId, propertyKey, payload);

		case SINGED_INTEGER_TWOS_COMPLEMENT_NOTATION_PROPERTY:
			deviceInstanceNumber = (int) args[index++];
			invokeId = (int) args[index++];
			propertyKey = (int) args[index++];
			payload = (byte[]) args[index++];
			return returnSignedIntegerTwosCommplementNotationProperty(deviceInstanceNumber, invokeId, propertyKey,
					payload);

		default:
			throw new RuntimeException("Unkown message type: " + messageType.name());
		}
	}

	private Message returnSignedIntegerTwosCommplementNotationProperty(final int deviceInstanceNumber,
			final int invokeId, final int propertyKey, final byte[] payload) {

		final VirtualLinkControl virtualLinkControl = new VirtualLinkControl();
		virtualLinkControl.setType(0x81);
		virtualLinkControl.setFunction(0x0A);
		virtualLinkControl.setLength(0x00);

		final NPDU npdu = new NPDU();
		npdu.setVersion(0x01);
//		npdu.setControl(0x00);
//		npdu.setSourceNetworkAddress(requestMessage.getNpdu().getDestinationNetworkNumber());
//		npdu.setDestinationMACLayerAddressLength(requestMessage.getNpdu().getDestinationMACLayerAddressLength());
//		npdu.setDestinationMac(requestMessage.getNpdu().getDestinationMac());

		// no additional information
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
		propertyIdentifierServiceParameter.setPayload(new byte[] { (byte) propertyKey });

		final ServiceParameter openingTagServiceParameter = new ServiceParameter();
		openingTagServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		openingTagServiceParameter.setTagNumber(0x03);
		openingTagServiceParameter.setLengthValueType(ServiceParameter.OPENING_TAG_CODE);

		final ServiceParameter valueServiceParameter = new ServiceParameter();
		valueServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
		valueServiceParameter.setTagNumber(ServiceParameter.SIGNED_INTEGER_TWOS_COMMPLEMENT_NOTATION);
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

	private Message whoIsMessage() {

		final VirtualLinkControl virtualLinkControl = new VirtualLinkControl();
		// Type: BACnet/IP (Annex J) (0x81)
		virtualLinkControl.setType(0x81);
		// Function: Original-Broadcast-NPDU (0x0b)
		virtualLinkControl.setFunction(0x0B);
		// BVLC-Length: 4 of 22 bytes BACnet packet length
		virtualLinkControl.setLength(0x00);

		final NPDU npdu = new NPDU();
		npdu.setVersion(0x01);
		npdu.setControl(0x00);

		final APDU apdu = new APDU();
		apdu.setPduType(PDUType.UNCONFIRMED_SERVICE_REQUEST_PDU);
		apdu.setServiceChoice(ServiceChoice.WHO_IS);

		final DefaultMessage result = new DefaultMessage();
		result.setVirtualLinkControl(virtualLinkControl);
		result.setNpdu(npdu);
		result.setApdu(apdu);

		virtualLinkControl.setLength(result.getDataLength());

		return result;
	}

	private Message whoIsMessage(final int lowerBound, final int upperBound) {

		final VirtualLinkControl virtualLinkControl = new VirtualLinkControl();
		// Type: BACnet/IP (Annex J) (0x81)
		virtualLinkControl.setType(0x81);
		// Function: Original-Broadcast-NPDU (0x0b)
		virtualLinkControl.setFunction(0x0B);
		// BVLC-Length: 4 of 22 bytes BACnet packet length
		virtualLinkControl.setLength(0x00);

		final NPDU npdu = new NPDU();
		npdu.setVersion(0x01);
		npdu.setControl(0x28);
		npdu.setDestinationNetworkNumber(0xFFFF);
		npdu.setDestinationMACLayerAddressLength(0x00);
		npdu.setSourceMacLayerAddressLength(0x03);
		npdu.setSourceMac(NetworkUtils.DEVICE_MAC_ADDRESS);
		npdu.setDestinationHopCount(0xFE);

		final ServiceParameter lowerBoundServiceParameter = new ServiceParameter();
		lowerBoundServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		lowerBoundServiceParameter.setTagNumber(0x00);
		lowerBoundServiceParameter.setLengthValueType(0x01);
		lowerBoundServiceParameter.setPayload(new byte[] { (byte) lowerBound });

		final ServiceParameter upperBoundServiceParameter = new ServiceParameter();
		upperBoundServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		upperBoundServiceParameter.setTagNumber(0x01);
		upperBoundServiceParameter.setLengthValueType(0x01);
		upperBoundServiceParameter.setPayload(new byte[] { (byte) upperBound });

		final APDU apdu = new APDU();
		apdu.setPduType(PDUType.UNCONFIRMED_SERVICE_REQUEST_PDU);
		apdu.setServiceChoice(ServiceChoice.WHO_IS);
		apdu.getServiceParameters().add(lowerBoundServiceParameter);
		apdu.getServiceParameters().add(upperBoundServiceParameter);

		final DefaultMessage result = new DefaultMessage();
		result.setVirtualLinkControl(virtualLinkControl);
		result.setNpdu(npdu);
		result.setApdu(apdu);

		virtualLinkControl.setLength(result.getDataLength());

		return result;
	}

	private Message returnEnumeratedProperty(final int deviceInstanceNumber, final int invokeId, final int propertyKey,
			final byte[] payload) {

		final VirtualLinkControl virtualLinkControl = new VirtualLinkControl();
		virtualLinkControl.setType(0x81);
		virtualLinkControl.setFunction(0x0A);
		virtualLinkControl.setLength(0x00);

		final NPDU npdu = new NPDU();
		npdu.setVersion(0x01);
//		npdu.setControl(0x00);

//		npdu.setControl(0x20);
//		npdu.setSourceNetworkAddress(requestMessage.getNpdu().getDestinationNetworkNumber());
//		npdu.setDestinationMACLayerAddressLength(requestMessage.getNpdu().getDestinationMACLayerAddressLength());
//		npdu.setDestinationMac(requestMessage.getNpdu().getDestinationMac());

		// no additional information
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
		propertyIdentifierServiceParameter.setPayload(new byte[] { (byte) propertyKey });

		final ServiceParameter openingTagServiceParameter = new ServiceParameter();
		openingTagServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		openingTagServiceParameter.setTagNumber(0x03);
		openingTagServiceParameter.setLengthValueType(ServiceParameter.OPENING_TAG_CODE);

		final ServiceParameter valueServiceParameter = new ServiceParameter();
		valueServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
		valueServiceParameter.setTagNumber(ServiceParameter.ENUMERATED_CODE);
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

	private Message returnBooleanProperty(final int deviceInstanceNumber, final int invokeId, final int propertyKey,
			final byte[] payload) {

		final VirtualLinkControl virtualLinkControl = new VirtualLinkControl();
		virtualLinkControl.setType(0x81);
		virtualLinkControl.setFunction(0x0A);
		virtualLinkControl.setLength(0x00);

		final NPDU npdu = new NPDU();
		npdu.setVersion(0x01);
//		npdu.setControl(0x00);
//		npdu.setSourceNetworkAddress(requestMessage.getNpdu().getDestinationNetworkNumber());
//		npdu.setDestinationMACLayerAddressLength(requestMessage.getNpdu().getDestinationMACLayerAddressLength());
//		npdu.setDestinationMac(requestMessage.getNpdu().getDestinationMac());

		// no additional information
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
		propertyIdentifierServiceParameter.setPayload(new byte[] { (byte) propertyKey });

		final ServiceParameter openingTagServiceParameter = new ServiceParameter();
		openingTagServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
		openingTagServiceParameter.setTagNumber(0x03);
		openingTagServiceParameter.setLengthValueType(ServiceParameter.OPENING_TAG_CODE);

		final ServiceParameter valueServiceParameter = new ServiceParameter();
		valueServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
		valueServiceParameter.setTagNumber(ServiceParameter.BOOLEAN_CODE);
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

	private Message returnIntegerProperty(final int deviceInstanceNumber, final int invokeId, final int propertyKey,
			final byte[] payload) {

		final VirtualLinkControl virtualLinkControl = new VirtualLinkControl();
		virtualLinkControl.setType(0x81);
		virtualLinkControl.setFunction(0x0A);
		virtualLinkControl.setLength(0x00);

		final NPDU npdu = new NPDU();
		npdu.setVersion(0x01);
//		npdu.setControl(0x00);
//		npdu.setSourceNetworkAddress(requestMessage.getNpdu().getDestinationNetworkNumber());
//		npdu.setDestinationMACLayerAddressLength(requestMessage.getNpdu().getDestinationMACLayerAddressLength());
//		npdu.setDestinationMac(requestMessage.getNpdu().getDestinationMac());

		// no additional information
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
		propertyIdentifierServiceParameter.setPayload(new byte[] { (byte) propertyKey });

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
