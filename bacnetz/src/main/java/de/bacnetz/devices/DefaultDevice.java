package de.bacnetz.devices;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.bacnetz.common.utils.BACnetUtils;
import de.bacnetz.common.utils.NetworkUtils;
import de.bacnetz.controller.DefaultMessage;
import de.bacnetz.controller.Message;
import de.bacnetz.factory.DefaultMessageFactory;
import de.bacnetz.factory.MessageFactory;
import de.bacnetz.factory.MessageType;
import de.bacnetz.stack.APDU;
import de.bacnetz.stack.BACnetProtocolObjectTypesSupportedBitString;
import de.bacnetz.stack.BACnetServicesSupportedBitString;
import de.bacnetz.stack.BaseBitString;
import de.bacnetz.stack.NPDU;
import de.bacnetz.stack.ObjectIdentifierServiceParameter;
import de.bacnetz.stack.PDUType;
import de.bacnetz.stack.ServiceParameter;
import de.bacnetz.stack.StatusFlagsBitString;
import de.bacnetz.stack.TagClass;
import de.bacnetz.stack.UnconfirmedServiceChoice;
import de.bacnetz.stack.VirtualLinkControl;

public class DefaultDevice implements Device {

    private static final Logger LOG = LogManager.getLogger(DefaultDevice.class);

    private final Map<Integer, DeviceProperty<?>> properties = new HashMap<>();

    private final Collection<Device> children = new ArrayList<>();

    private int id;

    private ObjectType objectType;

    private String name;

    private String description;

    private String location;

    private final MessageFactory messageFactory = new DefaultMessageFactory();

    private Map<Integer, String> vendorMap = new HashMap<>();

    private final AtomicInteger invokeId = new AtomicInteger(0);

    private Object presentValue;

    private boolean outOfService;

    private final List<String> states = new ArrayList<>();

    private String firmwareRevision;

    /**
     * ctor
     */
    public DefaultDevice() {

    }

    @Override
    public Device findDevice(final ServiceParameter serviceParameter) {

        final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = (ObjectIdentifierServiceParameter) serviceParameter;

        if (objectIdentifierServiceParameter.getObjectType() == objectType
                && objectIdentifierServiceParameter.getInstanceNumber() == id) {
            return this;
        }

        if (CollectionUtils.isEmpty(children)) {
            return null;
        }

        // find final the object in final the list of final objects by instance final
        // type and final instance id
        final Optional<Device> childDevice = getChildDevices().stream()
                .filter(d -> d.getObjectType() == objectIdentifierServiceParameter.getObjectType()
                        && d.getId() == objectIdentifierServiceParameter.getInstanceNumber())
                .findFirst();

        return childDevice.isPresent() ? childDevice.get() : null;
    }

    @Override
    public ServiceParameter getObjectIdentifierServiceParameter() {

        switch (objectType) {

        case DEVICE:
            return createDeviceServiceParameter();

        case NOTIFICATION_CLASS:
            return createNotificationClassServiceParameter();

        case BINARY_INPUT:
            return createBinaryInputServiceParameter();

        case MULTI_STATE_VALUE:
            return createMultiStateValueServiceParameter();

        default:
            throw new RuntimeException("Unknown objectType: " + objectType);
        }
    }

    private ServiceParameter createDeviceServiceParameter() {

        final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
        objectIdentifierServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
        objectIdentifierServiceParameter.setTagNumber(ServiceParameter.BACNET_OBJECT_IDENTIFIER);
        objectIdentifierServiceParameter.setLengthValueType(0x04);
        objectIdentifierServiceParameter.setObjectType(objectType);
        objectIdentifierServiceParameter.setInstanceNumber(id);

        return objectIdentifierServiceParameter;
    }

    private ServiceParameter createNotificationClassServiceParameter() {

        final ObjectIdentifierServiceParameter notificationServiceParameter = new ObjectIdentifierServiceParameter();
        notificationServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
        notificationServiceParameter.setTagNumber(ServiceParameter.BACNET_OBJECT_IDENTIFIER);
        notificationServiceParameter.setLengthValueType(0x04);
        notificationServiceParameter.setObjectType(ObjectType.NOTIFICATION_CLASS);
        notificationServiceParameter.setInstanceNumber(id);

        return notificationServiceParameter;
    }

    private ServiceParameter createBinaryInputServiceParameter() {

        final ObjectIdentifierServiceParameter binaryInputServiceParameter = new ObjectIdentifierServiceParameter();
        binaryInputServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
        binaryInputServiceParameter.setTagNumber(ServiceParameter.BACNET_OBJECT_IDENTIFIER);
        binaryInputServiceParameter.setLengthValueType(0x04);
        binaryInputServiceParameter.setObjectType(ObjectType.BINARY_INPUT);
        binaryInputServiceParameter.setInstanceNumber(id);

        return binaryInputServiceParameter;
    }

    private ServiceParameter createMultiStateValueServiceParameter() {

        final ObjectIdentifierServiceParameter multiStateValueServiceParameter = new ObjectIdentifierServiceParameter();
        multiStateValueServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
        multiStateValueServiceParameter.setTagNumber(ServiceParameter.BACNET_OBJECT_IDENTIFIER);
        multiStateValueServiceParameter.setLengthValueType(0x04);
        multiStateValueServiceParameter.setObjectType(ObjectType.MULTI_STATE_VALUE);
        multiStateValueServiceParameter.setInstanceNumber(id);

        return multiStateValueServiceParameter;
    }

