package de.bacnetz.controller;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.bacnetz.common.utils.NetworkUtils;
import de.bacnetz.common.utils.Utils;
import de.bacnetz.devices.Device;
import de.bacnetz.devices.DeviceProperty;
import de.bacnetz.devices.DevicePropertyType;
import de.bacnetz.devices.ObjectType;
import de.bacnetz.factory.DefaultMessageFactory;
import de.bacnetz.factory.MessageFactory;
import de.bacnetz.stack.APDU;
import de.bacnetz.stack.ConfirmedServiceChoice;
import de.bacnetz.stack.NPDU;
import de.bacnetz.stack.ObjectIdentifierServiceParameter;
import de.bacnetz.stack.PDUType;
import de.bacnetz.stack.ServiceParameter;
import de.bacnetz.stack.TagClass;
import de.bacnetz.stack.UnconfirmedServiceChoice;
import de.bacnetz.stack.VirtualLinkControl;

public class DefaultMessageController implements MessageController {

    private static final Logger LOG = LogManager.getLogger(DefaultMessageController.class);

    private Device device;

    private Map<Integer, String> vendorMap = new HashMap<>();

    private final MessageFactory messageFactory = new DefaultMessageFactory();

//	private final int answerLength = 1;

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

        final ConfirmedServiceChoice confirmedServiceChoice = message.getApdu().getConfirmedServiceChoice();
        if (confirmedServiceChoice != null) {
            switch (confirmedServiceChoice) {
            default:
                LOG.warn("Not implemented: {} ", confirmedServiceChoice);
            }

            return null;
        }

        final UnconfirmedServiceChoice unconfirmedServiceChoice = message.getApdu().getUnconfirmedServiceChoice();
        if (unconfirmedServiceChoice != null) {
            switch (unconfirmedServiceChoice) {

            case I_AM:
                LOG.info(">>> I_AM received!");
                return processIAMMessage(message);

            /** 20.1.3 BACnet-Unconfirmed-Request-PDU */
            case I_HAVE:
                LOG.info(">>> I_HAVE received!");
                // throw new RuntimeException(">>> Not implemented yet! Message: " +
                // message.getApdu().getServiceChoice());
                return null;

            /** 20.1.4 BACnet-SimpleACK-PDU */
            case UNCONFIRMED_COV_NOTIFICATION:
                LOG.info(">>> UNCONFIRMED_COV_NOTIFICATION received!");
                // throw new RuntimeException(">>> Not implemented yet! Message: " +
                // message.getApdu().getServiceChoice());
                return null;

            /** 20.1.5 BACnet-ComplexACK-PDU */
            case UNCONFIRMED_EVENT_NOTIFICATION:
                LOG.info(">>> UNCONFIRMED_EVENT_NOTIFICATION received!");
                // throw new RuntimeException(">>> Not implemented yet! Message: " +
                // message.getApdu().getServiceChoice());
                return null;

            /** 20.1.6 BACnet-SegmentACK-PDU */
            case UNCONFIRMED_PRIVATE_TRANSFER:
                LOG.info(">>> UNCONFIRMED_PRIVATE_TRANSFER received!");
                // throw new RuntimeException(">>> Not implemented yet! Message: " +
                // message.getApdu().getServiceChoice());
                return null;

            /** 20.1.7 BACnet-Error-PDU */
            case UNCONFIRMED_TEXT_MESSAGE:
                LOG.info(">>> UNCONFIRMED_TEXT_MESSAGE received!");
                // throw new RuntimeException(">>> Not implemented yet! Message: " +
                // message.getApdu().getServiceChoice());
                return null;

            /** 20.1.8 BACnet-Reject-PDU */
            case TIME_SYNCHRONIZATION:
                LOG.info(">>> TIME_SYNCHRONIZATION received!");
                // throw new RuntimeException(">>> Not implemented yet! Message: " +
                // message.getApdu().getServiceChoice());
                return null;

            /** 20.1.9 BACnet-Abort-PDU */
            case WHO_HAS:
                LOG.info(">>> WHO_HAS received!");
                // throw new RuntimeException(">>> Not implemented yet! Message: " +
                // message.getApdu().getServiceChoice());
                return null;

            /** 20.1.2 BACnet-Confirmed-Request-PDU */
            case WHO_IS:
                LOG.info(">>> WHO_IS received!");
                return processWhoIsMessage(message);

            case UTC_TIME_SYNCHRONIZATION:
                LOG.info(">>> UTC_TIME_SYNCHRONIZATION received!");
                // throw new RuntimeException(">>> Not implemented yet! Message: " +
                // message.getApdu().getServiceChoice());
                return null;

            case WRITE_GROUP:
                LOG.info(">>> WRITE_GROUP received!");
                // throw new RuntimeException(">>> Not implemented yet! Message: " +
                // message.getApdu().getServiceChoice());
                return null;

            case READ_PROPERTY:
                LOG.trace(">>> READ_PROPERTY received!");
                return processReadProperty(message);

            case READ_PROPERTY_MULTIPLE:
                LOG.trace(">>> READ_PROPERTY_MULTIPLE received!");
                return processReadPropertyMultiple(message);

            case WRITE_PROPERTY:
                LOG.trace(">>> WRITE_PROPERTY received!");
                return processWriteProperty(message);

            case DEVICE_COMMUNICATION_CONTROL:
                LOG.trace(">>> DEVICE_COMMUNICATION_CONTROL received!");
                return processDeviceCommunicationControl(message);

            case REINITIALIZE_DEVICE:
                LOG.trace(">>> REINITIALIZE_DEVICE received!");
                return processReinitializeDevice(message);

            default:
                LOG.warn(">>> Unknown message: " + message.getApdu().getUnconfirmedServiceChoice());
                return null;
            }
        }

