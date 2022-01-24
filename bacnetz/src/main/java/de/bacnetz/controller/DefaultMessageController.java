package de.bacnetz.controller;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.bacnetz.common.utils.NetworkUtils;
import de.bacnetz.common.utils.Utils;
import de.bacnetz.conversion.BACnetDateToByteConverter;
import de.bacnetz.conversion.BACnetTimeToByteConverter;
import de.bacnetz.conversion.Converter;
import de.bacnetz.devices.Device;
import de.bacnetz.devices.DeviceProperty;
import de.bacnetz.devices.DevicePropertyType;
import de.bacnetz.devices.DeviceService;
import de.bacnetz.devices.ObjectType;
import de.bacnetz.factory.MessageFactory;
import de.bacnetz.mstp.Header;
import de.bacnetz.services.CommunicationService;
import de.bacnetz.stack.APDU;
import de.bacnetz.stack.BACnetDate;
import de.bacnetz.stack.BACnetTime;
import de.bacnetz.stack.ConfirmedServiceChoice;
import de.bacnetz.stack.DefaultCOVSubscription;
import de.bacnetz.stack.ErrorClass;
import de.bacnetz.stack.ErrorCode;
import de.bacnetz.stack.LinkLayerType;
import de.bacnetz.stack.NPDU;
import de.bacnetz.stack.ObjectIdentifierServiceParameter;
import de.bacnetz.stack.PDUType;
import de.bacnetz.stack.ServiceParameter;
import de.bacnetz.stack.TagClass;
import de.bacnetz.stack.UnconfirmedServiceChoice;
import de.bacnetz.stack.VirtualLinkControl;
import de.bacnetz.stack.exception.BACnetzException;

/**
 * This controller processes APDU messages and ignores NPDU messages.<br />
 * <br />
 * 
 * Handling a message means to create one or more output messages as a response
 * to an incoming message. The information in the response messages may come
 * from a bacnet the bacnet device that is associated with this
 * controller.<br />
 * <br />
 * 
 * This controller is not a bean but each bacent device maintains it's own
 * personal instance of this controller. This controller instance is stored in a
 * member variable of the respective bacnet devices. This design was choose so
 * that the server can host any number of bacnet devices.<br />
 * <br />
 */
public class DefaultMessageController implements MessageController {

    /**
     * BACnet Testing Laboratories - Implementation Guidelines
     * 
     * 3.7 Device instance number 4194303 is for reading a Device object's
     * Object_Identifier Device instance number 4194303 can be used as a 'wildcard'
     * value for reading a Device object's Object_Identifier property (to determine
     * its Device instance). If a ReadProperty or ReadPropertyMultiple request is
     * received for the Object_Identifier property of Device 4194303, the response
     * shall convey the responding device's correct Device object instance.
     * 
     * The ability to respond to instance 4194303 as a 'wildcard' value was added in
     * Addendum 135-2001a; it might not be implemented in devices earlier than
     * Protocol_Revision 4.
     */
    private static final int WILDCARD_DEVICE_ID = 4194303;

    private static final Logger LOG = LogManager.getLogger(DefaultMessageController.class);

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private CommunicationService communicationService;

    private Map<Integer, String> vendorMap = new HashMap<>();

    private MessageFactory messageFactory;

    private final Converter<BACnetDate, byte[]> bacnetDateToByteConverter = new BACnetDateToByteConverter();

    private final Converter<BACnetTime, byte[]> bacnetTimeToByteConverter = new BACnetTimeToByteConverter();

    private LinkLayerType linkLayerType = LinkLayerType.IP;

    @Override
    public List<Message> processMessage(final Message message) {
        return processMessage(null, message);
    }

    @Override
    public List<Message> processMessage(final Header mstpHeader, final Message message) {
        if (message.getApdu() == null) {
            return processNonAPDUMessage(message);
        } else {
            return processAPDUMessage(mstpHeader, message);
        }
    }