    @Override
    public Message getPropertyValue(final Message requestMessage, final int propertyIdentifierCode) {

        LOG.trace("<<< READ_PROP: {} ({}) from device {}", DevicePropertyType.getByCode(propertyIdentifierCode).name(),
                propertyIdentifierCode, getObjectIdentifierServiceParameter().toString());

        final DeviceProperty<?> deviceProperty = getProperties().get(propertyIdentifierCode);
        if (deviceProperty == null) {
            LOG.info("error");

            final int errorClass = 0x02;
            final int errorCode = 0x20;
            return messageFactory.createErrorMessage(requestMessage, errorClass, errorCode);
        }

        if ((propertyIdentifierCode == DevicePropertyType.PRESENT_VALUE.getCode()) && (getId() == 1)) {
            LOG.trace("DEBUG");
        }

        return messageFactory.create(deviceProperty, this, requestMessage);
    }

    // TODO: add all these properties over.
//		else {
//
//			final String msg;
//
//			switch (propertyIdentifierCode) {

//            // 0x1C = 28d
//            case 0x1C:
//                LOG.trace("<<< READ_PROP: description ({})", propertyIdentifierCode);
//                return processStringProperty(propertyIdentifierCode, requestMessage, NetworkUtils.OBJECT_DESCRIPTION);

//            // 0x1E = 30d
//            case 0x1E:
//                LOG.trace("<<< READ_PROP: device-address-binding ({})", propertyIdentifierCode);
//                return processAddressBindingProperty(propertyIdentifierCode, requestMessage);
//
//            // 0x2C = 44d
//            case 0x2C:
//                LOG.trace("<<< READ_PROP: firmware-revision ({})", propertyIdentifierCode);
//                return processFirmwareRevisionProperty(propertyIdentifierCode, requestMessage);
//
//            // 0x38 = 56d
//            case 0x38:
//                LOG.trace("<<< READ_PROP: local-date ({})", propertyIdentifierCode);
//                return processTimeOfDeviceRestartProperty(propertyIdentifierCode, requestMessage, true, false,
//                        new Date());
//
//            // 0x39 = 57d
//            case 0x39:
//                LOG.trace("<<< READ_PROP: local-time ({})", propertyIdentifierCode);
//                return processTimeOfDeviceRestartProperty(propertyIdentifierCode, requestMessage, false, true,
//                        new Date());
//
//            // 0x3A = 58d
//            case 0x3A:
//                LOG.trace("<<< READ_PROP: location ({})", propertyIdentifierCode);
//                return processStringProperty(propertyIdentifierCode, requestMessage, NetworkUtils.OBJECT_LOCATION);
//
//            // 0x46 = 70d
//            case 0x46:
//                LOG.trace("<<< READ_PROP: model-name ({})", propertyIdentifierCode);
//                return processStringProperty(propertyIdentifierCode, requestMessage, NetworkUtils.MODEL_NAME);
//
//            // 0x4B = 75d
//            case 0x4B:
//                LOG.trace("<<< READ_PROP: object-identifier ({})", propertyIdentifierCode);
//                return processStringProperty(propertyIdentifierCode, requestMessage, objectType + ":" + id);
//
//            // 0x4c = 76d (0x4c = 76d) object list
//            case 0x4c:
//                LOG.trace("<<< READ_PROP: object-list ({})", propertyIdentifierCode);
//                return processObjectListRequest(propertyIdentifierCode, requestMessage);
//
//            // 0x4d = 77d (0x4d = 77d) object-name
//            case 0x4d:
//                LOG.trace("<<< READ_PROP: object-name ({})", propertyIdentifierCode);
//                return processObjectNameProperty(propertyIdentifierCode, requestMessage);
//
//            // 0x55 = 85d, present-value
//            case 0x55:
//                LOG.info("<<< READ_PROP: Device: {}, present-value ({})", name + ":" + objectType + ":" + id,
//                        propertyIdentifierCode);
//                return processPresentValueProperty(propertyIdentifierCode, requestMessage);
//
//            // 0x60 = 96d - protocol-services-supported
//            //
//            // H.5.2.13 Protocol_Object_Types_Supported
//            // This property indicates the BACnet protocol object types supported by this
//            // device. See 12.10.15. The protocol object
//            // types supported shall be at least Analog Input, Analog Output, Analog Value,
//            // Binary Input, Binary Output, and Binary
//            // Value.
//            case 0x60:
//                LOG.trace("<<< READ_PROP: protocol-object-types-supported Property ({})", propertyIdentifierCode);
//                return processProtocolObjectTypesSupportedServicesProperty(propertyIdentifierCode, requestMessage);
//
//            // Supported Services Property
//            // 0x61 = 97d
//            case 0x61:
//                LOG.trace("<<< READ_PROP: supported-services-property ({})", propertyIdentifierCode);
//                return processSupportedServicesProperty(propertyIdentifierCode, requestMessage);
//
//            // 0x6E = 110d state-text
//            case 0x6E:
//                LOG.trace("<<< READ_PROP: state-text ({})", propertyIdentifierCode);
//                return processStateTextProperty(propertyIdentifierCode, requestMessage);
//
//            // 0x6F = 111d, status-flags
//            // see bacnet_device25_object_list.pcapng - message 11702
//            case 0x6F:
//                LOG.trace("<<< READ_PROP: status-flags ({})", propertyIdentifierCode);
//                return processStatusFlagsProperty(propertyIdentifierCode, requestMessage);
//
//            // 0x74 = 116d
//            case 0x74:
//                LOG.trace("<<< READ_PROP: time-synchronization-recipients ({})", propertyIdentifierCode);
//                return processTimeSynchronizationRecipientsProperty(propertyIdentifierCode, requestMessage);
//
//            // 0x79 = 121d
//            case 0x79:
//                LOG.trace("<<< READ_PROP: vendor-name ({})", propertyIdentifierCode);
//                return processStringProperty(propertyIdentifierCode, requestMessage, NetworkUtils.VENDOR_NAME);
//
//            // 0x98 = 152d
//            case 0x98:
//                LOG.trace("<<< READ_PROP: active-cov-subscriptions ({})", propertyIdentifierCode);
//                return processActiveCovSubscriptionsProperty(propertyIdentifierCode, requestMessage);
//
//            // 0x9D = 157d
//            case 0x9D:
//                LOG.trace("<<< READ_PROP: last-restore-time ({})", propertyIdentifierCode);
//                return processTimeOfDeviceRestartProperty(propertyIdentifierCode, requestMessage, false, true,
//                        new Date());
//
//            // 0xAA = 170d
//            case 0xAA:
//                LOG.trace("<<< READ_PROP: manual-slave-address-binding ({})", propertyIdentifierCode);
//                return processAddressBindingProperty(propertyIdentifierCode, requestMessage);
//
//            // 0xAB = 171d
//            case 0xAB:
//                LOG.trace("<<< READ_PROP: slave-address-binding ({})", propertyIdentifierCode);
//                return processAddressBindingProperty(propertyIdentifierCode, requestMessage);
//
//            // 0xCA = 202d
//            case 0xCA:
//                LOG.trace("<<< READ_PROP: restart notification recipients ({})", propertyIdentifierCode);
//                return processRestartNotificationRecipientsProperty(propertyIdentifierCode, requestMessage);
//
//            // 0xCB = 203d
//            case 0xCB:
//                LOG.trace("<<< READ_PROP: time-of-device-restart ({})", propertyIdentifierCode);
//                return processTimeOfDeviceRestartProperty(propertyIdentifierCode, requestMessage, true, true,
//                        new Date());
//
//            // 0xCE = 206d
//            case 0xCE:
//                LOG.trace("<<< READ_PROP: UTC-time-synchronization-recipients ({})", propertyIdentifierCode);
//                return processUTCTimeSynchronizationRecipientsProperty(propertyIdentifierCode, requestMessage);
//
//            // 0x0173 = 371d property-list
//            case 0x0173:
//                LOG.trace("<<< READ_PROP: property list ({})", propertyIdentifierCode);
//                return processPropertyListProperty(propertyIdentifierCode, requestMessage);

//			default:
//				msg = "Unknown property! PropertyIdentifier = " + propertyIdentifierCode + " property: "
//						+ DevicePropertyType.getByCode(propertyIdentifierCode).getName();
//				LOG.error(msg);
//
//				return error(requestMessage.getApdu().getInvokeId());
//			}
//		}
//	}