        return null;
    }

    private Message processReinitializeDevice(final Message requestMessage) {

        final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = requestMessage.getApdu()
                .getObjectIdentifierServiceParameter();
        final Device findDevice = device.findDevice(objectIdentifierServiceParameter);

        // update the restart date
        findDevice.setTimeOfDeviceRestart(LocalDateTime.now());

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
            npdu.setDestinationNetworkNumber(NetworkUtils.DESTINATION_NETWORK_NUMBER);
            npdu.setDestinationMACLayerAddressLength(3);
            npdu.setDestinationMac(NetworkUtils.DEVICE_MAC_ADDRESS);

            npdu.setDestinationHopCount(255);
        }

        // TODO this parameter is not used
        final ObjectIdentifierServiceParameter resultObjectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
        resultObjectIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        resultObjectIdentifierServiceParameter.setTagNumber(0x00);
        resultObjectIdentifierServiceParameter.setLengthValueType(4);
        resultObjectIdentifierServiceParameter.setObjectType(findDevice.getObjectType());
        resultObjectIdentifierServiceParameter.setInstanceNumber(findDevice.getId());

        final APDU apdu = new APDU();
        apdu.setPduType(PDUType.SIMPLE_ACK_PDU);
        apdu.setInvokeId(requestMessage.getApdu().getInvokeId());
        apdu.setUnconfirmedServiceChoice(UnconfirmedServiceChoice.REINITIALIZE_DEVICE);
        apdu.setVendorMap(vendorMap);
