package de.bacnetz.factory;

import java.time.LocalDateTime;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.bacnetz.common.utils.BACnetUtils;
import de.bacnetz.common.utils.NetworkUtils;
import de.bacnetz.controller.DefaultMessage;
import de.bacnetz.controller.Message;
import de.bacnetz.conversion.BACnetDateToByteConverter;
import de.bacnetz.conversion.BACnetTimeToByteConverter;
import de.bacnetz.conversion.Converter;
import de.bacnetz.devices.CompositeDeviceProperty;
import de.bacnetz.devices.Device;
import de.bacnetz.devices.DeviceProperty;
import de.bacnetz.devices.DevicePropertyType;
import de.bacnetz.devices.ObjectType;
import de.bacnetz.stack.APDU;
import de.bacnetz.stack.BACnetDate;
import de.bacnetz.stack.BACnetServicesSupportedBitString;
import de.bacnetz.stack.BACnetTime;
import de.bacnetz.stack.ConfirmedServiceChoice;
import de.bacnetz.stack.ErrorClass;
import de.bacnetz.stack.ErrorCode;
import de.bacnetz.stack.NPDU;
import de.bacnetz.stack.ObjectIdentifierServiceParameter;
import de.bacnetz.stack.PDUType;
import de.bacnetz.stack.ServiceParameter;
import de.bacnetz.stack.TagClass;
import de.bacnetz.stack.UnconfirmedServiceChoice;
import de.bacnetz.stack.VirtualLinkControl;

public class DefaultMessageFactory implements MessageFactory {

    private static final Logger LOG = LogManager.getLogger(DefaultMessageFactory.class);

    /**
     * If a large payload does not fit into a single network packet, it is segmented
     * over several packets. This is a BACnet feature and can be forced (e.g. for
     * testing or when an embedded device has limited memory) by letting the
     * communication partner know that only very small payloads can be processed at
     * a time.<br />
     * <br />
     * Setting this constant to true forces the communication into using the
     * smallest packet size defined in BACnet.<br />
     * <br />
     * Used to test segmentation assembly/dissasembly.
     */
    private static final boolean TEST_SEGMENTATION = true;

    private Map<Integer, String> vendorMap = new HashMap<>();

    private final Converter<BACnetDate, byte[]> bacnetDateToByteConverter = new BACnetDateToByteConverter();

    private final Converter<BACnetTime, byte[]> bacnetTimeToByteConverter = new BACnetTimeToByteConverter();

    @Override
    public Message create(final Object... args) {

        LOG.trace(args);

        DeviceProperty<?> deviceProperty;
        Device device;
        Message requestMessage;

        int index = 0;

        if (args[0] instanceof DeviceProperty<?>) {

            deviceProperty = (DeviceProperty<?>) args[index++];
            device = (Device) args[index++];
            requestMessage = (Message) args[index++];

            switch (deviceProperty.getPropertyKey()) {

            case DeviceProperty.OBJECT_IDENTIFIER:
                return processObjectIdentifier(device, deviceProperty, requestMessage);

            case DeviceProperty.OBJECT_LIST:
                return processObjectListRequest(device, deviceProperty, requestMessage);

            case DeviceProperty.STATUS_FLAGS:
                return processStatusFlagsProperty(device, deviceProperty, requestMessage);

            case DeviceProperty.PROPERTY_LIST:
                return processPropertyListProperty(device, deviceProperty, requestMessage);

            case DeviceProperty.STATE_TEXT:
                return processStateTextProperty(device, deviceProperty, requestMessage);

            case DeviceProperty.PROTOCOL_SERVICES_SUPPORTED:
                return processSupportedServicesProperty(device, deviceProperty, requestMessage);

            case DeviceProperty.TIME_OF_DEVICE_RESTART:
                return processTimeOfDeviceRestartProperty(device, deviceProperty, requestMessage, true, true,
                        device.getTimeOfDeviceRestart());

            case DeviceProperty.LOCAL_DATE:
                return processTimeOfDeviceRestartProperty(device, deviceProperty, requestMessage, true, false,
                        LocalDateTime.now());

            case DeviceProperty.LOCAL_TIME:
                return processTimeOfDeviceRestartProperty(device, deviceProperty, requestMessage, false, true,
                        LocalDateTime.now());

            case DeviceProperty.DEVICE_ADDRESS_BINDING:
                return processAddressBindingProperty(device, deviceProperty, requestMessage);

            case DeviceProperty.RESTART_NOTIFICATION_RECIPIENTS:
                return processRestartNotificationRecipientsProperty(device, deviceProperty, requestMessage);

            case DeviceProperty.ACTIVE_COV_SUBSCRIPTION:
                LOG.trace(">>> SUBSCRIBE_COV received!");
                return processActiveCOVSubscriptions(device, deviceProperty, requestMessage);

            case DeviceProperty.PRIORITY:
                LOG.trace(">>> PRIORITY received!");
                return processPriority(device, deviceProperty, requestMessage);

//            case DeviceProperty.PRESENT_VALUE:
//                return device.processPresentValueProperty(deviceProperty, requestMessage);
            }

            switch (deviceProperty.getMessageType()) {

            case WHO_IS:
                if (args.length > 1) {
                    final int lowerBound = (int) args[index++];
                    final int upperBound = (int) args[index++];
                    return whoIsMessage(lowerBound, upperBound);
                } else {
                    return whoIsMessage();
                }

            case UNSIGNED_INTEGER:
                return returnIntegerProperty(device, requestMessage.getApdu().getInvokeId(),
                        deviceProperty.getPropertyKey(), deviceProperty.getValueAsByteArray());

            case BOOLEAN:
                return returnBooleanProperty(device, requestMessage.getApdu().getInvokeId(),
                        deviceProperty.getPropertyKey(), (Boolean) deviceProperty.getValue());

            case ENUMERATED:
                return createEnumeratedProperty(device, requestMessage.getApdu().getInvokeId(),
                        deviceProperty.getPropertyKey(), deviceProperty.getValueAsByteArray());

            case SINGED_INTEGER_TWOS_COMPLEMENT_NOTATION:
                return returnSignedIntegerTwosComplementNotationProperty(device, requestMessage.getApdu().getInvokeId(),
                        deviceProperty.getPropertyKey(), deviceProperty.getValueAsByteArray());

            case CHARACTER_STRING:
                return stringProperty(device, deviceProperty.getPropertyKey(), requestMessage,
                        deviceProperty.getValueAsString());

            default:
                final String msg = "Unknown property! PropertyIdentifier = " + deviceProperty.getPropertyKey()
                        + " property: " + DevicePropertyType.getByCode(deviceProperty.getPropertyKey()).getName();
                LOG.trace(msg);

                // return error message
                return createErrorMessage(requestMessage, ErrorClass.PROPERTY.getCode(),
                        ErrorCode.UNKNOWN_PROPERTY.getCode());
            }
        }

        requestMessage = (Message) args[0];

        final int errorClass = 0x02;
        final int errorCode = 0x20;

        return createErrorMessage(requestMessage, errorClass, errorCode);
    }