    private Message processStateTextProperty(final int propertyIdentifierCode, final Message requestMessage) {

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
            npdu.setDestinationNetworkNumber(NetworkUtils.DESTINATION_NETWORK_NUMBER);
            npdu.setDestinationMACLayerAddressLength(3);
            npdu.setDestinationMac(NetworkUtils.DEVICE_MAC_ADDRESS);

            npdu.setDestinationHopCount(255);
        }

        final ObjectIdentifierServiceParameter outwardObjectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
        outwardObjectIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        outwardObjectIdentifierServiceParameter.setTagNumber(0x00);
        outwardObjectIdentifierServiceParameter.setLengthValueType(4);
        outwardObjectIdentifierServiceParameter.setObjectType(objectType);
        outwardObjectIdentifierServiceParameter.setInstanceNumber(id);

        final ServiceParameter propertyIdentifierServiceParameter = new ServiceParameter();
        propertyIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        propertyIdentifierServiceParameter.setTagNumber(0x01);
        propertyIdentifierServiceParameter.setLengthValueType(0x01);
        propertyIdentifierServiceParameter.setPayload(new byte[] { (byte) propertyIdentifierCode });

        final APDU apdu = new APDU();
        apdu.setPduType(PDUType.COMPLEX_ACK_PDU);
        apdu.setInvokeId(requestMessage.getApdu().getInvokeId());
        apdu.setUnconfirmedServiceChoice(UnconfirmedServiceChoice.READ_PROPERTY);
        apdu.setVendorMap(vendorMap);
        apdu.getServiceParameters().add(outwardObjectIdentifierServiceParameter);
        apdu.getServiceParameters().add(propertyIdentifierServiceParameter);

        // {[4]
        final ServiceParameter openingTagServiceParameter = new ServiceParameter();
        openingTagServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        openingTagServiceParameter.setTagNumber(0x04);
        openingTagServiceParameter.setLengthValueType(ServiceParameter.OPENING_TAG_CODE);
        apdu.getServiceParameters().add(openingTagServiceParameter);

        ServiceParameter stringServiceParameter = new ServiceParameter();
        stringServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
        stringServiceParameter.setTagNumber(ServiceParameter.APPLICATION_TAG_NUMBER_CHARACTER_STRING);
        stringServiceParameter.setLengthValueType(ServiceParameter.EXTENDED_VALUE);
        stringServiceParameter.setPayload(BACnetUtils.retrieveAsString("watchdog"));
        apdu.getServiceParameters().add(stringServiceParameter);

        stringServiceParameter = new ServiceParameter();
        stringServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
        stringServiceParameter.setTagNumber(ServiceParameter.APPLICATION_TAG_NUMBER_CHARACTER_STRING);
        stringServiceParameter.setLengthValueType(ServiceParameter.EXTENDED_VALUE);
        stringServiceParameter.setPayload(BACnetUtils.retrieveAsString("1_door"));
        apdu.getServiceParameters().add(stringServiceParameter);

        // }[4]
        final ServiceParameter closingTagServiceParameter = new ServiceParameter();
        closingTagServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        closingTagServiceParameter.setTagNumber(0x04);
        closingTagServiceParameter.setLengthValueType(ServiceParameter.CLOSING_TAG_CODE);
        apdu.getServiceParameters().add(closingTagServiceParameter);

        final DefaultMessage result = new DefaultMessage();
        result.setVirtualLinkControl(virtualLinkControl);
        result.setNpdu(npdu);
        result.setApdu(apdu);

