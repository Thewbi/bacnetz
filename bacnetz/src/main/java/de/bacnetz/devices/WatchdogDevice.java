package de.bacnetz.devices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.bacnetz.stack.ObjectIdentifierServiceParameter;

public class WatchdogDevice extends BaseDevice {

    private static final Logger LOG = LogManager.getLogger(WatchdogDevice.class);

    @Override
    public void onValueChanged(final Device device, final DeviceProperty<Object> presentValueDeviceProperty,
            final Object oldPresentValue, final Object newPresentValue) {

        Device watchdogState = null;

        // device id 4 is the command deviec
        if (device.getId() == 4) {

            watchdogState = findDevice(
                    ObjectIdentifierServiceParameter.createFromTypeAndInstanceNumber(ObjectType.MULTI_STATE_VALUE, 3));

            final int newValue = (int) newPresentValue;
            switch (newValue) {

            // 1 is enable watchdog command
            case 1:
                watchdogState.writeProperty(DeviceProperty.PRESENT_VALUE, 1);
                break;

            // 2 is disable watchdog command
            case 2:
                watchdogState.writeProperty(DeviceProperty.PRESENT_VALUE, 2);
                break;

            // 3 is short time released command
            case 3:
                watchdogState.writeProperty(DeviceProperty.PRESENT_VALUE, 3);
                break;

            default:
                break;
            }
        }
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
