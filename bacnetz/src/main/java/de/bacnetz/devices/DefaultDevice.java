package de.bacnetz.devices;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.bacnetz.common.utils.BACnetUtils;
import de.bacnetz.common.utils.Utils;
import de.bacnetz.configuration.ConfigurationManager;
import de.bacnetz.configuration.DefaultConfigurationManager;
import de.bacnetz.controller.DefaultMessage;
import de.bacnetz.controller.DefaultMessageController;
import de.bacnetz.controller.Message;
import de.bacnetz.factory.MessageFactory;
import de.bacnetz.factory.MessageType;
import de.bacnetz.services.CommunicationService;
import de.bacnetz.stack.APDU;
import de.bacnetz.stack.BACnetProtocolObjectTypesSupportedBitString;
import de.bacnetz.stack.BACnetServicesSupportedBitString;
import de.bacnetz.stack.BaseBitString;
import de.bacnetz.stack.COVSubscription;
import de.bacnetz.stack.ConfirmedServiceChoice;
import de.bacnetz.stack.ErrorClass;
import de.bacnetz.stack.ErrorCode;
import de.bacnetz.stack.LinkLayerType;
import de.bacnetz.stack.NPDU;
import de.bacnetz.stack.ObjectIdentifierServiceParameter;
import de.bacnetz.stack.PDUType;
import de.bacnetz.stack.ServiceParameter;
import de.bacnetz.stack.StatusFlagsBitString;
import de.bacnetz.stack.TagClass;
import de.bacnetz.stack.VirtualLinkControl;
import de.bacnetz.threads.MulticastListenerReaderThread;

/**
 * A device is a hierarchy of devices and child devices. A FourDoorSolution for
 * example is a device that contains four child devices, one for each of the
 * four doors.
 * 
 * Each door in turn contains a binary child device, which models the door's
 * state (open or closed). This describes a three level hierarchy of devices.
 * 
 * Check the DefaultDeviceFactory class to see how the devices are assembled.
 * Each device stores a reference to it's optional parent device.
 */
public class DefaultDevice implements Device, CommunicationService {

    private static final Logger LOG = LogManager.getLogger(DefaultDevice.class);

    /** the optional parent device in the hiearchy of objects */
    private Device parentDevice;

    /** this map contains all child devices */
    private final Map<ObjectIdentifierServiceParameter, Device> deviceMap = new HashMap<>();

    private final Map<Integer, DeviceProperty<?>> properties = new HashMap<>();

    private final Collection<Device> children = new ArrayList<>();

    private int id;

    private ObjectType objectType;

    private String name;

    private String description;

    private String location;

    private MessageFactory messageFactory;

    private Map<Integer, String> vendorMap = new HashMap<>();

    private int vendorId;

    /** invoke id used in APDU (currently only in ToogleDoorOpenStateThread */
    private final AtomicInteger invokeId = new AtomicInteger(0);

    private boolean outOfService;

    private final List<String> states = new ArrayList<>();

    private String firmwareRevision;

    private LocalDateTime timeOfDeviceRestart = LocalDateTime.now();

    private final Set<COVSubscription> covSubscriptions = new HashSet<>();

    private DatagramSocket datagramSocket;

    private ConfigurationManager configurationManager;

    /**
     * Every device that is created (every four door solution, every TZ320, will
     * bind to a port. On that port a MulticastListenerReaderThread is started to
     * listen for bacnet messages.
     */
    private MulticastListenerReaderThread multicastListenerReaderThread;

    private int tempActionId;

    /**
     * Internal service (no spring bean). Used during binding of this device to a
     * port.
     */
    private DefaultDeviceService deviceService;

    /**
     * Internal service (no spring bean). Used during binding of this device to a
     * port.
     */
    private DefaultMessageController messageController;

    /**
     * ctor
     */
    public DefaultDevice() {

    }

    @Override
    public Device findDevice(final ServiceParameter serviceParameter) {

        final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = (ObjectIdentifierServiceParameter) serviceParameter;

        // find the device itself
        if (objectIdentifierServiceParameter.getObjectType() == objectType
                && objectIdentifierServiceParameter.getInstanceNumber() == id) {
            return this;
        }

        if (CollectionUtils.isEmpty(children)) {
            return null;
        }

        // find the object in the list of objects by instance type and instance id
        final Optional<Device> childDevice = getChildDevices().stream()
                .filter(d -> (d.getObjectIdentifierServiceParameter().equals(objectIdentifierServiceParameter)))
                .findFirst();

        return childDevice.isPresent() ? childDevice.get() : null;
    }