        virtualLinkControl.setLength(result.getDataLength());

//      final byte[] bytes = result.getBytes();
//      LOG.info(Utils.byteArrayToStringNoPrefix(bytes));

        return result;
    }

    @Override
    public Message processPresentValueProperty(final DeviceProperty<?> deviceProperty, final Message requestMessage) {

        if (StringUtils.equalsIgnoreCase(name, "module_type")) {

            // TODO: add a complex Value Type that stores a value and datatype meta
            // information
            // so that devices can actually store the property values internally and the
            // values do not
            // have to be hardcoded
            return getMessageFactory().create(MessageType.UNSIGNED_INTEGER, this,
                    requestMessage.getApdu().getInvokeId(), deviceProperty.getPropertyKey(),
                    new byte[] { (byte) 0x04 });
        }

        if (StringUtils.equalsIgnoreCase(name, "alarm_type")) {

            // TODO: add a complex Value Type that stores a value and datatype meta
            // information
            // so that devices can actually store the property values internally and the
            // values do not
            // have to be hardcoded
            return getMessageFactory().create(MessageType.UNSIGNED_INTEGER, this,
                    requestMessage.getApdu().getInvokeId(), deviceProperty.getPropertyKey(),
                    new byte[] { (byte) 0x01 });
        }

        return getMessageFactory().create(MessageType.ENUMERATED, this, requestMessage.getApdu().getInvokeId(),
                deviceProperty.getPropertyKey(), new byte[] { (byte) 0x01 });
    }

    private Message processStatusFlagsProperty(final int propertyIdentifierCode, final Message requestMessage) {

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
            npdu.setDestinationNetworkNumber(NetworkUtils.DESTINATION_NETWORK_NUMBER);
            npdu.setDestinationMACLayerAddressLength(3);
            npdu.setDestinationMac(NetworkUtils.DEVICE_MAC_ADDRESS);

            npdu.setDestinationHopCount(255);
        }

        final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
        objectIdentifierServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
        objectIdentifierServiceParameter.setTagNumber(0x00);
        objectIdentifierServiceParameter.setLengthValueType(4);
        objectIdentifierServiceParameter.setObjectType(objectType);
        objectIdentifierServiceParameter.setInstanceNumber(id);

        final ServiceParameter protocolServicesSupportedServiceParameter = new ServiceParameter();
        protocolServicesSupportedServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        protocolServicesSupportedServiceParameter.setTagNumber(0x01);
        protocolServicesSupportedServiceParameter.setLengthValueType(1);
        protocolServicesSupportedServiceParameter.setPayload(new byte[] { (byte) propertyIdentifierCode });

        final ServiceParameter openingTagServiceParameter = new ServiceParameter();
        openingTagServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        openingTagServiceParameter.setTagNumber(0x03);
        openingTagServiceParameter.setLengthValueType(ServiceParameter.OPENING_TAG_CODE);

        final ServiceParameter statusFlagsBitStringServiceParameter = getStatusFlagsServiceParameter();

        final ServiceParameter closingTagServiceParameter = new ServiceParameter();
        closingTagServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        closingTagServiceParameter.setTagNumber(0x03);
        closingTagServiceParameter.setLengthValueType(ServiceParameter.CLOSING_TAG_CODE);

        final APDU apdu = new APDU();
        apdu.setPduType(PDUType.COMPLEX_ACK_PDU);
        apdu.setInvokeId(requestMessage.getApdu().getInvokeId());
        apdu.setUnconfirmedServiceChoice(UnconfirmedServiceChoice.READ_PROPERTY);
        apdu.setVendorMap(vendorMap);
        apdu.getServiceParameters().add(objectIdentifierServiceParameter);
        apdu.getServiceParameters().add(protocolServicesSupportedServiceParameter);
        apdu.getServiceParameters().add(openingTagServiceParameter);
        apdu.getServiceParameters().add(statusFlagsBitStringServiceParameter);
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

    @Override
    public ServiceParameter getStatusFlagsServiceParameter() {

        final ServiceParameter protocolServicesSupportedBitStringServiceParameter = new ServiceParameter();
        protocolServicesSupportedBitStringServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
        protocolServicesSupportedBitStringServiceParameter
                .setTagNumber(ServiceParameter.APPLICATION_TAG_NUMBER_BIT_STRING);
        protocolServicesSupportedBitStringServiceParameter.setLengthValueType(0x02);
        protocolServicesSupportedBitStringServiceParameter.setPayload(getStatusFlagsPayload());

        return protocolServicesSupportedBitStringServiceParameter;
    }

    private byte[] getStatusFlagsPayload() {

        final BaseBitString bitString = retrieveStatusFlags();
        final BitSet bitSet = bitString.getBitSet();
        final byte[] bitSetByteArray = bitSet.toByteArray();

        // this is the result payload
        final byte[] result = new byte[2];

        // unused bits
        result[0] = (byte) 0x04;

        // payload
        System.arraycopy(bitSetByteArray, 0, result, 1, bitSetByteArray.length);

//		LOG.trace(Utils.byteArrayToStringNoPrefix(result));

        return result;
    }

    private StatusFlagsBitString retrieveStatusFlags() {

        final StatusFlagsBitString bitString = new StatusFlagsBitString();
        bitString.setInAlarm(false);
        bitString.setFault(false);
        bitString.setOverridden(false);
        bitString.setOutOfService(false);

        return bitString;
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
            npdu.setDestinationNetworkNumber(NetworkUtils.DESTINATION_NETWORK_NUMBER);
            npdu.setDestinationMACLayerAddressLength(3);
            npdu.setDestinationMac(NetworkUtils.DEVICE_MAC_ADDRESS);

            npdu.setDestinationHopCount(255);
        }

        // this object identifier has to be context specific. I do not know why
        final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
        objectIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        objectIdentifierServiceParameter.setTagNumber(0x00);
        objectIdentifierServiceParameter.setLengthValueType(4);
