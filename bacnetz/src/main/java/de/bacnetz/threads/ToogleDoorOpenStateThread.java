package de.bacnetz.threads;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.bacnetz.common.utils.NetworkUtils;
import de.bacnetz.controller.DefaultMessage;
import de.bacnetz.devices.BinaryInputDevice;
import de.bacnetz.devices.Device;
import de.bacnetz.devices.DevicePropertyType;
import de.bacnetz.factory.MessageType;
import de.bacnetz.services.CommunicationService;
import de.bacnetz.stack.APDU;
import de.bacnetz.stack.NPDU;
import de.bacnetz.stack.ObjectIdentifierServiceParameter;
import de.bacnetz.stack.PDUType;
import de.bacnetz.stack.ServiceParameter;
import de.bacnetz.stack.TagClass;
import de.bacnetz.stack.UnconfirmedServiceChoice;
import de.bacnetz.stack.VirtualLinkControl;

public class ToogleDoorOpenStateThread implements Runnable {

    private static final int SLEEP_TIME = 10000;

    private static final Logger LOG = LogManager.getLogger(ToogleDoorOpenStateThread.class);

    private Device parentDevice;

    private Device childDevice;

    private Map<Integer, String> vendorMap;

    private CommunicationService communicationService;

    @Override
    public void run() {

        if ((parentDevice == null) || (childDevice == null)) {

            final String msg = "No device set! Need device!";
            LOG.error(msg);
            throw new RuntimeException(msg);
        }

        // sleep before the loop starts it's work
        try {
            Thread.sleep(SLEEP_TIME);
        } catch (final InterruptedException e) {
            LOG.error(e.getMessage(), e);
        }

        final BinaryInputDevice binaryInputDevice = (BinaryInputDevice) childDevice;

        while (true) {

            // toggle
            binaryInputDevice.setPresentValue(!(Boolean) binaryInputDevice.getPresentValue());

            sendCOV(parentDevice, binaryInputDevice, vendorMap, NetworkUtils.TARGET_IP, communicationService);

            try {
                Thread.sleep(SLEEP_TIME);
            } catch (final InterruptedException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    public static void sendCOV(final Device parentDevice, final Device childDevice,
            final Map<Integer, String> vendorMap, final String targetIp,
            final CommunicationService communicationService) {

        final String msg = "Sending COV update to targetIp:" + targetIp;
        LOG.info(msg);

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
        apdu.setPduType(PDUType.CONFIRMED_SERVICE_REQUEST_PDU);
        apdu.setSegmentedResponseAccepted(true);
        apdu.setMaxResponseSegmentsAccepted(8);
        apdu.setSizeOfMaximumAPDUAccepted(3);
        apdu.setInvokeId(parentDevice.retrieveNextInvokeId());
        apdu.setUnconfirmedServiceChoice(UnconfirmedServiceChoice.CONFIRMED_COV_NOTIFICATION);
        apdu.setVendorMap(vendorMap);

        final ServiceParameter processIdentifierServiceParameter = new ServiceParameter();
        processIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        processIdentifierServiceParameter.setTagNumber(0x00);
        processIdentifierServiceParameter.setLengthValueType(0x01);
        processIdentifierServiceParameter.setPayload(new byte[] { 0x01 });
        apdu.getServiceParameters().add(processIdentifierServiceParameter);

        final ObjectIdentifierServiceParameter parentObjectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
        parentObjectIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        parentObjectIdentifierServiceParameter.setTagNumber(0x01);
        parentObjectIdentifierServiceParameter.setLengthValueType(4);
        parentObjectIdentifierServiceParameter.setObjectType(parentDevice.getObjectType());
        parentObjectIdentifierServiceParameter.setInstanceNumber(parentDevice.getId());
        apdu.getServiceParameters().add(parentObjectIdentifierServiceParameter);

        final ObjectIdentifierServiceParameter childObjectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
        childObjectIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        childObjectIdentifierServiceParameter.setTagNumber(0x02);
        childObjectIdentifierServiceParameter.setLengthValueType(4);
        childObjectIdentifierServiceParameter.setObjectType(childDevice.getObjectType());
        childObjectIdentifierServiceParameter.setInstanceNumber(childDevice.getId());
        apdu.getServiceParameters().add(childObjectIdentifierServiceParameter);

        final ServiceParameter timeRemainingServiceParameter = new ServiceParameter();
        timeRemainingServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        timeRemainingServiceParameter.setTagNumber(0x03);
        timeRemainingServiceParameter.setLengthValueType(2);
        timeRemainingServiceParameter.setPayload(new byte[] { (byte) 0x1a, (byte) 0x40 });
        apdu.getServiceParameters().add(timeRemainingServiceParameter);

        // opening {[4]
        final ServiceParameter openingTag4ServiceParameter = new ServiceParameter();
        openingTag4ServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        openingTag4ServiceParameter.setTagNumber(0x04);
        openingTag4ServiceParameter.setLengthValueType(ServiceParameter.OPENING_TAG_CODE);
        apdu.getServiceParameters().add(openingTag4ServiceParameter);

        // property identifier: present-value
        final ServiceParameter propertyIdentifierPresentValueServiceParameter = new ServiceParameter();
        propertyIdentifierPresentValueServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        propertyIdentifierPresentValueServiceParameter.setTagNumber(0x00);
        propertyIdentifierPresentValueServiceParameter.setLengthValueType(0x01);
        propertyIdentifierPresentValueServiceParameter
                .setPayload(new byte[] { (byte) DevicePropertyType.PRESENT_VALUE.getCode() });
        apdu.getServiceParameters().add(propertyIdentifierPresentValueServiceParameter);

        // opening {[2]
        ServiceParameter openingTag2ServiceParameter = new ServiceParameter();
        openingTag2ServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        openingTag2ServiceParameter.setTagNumber(0x02);
        openingTag2ServiceParameter.setLengthValueType(ServiceParameter.OPENING_TAG_CODE);
        apdu.getServiceParameters().add(openingTag2ServiceParameter);

        final ServiceParameter presentValueServiceParameter = new ServiceParameter();
        presentValueServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
        presentValueServiceParameter.setTagNumber(MessageType.ENUMERATED.getValue());
        presentValueServiceParameter.setLengthValueType(0x01);
//        presentValueServiceParameter
//                .setPayload(new byte[] { (byte) ((Boolean) childDevice.getPresentValue() ? 1 : 0) });
        presentValueServiceParameter.setPayload((byte[]) childDevice.getPresentValue());
        apdu.getServiceParameters().add(presentValueServiceParameter);

        // closing }[2]
        ServiceParameter closingTag2ServiceParameter = new ServiceParameter();
        closingTag2ServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        closingTag2ServiceParameter.setTagNumber(0x02);
        closingTag2ServiceParameter.setLengthValueType(ServiceParameter.CLOSING_TAG_CODE);
        apdu.getServiceParameters().add(closingTag2ServiceParameter);

        // property identifier: status-flags
        final ServiceParameter propertyIdentifierStatusFlagsServiceParameter = new ServiceParameter();
        propertyIdentifierStatusFlagsServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        propertyIdentifierStatusFlagsServiceParameter.setTagNumber(0x00);
        propertyIdentifierStatusFlagsServiceParameter.setLengthValueType(0x01);
        propertyIdentifierStatusFlagsServiceParameter
                .setPayload(new byte[] { (byte) DevicePropertyType.STATUS_FLAGS.getCode() });
        apdu.getServiceParameters().add(propertyIdentifierStatusFlagsServiceParameter);

        // opening {[2]
        openingTag2ServiceParameter = new ServiceParameter();
        openingTag2ServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        openingTag2ServiceParameter.setTagNumber(0x02);
        openingTag2ServiceParameter.setLengthValueType(ServiceParameter.OPENING_TAG_CODE);
        apdu.getServiceParameters().add(openingTag2ServiceParameter);

        final ServiceParameter statusFlagsServiceParameter = childDevice.getStatusFlagsServiceParameter();
        apdu.getServiceParameters().add(statusFlagsServiceParameter);

        // closing }[2]
        closingTag2ServiceParameter = new ServiceParameter();
        closingTag2ServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        closingTag2ServiceParameter.setTagNumber(0x02);
        closingTag2ServiceParameter.setLengthValueType(ServiceParameter.CLOSING_TAG_CODE);
        apdu.getServiceParameters().add(closingTag2ServiceParameter);

        // closing }[4]
        final ServiceParameter closingTagServiceParameter = new ServiceParameter();
        closingTagServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        closingTagServiceParameter.setTagNumber(0x04);
        closingTagServiceParameter.setLengthValueType(ServiceParameter.CLOSING_TAG_CODE);
        apdu.getServiceParameters().add(closingTagServiceParameter);

        final DefaultMessage responseMessage = new DefaultMessage();
        responseMessage.setVirtualLinkControl(virtualLinkControl);
        responseMessage.setNpdu(npdu);
        responseMessage.setApdu(apdu);

        virtualLinkControl.setLength(responseMessage.getDataLength());

        try {
            final InetAddress datagramPacketAddress = InetAddress.getByName(targetIp);
            communicationService.pointToPointMessage(responseMessage, datagramPacketAddress);
        } catch (final UnknownHostException e) {
            LOG.error(e.getMessage(), e);
        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public Map<Integer, String> getVendorMap() {
        return vendorMap;
    }

    public void setVendorMap(final Map<Integer, String> vendorMap) {
        this.vendorMap = vendorMap;
    }

    public CommunicationService getCommunicationService() {
        return communicationService;
    }

    public void setCommunicationService(final CommunicationService communicationService) {
        this.communicationService = communicationService;
    }

    public Device getParentDevice() {
        return parentDevice;
    }

    public void setParentDevice(final Device parentDevice) {
        this.parentDevice = parentDevice;
    }

    public Device getChildDevice() {
        return childDevice;
    }

    public void setChildDevice(final Device childDevice) {
        this.childDevice = childDevice;
    }

}
