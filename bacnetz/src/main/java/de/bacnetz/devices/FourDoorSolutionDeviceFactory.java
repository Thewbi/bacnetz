package de.bacnetz.devices;

import java.util.Map;

import de.bacnetz.factory.MessageType;
import de.bacnetz.stack.ObjectIdentifierServiceParameter;

public class FourDoorSolutionDeviceFactory extends DefaultDeviceFactory {

    @SuppressWarnings("unchecked")
    @Override
    public Device create(final Object... args) {

        // @formatter:off
        
        // 0 - deviceMap
        // 1 - vendorMap
        // 2 - deviceId
        // 3 - deviceName
        // 4 - modelName
        // 5 - vendorId
        
        // @formatter:on

        final Map<ObjectIdentifierServiceParameter, Device> deviceMap = (Map<ObjectIdentifierServiceParameter, Device>) args[0];
        final Map<Integer, String> vendorMap = (Map<Integer, String>) args[1];
        final int deviceId = (int) args[2];
        final String deviceName = (String) args[3];
        final String modelName = (String) args[4];
        final int vendorId = (int) args[5];

        return createIO420FourDoorSolution(deviceMap, vendorMap, deviceId, deviceName, modelName, vendorId);
    }

    @Override
    protected BaseDevice createNewInstance() {
        return new FourDoorSolutionDevice();
    }

    private Device createIO420FourDoorSolution(final Map<ObjectIdentifierServiceParameter, Device> deviceMap,
            final Map<Integer, String> vendorMap, final int deviceId, final String objectName, final String modelName,
            final int vendorId) {

        final BaseDevice device = createNewInstance();
        device.setObjectType(ObjectType.DEVICE);
        device.setId(deviceId);
        device.setName(objectName);
        device.setModelName(modelName);
        device.setDescription("no entry");
        device.setLocation("Office");
        device.setVendorMap(vendorMap);
        device.setVendorId(vendorId);
        device.setFirmwareRevision("v1.2.3.4.5.6");
        device.setConfigurationManager(getConfigurationManager());
        device.setMessageFactory(getMessageFactory());

        // add children first, because the children are encoded into the device
        // properties during a function call to addPropertiesToDevice()
        addChildrenToDevice(device, vendorMap);

        // make sure to add child devices first using a call to addChildrenToDevice!
        addPropertiesToDevice(device);

        addListenersToDevice(device);

        deviceMap.put(device.getObjectIdentifierServiceParameter(), device);

        return device;
    }

    private void addChildrenToDevice(final Device device, final Map<Integer, String> vendorMap) {

        Device childDevice = null;
        int index = 0;
        final int parentDeviceId = 0;

        // 1 module-type (19, 1)
        index++;
        childDevice = new DefaultDevice();
        childDevice.setParentDevice(device);
        // object-identifier (19, 1)
        childDevice.setObjectType(ObjectType.MULTI_STATE_VALUE);
        childDevice.setId(1);
        childDevice.setName("module_type");
        childDevice.setDescription("no entry");
        childDevice.setLocation("Office");
        childDevice.setVendorMap(vendorMap);
        childDevice.setMessageFactory(getMessageFactory());
        addPropertiesToModuleTypeDevice(childDevice);
        device.getChildDevices().add(childDevice);
        // 4 = four door solution
        childDevice.writeProperty(DeviceProperty.PRESENT_VALUE, 4);
        device.getDeviceMap().put(childDevice.getObjectIdentifierServiceParameter(), childDevice);
        addListenersToDevice(childDevice);

        // 2 - alarm-type (19, 2)
        index++;
        childDevice = new DefaultDevice();
        childDevice.setParentDevice(device);
        // object-identifier (19, 2)
        childDevice.setObjectType(ObjectType.MULTI_STATE_VALUE);
        childDevice.setId(2);
        childDevice.setName("alarm_type");
        childDevice.setDescription("no entry");
        childDevice.setLocation("Office");
        childDevice.setVendorMap(vendorMap);
        childDevice.setMessageFactory(getMessageFactory());
        addPropertiesToAlarmStateDevice(childDevice);
        device.getChildDevices().add(childDevice);
        childDevice.writeProperty(DeviceProperty.PRESENT_VALUE, 1);
        device.getDeviceMap().put(childDevice.getObjectIdentifierServiceParameter(), childDevice);
        addListenersToDevice(childDevice);

        // 7 (notification-class) (15, 50)
        index++;
        childDevice = new DefaultDevice();
        childDevice.setParentDevice(device);
        childDevice.setObjectType(ObjectType.NOTIFICATION_CLASS);
        childDevice.setId(50);
        childDevice.setName("notificaton_class_obj");
        childDevice.setDescription("no entry");
        childDevice.setLocation("Office");
        childDevice.setVendorMap(vendorMap);
        childDevice.setMessageFactory(getMessageFactory());
        addPropertiesToNotificationClassDevice(childDevice);
        device.getChildDevices().add(childDevice);
        device.getDeviceMap().put(childDevice.getObjectIdentifierServiceParameter(), childDevice);
        addListenersToDevice(childDevice);

        index = createDoor1(device, vendorMap, index, parentDeviceId);

        index = createDoor2(device, vendorMap, index, parentDeviceId);

        index = createDoor3(device, vendorMap, index, parentDeviceId);

        createDoor4(device, vendorMap, index, parentDeviceId);
    }