//        objectIdentifierServiceParameter.setObjectType(ObjectIdentifierServiceParameter.OBJECT_TYPE_DEVICE);
//        objectIdentifierServiceParameter.setInstanceNumber(NetworkUtils.DEVICE_INSTANCE_NUMBER);
        objectIdentifierServiceParameter.setObjectType(objectType);
        objectIdentifierServiceParameter.setInstanceNumber(id);

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
        apdu.setUnconfirmedServiceChoice(UnconfirmedServiceChoice.READ_PROPERTY);
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
            npdu.setDestinationNetworkNumber(NetworkUtils.DESTINATION_NETWORK_NUMBER);
            npdu.setDestinationMACLayerAddressLength(3);
            npdu.setDestinationMac(NetworkUtils.DEVICE_MAC_ADDRESS);

            npdu.setDestinationHopCount(255);
        }

        final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
        objectIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        objectIdentifierServiceParameter.setTagNumber(0x00);
        objectIdentifierServiceParameter.setLengthValueType(4);
        objectIdentifierServiceParameter.setObjectType(objectType);
        objectIdentifierServiceParameter.setInstanceNumber(id);

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
        descriptionServiceParameter.setPayload(BACnetUtils.retrieveAsString(data));

        final ServiceParameter closingTagServiceParameter3 = new ServiceParameter();
        closingTagServiceParameter3.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        closingTagServiceParameter3.setTagNumber(0x03);
        closingTagServiceParameter3.setLengthValueType(ServiceParameter.CLOSING_TAG_CODE);

        final APDU apdu = new APDU();
        apdu.setPduType(PDUType.COMPLEX_ACK_PDU);
        apdu.setInvokeId(requestMessage.getApdu().getInvokeId());
        apdu.setUnconfirmedServiceChoice(UnconfirmedServiceChoice.READ_PROPERTY);
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

//    private Message processDaylightSavingsStatusProperty(final int propertyIdentifierCode,
//            final Message requestMessage) {
//        return messageFactory.create(MessageType.BOOLEAN_PROPERTY, NetworkUtils.DEVICE_INSTANCE_NUMBER,
//                requestMessage.getApdu().getInvokeId(), propertyIdentifierCode, new byte[] { (byte) 0x01 });
//    }
//
//    private Message processApplicationSoftwareVersionProperty(final int propertyIdentifierCode,
//            final Message requestMessage) {
//        return messageFactory.create(MessageType.INTEGER_PROPERTY, NetworkUtils.DEVICE_INSTANCE_NUMBER,
//                requestMessage.getApdu().getInvokeId(), propertyIdentifierCode, new byte[] { (byte) 0x01 });
//    }
//
//    private Message processSystemStatusProperty(final int propertyIdentifierCode, final Message requestMessage) {
//
//        // 0x00 == operational
//        final int systemStatus = 0x00;
//
//        return messageFactory.create(MessageType.ENUMERATED, NetworkUtils.DEVICE_INSTANCE_NUMBER,
//                requestMessage.getApdu().getInvokeId(), propertyIdentifierCode, new byte[] { (byte) systemStatus });
//    }

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
            npdu.setDestinationNetworkNumber(NetworkUtils.DESTINATION_NETWORK_NUMBER);
            npdu.setDestinationMACLayerAddressLength(3);
            npdu.setDestinationMac(NetworkUtils.DEVICE_MAC_ADDRESS);

            npdu.setDestinationHopCount(255);
        }

        // this object identifier has to be context specific. I do not know why
        final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
        objectIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        objectIdentifierServiceParameter.setTagNumber(0x00);
        objectIdentifierServiceParameter.setLengthValueType(4);
//        objectIdentifierServiceParameter.setObjectType(ObjectIdentifierServiceParameter.OBJECT_TYPE_DEVICE);
//        objectIdentifierServiceParameter.setInstanceNumber(NetworkUtils.DEVICE_INSTANCE_NUMBER);
        objectIdentifierServiceParameter.setObjectType(objectType);
        objectIdentifierServiceParameter.setInstanceNumber(id);

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
        apdu.setUnconfirmedServiceChoice(UnconfirmedServiceChoice.READ_PROPERTY);
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
            npdu.setDestinationNetworkNumber(NetworkUtils.DESTINATION_NETWORK_NUMBER);
            npdu.setDestinationMACLayerAddressLength(3);
            npdu.setDestinationMac(NetworkUtils.DEVICE_MAC_ADDRESS);

            npdu.setDestinationHopCount(255);
        }

        // this object identifier has to be context specific. I do not know why
        final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
        objectIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        objectIdentifierServiceParameter.setTagNumber(0x00);
        objectIdentifierServiceParameter.setLengthValueType(4);
//        objectIdentifierServiceParameter.setObjectType(ObjectIdentifierServiceParameter.OBJECT_TYPE_DEVICE);
//        objectIdentifierServiceParameter.setInstanceNumber(NetworkUtils.DEVICE_INSTANCE_NUMBER);
        objectIdentifierServiceParameter.setObjectType(objectType);
        objectIdentifierServiceParameter.setInstanceNumber(id);

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
        apdu.setUnconfirmedServiceChoice(UnconfirmedServiceChoice.READ_PROPERTY);
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
            npdu.setDestinationNetworkNumber(NetworkUtils.DESTINATION_NETWORK_NUMBER);
            npdu.setDestinationMACLayerAddressLength(3);
            npdu.setDestinationMac(NetworkUtils.DEVICE_MAC_ADDRESS);

            npdu.setDestinationHopCount(255);
        }

        // this object identifier has to be context specific. I do not know why
        final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
        objectIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        objectIdentifierServiceParameter.setTagNumber(0x00);
        objectIdentifierServiceParameter.setLengthValueType(4);
