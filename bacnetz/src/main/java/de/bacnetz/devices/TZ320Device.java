package de.bacnetz.devices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.bacnetz.common.websocket.subscriptions.Subscription;
import de.bacnetz.stack.ObjectIdentifierServiceParameter;

public class TZ320Device extends BaseDevice {

    private static final int DOOR_STATE_SUB_DEVICE_ID = 2;

    private static final int UNLOCK_COMMAND_VALUE = 2;

    private static final Logger LOG = LogManager.getLogger(TZ320Device.class);

    @Override
    public void executeAction() {

        // find the door state sub-device
        final ObjectIdentifierServiceParameter doorStateObjectIdentifier = ObjectIdentifierServiceParameter
                .createFromTypeAndInstanceNumber(ObjectType.BINARY_INPUT, DOOR_STATE_SUB_DEVICE_ID);
        final Device door1CloseStateBinaryInput = findDevice(doorStateObjectIdentifier);

        // toggle the door state value (open, close the door)
        if (door1CloseStateBinaryInput != null) {
            final byte[] data = (byte[]) door1CloseStateBinaryInput.getPresentValue();
            door1CloseStateBinaryInput.writeProperty(DeviceProperty.PRESENT_VALUE, new byte[] { (byte) (1 - data[0]) });
        }
    }

    @Override
    public void onValueChanged(final Device device, final DeviceProperty<Object> presentValueDeviceProperty,
            final Object oldPresentValue, final Object newPresentValue) {

        LOG.info("OnValueChange: Device: {}, DeviceProperty: {}, oldValue: {}, newValue: {}", device,
                presentValueDeviceProperty, oldPresentValue, newPresentValue);

        Device tz320State = null;
        Device lockState = null;
        Device closeState = null;

        // door state
        if (device.getId() == 2) {

            final byte[] newValue = (byte[]) newPresentValue;
            switch (newValue[0]) {

            case 0:
                this.getListeners().entrySet().stream().filter(kvp -> {
                    return kvp.getKey() instanceof Subscription;
                }).forEach(kvp -> kvp.getValue().event(this, "Door Open"));
                break;

            case 1:
                this.getListeners().entrySet().stream().filter(kvp -> {
                    return kvp.getKey() instanceof Subscription;
                }).forEach(kvp -> kvp.getValue().event(this, "Door Closed"));
                break;
            }

        }

        // TZ320 logic - when the command property changed, also update the state
        // accordingly
        // TODO: reset the command property to 1 again!
        //
        // id = 4 => command multi_state_value
        if (device.getId() == 4) {

            final int newValue = (int) newPresentValue;
            switch (newValue) {

            // unlock
            case UNLOCK_COMMAND_VALUE:
                // sub-device 3 is the device's state - locked, unlocked, short time released
                tz320State = findDevice(ObjectIdentifierServiceParameter
                        .createFromTypeAndInstanceNumber(ObjectType.MULTI_STATE_VALUE, 3));
                tz320State.writeProperty(DeviceProperty.PRESENT_VALUE, 1);

                // sub-device 1 is lock state
                // 1 == locked, 0 == unlocked
                lockState = findDevice(
                        ObjectIdentifierServiceParameter.createFromTypeAndInstanceNumber(ObjectType.BINARY_INPUT, 1));
                lockState.writeProperty(DeviceProperty.PRESENT_VALUE, 0);

                // sub-device 2 is door state
                // 1 == closed, 0 == open
                // lets leave the door close state on 1 always for now
                closeState = findDevice(ObjectIdentifierServiceParameter
                        .createFromTypeAndInstanceNumber(ObjectType.BINARY_INPUT, DOOR_STATE_SUB_DEVICE_ID));
                closeState.writeProperty(DeviceProperty.PRESENT_VALUE, 0);

                this.getListeners().entrySet().stream().filter(kvp -> {
                    return kvp.getKey() instanceof Subscription;
                }).forEach(kvp -> kvp.getValue().event(this, "Lock Unlock"));
                break;

            // lock
            case 3:
                // sub-device 3 is the device's state - locked, unlocked, short time released
                tz320State = findDevice(ObjectIdentifierServiceParameter
                        .createFromTypeAndInstanceNumber(ObjectType.MULTI_STATE_VALUE, 3));
                tz320State.writeProperty(DeviceProperty.PRESENT_VALUE, 4);

                // sub-device 1 is lock state
                // 1 == locked, 0 == unlocked
                lockState = findDevice(
                        ObjectIdentifierServiceParameter.createFromTypeAndInstanceNumber(ObjectType.BINARY_INPUT, 1));
                lockState.writeProperty(DeviceProperty.PRESENT_VALUE, 1);

                // sub-device 2 is door state
                // 1 == closed, 0 == open
                closeState = findDevice(
                        ObjectIdentifierServiceParameter.createFromTypeAndInstanceNumber(ObjectType.BINARY_INPUT, 2));
                closeState.writeProperty(DeviceProperty.PRESENT_VALUE, 1);

                this.getListeners().entrySet().stream().filter(kvp -> {
                    return kvp.getKey() instanceof Subscription;
                }).forEach(kvp -> kvp.getValue().event(this, "Lock Locked"));
                break;

            // short time release (Kurzzeitfreigabe, KZF)
            case 4:
                // sub-device 3 is the device's state - locked, unlocked, short time released
                tz320State = findDevice(ObjectIdentifierServiceParameter
                        .createFromTypeAndInstanceNumber(ObjectType.MULTI_STATE_VALUE, 3));
                tz320State.writeProperty(DeviceProperty.PRESENT_VALUE, 6);

                // sub-device 1 is lock state
                // 1 == locked, 0 == unlocked
                lockState = findDevice(
                        ObjectIdentifierServiceParameter.createFromTypeAndInstanceNumber(ObjectType.BINARY_INPUT, 1));
                lockState.writeProperty(DeviceProperty.PRESENT_VALUE, 0);

                // sub-device 2 is door state
                // 1 == closed, 0 == open
                closeState = findDevice(
                        ObjectIdentifierServiceParameter.createFromTypeAndInstanceNumber(ObjectType.BINARY_INPUT, 2));
                closeState.writeProperty(DeviceProperty.PRESENT_VALUE, 1);

                this.getListeners().entrySet().stream().filter(kvp -> {
                    return kvp.getKey() instanceof Subscription;
                }).forEach(kvp -> kvp.getValue().event(this, "Lock Short Time Release"));
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
