package de.bacnetz.devices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.bacnetz.controller.Message;

public class BinaryInputDevice extends DefaultDevice {

    private static final Logger LOG = LogManager.getLogger(BinaryInputDevice.class);

    @Override
    public Message processPresentValueProperty(final DeviceProperty<?> deviceProperty, final Message requestMessage) {

        final int value = ((boolean) getPresentValue()) ? 1 : 0;

        return getMessageFactory().createEnumeratedProperty(this, requestMessage, deviceProperty.getPropertyKey(),
                new byte[] { (byte) value });
    }

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