    private Message processObjectIdentifier(final Device device, final DeviceProperty<?> deviceProperty,
            final Message requestMessage) {

        final VirtualLinkControl virtualLinkControl = new VirtualLinkControl();
        virtualLinkControl.setType(0x81);
        virtualLinkControl.setFunction(0x0A);
        virtualLinkControl.setLength(0x00);

        // NPDU including destination network information
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

        final APDU apdu = new APDU();
        apdu.setPduType(PDUType.COMPLEX_ACK_PDU);
        apdu.setInvokeId(requestMessage.getApdu().getInvokeId());
        apdu.setConfirmedServiceChoice(ConfirmedServiceChoice.READ_PROPERTY);
        apdu.setVendorMap(vendorMap);

        // this object identifier has to be context specific. I do not know why
        final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
        objectIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        objectIdentifierServiceParameter.setTagNumber(0x00);
        objectIdentifierServiceParameter.setLengthValueType(4);
        objectIdentifierServiceParameter.setObjectType(device.getObjectType());
        objectIdentifierServiceParameter.setInstanceNumber(device.getId());
        apdu.getServiceParameters().add(objectIdentifierServiceParameter);

        final ServiceParameter propertyIdentifierServiceParameter = new ServiceParameter();
        propertyIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        propertyIdentifierServiceParameter.setTagNumber(0x01);
        propertyIdentifierServiceParameter.setLengthValueType(0x01);
        propertyIdentifierServiceParameter.setPayload(new byte[] { (byte) deviceProperty.getPropertyKey() });
        apdu.getServiceParameters().add(propertyIdentifierServiceParameter);

        // {[3]
        final ServiceParameter openingTagServiceParameter = new ServiceParameter();
        openingTagServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        openingTagServiceParameter.setTagNumber(0x03);
        openingTagServiceParameter.setLengthValueType(ServiceParameter.OPENING_TAG_CODE);
        apdu.getServiceParameters().add(openingTagServiceParameter);

        final ObjectIdentifierServiceParameter objectIdentifierServiceParameter2 = new ObjectIdentifierServiceParameter();
        objectIdentifierServiceParameter2.setTagClass(TagClass.APPLICATION_TAG);
        objectIdentifierServiceParameter2.setTagNumber(MessageType.BACNET_OBJECT_IDENTIFIER.getId());
        objectIdentifierServiceParameter2.setLengthValueType(4);
        objectIdentifierServiceParameter2.setObjectType(device.getObjectType());
        objectIdentifierServiceParameter2.setInstanceNumber(device.getId());
        apdu.getServiceParameters().add(objectIdentifierServiceParameter2);

        // }[3]
        final ServiceParameter closingTagServiceParameter = new ServiceParameter();
        closingTagServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        closingTagServiceParameter.setTagNumber(0x03);
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

    private Message processPriority(final Device device, final DeviceProperty<?> deviceProperty,
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

        final ObjectIdentifierServiceParameter outwardObjectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
        outwardObjectIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        outwardObjectIdentifierServiceParameter.setTagNumber(0x00);
        outwardObjectIdentifierServiceParameter.setLengthValueType(4);
        outwardObjectIdentifierServiceParameter.setObjectType(device.getObjectType());
        outwardObjectIdentifierServiceParameter.setInstanceNumber(device.getId());

        final ServiceParameter propertyIdentifierServiceParameter = new ServiceParameter();
        propertyIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        propertyIdentifierServiceParameter.setTagNumber(0x01);
        propertyIdentifierServiceParameter.setLengthValueType(0x01);
        propertyIdentifierServiceParameter.setPayload(new byte[] { (byte) deviceProperty.getPropertyKey() });

        final APDU apdu = new APDU();
        apdu.setPduType(PDUType.COMPLEX_ACK_PDU);
        apdu.setInvokeId(requestMessage.getApdu().getInvokeId());
        apdu.setConfirmedServiceChoice(ConfirmedServiceChoice.READ_PROPERTY);
        apdu.setVendorMap(vendorMap);
        apdu.getServiceParameters().add(outwardObjectIdentifierServiceParameter);
        apdu.getServiceParameters().add(propertyIdentifierServiceParameter);

        // {[4]
        final ServiceParameter openingTagServiceParameter = new ServiceParameter();
        openingTagServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        openingTagServiceParameter.setTagNumber(0x04);
        openingTagServiceParameter.setLengthValueType(ServiceParameter.OPENING_TAG_CODE);
        apdu.getServiceParameters().add(openingTagServiceParameter);

        for (final DeviceProperty<?> childDeviceProperty : ((CompositeDeviceProperty) deviceProperty)
                .getCompositeList()) {

            final ServiceParameter childServiceParameter = new ServiceParameter();
            childServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
            childServiceParameter.setTagNumber(ServiceParameter.UNSIGNED_INTEGER_CODE);
            childServiceParameter.setLengthValueType(1);
            childServiceParameter
                    .setPayload(new byte[] { (byte) ((Integer) childDeviceProperty.getValue()).intValue() });
            apdu.getServiceParameters().add(childServiceParameter);
        }

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

    /**
     * 12.11.39 Active_COV_Subscriptions
     * 
     * The Active_COV_Subscriptions property is a BACnetLIST of
     * BACnetCOVSubscription, each of which consists of a Recipient, a Monitored
     * Property Reference, an Issue Confirmed Notifications flag, a Time Remaining
     * value and an optional COV Increment. This property provides a network-visible
     * indication of those COV subscriptions that are active at any given time.
     * Whenever a COV Subscription is created with the SubscribeCOV or
     * SubscribeCOVProperty service, a new entry is added to the
     * Active_COV_Subscriptions list. Similarly, whenever a COV Subscription is
     * terminated, the corresponding entry shall be removed from the
     * Active_COV_Subscriptions list.
     * 
     * <pre>
     * BACnetCOVSubscription ::= SEQUENCE {
     *      recipient [0] BACnetRecipientProcess,
     *      monitoredPropertyReference [1] BACnetObjectPropertyReference,
     *      issueConfirmedNotifications [2] BOOLEAN,
     *      timeRemaining [3] Unsigned,
     *      covIncrement [4] REAL OPTIONAL -- used only with monitored properties with a numeric datatype
     * }
     * </pre>
     * 
     * @param device
     * @param deviceProperty
     * @param requestMessage
     * @return
     */
    private Message processActiveCOVSubscriptions(final Device device, final DeviceProperty<?> deviceProperty,
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

        final APDU apdu = new APDU();
        apdu.setPduType(PDUType.COMPLEX_ACK_PDU);
        apdu.setInvokeId(requestMessage.getApdu().getInvokeId());
        apdu.setConfirmedServiceChoice(ConfirmedServiceChoice.READ_PROPERTY);
        apdu.setVendorMap(vendorMap);

        final ObjectIdentifierServiceParameter outwardObjectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
        outwardObjectIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        outwardObjectIdentifierServiceParameter.setTagNumber(0x00);
        outwardObjectIdentifierServiceParameter.setLengthValueType(4);
        outwardObjectIdentifierServiceParameter.setObjectType(device.getObjectType());
        outwardObjectIdentifierServiceParameter.setInstanceNumber(device.getId());
        apdu.getServiceParameters().add(outwardObjectIdentifierServiceParameter);

        final ServiceParameter propertyIdentifierServiceParameter = new ServiceParameter();
        propertyIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        propertyIdentifierServiceParameter.setTagNumber(0x01);
        propertyIdentifierServiceParameter.setLengthValueType(0x01);
        propertyIdentifierServiceParameter.setPayload(new byte[] { (byte) deviceProperty.getPropertyKey() });
        apdu.getServiceParameters().add(propertyIdentifierServiceParameter);

        // {[3]
        final ServiceParameter openingTagServiceParameter = new ServiceParameter();
        openingTagServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        openingTagServiceParameter.setTagNumber(0x03);
        openingTagServiceParameter.setLengthValueType(ServiceParameter.OPENING_TAG_CODE);
        apdu.getServiceParameters().add(openingTagServiceParameter);

//        final ServiceParameter objectNameServiceParameter = new ServiceParameter();
//        objectNameServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
//        objectNameServiceParameter.setTagNumber(ServiceParameter.APPLICATION_TAG_NUMBER_CHARACTER_STRING);
//        objectNameServiceParameter.setLengthValueType(ServiceParameter.EXTENDED_VALUE);
//        objectNameServiceParameter.setPayload(BACnetUtils.retrieveAsString(data));
//        apdu.getServiceParameters().add(objectNameServiceParameter);

        // @formatter:off
        
        // recipient
        // {[0] - ServiceParamter: Opening Tag: 6
        
        //      recipient process
        //      {[0] - ServiceParamter: Opening Tag: 6
        //          {[1] - ServiceParamter: Opening Tag: 6
        //          network number - Service Parameter: Unsigned Integer, value=100 ????
        //          mac address/IP/PORT - Service Parameter: OctetString, (4 byte IPv4) (2 byte Port)
        //          }[1] - ServiceParamter: Closing Tag: 7
        //      }[0] - ServiceParamter: Closing Tag: 7
        //
        //      process identifier - Context Tag: 1
        
        // }[0] - ServiceParamter: Closing Tag: 7
        
        // monitored property reference
        // {[1] - ServiceParamter: Opening Tag: 6
        //      object identifier Service parameter
        //      Property identifier: Service parameter
        // }[1] - ServiceParamter: Closing Tag: 7
        
        // issue confirmed notifications: TRUE: Context Specific Tag, Value = 0x01 == TRUE
        
        // time remaining: Context Specific Tag, value = 2 byte (e.g. 0x1a43 = 6723d ==)
        
        // @formatter:on

        // }[3]
        final ServiceParameter closingTagServiceParameter = new ServiceParameter();
        closingTagServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        closingTagServiceParameter.setTagNumber(0x03);
        closingTagServiceParameter.setLengthValueType(ServiceParameter.CLOSING_TAG_CODE);
        apdu.getServiceParameters().add(closingTagServiceParameter);

        final DefaultMessage result = new DefaultMessage();
        result.setVirtualLinkControl(virtualLinkControl);
        result.setNpdu(npdu);
        result.setApdu(apdu);

        virtualLinkControl.setLength(result.getDataLength());

        return result;
    }

    private Message stringProperty(final Device device, final int propertyIdentifierCode, final Message requestMessage,
            final String data) {

//		final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = (ObjectIdentifierServiceParameter) requestMessage
//				.getApdu().getServiceParameters().get(0);
//		final Device targetDevice = findDevice(objectIdentifierServiceParameter);

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
        outwardObjectIdentifierServiceParameter.setObjectType(device.getObjectType());
        outwardObjectIdentifierServiceParameter.setInstanceNumber(device.getId());

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
        objectNameServiceParameter.setPayload(BACnetUtils.retrieveAsString(data));

        final ServiceParameter closingTagServiceParameter = new ServiceParameter();
        closingTagServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        closingTagServiceParameter.setTagNumber(0x03);
        closingTagServiceParameter.setLengthValueType(ServiceParameter.CLOSING_TAG_CODE);

        final APDU apdu = new APDU();
        apdu.setPduType(PDUType.COMPLEX_ACK_PDU);
        apdu.setInvokeId(requestMessage.getApdu().getInvokeId());
        apdu.setConfirmedServiceChoice(ConfirmedServiceChoice.READ_PROPERTY);
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

    @Override
    public Message createErrorMessage(final Message requestMessage, final int errorClass, final int errorCode) {

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
        errorClassServiceParameter.setPayload(new byte[] { (byte) errorClass });

        final ServiceParameter errorCodeServiceParameter = new ServiceParameter();
        errorCodeServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
        errorCodeServiceParameter.setTagNumber(ServiceParameter.ENUMERATED_CODE);
        errorCodeServiceParameter.setLengthValueType(0x01);
        errorCodeServiceParameter.setPayload(new byte[] { (byte) errorCode });

        final APDU apdu = new APDU();
        apdu.setPduType(PDUType.ERROR_PDU);
        apdu.setInvokeId(requestMessage.getApdu().getInvokeId());
        apdu.setConfirmedServiceChoice(ConfirmedServiceChoice.READ_PROPERTY);
        apdu.setVendorMap(vendorMap);
        apdu.getServiceParameters().add(errorClassServiceParameter);
        apdu.getServiceParameters().add(errorCodeServiceParameter);

        final DefaultMessage result = new DefaultMessage();
        result.setVirtualLinkControl(virtualLinkControl);
        result.setNpdu(npdu);
        result.setApdu(apdu);

        virtualLinkControl.setLength(result.getDataLength());

        return result;
    }

    /**
     * The bacnet partner can issue two different requests for the object list. See
     * bacnet_whois_iam_readProperty.pcapng (messages 1117 and 1123) <br />
     * <br />
     * 
     * One of the requests (message 1117) is about the length of the object list.
     * The response is an unsigned integer.<br />
     * <br />
     * 
     * The other request (message 1123) asks for the actual object list. The
     * response is a complex ack with all objects as service parameters.<br />
     * <br />
     * 
     * @param propertyIdentifierCode
     * @param requestMessage
     * @return
     */
    private Message processObjectListRequest(final Device device, final DeviceProperty<?> deviceProperty,
            final Message requestMessage) {

        final List<ServiceParameter> serviceParameters = requestMessage.getApdu().getServiceParameters();

        final int serviceParamterSize = serviceParameters.size();

        LOG.trace("serviceParameters.size() = {}", serviceParamterSize);

        if (serviceParameters.size() == 3) {

            final ServiceParameter serviceParameter = serviceParameters.get(2);

            if (serviceParameter.getPayload()[0] == 0) {

                // The query want's to know about the Array Index 0.
                // In bacnet the first element of an array always contains the amount of
                // elements/objects inside the array/object list
                LOG.trace(
                        "The query want's to know about the Array Index 0 = the amount of objects in the object list");
                return processObjectListLengthProperty(device, deviceProperty.getPropertyKey(), requestMessage);

            } else {
                throw new RuntimeException("unknown query!");
            }
        }

        return processObjectListProperty(device, deviceProperty.getPropertyKey(), requestMessage);
    }

//    public byte[] getSupportedServicesPayload(final Device device) {
//
//        // retrieve the bits that describe which services are supported by this device
//        final BACnetServicesSupportedBitString bacnetServicesSupportedBitString = device.retrieveServicesSupported();
//        final BitSet bitSet = bacnetServicesSupportedBitString.getBitSet();
//        final byte[] bitSetByteArray = bitSet.toByteArray();
//
//        // this is the result payload
//        final byte[] result = new byte[7];
//
//        // length value is 6 byte
//        result[0] = (byte) 0x06;
//        // first byte is an unused zero byte
//        // there is an unused zero byte at the beginning for some reason
//        result[1] = (byte) 0x00;
//        // the last 5 byte contain the bit set of all available services of this device
//        System.arraycopy(bitSetByteArray, 0, result, 2, bitSetByteArray.length);
//
//        return result;
//    }

    public byte[] getSupportedServicesPayload(final Device device) {

        // retrieve the bits that describe which services are supported by this device
        final BACnetServicesSupportedBitString bacnetServicesSupportedBitString = device.retrieveServicesSupported();
        final BitSet bitSet = bacnetServicesSupportedBitString.getBitSet();
        final byte[] bitSetByteArray = bitSet.toByteArray();

        // this is the result payload
        final byte[] result = new byte[6];

//        // length value is 6 byte
//        result[0] = (byte) 0x06;
        // first byte is an unused zero byte
        // there is an unused zero byte at the beginning for some reason
        result[0] = (byte) 0x00;
        // the last 5 byte contain the bit set of all available services of this device
        System.arraycopy(bitSetByteArray, 0, result, 1, bitSetByteArray.length);

        return result;
    }

    private Message processObjectListProperty(final Device device, final int propertyIdentifierCode,
            final Message requestMessage) {

        final VirtualLinkControl virtualLinkControl = new VirtualLinkControl();
        virtualLinkControl.setType(0x81);
        virtualLinkControl.setFunction(0x0A);
        virtualLinkControl.setLength(0x00);

        // NPDU including destination network information
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
        objectIdentifierServiceParameter.setObjectType(device.getObjectType());
        objectIdentifierServiceParameter.setInstanceNumber(device.getId());

        final ServiceParameter propertyIdentifierServiceParameter = new ServiceParameter();
        propertyIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        propertyIdentifierServiceParameter.setTagNumber(0x01);
        propertyIdentifierServiceParameter.setLengthValueType(0x01);
        propertyIdentifierServiceParameter.setPayload(new byte[] { (byte) propertyIdentifierCode });

        // {[3]
        final ServiceParameter openingTagServiceParameter = new ServiceParameter();
        openingTagServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        openingTagServiceParameter.setTagNumber(0x03);
        openingTagServiceParameter.setLengthValueType(ServiceParameter.OPENING_TAG_CODE);

        final ServiceParameter protocolServicesSupportedBitStringServiceParameter = new ServiceParameter();
        protocolServicesSupportedBitStringServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
        protocolServicesSupportedBitStringServiceParameter
                .setTagNumber(ServiceParameter.APPLICATION_TAG_NUMBER_BIT_STRING);
        protocolServicesSupportedBitStringServiceParameter.setLengthValueType(ServiceParameter.EXTENDED_VALUE);
        protocolServicesSupportedBitStringServiceParameter.setPayload(getSupportedServicesPayload(device));

        // }[3]
        final ServiceParameter closingTagServiceParameter = new ServiceParameter();
        closingTagServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        closingTagServiceParameter.setTagNumber(0x03);
        closingTagServiceParameter.setLengthValueType(ServiceParameter.CLOSING_TAG_CODE);

        final APDU apdu = new APDU();
        apdu.setPduType(PDUType.COMPLEX_ACK_PDU);
        apdu.setInvokeId(requestMessage.getApdu().getInvokeId());
//      apdu.setUnconfirmedServiceChoice(UnconfirmedServiceChoice.READ_PROPERTY);
        apdu.setConfirmedServiceChoice(ConfirmedServiceChoice.READ_PROPERTY);
        apdu.setVendorMap(vendorMap);

        // a sub device
        apdu.getServiceParameters().add(objectIdentifierServiceParameter);
        apdu.getServiceParameters().add(propertyIdentifierServiceParameter);
        apdu.getServiceParameters().add(openingTagServiceParameter);

        // does the simulated device have to list this object identifier again?
        // the object lists itself!
        apdu.getServiceParameters().add(device.getObjectIdentifierServiceParameter());

        // add all children
        if (CollectionUtils.isNotEmpty(device.getChildDevices())) {

            for (final Device childDevice : device.getChildDevices()) {

                apdu.getServiceParameters().add(childDevice.getObjectIdentifierServiceParameter());
            }
        }

//		// 1
//		apdu.getServiceParameters().add(createMultiStateValueServiceParameter(1));
//		// 2
//		apdu.getServiceParameters().add(createMultiStateValueServiceParameter(2));
//		// 3
//		apdu.getServiceParameters().add(binaryInputServiceParameter(1));
//		// 4
//		apdu.getServiceParameters().add(binaryInputServiceParameter(2));
//		// 5
//		apdu.getServiceParameters().add(createMultiStateValueServiceParameter(3));
//		// 6
//		apdu.getServiceParameters().add(createMultiStateValueServiceParameter(4));
//		// 7
//		apdu.getServiceParameters().add(createNotificationClassServiceParameter(50));
//		// 8
//		apdu.getServiceParameters().add(createMultiStateValueServiceParameter(5));
//		// 9
//		apdu.getServiceParameters().add(createMultiStateValueServiceParameter(6));
//		// 10
//		apdu.getServiceParameters().add(binaryInputServiceParameter(3));
//		// 11
//		apdu.getServiceParameters().add(binaryInputServiceParameter(4));
//		// 12
//		apdu.getServiceParameters().add(createMultiStateValueServiceParameter(7));
//		// 13
//		apdu.getServiceParameters().add(createMultiStateValueServiceParameter(8));
//		// 14
//		apdu.getServiceParameters().add(createMultiStateValueServiceParameter(9));
//		// 15
//		apdu.getServiceParameters().add(createMultiStateValueServiceParameter(10));

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

    private Message processObjectListLengthProperty(final Device device, final int propertyIdentifierCode,
            final Message requestMessage) {

        final VirtualLinkControl virtualLinkControl = new VirtualLinkControl();
        virtualLinkControl.setType(0x81);
        virtualLinkControl.setFunction(0x0A);
        virtualLinkControl.setLength(0x00);

        // NPDU including destination network information
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
        objectIdentifierServiceParameter.setObjectType(device.getObjectType());
        objectIdentifierServiceParameter.setInstanceNumber(device.getId());

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

        final ServiceParameter openingTagServiceParameter = new ServiceParameter();
        openingTagServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        openingTagServiceParameter.setTagNumber(0x03);
        openingTagServiceParameter.setLengthValueType(ServiceParameter.OPENING_TAG_CODE);

        final ServiceParameter objectListLengthServiceParameter = new ServiceParameter();
        objectListLengthServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
        objectListLengthServiceParameter.setTagNumber(0x02);
        objectListLengthServiceParameter.setLengthValueType(0x01);
        objectListLengthServiceParameter.setPayload(new byte[] { (byte) (device.getChildDevices().size() + 1) });

        final ServiceParameter closingTagServiceParameter = new ServiceParameter();
        closingTagServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        closingTagServiceParameter.setTagNumber(0x03);
        closingTagServiceParameter.setLengthValueType(ServiceParameter.CLOSING_TAG_CODE);

        final APDU apdu = new APDU();
        apdu.setPduType(PDUType.COMPLEX_ACK_PDU);
        apdu.setInvokeId(requestMessage.getApdu().getInvokeId());
        apdu.setConfirmedServiceChoice(ConfirmedServiceChoice.READ_PROPERTY);
        apdu.setVendorMap(vendorMap);

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

    private Message returnSignedIntegerTwosComplementNotationProperty(final Device device, final int invokeId,
            final int propertyKey, final byte[] payload) {

        final VirtualLinkControl virtualLinkControl = new VirtualLinkControl();
        virtualLinkControl.setType(0x81);
        virtualLinkControl.setFunction(0x0A);
        virtualLinkControl.setLength(0x00);

        final NPDU npdu = new NPDU();
        npdu.setVersion(0x01);

        // no additional information
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
        objectIdentifierServiceParameter.setObjectType(device.getObjectType());
        objectIdentifierServiceParameter.setInstanceNumber(device.getId());

        final ServiceParameter propertyIdentifierServiceParameter = new ServiceParameter();
        propertyIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
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
//      apdu.setUnconfirmedServiceChoice(UnconfirmedServiceChoice.READ_PROPERTY);
        apdu.setConfirmedServiceChoice(ConfirmedServiceChoice.READ_PROPERTY);
        apdu.setVendorMap(vendorMap);
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

//		final byte[] bytes = result.getBytes();
//		LOG.info(Utils.byteArrayToStringNoPrefix(bytes));

        return result;
    }

    @Override
    public Message whoIsMessage() {

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
        apdu.setUnconfirmedServiceChoice(UnconfirmedServiceChoice.WHO_IS);

        final DefaultMessage defaultMessage = new DefaultMessage();
        defaultMessage.setVirtualLinkControl(virtualLinkControl);
        defaultMessage.setNpdu(npdu);
        defaultMessage.setApdu(apdu);

        virtualLinkControl.setLength(defaultMessage.getDataLength());

        return defaultMessage;
    }

    @Override
    public Message whoIsMessage(final int lowerBound, final int upperBound) {

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
        npdu.setDestinationNetworkNumber(NetworkUtils.BROADCAST_NETWORK_NUMBER);
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
        apdu.setUnconfirmedServiceChoice(UnconfirmedServiceChoice.WHO_IS);
        apdu.getServiceParameters().add(lowerBoundServiceParameter);
        apdu.getServiceParameters().add(upperBoundServiceParameter);

        final DefaultMessage result = new DefaultMessage();
        result.setVirtualLinkControl(virtualLinkControl);
        result.setNpdu(npdu);
        result.setApdu(apdu);

        virtualLinkControl.setLength(result.getDataLength());

        return result;
    }

    @Override
    public Message createEnumeratedProperty(final Device device, final int invokeId, final int propertyKey,
            final byte[] payload) {

        final VirtualLinkControl virtualLinkControl = new VirtualLinkControl();
        virtualLinkControl.setType(0x81);
        virtualLinkControl.setFunction(0x0A);
        virtualLinkControl.setLength(0x00);

        final NPDU npdu = new NPDU();
        npdu.setVersion(0x01);

        // no additional information
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
        objectIdentifierServiceParameter.setObjectType(device.getObjectType());
        objectIdentifierServiceParameter.setInstanceNumber(device.getId());

        final ServiceParameter propertyIdentifierServiceParameter = new ServiceParameter();
        propertyIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        propertyIdentifierServiceParameter.setTagNumber(0x01);
        propertyIdentifierServiceParameter.setLengthValueType(1);
        propertyIdentifierServiceParameter.setPayload(new byte[] { (byte) propertyKey });

        // {[3]
        final ServiceParameter openingTagServiceParameter = new ServiceParameter();
        openingTagServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        openingTagServiceParameter.setTagNumber(0x03);
        openingTagServiceParameter.setLengthValueType(ServiceParameter.OPENING_TAG_CODE);

        final ServiceParameter valueServiceParameter = new ServiceParameter();
        valueServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
        valueServiceParameter.setTagNumber(ServiceParameter.ENUMERATED_CODE);
        valueServiceParameter.setLengthValueType(payload.length);
        valueServiceParameter.setPayload(payload);

        // }[3]
        final ServiceParameter closingTagServiceParameter = new ServiceParameter();
        closingTagServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        closingTagServiceParameter.setTagNumber(0x03);
        closingTagServiceParameter.setLengthValueType(ServiceParameter.CLOSING_TAG_CODE);

        final APDU apdu = new APDU();
        apdu.setPduType(PDUType.COMPLEX_ACK_PDU);
        apdu.setInvokeId(invokeId);
//      apdu.setUnconfirmedServiceChoice(UnconfirmedServiceChoice.READ_PROPERTY);
        apdu.setConfirmedServiceChoice(ConfirmedServiceChoice.READ_PROPERTY);
        apdu.setVendorMap(vendorMap);
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

//		final byte[] bytes = result.getBytes();
//		LOG.info(Utils.byteArrayToStringNoPrefix(bytes));

        return result;
    }

    private Message returnBooleanProperty(final Device device, final int invokeId, final int propertyKey,
            final boolean payload) {

        final VirtualLinkControl virtualLinkControl = new VirtualLinkControl();
        virtualLinkControl.setType(0x81);
        virtualLinkControl.setFunction(0x0A);
        virtualLinkControl.setLength(0x00);

        final NPDU npdu = new NPDU();
        npdu.setVersion(0x01);
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
        objectIdentifierServiceParameter.setObjectType(device.getObjectType());
        objectIdentifierServiceParameter.setInstanceNumber(device.getId());

        final ServiceParameter propertyIdentifierServiceParameter = new ServiceParameter();
        propertyIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
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
        valueServiceParameter.setLengthValueType(payload ? 1 : 0);

        final ServiceParameter closingTagServiceParameter = new ServiceParameter();
        closingTagServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        closingTagServiceParameter.setTagNumber(0x03);
        closingTagServiceParameter.setLengthValueType(ServiceParameter.CLOSING_TAG_CODE);

        final APDU apdu = new APDU();
        apdu.setPduType(PDUType.COMPLEX_ACK_PDU);
        apdu.setInvokeId(invokeId);
//      apdu.setUnconfirmedServiceChoice(UnconfirmedServiceChoice.READ_PROPERTY);
        apdu.setConfirmedServiceChoice(ConfirmedServiceChoice.READ_PROPERTY);
        apdu.setVendorMap(vendorMap);
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

//		final byte[] bytes = result.getBytes();
//		LOG.info(Utils.byteArrayToStringNoPrefix(bytes));

        return result;
    }

    private Message returnIntegerProperty(final Device device, final int invokeId, final int propertyKey,
            final byte[] payload) {

        final VirtualLinkControl virtualLinkControl = new VirtualLinkControl();
        virtualLinkControl.setType(0x81);
        virtualLinkControl.setFunction(0x0A);
        virtualLinkControl.setLength(0x00);

        final NPDU npdu = new NPDU();
        npdu.setVersion(0x01);
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
        objectIdentifierServiceParameter.setObjectType(device.getObjectType());
        objectIdentifierServiceParameter.setInstanceNumber(device.getId());

        final ServiceParameter propertyIdentifierServiceParameter = new ServiceParameter();
        propertyIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
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
//      apdu.setUnconfirmedServiceChoice(UnconfirmedServiceChoice.READ_PROPERTY);
        apdu.setConfirmedServiceChoice(ConfirmedServiceChoice.READ_PROPERTY);
        apdu.setVendorMap(vendorMap);
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

//		final byte[] bytes = result.getBytes();
//		LOG.info(Utils.byteArrayToStringNoPrefix(bytes));

        return result;
    }

    private DefaultMessage processSupportedServicesProperty(final Device device, final DeviceProperty<?> deviceProperty,
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
//        objectIdentifierServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
        objectIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        objectIdentifierServiceParameter.setTagNumber(0x00);
        objectIdentifierServiceParameter.setLengthValueType(4);
        objectIdentifierServiceParameter.setObjectType(device.getObjectType());
        objectIdentifierServiceParameter.setInstanceNumber(device.getId());

        final ServiceParameter protocolServicesSupportedServiceParameter = new ServiceParameter();
        protocolServicesSupportedServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        protocolServicesSupportedServiceParameter.setTagNumber(0x01);
        protocolServicesSupportedServiceParameter.setLengthValueType(1);
        protocolServicesSupportedServiceParameter.setPayload(new byte[] { (byte) deviceProperty.getPropertyKey() });

        // {[3]
        final ServiceParameter openingTagServiceParameter = new ServiceParameter();
        openingTagServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        openingTagServiceParameter.setTagNumber(0x03);
        openingTagServiceParameter.setLengthValueType(ServiceParameter.OPENING_TAG_CODE);

        final ServiceParameter protocolServicesSupportedBitStringServiceParameter = new ServiceParameter();
        protocolServicesSupportedBitStringServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
        protocolServicesSupportedBitStringServiceParameter
                .setTagNumber(ServiceParameter.APPLICATION_TAG_NUMBER_BIT_STRING);
        protocolServicesSupportedBitStringServiceParameter.setLengthValueType(ServiceParameter.EXTENDED_VALUE);
        protocolServicesSupportedBitStringServiceParameter.setPayload(getSupportedServicesPayload(device));

        // }[3]
        final ServiceParameter closingTagServiceParameter = new ServiceParameter();
        closingTagServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        closingTagServiceParameter.setTagNumber(0x03);
        closingTagServiceParameter.setLengthValueType(ServiceParameter.CLOSING_TAG_CODE);

        final APDU apdu = new APDU();
        apdu.setPduType(PDUType.COMPLEX_ACK_PDU);
        apdu.setInvokeId(requestMessage.getApdu().getInvokeId());
//      apdu.setUnconfirmedServiceChoice(UnconfirmedServiceChoice.READ_PROPERTY);
        apdu.setConfirmedServiceChoice(ConfirmedServiceChoice.READ_PROPERTY);
        apdu.setVendorMap(vendorMap);
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

        return result;
    }

    private Message processStatusFlagsProperty(final Device device, final DeviceProperty<?> deviceProperty,
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
//        objectIdentifierServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
        objectIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        objectIdentifierServiceParameter.setTagNumber(0x00);
        objectIdentifierServiceParameter.setLengthValueType(4);
        objectIdentifierServiceParameter.setObjectType(device.getObjectType());
        objectIdentifierServiceParameter.setInstanceNumber(device.getId());

        final ServiceParameter protocolServicesSupportedServiceParameter = new ServiceParameter();
        protocolServicesSupportedServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        protocolServicesSupportedServiceParameter.setTagNumber(0x01);
        protocolServicesSupportedServiceParameter.setLengthValueType(1);
        protocolServicesSupportedServiceParameter.setPayload(new byte[] { (byte) deviceProperty.getPropertyKey() });

        // {[3]
        final ServiceParameter openingTagServiceParameter = new ServiceParameter();
        openingTagServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        openingTagServiceParameter.setTagNumber(0x03);
        openingTagServiceParameter.setLengthValueType(ServiceParameter.OPENING_TAG_CODE);

        final ServiceParameter statusFlagsBitStringServiceParameter = device.getStatusFlagsServiceParameter();

        // }[3]
        final ServiceParameter closingTagServiceParameter = new ServiceParameter();
        closingTagServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        closingTagServiceParameter.setTagNumber(0x03);
        closingTagServiceParameter.setLengthValueType(ServiceParameter.CLOSING_TAG_CODE);

        final APDU apdu = new APDU();
        apdu.setPduType(PDUType.COMPLEX_ACK_PDU);
        apdu.setInvokeId(requestMessage.getApdu().getInvokeId());
//      apdu.setUnconfirmedServiceChoice(UnconfirmedServiceChoice.READ_PROPERTY);
        apdu.setConfirmedServiceChoice(ConfirmedServiceChoice.READ_PROPERTY);
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

    private Message processPropertyListProperty(final Device device, final DeviceProperty<?> deviceProperty,
            final Message requestMessage) {

        final int errorClass = 0x01;
        final int errorCode = 0x31;

        return createErrorMessage(requestMessage, errorClass, errorCode);

//        final VirtualLinkControl virtualLinkControl = new VirtualLinkControl();
//        virtualLinkControl.setType(0x81);
//        virtualLinkControl.setFunction(0x0A);
//        virtualLinkControl.setLength(0x00);
//
//        final NPDU npdu = new NPDU();
//        npdu.setVersion(0x01);
//
//        // no additional information
//        // this works, if the cp is connected to the device directly via 192.168.2.1
//        npdu.setControl(0x00);
//
//        if (NetworkUtils.ADD_ADDITIONAL_NETWORK_INFORMATION) {
//
//            // destination network information
//            npdu.setControl(0x20);
//            npdu.setDestinationNetworkNumber(NetworkUtils.DESTINATION_NETWORK_NUMBER);
//            npdu.setDestinationMACLayerAddressLength(3);
//            npdu.setDestinationMac(NetworkUtils.DEVICE_MAC_ADDRESS);
//
//            npdu.setDestinationHopCount(255);
//        }
//
//        final ServiceParameter errorClassServiceParameter = new ServiceParameter();
//        errorClassServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
//        errorClassServiceParameter.setTagNumber(ServiceParameter.ENUMERATED_CODE);
//        errorClassServiceParameter.setLengthValueType(0x01);
//        errorClassServiceParameter.setPayload(new byte[] { (byte) 0x01 });
//
//        final ServiceParameter errorCodeServiceParameter = new ServiceParameter();
//        errorCodeServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
//        errorCodeServiceParameter.setTagNumber(ServiceParameter.ENUMERATED_CODE);
//        errorCodeServiceParameter.setLengthValueType(0x01);
//        // 0x31 = unknown_object
//        errorCodeServiceParameter.setPayload(new byte[] { (byte) 0x31 });
//
//        final APDU apdu = new APDU();
//        apdu.setPduType(PDUType.ERROR_PDU);
//        apdu.setInvokeId(requestMessage.getApdu().getInvokeId());
//        apdu.setUnconfirmedServiceChoice(UnconfirmedServiceChoice.READ_PROPERTY);
//        apdu.setVendorMap(vendorMap);
//        apdu.getServiceParameters().add(errorClassServiceParameter);
//        apdu.getServiceParameters().add(errorCodeServiceParameter);
//
//        final DefaultMessage result = new DefaultMessage();
//        result.setVirtualLinkControl(virtualLinkControl);
//        result.setNpdu(npdu);
//        result.setApdu(apdu);
//
//        virtualLinkControl.setLength(result.getDataLength());
//
//        return result;
    }

    private Message processStateTextProperty(final Device device, final DeviceProperty<?> deviceProperty,
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

        final ObjectIdentifierServiceParameter outwardObjectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
        outwardObjectIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        outwardObjectIdentifierServiceParameter.setTagNumber(0x00);
        outwardObjectIdentifierServiceParameter.setLengthValueType(4);
        outwardObjectIdentifierServiceParameter.setObjectType(device.getObjectType());
        outwardObjectIdentifierServiceParameter.setInstanceNumber(device.getId());

        final ServiceParameter propertyIdentifierServiceParameter = new ServiceParameter();
        propertyIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        propertyIdentifierServiceParameter.setTagNumber(0x01);
        propertyIdentifierServiceParameter.setLengthValueType(0x01);
        propertyIdentifierServiceParameter.setPayload(new byte[] { (byte) deviceProperty.getPropertyKey() });

        final APDU apdu = new APDU();
        apdu.setPduType(PDUType.COMPLEX_ACK_PDU);
        apdu.setInvokeId(requestMessage.getApdu().getInvokeId());
//      apdu.setUnconfirmedServiceChoice(UnconfirmedServiceChoice.READ_PROPERTY);
        apdu.setConfirmedServiceChoice(ConfirmedServiceChoice.READ_PROPERTY);
        apdu.setVendorMap(vendorMap);
        apdu.getServiceParameters().add(outwardObjectIdentifierServiceParameter);
        apdu.getServiceParameters().add(propertyIdentifierServiceParameter);

        // {[4]
        final ServiceParameter openingTagServiceParameter = new ServiceParameter();
        openingTagServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        openingTagServiceParameter.setTagNumber(0x04);
        openingTagServiceParameter.setLengthValueType(ServiceParameter.OPENING_TAG_CODE);
        apdu.getServiceParameters().add(openingTagServiceParameter);

        for (final DeviceProperty<?> childDeviceProperty : ((CompositeDeviceProperty) deviceProperty)
                .getCompositeList()) {

            final ServiceParameter stringServiceParameter = new ServiceParameter();
            stringServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
            stringServiceParameter.setTagNumber(ServiceParameter.APPLICATION_TAG_NUMBER_CHARACTER_STRING);
            stringServiceParameter.setLengthValueType(ServiceParameter.EXTENDED_VALUE);
            stringServiceParameter.setPayload(BACnetUtils.retrieveAsString((String) childDeviceProperty.getValue()));
            apdu.getServiceParameters().add(stringServiceParameter);
        }

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

    private Message processTimeOfDeviceRestartProperty(final Device device, final DeviceProperty<?> deviceProperty,
            final Message requestMessage, final boolean addDate, final boolean addTime, final LocalDateTime date) {

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
        objectIdentifierServiceParameter.setObjectType(device.getObjectType());
        objectIdentifierServiceParameter.setInstanceNumber(device.getId());

        final ServiceParameter propertyIdentifierServiceParameter = new ServiceParameter();
        propertyIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        propertyIdentifierServiceParameter.setTagNumber(0x01);
        propertyIdentifierServiceParameter.setLengthValueType(0x01);
        propertyIdentifierServiceParameter.setPayload(new byte[] { (byte) deviceProperty.getPropertyKey() });

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

        // encode the date parameter here
        final BACnetDate bacnetDate = new BACnetDate();
        bacnetDate.fromLocalDateTime(date);

        final byte[] bacnetDateAsByteArray = bacnetDateToByteConverter.convert(bacnetDate);

        final ServiceParameter dateServiceParameter = new ServiceParameter();
        dateServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
        dateServiceParameter.setTagNumber(ServiceParameter.DATE);
        dateServiceParameter.setLengthValueType(0x04);
        dateServiceParameter.setPayload(bacnetDateAsByteArray);

        // encode the time parameter here!
        final BACnetTime bacnetTime = new BACnetTime();
        bacnetTime.fromLocalDateTime(date);

        final byte[] bacnetTimeAsByteArray = bacnetTimeToByteConverter.convert(bacnetTime);

        final ServiceParameter timeServiceParameter = new ServiceParameter();
        timeServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
        timeServiceParameter.setTagNumber(ServiceParameter.TIME);
        timeServiceParameter.setLengthValueType(0x04);
        timeServiceParameter.setPayload(bacnetTimeAsByteArray);

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
//      apdu.setUnconfirmedServiceChoice(UnconfirmedServiceChoice.READ_PROPERTY);
        apdu.setConfirmedServiceChoice(ConfirmedServiceChoice.READ_PROPERTY);
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

//      final byte[] bytes = result.getBytes();
//      LOG.info(Utils.byteArrayToStringNoPrefix(bytes));

        return result;
    }

    private Message processAddressBindingProperty(final Device device, final DeviceProperty<?> deviceProperty,
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
        objectIdentifierServiceParameter.setObjectType(device.getObjectType());
        objectIdentifierServiceParameter.setInstanceNumber(device.getId());

        final ServiceParameter propertyIdentifierServiceParameter = new ServiceParameter();
        propertyIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        propertyIdentifierServiceParameter.setTagNumber(0x01);
        propertyIdentifierServiceParameter.setLengthValueType(0x01);
        propertyIdentifierServiceParameter.setPayload(new byte[] { (byte) deviceProperty.getPropertyKey() });

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
//      apdu.setUnconfirmedServiceChoice(UnconfirmedServiceChoice.READ_PROPERTY);
        apdu.setConfirmedServiceChoice(ConfirmedServiceChoice.READ_PROPERTY);
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

//      final byte[] bytes = result.getBytes();
//      LOG.info(Utils.byteArrayToStringNoPrefix(bytes));

        return result;
    }

    private Message processRestartNotificationRecipientsProperty(final Device device,
            final DeviceProperty<?> deviceProperty, final Message requestMessage) {

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
        objectIdentifierServiceParameter.setObjectType(device.getObjectType());
        objectIdentifierServiceParameter.setInstanceNumber(device.getId());

        final ServiceParameter propertyIdentifierServiceParameter = new ServiceParameter();
        propertyIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        propertyIdentifierServiceParameter.setTagNumber(0x01);
        propertyIdentifierServiceParameter.setLengthValueType(0x01);
        propertyIdentifierServiceParameter.setPayload(new byte[] { (byte) deviceProperty.getPropertyKey() });

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
//      apdu.setUnconfirmedServiceChoice(UnconfirmedServiceChoice.READ_PROPERTY);
        apdu.setConfirmedServiceChoice(ConfirmedServiceChoice.READ_PROPERTY);
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

//      final byte[] bytes = result.getBytes();
//      LOG.info(Utils.byteArrayToStringNoPrefix(bytes));

        return result;
    }

    @Override
    public Message requestObjectList(final ObjectType objectType, final int bacnetID) {

        final VirtualLinkControl virtualLinkControl = new VirtualLinkControl();
        virtualLinkControl.setType(0x81);
        virtualLinkControl.setFunction(0x0A);
        // not known right now. Length is set later, when the full package data was
        // added
        virtualLinkControl.setLength(0x00);

        // NPDU including destination network information
        final NPDU outNpdu = new NPDU();
        outNpdu.setVersion(0x01);

        // no additional information
        // this works, if the cp is connected to the device directly via 192.168.2.1
        outNpdu.setControl(0x00);
        // npdu.setControl(0x2c);

        // this object identifier has to be context specific. I do not know why
        final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
        objectIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        objectIdentifierServiceParameter.setTagNumber(0x00);
        objectIdentifierServiceParameter.setLengthValueType(0x04);
        objectIdentifierServiceParameter.setObjectType(objectType);
        objectIdentifierServiceParameter.setInstanceNumber(bacnetID);

        final ServiceParameter propertyIdentifierServiceParameter = new ServiceParameter();
        propertyIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        propertyIdentifierServiceParameter.setTagNumber(0x01);
        propertyIdentifierServiceParameter.setLengthValueType(0x01);
        propertyIdentifierServiceParameter.setPayload(new byte[] { (byte) DeviceProperty.OBJECT_LIST });

        final APDU outApdu = new APDU();
        outApdu.setPduType(PDUType.CONFIRMED_SERVICE_REQUEST_PDU);
        outApdu.setInvokeId(1);
        outApdu.setConfirmedServiceChoice(ConfirmedServiceChoice.READ_PROPERTY);

        // this message is segmented, yes or no
        outApdu.setSegmentation(false);

        if (TEST_SEGMENTATION) {

            // allow segmentation for large responses
            outApdu.setSegmentedResponseAccepted(true);
//
////        outApdu.setMaxResponseSegmentsAccepted(30);
            outApdu.setMaxResponseSegmentsAccepted(16);
//
//        // binary 0000b (0d) - MinimumMessageSize (50 Octets)
//        // binary 0001b (1d) - MinimumMessageSize (128 Octets)
//        // binary 0010b (2d) - MinimumMessageSize (206 Octets)
//        // binary 0011b (3d) - MinimumMessageSize (480 Octets)
//        // binary 0100b (4d) - MinimumMessageSize (1024 Octets)
//        // binary 0101b (5d) - MinimumMessageSize (1476 Octets)

            // for testing, choose the absolute smallest messages size so that the partner
            // is forced into segmenting the response
            outApdu.setSizeOfMaximumAPDUAccepted(0);
//        outApdu.setSizeOfMaximumAPDUAccepted(5);

//        outApdu.setSizeOfMaximumAPDUAccepted(500); --> aborted

        }

        // page 57 in Standard 135-2012

        outApdu.getServiceParameters().add(objectIdentifierServiceParameter);
        outApdu.getServiceParameters().add(propertyIdentifierServiceParameter);

        final DefaultMessage outMessage = new DefaultMessage();
        outMessage.setVirtualLinkControl(virtualLinkControl);
        outMessage.setNpdu(outNpdu);
        outMessage.setApdu(outApdu);

        virtualLinkControl.setLength(outMessage.getDataLength());

        return outMessage;
    }

    public Map<Integer, String> getVendorMap() {
        return vendorMap;
    }

    @Override
    public void setVendorMap(final Map<Integer, String> vendorMap) {
        this.vendorMap = vendorMap;
    }

}