    private int createDoor1(final Device device, final Map<Integer, String> vendorMap, int index,
            final int parentDeviceId) {

        Device childDevice;

        // 3 binary-input,3 - open/close state of door 1 (opened, closed)
        index++;
        childDevice = new BinaryInputDevice();
        childDevice.setParentDevice(device);
        // object-identifier (3, 1)
        childDevice.setObjectType(ObjectType.BINARY_INPUT);
        childDevice.setId(1);
        childDevice.setName("door1_close_state");
        childDevice.setDescription("no entry");
        childDevice.setLocation("Office");
        childDevice.setVendorMap(vendorMap);
        childDevice.setMessageFactory(getMessageFactory());
        addPropertiesToDoorCloseStateDevice(childDevice);
        device.getChildDevices().add(childDevice);
        childDevice.writeProperty(DeviceProperty.PRESENT_VALUE, new byte[] { 1 });
        device.getDeviceMap().put(childDevice.getObjectIdentifierServiceParameter(), childDevice);
        addListenersToDevice(childDevice);

        // 6 - multi_state_value,6 - command
        index++;
        childDevice = new DefaultDevice();
        childDevice.setParentDevice(device);
        // object-identifier (19, 4)
        childDevice.setObjectType(ObjectType.MULTI_STATE_VALUE);
        childDevice.setId(4);
        childDevice.setName("door1_command");
        childDevice.setDescription("no entry");
        childDevice.setLocation("Office");
        childDevice.setVendorMap(vendorMap);
        childDevice.setMessageFactory(getMessageFactory());
        addPropertiesToDoorCommandStateDevice(childDevice);
        device.getChildDevices().add(childDevice);
        childDevice.writeProperty(DeviceProperty.PRESENT_VALUE, 1);
        device.getDeviceMap().put(childDevice.getObjectIdentifierServiceParameter(), childDevice);
        addListenersToDevice(childDevice);

        // 5 - multi_state_value,5 - door1_state - lock state (locked, unlocked,
        // short-time-released)
        index++;
        childDevice = new DefaultDevice();
        childDevice.setParentDevice(device);
        // object-identifier (19, 3)
        childDevice.setObjectType(ObjectType.MULTI_STATE_VALUE);
        childDevice.setId(3);
        childDevice.setName("door1_state");
        childDevice.setDescription("no entry");
        childDevice.setLocation("Office");
        childDevice.setVendorMap(vendorMap);
        childDevice.getStates().add("unlock");
        childDevice.getStates().add("lock");
        childDevice.getStates().add("short time released");
        childDevice.getStates().add("time switch active");
        childDevice.setMessageFactory(getMessageFactory());
        addPropertiesToDoorStateDevice(childDevice);
        device.getChildDevices().add(childDevice);
        childDevice.writeProperty(DeviceProperty.PRESENT_VALUE, 1);
        device.getDeviceMap().put(childDevice.getObjectIdentifierServiceParameter(), childDevice);
        addListenersToDevice(childDevice);

        return index;
    }

    private int createDoor2(final Device device, final Map<Integer, String> vendorMap, int index,
            final int parentDeviceId) {

        Device childDevice;

        // 4 binary-input,2 - open/close state of door 2 or 4???
        index++;
        childDevice = new BinaryInputDevice();
        childDevice.setParentDevice(device);
        childDevice.setObjectType(ObjectType.BINARY_INPUT);
        childDevice.setId(2);
        childDevice.setName("door2_close_state");
        childDevice.setDescription("no entry");
        childDevice.setLocation("Office");
        childDevice.setVendorMap(vendorMap);
        childDevice.setMessageFactory(getMessageFactory());
        addPropertiesToDoorCloseStateDevice(childDevice);
        device.getChildDevices().add(childDevice);
        childDevice.writeProperty(DeviceProperty.PRESENT_VALUE, new byte[] { 1 });
        device.getDeviceMap().put(childDevice.getObjectIdentifierServiceParameter(), childDevice);
        addListenersToDevice(childDevice);

        // 9
        index++;
        childDevice = new DefaultDevice();
        childDevice.setParentDevice(device);
        childDevice.setObjectType(ObjectType.MULTI_STATE_VALUE);
        childDevice.setId(6);
        childDevice.setName("door2_command");
        childDevice.setDescription("no entry");
        childDevice.setLocation("Office");
        childDevice.setMessageFactory(getMessageFactory());
        addPropertiesToDoorCommandStateDevice(childDevice);
        device.getChildDevices().add(childDevice);
        childDevice.writeProperty(DeviceProperty.PRESENT_VALUE, 1);
        device.getDeviceMap().put(childDevice.getObjectIdentifierServiceParameter(), childDevice);
        addListenersToDevice(childDevice);

        // 8, lock state
        index++;
        childDevice = new DefaultDevice();
        childDevice.setParentDevice(device);
        childDevice.setObjectType(ObjectType.MULTI_STATE_VALUE);
        childDevice.setId(5);
        childDevice.setName("door2_state");
        childDevice.setDescription("no entry");
        childDevice.setLocation("Office");
        childDevice.setVendorMap(vendorMap);
        childDevice.getStates().add("unlock");
        childDevice.getStates().add("lock");
        childDevice.getStates().add("short time released");
        childDevice.getStates().add("time switch active");
        childDevice.setMessageFactory(getMessageFactory());
        addPropertiesToDoorStateDevice(childDevice);
        device.getChildDevices().add(childDevice);
        childDevice.writeProperty(DeviceProperty.PRESENT_VALUE, 1);
        device.getDeviceMap().put(childDevice.getObjectIdentifierServiceParameter(), childDevice);
        addListenersToDevice(childDevice);

        return index;
    }

