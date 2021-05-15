package de.bacnetz.devices;

public class DefaultDeviceFactory extends BaseDeviceFactory {

    @Override
    protected BaseDevice createNewInstance() {
        return new DefaultDevice();
    }

}