//        apdu.getServiceParameters().add(resultObjectIdentifierServiceParameter);

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
        if (CollectionUtils.isNotEmpty(serviceParameters) && serviceParameters.size() == 2) {

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

                LOG.info("Ignoring Who-Is! DeviceID: {}, [{} - {}]", NetworkUtils.DEVICE_INSTANCE_NUMBER, lowerBound,
                        upperBound);
                return null;
            }
        }

        LOG.info("WHO_IS is not ignored!");

        // return Unconfirmed request i-Am device,10001
        final int deviceInstanceNumber = NetworkUtils.DEVICE_INSTANCE_NUMBER;

        final VirtualLinkControl virtualLinkControl = new VirtualLinkControl();
        virtualLinkControl.setType(0x81);
        virtualLinkControl.setFunction(0x0B);
        virtualLinkControl.setLength(0x00);

        // NPDU including destination network information
        final NPDU npdu = new NPDU();
        npdu.setVersion(0x01);
        npdu.setControl(0x20);
        npdu.setDestinationNetworkNumber(NetworkUtils.BROADCAST_NETWORK_NUMBER);
        // indicates broadcast on destination network
        npdu.setDestinationMACLayerAddressLength(0);
        npdu.setDestinationHopCount(255);

        final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
        objectIdentifierServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
        objectIdentifierServiceParameter.setTagNumber(ServiceParameter.BACNET_OBJECT_IDENTIFIER);
        objectIdentifierServiceParameter.setLengthValueType(4);
        objectIdentifierServiceParameter.setObjectType(ObjectType.DEVICE);
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
        apdu.setPduType(PDUType.UNCONFIRMED_SERVICE_REQUEST_PDU);
        apdu.setUnconfirmedServiceChoice(UnconfirmedServiceChoice.I_AM);
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

        LOG.trace("processWriteProperty()");
        final int propertyIdentifier = requestMessage.getApdu().getPropertyIdentifier();
        LOG.trace("Property Identifier: {}", propertyIdentifier);
        final int propertyIdentifierCode = propertyIdentifier;

        final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = requestMessage.getApdu()
                .getObjectIdentifierServiceParameter();
        LOG.info(">>> Write Identifier: {} ({}) Object Identifier: {}", propertyIdentifierCode,
                DevicePropertyType.getByCode(propertyIdentifierCode).getName(),
                objectIdentifierServiceParameter.toString());

        switch (propertyIdentifier) {

        // 0xCA = 202d
        case 0xCA:
            LOG.info("<<< WRITE_PROP: restart notification recipients ({})", propertyIdentifierCode);
            return processWriteRestartNotificationRecipientsProperty(propertyIdentifierCode, requestMessage);

        default:
            return processWriteRestartNotificationRecipientsProperty(propertyIdentifierCode, requestMessage);
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
            npdu.setDestinationNetworkNumber(NetworkUtils.DESTINATION_NETWORK_NUMBER);
            npdu.setDestinationMACLayerAddressLength(3);
            npdu.setDestinationMac(NetworkUtils.DEVICE_MAC_ADDRESS);

            npdu.setDestinationHopCount(255);
        }

        // TODO this object is not used!
        final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
        objectIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        objectIdentifierServiceParameter.setTagNumber(0x00);
        objectIdentifierServiceParameter.setLengthValueType(4);
        objectIdentifierServiceParameter.setObjectType(ObjectType.DEVICE);
        objectIdentifierServiceParameter.setInstanceNumber(NetworkUtils.DEVICE_INSTANCE_NUMBER);

        // TODO this object is not used!
        final ServiceParameter propertyIdentifierServiceParameter = new ServiceParameter();
        propertyIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        propertyIdentifierServiceParameter.setTagNumber(0x01);
        propertyIdentifierServiceParameter.setLengthValueType(0x01);
        propertyIdentifierServiceParameter.setPayload(new byte[] { (byte) propertyIdentifierCode });

        final APDU apdu = new APDU();
        apdu.setPduType(PDUType.SIMPLE_ACK_PDU);
        apdu.setInvokeId(requestMessage.getApdu().getInvokeId());
        apdu.setUnconfirmedServiceChoice(UnconfirmedServiceChoice.WRITE_PROPERTY);
        apdu.setVendorMap(vendorMap);

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

        final int propertyIdentifierCode = requestMessage.getApdu().getPropertyIdentifier();
        LOG.trace("Property Identifier: {}", propertyIdentifierCode);

        final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = requestMessage.getApdu()
                .getObjectIdentifierServiceParameter();

        LOG.trace(">>> Property Identifier: {} ({}) Object Identifier: {}", propertyIdentifierCode,
                DevicePropertyType.getByCode(propertyIdentifierCode).getName(),
                objectIdentifierServiceParameter.toString());

        // find device
        final Device targetDevice = device.findDevice(objectIdentifierServiceParameter);

        return targetDevice.getPropertyValue(requestMessage, propertyIdentifierCode);
    }

    private Message processReadPropertyMultiple(final Message requestMessage) {

        final ObjectIdentifierServiceParameter targetObjectIdentifierServiceParameter = requestMessage.getApdu()
                .getObjectIdentifierServiceParameter();

        // find device
        final Device targetDevice = device.findDevice(targetObjectIdentifierServiceParameter);

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
        objectIdentifierServiceParameter.setObjectType(ObjectType.DEVICE);
        objectIdentifierServiceParameter.setInstanceNumber(NetworkUtils.DEVICE_INSTANCE_NUMBER);

        final APDU apdu = new APDU();
        apdu.setPduType(PDUType.COMPLEX_ACK_PDU);
        apdu.setInvokeId(requestMessage.getApdu().getInvokeId());
        apdu.setUnconfirmedServiceChoice(UnconfirmedServiceChoice.READ_PROPERTY_MULTIPLE);
        apdu.setVendorMap(vendorMap);
        apdu.getServiceParameters().add(objectIdentifierServiceParameter);

        // opening {[1]
        final ServiceParameter openingTagServiceParameter = new ServiceParameter();
        openingTagServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        openingTagServiceParameter.setTagNumber(0x01);
        openingTagServiceParameter.setLengthValueType(ServiceParameter.OPENING_TAG_CODE);
        apdu.getServiceParameters().add(openingTagServiceParameter);

        LOG.trace(openingTagServiceParameter);

        // read the service parameters to find out which properties where requested or
        // if the 'all' keyword was sent for all properties
        final List<ServiceParameter> serviceParameters = requestMessage.getApdu().getServiceParameters();
        if (CollectionUtils.isEmpty(serviceParameters)) {
            LOG.warn("No service parameters in readPropertyMultiple request");
        }

//		answerLength = 100;

        boolean withinRequestedProperties = false;
        for (final ServiceParameter serviceParameter : serviceParameters) {

            LOG.trace(serviceParameter);

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

                int index = 0;

                for (final DeviceProperty<?> deviceProperty : targetDevice.getProperties().values()) {

//                    // object-list is not part of the 'all' collection
//                    if (deviceProperty.getPropertyKey() != DeviceProperty.OBJECT_LIST) {
//                        LOG.info("SKIPPING ServiceParameter for DeviceProperty " + index + ") " + deviceProperty
//                                + " ...");
//                        continue;
//                    }

                    if (deviceProperty.getPropertyKey() == DeviceProperty.PROPERTY_LIST) {
                        LOG.info("SKIPPING ServiceParameter for DeviceProperty " + index + ") " + deviceProperty
                                + " ...");
                        continue;
                    }

                    if (deviceProperty.getPropertyKey() == DeviceProperty.TIME_OF_STATE_COUNT_RESET) {
                        LOG.info("SKIPPING ServiceParameter for DeviceProperty " + index + ") " + deviceProperty
                                + " ...");
                        continue;
                    }

//					if (deviceProperty.getPropertyKey() != DeviceProperty.VENDOR_NAME) {
//						LOG.info("SKIPPING ServiceParameter for DeviceProperty " + index + ") " + deviceProperty
//								+ " ...");
//						continue;
//					}

//					if (deviceProperty.getPropertyKey() == DeviceProperty.DAYLIGHT_SAVINGS_STATUS) {
//						LOG.info("test");
//					}
//
//					if (deviceProperty.getPropertyKey() != DeviceProperty.DAYLIGHT_SAVINGS_STATUS) {
//						continue;
//					}

//					if (index != answerLength) {
//						answerLength++;
//						LOG.info("Next length: " + answerLength);
//						break;
//					}

                    index++;

                    LOG.info("Adding ServiceParameter for DeviceProperty " + index + ") " + deviceProperty + " ...");

                    // add the property identifier
                    final ServiceParameter propertyIdentifierServiceParameter = new ServiceParameter();
                    propertyIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
                    propertyIdentifierServiceParameter.setTagNumber(2);
                    propertyIdentifierServiceParameter.setLengthValueType(1);
                    propertyIdentifierServiceParameter
                            .setPayload(new byte[] { (byte) deviceProperty.getPropertyKey() });
                    apdu.getServiceParameters().add(propertyIdentifierServiceParameter);

                    LOG.trace(propertyIdentifierServiceParameter);

                    // add the property value
                    addPropertyValue(apdu, deviceProperty);

                    LOG.trace("Adding ServiceParameter for DeviceProperty: " + deviceProperty + " done.");
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
        LOG.trace(closingTagServiceParameter);

        final DefaultMessage result = new DefaultMessage();
        result.setVirtualLinkControl(virtualLinkControl);
        result.setNpdu(npdu);
        result.setApdu(apdu);

        LOG.trace("All service parameters ...");
        if (CollectionUtils.isNotEmpty(apdu.getServiceParameters())) {
            for (final ServiceParameter serviceParameter : apdu.getServiceParameters()) {
                final byte[] temp = new byte[100];
                serviceParameter.toBytes(temp, 0);

                LOG.trace(serviceParameter + " - " + Utils.byteArrayToStringNoPrefix(temp));
            }
        }
        LOG.trace("All service parameters done.");

        virtualLinkControl.setLength(result.getDataLength());

//		final byte[] bytes = result.getBytes();
//		LOG.info(Utils.byteArrayToStringNoPrefix(bytes));

        return result;
    }

    private void addPropertyValue(final APDU apdu, final DeviceProperty<?> deviceProperty) {

        // opening tag {[4]
        final ServiceParameter openingTagServiceParameter = new ServiceParameter();
        openingTagServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        openingTagServiceParameter.setTagNumber(0x04);
        openingTagServiceParameter.setLengthValueType(ServiceParameter.OPENING_TAG_CODE);
        apdu.getServiceParameters().add(openingTagServiceParameter);
        LOG.trace(openingTagServiceParameter);

        deviceProperty.getServiceParameters().stream().forEach(sp -> {

            apdu.getServiceParameters().add(sp);
            LOG.trace(sp);

        });

        // closing tag }[4]
        final ServiceParameter closingTagServiceParameter = new ServiceParameter();
        closingTagServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        closingTagServiceParameter.setTagNumber(0x04);
        closingTagServiceParameter.setLengthValueType(ServiceParameter.CLOSING_TAG_CODE);
        apdu.getServiceParameters().add(closingTagServiceParameter);
        LOG.trace(closingTagServiceParameter);
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

        final ServiceParameter segmentationSupportedServiceParameter = new ServiceParameter();
        segmentationSupportedServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
        segmentationSupportedServiceParameter.setTagNumber(ServiceParameter.ENUMERATED_CODE);
        segmentationSupportedServiceParameter.setLengthValueType(1);
        // 0x00 == system-status: operational
        segmentationSupportedServiceParameter.setPayload(new byte[] { (byte) 0x00 });
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

    public Device getDevice() {
        return device;
    }

    public void setDevice(final Device device) {
        this.device = device;
    }

}

//private Message processLastRestartReasonProperty(final int propertyKey, final Message requestMessage) {
//// coldstart 1
//return messageFactory.create(MessageType.ENUMERATED, NetworkUtils.DEVICE_INSTANCE_NUMBER,
//      requestMessage.getApdu().getInvokeId(), propertyKey, new byte[] { (byte) 0x01 });
//}
//
//private Message processProtocolVersionProperty(final int propertyKey, final Message requestMessage) {
//// protocol version 1
//return messageFactory.create(MessageType.INTEGER_PROPERTY, NetworkUtils.DEVICE_INSTANCE_NUMBER,
//      requestMessage.getApdu().getInvokeId(), propertyKey, new byte[] { (byte) 0x01 });
//}
//
//private Message processProtocolRevisionProperty(final int propertyKey, final Message requestMessage) {
//// protocol revision 0x0C = 12d
//return messageFactory.create(MessageType.INTEGER_PROPERTY, NetworkUtils.DEVICE_INSTANCE_NUMBER,
//      requestMessage.getApdu().getInvokeId(), propertyKey, new byte[] { (byte) 0x0C });
//}

//private Message processDatabaseRevisionProperty(final int propertyKey, final Message requestMessage) {
//// database revivion 3
//return messageFactory.create(MessageType.INTEGER_PROPERTY, NetworkUtils.DEVICE_INSTANCE_NUMBER,
//      requestMessage.getApdu().getInvokeId(), propertyKey, new byte[] { (byte) 0x03 });
//}
//
//private Message processAPDUSegmentTimeoutProperty(final int propertyKey, final Message requestMessage) {
//
//// APDU Segment-Timeout:
//// Dieser Wert in Millisekunden legt fest, nach welcher Zeitspanne ein
//// quittierpflichtiges, segmentiertes Telegramm als fehlgeschlagen gewertet
//// wird, wenn die Segmentbestätigung ausbleibt. Der Standardwert beträgt
//// 2000 Millisekunden.
//// 2000d == 0x07D0
//return messageFactory.create(MessageType.INTEGER_PROPERTY, NetworkUtils.DEVICE_INSTANCE_NUMBER,
//      requestMessage.getApdu().getInvokeId(), propertyKey, new byte[] { (byte) 0x07, (byte) 0xD0 });
//}
//
//private Message processMaxSegmentsAcceptedProperty(final int propertyKey, final Message requestMessage) {
//
//// APDU Max Segments Accepted:
//// Legt fest, wie viele Segmente maximal akzeptiert werden.
//return messageFactory.create(MessageType.INTEGER_PROPERTY, NetworkUtils.DEVICE_INSTANCE_NUMBER,
//      requestMessage.getApdu().getInvokeId(), propertyKey, new byte[] { (byte) 0x01 });
//}
//
//private Message processAPDUTimeoutProperty(final int propertyKey, final Message requestMessage) {
//
//// ADPU Timeout:
//// Dieser Wert in Millisekunden legt fest, nach welcher Zeitspanne ein
//// quittierpflichtiges Telegramm als fehlgeschlagen gewertet wird, wenn die
//// Bestätigung ausbleibt. Der Standardwert beträgt 3000 ms.
//// 3000d == 0x0BB8
//return messageFactory.create(MessageType.INTEGER_PROPERTY, NetworkUtils.DEVICE_INSTANCE_NUMBER,
//      requestMessage.getApdu().getInvokeId(), propertyKey, new byte[] { (byte) 0x0B, (byte) 0xB8 });
//}
//
//private Message processMaxAPDULengthAcceptedProperty(final int propertyKey, final Message requestMessage) {
//
//// Maximum APDU Length is dependent on the physical layer used, for example the
//// maximum APDU size for BACnet/IP is 1497 octets, but for BACnet MS/TP
//// segments, the maximum APDU size is only 480 octets.
////
//// 1497d = 0x05D9
//// 62d = 0x3E
//return messageFactory.create(MessageType.INTEGER_PROPERTY, NetworkUtils.DEVICE_INSTANCE_NUMBER,
//      requestMessage.getApdu().getInvokeId(), propertyKey, new byte[] { (byte) 0x05, (byte) 0xD9 });
//}
//
//private Message processSegmentationSupportedProperty(final int propertyKey, final Message requestMessage) {
//
//// segmented-both (0)
//// segmented-transmit (1)
//// segmented-receive (2)
//// no-segmentation (3)
//return messageFactory.create(MessageType.ENUMERATED, NetworkUtils.DEVICE_INSTANCE_NUMBER,
//      requestMessage.getApdu().getInvokeId(), propertyKey, new byte[] { (byte) 0x00 });
//}