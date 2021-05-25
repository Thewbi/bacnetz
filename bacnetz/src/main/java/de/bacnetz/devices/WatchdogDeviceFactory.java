package de.bacnetz.devices;

import java.util.Map;

public class WatchdogDeviceFactory extends TZ320DeviceFactory {

    private static final int WATCHDOG_INDEX = 1;

    @Override
    protected BaseDevice createNewInstance() {
        return new WatchdogDevice();
    }

    @Override
    protected void getModuleTypeChildDevice(final Device device, final Map<Integer, String> vendorMap) {
        // multi_state_value, 1
        final Device childDevice = new DefaultDevice();
        childDevice.setParentDevice(device);
        childDevice.setObjectType(ObjectType.MULTI_STATE_VALUE);
        childDevice.setId(1);
        childDevice.setName("module_type");
        childDevice.setDescription("no entry");
        childDevice.setLocation("Office");
        childDevice.setVendorMap(vendorMap);
        childDevice.setMessageFactory(getMessageFactory());
        addPropertiesToModuleTypeDevice(childDevice);
        device.getChildDevices().add(childDevice);
        childDevice.writeProperty(DeviceProperty.PRESENT_VALUE, WATCHDOG_INDEX);
        device.getDeviceMap().put(childDevice.getObjectIdentifierServiceParameter(), childDevice);
        addListenersToDevice(childDevice);
    }

    @Override
    protected void getCommandOptions(final Device device) {
        device.getStates().add("enable watchdog");
        device.getStates().add("disable watchdog");
        device.getStates().add("short time released");
    }

    @Override
    protected String getCommandName() {
        return "watchdog_command";
    }

    @Override
    protected void getStateOptions(final Device device) {
        device.getStates().add("watchdog enabled");
        device.getStates().add("watchdog disabled");
        device.getStates().add("short time released");
        device.getStates().add("alarm supressed");
        device.getStates().add("alarm delayed");
        device.getStates().add("alarm active");
    }

    @Override
    protected String getStateName() {
        return "watchdog_state";
    }

    @Override
    protected void getAlarmTypeOptions(final Device device) {
        device.getStates().add("no alarm");
        device.getStates().add("fire alarm");
        device.getStates().add("smoke heat alarm");
        device.getStates().add("burglary alarm");
        device.getStates().add("sabotage alarm");
        device.getStates().add("time switch group conflict");
        device.getStates().add("burglary group conflict");
        device.getStates().add("fire alarm group conflict");
        device.getStates().add("can/power supply disturbed");
        device.getStates().add("BACnet disturbed");
        device.getStates().add("Iq Aut active leaf error");
        device.getStates().add("Iq Aut inactive leaf error");
        device.getStates().add("Iq Lock El error");
        device.getStates().add("Iq Lock 72 error");
        device.getStates().add("watchdog alarm active");
    }

    @Override
    protected String getAlarmTypeName() {
        return "alarm_type";
    }

}