//        objectIdentifierServiceParameter.setObjectType(ObjectIdentifierServiceParameter.OBJECT_TYPE_DEVICE);
//        objectIdentifierServiceParameter.setInstanceNumber(NetworkUtils.DEVICE_INSTANCE_NUMBER);
        objectIdentifierServiceParameter.setObjectType(objectType);
        objectIdentifierServiceParameter.setInstanceNumber(id);

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
        apdu.setUnconfirmedServiceChoice(UnconfirmedServiceChoice.READ_PROPERTY);
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
            npdu.setDestinationNetworkNumber(NetworkUtils.DESTINATION_NETWORK_NUMBER);
            npdu.setDestinationMACLayerAddressLength(3);
            npdu.setDestinationMac(NetworkUtils.DEVICE_MAC_ADDRESS);

            npdu.setDestinationHopCount(255);
        }

        // this object identifier has to be context specific. I do not know why
        final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
        objectIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        objectIdentifierServiceParameter.setTagNumber(0x00);
        objectIdentifierServiceParameter.setLengthValueType(4);
        objectIdentifierServiceParameter.setObjectType(objectType);
        objectIdentifierServiceParameter.setInstanceNumber(id);

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
        apdu.setUnconfirmedServiceChoice(UnconfirmedServiceChoice.READ_PROPERTY);
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
            npdu.setDestinationNetworkNumber(NetworkUtils.DESTINATION_NETWORK_NUMBER);
            npdu.setDestinationMACLayerAddressLength(3);
            npdu.setDestinationMac(NetworkUtils.DEVICE_MAC_ADDRESS);

            npdu.setDestinationHopCount(255);
        }

        // this object identifier has to be context specific. I do not know why
        final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
        objectIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        objectIdentifierServiceParameter.setTagNumber(0x00);
        objectIdentifierServiceParameter.setLengthValueType(4);
        objectIdentifierServiceParameter.setObjectType(objectType);
        objectIdentifierServiceParameter.setInstanceNumber(id);

        final ServiceParameter propertyIdentifierServiceParameter = new ServiceParameter();
        propertyIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        propertyIdentifierServiceParameter.setTagNumber(0x01);
        propertyIdentifierServiceParameter.setLengthValueType(0x01);
        propertyIdentifierServiceParameter.setPayload(new byte[] { (byte) propertyIdentifierCode });

        // {[3]
        final ServiceParameter openingTagServiceParameter3 = new ServiceParameter();
        openingTagServiceParameter3.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        openingTagServiceParameter3.setTagNumber(0x03);
        openingTagServiceParameter3.setLengthValueType(ServiceParameter.OPENING_TAG_CODE);

        // {[2]
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

        // }[2]
        final ServiceParameter closingTagServiceParameter2 = new ServiceParameter();
        closingTagServiceParameter2.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        closingTagServiceParameter2.setTagNumber(0x02);
        closingTagServiceParameter2.setLengthValueType(ServiceParameter.CLOSING_TAG_CODE);

        // }[3]
        final ServiceParameter closingTagServiceParameter3 = new ServiceParameter();
        closingTagServiceParameter3.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        closingTagServiceParameter3.setTagNumber(0x03);
        closingTagServiceParameter3.setLengthValueType(ServiceParameter.CLOSING_TAG_CODE);

        final APDU apdu = new APDU();
        apdu.setPduType(PDUType.COMPLEX_ACK_PDU);
        apdu.setInvokeId(requestMessage.getApdu().getInvokeId());
        apdu.setUnconfirmedServiceChoice(UnconfirmedServiceChoice.READ_PROPERTY);
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
            npdu.setDestinationNetworkNumber(NetworkUtils.DESTINATION_NETWORK_NUMBER);
            npdu.setDestinationMACLayerAddressLength(3);
            npdu.setDestinationMac(NetworkUtils.DEVICE_MAC_ADDRESS);

            npdu.setDestinationHopCount(255);
        }

        final ServiceParameter errorClassServiceParameter = new ServiceParameter();
        errorClassServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
        errorClassServiceParameter.setTagNumber(ServiceParameter.ENUMERATED_CODE);
        errorClassServiceParameter.setLengthValueType(0x01);
        errorClassServiceParameter.setPayload(new byte[] { (byte) 0x01 });

        final ServiceParameter errorCodeServiceParameter = new ServiceParameter();
        errorCodeServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
        errorCodeServiceParameter.setTagNumber(ServiceParameter.ENUMERATED_CODE);
        errorCodeServiceParameter.setLengthValueType(0x01);
        // 0x31 = unknown_object
        errorCodeServiceParameter.setPayload(new byte[] { (byte) 0x31 });

        final APDU apdu = new APDU();
        apdu.setPduType(PDUType.ERROR_PDU);
        apdu.setInvokeId(requestMessage.getApdu().getInvokeId());
        apdu.setUnconfirmedServiceChoice(UnconfirmedServiceChoice.READ_PROPERTY);
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

    // Returns a number that has all bits same as n
    // except the k'th bit which is made 0
    int turnOffK(final int n, final int k) {

        // k must be greater than 0
        if (k <= 0)
            return n;

        // Do & of n with a number with all set bits except
        // the k'th bit
        return (n & ~(1 << (k - 1)));
    }

    private Message processObjectNameProperty(final int propertyIdentifierCode, final Message requestMessage) {

        final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = (ObjectIdentifierServiceParameter) requestMessage
                .getApdu().getServiceParameters().get(0);

        final Device targetDevice = findDevice(objectIdentifierServiceParameter);

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
            npdu.setDestinationNetworkNumber(NetworkUtils.DESTINATION_NETWORK_NUMBER);
            npdu.setDestinationMACLayerAddressLength(3);
            npdu.setDestinationMac(NetworkUtils.DEVICE_MAC_ADDRESS);

            npdu.setDestinationHopCount(255);
        }

        final ObjectIdentifierServiceParameter outwardObjectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
        outwardObjectIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        outwardObjectIdentifierServiceParameter.setTagNumber(0x00);
        outwardObjectIdentifierServiceParameter.setLengthValueType(4);
        objectIdentifierServiceParameter.setObjectType(objectType);
        objectIdentifierServiceParameter.setInstanceNumber(id);

        final ServiceParameter propertyIdentifierServiceParameter = new ServiceParameter();
        propertyIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        propertyIdentifierServiceParameter.setTagNumber(0x01);
        propertyIdentifierServiceParameter.setLengthValueType(0x01);
        propertyIdentifierServiceParameter.setPayload(new byte[] { (byte) propertyIdentifierCode });

        final ServiceParameter openingTagServiceParameter = new ServiceParameter();
        openingTagServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        openingTagServiceParameter.setTagNumber(0x03);
        openingTagServiceParameter.setLengthValueType(ServiceParameter.OPENING_TAG_CODE);

        final ServiceParameter objectNameServiceParameter = new ServiceParameter();
        objectNameServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
        objectNameServiceParameter.setTagNumber(ServiceParameter.APPLICATION_TAG_NUMBER_CHARACTER_STRING);
        objectNameServiceParameter.setLengthValueType(ServiceParameter.EXTENDED_VALUE);
        objectNameServiceParameter.setPayload(BACnetUtils.retrieveAsString(targetDevice.getName()));

        final ServiceParameter closingTagServiceParameter = new ServiceParameter();
        closingTagServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        closingTagServiceParameter.setTagNumber(0x03);
        closingTagServiceParameter.setLengthValueType(ServiceParameter.CLOSING_TAG_CODE);

        final APDU apdu = new APDU();
        apdu.setPduType(PDUType.COMPLEX_ACK_PDU);
        apdu.setInvokeId(requestMessage.getApdu().getInvokeId());
        apdu.setUnconfirmedServiceChoice(UnconfirmedServiceChoice.READ_PROPERTY);
        apdu.setVendorMap(vendorMap);
        apdu.getServiceParameters().add(outwardObjectIdentifierServiceParameter);
        apdu.getServiceParameters().add(propertyIdentifierServiceParameter);
        apdu.getServiceParameters().add(openingTagServiceParameter);
        apdu.getServiceParameters().add(objectNameServiceParameter);
        apdu.getServiceParameters().add(closingTagServiceParameter);

        final DefaultMessage result = new DefaultMessage();
        result.setVirtualLinkControl(virtualLinkControl);
        result.setNpdu(npdu);
        result.setApdu(apdu);

        virtualLinkControl.setLength(result.getDataLength());

        return result;
    }

    private Message processProtocolObjectTypesSupportedServicesProperty(final int propertyIdentifierCode,
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
            npdu.setDestinationNetworkNumber(NetworkUtils.DESTINATION_NETWORK_NUMBER);
            npdu.setDestinationMACLayerAddressLength(3);
            npdu.setDestinationMac(NetworkUtils.DEVICE_MAC_ADDRESS);

            npdu.setDestinationHopCount(255);
        }

        final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
        objectIdentifierServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
        objectIdentifierServiceParameter.setTagNumber(0x00);
        objectIdentifierServiceParameter.setLengthValueType(4);
        objectIdentifierServiceParameter.setObjectType(objectType);
        objectIdentifierServiceParameter.setInstanceNumber(id);

        final ServiceParameter protocolObjectTypesSupportedServiceParameter = new ServiceParameter();
        protocolObjectTypesSupportedServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        protocolObjectTypesSupportedServiceParameter.setTagNumber(0x01);
        protocolObjectTypesSupportedServiceParameter.setLengthValueType(1);
        protocolObjectTypesSupportedServiceParameter.setPayload(new byte[] { (byte) propertyIdentifierCode });

        final ServiceParameter openingTagServiceParameter = new ServiceParameter();
        openingTagServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        openingTagServiceParameter.setTagNumber(0x03);
        openingTagServiceParameter.setLengthValueType(ServiceParameter.OPENING_TAG_CODE);

        final ServiceParameter protocolObjectTypesSupportedBitStringServiceParameter = new ServiceParameter();
        protocolObjectTypesSupportedBitStringServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
        protocolObjectTypesSupportedBitStringServiceParameter
                .setTagNumber(ServiceParameter.APPLICATION_TAG_NUMBER_BIT_STRING);
        protocolObjectTypesSupportedBitStringServiceParameter.setLengthValueType(ServiceParameter.EXTENDED_VALUE);
        protocolObjectTypesSupportedBitStringServiceParameter.setPayload(getSupportedProtocolObjectTypesPayload());

        final ServiceParameter closingTagServiceParameter = new ServiceParameter();
        closingTagServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        closingTagServiceParameter.setTagNumber(0x03);
        closingTagServiceParameter.setLengthValueType(ServiceParameter.CLOSING_TAG_CODE);

        final APDU apdu = new APDU();
        apdu.setPduType(PDUType.COMPLEX_ACK_PDU);
        apdu.setInvokeId(requestMessage.getApdu().getInvokeId());
        apdu.setUnconfirmedServiceChoice(UnconfirmedServiceChoice.READ_PROPERTY);
        apdu.setVendorMap(vendorMap);
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

        return result;
    }

