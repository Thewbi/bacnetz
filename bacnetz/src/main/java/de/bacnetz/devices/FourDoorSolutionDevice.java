package de.bacnetz.devices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.bacnetz.stack.ObjectIdentifierServiceParameter;

public class FourDoorSolutionDevice extends BaseDevice {

    private static final Logger LOG = LogManager.getLogger(FourDoorSolutionDevice.class);

    private int tempActionId;

    @Override
    public void onValueChanged(final Device device, final DeviceProperty<Object> presentValueDeviceProperty,
            final Object oldPresentValue, final Object newPresentValue) {
        throw new RuntimeException("Not implemented yet!");
    }

    @Override
    public void executeAction() {

        allToggle();
//        moduloToggle();
    }

    private void allToggle() {

        getLogger().info("Toogling all doors on device: '{}'", getId());

        final int startId = 0;

        getLogger().trace("Toggling door 1 ...");
        final ObjectIdentifierServiceParameter createFromTypeAndInstanceNumber1 = ObjectIdentifierServiceParameter
                .createFromTypeAndInstanceNumber(ObjectType.BINARY_INPUT, startId + 1);
        final Device door1CloseStateBinaryInput = findDevice(createFromTypeAndInstanceNumber1);
        if (door1CloseStateBinaryInput != null) {
            final byte[] byteArray1 = (byte[]) door1CloseStateBinaryInput.getPresentValue();
            door1CloseStateBinaryInput.writeProperty(DeviceProperty.PRESENT_VALUE,
                    new byte[] { (byte) (1 - byteArray1[0]) });
        }

        getLogger().trace("Toggling door 2 ...");
        final ObjectIdentifierServiceParameter createFromTypeAndInstanceNumber2 = ObjectIdentifierServiceParameter
                .createFromTypeAndInstanceNumber(ObjectType.BINARY_INPUT, startId + 2);
        final Device door2CloseStateBinaryInput = findDevice(createFromTypeAndInstanceNumber2);
        if (door2CloseStateBinaryInput != null) {
            final byte[] byteArray2 = (byte[]) door2CloseStateBinaryInput.getPresentValue();
            door2CloseStateBinaryInput.writeProperty(DeviceProperty.PRESENT_VALUE,
                    new byte[] { (byte) (1 - byteArray2[0]) });
        }

        getLogger().trace("Toggling door 3 ...");
        final ObjectIdentifierServiceParameter createFromTypeAndInstanceNumber3 = ObjectIdentifierServiceParameter
                .createFromTypeAndInstanceNumber(ObjectType.BINARY_INPUT, startId + 3);
        final Device door3CloseStateBinaryInput = findDevice(createFromTypeAndInstanceNumber3);
        if (door3CloseStateBinaryInput != null) {
            final byte[] byteArray3 = (byte[]) door3CloseStateBinaryInput.getPresentValue();
            door3CloseStateBinaryInput.writeProperty(DeviceProperty.PRESENT_VALUE,
                    new byte[] { (byte) (1 - byteArray3[0]) });
        }

        getLogger().trace("Toggling door 4 ...");
        final ObjectIdentifierServiceParameter createFromTypeAndInstanceNumber4 = ObjectIdentifierServiceParameter
                .createFromTypeAndInstanceNumber(ObjectType.BINARY_INPUT, startId + 4);
        final Device door4CloseStateBinaryInput = findDevice(createFromTypeAndInstanceNumber4);
        if (door4CloseStateBinaryInput != null) {
            final byte[] byteArray4 = (byte[]) door4CloseStateBinaryInput.getPresentValue();
            door4CloseStateBinaryInput.writeProperty(DeviceProperty.PRESENT_VALUE,
                    new byte[] { (byte) (1 - byteArray4[0]) });
        }
    }

