package de.bacnetz.devices;

public class DeviceCreationDescriptor {

    private int amountOfDevices = 1;

    private int startDeviceId = 20000;

    private int deviceIdIncrement = 1;

    private int deviceIdOffset = 0;

    private DeviceType deviceType;

    private String deviceName = "IO 420";

    private String modelName;

    @Override
    public String toString() {
        return "DeviceCreationDescriptor [amountOfDevices=" + amountOfDevices + ", startDeviceId=" + startDeviceId
                + ", deviceIdIncrement=" + deviceIdIncrement + ", deviceIdOffset=" + deviceIdOffset + ", deviceType="
                + deviceType + ", deviceName=" + deviceName + ", modelName=" + modelName + "]";
    }

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

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(final DeviceType deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(final String deviceName) {
        this.deviceName = deviceName;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(final String modelName) {
        this.modelName = modelName;
    }

}