    private int createDoor3(final Device device, final Map<Integer, String> vendorMap, int index,
            final int parentDeviceId) {

        Device childDevice;

        // 10 binary-input,10 - open/close state of door 3
        index++;
        childDevice = new BinaryInputDevice();
        childDevice.setParentDevice(device);
        childDevice.setObjectType(ObjectType.BINARY_INPUT);
        childDevice.setId(3);
        childDevice.setName("door3_close_state");
        childDevice.setDescription("no entry");
        childDevice.setLocation("Office");
        childDevice.setVendorMap(vendorMap);
        childDevice.setMessageFactory(getMessageFactory());
        addPropertiesToDoorCloseStateDevice(childDevice);
        device.getChildDevices().add(childDevice);
        childDevice.writeProperty(DeviceProperty.PRESENT_VALUE, new byte[] { 1 });
        device.getDeviceMap().put(childDevice.getObjectIdentifierServiceParameter(), childDevice);
        addListenersToDevice(childDevice);

        // 13
        index++;
        childDevice = new DefaultDevice();
        childDevice.setParentDevice(device);
        childDevice.setObjectType(ObjectType.MULTI_STATE_VALUE);
        childDevice.setId(8);
        childDevice.setName("door3_command");
        childDevice.setDescription("no entry");
        childDevice.setMessageFactory(getMessageFactory());
        addPropertiesToDoorCommandStateDevice(childDevice);
        device.getChildDevices().add(childDevice);
        childDevice.writeProperty(DeviceProperty.PRESENT_VALUE, 1);
        device.getDeviceMap().put(childDevice.getObjectIdentifierServiceParameter(), childDevice);
        addListenersToDevice(childDevice);

        // 12
        index++;
        childDevice = new DefaultDevice();
        childDevice.setParentDevice(device);
        childDevice.setObjectType(ObjectType.MULTI_STATE_VALUE);
        childDevice.setId(7);
        childDevice.setName("door3_state");
        childDevice.setDescription("no entry");
        childDevice.setLocation("Office");
        childDevice.setVendorMap(vendorMap);
        childDevice.getStates().add("unlock");
        childDevice.getStates().add("lock");
        childDevice.getStates().add("short time released");
        childDevice.getStates().add("time switch active");
        childDevice.setMessageFactory(getMessageFactory());
        addPropertiesToDoorStateDevice(childDevice);
        device.getChildDevices().add(childDevice);
        childDevice.writeProperty(DeviceProperty.PRESENT_VALUE, 1);
        device.getDeviceMap().put(childDevice.getObjectIdentifierServiceParameter(), childDevice);
        addListenersToDevice(childDevice);

        return index;
    }

    private void createDoor4(final Device device, final Map<Integer, String> vendorMap, int index,
            final int parentDeviceId) {

        Device childDevice;

        // 11 binary-input,11 - open/close state of door 4 - door state (open, closed)
        index++;
        childDevice = new BinaryInputDevice();
        childDevice.setParentDevice(device);
        childDevice.setObjectType(ObjectType.BINARY_INPUT);
        childDevice.setId(4);
        childDevice.setName("door4_close_state");
        childDevice.setDescription("no entry");
        childDevice.setLocation("Office");
        childDevice.setVendorMap(vendorMap);
        childDevice.setMessageFactory(getMessageFactory());
        addPropertiesToDoorCloseStateDevice(childDevice);
        device.getChildDevices().add(childDevice);
        childDevice.writeProperty(DeviceProperty.PRESENT_VALUE, new byte[] { 1 });
        device.getDeviceMap().put(childDevice.getObjectIdentifierServiceParameter(), childDevice);
        addListenersToDevice(childDevice);

        // 15 four_door_solution_door4_state MASTER COMMAND and State (locked, unlocked,
        // short_time_released)
        index++;
        childDevice = new DefaultDevice();
        childDevice.setParentDevice(device);
        childDevice.setObjectType(ObjectType.MULTI_STATE_VALUE);
        childDevice.setId(10);
        childDevice.setName("door4_command");
        childDevice.setDescription("no entry");
        childDevice.setLocation("Office");
        childDevice.setVendorMap(vendorMap);
        childDevice.setMessageFactory(getMessageFactory());
        addPropertiesToDoorCommandStateDevice(childDevice);
        device.getChildDevices().add(childDevice);
        childDevice.writeProperty(DeviceProperty.PRESENT_VALUE, 1);
        device.getDeviceMap().put(childDevice.getObjectIdentifierServiceParameter(), childDevice);
        addListenersToDevice(childDevice);

        // 14 four_door_solution_door4_state lock_state
        // (locked, unlocked, short time release)
        index++;
        childDevice = new DefaultDevice();
        childDevice.setParentDevice(device);
        childDevice.setObjectType(ObjectType.MULTI_STATE_VALUE);
        childDevice.setId(9);
        childDevice.setName("door4_state");
        childDevice.setDescription("no entry");
        childDevice.setLocation("Office");
        childDevice.setVendorMap(vendorMap);
        childDevice.getStates().add("unlock");
        childDevice.getStates().add("lock");
        childDevice.getStates().add("short time released");
        childDevice.getStates().add("time switch active");
        childDevice.setMessageFactory(getMessageFactory());
        addPropertiesToDoorStateDevice(childDevice);
        device.getChildDevices().add(childDevice);
        childDevice.writeProperty(DeviceProperty.PRESENT_VALUE, 1);
        device.getDeviceMap().put(childDevice.getObjectIdentifierServiceParameter(), childDevice);
        addListenersToDevice(childDevice);
    }

