package de.bacnetz.devices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.bacnetz.stack.ObjectIdentifierServiceParameter;

public class TZ320Device extends BaseDevice {

    private static final Logger LOG = LogManager.getLogger(TZ320Device.class);

    @Override
    public void onValueChanged(final Device device, final DeviceProperty<Object> presentValueDeviceProperty,
            final Object oldPresentValue, final Object newPresentValue) {

        LOG.info("OnValueChange: Device: {}, DeviceProperty: {}, oldValue: {}, newValue: {}", device,
                presentValueDeviceProperty, oldPresentValue, newPresentValue);

        Device tz320State = null;
        Device lockState = null;
        Device closeState = null;

        // TZ320 logic - when the command property changed, also update the state
        // accordingly
        // TODO: reset the command property to 1 again!
        //
        // id = 4 => command multi_state_value
        if (device.getId() == 4) {

            final int newValue = (int) newPresentValue;
            switch (newValue) {

            // unlock
            case 2:
                tz320State = findDevice(ObjectIdentifierServiceParameter
                        .createFromTypeAndInstanceNumber(ObjectType.MULTI_STATE_VALUE, 3));
                tz320State.setPresentValue(1);

                // 1 == locked, 0 == unlocked
                lockState = findDevice(
                        ObjectIdentifierServiceParameter.createFromTypeAndInstanceNumber(ObjectType.BINARY_INPUT, 1));
                lockState.setPresentValue(0);

                // 1 == closed, 0 == open
                // lets leave the door close state on 1 always for now
                closeState = findDevice(
                        ObjectIdentifierServiceParameter.createFromTypeAndInstanceNumber(ObjectType.BINARY_INPUT, 2));
                closeState.setPresentValue(0);
                break;

            // lock
            case 3:
                tz320State = findDevice(ObjectIdentifierServiceParameter
                        .createFromTypeAndInstanceNumber(ObjectType.MULTI_STATE_VALUE, 3));
                tz320State.setPresentValue(4);

                // 1 == locked, 0 == unlocked
                lockState = findDevice(
                        ObjectIdentifierServiceParameter.createFromTypeAndInstanceNumber(ObjectType.BINARY_INPUT, 1));
                lockState.setPresentValue(1);

                // 1 == closed, 0 == open
                closeState = findDevice(
                        ObjectIdentifierServiceParameter.createFromTypeAndInstanceNumber(ObjectType.BINARY_INPUT, 2));
                closeState.setPresentValue(1);
                break;

            // short time release (Kurzzeitfreigabe, KZF)
            case 4:
                tz320State = findDevice(ObjectIdentifierServiceParameter
                        .createFromTypeAndInstanceNumber(ObjectType.MULTI_STATE_VALUE, 3));
                tz320State.setPresentValue(6);

                // 1 == locked, 0 == unlocked
                lockState = findDevice(
                        ObjectIdentifierServiceParameter.createFromTypeAndInstanceNumber(ObjectType.BINARY_INPUT, 1));
                lockState.setPresentValue(0);

                // 1 == closed, 0 == open
                closeState = findDevice(
                        ObjectIdentifierServiceParameter.createFromTypeAndInstanceNumber(ObjectType.BINARY_INPUT, 2));
                closeState.setPresentValue(1);
                break;

            default:
                break;
            }
        }
    }

    @Override
    public Logger getLogger() {
        return LOG;
    }

}
