package de.bacnetz.devices;

public class DeviceCreationDescriptor {

    private int amountOfDevices = 1;

    private int startDeviceId = 20000;

    private int deviceIdIncrement = 1;

    private int deviceIdOffset = 0;

    public int getAmountOfDevices() {
        return amountOfDevices;
    }

    public void setAmountOfDevices(final int amountOfDevices) {
        this.amountOfDevices = amountOfDevices;
    }

    public int getStartDeviceId() {
        return startDeviceId;
    }

    public void setStartDeviceId(final int startDeviceId) {
        this.startDeviceId = startDeviceId;
    }

    public int getDeviceIdIncrement() {
        return deviceIdIncrement;
    }

    public void setDeviceIdIncrement(final int deviceIdIncrement) {
        this.deviceIdIncrement = deviceIdIncrement;
    }

    public int getDeviceIdOffset() {
        return deviceIdOffset;
    }

    public void setDeviceIdOffset(final int deviceIdOffset) {
        this.deviceIdOffset = deviceIdOffset;
    }

}
