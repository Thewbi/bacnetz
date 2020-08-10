package de.bacnetz.stack;

import java.util.Map;

import de.bacnetz.devices.Device;
import de.bacnetz.services.CommunicationService;
import de.bacnetz.threads.ToogleDoorOpenStateThread;

public class DefaultCOVSubscription implements COVSubscription {

    /** IP of the subscriber */
    private String clientIp;

    private int subscriberProcessId;

    private boolean issueConfirmedNotifications;

    private long lifetime;

    private CommunicationService communicationService;

    private Device parentDevice;

    private Device device;

    private Map<Integer, String> vendorMap;

    @Override
    public void vaueChanged(final Object newValue) {
        ToogleDoorOpenStateThread.sendCOV(parentDevice, device, vendorMap, clientIp, communicationService);
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

}