//	private Message processStructuredObjectListProperty(final int propertyIdentifierCode,
//			final Message requestMessage) {
//		return error(requestMessage.getApdu().getInvokeId());
//	}

    private Message processFirmwareRevisionProperty(final int propertyIdentifierCode, final Message requestMessage) {
        return messageFactory.create(MessageType.UNSIGNED_INTEGER, this, requestMessage.getApdu().getInvokeId(),
                propertyIdentifierCode, new byte[] { (byte) 0x01 });
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

    @Override
    public BACnetServicesSupportedBitString retrieveServicesSupported() {
        return retrieveIO420ServicesSupported();
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

    @Override
    public int retrieveNextInvokeId() {
        return invokeId.incrementAndGet();
    }

    @Override
    public Map<Integer, String> getVendorMap() {
        return vendorMap;
    }

    @Override
    public void setVendorMap(final Map<Integer, String> vendorMap) {
        this.vendorMap = vendorMap;
    }

    @Override
    public Collection<Device> getChildDevices() {
        return children;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(final int id) {
        this.id = id;
    }

    @Override
    public ObjectType getObjectType() {
        return objectType;
    }

    @Override
    public void setObjectType(final ObjectType objectType) {
        this.objectType = objectType;
    }

    @Override
    public Map<Integer, DeviceProperty<?>> getProperties() {
        return properties;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(final String name) {
        this.name = name;
    }

    public MessageFactory getMessageFactory() {
        return messageFactory;
    }

    @Override
    public Object getPresentValue() {
        return presentValue;
    }

    @Override
    public void setPresentValue(final Object presentValue) {
        LOG.info("Set Present Value: " + presentValue);
        this.presentValue = presentValue;
    }

    @Override
    public boolean isOutOfService() {
        return outOfService;
    }

    @Override
    public void setOutOfService(final boolean outOfService) {
        this.outOfService = outOfService;
    }

    @Override
    public List<String> getStates() {
        return states;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(final String description) {
        this.description = description;
    }

    @Override
    public String getFirmwareRevision() {
        return firmwareRevision;
    }

    @Override
    public void setFirmwareRevision(final String firmwareRevision) {
        this.firmwareRevision = firmwareRevision;
    }

    @Override
    public String getLocation() {
        return location;
    }

    @Override
    public void setLocation(final String location) {
        this.location = location;
    }

}

//// 0x0A = 10d APDU-Segment-Timeout
//case 0x0A:
//	LOG.info("<<< READ_PROP: APDU-Segment-Timeout ({})", propertyIdentifierCode);
//	return processAPDUSegmentTimeoutProperty(propertyIdentifierCode, requestMessage);
//
//// 0x0B = 11d APDU-Timeout
//case 0x0B:
//	LOG.info("<<< READ_PROP: APDU-Timeout ({})", propertyIdentifierCode);
//	return processAPDUTimeoutProperty(propertyIdentifierCode, requestMessage);

//// 0x0C = 12
//case 0x0C:
//	LOG.info("<<< READ_PROP: application-software-version ({})", propertyIdentifierCode);
//	return processApplicationSoftwareVersionProperty(propertyIdentifierCode, requestMessage);

// // 0x14 = 20
// case 0x14:
// LOG.info("<<< READ_PROP: ??? ({})", propertyIdentifierCode);
// return process???Property(propertyIdentifierCode, requestMessage);

//// 0x18 = 24
//case 0x18:
//	LOG.info("<<< READ_PROP: daylight-savings-status ({})", propertyIdentifierCode);
//	return processDaylightSavingsStatusProperty(propertyIdentifierCode, requestMessage);

//// max-apdu-length-accepted
//// 0x3E = 62d
//case 0x3E:
//	LOG.info("<<< READ_PROP: max-apdu-length-accepted ({})", propertyIdentifierCode);
//	return processMaxAPDULengthAcceptedProperty(propertyIdentifierCode, requestMessage);

//// Segmentation supported
//// 0x6B = 107d
//case 0x6B:
//LOG.info("<<< READ_PROP: Segmentation supported ({})", propertyIdentifierCode);
//return processSegmentationSupportedProperty(propertyIdentifierCode, requestMessage);

//// 0x70 = 112
//case 0x70:
//	LOG.info("<<< READ_PROP: system status ({})", propertyIdentifierCode);
//	return processSystemStatusProperty(propertyIdentifierCode, requestMessage);

//// max-segments-accepted
//// 0xA7 = 167d
//case 0xA7:
//LOG.info("<<< READ_PROP: max-segments-accepted ({})", propertyIdentifierCode);
//return processMaxSegmentsAcceptedProperty(propertyIdentifierCode, requestMessage);

//
//// 0x9B = 155d database-revision (155d = 0x9B) defined in ASHRAE on page 696
//case 0x9B:
//LOG.info("<<< READ_PROP: database-revision ({})", propertyIdentifierCode);
//return processDatabaseRevisionProperty(propertyIdentifierCode, requestMessage);
//
//// 0x8B = 139d protocol-revision (0x8B = 139d)
//case 0x8B:
//LOG.info("<<< READ_PROP: protocol-revision ({})", propertyIdentifierCode);
//return processProtocolRevisionProperty(propertyIdentifierCode, requestMessage);
//
//// 0x62 = 98d protocol-version
//case 0x62:
//LOG.info("<<< READ_PROP: protocol-version ({})", propertyIdentifierCode);
//return processProtocolVersionProperty(propertyIdentifierCode, requestMessage);

//case DeviceProperty.LAST_RESTART_REASON:
//LOG.info("<<< READ_PROP: last-restart-reason ({})", propertyIdentifierCode);
//return processLastRestartReasonProperty(propertyIdentifierCode, requestMessage);

//// 0x27 = 39d, fault-values, optional
//case 0x27:
//	LOG.info("<<< READ_PROP: fault-values ({})", propertyIdentifierCode);
//	msg = "Unknown property! PropertyIdentifier = " + propertyIdentifier;
//	LOG.error(msg);
//	throw new NotImplementedException(msg);

// 0xA8 = 168d, profile-name
//case 0xA8:
//	LOG.info("<<< READ_PROP: profile-name ({})", propertyIdentifierCode);
//	return processStringProperty(propertyIdentifierCode, requestMessage, NetworkUtils.PROFILE_NAME);