    /**
     * Messages that are not APDU are just ignored by this controller.
     * 
     * @param message
     * @return
     */
    private List<Message> processNonAPDUMessage(final Message message) {

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
            // for a message that is not known yet, output a warning message so it can
            // be decided wether to handle that message or wether to add that message into
            // the list of ignored messages
            LOG.warn("<<< Unknown message: " + message.getNpdu().getNetworkLayerMessageType());
            return null;
        }
    }

    private List<Message> processAPDUMessage(final Header mstpHeader, final Message message) {

        LOG.trace(message);

        final ConfirmedServiceChoice confirmedServiceChoice = message.getApdu().getConfirmedServiceChoice();
        if (confirmedServiceChoice != null) {

            switch (confirmedServiceChoice) {

            case READ_PROPERTY:
                LOG.trace(">>> READ_PROPERTY received!");
                return processReadProperty(mstpHeader, message);

            case READ_PROPERTY_MULTIPLE:
                LOG.trace(">>> READ_PROPERTY_MULTIPLE received!");
                return processReadPropertyMultiple(mstpHeader, message);

            case WRITE_PROPERTY:
                LOG.trace(">>> WRITE_PROPERTY received!");
                return processWriteProperty(message);

            case REINITIALIZE_DEVICE:
                LOG.trace(">>> REINITIALIZE_DEVICE received!");
                return processReinitializeDevice(message);

            case SUBSCRIBE_COV:
                LOG.trace(">>> SUBSCRIBE_COV received! {}", message);
                return processSubscribeCov(message);

            case ADD_LIST_ELEMENT:
                LOG.trace(">>> ADD_LIST_ELEMENT received!");
                return processAddListElement(message);

            case confirmedCOVNotification:
                return processConfirmedCOVNotification();

            default:
                LOG.error(message);
                LOG.error("Not implemented: {} ", confirmedServiceChoice);
                break;
            }

            return null;
        }

        final UnconfirmedServiceChoice unconfirmedServiceChoice = message.getApdu().getUnconfirmedServiceChoice();
        if (unconfirmedServiceChoice != null) {

            switch (unconfirmedServiceChoice) {

            case I_AM:
                LOG.trace(">>> I_AM received!");
                LOG.trace(message);
                return processIAMMessage(message);

            // 20.1.3 BACnet-Unconfirmed-Request-PDU
            case I_HAVE:
                LOG.info(">>> I_HAVE received!");
                return null;

            // 20.1.4 BACnet-SimpleACK-PDU
            case UNCONFIRMED_COV_NOTIFICATION:
                LOG.info(">>> UNCONFIRMED_COV_NOTIFICATION received!");
                return null;

            // 20.1.5 BACnet-ComplexACK-PDU
            case UNCONFIRMED_EVENT_NOTIFICATION:
                LOG.info(">>> UNCONFIRMED_EVENT_NOTIFICATION received!");
                return null;

            // 20.1.6 BACnet-SegmentACK-PDU
            case UNCONFIRMED_PRIVATE_TRANSFER:
                LOG.info(">>> UNCONFIRMED_PRIVATE_TRANSFER received!");
                return null;

            // 20.1.7 BACnet-Error-PDU
            case UNCONFIRMED_TEXT_MESSAGE:
                LOG.info(">>> UNCONFIRMED_TEXT_MESSAGE received!");
                return null;

            // 20.1.8 BACnet-Reject-PDU
            case TIME_SYNCHRONIZATION:
                LOG.info(">>> TIME_SYNCHRONIZATION received!");
                return null;

            // 20.1.9 BACnet-Abort-PDU
            case WHO_HAS:
                LOG.info(">>> WHO_HAS received!");
                return null;

            // 20.1.2 BACnet-Confirmed-Request-PDU
            case WHO_IS:
                LOG.trace(">>> WHO_IS received!");
                return processWhoIsMessage(message);

            case UTC_TIME_SYNCHRONIZATION:
                LOG.info(">>> UTC_TIME_SYNCHRONIZATION received!");
                return null;

            case WRITE_GROUP:
                LOG.info(">>> WRITE_GROUP received!");
                return null;

            case UNKNOWN_SERVICE_CHOICE:
                LOG.info(">>> UNKNOWN_SERVICE_CHOICE received!");
                return null;

            default:
                LOG.warn(">>> Unknown message: " + message.getApdu().getUnconfirmedServiceChoice());
                return null;
            }
        }

        return null;
    }

    /**
     * After a COV subscriber receives a COV update from this device, the subscriber
     * acknowledges the COV update. This device silently ignores the Acknowledge
     * because it is not 100% bacnet conformant and does not care if messages arrive
     * or not.
     * 
     * @return
     */
    private List<Message> processConfirmedCOVNotification() {
        return null;
    }

    private List<Message> processAddListElement(final Message requestMessage) {

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

        final APDU apdu = new APDU();
        apdu.setPduType(PDUType.SIMPLE_ACK_PDU);
        apdu.setInvokeId(requestMessage.getApdu().getInvokeId());
        apdu.setConfirmedServiceChoice(ConfirmedServiceChoice.ADD_LIST_ELEMENT);
        apdu.setVendorMap(vendorMap);

        final DefaultMessage message = new DefaultMessage();
        if (linkLayerType != LinkLayerType.MSTP) {
            message.setVirtualLinkControl(virtualLinkControl);
        }
        message.setNpdu(npdu);
        message.setApdu(apdu);

        virtualLinkControl.setLength(message.getDataLength());

        final List<Message> result = new ArrayList<>();
        result.add(message);

        return result;
    }

    private List<Message> processSubscribeCov(final Message requestMessage) {

        // find the device that this COV subscription / deletion is targeted at
        final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = requestMessage.getApdu()
                .getFirstObjectIdentifierServiceParameter();
        final Device findDevice = deviceService.getDeviceMap().get(objectIdentifierServiceParameter);

        // DEBUG
        if (LOG.isTraceEnabled()) {
		    final String msg = "processSubscribeCov() - COV subscription received! Object: "
		            + objectIdentifierServiceParameter.toString();
		    LOG.trace(msg);
		    LOG.trace(requestMessage);
        }

        // find which properties have been subscribed / deleted from to
        final List<ServiceParameter> serviceParameters = requestMessage.getApdu().getServiceParameters();

        final ServiceParameter subscriberProcessIdServiceParameter = serviceParameters.get(0);

        // delete or add a subscription
        if (serviceParameters.size() == 2) {
        	
        	// when there are only two service parameters, that means the list of properties
            // to subscribe to is empty which means no subscription should survive! 
        	// This means that this message is in fact a request delete all COV subscriptions.

            LOG.info("Deleting all COV subscriptions from the device: \"{}\" HashCode: {}", findDevice, findDevice.hashCode());
            LOG.info("ParentDevice: \"{}\" HashCode: {}", findDevice.getParentDevice().getObjectIdentifierServiceParameter(), findDevice.getParentDevice().hashCode());

            findDevice.getCovSubscriptions().clear();

        } else {
        	
        	if (LOG.isTraceEnabled()) {
	        	LOG.info("Adding a new COV subscription to the device: \"{}\" HashCode: {}", findDevice.getObjectIdentifierServiceParameter(), findDevice.hashCode());
	        	LOG.info("ParentDevice: \"{}\" HashCode: {}", findDevice.getParentDevice().getObjectIdentifierServiceParameter(), findDevice.getParentDevice().hashCode());
        	}
        	
            // TODO: create a factory that creates COVSubscriptions
            final DefaultCOVSubscription covSubscription = new DefaultCOVSubscription();
            covSubscription.setClientIp(requestMessage.getSourceInetSocketAddress().getHostString());
//            covSubscription.setPort(requestMessage.get);
            covSubscription.setRequestMessage(requestMessage);
            covSubscription.setCommunicationService(communicationService);
            covSubscription.setDevice(findDevice);
            covSubscription.setParentDevice(findDevice.getParentDevice());
            // TODO: lifetime is not correct
            covSubscription.setLifetime(1000000);
            covSubscription.setSubscriberProcessId(subscriberProcessIdServiceParameter.getPayload()[0]);
            covSubscription.setVendorMap(vendorMap);
            covSubscription.setNpdu(new NPDU(requestMessage.getNpdu()));

            findDevice.getCovSubscriptions().add(covSubscription);
        }

        //
        // Send Acknowledge
        //

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

        final APDU apdu = new APDU();
        apdu.setPduType(PDUType.SIMPLE_ACK_PDU);
        apdu.setInvokeId(requestMessage.getApdu().getInvokeId());
        apdu.setConfirmedServiceChoice(ConfirmedServiceChoice.SUBSCRIBE_COV);
        apdu.setVendorMap(vendorMap);

        final DefaultMessage message = new DefaultMessage();
        if (linkLayerType != LinkLayerType.MSTP) {
            message.setVirtualLinkControl(virtualLinkControl);
        }
        message.setNpdu(npdu);
        message.setApdu(apdu);

        virtualLinkControl.setLength(message.getDataLength());

        final List<Message> result = new ArrayList<>();
        result.add(message);

        return result;
    }

    private List<Message> processReinitializeDevice(final Message requestMessage) {

        final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = requestMessage.getApdu()
                .getFirstObjectIdentifierServiceParameter();
        final Device findDevice = deviceService.getDeviceMap().get(objectIdentifierServiceParameter);

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
        npdu.copyNetworkInformation(requestMessage.getNpdu());

        final APDU apdu = new APDU();
        apdu.setPduType(PDUType.SIMPLE_ACK_PDU);
        apdu.setInvokeId(requestMessage.getApdu().getInvokeId());
        apdu.setConfirmedServiceChoice(ConfirmedServiceChoice.REINITIALIZE_DEVICE);
        apdu.setVendorMap(vendorMap);

        final DefaultMessage message = new DefaultMessage();
        if (linkLayerType != LinkLayerType.MSTP) {
            message.setVirtualLinkControl(virtualLinkControl);
        }
        message.setNpdu(npdu);
        message.setApdu(apdu);

        virtualLinkControl.setLength(message.getDataLength());

        final List<Message> result = new ArrayList<>();
        result.add(message);

        return result;
    }

    /**
     * Answer Who-Is with I-Am.
     * 
     * @param message
     * @return
     */
    private List<Message> processWhoIsMessage(final Message message) {

        final int lowerBound = lowerBound(message);
        final int upperBound = upperBound(message);
        final List<Device> filteredDevices = filterDevices(message);

        if (CollectionUtils.isEmpty(filteredDevices)) {

            LOG.trace("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            LOG.trace("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

            if ((lowerBound != -1) || (upperBound != -1)) {
                LOG.trace(
                        "No devices available in this server in the range {} to {}! Will not answer WHO-IS! Add devices!",
                        lowerBound, upperBound);
            } else {

                LOG.trace("No devices available in this server! Will not answer WHO-IS! Add devices!");
            }

            LOG.trace("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            LOG.trace("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }

        filteredDevices.stream().forEach(d -> {
            try {
                d.sendIamMessage(linkLayerType);
            } catch (final IOException e) {
                LOG.error(e.getMessage(), e);
            }
        });

        return null;
    }

    private int lowerBound(final Message message) {
        final List<ServiceParameter> serviceParameters = message.getApdu().getServiceParameters();
        if (CollectionUtils.isEmpty(serviceParameters)) {
            return -1;
        }

        final boolean bigEndian = true;

        final ServiceParameter lowerBoundServiceParameter = serviceParameters.get(0);

        // find lower bound as integer
        final int lowerBound = (lowerBoundServiceParameter.getPayload().length == 1)
                ? lowerBoundServiceParameter.getPayload()[0]
                : Utils.bytesToUnsignedShort(lowerBoundServiceParameter.getPayload()[0],
                        lowerBoundServiceParameter.getPayload()[1], bigEndian);

        return lowerBound;
    }

    private int upperBound(final Message message) {
        final List<ServiceParameter> serviceParameters = message.getApdu().getServiceParameters();
        if (CollectionUtils.isEmpty(serviceParameters)) {
            return -1;
        }

        final boolean bigEndian = true;

        final ServiceParameter upperBoundServiceParameter = serviceParameters.get(1);

        // find upper bound as integer
        final int upperBound = (upperBoundServiceParameter.getPayload().length == 1)
                ? upperBoundServiceParameter.getPayload()[0]
                : Utils.bytesToUnsignedShort(upperBoundServiceParameter.getPayload()[0],
                        upperBoundServiceParameter.getPayload()[1], bigEndian);

        return upperBound;
    }

    private List<Device> filterDevices(final Message message) {

        final List<ServiceParameter> serviceParameters = message.getApdu().getServiceParameters();
        if (CollectionUtils.isEmpty(serviceParameters)) {
            return deviceService.getDevices();
        }

        final int lowerBound = lowerBound(message);
        final int upperBound = upperBound(message);

        return deviceService.getDevices().stream().filter(d -> (lowerBound <= d.getId() && d.getId() <= upperBound))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public static Message retrieveIamMessage(final Device device, final LinkLayerType linkLayerType) {

        // return Unconfirmed request i-Am device,10001
        final VirtualLinkControl virtualLinkControl = new VirtualLinkControl();
        virtualLinkControl.setType(0x81);
        virtualLinkControl.setFunction(0x0B);
        virtualLinkControl.setLength(0x00);

        // NPDU including destination network information
        final NPDU npdu = new NPDU();
        npdu.setVersion(0x01);
        npdu.setControl(0x20);
        npdu.setDestinationNetworkAddress(NetworkUtils.BROADCAST_NETWORK_NUMBER);
        // indicates broadcast on destination network
        npdu.setDestinationMACLayerAddressLength(0);
        npdu.setDestinationHopCount(255);

        final APDU apdu = new APDU();
        apdu.setPduType(PDUType.UNCONFIRMED_SERVICE_REQUEST_PDU);
        apdu.setUnconfirmedServiceChoice(UnconfirmedServiceChoice.I_AM);
        apdu.setInvokeId(Integer.MIN_VALUE);

        final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
        objectIdentifierServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
        objectIdentifierServiceParameter.setTagNumber(ServiceParameter.BACNET_OBJECT_IDENTIFIER);
        objectIdentifierServiceParameter.setLengthValueType(4);
        objectIdentifierServiceParameter.setObjectType(device.getObjectType());
        objectIdentifierServiceParameter.setInstanceNumber(device.getId());
        apdu.getServiceParameters().add(objectIdentifierServiceParameter);

        final ServiceParameter maximumAPDUServiceParameter = new ServiceParameter();
        maximumAPDUServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
        maximumAPDUServiceParameter.setTagNumber(ServiceParameter.UNSIGNED_INTEGER_CODE);
        maximumAPDUServiceParameter.setLengthValueType(2);
        maximumAPDUServiceParameter.setPayload(new byte[] { (byte) 0x05, (byte) 0xC4 }); // 0x05C4 = 1476
        apdu.getServiceParameters().add(maximumAPDUServiceParameter);

        final ServiceParameter segmentationSupportedServiceParameter = new ServiceParameter();
        segmentationSupportedServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
        segmentationSupportedServiceParameter.setTagNumber(ServiceParameter.ENUMERATED_CODE);
        segmentationSupportedServiceParameter.setLengthValueType(1);
        segmentationSupportedServiceParameter.setPayload(new byte[] { (byte) 0x00 }); // segmented-both
        apdu.getServiceParameters().add(segmentationSupportedServiceParameter);

        final byte[] vendorIdBuffer = Utils.shortToBuffer((short) device.getVendorId());

        final ServiceParameter vendorIdServiceParameter = new ServiceParameter();
        vendorIdServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
        vendorIdServiceParameter.setTagNumber(ServiceParameter.UNSIGNED_INTEGER_CODE);
        vendorIdServiceParameter.setLengthValueType(vendorIdBuffer.length);
        vendorIdServiceParameter.setPayload(vendorIdBuffer);
        apdu.getServiceParameters().add(vendorIdServiceParameter);

        final DefaultMessage result = new DefaultMessage();
        if (linkLayerType != LinkLayerType.MSTP) {
            result.setVirtualLinkControl(virtualLinkControl);
        }
        result.setNpdu(npdu);
        result.setApdu(apdu);

        virtualLinkControl.setLength(virtualLinkControl.getDataLength() + npdu.getDataLength() + apdu.getDataLength());

        return result;
    }

    private List<Message> processWriteProperty(final Message requestMessage) {

        LOG.trace("processWriteProperty()");
        final int propertyIdentifier = requestMessage.getApdu().getPropertyIdentifier();
        LOG.trace("Property Identifier: {}", propertyIdentifier);
        final int propertyIdentifierCode = propertyIdentifier;

        final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = requestMessage.getApdu()
                .getFirstObjectIdentifierServiceParameter();
        LOG.trace(">>> Write Identifier: {} ({}) Object Identifier: {}", propertyIdentifierCode,
                DevicePropertyType.getByCode(propertyIdentifierCode).getName(),
                objectIdentifierServiceParameter.toString());

        final DevicePropertyType devicePropertyType = DevicePropertyType.getByCode(propertyIdentifier);

        switch (devicePropertyType) {

        // 0xCA = 202d
        case RESTART_NOTIFICATION_RECIPIENTS:
            LOG.trace("<<< WRITE_PROP: restart notification recipients ({})", propertyIdentifierCode);
            return processWriteRestartNotificationRecipientsProperty(propertyIdentifierCode, requestMessage);

        case PRESENT_VALUE:
            return processWritePresentValue(propertyIdentifierCode, requestMessage);

        case DESCRIPTION:
            return processWriteDescription(propertyIdentifierCode, requestMessage);

        case LOCATION:
            return processWriteLocation(propertyIdentifierCode, requestMessage);

        default:
            LOG.warn("Cannot write property: " + devicePropertyType + " (" + propertyIdentifierCode + ")");
            return null;
        }
    }

    private List<Message> processWriteLocation(final int propertyIdentifierCode, final Message requestMessage) {
        final DefaultMessage message = simpleAck(requestMessage, ConfirmedServiceChoice.WRITE_PROPERTY);
        final List<Message> result = new ArrayList<>();
        result.add(message);

        return result;
    }

    private List<Message> processWriteDescription(final int propertyIdentifierCode, final Message requestMessage) {
        final DefaultMessage message = simpleAck(requestMessage, ConfirmedServiceChoice.WRITE_PROPERTY);
        final List<Message> result = new ArrayList<>();
        result.add(message);

        return result;
    }

    private List<Message> processWritePresentValue(final int propertyIdentifierCode, final Message requestMessage) {

        LOG.trace(requestMessage);

        final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = (ObjectIdentifierServiceParameter) requestMessage
                .getApdu().getServiceParameters().get(0);

        final List<Device> devices = deviceService.findDevice(objectIdentifierServiceParameter, LinkLayerType.IP);
        final Device device = devices.get(0);
        LOG.trace(device);

        final ServiceParameter serviceParameter = requestMessage.getApdu().getServiceParameters().get(3);
        final byte[] payload = serviceParameter.getPayload();

        LOG.trace(Utils.bytesToHex(payload));

        final DefaultMessage message = simpleAck(requestMessage, ConfirmedServiceChoice.WRITE_PROPERTY);
        final List<Message> result = new ArrayList<>();
        result.add(message);

        device.writeProperty(DeviceProperty.PRESENT_VALUE, (int) payload[0]);

        return result;
    }

    private List<Message> processWriteRestartNotificationRecipientsProperty(final int propertyIdentifierCode,
            final Message requestMessage) {

        final DefaultMessage message = simpleAck(requestMessage, ConfirmedServiceChoice.WRITE_PROPERTY);
        final List<Message> result = new ArrayList<>();
        result.add(message);

        return result;
    }

    private DefaultMessage simpleAck(final Message requestMessage,
            final ConfirmedServiceChoice confirmedServiceChoice) {

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

        final APDU apdu = new APDU();
        apdu.setPduType(PDUType.SIMPLE_ACK_PDU);
        apdu.setInvokeId(requestMessage.getApdu().getInvokeId());
        apdu.setConfirmedServiceChoice(confirmedServiceChoice);
        apdu.setVendorMap(vendorMap);

        final DefaultMessage message = new DefaultMessage();
        if (linkLayerType != LinkLayerType.MSTP) {
            message.setVirtualLinkControl(virtualLinkControl);
        }
        message.setNpdu(npdu);
        message.setApdu(apdu);

        virtualLinkControl.setLength(message.getDataLength());
        return message;
    }

    private List<Message> processReadProperty(final Header mstpHeader, final Message requestMessage) {

        LOG.trace("processReadProperty()");

        final int propertyIdentifierCode = requestMessage.getApdu().getPropertyIdentifier();

        LOG.trace("Property Identifier: {}", propertyIdentifierCode);

        final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = requestMessage.getApdu()
                .getFirstObjectIdentifierServiceParameter();

        LOG.trace(">>> Property Identifier: {} ({}) Object Identifier: {}", propertyIdentifierCode,
                DevicePropertyType.getByCode(propertyIdentifierCode).getName(),
                objectIdentifierServiceParameter.toString());

        final boolean wildcardId = objectIdentifierServiceParameter.getInstanceNumber() == WILDCARD_DEVICE_ID;
        if (wildcardId) {
            objectIdentifierServiceParameter.setInstanceNumber(2);
        }

        // find device
        final Device targetDevice = deviceService.getDeviceMap().get(objectIdentifierServiceParameter);
        final List<Message> result = new ArrayList<>();
        if (targetDevice != null) {
            result.add(targetDevice.getPropertyValue(requestMessage, propertyIdentifierCode));
        } else {

            // find the parent device via the mstp header's destination address
            // and the use the APDU's object identifier to identify the child device
            if (mstpHeader != null) {

                final Device childDevice = retrieveChildViaMSTPHeader(mstpHeader, objectIdentifierServiceParameter);

                result.add(childDevice.getPropertyValue(requestMessage, propertyIdentifierCode));
            }

            LOG.warn("No device found for OID: {}", objectIdentifierServiceParameter);
        }

        return result;
    }

    private Device retrieveChildViaMSTPHeader(final Header mstpHeader,
            final ObjectIdentifierServiceParameter objectIdentifierServiceParameter) {
        final ObjectIdentifierServiceParameter parentOID = ObjectIdentifierServiceParameter
                .createFromTypeAndInstanceNumber(ObjectType.DEVICE, mstpHeader.getDestinationAddress());
        final Device parentDevice = deviceService.getDeviceMap().get(parentOID);
        final Device childDevice = parentDevice.getDeviceMap().get(objectIdentifierServiceParameter);
        return childDevice;
    }

    private List<Message> processReadPropertyMultiple(final Header mstpHeader, final Message requestMessage) {

        final VirtualLinkControl virtualLinkControl = new VirtualLinkControl();
        virtualLinkControl.setType(0x81);
        virtualLinkControl.setFunction(0x0A);
        virtualLinkControl.setLength(0x00);

        final APDU targetApdu = new APDU();
        targetApdu.setPduType(PDUType.COMPLEX_ACK_PDU);
        targetApdu.setInvokeId(requestMessage.getApdu().getInvokeId());
        targetApdu.setConfirmedServiceChoice(ConfirmedServiceChoice.READ_PROPERTY_MULTIPLE);
        targetApdu.setVendorMap(vendorMap);

        final APDU sourceApdu = requestMessage.getApdu();

        int index = 0;

        for (final ServiceParameter serviceParameter : sourceApdu.getServiceParameters()) {

            if (serviceParameter instanceof ObjectIdentifierServiceParameter) {

                final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = (ObjectIdentifierServiceParameter) serviceParameter;
                index = processDevice(mstpHeader, objectIdentifierServiceParameter, index,
                        sourceApdu.getServiceParameters(), targetApdu);
            }
        }

        final NPDU npdu = new NPDU();
        npdu.setVersion(0x01);
        // no additional information
        // this works, if the cp is connected to the device directly via 192.168.2.1
        npdu.setControl(0x00);
        npdu.copyNetworkInformation(requestMessage.getNpdu());

        final DefaultMessage message = new DefaultMessage();
        if (linkLayerType != LinkLayerType.MSTP) {
            message.setVirtualLinkControl(virtualLinkControl);
        }
        message.setNpdu(npdu);
        message.setApdu(targetApdu);

//        // DEBUG
//        LOG.trace("All service parameters ...");
//        if (CollectionUtils.isNotEmpty(targetApdu.getServiceParameters())) {
//            for (final ServiceParameter serviceParameter : targetApdu.getServiceParameters()) {
//                final byte[] temp = new byte[100];
//                serviceParameter.toBytes(temp, 0);
//
//                LOG.trace(serviceParameter + " - " + Utils.byteArrayToStringNoPrefix(temp));
//            }
//        }
//        LOG.trace("All service parameters done.");

        virtualLinkControl.setLength(message.getDataLength());

        final List<Message> result = new ArrayList<>();
        result.add(message);

        return result;
    }

    /**
     * This method will assemble ServiceParameters to answer the request and write
     * those into the specified APDU.
     * 
     * @param mstpHeader
     * @param targetObjectIdentifierServiceParameter
     * @param sourceServiceParameterIndex
     * @param serviceParameters
     * @param targetApdu
     * @return
     */
    private int processDevice(final Header mstpHeader,
            final ObjectIdentifierServiceParameter targetObjectIdentifierServiceParameter,
            final int sourceServiceParameterIndex, final List<ServiceParameter> serviceParameters,
            final APDU targetApdu) {

        // find device
        Device targetDevice = deviceService.getDeviceMap().get(targetObjectIdentifierServiceParameter);

        if (targetDevice == null) {
            LOG.error("Cannot retrieve device for objectIdentifier: '{}'", targetObjectIdentifierServiceParameter);

            // find the parent device via the mstp header's destination address
            // and the use the APDU's object identifier to identify the child device
            if (mstpHeader != null) {

                final Device childDevice = retrieveChildViaMSTPHeader(mstpHeader,
                        targetObjectIdentifierServiceParameter);
                targetDevice = childDevice;
            }
        }

        if (targetDevice != null) {

            final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
            objectIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
            objectIdentifierServiceParameter.setTagNumber(0x00);
            objectIdentifierServiceParameter.setLengthValueType(4);
            objectIdentifierServiceParameter.setObjectType(targetDevice.getObjectType());
            objectIdentifierServiceParameter.setInstanceNumber(targetDevice.getId());
            targetApdu.getServiceParameters().add(objectIdentifierServiceParameter);

            // opening {[1]
            final ServiceParameter openingTagServiceParameter1 = new ServiceParameter();
            openingTagServiceParameter1.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
            openingTagServiceParameter1.setTagNumber(0x01);
            openingTagServiceParameter1.setLengthValueType(ServiceParameter.OPENING_TAG_CODE);
            targetApdu.getServiceParameters().add(openingTagServiceParameter1);
        }

        // read the service parameters to find out which properties where requested or
        // if the 'all' keyword was sent for all properties
        if (CollectionUtils.isEmpty(serviceParameters)) {
            LOG.warn("No service parameters in readPropertyMultiple request");
        } else {
            LOG.trace("serviceParameters.length(): {} ", serviceParameters.size());
        }

        int index = sourceServiceParameterIndex + 1;
        boolean withinRequestedProperties = false;
        while (index < serviceParameters.size()) {

            final ServiceParameter serviceParameter = serviceParameters.get(index);

            if (serviceParameter instanceof ObjectIdentifierServiceParameter) {
                break;
            }

            index++;

            // DEBUG
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

            final byte temp = serviceParameter.getPayload()[0];
            LOG.trace("temp: {}", temp);
            final int devicePropertyKey = temp & 0xFF;
            LOG.trace("devicePropertyKey: {}", devicePropertyKey);

            // 'all' service property
            if (devicePropertyKey == DeviceProperty.ALL) {

                int debugOutputIndex = 0;

                if (MapUtils.isNotEmpty(targetDevice.getProperties())) {

                    for (final DeviceProperty<?> deviceProperty : targetDevice.getProperties().values()) {

                        debugOutputIndex++;

                        LOG.trace("Adding ServiceParameter for DeviceProperty " + debugOutputIndex + ") "
                                + deviceProperty + " ...");

                        if (targetDevice != null) {

                            // add the property identifier
                            final ServiceParameter propertyIdentifierServiceParameter = new ServiceParameter();
                            propertyIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
                            propertyIdentifierServiceParameter.setTagNumber(2);
                            propertyIdentifierServiceParameter.setLengthValueType(1);
                            propertyIdentifierServiceParameter
                                    .setPayload(new byte[] { (byte) deviceProperty.getPropertyKey() });
                            targetApdu.getServiceParameters().add(propertyIdentifierServiceParameter);
                        }

                        // add the property value
                        addPropertyValue(targetApdu, deviceProperty);

                        LOG.trace("Adding ServiceParameter for DeviceProperty: " + deviceProperty + " done.");
                    }
                }

            } else if (devicePropertyKey == DevicePropertyType.TIME_OF_DEVICE_RESTART.getCode()) {

                if (targetDevice != null) {

                    if (MapUtils.isNotEmpty(targetDevice.getProperties())
                            && targetDevice.getProperties().containsKey(devicePropertyKey)) {

                        final DeviceProperty<?> deviceProperty = targetDevice.getProperties().get(devicePropertyKey);

                        // add the property identifier
                        final ServiceParameter propertyIdentifierServiceParameter = new ServiceParameter();
                        propertyIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
                        propertyIdentifierServiceParameter.setTagNumber(2);
                        propertyIdentifierServiceParameter.setLengthValueType(1);
                        propertyIdentifierServiceParameter
                                .setPayload(new byte[] { (byte) deviceProperty.getPropertyKey() });
                        targetApdu.getServiceParameters().add(propertyIdentifierServiceParameter);

                        // opening tag {[4]
                        final ServiceParameter openingTagServiceParameter4 = new ServiceParameter();
                        openingTagServiceParameter4.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
                        openingTagServiceParameter4.setTagNumber(0x04);
                        openingTagServiceParameter4.setLengthValueType(ServiceParameter.OPENING_TAG_CODE);
                        targetApdu.getServiceParameters().add(openingTagServiceParameter4);

                        // opening tag {[2]
                        final ServiceParameter openingTagServiceParameter2 = new ServiceParameter();
                        openingTagServiceParameter2.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
                        openingTagServiceParameter2.setTagNumber(0x02);
                        openingTagServiceParameter2.setLengthValueType(ServiceParameter.OPENING_TAG_CODE);
                        targetApdu.getServiceParameters().add(openingTagServiceParameter2);

                        // see
                        // de.bacnetz.factory.DefaultMessageFactory.processTimeOfDeviceRestartProperty(Device,
                        // DeviceProperty<?>, Message, boolean, boolean, LocalDateTime)

                        // encode the date parameter
                        final BACnetDate bacnetDate = new BACnetDate();
                        bacnetDate.fromLocalDateTime(targetDevice.getTimeOfDeviceRestart());
                        final byte[] bacnetDateAsByteArray = bacnetDateToByteConverter.convert(bacnetDate);
                        final ServiceParameter dateServiceParameter = new ServiceParameter();
                        dateServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
                        dateServiceParameter.setTagNumber(ServiceParameter.DATE);
                        dateServiceParameter.setLengthValueType(0x04);
                        dateServiceParameter.setPayload(bacnetDateAsByteArray);
                        targetApdu.getServiceParameters().add(dateServiceParameter);

                        // encode the time parameter
                        final BACnetTime bacnetTime = new BACnetTime();
                        bacnetTime.fromLocalDateTime(targetDevice.getTimeOfDeviceRestart());
                        final byte[] bacnetTimeAsByteArray = bacnetTimeToByteConverter.convert(bacnetTime);
                        final ServiceParameter timeServiceParameter = new ServiceParameter();
                        timeServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
                        timeServiceParameter.setTagNumber(ServiceParameter.TIME);
                        timeServiceParameter.setLengthValueType(0x04);
                        timeServiceParameter.setPayload(bacnetTimeAsByteArray);
                        targetApdu.getServiceParameters().add(timeServiceParameter);

                        // closing tag }[2]
                        final ServiceParameter closingTagServiceParameter2 = new ServiceParameter();
                        closingTagServiceParameter2.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
                        closingTagServiceParameter2.setTagNumber(0x02);
                        closingTagServiceParameter2.setLengthValueType(ServiceParameter.CLOSING_TAG_CODE);
                        targetApdu.getServiceParameters().add(closingTagServiceParameter2);

                        // closing tag }[4]
                        final ServiceParameter closingTagServiceParameter4 = new ServiceParameter();
                        closingTagServiceParameter4.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
                        closingTagServiceParameter4.setTagNumber(0x04);
                        closingTagServiceParameter4.setLengthValueType(ServiceParameter.CLOSING_TAG_CODE);
                        targetApdu.getServiceParameters().add(closingTagServiceParameter4);
                    }
                }

            } else {

                if (targetDevice != null) {

                    // add a property value service parameter for the deviceProperty
                    if (targetDevice.getProperties().containsKey(devicePropertyKey)) {

                        final DeviceProperty<?> deviceProperty = targetDevice.getProperties().get(devicePropertyKey);

                        LOG.trace("For Device {} adding property {}", targetDevice, deviceProperty);

                        // add the property identifier
                        final ServiceParameter propertyIdentifierServiceParameter = new ServiceParameter();
                        propertyIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
                        propertyIdentifierServiceParameter.setTagNumber(2);
                        propertyIdentifierServiceParameter.setLengthValueType(1);
                        propertyIdentifierServiceParameter
                                .setPayload(new byte[] { (byte) deviceProperty.getPropertyKey() });
                        targetApdu.getServiceParameters().add(propertyIdentifierServiceParameter);

                        // add the property value
                        addPropertyValue(targetApdu, deviceProperty);

                    } else {

                        LOG.trace("deviceProperty {} ({}} not present for device {}",
                                DevicePropertyType.getByCode(devicePropertyKey), devicePropertyKey, targetDevice);

                        // output property error, unknown property

                        // add the property identifier
                        final ServiceParameter propertyIdentifierServiceParameter = new ServiceParameter();
                        propertyIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
                        propertyIdentifierServiceParameter.setTagNumber(2);
                        propertyIdentifierServiceParameter.setLengthValueType(1);
                        propertyIdentifierServiceParameter.setPayload(new byte[] { (byte) devicePropertyKey });
                        targetApdu.getServiceParameters().add(propertyIdentifierServiceParameter);

                        // opening tag {[5]
                        final ServiceParameter openingTagServiceParameter5 = new ServiceParameter();
                        openingTagServiceParameter5.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
                        openingTagServiceParameter5.setTagNumber(0x05);
                        openingTagServiceParameter5.setLengthValueType(ServiceParameter.OPENING_TAG_CODE);
                        targetApdu.getServiceParameters().add(openingTagServiceParameter5);

                        final ServiceParameter errorClassServiceParameter = new ServiceParameter();
                        errorClassServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
                        errorClassServiceParameter.setTagNumber(ServiceParameter.ENUMERATED_CODE);
                        errorClassServiceParameter.setLengthValueType(0x01);
                        errorClassServiceParameter.setPayload(new byte[] { (byte) ErrorClass.PROPERTY.getCode() });
                        targetApdu.getServiceParameters().add(errorClassServiceParameter);

                        final ServiceParameter errorCodeServiceParameter = new ServiceParameter();
                        errorCodeServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
                        errorCodeServiceParameter.setTagNumber(ServiceParameter.ENUMERATED_CODE);
                        errorCodeServiceParameter.setLengthValueType(0x01);
                        errorCodeServiceParameter
                                .setPayload(new byte[] { (byte) ErrorCode.UNKNOWN_PROPERTY.getCode() });
                        targetApdu.getServiceParameters().add(errorCodeServiceParameter);

                        // closing tag }[5]
                        final ServiceParameter closingTagServiceParameter5 = new ServiceParameter();
                        closingTagServiceParameter5.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
                        closingTagServiceParameter5.setTagNumber(0x05);
                        closingTagServiceParameter5.setLengthValueType(ServiceParameter.CLOSING_TAG_CODE);
                        targetApdu.getServiceParameters().add(closingTagServiceParameter5);
                    }
                }
            }
        }

        if (targetDevice != null) {
            // closing }[1]
            final ServiceParameter closingTagServiceParameter1 = new ServiceParameter();
            closingTagServiceParameter1.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
            closingTagServiceParameter1.setTagNumber(0x01);
            closingTagServiceParameter1.setLengthValueType(ServiceParameter.CLOSING_TAG_CODE);
            targetApdu.getServiceParameters().add(closingTagServiceParameter1);
            LOG.trace(closingTagServiceParameter1);
        }

        return index;
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

    @SuppressWarnings("unused")
    private List<Message> processSystemStatusMessage(final Message message) {

        final DefaultMessage resultMessage = new DefaultMessage(message);

        resultMessage.getNpdu().copyNetworkInformation(message.getNpdu());

        // APDU
        resultMessage.getApdu().setPduType(PDUType.COMPLEX_ACK_PDU);

        // add new service parameters into the APDU
        final ServiceParameter openingTagServiceParameter = new ServiceParameter();
        openingTagServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        openingTagServiceParameter.setTagNumber(0x04);
        openingTagServiceParameter.setLengthValueType(ServiceParameter.OPENING_TAG_CODE);
        resultMessage.getApdu().getServiceParameters().add(2, openingTagServiceParameter);

        final ServiceParameter segmentationSupportedServiceParameter = new ServiceParameter();
        segmentationSupportedServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
        segmentationSupportedServiceParameter.setTagNumber(ServiceParameter.ENUMERATED_CODE);
        segmentationSupportedServiceParameter.setLengthValueType(1);
        // 0x00 == system-status: operational
        segmentationSupportedServiceParameter.setPayload(new byte[] { (byte) 0x00 });
        resultMessage.getApdu().getServiceParameters().add(3, segmentationSupportedServiceParameter);

        final ServiceParameter closingTagServiceParameter = new ServiceParameter();
        closingTagServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        closingTagServiceParameter.setTagNumber(0x04);
        closingTagServiceParameter.setLengthValueType(ServiceParameter.CLOSING_TAG_CODE);
        resultMessage.getApdu().getServiceParameters().add(4, closingTagServiceParameter);

        // set message.VirtualLinkControl.size to the size of the entire message
        resultMessage.getVirtualLinkControl().setLength(resultMessage.getDataLength());

        final List<Message> result = new ArrayList<>();
        result.add(resultMessage);

        return result;
    }

    /**
     * Answer I-Am with nothing
     * 
     * @param message
     * @return
     * @throws BACnetzException
     */
    private List<Message> processIAMMessage(final Message message) throws BACnetzException {

        // retrieve the IP and port from where this message originated
        final InetSocketAddress sourceInetSocketAddress = message.getSourceInetSocketAddress();

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

        LOG.info(">>> processIAMMessage from Source: {}, InstanceNumber: {}, VendorId: {}, VendorName: {}",
                sourceInetSocketAddress, objectIdentifierServiceParameter.getInstanceNumber(), vendorId,
                vendorMap.get(vendorId));

        // TODO: add node into UI tree for this newly discovered device

//        sendResponseToIAM(sourceInetSocketAddress, objectIdentifierServiceParameter);
        LOG.warn("Currently ignoring incoming I-AM messages!");

        return null;
    }

    @SuppressWarnings("unused")
    private void sendResponseToIAM(final InetSocketAddress sourceInetSocketAddress,
            final ServiceParameter objectIdentifierServiceParameter) {
        final VirtualLinkControl virtualLinkControl = new VirtualLinkControl();
        virtualLinkControl.setType(0x81);
        virtualLinkControl.setFunction(0x0A);
        virtualLinkControl.setLength(0x00);

        // NPDU including destination network information
        final NPDU outNpdu = new NPDU();
        outNpdu.setVersion(0x01);

        // no additional information
        // this works, if the cp is connected to the device directly via 192.168.2.1
        outNpdu.setControl(0x00);

        // this object identifier has to be context specific. I do not know why
        objectIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);

        final ServiceParameter propertyIdentifierServiceParameter = new ServiceParameter();
        propertyIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        propertyIdentifierServiceParameter.setTagNumber(0x01);
        propertyIdentifierServiceParameter.setLengthValueType(0x01);
        propertyIdentifierServiceParameter.setPayload(new byte[] { (byte) DeviceProperty.OBJECT_LIST });

        // request index 0 which is the length of the array
        final ServiceParameter propertyArrayIndexServiceParameter = new ServiceParameter();
        propertyArrayIndexServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        propertyArrayIndexServiceParameter.setTagNumber(0x02);
        propertyArrayIndexServiceParameter.setLengthValueType(0x01);
        propertyArrayIndexServiceParameter.setPayload(new byte[] { (byte) 0x00 });

        final APDU outApdu = new APDU();
        outApdu.setPduType(PDUType.CONFIRMED_SERVICE_REQUEST_PDU);
        // outApdu.setInvokeId(invokeId);
        outApdu.setConfirmedServiceChoice(ConfirmedServiceChoice.READ_PROPERTY);
        outApdu.setVendorMap(vendorMap);

        outApdu.getServiceParameters().add(objectIdentifierServiceParameter);
        outApdu.getServiceParameters().add(propertyIdentifierServiceParameter);
        outApdu.getServiceParameters().add(propertyArrayIndexServiceParameter);

        final DefaultMessage outMessage = new DefaultMessage();
        outMessage.setVirtualLinkControl(virtualLinkControl);
        outMessage.setNpdu(outNpdu);
        outMessage.setApdu(outApdu);

        virtualLinkControl.setLength(outMessage.getDataLength());

        try {
            LOG.info("Address: {}, Port: {}", sourceInetSocketAddress.getAddress(), sourceInetSocketAddress.getPort());
            @SuppressWarnings("resource")
            final Socket socket = new Socket(sourceInetSocketAddress.getAddress(), sourceInetSocketAddress.getPort());
            final DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.write(outMessage.getBytes());
        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public Map<Integer, String> getVendorMap() {
        return vendorMap;
    }

    public void setVendorMap(final Map<Integer, String> vendorMap) {
        this.vendorMap = vendorMap;
        messageFactory.setVendorMap(vendorMap);
    }

    public CommunicationService getCommunicationService() {
        return communicationService;
    }

    @Override
    public void setCommunicationService(final CommunicationService communicationService) {
        this.communicationService = communicationService;
    }

    public void setDeviceService(final DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    public MessageFactory getMessageFactory() {
        return messageFactory;
    }

    public void setMessageFactory(final MessageFactory messageFactory) {
        this.messageFactory = messageFactory;
    }

    public LinkLayerType getLinkLayerType() {
        return linkLayerType;
    }

    public void setLinkLayerType(final LinkLayerType linkLayerType) {
        this.linkLayerType = linkLayerType;
    }

}