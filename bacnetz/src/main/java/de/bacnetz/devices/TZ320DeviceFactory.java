package de.bacnetz.devices;

import java.util.Map;

import de.bacnetz.factory.MessageType;
import de.bacnetz.stack.BACnetServicesSupportedBitString;
import de.bacnetz.stack.ObjectIdentifierServiceParameter;

public class TZ320DeviceFactory extends DefaultDeviceFactory {

    @SuppressWarnings("unchecked")
    @Override
    public Device create(final Object... args) {

        final Map<ObjectIdentifierServiceParameter, Device> deviceMap = (Map<ObjectIdentifierServiceParameter, Device>) args[0];
        final Map<Integer, String> vendorMap = (Map<Integer, String>) args[1];
        final int deviceId = (int) args[2];
        final String objectName = (String) args[3];
        final int vendorId = (int) args[4];

        return createTZ320(deviceMap, vendorMap, deviceId, objectName, vendorId);
    }

    private Device createTZ320(final Map<ObjectIdentifierServiceParameter, Device> deviceMap,
            final Map<Integer, String> vendorMap, final int deviceId, final String objectName, final int vendorId) {

        final DefaultDevice device = new DefaultDevice();
        device.setObjectType(ObjectType.DEVICE);
        device.setId(deviceId);
        device.setName(objectName);
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

        deviceMap.put(device.getObjectIdentifierServiceParameter(), device);

        return device;
    }

