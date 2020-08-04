package de.bacnetz.devices;

import java.util.Map;

import de.bacnet.factory.Factory;
import de.bacnet.factory.MessageType;
import de.bacnetz.common.utils.NetworkUtils;
import de.bacnetz.stack.ObjectIdentifierServiceParameter;

public class DefaultDeviceFactory implements Factory<Device> {

	private static final int COLDSTART = // new byte[] { (byte) 0x01 },
			1;
	private static final int GEZE_GMBH = // new byte[] { (byte) 0x02, (byte) 0x1A },
			538;

	@SuppressWarnings("unchecked")
	@Override
	public Device create(final Object... args) {

		final Map<Integer, String> vendorMap = (Map<Integer, String>) args[0];

		return createIO420FourDoorSolution(vendorMap);
	}

	private Device createIO420FourDoorSolution(final Map<Integer, String> vendorMap) {

		final Device device = new DefaultDevice();
		device.setId(NetworkUtils.DEVICE_INSTANCE_NUMBER);
		device.setName(NetworkUtils.OBJECT_NAME);
		device.setVendorMap(vendorMap);
		device.setObjectType(ObjectIdentifierServiceParameter.OBJECT_TYPE_DEVICE);

		addPropertiesToDevice(device);

		return device;
	}

	private void addPropertiesToDevice(final Device device) {

		DefaultDeviceProperty deviceProperty = null;

		// 0x0C = 12d application-software-version
		// Values: 0x01 == version 1.0
		deviceProperty = new DefaultDeviceProperty<Integer>("application-software-version",
				DeviceProperty.APPLICATION_SOFTWARE_VERSION,
//				new byte[] { (byte) 0x01 }, 
				1, MessageType.UNSIGNED_INTEGER);
		device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

		// 0x1C = 28d description
		// TODO: why is the description not a string???
		deviceProperty = new DefaultDeviceProperty<Integer>("description", DeviceProperty.DESCRIPTION,
//				new byte[] { (byte) 0x01 }, 
				1, MessageType.UNSIGNED_INTEGER);

		// 0x70 = 112d - system status
		// 0x00 == operational
		final int systemStatus = 0x00;
		deviceProperty = new DefaultDeviceProperty<byte[]>("system-status", DeviceProperty.SYSTEM_STATUS,
				new byte[] { (byte) systemStatus }, MessageType.ENUMERATED);
		device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

		// 0x6B = 107d segmentation supported
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

		// Maximum APDU Length is dependent on the physical layer used, for example the
		// maximum APDU size for BACnet/IP is 1497 octets, but for BACnet MS/TP
		// segments, the maximum APDU size is only 480 octets.
		//
		// 1497d = 0x05D9
		// 0x3E = 62d
		deviceProperty = new DefaultDeviceProperty<Integer>("max-apdu-length-accepted",
				DeviceProperty.MAX_APDU_LENGTH_ACCEPTED,
//				new byte[] { (byte) 0x05, (byte) 0xD9 }, 
				1497, MessageType.UNSIGNED_INTEGER);
		device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

		// 0xA7 = 167d max-segments-accepted
		deviceProperty = new DefaultDeviceProperty<Integer>("max-segments-accepted",
				DeviceProperty.MAX_SEGMENTS_ACCEPTED,
//				new byte[] { (byte) 0x01 }, 
				1, MessageType.UNSIGNED_INTEGER);
		device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

		// 0x0A = 10d APDU-Segment-Timeout
		// APDU Segment-Timeout:
		// Dieser Wert in Millisekunden legt fest, nach welcher Zeitspanne ein
		// quittierpflichtiges, segmentiertes Telegramm als fehlgeschlagen gewertet
		// wird, wenn die Segmentbestätigung ausbleibt. Der Standardwert beträgt
		// 2000 Millisekunden.
		// 2000d == 0x07D0
		deviceProperty = new DefaultDeviceProperty<Integer>("apdu-segment-timeout", DeviceProperty.APDU_SEGMENT_TIMEOUT,
//				new byte[] { (byte) 0x07, (byte) 0xD0 }, 
				2000, MessageType.UNSIGNED_INTEGER);
		device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

		// 0x0B = 11d APDU-Timeout
		// ADPU Timeout:
		// Dieser Wert in Millisekunden legt fest, nach welcher Zeitspanne ein
		// quittierpflichtiges Telegramm als fehlgeschlagen gewertet wird, wenn die
		// Bestätigung ausbleibt. Der Standardwert beträgt 3000 ms.
		// 3000d == 0x0BB8
//		deviceProperty = new DefaultDeviceProperty("apdu-timeout", DeviceProperty.APDU_TIMEOUT,
//				new byte[] { (byte) 0x0B, (byte) 0xB8 }, MessageType.UNSIGNED_INTEGER);

		deviceProperty = new DefaultDeviceProperty<Integer>("apdu-timeout", DeviceProperty.APDU_TIMEOUT, 3000,
				MessageType.UNSIGNED_INTEGER);
		device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

		// 0x9B = 155d database-revision (155d = 0x9B) defined in ASHRAE on page 696
		// database revision 3
		deviceProperty = new DefaultDeviceProperty<Integer>("database-revision", DeviceProperty.DATABASE_REVISION,
//				new byte[] { (byte) 0x03 }, 
				3, MessageType.UNSIGNED_INTEGER);
		device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

		// 0x18 = 24d daylight-savings-status
		deviceProperty = new DefaultDeviceProperty<Boolean>("daylight-savings-status",
				DeviceProperty.DAYLIGHT_SAVINGS_STATUS,
//				new byte[] { (byte) 0x01 }, 
				true, MessageType.BOOLEAN);
		device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

		// 0x24 = 36d event-state
		deviceProperty = new DefaultDeviceProperty<byte[]>("event-state", DeviceProperty.EVENT_STATE,
				new byte[] { (byte) 0x00 }, MessageType.ENUMERATED);
		device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

		// 0x3F = 63d max-info-frames
		deviceProperty = new DefaultDeviceProperty<Integer>("max-info-frames", DeviceProperty.MAX_INFO_FRAMES,
//				new byte[] { (byte) 0x64 }, 
				100, MessageType.UNSIGNED_INTEGER);
		device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

		// 0x40 = 64d max-master
		deviceProperty = new DefaultDeviceProperty<Integer>("max-master", DeviceProperty.MAX_MASTER,
//				new byte[] { (byte) 0x7F },
				0x7F, MessageType.UNSIGNED_INTEGER);
		device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

		// 0x49 = 73d number-of-APDU-retries
		deviceProperty = new DefaultDeviceProperty<Integer>("number-of-APDU-retries",
				DeviceProperty.NUMBER_OF_APDU_RETRIES,
//				new byte[] { (byte) 0x10 }, 
				0x10, MessageType.UNSIGNED_INTEGER);
		device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

		// 0x4A = 74d number-of-states
		deviceProperty = new DefaultDeviceProperty<Integer>("number-of-states", DeviceProperty.NUMBER_OF_STATES,
//				new byte[] { (byte) 0x0E }, 
				0x0E, MessageType.UNSIGNED_INTEGER);
		device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

		// 0x4c = 76d (0x4c = 76d) object list
//      case 0x4c:
//          LOG.trace("<<< READ_PROP: object-list ({})", propertyIdentifierCode);
//          return processObjectListRequest(propertyIdentifierCode, requestMessage);
		deviceProperty = new DefaultDeviceProperty<Integer>("object-list", DeviceProperty.OBJECT_LIST, null,
				MessageType.UNSIGNED_INTEGER);
		device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

		// 0x4d = 77d (0x4d = 77d) object-name
//      case 0x4d:
//          LOG.trace("<<< READ_PROP: object-name ({})", propertyIdentifierCode);
//          return processObjectNameProperty(propertyIdentifierCode, requestMessage);
		deviceProperty = new DefaultDeviceProperty("object-name", DeviceProperty.OBJECT_NAME, device.getName(),
				MessageType.CHARACTER_STRING);
		device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

		// 0x51 = 81d out-of-service
		deviceProperty = new DefaultDeviceProperty<Boolean>("out-of-service", DeviceProperty.OUT_OF_SERVICE,
//				new byte[] { (byte) 0x00 }, 
				false, MessageType.BOOLEAN);
		device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

		// 0x62 = 98d protocol-version
		deviceProperty = new DefaultDeviceProperty<Integer>("protocol-version", DeviceProperty.PROTOCOL_VERSION,
//				new byte[] { (byte) 0x01 }, 
				1, MessageType.UNSIGNED_INTEGER);
		device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

		// 0x77 = 119d utc-offset
		deviceProperty = new DefaultDeviceProperty<byte[]>("utc-offset", DeviceProperty.UTC_OFFSET,
				new byte[] { (byte) 0xC4 }, MessageType.SINGED_INTEGER_TWOS_COMPLEMENT_NOTATION);
		device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

		// 0x78 = 120d vendor-identifier
		// 0x021A = 538 = GEZE GmbH
		deviceProperty = new DefaultDeviceProperty<Integer>("vendor-identifier", DeviceProperty.VENDOR_IDENTIFIER,
				GEZE_GMBH, MessageType.UNSIGNED_INTEGER);
		device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

//        // 0x79 = 121d vendor-name
//        // 0x021A = 538 = GEZE GmbH
//        deviceProperty = new DefaultDeviceProperty("vendor-name", DeviceProperty.VENDOR_NAME,
//                new byte[] { (byte) 0x02, (byte) 0x1A }, MessageType.UNSIGNED_INTEGER);
//        device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

		// 0x8B = 139d protocol-revision (0x8B = 139d)
		// the value of the protocol-revision property is set to 0x0C = 12d
		deviceProperty = new DefaultDeviceProperty<Integer>("protocol-revision", DeviceProperty.PROTOCOL_REVISION,
//				new byte[] { (byte) 0x0C }, 
				0x0C, MessageType.UNSIGNED_INTEGER);
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
//              new byte[] { (byte) 0x08 }, MessageType.ENUMERATED);
				new byte[] { (byte) device.getObjectType() }, MessageType.ENUMERATED);
		device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