    private void addPropertiesToDevice(final Device device) {

        DefaultDeviceProperty<?> deviceProperty = null;

        // 0x4B = 75d - object-identifier
        final int objectIdentifier = ObjectIdentifierServiceParameter
                .encodeObjectTypeAndInstanceNumber(device.getObjectType(), device.getId());
        deviceProperty = new DefaultDeviceProperty<Integer>(DevicePropertyType.OBJECT_IDENTIFIER.getName(),
                DevicePropertyType.OBJECT_IDENTIFIER.getCode(), objectIdentifier, MessageType.BACNET_OBJECT_IDENTIFIER);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x3A = 58d - location
        deviceProperty = new DefaultDeviceProperty<String>("location", DeviceProperty.LOCATION, device.getLocation(),
                MessageType.CHARACTER_STRING);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x38 = 56d - local-date
        deviceProperty = new DefaultDeviceProperty<Integer>("local-date", DeviceProperty.LOCAL_TIME, 1,
                MessageType.UNSIGNED_INTEGER);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x39 = 57d - local-time
        deviceProperty = new DefaultDeviceProperty<Integer>("local-time", DeviceProperty.LOCAL_DATE, 1,
                MessageType.UNSIGNED_INTEGER);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0xCB = 203d - time-of-device-restart
        deviceProperty = new DefaultDeviceProperty<Integer>(DevicePropertyType.TIME_OF_DEVICE_RESTART.getName(),
                DevicePropertyType.TIME_OF_DEVICE_RESTART.getCode(), 1, MessageType.UNSIGNED_INTEGER);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x60 = 96d - protocol-services-supported
        //
        // H.5.2.13 Protocol_Object_Types_Supported
        // This property indicates the BACnet protocol object types supported by this
        // device. See 12.10.15. The protocol object
        // types supported shall be at least Analog Input, Analog Output, Analog Value,
        // Binary Input, Binary Output, and Binary
        // Value.
        deviceProperty = new DefaultDeviceProperty<Integer>("protocol-services-supported",
                DeviceProperty.PROTOCOL_SERVICES_SUPPORTED, 1, MessageType.UNSIGNED_INTEGER);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x0C = 12d - application-software-version
        // Values: 0x01 == version 1.0
        deviceProperty = new DefaultDeviceProperty<Integer>("application-software-version",
                DeviceProperty.APPLICATION_SOFTWARE_VERSION, 1, MessageType.UNSIGNED_INTEGER);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x1C = 28d - description
        deviceProperty = new DefaultDeviceProperty<String>("description", DeviceProperty.DESCRIPTION,
                device.getDescription(), MessageType.CHARACTER_STRING);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x70 = 112d - system status
        // 0x00 == operational
        final int systemStatus = 0x00;
        deviceProperty = new DefaultDeviceProperty<byte[]>("system-status", DeviceProperty.SYSTEM_STATUS,
                new byte[] { (byte) systemStatus }, MessageType.ENUMERATED);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x6B = 107d - segmentation supported
        //
        // segmented-both (0)
        // segmented-transmit (1)
        // segmented-receive (2)
        // no-segmentation (3)
        final int segmentationSupported = 0x00;
        deviceProperty = new DefaultDeviceProperty<byte[]>("segmentation-supported",
                DeviceProperty.SEGMENTATION_SUPPORTED, new byte[] { (byte) segmentationSupported },
                MessageType.ENUMERATED);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 1497d = 0x05D9
        // 0x3E = 62d - max-apdu-length-accepted
        //
        // Maximum APDU Length is dependent on the physical layer used, for example the
        // maximum APDU size for BACnet/IP is 1497 octets, but for BACnet MS/TP
        // segments, the maximum APDU size is only 480 octets.
        deviceProperty = new DefaultDeviceProperty<Integer>("max-apdu-length-accepted",
                DeviceProperty.MAX_APDU_LENGTH_ACCEPTED, 1497, MessageType.UNSIGNED_INTEGER);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0xA7 = 167d - max-segments-accepted
        deviceProperty = new DefaultDeviceProperty<Integer>("max-segments-accepted",
                DeviceProperty.MAX_SEGMENTS_ACCEPTED, 1, MessageType.UNSIGNED_INTEGER);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x0A = 10d - APDU-Segment-Timeout
        //
        // APDU Segment-Timeout:
        // Dieser Wert in Millisekunden legt fest, nach welcher Zeitspanne ein
        // quittierpflichtiges, segmentiertes Telegramm als fehlgeschlagen gewertet
        // wird, wenn die Segmentbestätigung ausbleibt. Der Standardwert beträgt
        // 2000 Millisekunden.
        // 2000d == 0x07D0
        deviceProperty = new DefaultDeviceProperty<Integer>("apdu-segment-timeout", DeviceProperty.APDU_SEGMENT_TIMEOUT,
                2000, MessageType.UNSIGNED_INTEGER);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x0B = 11d - APDU-Timeout
        // ADPU Timeout:
        // Dieser Wert in Millisekunden legt fest, nach welcher Zeitspanne ein
        // quittierpflichtiges Telegramm als fehlgeschlagen gewertet wird, wenn die
        // Bestätigung ausbleibt. Der Standardwert beträgt 3000 ms.
        // 3000d == 0x0BB8
        deviceProperty = new DefaultDeviceProperty<Integer>("apdu-timeout", DeviceProperty.APDU_TIMEOUT, 3000,
                MessageType.UNSIGNED_INTEGER);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x9B = 155d - database-revision (155d = 0x9B) defined in ASHRAE on page 696
        // database revision 3
        deviceProperty = new DefaultDeviceProperty<Integer>("database-revision", DeviceProperty.DATABASE_REVISION, 3,
                MessageType.UNSIGNED_INTEGER);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x18 = 24d - daylight-savings-status
        deviceProperty = new DefaultDeviceProperty<Boolean>("daylight-savings-status",
                DeviceProperty.DAYLIGHT_SAVINGS_STATUS, true, MessageType.BOOLEAN);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x24 = 36d - event-state
        deviceProperty = new DefaultDeviceProperty<byte[]>("event-state", DeviceProperty.EVENT_STATE,
                new byte[] { (byte) 0x00 }, MessageType.ENUMERATED);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x3F = 63d - max-info-frames
        deviceProperty = new DefaultDeviceProperty<Integer>("max-info-frames", DeviceProperty.MAX_INFO_FRAMES, 100,
                MessageType.UNSIGNED_INTEGER);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x40 = 64d - max-master
        deviceProperty = new DefaultDeviceProperty<Integer>("max-master", DeviceProperty.MAX_MASTER, 0x7F,
                MessageType.UNSIGNED_INTEGER);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x49 = 73d - number-of-APDU-retries
        deviceProperty = new DefaultDeviceProperty<Integer>("number-of-APDU-retries",
                DeviceProperty.NUMBER_OF_APDU_RETRIES, 0x10, MessageType.UNSIGNED_INTEGER);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x4A = 74d - number-of-states
        deviceProperty = new DefaultDeviceProperty<Integer>("number-of-states", DeviceProperty.NUMBER_OF_STATES, 0x0E,
                MessageType.UNSIGNED_INTEGER);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x4c = 76d (0x4c = 76d) - object list
//      case 0x4c:
//          LOG.trace("<<< READ_PROP: object-list ({})", propertyIdentifierCode);
//          return processObjectListRequest(propertyIdentifierCode, requestMessage);
//      deviceProperty = new DefaultDeviceProperty<Integer>("object-list", DeviceProperty.OBJECT_LIST, null,
//              MessageType.UNSIGNED_INTEGER);
//      device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);
        final CompositeDeviceProperty objectListCompositeDeviceProperty = new CompositeDeviceProperty("object-list",
                DeviceProperty.OBJECT_LIST, null, MessageType.UNSIGNED_INTEGER);
        for (final Device childDevice : device.getChildDevices()) {

            final int childObjectIdentifier = ObjectIdentifierServiceParameter
                    .encodeObjectTypeAndInstanceNumber(childDevice.getObjectType(), childDevice.getId());

            // object identifier for each device
            final DefaultDeviceProperty<Integer> childObjectIdentifierDeviceProperty = new DefaultDeviceProperty<>(
                    DevicePropertyType.OBJECT_IDENTIFIER.getName(), DevicePropertyType.OBJECT_IDENTIFIER.getCode(),
                    childObjectIdentifier, MessageType.BACNET_OBJECT_IDENTIFIER);

            objectListCompositeDeviceProperty.getCompositeList().add(childObjectIdentifierDeviceProperty);
        }
        device.getProperties().put(objectListCompositeDeviceProperty.getPropertyKey(),
                objectListCompositeDeviceProperty);

        // 0x4d = 77d (0x4d = 77d) object-name
//      case 0x4d:
//          LOG.trace("<<< READ_PROP: object-name ({})", propertyIdentifierCode);
//          return processObjectNameProperty(propertyIdentifierCode, requestMessage);
        deviceProperty = new DefaultDeviceProperty<String>("object-name", DeviceProperty.OBJECT_NAME, device.getName(),
                MessageType.CHARACTER_STRING);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x51 = 81d out-of-service
        deviceProperty = new DefaultDeviceProperty<Boolean>("out-of-service", DeviceProperty.OUT_OF_SERVICE, false,
                MessageType.BOOLEAN);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x62 = 98d protocol-version
        deviceProperty = new DefaultDeviceProperty<Integer>("protocol-version", DeviceProperty.PROTOCOL_VERSION, 1,
                MessageType.UNSIGNED_INTEGER);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x77 = 119d UTC-offset
        deviceProperty = new DefaultDeviceProperty<byte[]>("utc-offset", DeviceProperty.UTC_OFFSET,
                new byte[] { (byte) 0xC4 }, MessageType.SINGED_INTEGER_TWOS_COMPLEMENT_NOTATION);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x78 = 120d - vendor-identifier
        deviceProperty = new DefaultDeviceProperty<Integer>("vendor-identifier", DeviceProperty.VENDOR_IDENTIFIER,
                device.getVendorId(), MessageType.UNSIGNED_INTEGER);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

//        // 0x79 = 121d vendor-name
        deviceProperty = new DefaultDeviceProperty<String>("vendor-name", DeviceProperty.VENDOR_NAME,
                device.getVendorMap().get(device.getVendorId()), MessageType.CHARACTER_STRING);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x46 = 70d model-name
        deviceProperty = new DefaultDeviceProperty<String>("model-name", DeviceProperty.MODEL_NAME, device.getName(),
                MessageType.CHARACTER_STRING);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x8B = 139d protocol-revision (0x8B = 139d)
        // the value of the protocol-revision property is set to 0x0C = 12d
        deviceProperty = new DefaultDeviceProperty<Integer>("protocol-revision", DeviceProperty.PROTOCOL_REVISION, 0x0C,
                MessageType.UNSIGNED_INTEGER);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x2C = 44d firmware-revision
//      case 0x2C:
//          LOG.trace("<<< READ_PROP: firmware-revision ({})", propertyIdentifierCode);
//          return processFirmwareRevisionProperty(propertyIdentifierCode, requestMessage);
        deviceProperty = new DefaultDeviceProperty<String>("firmware-revision", DeviceProperty.FIRMWARE_REVISION,
                device.getFirmwareRevision(), MessageType.CHARACTER_STRING);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

//        // 0xC1 = 193d
//        deviceProperty = new DefaultDeviceProperty("align-intervals", DeviceProperty.ALIGN_INTERVALS, true,
//                MessageType.BOOLEAN_PROPERTY);
//        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0xA9 = 169d
        deviceProperty = new DefaultDeviceProperty<Boolean>("auto-slave-discovery", DeviceProperty.AUTO_SLAVE_DISCOVERY,
                false, MessageType.BOOLEAN);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0xAC = 172d
        deviceProperty = new DefaultDeviceProperty<Boolean>("slave-proxy-enable", DeviceProperty.SLAVE_PROXY_ENABLE,
                false, MessageType.BOOLEAN);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x4F = 79
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

        // 0x5F = 95d protocol-conformance-class
        deviceProperty = new DefaultDeviceProperty<Integer>("protocol-conformance-class",
                DeviceProperty.PROTOCOL_CONFORMANCE_CLASS, 0x02, MessageType.UNSIGNED_INTEGER);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x99 = 153d backup-failure-timeout
        deviceProperty = new DefaultDeviceProperty<Integer>("backup-failure-timeout",
                DeviceProperty.BACKUP_FAILURE_TIMEOUT, 538, MessageType.UNSIGNED_INTEGER);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x9A = 154d configuration-files
        deviceProperty = new DefaultDeviceProperty<Integer>("configuration-files", DeviceProperty.CONFIGURATION_FILES,
                0, MessageType.UNSIGNED_INTEGER);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0xC3 = 195d interval-offset
        deviceProperty = new DefaultDeviceProperty<Integer>("interval-offset", DeviceProperty.INTERVALL_OFFSET, 538,
                MessageType.UNSIGNED_INTEGER);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0xC4 = 196d
        // last restart reason - 0x01 == coldstart
        deviceProperty = new DefaultDeviceProperty<byte[]>("last-restart-reason", DeviceProperty.LAST_RESTART_REASON,
                new byte[] { (byte) RestartReason.COLD_START.getCode() }, MessageType.ENUMERATED);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0xCC = 204d interval-offset
        deviceProperty = new DefaultDeviceProperty<Integer>("time-synchronization-intervall",
                DeviceProperty.TIME_SYNCHRONIZATION_INTERVALL, 538, MessageType.UNSIGNED_INTEGER);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

////      // 0xD1 = 209d structured object list
////      case 0xD1:
////          LOG.trace("<<< READ_PROP: structured object list ({})", propertyIdentifierCode);
////          return processStructuredObjectListProperty(propertyIdentifierCode, requestMessage);
//      deviceProperty = new DefaultDeviceProperty<Object>("structured-object-list",
//              DeviceProperty.STRUCTURED_OBJECT_LIST, null, MessageType.UNSIGNED_INTEGER);
//      device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x0173 = 371d property-list
//      case 0x0173:
//          LOG.trace("<<< READ_PROP: property list ({})", propertyIdentifierCode);
//          return processPropertyListProperty(propertyIdentifierCode, requestMessage);
        deviceProperty = new DefaultDeviceProperty<Integer>("property-list", DeviceProperty.PROPERTY_LIST, 0,
                MessageType.UNSIGNED_INTEGER);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0xCA = 202d
//      case 0xCA:
//          LOG.trace("<<< READ_PROP: restart-notification-recipients ({})", propertyIdentifierCode);
//          return processRestartNotificationRecipientsProperty(propertyIdentifierCode, requestMessage);
        deviceProperty = new DefaultDeviceProperty<Integer>("restart-notification-recipients",
                DeviceProperty.RESTART_NOTIFICATION_RECIPIENTS, 0, MessageType.UNSIGNED_INTEGER);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x98 = 152d - active-cov-subscriptions
        deviceProperty = new DefaultDeviceProperty<Integer>(DevicePropertyType.ACTIVE_COV_SUBSCRIPTIONS.getName(),
                DevicePropertyType.ACTIVE_COV_SUBSCRIPTIONS.getCode(), 0, MessageType.UNSIGNED_INTEGER);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);
    }

    private void addPropertiesToAlarmStateDevice(final Device device) {

        DefaultDeviceProperty<?> deviceProperty = null;

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

        // 0x1C = 28d description
        deviceProperty = new DefaultDeviceProperty<String>("description", DeviceProperty.DESCRIPTION,
                device.getDescription(), MessageType.CHARACTER_STRING);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x55 = 85d present-value
        deviceProperty = new DefaultDeviceProperty<Integer>("present-value", DeviceProperty.PRESENT_VALUE,
                (Integer) device.getPresentValue(), MessageType.UNSIGNED_INTEGER);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x6F = 111d status-flags
        deviceProperty = new DefaultDeviceProperty<Integer>("status-flags", DeviceProperty.STATUS_FLAGS, 0x00,
                MessageType.UNSIGNED_INTEGER);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x24 = 36d event-state
        deviceProperty = new DefaultDeviceProperty<byte[]>("event-state", DeviceProperty.EVENT_STATE,
                new byte[] { (byte) 0x00 }, MessageType.ENUMERATED);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x51 = 81d out-of-service
        deviceProperty = new DefaultDeviceProperty<Boolean>("out-of-service", DeviceProperty.OUT_OF_SERVICE,
                device.isOutOfService(), MessageType.BOOLEAN);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x4A = 74d number-of-states
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

        deviceProperty = new DefaultDeviceProperty<Integer>("number-of-states", DeviceProperty.NUMBER_OF_STATES,
                device.getStates().size(), MessageType.UNSIGNED_INTEGER);
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

    }

    private void addPropertiesToDoorCommandStateDevice(final Device device) {

        DefaultDeviceProperty<?> deviceProperty = null;

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

        // 0x1C = 28d description
        deviceProperty = new DefaultDeviceProperty<String>("description", DeviceProperty.DESCRIPTION,
                device.getDescription(), MessageType.CHARACTER_STRING);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x55 = 85d present-value
        deviceProperty = new DefaultDeviceProperty<Integer>("present-value", DeviceProperty.PRESENT_VALUE,
                (Integer) device.getPresentValue(), MessageType.UNSIGNED_INTEGER);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x6F = 111d status-flags
        deviceProperty = new DefaultDeviceProperty<Integer>("status-flags", DeviceProperty.STATUS_FLAGS, 0x00,
                MessageType.UNSIGNED_INTEGER);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x24 = 36d event-state
        deviceProperty = new DefaultDeviceProperty<byte[]>("event-state", DeviceProperty.EVENT_STATE,
                new byte[] { (byte) 0x00 }, MessageType.ENUMERATED);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x51 = 81d out-of-service
        deviceProperty = new DefaultDeviceProperty<Boolean>("out-of-service", DeviceProperty.OUT_OF_SERVICE,
                device.isOutOfService(), MessageType.BOOLEAN);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x4A = 74d number-of-states
        device.getStates().add("unlock");
        device.getStates().add("lock");
        device.getStates().add("short time release");
        deviceProperty = new DefaultDeviceProperty<Integer>("number-of-states", DeviceProperty.NUMBER_OF_STATES,
                device.getStates().size(), MessageType.UNSIGNED_INTEGER);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x6E = 110d state-text
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
    }

    private void addPropertiesToDoorCloseStateDevice(final Device device) {

        DefaultDeviceProperty<?> deviceProperty = null;

        // 0x4B = 75d - object-identifier
        final int objectIdentifier = ObjectIdentifierServiceParameter
                .encodeObjectTypeAndInstanceNumber(device.getObjectType(), device.getId());
        deviceProperty = new DefaultDeviceProperty<Integer>("object-identifier", DeviceProperty.OBJECT_IDENTIFIER,
                objectIdentifier, MessageType.BACNET_OBJECT_IDENTIFIER);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x4d = 77d - object-name
        deviceProperty = new DefaultDeviceProperty<String>("object-name", DeviceProperty.OBJECT_NAME, device.getName(),
                MessageType.CHARACTER_STRING);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x4F = 79 - object-type
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

        // 0x1C = 28d - description
        deviceProperty = new DefaultDeviceProperty<String>("description", DeviceProperty.DESCRIPTION,
                device.getDescription(), MessageType.CHARACTER_STRING);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x55 = 85d - present-value
        deviceProperty = new DefaultDeviceProperty<byte[]>("present-value", DeviceProperty.PRESENT_VALUE,
                new byte[] { (byte) (((Integer) device.getPresentValue())).intValue() }, MessageType.ENUMERATED);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x6F = 111d - status-flags
        deviceProperty = new DefaultDeviceProperty<Integer>("status-flags", DeviceProperty.STATUS_FLAGS, 0x00,
                MessageType.UNSIGNED_INTEGER);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x24 = 36d - event-state
        deviceProperty = new DefaultDeviceProperty<byte[]>("event-state", DeviceProperty.EVENT_STATE,
                new byte[] { (byte) 0x00 }, MessageType.ENUMERATED);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x51 = 81d - out-of-service
        deviceProperty = new DefaultDeviceProperty<Boolean>("out-of-service", DeviceProperty.OUT_OF_SERVICE,
                device.isOutOfService(), MessageType.BOOLEAN);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x54 = 84 - polarity
        //
        // using polarity you can invert the meaning of a boolean/binary-input device
        deviceProperty = new DefaultDeviceProperty<byte[]>("polarity", DeviceProperty.POLARITY,
                new byte[] { (byte) 0x00 }, MessageType.ENUMERATED);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x2E = 46d - inactive-text
        deviceProperty = new DefaultDeviceProperty<String>("inactive-text", DeviceProperty.INACTIVE_TEXT, "open",
                MessageType.CHARACTER_STRING);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x04 = 4d - active-text
        deviceProperty = new DefaultDeviceProperty<String>("active-text", DeviceProperty.ACTIVE_TEXT, "closed",
                MessageType.CHARACTER_STRING);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x0173 = 371d property list
        deviceProperty = new DefaultDeviceProperty<Integer>("property-list", DeviceProperty.PROPERTY_LIST, 0,
                MessageType.UNSIGNED_INTEGER);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);
    }

    private void addPropertiesToNotificationClassDevice(final Device device) {

        DefaultDeviceProperty<?> deviceProperty = null;

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
        // @formatter:off
        deviceProperty = new DefaultDeviceProperty<byte[]>(
                "object-type", 
                DeviceProperty.OBJECT_TYPE,
                new byte[] { (byte) device.getObjectType().getCode() }, 
                MessageType.ENUMERATED);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);
        // @formatter:on

        // 0x1C = 28d description
        deviceProperty = new DefaultDeviceProperty<String>("description", DeviceProperty.DESCRIPTION,
                device.getDescription(), MessageType.CHARACTER_STRING);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x11 = 17d notification-class
        deviceProperty = new DefaultDeviceProperty<Integer>("notification-class", DeviceProperty.NOTIFICATION_CLASS, 50,
                MessageType.UNSIGNED_INTEGER);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x66 = 102d - recipient-list
