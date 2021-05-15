package de.bacnetz.devices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FourDoorSolutionDevice extends BaseDevice {

    private static final Logger LOG = LogManager.getLogger(FourDoorSolutionDevice.class);

    @Override
    public void onValueChanged(final Device device, final DeviceProperty<Object> presentValueDeviceProperty,
            final Object oldPresentValue, final Object newPresentValue) {
        throw new RuntimeException("Not implemented yet!");
    }

    @Override
    public Logger getLogger() {
        return LOG;
    }

}
