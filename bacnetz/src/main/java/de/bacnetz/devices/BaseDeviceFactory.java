package de.bacnetz.devices;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import de.bacnetz.configuration.ConfigurationManager;
import de.bacnetz.factory.Factory;
import de.bacnetz.factory.MessageFactory;
import de.bacnetz.factory.MessageType;
import de.bacnetz.stack.ObjectIdentifierServiceParameter;

public abstract class BaseDeviceFactory implements Factory<Device> {

    @Autowired
    private ConfigurationManager configurationManager;

    @Autowired
    private MessageFactory messageFactory;

    private FourDoorSolutionDeviceFactory fourDoorSolutionDeviceFactory;

    private TZ320DeviceFactory tz320DeviceFactory;

    private WatchdogDeviceFactory watchdogDeviceFactory;

    protected abstract BaseDevice createNewInstance();

    @SuppressWarnings("unchecked")
    @Override
    public Device create(final Object... args) {

        final DeviceType deviceType = (DeviceType) args[0];
        final Map<ObjectIdentifierServiceParameter, Device> deviceMap = (Map<ObjectIdentifierServiceParameter, Device>) args[1];
        final Map<Integer, String> vendorMap = (Map<Integer, String>) args[2];
        final int deviceId = (int) args[3];
        final String objectName = (String) args[4];
        final String modelName = (String) args[5];
        final int vendorId = (int) args[6];

        if (fourDoorSolutionDeviceFactory == null) {
            fourDoorSolutionDeviceFactory = new FourDoorSolutionDeviceFactory();
            fourDoorSolutionDeviceFactory.setConfigurationManager(configurationManager);
            fourDoorSolutionDeviceFactory.setMessageFactory(messageFactory);
        }

        if (tz320DeviceFactory == null) {
            tz320DeviceFactory = new TZ320DeviceFactory();
            tz320DeviceFactory.setConfigurationManager(configurationManager);
            tz320DeviceFactory.setMessageFactory(messageFactory);
        }

        if (watchdogDeviceFactory == null) {
            watchdogDeviceFactory = new WatchdogDeviceFactory();
            watchdogDeviceFactory.setConfigurationManager(configurationManager);
            watchdogDeviceFactory.setMessageFactory(messageFactory);
        }

        switch (deviceType) {

        case FOUR_DOOR_SOLUTION:
            return fourDoorSolutionDeviceFactory.create(deviceMap, vendorMap, deviceId, objectName, modelName,
                    vendorId);

        case TZ320:
            return tz320DeviceFactory.create(deviceMap, vendorMap, deviceId, objectName, modelName, vendorId);

        case WATCHDOG:
            return watchdogDeviceFactory.create(deviceMap, vendorMap, deviceId, objectName, modelName, vendorId);

        default:
            throw new RuntimeException("Unknown DeviceType " + deviceType);
        }

    }

    protected void addPropertiesToModuleTypeDevice(final Device device) {

        DefaultDeviceProperty<?> deviceProperty = null;

        // 0x24 = 36d event-state
        deviceProperty = new DefaultDeviceProperty<byte[]>("event-state", DeviceProperty.EVENT_STATE,
                new byte[] { (byte) 0x00 }, MessageType.ENUMERATED);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x4A = 74d number-of-states
        device.getStates().add("watchdog");
        device.getStates().add("1_door");
        device.getStates().add("2_doors");
        device.getStates().add("4_doors");
        device.getStates().add("iq_lock_el");
        device.getStates().add("iq_lock_bus");
        device.getStates().add("automatic_stat");
        device.getStates().add("automatic_4sec");
        device.getStates().add("windows");
        device.getStates().add("universal");
        device.getStates().add("RWS_KL400");
        device.getStates().add("DCU_128");
        device.getStates().add("DCU_6_revolving_door");
        device.getStates().add("DCU_128_RWS_KL");
        deviceProperty = new DefaultDeviceProperty<Integer>("number-of-states", DeviceProperty.NUMBER_OF_STATES,
                device.getStates().size(), MessageType.UNSIGNED_INTEGER);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x4B = 75d object-identifier
        final int objectIdentifier = ObjectIdentifierServiceParameter
                .encodeObjectTypeAndInstanceNumber(device.getObjectType(), device.getId());
        deviceProperty = new DefaultDeviceProperty<Integer>("object-identifier", DeviceProperty.OBJECT_IDENTIFIER,
                objectIdentifier, MessageType.BACNET_OBJECT_IDENTIFIER);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x4d = 77d object-name
        deviceProperty = new DefaultDeviceProperty<String>("object-name", DeviceProperty.OBJECT_NAME, device.getName(),
                MessageType.CHARACTER_STRING);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x4F = 79 object-type
        //
        // BACnetObjectType
        // ENUMERATED:
        // analog-input (0)
        // analog-output (1)
        // analog-value (2)
        // binary-input (3)
        // binary-output (4)
        // binary-value (5)
        // device (8)
        // multi-state-input (13)
        // multi-state-output (14)
        // multi-state-value (19)
        deviceProperty = new DefaultDeviceProperty<byte[]>("object-type", DeviceProperty.OBJECT_TYPE,
                new byte[] { (byte) device.getObjectType().getCode() }, MessageType.ENUMERATED);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x51 = 81d - out-of-service
        deviceProperty = new DefaultDeviceProperty<Boolean>("out-of-service", DeviceProperty.OUT_OF_SERVICE,
                device.isOutOfService(), MessageType.BOOLEAN);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x55 = 85d - present-value
        deviceProperty = new DefaultDeviceProperty<Integer>("present-value", DeviceProperty.PRESENT_VALUE,
                (Integer) device.getPresentValue(), MessageType.UNSIGNED_INTEGER);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x1C = 28d - description
        deviceProperty = new DefaultDeviceProperty<String>("description", DeviceProperty.DESCRIPTION,
                device.getDescription(), MessageType.CHARACTER_STRING);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x6E = 110d - state-text
        // composite
        final CompositeDeviceProperty stateTextCompositeDeviceProperty = new CompositeDeviceProperty("state-text",
                DeviceProperty.STATE_TEXT, objectIdentifier, MessageType.UNSIGNED_INTEGER);
        DefaultDeviceProperty<String> subDeviceProperty = null;
        for (final String state : device.getStates()) {
            subDeviceProperty = new DefaultDeviceProperty<String>(state, DeviceProperty.STATE_TEXT, state,
                    MessageType.CHARACTER_STRING);
            stateTextCompositeDeviceProperty.getCompositeList().add(subDeviceProperty);
        }
        device.getProperties().put(stateTextCompositeDeviceProperty.getPropertyKey(), stateTextCompositeDeviceProperty);

        // 0x6F = 111d status-flags
        deviceProperty = new DefaultDeviceProperty<Integer>("status-flags", DeviceProperty.STATUS_FLAGS, 0x00,
                MessageType.UNSIGNED_INTEGER);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);
    }

    public ConfigurationManager getConfigurationManager() {
        return configurationManager;
    }

    public void setConfigurationManager(final ConfigurationManager configurationManager) {
        this.configurationManager = configurationManager;
    }

    public MessageFactory getMessageFactory() {
        return messageFactory;
    }

    public void setMessageFactory(final MessageFactory messageFactory) {
        this.messageFactory = messageFactory;
    }
}
