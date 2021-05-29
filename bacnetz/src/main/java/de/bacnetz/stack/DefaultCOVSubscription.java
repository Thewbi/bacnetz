package de.bacnetz.stack;

import java.util.Map;

import de.bacnetz.controller.Message;
import de.bacnetz.devices.Device;
import de.bacnetz.services.CommunicationService;
import de.bacnetz.threads.ToggleDoorOpenStateThread;

public class DefaultCOVSubscription implements COVSubscription {

    /** IP of the subscriber */
    private String clientIp;

    /** port of the subscriber */
    private int clientPort;

    private Message requestMessage;

    private int subscriberProcessId;

    private boolean issueConfirmedNotifications;

    private long lifetime;

    private CommunicationService communicationService;

    private Device parentDevice;

    private Device device;

    private Map<Integer, String> vendorMap;

    private NPDU npdu;

    @Override
    public void valueChanged(final Object newValue) {
        ToggleDoorOpenStateThread.sendCOV(parentDevice, device, vendorMap, requestMessage, clientIp, clientPort,
                communicationService, npdu);
    }

    @Override
    public String getClientIp() {
        return clientIp;
    }

    @Override
    public void setClientIp(final String clientIp) {
        this.clientIp = clientIp;
    }

    @Override
    public int getSubscriberProcessId() {
        return subscriberProcessId;
    }

    @Override
    public void setSubscriberProcessId(final int subscriberProcessId) {
        this.subscriberProcessId = subscriberProcessId;
    }

    @Override
    public boolean isIssueConfirmedNotifications() {
        return issueConfirmedNotifications;
    }

    @Override
    public void setIssueConfirmedNotifications(final boolean issueConfirmedNotifications) {
        this.issueConfirmedNotifications = issueConfirmedNotifications;
    }

    public long getLifetime() {
        return lifetime;
    }

    public void setLifetime(final long lifetime) {
        this.lifetime = lifetime;
    }

    @Override
    public CommunicationService getCommunicationService() {
        return communicationService;
    }

    @Override
    public void setCommunicationService(final CommunicationService communicationService) {
        this.communicationService = communicationService;
    }

    public Device getParentDevice() {
        return parentDevice;
    }

    public void setParentDevice(final Device parentDevice) {
        this.parentDevice = parentDevice;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(final Device device) {
        this.device = device;
    }

    public Map<Integer, String> getVendorMap() {
        return vendorMap;
    }

    public void setVendorMap(final Map<Integer, String> vendorMap) {
        this.vendorMap = vendorMap;
    }

    public NPDU getNpdu() {
        return npdu;
    }

    public void setNpdu(final NPDU npdu) {
        this.npdu = npdu;
    }

    public int getClientPort() {
        return clientPort;
    }

    public void setClientPort(final int clientPort) {
        this.clientPort = clientPort;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((clientIp == null) ? 0 : clientIp.hashCode());
        result = prime * result + ((device == null) ? 0 : device.hashCode());
        result = prime * result + clientPort;
        result = prime * result + subscriberProcessId;
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final DefaultCOVSubscription other = (DefaultCOVSubscription) obj;
        if (clientIp == null) {
            if (other.clientIp != null)
                return false;
        } else if (!clientIp.equals(other.clientIp))
            return false;
        if (device == null) {
            if (other.device != null)
                return false;
        } else if (!device.equals(other.device))
            return false;
        if (clientPort != other.clientPort)
            return false;
        if (subscriberProcessId != other.subscriberProcessId)
            return false;
        return true;
    }

    public Message getRequestMessage() {
        return requestMessage;
    }

    public void setRequestMessage(final Message requestMessage) {
        this.requestMessage = requestMessage;
    }

}
