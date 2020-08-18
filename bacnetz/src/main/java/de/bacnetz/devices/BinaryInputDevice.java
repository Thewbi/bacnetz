package de.bacnetz.devices;

import de.bacnetz.controller.Message;

public class BinaryInputDevice extends DefaultDevice {

    @Override
    public Message processPresentValueProperty(final DeviceProperty<?> deviceProperty, final Message requestMessage) {

        final int value = ((boolean) getPresentValue()) ? 1 : 0;

        return getMessageFactory().createEnumeratedProperty(this, requestMessage.getApdu().getInvokeId(),
                deviceProperty.getPropertyKey(), new byte[] { (byte) value });
    }

    @Override
    public void setPresentValue(final Object newPresentValue) {

        if (!getProperties().containsKey(DevicePropertyType.PRESENT_VALUE.getCode())) {
            return;
        }

//        LOG.info("Set Present Value: " + newPresentValue);

        final DeviceProperty<Object> presentValueDeviceProperty = (DeviceProperty<Object>) getProperties()
                .get(DevicePropertyType.PRESENT_VALUE.getCode());

//        final Integer presentValue = (byte[]) presentValueDeviceProperty.getValue();

//        final boolean valueChanged = false;
        final boolean valueChanged = true;

//        if ((presentValue == null) && (newPresentValue != null)) {
//
//            valueChanged = true;
//
//        } else if ((presentValue != null) && (newPresentValue == null)) {
//
//            valueChanged = true;
//
//        } else if ((presentValue != null) && (newPresentValue != null)) {
//
//            if (!presentValue.equals(newPresentValue)) {
//
//                valueChanged = true;
//            }
//        }

        if (valueChanged) {

            // set new value
            presentValueDeviceProperty.setValue(newPresentValue);

            // send message to all subscribers
            getCovSubscriptions().stream().forEach(s -> {
                s.valueChanged(newPresentValue);
            });
        }
    }

}
