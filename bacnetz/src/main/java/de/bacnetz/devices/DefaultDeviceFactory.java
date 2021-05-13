package de.bacnetz.devices;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import de.bacnetz.configuration.ConfigurationManager;
import de.bacnetz.factory.Factory;
import de.bacnetz.factory.MessageFactory;
import de.bacnetz.stack.ObjectIdentifierServiceParameter;

public class DefaultDeviceFactory implements Factory<Device> {

    @Autowired
    private ConfigurationManager configurationManager;

    @Autowired
    private MessageFactory messageFactory;

    private FourDoorSolutionDeviceFactory fourDoorSolutionDeviceFactory;

    private TZ320DeviceFactory tz320DeviceFactory;

    @SuppressWarnings("unchecked")
    @Override
    public Device create(final Object... args) {

        final DeviceType deviceType = (DeviceType) args[0];
        final Map<ObjectIdentifierServiceParameter, Device> deviceMap = (Map<ObjectIdentifierServiceParameter, Device>) args[1];
        final Map<Integer, String> vendorMap = (Map<Integer, String>) args[2];
        final int deviceId = (int) args[3];
        final String objectName = (String) args[4];
        final int vendorId = (int) args[5];

        if (fourDoorSolutionDeviceFactory == null) {
            fourDoorSolutionDeviceFactory = new FourDoorSolutionDeviceFactory();
        }

        if (tz320DeviceFactory == null) {
            tz320DeviceFactory = new TZ320DeviceFactory();
        }

        switch (deviceType) {

        case FOUR_DOOR_SOLUTION:
            return fourDoorSolutionDeviceFactory.create(deviceMap, vendorMap, deviceId, objectName, vendorId);

        case TZ320:
            return tz320DeviceFactory.create(deviceMap, vendorMap, deviceId, objectName, vendorId);

        default:
            throw new RuntimeException("Unknown DeviceType " + deviceType);
        }

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