    @SuppressWarnings("unused")
    private void moduloToggle() {

        final int startId = 0;

        if (tempActionId == 0) {
            getLogger().info("Toggling door 1 ...");
            final ObjectIdentifierServiceParameter createFromTypeAndInstanceNumber1 = ObjectIdentifierServiceParameter
                    .createFromTypeAndInstanceNumber(ObjectType.BINARY_INPUT, startId + 1);
            final Device door1CloseStateBinaryInput = findDevice(createFromTypeAndInstanceNumber1);
            if (door1CloseStateBinaryInput != null) {
                final byte[] byteArray1 = (byte[]) door1CloseStateBinaryInput.getPresentValue();
                door1CloseStateBinaryInput.writeProperty(DeviceProperty.PRESENT_VALUE,
                        new byte[] { (byte) (1 - byteArray1[0]) });
            }
        }

        if (tempActionId == 1) {
            getLogger().info("Toggling door 2 ...");
            final ObjectIdentifierServiceParameter createFromTypeAndInstanceNumber2 = ObjectIdentifierServiceParameter
                    .createFromTypeAndInstanceNumber(ObjectType.BINARY_INPUT, startId + 2);
            final Device door2CloseStateBinaryInput = findDevice(createFromTypeAndInstanceNumber2);
            if (door2CloseStateBinaryInput != null) {
                final byte[] byteArray2 = (byte[]) door2CloseStateBinaryInput.getPresentValue();
                door2CloseStateBinaryInput.writeProperty(DeviceProperty.PRESENT_VALUE,
                        new byte[] { (byte) (1 - byteArray2[0]) });
            }
        }

        if (tempActionId == 2) {
            getLogger().info("Toggling door 3 ...");
            final ObjectIdentifierServiceParameter createFromTypeAndInstanceNumber3 = ObjectIdentifierServiceParameter
                    .createFromTypeAndInstanceNumber(ObjectType.BINARY_INPUT, startId + 3);
            final Device door3CloseStateBinaryInput = findDevice(createFromTypeAndInstanceNumber3);
            if (door3CloseStateBinaryInput != null) {
                final byte[] byteArray3 = (byte[]) door3CloseStateBinaryInput.getPresentValue();
                door3CloseStateBinaryInput.writeProperty(DeviceProperty.PRESENT_VALUE,
                        new byte[] { (byte) (1 - byteArray3[0]) });
            }
        }

        if (tempActionId == 3) {
            getLogger().info("Toggling door 4 ...");
            final ObjectIdentifierServiceParameter createFromTypeAndInstanceNumber4 = ObjectIdentifierServiceParameter
                    .createFromTypeAndInstanceNumber(ObjectType.BINARY_INPUT, startId + 4);
            final Device door4CloseStateBinaryInput = findDevice(createFromTypeAndInstanceNumber4);
            if (door4CloseStateBinaryInput != null) {
                final byte[] byteArray4 = (byte[]) door4CloseStateBinaryInput.getPresentValue();
                door4CloseStateBinaryInput.writeProperty(DeviceProperty.PRESENT_VALUE,
                        new byte[] { (byte) (1 - byteArray4[0]) });
            }
        }

        // toggle all doors
        if (tempActionId == 4) {

            getLogger().info("Toggling door 1 ...");
            final ObjectIdentifierServiceParameter createFromTypeAndInstanceNumber1 = ObjectIdentifierServiceParameter
                    .createFromTypeAndInstanceNumber(ObjectType.BINARY_INPUT, startId + 1);
            final Device door1CloseStateBinaryInput = findDevice(createFromTypeAndInstanceNumber1);
            if (door1CloseStateBinaryInput != null) {
                final byte[] byteArray1 = (byte[]) door1CloseStateBinaryInput.getPresentValue();
                door1CloseStateBinaryInput.writeProperty(DeviceProperty.PRESENT_VALUE,
                        new byte[] { (byte) (1 - byteArray1[0]) });
            }

            getLogger().info("Toggling door 2 ...");
            final ObjectIdentifierServiceParameter createFromTypeAndInstanceNumber2 = ObjectIdentifierServiceParameter
                    .createFromTypeAndInstanceNumber(ObjectType.BINARY_INPUT, startId + 2);
            final Device door2CloseStateBinaryInput = findDevice(createFromTypeAndInstanceNumber2);
            if (door2CloseStateBinaryInput != null) {
                final byte[] byteArray2 = (byte[]) door2CloseStateBinaryInput.getPresentValue();
                door2CloseStateBinaryInput.writeProperty(DeviceProperty.PRESENT_VALUE,
                        new byte[] { (byte) (1 - byteArray2[0]) });
            }

            getLogger().info("Toggling door 3 ...");
            final ObjectIdentifierServiceParameter createFromTypeAndInstanceNumber3 = ObjectIdentifierServiceParameter
                    .createFromTypeAndInstanceNumber(ObjectType.BINARY_INPUT, startId + 3);
            final Device door3CloseStateBinaryInput = findDevice(createFromTypeAndInstanceNumber3);
            if (door3CloseStateBinaryInput != null) {
                final byte[] byteArray3 = (byte[]) door3CloseStateBinaryInput.getPresentValue();
                door3CloseStateBinaryInput.writeProperty(DeviceProperty.PRESENT_VALUE,
                        new byte[] { (byte) (1 - byteArray3[0]) });
            }

            getLogger().info("Toggling door 4 ...");
            final ObjectIdentifierServiceParameter createFromTypeAndInstanceNumber4 = ObjectIdentifierServiceParameter
                    .createFromTypeAndInstanceNumber(ObjectType.BINARY_INPUT, startId + 4);
            final Device door4CloseStateBinaryInput = findDevice(createFromTypeAndInstanceNumber4);
            if (door4CloseStateBinaryInput != null) {
                final byte[] byteArray4 = (byte[]) door4CloseStateBinaryInput.getPresentValue();
                door4CloseStateBinaryInput.writeProperty(DeviceProperty.PRESENT_VALUE,
                        new byte[] { (byte) (1 - byteArray4[0]) });
            }
        }

        tempActionId++;
        tempActionId = tempActionId % 5;
    }

    @Override
    public Logger getLogger() {
        return LOG;
    }

}