    @Override
    public ObjectIdentifierServiceParameter getObjectIdentifierServiceParameter() {

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

    private ObjectIdentifierServiceParameter createDeviceServiceParameter() {

        final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
        objectIdentifierServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
        objectIdentifierServiceParameter.setTagNumber(ServiceParameter.BACNET_OBJECT_IDENTIFIER);
        objectIdentifierServiceParameter.setLengthValueType(0x04);
        objectIdentifierServiceParameter.setObjectType(objectType);
        objectIdentifierServiceParameter.setInstanceNumber(id);

        return objectIdentifierServiceParameter;
    }

    private ObjectIdentifierServiceParameter createNotificationClassServiceParameter() {

        final ObjectIdentifierServiceParameter notificationServiceParameter = new ObjectIdentifierServiceParameter();
        notificationServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
        notificationServiceParameter.setTagNumber(ServiceParameter.BACNET_OBJECT_IDENTIFIER);
        notificationServiceParameter.setLengthValueType(0x04);
        notificationServiceParameter.setObjectType(ObjectType.NOTIFICATION_CLASS);
        notificationServiceParameter.setInstanceNumber(id);

        return notificationServiceParameter;
    }

    private ObjectIdentifierServiceParameter createBinaryInputServiceParameter() {

        final ObjectIdentifierServiceParameter binaryInputServiceParameter = new ObjectIdentifierServiceParameter();
        binaryInputServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
        binaryInputServiceParameter.setTagNumber(ServiceParameter.BACNET_OBJECT_IDENTIFIER);
        binaryInputServiceParameter.setLengthValueType(0x04);
        binaryInputServiceParameter.setObjectType(ObjectType.BINARY_INPUT);
        binaryInputServiceParameter.setInstanceNumber(id);

        return binaryInputServiceParameter;
    }

    private ObjectIdentifierServiceParameter createMultiStateValueServiceParameter() {

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

            LOG.error("Property {} ({}) not available in device {}! Sending error!",
                    DevicePropertyType.getByCode(propertyIdentifierCode).name(), propertyIdentifierCode,
                    getObjectIdentifierServiceParameter().toString());

            return messageFactory.createErrorMessage(requestMessage, ErrorClass.PROPERTY.getCode(),
                    ErrorCode.UNKNOWN_PROPERTY.getCode());
        }

        return messageFactory.create(deviceProperty, this, requestMessage);
    }

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
        npdu.copyNetworkInformation(requestMessage.getNpdu());

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
        npdu.copyNetworkInformation(requestMessage.getNpdu());

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
        apdu.setConfirmedServiceChoice(ConfirmedServiceChoice.READ_PROPERTY);
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

        return result;
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
        npdu.copyNetworkInformation(requestMessage.getNpdu());

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
        npdu.copyNetworkInformation(requestMessage.getNpdu());

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
        npdu.copyNetworkInformation(requestMessage.getNpdu());

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