    private void addPropertiesToDevice(final DefaultDevice device) {
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

        // 0x60 = 96d - protocol-services-supported
        //
        // H.5.2.13 Protocol_Object_Types_Supported
        // This property indicates the BACnet protocol object types supported by this
        // device. See 12.10.15. The protocol object
        // types supported shall be at least Analog Input, Analog Output, Analog Value,
        // Binary Input, Binary Output, and Binary
        // Value.
        final BACnetServicesSupportedBitString retrieveServicesSupported = device.retrieveServicesSupported();
        deviceProperty = new DefaultDeviceProperty<String>("protocol-services-supported",
                DeviceProperty.PROTOCOL_SERVICES_SUPPORTED, retrieveServicesSupported.getStringValue(),
                MessageType.CHARACTER_STRING);
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
//      deviceProperty = new DefaultDeviceProperty("apdu-timeout", DeviceProperty.APDU_TIMEOUT,
//              new byte[] { (byte) 0x0B, (byte) 0xB8 }, MessageType.UNSIGNED_INTEGER);
        deviceProperty = new DefaultDeviceProperty<Integer>("apdu-timeout", DeviceProperty.APDU_TIMEOUT, 3000,
                MessageType.UNSIGNED_INTEGER);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x9B = 155d - database-revision (155d = 0x9B) defined in ASHRAE on page 696
        // database revision 3
        deviceProperty = new DefaultDeviceProperty<Integer>("database-revision", DeviceProperty.DATABASE_REVISION,
//              new byte[] { (byte) 0x03 }, 
                3, MessageType.UNSIGNED_INTEGER);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x18 = 24d - daylight-savings-status
        deviceProperty = new DefaultDeviceProperty<Boolean>("daylight-savings-status",
                DeviceProperty.DAYLIGHT_SAVINGS_STATUS,
//              new byte[] { (byte) 0x01 }, 
                true, MessageType.BOOLEAN);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x24 = 36d - event-state
        deviceProperty = new DefaultDeviceProperty<byte[]>("event-state", DeviceProperty.EVENT_STATE,
                new byte[] { (byte) 0x00 }, MessageType.ENUMERATED);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x3F = 63d - max-info-frames
        deviceProperty = new DefaultDeviceProperty<Integer>("max-info-frames", DeviceProperty.MAX_INFO_FRAMES,
//              new byte[] { (byte) 0x64 }, 
                100, MessageType.UNSIGNED_INTEGER);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x40 = 64d - max-master
        deviceProperty = new DefaultDeviceProperty<Integer>("max-master", DeviceProperty.MAX_MASTER,
//              new byte[] { (byte) 0x7F },
                0x7F, MessageType.UNSIGNED_INTEGER);
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

        // add your own object-identifier
        final DefaultDeviceProperty<Integer> objectIdentifierDeviceProperty = new DefaultDeviceProperty<>(
                DevicePropertyType.OBJECT_IDENTIFIER.getName(), DevicePropertyType.OBJECT_IDENTIFIER.getCode(),
                objectIdentifier, MessageType.BACNET_OBJECT_IDENTIFIER);
        objectListCompositeDeviceProperty.getCompositeList().add(objectIdentifierDeviceProperty);

        // add the object identifiers of all the children
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
        deviceProperty = new DefaultDeviceProperty<Boolean>("out-of-service", DeviceProperty.OUT_OF_SERVICE,
//              new byte[] { (byte) 0x00 }, 
                false, MessageType.BOOLEAN);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x62 = 98d protocol-version
        deviceProperty = new DefaultDeviceProperty<Integer>("protocol-version", DeviceProperty.PROTOCOL_VERSION,
//              new byte[] { (byte) 0x01 }, 
                1, MessageType.UNSIGNED_INTEGER);
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

//        // 0xA9 = 169d
//        deviceProperty = new DefaultDeviceProperty<Boolean>("auto-slave-discovery", DeviceProperty.AUTO_SLAVE_DISCOVERY,
//                false, MessageType.BOOLEAN);
//        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);
//
//        // 0xAC = 172d
//        deviceProperty = new DefaultDeviceProperty<Boolean>("slave-proxy-enable", DeviceProperty.SLAVE_PROXY_ENABLE,
//                false, MessageType.BOOLEAN);
//        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

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

//        // 0x5F = 95d protocol-conformance-class
//        deviceProperty = new DefaultDeviceProperty<Integer>("protocol-conformance-class",
//                DeviceProperty.PROTOCOL_CONFORMANCE_CLASS, 0x02, MessageType.UNSIGNED_INTEGER);
//        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);
//
//        // 0x99 = 153d backup-failure-timeout
//        deviceProperty = new DefaultDeviceProperty<Integer>("backup-failure-timeout",
//                DeviceProperty.BACKUP_FAILURE_TIMEOUT, 538, MessageType.UNSIGNED_INTEGER);
//        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);
//
//        // 0x9A = 154d configuration-files
//        deviceProperty = new DefaultDeviceProperty<Integer>("configuration-files", DeviceProperty.CONFIGURATION_FILES,
//                0, MessageType.UNSIGNED_INTEGER);
//        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);
//
//        // 0xC3 = 195d interval-offset
//        deviceProperty = new DefaultDeviceProperty<Integer>("interval-offset", DeviceProperty.INTERVALL_OFFSET, 538,
//                MessageType.UNSIGNED_INTEGER);
//        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

//        // 0xCC = 204d interval-offset
//        deviceProperty = new DefaultDeviceProperty<Integer>("time-synchronization-intervall",
//                DeviceProperty.TIME_SYNCHRONIZATION_INTERVALL, 538, MessageType.UNSIGNED_INTEGER);
//        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);
//
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
//        deviceProperty = new DefaultDeviceProperty<Integer>("property-list", DeviceProperty.PROPERTY_LIST, 0,
//                MessageType.UNSIGNED_INTEGER);
//        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0xC4 = 196d
        // last restart reason - 0x01 == coldstart
        deviceProperty = new DefaultDeviceProperty<byte[]>("last-restart-reason", DeviceProperty.LAST_RESTART_REASON,
                new byte[] { (byte) RestartReason.DETECTED_POWERED_OFF.getCode() }, MessageType.ENUMERATED);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0xCB = 203d - time-of-device-restart
        deviceProperty = new DefaultDeviceProperty<Integer>(DevicePropertyType.TIME_OF_DEVICE_RESTART.getName(),
                DevicePropertyType.TIME_OF_DEVICE_RESTART.getCode(), 1, MessageType.UNSIGNED_INTEGER);
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

    private void addChildrenToDevice(final DefaultDevice device, final Map<Integer, String> vendorMap) {

        Device childDevice = null;

        getModuleTypeChildDevice(device, vendorMap);

        // multi_state_value, 2
        childDevice = new DefaultDevice();
        childDevice.setParentDevice(device);
        childDevice.setObjectType(ObjectType.MULTI_STATE_VALUE);
        childDevice.setId(2);
        childDevice.setName(getAlarmTypeName());
        childDevice.setDescription("no entry");
        childDevice.setLocation("Office");
        childDevice.setVendorMap(vendorMap);
        childDevice.setMessageFactory(getMessageFactory());
        addPropertiesToAlarmStateDevice(childDevice);
        device.getChildDevices().add(childDevice);
        childDevice.setPresentValue(1);
        device.getDeviceMap().put(childDevice.getObjectIdentifierServiceParameter(), childDevice);

        // binary-input, 1
        childDevice = new BinaryInputDevice();
        childDevice.setParentDevice(device);
        childDevice.setObjectType(ObjectType.BINARY_INPUT);
        childDevice.setId(1);
        childDevice.setName("lock_state");
        childDevice.setDescription("no entry");
        childDevice.setLocation("Office");
        childDevice.setVendorMap(vendorMap);
        childDevice.setMessageFactory(getMessageFactory());
        addPropertiesToDoorLockStateDevice(childDevice);
        device.getChildDevices().add(childDevice);
        childDevice.setPresentValue(new byte[] { 1 });
        device.getDeviceMap().put(childDevice.getObjectIdentifierServiceParameter(), childDevice);

        // binary-input, 2
        childDevice = new BinaryInputDevice();
        childDevice.setParentDevice(device);
        childDevice.setObjectType(ObjectType.BINARY_INPUT);
        childDevice.setId(2);
        childDevice.setName("close_state");
        childDevice.setDescription("no entry");
        childDevice.setLocation("Office");
        childDevice.setVendorMap(vendorMap);
        childDevice.setMessageFactory(getMessageFactory());
        addPropertiesToDoorCloseStateDevice(childDevice);
        device.getChildDevices().add(childDevice);
        childDevice.setPresentValue(new byte[] { 1 });
        device.getDeviceMap().put(childDevice.getObjectIdentifierServiceParameter(), childDevice);

        // multi_state_value, 3
        childDevice = new DefaultDevice();
        childDevice.setParentDevice(device);
        childDevice.setObjectType(ObjectType.MULTI_STATE_VALUE);
        childDevice.setId(3);
        childDevice.setName(getStateName());
        childDevice.setDescription("no entry");
        childDevice.setLocation("Office");
        childDevice.setVendorMap(vendorMap);
        childDevice.setMessageFactory(getMessageFactory());
        addPropertiesToTZ320StateDevice(childDevice);
        device.getChildDevices().add(childDevice);
        childDevice.setPresentValue(1);
        device.getDeviceMap().put(childDevice.getObjectIdentifierServiceParameter(), childDevice);

        // multi_state_value, 4
        childDevice = new DefaultDevice();
        childDevice.setParentDevice(device);
        childDevice.setObjectType(ObjectType.MULTI_STATE_VALUE);
        childDevice.setId(4);
        childDevice.setName(getCommandName());
        childDevice.setDescription("no entry");
        childDevice.setLocation("Office");
        childDevice.setVendorMap(vendorMap);
        childDevice.setMessageFactory(getMessageFactory());
        addPropertiesToTZ320CommandDevice(childDevice);
        device.getChildDevices().add(childDevice);
        childDevice.setPresentValue(1);
        device.getDeviceMap().put(childDevice.getObjectIdentifierServiceParameter(), childDevice);

        // 7 (notification-class) (15, 50)
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
    }

    protected String getAlarmTypeName() {
        return "alarm_TZ320";
    }

    protected String getStateName() {
        return "TZ320_state";
    }

    protected String getCommandName() {
        return "TZ320_command";
    }

    protected void getModuleTypeChildDevice(final DefaultDevice device, final Map<Integer, String> vendorMap) {
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
        childDevice.setPresentValue(11);
        device.getDeviceMap().put(childDevice.getObjectIdentifierServiceParameter(), childDevice);
    }

    private void addPropertiesToDoorLockStateDevice(final Device device) {
        DefaultDeviceProperty<?> deviceProperty = null;

        // 0x4B = 75d - object-identifier
        final int objectIdentifier = ObjectIdentifierServiceParameter
                .encodeObjectTypeAndInstanceNumber(device.getObjectType(), device.getId());
        deviceProperty = new DefaultDeviceProperty<Integer>("object-identifier", DeviceProperty.OBJECT_IDENTIFIER,
                objectIdentifier, MessageType.BACNET_OBJECT_IDENTIFIER);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // elapsed-active-time
        deviceProperty = new DefaultDeviceProperty<Integer>(DevicePropertyType.ELAPSED_ACTIVE_TIME.getName(),
                DevicePropertyType.ELAPSED_ACTIVE_TIME.getCode(), 22015, MessageType.UNSIGNED_INTEGER);
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

    private void addPropertiesToTZ320CommandDevice(final Device device) {
        DefaultDeviceProperty<?> deviceProperty = null;

        // 0x24 = 36d event-state
        deviceProperty = new DefaultDeviceProperty<byte[]>("event-state", DeviceProperty.EVENT_STATE,
                new byte[] { (byte) 0x00 }, MessageType.ENUMERATED);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x4A = 74d number-of-states
        getCommandOptions(device);
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

    protected void getCommandOptions(final Device device) {
        device.getStates().add("no command");
        device.getStates().add("unlock");
        device.getStates().add("lock");
        device.getStates().add("short time release");
    }

    private void addPropertiesToTZ320StateDevice(final Device device) {
        DefaultDeviceProperty<?> deviceProperty = null;

        // 0x24 = 36d event-state
        deviceProperty = new DefaultDeviceProperty<byte[]>("event-state", DeviceProperty.EVENT_STATE,
                new byte[] { (byte) 0x00 }, MessageType.ENUMERATED);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x4A = 74d number-of-states
        getStateOptions(device);
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

    protected void getStateOptions(final Device device) {
        device.getStates().add("unlock");
        device.getStates().add("time switch active");
        device.getStates().add("pre-lock");
        device.getStates().add("lock");
        device.getStates().add("burglar-lock");
        device.getStates().add("short time released");
        device.getStates().add("service mode");
        device.getStates().add("alarm active");
        device.getStates().add("active sluice");
        device.getStates().add("passive sluice");
        device.getStates().add("sluice busy");
    }

    private void addPropertiesToAlarmStateDevice(final Device device) {
        DefaultDeviceProperty<?> deviceProperty = null;

        // 0x24 = 36d event-state
        deviceProperty = new DefaultDeviceProperty<byte[]>("event-state", DeviceProperty.EVENT_STATE,
                new byte[] { (byte) 0x00 }, MessageType.ENUMERATED);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // 0x4A = 74d number-of-states
        getAlarmTypeOptions(device);
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

    protected void getAlarmTypeOptions(final Device device) {
        device.getStates().add("no alarm");
        device.getStates().add("emergency open-TZ");
        device.getStates().add("hazard alarm-TZ");
        device.getStates().add("door alarm-TZ");
        device.getStates().add("sabotage-TZ");
        device.getStates().add("emergency open-TT");
        device.getStates().add("sabotage-TT");
        device.getStates().add("sabotage-KL");
        device.getStates().add("emergency open-BUS");
        device.getStates().add("can disturbed");
        device.getStates().add("relay fault-TZ");
        device.getStates().add("exit opener fault-TZ");
        device.getStates().add("Comm. KL disturbed");
        device.getStates().add("Comm. TT disturbed");
        device.getStates().add("RTC disturbed-TZ");
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

//    private void addPropertiesToDoorCommandStateDevice(final Device device) {
//
//        DefaultDeviceProperty<?> deviceProperty = null;
//
//        // 0x4B = 75d object-identifier
//        final int objectIdentifier = ObjectIdentifierServiceParameter
//                .encodeObjectTypeAndInstanceNumber(device.getObjectType(), device.getId());
//        deviceProperty = new DefaultDeviceProperty<Integer>("object-identifier", DeviceProperty.OBJECT_IDENTIFIER,
//                objectIdentifier, MessageType.BACNET_OBJECT_IDENTIFIER);
//        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);
//
//        // 0x4d = 77d object-name
//        deviceProperty = new DefaultDeviceProperty<String>("object-name", DeviceProperty.OBJECT_NAME, device.getName(),
//                MessageType.CHARACTER_STRING);
//        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);
//
//        // 0x4F = 79 object-type
//        //
//        // BACnetObjectType
//        // ENUMERATED:
//        // analog-input (0)
//        // analog-output (1)
//        // analog-value (2)
//        // binary-input (3)
//        // binary-output (4)
//        // binary-value (5)
//        // device (8)
//        // multi-state-input (13)
//        // multi-state-output (14)
//        // multi-state-value (19)
//        deviceProperty = new DefaultDeviceProperty<byte[]>("object-type", DeviceProperty.OBJECT_TYPE,
//                new byte[] { (byte) device.getObjectType().getCode() }, MessageType.ENUMERATED);
//        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);
//
//        // 0x1C = 28d description
//        deviceProperty = new DefaultDeviceProperty<String>("description", DeviceProperty.DESCRIPTION,
//                device.getDescription(), MessageType.CHARACTER_STRING);
//        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);
//
//        // 0x55 = 85d present-value
//        deviceProperty = new DefaultDeviceProperty<Integer>("present-value", DeviceProperty.PRESENT_VALUE,
//                (Integer) device.getPresentValue(), MessageType.UNSIGNED_INTEGER);
//        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);
//
//        // 0x6F = 111d status-flags
//        deviceProperty = new DefaultDeviceProperty<Integer>("status-flags", DeviceProperty.STATUS_FLAGS, 0x00,
//                MessageType.UNSIGNED_INTEGER);
//        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);
//
//        // 0x24 = 36d event-state
//        deviceProperty = new DefaultDeviceProperty<byte[]>("event-state", DeviceProperty.EVENT_STATE,
//                new byte[] { (byte) 0x00 }, MessageType.ENUMERATED);
//        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);
//
//        // 0x51 = 81d out-of-service
//        deviceProperty = new DefaultDeviceProperty<Boolean>("out-of-service", DeviceProperty.OUT_OF_SERVICE,
//                device.isOutOfService(), MessageType.BOOLEAN);
//        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);
//
//        // 0x4A = 74d number-of-states
//        device.getStates().add("unlock");
//        device.getStates().add("lock");
//        device.getStates().add("short time release");
//        deviceProperty = new DefaultDeviceProperty<Integer>("number-of-states", DeviceProperty.NUMBER_OF_STATES,
//                device.getStates().size(), MessageType.UNSIGNED_INTEGER);
//        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);
//
//        // 0x6E = 110d state-text
//        // composite
//        final CompositeDeviceProperty stateTextCompositeDeviceProperty = new CompositeDeviceProperty("state-text",
//                DeviceProperty.STATE_TEXT, objectIdentifier, MessageType.UNSIGNED_INTEGER);
//        DefaultDeviceProperty<String> subDeviceProperty = null;
//        for (final String state : device.getStates()) {
//            subDeviceProperty = new DefaultDeviceProperty<String>(state, DeviceProperty.STATE_TEXT, state,
//                    MessageType.CHARACTER_STRING);
//            stateTextCompositeDeviceProperty.getCompositeList().add(subDeviceProperty);
//        }
//        device.getProperties().put(stateTextCompositeDeviceProperty.getPropertyKey(), stateTextCompositeDeviceProperty);
//    }

    private void addPropertiesToDoorCloseStateDevice(final Device device) {

        DefaultDeviceProperty<?> deviceProperty = null;

        // 0x4B = 75d - object-identifier
        final int objectIdentifier = ObjectIdentifierServiceParameter
                .encodeObjectTypeAndInstanceNumber(device.getObjectType(), device.getId());
        deviceProperty = new DefaultDeviceProperty<Integer>("object-identifier", DeviceProperty.OBJECT_IDENTIFIER,
                objectIdentifier, MessageType.BACNET_OBJECT_IDENTIFIER);
        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

        // elapsed-active-time
        deviceProperty = new DefaultDeviceProperty<Integer>(DevicePropertyType.ELAPSED_ACTIVE_TIME.getName(),
                DevicePropertyType.ELAPSED_ACTIVE_TIME.getCode(), 22015, MessageType.UNSIGNED_INTEGER);
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

}