//        final recipient-list

        // 0x56 = 86d - priority
//        deviceProperty = new DefaultDeviceProperty<Integer>(DevicePropertyType.PRIORITY.getName(),
//                DevicePropertyType.PRIORITY.getCode(), 0, MessageType.UNSIGNED_INTEGER);

        final CompositeDeviceProperty stateTextCompositeDeviceProperty = new CompositeDeviceProperty(
                DevicePropertyType.PRIORITY.getName(), DevicePropertyType.PRIORITY.getCode(), null,
                MessageType.UNSIGNED_INTEGER);

        DefaultDeviceProperty<?> subDeviceProperty = new DefaultDeviceProperty<Integer>(
                DevicePropertyType.PRIORITY.getName(), DevicePropertyType.PRIORITY.getCode(), 20,
                MessageType.UNSIGNED_INTEGER);
        stateTextCompositeDeviceProperty.getCompositeList().add(subDeviceProperty);

        subDeviceProperty = new DefaultDeviceProperty<Integer>(DevicePropertyType.PRIORITY.getName(),
                DevicePropertyType.PRIORITY.getCode(), 10, MessageType.UNSIGNED_INTEGER);
        stateTextCompositeDeviceProperty.getCompositeList().add(subDeviceProperty);

        subDeviceProperty = new DefaultDeviceProperty<Integer>(DevicePropertyType.PRIORITY.getName(),
                DevicePropertyType.PRIORITY.getCode(), 30, MessageType.UNSIGNED_INTEGER);
        stateTextCompositeDeviceProperty.getCompositeList().add(subDeviceProperty);

        device.getProperties().put(stateTextCompositeDeviceProperty.getPropertyKey(), stateTextCompositeDeviceProperty);
    }

    private void addPropertiesToDoorStateDevice(final Device device) {

        DefaultDeviceProperty<?> deviceProperty = null;

        // 0x24 = 36d event-state
        deviceProperty = new DefaultDeviceProperty<byte[]>("event-state", DeviceProperty.EVENT_STATE,
                new byte[] { (byte) 0x00 }, MessageType.ENUMERATED);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x4A = 74d number-of-states
        deviceProperty = new DefaultDeviceProperty<Integer>("number-of-states", DeviceProperty.NUMBER_OF_STATES,
                device.getStates().size(), MessageType.UNSIGNED_INTEGER);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x4B = 75d object-identifier
//      case 0x4B:
//          LOG.trace("<<< READ_PROP: object-identifier ({})", propertyIdentifierCode);
//          return processStringProperty(propertyIdentifierCode, requestMessage, objectType + ":" + id);
        final int objectIdentifier = ObjectIdentifierServiceParameter
                .encodeObjectTypeAndInstanceNumber(device.getObjectType(), device.getId());
        deviceProperty = new DefaultDeviceProperty<Integer>("object-identifier", DeviceProperty.OBJECT_IDENTIFIER,
                objectIdentifier, MessageType.BACNET_OBJECT_IDENTIFIER);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x4d = 77d object-name
//      case 0x4d:
//          LOG.trace("<<< READ_PROP: object-name ({})", propertyIdentifierCode);
//          return processObjectNameProperty(propertyIdentifierCode, requestMessage);
        deviceProperty = new DefaultDeviceProperty<String>("object-name", DeviceProperty.OBJECT_NAME, device.getName(),
                MessageType.CHARACTER_STRING);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x51 = 81d out-of-service
        deviceProperty = new DefaultDeviceProperty<Boolean>("out-of-service", DeviceProperty.OUT_OF_SERVICE,
                device.isOutOfService(), MessageType.BOOLEAN);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x55 = 85d present-value
//      case 0x55:
//          LOG.info("<<< READ_PROP: Device: {}, present-value ({})", name + ":" + objectType + ":" + id,
//                  propertyIdentifierCode);
//          return processPresentValueProperty(propertyIdentifierCode, requestMessage);
//        ((Boolean) device.getPresentValue()) ? 1 : 0
        deviceProperty = new DefaultDeviceProperty<Integer>("present-value", DeviceProperty.PRESENT_VALUE,
                ((Integer) device.getPresentValue()), MessageType.UNSIGNED_INTEGER);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x6F = 111d status-flags
//      // see bacnet_device25_object_list.pcapng - message 11702
//      case 0x6F:
//          LOG.trace("<<< READ_PROP: status-flags ({})", propertyIdentifierCode);
//          return processStatusFlagsProperty(propertyIdentifierCode, requestMessage);
        deviceProperty = new DefaultDeviceProperty<Integer>("status-flags", DeviceProperty.STATUS_FLAGS, 0x00,
                MessageType.UNSIGNED_INTEGER);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x6E = 110d state-text
//      case 0x6E:
//          LOG.trace("<<< READ_PROP: state-text ({})", propertyIdentifierCode);
//          return processStateTextProperty(propertyIdentifierCode, requestMessage);
        // 0x6E = 110d state-text
        // composite
        final CompositeDeviceProperty stateTextCompositeDeviceProperty = new CompositeDeviceProperty("state-text",
                DeviceProperty.STATE_TEXT, objectIdentifier, MessageType.UNSIGNED_INTEGER);
        DefaultDeviceProperty<String> subDeviceProperty = null;
        for (final String state : device.getStates()) {
            subDeviceProperty = new DefaultDeviceProperty<String>(state, DeviceProperty.STATE_TEXT,
                    "time switch active", MessageType.CHARACTER_STRING);
            stateTextCompositeDeviceProperty.getCompositeList().add(subDeviceProperty);
        }
        device.getProperties().put(stateTextCompositeDeviceProperty.getPropertyKey(), stateTextCompositeDeviceProperty);

        // 0x0173 = 371d property list
//      case 0x0173:
//          LOG.trace("<<< READ_PROP: property list ({})", propertyIdentifierCode);
//          return processPropertyListProperty(propertyIdentifierCode, requestMessage);
        deviceProperty = new DefaultDeviceProperty<Integer>("property-list", DeviceProperty.PROPERTY_LIST, 0,
                MessageType.UNSIGNED_INTEGER);
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

        // @formatter:off
        
        deviceProperty = new DefaultDeviceProperty<byte[]>(
                "object-type", 
                DeviceProperty.OBJECT_TYPE,
                new byte[] { (byte) device.getObjectType().getCode() }, 
                MessageType.ENUMERATED);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);
        
        // @formatter:on
    }

}