        return result;
    }

    // Returns a number that has all bits same as n
    // except the k'th bit which is made 0
    int turnOffK(final int n, final int k) {

        // k must be greater than 0
        if (k <= 0) {
            return n;
        }

        // Do & of n with a number with all set bits except
        // the k'th bit
        return (n & ~(1 << (k - 1)));
    }

    /**
     * TODO: Not yet moved into the DefaultMessageFactory.
     * 
     * @param propertyIdentifierCode
     * @param requestMessage
     * @return
     */
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
        npdu.copyNetworkInformation(requestMessage.getNpdu());

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
        npdu.copyNetworkInformation(requestMessage.getNpdu());

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
        apdu.setConfirmedServiceChoice(ConfirmedServiceChoice.READ_PROPERTY);
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
//        return retrieveIO420ServicesSupported();
//        return retrieveIO420ServicesSupportedModified();
//        return retrieveAllSameValueServicesSupported(false);
        return retrieveTZ320ServicesSupportedModified();
    }

    private BACnetServicesSupportedBitString retrieveTZ320ServicesSupportedModified() {

        final boolean value = false;

        final BACnetServicesSupportedBitString bacnetServicesSupportedBitString = new BACnetServicesSupportedBitString();

        // byte 1
        bacnetServicesSupportedBitString.setAcknowledgeAlarm(true);
        bacnetServicesSupportedBitString.setConfirmedCOVNotification(value);
        bacnetServicesSupportedBitString.setConfirmedEventNotification(value);
        bacnetServicesSupportedBitString.setGetAlarmSummary(true);

        bacnetServicesSupportedBitString.setGetEnrollmentSummary(true);
        bacnetServicesSupportedBitString.setSubscribeCOV(true);
        bacnetServicesSupportedBitString.setAtomicReadFile(true);
        bacnetServicesSupportedBitString.setAtomicWriteFile(true);

        // byte 2
        bacnetServicesSupportedBitString.setAddListElement(true);
        bacnetServicesSupportedBitString.setRemoveListElement(true);
        bacnetServicesSupportedBitString.setCreateObject(value);
        bacnetServicesSupportedBitString.setDeleteObject(value);

        bacnetServicesSupportedBitString.setReadProperty(true);
        bacnetServicesSupportedBitString.setReadPropertyConditional(value);
        bacnetServicesSupportedBitString.setReadPropertyMultiple(true);
        bacnetServicesSupportedBitString.setWriteProperty(true);

        // byte 3
        bacnetServicesSupportedBitString.setWritePropertyMultiple(true);
        bacnetServicesSupportedBitString.setDeviceCommunicationControl(true);
        bacnetServicesSupportedBitString.setConfirmedPrivateTransfer(true);
        bacnetServicesSupportedBitString.setConfirmedTextMessage(value);

        bacnetServicesSupportedBitString.setReinitializeDevice(true);
        bacnetServicesSupportedBitString.setVtOpen(value);
        bacnetServicesSupportedBitString.setVtClose(value);
        bacnetServicesSupportedBitString.setVtData(value);

        // byte 4
        bacnetServicesSupportedBitString.setAuthenticate(value);
        bacnetServicesSupportedBitString.setRequestKey(value);
        bacnetServicesSupportedBitString.setiAm(true);
        bacnetServicesSupportedBitString.setiHave(true);

        bacnetServicesSupportedBitString.setUnconfirmedCOVNotification(value);
        bacnetServicesSupportedBitString.setUnconfirmedEventNotification(value);
        bacnetServicesSupportedBitString.setUnconfirmedPrivateTransfer(true);
        bacnetServicesSupportedBitString.setUnconfirmedTextMessage(value);

        // byte 5
        bacnetServicesSupportedBitString.setTimeSynchronization(true);
        bacnetServicesSupportedBitString.setWhoHas(true);
        bacnetServicesSupportedBitString.setWhoIs(true);
        bacnetServicesSupportedBitString.setReadRange(value);

        bacnetServicesSupportedBitString.setUtcTimeSynchronization(true);
        bacnetServicesSupportedBitString.setLifeSafetyOperation(value);
        bacnetServicesSupportedBitString.setSubscribeCOVProperty(value);
        bacnetServicesSupportedBitString.setGetEventInformation(true);

        return bacnetServicesSupportedBitString;
    }

    /**
     * <pre>
     * Loytec:
     * protocol-services-supported: (Bit String) (FTFFFFTTTTFFTFTTTTFFTFFFFFTFTFFFTTTTTFTF)
     * 
     * IO-420 FourDoorSolution:
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

    private BACnetServicesSupportedBitString retrieveAllSameValueServicesSupported(final boolean value) {

        final BACnetServicesSupportedBitString bacnetServicesSupportedBitString = new BACnetServicesSupportedBitString();

        // byte 1
        bacnetServicesSupportedBitString.setAcknowledgeAlarm(value);
        bacnetServicesSupportedBitString.setConfirmedCOVNotification(value);
        bacnetServicesSupportedBitString.setConfirmedEventNotification(value);
        bacnetServicesSupportedBitString.setGetAlarmSummary(value);
        bacnetServicesSupportedBitString.setGetEnrollmentSummary(value);
        bacnetServicesSupportedBitString.setSubscribeCOV(value);
        bacnetServicesSupportedBitString.setAtomicReadFile(value);
        bacnetServicesSupportedBitString.setAtomicWriteFile(value);

        // byte 2
        bacnetServicesSupportedBitString.setAddListElement(value);
        bacnetServicesSupportedBitString.setRemoveListElement(value);
        bacnetServicesSupportedBitString.setCreateObject(value);
        bacnetServicesSupportedBitString.setDeleteObject(value);
        bacnetServicesSupportedBitString.setReadProperty(value);
        bacnetServicesSupportedBitString.setReadPropertyConditional(value);
        bacnetServicesSupportedBitString.setReadPropertyMultiple(value);
        bacnetServicesSupportedBitString.setWriteProperty(value);

        // byte 3
        bacnetServicesSupportedBitString.setWritePropertyMultiple(value);
        bacnetServicesSupportedBitString.setDeviceCommunicationControl(value);
        bacnetServicesSupportedBitString.setConfirmedPrivateTransfer(value);
        bacnetServicesSupportedBitString.setConfirmedTextMessage(value);
        bacnetServicesSupportedBitString.setReinitializeDevice(value);
        bacnetServicesSupportedBitString.setVtOpen(value);
        bacnetServicesSupportedBitString.setVtClose(value);
        bacnetServicesSupportedBitString.setVtData(value);

        // byte 4
        bacnetServicesSupportedBitString.setAuthenticate(value);
        bacnetServicesSupportedBitString.setRequestKey(value);
        bacnetServicesSupportedBitString.setiAm(value);
        bacnetServicesSupportedBitString.setiHave(value);
        bacnetServicesSupportedBitString.setUnconfirmedCOVNotification(value);
        bacnetServicesSupportedBitString.setUnconfirmedEventNotification(value);
        bacnetServicesSupportedBitString.setUnconfirmedPrivateTransfer(value);
        bacnetServicesSupportedBitString.setUnconfirmedTextMessage(value);

        // byte 5
        bacnetServicesSupportedBitString.setTimeSynchronization(value);
        bacnetServicesSupportedBitString.setWhoHas(value);
        bacnetServicesSupportedBitString.setWhoIs(value);
        bacnetServicesSupportedBitString.setReadRange(value);
        bacnetServicesSupportedBitString.setUtcTimeSynchronization(value);
        bacnetServicesSupportedBitString.setLifeSafetyOperation(value);
        bacnetServicesSupportedBitString.setSubscribeCOVProperty(value);
        bacnetServicesSupportedBitString.setGetEventInformation(value);

        return bacnetServicesSupportedBitString;
    }

    private BACnetServicesSupportedBitString retrieveIO420ServicesSupportedModified() {

        final BACnetServicesSupportedBitString bacnetServicesSupportedBitString = new BACnetServicesSupportedBitString();

        // byte 1
        bacnetServicesSupportedBitString.setAcknowledgeAlarm(true);
        bacnetServicesSupportedBitString.setConfirmedCOVNotification(true);
        bacnetServicesSupportedBitString.setConfirmedEventNotification(false);
        bacnetServicesSupportedBitString.setGetAlarmSummary(true);
        bacnetServicesSupportedBitString.setGetEnrollmentSummary(true);
        bacnetServicesSupportedBitString.setSubscribeCOV(true);
        bacnetServicesSupportedBitString.setAtomicReadFile(true);
        bacnetServicesSupportedBitString.setAtomicWriteFile(true);

        // byte 2
        bacnetServicesSupportedBitString.setAddListElement(true);
        bacnetServicesSupportedBitString.setRemoveListElement(true);
        bacnetServicesSupportedBitString.setCreateObject(false);
        bacnetServicesSupportedBitString.setDeleteObject(false);
        bacnetServicesSupportedBitString.setReadProperty(true);
        bacnetServicesSupportedBitString.setReadPropertyConditional(false);
        bacnetServicesSupportedBitString.setReadPropertyMultiple(true);
        bacnetServicesSupportedBitString.setWriteProperty(true);

        // byte 3
        bacnetServicesSupportedBitString.setWritePropertyMultiple(true);
        bacnetServicesSupportedBitString.setDeviceCommunicationControl(true);
        bacnetServicesSupportedBitString.setConfirmedPrivateTransfer(true);
        bacnetServicesSupportedBitString.setConfirmedTextMessage(false);
        bacnetServicesSupportedBitString.setReinitializeDevice(true);
        bacnetServicesSupportedBitString.setVtOpen(false);
        bacnetServicesSupportedBitString.setVtClose(false);
        bacnetServicesSupportedBitString.setVtData(false);

        // byte 4
        bacnetServicesSupportedBitString.setAuthenticate(false);
        bacnetServicesSupportedBitString.setRequestKey(false);
        bacnetServicesSupportedBitString.setiAm(true);
        bacnetServicesSupportedBitString.setiHave(true);
        bacnetServicesSupportedBitString.setUnconfirmedCOVNotification(true);
        bacnetServicesSupportedBitString.setUnconfirmedEventNotification(false);
        bacnetServicesSupportedBitString.setUnconfirmedPrivateTransfer(true);
        bacnetServicesSupportedBitString.setUnconfirmedTextMessage(false);

        // byte 5
        bacnetServicesSupportedBitString.setTimeSynchronization(true);
        bacnetServicesSupportedBitString.setWhoHas(true);
        bacnetServicesSupportedBitString.setWhoIs(true);
        bacnetServicesSupportedBitString.setReadRange(false);
        bacnetServicesSupportedBitString.setUtcTimeSynchronization(true);
        bacnetServicesSupportedBitString.setLifeSafetyOperation(false);
        bacnetServicesSupportedBitString.setSubscribeCOVProperty(true);
        bacnetServicesSupportedBitString.setGetEventInformation(true);

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
        bacnetServicesSupportedBitString.setSubscribeCOVProperty(true);
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

        final DeviceProperty<Object> deviceProperty = (DeviceProperty<Object>) properties
                .get(DevicePropertyType.PRESENT_VALUE.getCode());

        if (deviceProperty == null) {
            return 0;
        }

        final Object value = deviceProperty.getValue();

        if (value == null) {
            deviceProperty.setValue(0);
        }

        return properties.get(DevicePropertyType.PRESENT_VALUE.getCode()).getValue();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setPresentValue(final Object newPresentValue) {

        if (!properties.containsKey(DevicePropertyType.PRESENT_VALUE.getCode())) {
            return;
        }

        final DeviceProperty<Object> presentValueDeviceProperty = (DeviceProperty<Object>) properties
                .get(DevicePropertyType.PRESENT_VALUE.getCode());

        final Object oldPresentValue = presentValueDeviceProperty.getValue();

        boolean valueChanged = false;

        if ((oldPresentValue == null) && (newPresentValue != null)) {

            valueChanged = true;

        } else if ((oldPresentValue != null) && (newPresentValue == null)) {

            valueChanged = true;

        } else if ((oldPresentValue != null) && (newPresentValue != null)) {

            if (!oldPresentValue.equals(newPresentValue)) {

                valueChanged = true;
            }
        }

        LOG.info("NewPresentValue: {}, OldPresentValue: {}, ValueChanged: {}", newPresentValue, oldPresentValue,
                valueChanged);

        if (valueChanged) {

            presentValueDeviceProperty.setValue(newPresentValue);

            getParentDevice().onValueChanged(this, presentValueDeviceProperty, newPresentValue, newPresentValue);

            covSubscriptions.stream().forEach(s -> {
                s.valueChanged(newPresentValue);
            });
        }
    }

    @Override
    public void onValueChanged(final Device device, final DeviceProperty<Object> presentValueDeviceProperty,
            final Object oldPresentValue, final Object newPresentValue) {

        LOG.info("OnValueChange: Device: {}, DeviceProperty: {}, oldValue: {}, newValue: {}", device,
                presentValueDeviceProperty, oldPresentValue, newPresentValue);

        Device tz320State = null;
        Device lockState = null;
        Device closeState = null;

        // TZ320 logic - when the command property changed, also update the state
        // accordingly
        // TODO: reset the command property to 1 again!
        //
        // id = 4 => command multi_state_value
        if (device.getId() == 4) {

            final int newValue = (int) newPresentValue;
            switch (newValue) {

            // unlock
            case 2:
                tz320State = this.findDevice(ObjectIdentifierServiceParameter
                        .createFromTypeAndInstanceNumber(ObjectType.MULTI_STATE_VALUE, 3));
                tz320State.setPresentValue(1);

                // 1 == locked, 0 == unlocked
                lockState = this.findDevice(
                        ObjectIdentifierServiceParameter.createFromTypeAndInstanceNumber(ObjectType.BINARY_INPUT, 1));
                lockState.setPresentValue(0);

                // 1 == closed, 0 == open
                // lets leave the door close state on 1 always for now
                closeState = this.findDevice(
                        ObjectIdentifierServiceParameter.createFromTypeAndInstanceNumber(ObjectType.BINARY_INPUT, 2));
                closeState.setPresentValue(0);
                break;

            // lock
            case 3:
                tz320State = this.findDevice(ObjectIdentifierServiceParameter
                        .createFromTypeAndInstanceNumber(ObjectType.MULTI_STATE_VALUE, 3));
                tz320State.setPresentValue(4);

                // 1 == locked, 0 == unlocked
                lockState = this.findDevice(
                        ObjectIdentifierServiceParameter.createFromTypeAndInstanceNumber(ObjectType.BINARY_INPUT, 1));
                lockState.setPresentValue(1);

                // 1 == closed, 0 == open
                closeState = this.findDevice(
                        ObjectIdentifierServiceParameter.createFromTypeAndInstanceNumber(ObjectType.BINARY_INPUT, 2));
                closeState.setPresentValue(1);
                break;

            // short time release (Kurzzeitfreigabe, KZF)
            case 4:
                tz320State = this.findDevice(ObjectIdentifierServiceParameter
                        .createFromTypeAndInstanceNumber(ObjectType.MULTI_STATE_VALUE, 3));
                tz320State.setPresentValue(6);

                // 1 == locked, 0 == unlocked
                lockState = this.findDevice(
                        ObjectIdentifierServiceParameter.createFromTypeAndInstanceNumber(ObjectType.BINARY_INPUT, 1));
                lockState.setPresentValue(0);

                // 1 == closed, 0 == open
                closeState = this.findDevice(
                        ObjectIdentifierServiceParameter.createFromTypeAndInstanceNumber(ObjectType.BINARY_INPUT, 2));
                closeState.setPresentValue(1);
                break;

            default:
                break;
            }
        }
    }

    @Override
    public void executeAction() {

        allToggle();
//        moduloToggle();
    }

    private void allToggle() {

        LOG.info("Toogling all doors on device: '{}'", getId());

        final int startId = 0;

        LOG.trace("Toggling door 1 ...");
        final ObjectIdentifierServiceParameter createFromTypeAndInstanceNumber1 = ObjectIdentifierServiceParameter
                .createFromTypeAndInstanceNumber(ObjectType.BINARY_INPUT, startId + 1);
        final Device door1CloseStateBinaryInput = findDevice(createFromTypeAndInstanceNumber1);
        if (door1CloseStateBinaryInput != null) {
            final byte[] byteArray1 = (byte[]) door1CloseStateBinaryInput.getPresentValue();
            door1CloseStateBinaryInput.setPresentValue(new byte[] { (byte) (1 - byteArray1[0]) });
        }

        LOG.trace("Toggling door 2 ...");
        final ObjectIdentifierServiceParameter createFromTypeAndInstanceNumber2 = ObjectIdentifierServiceParameter
                .createFromTypeAndInstanceNumber(ObjectType.BINARY_INPUT, startId + 2);
        final Device door2CloseStateBinaryInput = findDevice(createFromTypeAndInstanceNumber2);
        if (door2CloseStateBinaryInput != null) {
            final byte[] byteArray2 = (byte[]) door2CloseStateBinaryInput.getPresentValue();
            door2CloseStateBinaryInput.setPresentValue(new byte[] { (byte) (1 - byteArray2[0]) });
        }

        LOG.trace("Toggling door 3 ...");
        final ObjectIdentifierServiceParameter createFromTypeAndInstanceNumber3 = ObjectIdentifierServiceParameter
                .createFromTypeAndInstanceNumber(ObjectType.BINARY_INPUT, startId + 3);
        final Device door3CloseStateBinaryInput = findDevice(createFromTypeAndInstanceNumber3);
        if (door3CloseStateBinaryInput != null) {
            final byte[] byteArray3 = (byte[]) door3CloseStateBinaryInput.getPresentValue();
            door3CloseStateBinaryInput.setPresentValue(new byte[] { (byte) (1 - byteArray3[0]) });
        }

        LOG.trace("Toggling door 4 ...");
        final ObjectIdentifierServiceParameter createFromTypeAndInstanceNumber4 = ObjectIdentifierServiceParameter
                .createFromTypeAndInstanceNumber(ObjectType.BINARY_INPUT, startId + 4);
        final Device door4CloseStateBinaryInput = findDevice(createFromTypeAndInstanceNumber4);
        if (door4CloseStateBinaryInput != null) {
            final byte[] byteArray4 = (byte[]) door4CloseStateBinaryInput.getPresentValue();
            door4CloseStateBinaryInput.setPresentValue(new byte[] { (byte) (1 - byteArray4[0]) });
        }
    }

    @SuppressWarnings("unused")
    private void moduloToggle() {

        final int startId = 0;

        if (tempActionId == 0) {
            LOG.info("Toggling door 1 ...");
            final ObjectIdentifierServiceParameter createFromTypeAndInstanceNumber1 = ObjectIdentifierServiceParameter
                    .createFromTypeAndInstanceNumber(ObjectType.BINARY_INPUT, startId + 1);
            final Device door1CloseStateBinaryInput = findDevice(createFromTypeAndInstanceNumber1);
            if (door1CloseStateBinaryInput != null) {
                final byte[] byteArray1 = (byte[]) door1CloseStateBinaryInput.getPresentValue();
                door1CloseStateBinaryInput.setPresentValue(new byte[] { (byte) (1 - byteArray1[0]) });
            }
        }

        if (tempActionId == 1) {
            LOG.info("Toggling door 2 ...");
            final ObjectIdentifierServiceParameter createFromTypeAndInstanceNumber2 = ObjectIdentifierServiceParameter
                    .createFromTypeAndInstanceNumber(ObjectType.BINARY_INPUT, startId + 2);
            final Device door2CloseStateBinaryInput = findDevice(createFromTypeAndInstanceNumber2);
            if (door2CloseStateBinaryInput != null) {
                final byte[] byteArray2 = (byte[]) door2CloseStateBinaryInput.getPresentValue();
                door2CloseStateBinaryInput.setPresentValue(new byte[] { (byte) (1 - byteArray2[0]) });
            }
        }

        if (tempActionId == 2) {
            LOG.info("Toggling door 3 ...");
            final ObjectIdentifierServiceParameter createFromTypeAndInstanceNumber3 = ObjectIdentifierServiceParameter
                    .createFromTypeAndInstanceNumber(ObjectType.BINARY_INPUT, startId + 3);
            final Device door3CloseStateBinaryInput = findDevice(createFromTypeAndInstanceNumber3);
            if (door3CloseStateBinaryInput != null) {
                final byte[] byteArray3 = (byte[]) door3CloseStateBinaryInput.getPresentValue();
                door3CloseStateBinaryInput.setPresentValue(new byte[] { (byte) (1 - byteArray3[0]) });
            }
        }

        if (tempActionId == 3) {
            LOG.info("Toggling door 4 ...");
            final ObjectIdentifierServiceParameter createFromTypeAndInstanceNumber4 = ObjectIdentifierServiceParameter
                    .createFromTypeAndInstanceNumber(ObjectType.BINARY_INPUT, startId + 4);
            final Device door4CloseStateBinaryInput = findDevice(createFromTypeAndInstanceNumber4);
            if (door4CloseStateBinaryInput != null) {
                final byte[] byteArray4 = (byte[]) door4CloseStateBinaryInput.getPresentValue();
                door4CloseStateBinaryInput.setPresentValue(new byte[] { (byte) (1 - byteArray4[0]) });
            }
        }

        // toggle all doors
        if (tempActionId == 4) {

            LOG.info("Toggling door 1 ...");
            final ObjectIdentifierServiceParameter createFromTypeAndInstanceNumber1 = ObjectIdentifierServiceParameter
                    .createFromTypeAndInstanceNumber(ObjectType.BINARY_INPUT, startId + 1);
            final Device door1CloseStateBinaryInput = findDevice(createFromTypeAndInstanceNumber1);
            if (door1CloseStateBinaryInput != null) {
                final byte[] byteArray1 = (byte[]) door1CloseStateBinaryInput.getPresentValue();
                door1CloseStateBinaryInput.setPresentValue(new byte[] { (byte) (1 - byteArray1[0]) });
            }

            LOG.info("Toggling door 2 ...");
            final ObjectIdentifierServiceParameter createFromTypeAndInstanceNumber2 = ObjectIdentifierServiceParameter
                    .createFromTypeAndInstanceNumber(ObjectType.BINARY_INPUT, startId + 2);
            final Device door2CloseStateBinaryInput = findDevice(createFromTypeAndInstanceNumber2);
            if (door2CloseStateBinaryInput != null) {
                final byte[] byteArray2 = (byte[]) door2CloseStateBinaryInput.getPresentValue();
                door2CloseStateBinaryInput.setPresentValue(new byte[] { (byte) (1 - byteArray2[0]) });
            }

            LOG.info("Toggling door 3 ...");
            final ObjectIdentifierServiceParameter createFromTypeAndInstanceNumber3 = ObjectIdentifierServiceParameter
                    .createFromTypeAndInstanceNumber(ObjectType.BINARY_INPUT, startId + 3);
            final Device door3CloseStateBinaryInput = findDevice(createFromTypeAndInstanceNumber3);
            if (door3CloseStateBinaryInput != null) {
                final byte[] byteArray3 = (byte[]) door3CloseStateBinaryInput.getPresentValue();
                door3CloseStateBinaryInput.setPresentValue(new byte[] { (byte) (1 - byteArray3[0]) });
            }

            LOG.info("Toggling door 4 ...");
            final ObjectIdentifierServiceParameter createFromTypeAndInstanceNumber4 = ObjectIdentifierServiceParameter
                    .createFromTypeAndInstanceNumber(ObjectType.BINARY_INPUT, startId + 4);
            final Device door4CloseStateBinaryInput = findDevice(createFromTypeAndInstanceNumber4);
            if (door4CloseStateBinaryInput != null) {
                final byte[] byteArray4 = (byte[]) door4CloseStateBinaryInput.getPresentValue();
                door4CloseStateBinaryInput.setPresentValue(new byte[] { (byte) (1 - byteArray4[0]) });
            }
        }

        tempActionId++;
        tempActionId = tempActionId % 5;
    }

    @Override
    public void bindSocket(final String ip, final int port) throws SocketException, UnknownHostException {

        LOG.info("Device is binding to IP: '{}' and Port: '{}'", ip, port);

        if (datagramSocket != null) {
            return;
        }

        datagramSocket = new DatagramSocket(port, InetAddress.getByName(ip));

        deviceService = new DefaultDeviceService();

        messageController = new DefaultMessageController();
        messageController.setCommunicationService(this);
        messageController.setDeviceService(deviceService);

        deviceService.getDeviceMap().put(getObjectIdentifierServiceParameter(), this);
        children.stream().forEach(d -> deviceService.getDeviceMap().put(d.getObjectIdentifierServiceParameter(), d));

        multicastListenerReaderThread = new MulticastListenerReaderThread();
        multicastListenerReaderThread.setConfigurationManager(configurationManager);
        multicastListenerReaderThread.setVendorMap(vendorMap);
        multicastListenerReaderThread.setBroadcastDatagramSocket(datagramSocket);
        multicastListenerReaderThread.getMessageControllers().add(messageController);

        new Thread(multicastListenerReaderThread).start();
    }

    @Override
    public void cleanUp() {

        if (multicastListenerReaderThread == null) {
            multicastListenerReaderThread.stopBroadCastListener();
            multicastListenerReaderThread = null;
        }

        if (datagramSocket != null) {
            datagramSocket.close();
            datagramSocket = null;
        }

        messageController = null;
    }

    @Override
    public void sendIamMessage(final LinkLayerType linkLayerType) throws IOException {
        final Message retrieveIamMessage = DefaultMessageController.retrieveIamMessage(this, linkLayerType);
        broadcastMessage(retrieveIamMessage);
    }

    private void broadcastMessage(final Message message) throws IOException {

        final byte[] bytes = message.getBytes();

        if (message.getVirtualLinkControl().getLength() != bytes.length) {
            throw new RuntimeException(
                    "Message is invalid! The length in the virtual link control does not match the real data length!");
        }

        final int port = configurationManager.getPropertyAsInt(ConfigurationManager.PORT_CONFIG_KEY);
        final String multicastIP = configurationManager
                .getPropertyAsString(ConfigurationManager.MULTICAST_IP_CONFIG_KEY);

        LOG.trace(
                "<<< Broadcast Sending to " + multicastIP + ":" + port + ": " + Utils.byteArrayToStringNoPrefix(bytes));
        LOG.trace(message);

        final SocketAddress socketAddress = new InetSocketAddress(multicastIP, port);
        final DatagramPacket responseDatagramPacket = new DatagramPacket(bytes, bytes.length, socketAddress);

        datagramSocket.send(responseDatagramPacket);
    }

    @Override
    public void pointToPointMessage(final Message responseMessage, final InetAddress datagramPacketAddress)
            throws IOException {

        final byte[] bytes = responseMessage.getBytes();

        if (responseMessage.getVirtualLinkControl().getLength() != bytes.length) {
            throw new RuntimeException(
                    "Message is invalid! The length in the virtual link control does not match the real data length!");
        }

        final InetAddress destinationAddress = datagramPacketAddress;
        final DatagramPacket responseDatagramPacket = new DatagramPacket(bytes, bytes.length, destinationAddress,
                DefaultConfigurationManager.BACNET_PORT_DEFAULT_VALUE);

        datagramSocket.send(responseDatagramPacket);
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

    @Override
    public LocalDateTime getTimeOfDeviceRestart() {
        return timeOfDeviceRestart;
    }

    @Override
    public void setTimeOfDeviceRestart(final LocalDateTime timeOfDeviceRestart) {
        this.timeOfDeviceRestart = timeOfDeviceRestart;
    }

    @Override
    public Set<COVSubscription> getCovSubscriptions() {
        return covSubscriptions;
    }

    @Override
    public Device getParentDevice() {
        return parentDevice;
    }

    @Override
    public void setParentDevice(final Device parentDevice) {
        this.parentDevice = parentDevice;
    }

    @Override
    public int getVendorId() {
        return vendorId;
    }

    @Override
    public void setVendorId(final int vendorId) {
        this.vendorId = vendorId;
    }

    public ConfigurationManager getConfigurationManager() {
        return configurationManager;
    }

    public void setConfigurationManager(final ConfigurationManager configurationManager) {
        this.configurationManager = configurationManager;
    }

    @Override
    public Map<ObjectIdentifierServiceParameter, Device> getDeviceMap() {
        return deviceMap;
    }

    @Override
    public void setMessageFactory(final MessageFactory messageFactory) {
        this.messageFactory = messageFactory;
    }

    @Override
    public String toString() {
        return "DefaultDevice [id=" + id + ", objectType=" + objectType + "]";
    }

}