		// 0x5F = 95d protocol-conformance-class
		deviceProperty = new DefaultDeviceProperty<Integer>("protocol-conformance-class",
				DeviceProperty.PROTOCOL_CONFORMANCE_CLASS,
//				new byte[] { (byte) 0x02 }, 
				0x02, MessageType.UNSIGNED_INTEGER);
		device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

		// 0x99 = 153d backup-failure-timeout
		deviceProperty = new DefaultDeviceProperty<Integer>("backup-failure-timeout",
				DeviceProperty.BACKUP_FAILURE_TIMEOUT,
//				new byte[] { (byte) 0x02, (byte) 0x1A }, 
				GEZE_GMBH, MessageType.UNSIGNED_INTEGER);
		device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

		// 0x9A = 154d configuration-files
		deviceProperty = new DefaultDeviceProperty<Integer>("configuration-files", DeviceProperty.CONFIGURATION_FILES,
//				new byte[] { (byte) 0x00 }, 
				0, MessageType.UNSIGNED_INTEGER);
		device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

		// 0xC3 = 195d interval-offset
		deviceProperty = new DefaultDeviceProperty<Integer>("interval-offset", DeviceProperty.INTERVALL_OFFSET,
//				new byte[] { (byte) 0x02, (byte) 0x1A }, 
				GEZE_GMBH, MessageType.UNSIGNED_INTEGER);
		device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

		// 0xC4 = 196d
		// last restart reason - 0x01 == coldstart
		deviceProperty = new DefaultDeviceProperty<byte[]>("last-restart-reason", DeviceProperty.LAST_RESTART_REASON,
				new byte[] { (byte) COLDSTART }, MessageType.ENUMERATED);
		device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

		// 0xCC = 204d interval-offset
		deviceProperty = new DefaultDeviceProperty<Integer>("time-synchronization-intervall",
				DeviceProperty.TIME_SYNCHRONIZATION_INTERVALL,
//				new byte[] { (byte) 0x02, (byte) 0x1A },
				GEZE_GMBH, MessageType.UNSIGNED_INTEGER);
		device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);

////      // 0xD1 = 209d structured object list
////      case 0xD1:
////          LOG.trace("<<< READ_PROP: structured object list ({})", propertyIdentifierCode);
////          return processStructuredObjectListProperty(propertyIdentifierCode, requestMessage);
//		deviceProperty = new DefaultDeviceProperty<Object>("structured-object-list",
//				DeviceProperty.STRUCTURED_OBJECT_LIST, null, MessageType.UNSIGNED_INTEGER);
//		device.getProperties().put(deviceProperty.getPropertyKey(), deviceProperty);
	}

}
