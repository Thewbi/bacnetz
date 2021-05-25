package de.bacnetz.devices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A device is a hierarchy of devices and child devices. A FourDoorSolution for
 * example is a device that contains four child devices, one for each of the
 * four doors.<br />
 * <br />
 * 
 * Each door in turn contains a binary child device, which models the door's
 * state (open or closed). This describes a three level hierarchy of
 * devices.<br />
 * <br />
 * 
 * Check the DefaultDeviceFactory class to see how the devices are assembled.
 * Each device stores a reference to it's optional parent device.
 */
public class DefaultDevice extends BaseDevice {

    private static final Logger LOG = LogManager.getLogger(DefaultDevice.class);

    @Override
    public void onValueChanged(final Device device, final DeviceProperty<Object> presentValueDeviceProperty,
            final Object oldPresentValue, final Object newPresentValue) {
        throw new RuntimeException("Not implemented yet!");
    }

    @Override
    public void executeAction() {
        LOG.warn("Not implemented yet!");
    }

    @Override
    public Logger getLogger() {
        return LOG;
    }

}