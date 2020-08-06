package de.bacnetz.devices;

import de.bacnetz.controller.Message;

public class BinaryInputDevice extends DefaultDevice {

    @Override
    public Message processPresentValueProperty(final DeviceProperty<?> deviceProperty, final Message requestMessage) {

        final int value = ((boolean) getPresentValue()) ? 1 : 0;

        return getMessageFactory().createEnumeratedProperty(this, requestMessage.getApdu().getInvokeId(),
                deviceProperty.getPropertyKey(), new byte[] { (byte) value });
    }

}
