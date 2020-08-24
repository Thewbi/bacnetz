package de.bacnetz.threads;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import de.bacnetz.common.utils.Utils;
import de.bacnetz.controller.DefaultMessageController;
import de.bacnetz.controller.Message;
import de.bacnetz.controller.MessageController;
import de.bacnetz.devices.DefaultDevice;
import de.bacnetz.devices.DefaultDeviceProperty;
import de.bacnetz.devices.Device;
import de.bacnetz.devices.DeviceProperty;
import de.bacnetz.devices.DevicePropertyType;
import de.bacnetz.devices.ObjectType;
import de.bacnetz.factory.MessageType;
import de.bacnetz.stack.ObjectIdentifierServiceParameter;
import de.bacnetz.stack.PDUType;
import de.bacnetz.stack.ServiceParameter;

public class MulticastListenerReaderThreadTest {

    private static final Logger LOG = LogManager.getLogger(MulticastListenerReaderThreadTest.class);

    /**
     * Unconfirmed request who-is with specific range defined by two service
     * parameters
     */
    @Test
    public void testParseBuffer() {

        final byte[] hexStringToByteArray = Utils.hexStringToByteArray("810B00120120FFFF00FF10080A1F471A1F47");

        final MulticastListenerReaderThread multicastListenerReaderThread = new MulticastListenerReaderThread();

        final Message request = multicastListenerReaderThread.parseBuffer(hexStringToByteArray,
                hexStringToByteArray.length);
        final List<Message> response = multicastListenerReaderThread.sendMessageToController(request);
    }

    @Test
    public void testParseBufferConfirmedCOVNotificiation() {

        final byte[] hexStringToByteArray = Utils.hexStringToByteArray("810A00090100200C01");

        final MulticastListenerReaderThread multicastListenerReaderThread = new MulticastListenerReaderThread();

        final Message request = multicastListenerReaderThread.parseBuffer(hexStringToByteArray,
                hexStringToByteArray.length);
        final List<Message> response = multicastListenerReaderThread.sendMessageToController(request);
    }

    /**
     * Unconfirmed request who-is without any ranges. No service parameters are
     * contained.
     */
    @Test
    public void testParseBuffer2() {

        final byte[] hexStringToByteArray = Utils.hexStringToByteArray("810B000801001008");

        final MulticastListenerReaderThread multicastListenerReaderThread = new MulticastListenerReaderThread();

        final Message request = multicastListenerReaderThread.parseBuffer(hexStringToByteArray,
                hexStringToByteArray.length);
        final List<Message> response = multicastListenerReaderThread.sendMessageToController(request);
    }

    /**
     * Unconfirmed request who-is without any ranges. No service parameters are
     * contained.
     */
    @Test
    public void testParseBuffer3() {

        final byte[] hexStringToByteArray = Utils.hexStringToByteArray("810B001401001000C4020027102201E0910021B2");

        final MulticastListenerReaderThread multicastListenerReaderThread = new MulticastListenerReaderThread();

        final Message request = multicastListenerReaderThread.parseBuffer(hexStringToByteArray,
                hexStringToByteArray.length);
        final List<Message> response = multicastListenerReaderThread.sendMessageToController(request);
    }

    @Test
    public void testIAM() {

        final byte[] hexStringToByteArray = Utils.hexStringToByteArray("810b001401001000c4020027102201e0910021b2");

        final MulticastListenerReaderThread multicastListenerReaderThread = new MulticastListenerReaderThread();

        final Message request = multicastListenerReaderThread.parseBuffer(hexStringToByteArray,
                hexStringToByteArray.length);
        final List<Message> response = multicastListenerReaderThread.sendMessageToController(request);
    }

    @Test
    public void testConfirmedREQ_ReadPropertyMultiple() {

        final byte[] hexStringToByteArray = Utils
                .hexStringToByteArray("810a0019010c012e030012680243990e0c020027101e09701f");

        final DefaultMessageController defaultMessageController = new DefaultMessageController();

        final MulticastListenerReaderThread multicastListenerReaderThread = new MulticastListenerReaderThread();
        multicastListenerReaderThread.getMessageControllers().add(defaultMessageController);

        final Message request = multicastListenerReaderThread.parseBuffer(hexStringToByteArray,
                hexStringToByteArray.length);
        final List<Message> response = multicastListenerReaderThread.sendMessageToController(request);
    }

//    @Test
//    public void testConfirmedREQ_ReadPropertyMultiple_2() {
//
//        final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
//        objectIdentifierServiceParameter.setInstanceNumber(10001);
//        objectIdentifierServiceParameter.setObjectType(ObjectType.DEVICE);
//
//        final Device device = mock(DefaultDevice.class);
//        when(device.findDevice(anyObject())).thenReturn(device);
//        when(device.getObjectIdentifierServiceParameter()).thenReturn(objectIdentifierServiceParameter);
//
//        final byte[] hexStringToByteArray = Utils
//                .hexStringToByteArray("810a001b01040245680e0c020027111e096b093e09a7090b090a1f");
//
//        final DefaultMessageController defaultMessageController = new DefaultMessageController();
//        defaultMessageController.getDevices().add(device);
//        defaultMessageController.getDeviceMap().put(device.getObjectIdentifierServiceParameter(), device);
//
//        final MulticastListenerReaderThread multicastListenerReaderThread = new MulticastListenerReaderThread();
//        multicastListenerReaderThread.getMessageControllers().add(defaultMessageController);
//
//        final Message request = multicastListenerReaderThread.parseBuffer(hexStringToByteArray,
//                hexStringToByteArray.length);
//        final List<Message> response = multicastListenerReaderThread.sendMessageToController(request);
//
//        LOG.info("Response: " + response);
//    }

    @Test
    public void testConfirmedREQ_WhoIs() {

        final byte[] hexStringToByteArray = Utils.hexStringToByteArray("810B00120120FFFF00FF10080A1F461A1F46");

        final DefaultMessageController defaultMessageController = new DefaultMessageController();

        final MulticastListenerReaderThread multicastListenerReaderThread = new MulticastListenerReaderThread();
        multicastListenerReaderThread.getMessageControllers().add(defaultMessageController);

        final Message request = multicastListenerReaderThread.parseBuffer(hexStringToByteArray,
                hexStringToByteArray.length);
        final List<Message> response = multicastListenerReaderThread.sendMessageToController(request);
    }

    @Test
    public void testConfirmedREQ_WhoIs2() {

        final byte[] hexStringToByteArray = Utils
                .hexStringToByteArray("810B00180128FFFF00012E03001268FE10080A27111A2711");

        final DefaultMessageController defaultMessageController = new DefaultMessageController();

        final MulticastListenerReaderThread multicastListenerReaderThread = new MulticastListenerReaderThread();
        multicastListenerReaderThread.getMessageControllers().add(defaultMessageController);

        final Message request = multicastListenerReaderThread.parseBuffer(hexStringToByteArray,
                hexStringToByteArray.length);
        final List<Message> response = multicastListenerReaderThread.sendMessageToController(request);
    }

    @Test
    public void testConfirmedREQ_WhoIs3() {

        final byte[] hexStringToByteArray = Utils
                .hexStringToByteArray("810A0017010C012E030012680245700C0C020027111961");

        final DefaultMessageController defaultMessageController = new DefaultMessageController();

        final MulticastListenerReaderThread multicastListenerReaderThread = new MulticastListenerReaderThread();
        multicastListenerReaderThread.getMessageControllers().add(defaultMessageController);

        final Message request = multicastListenerReaderThread.parseBuffer(hexStringToByteArray,
                hexStringToByteArray.length);
        final List<Message> response = multicastListenerReaderThread.sendMessageToController(request);
    }

    @Test
    public void testObjectList() {

        final byte[] hexStringToByteArray = Utils.hexStringToByteArray("810a0016012403e70119ff0245780c0c02000019194c");

        final DefaultMessageController defaultMessageController = new DefaultMessageController();

        final MulticastListenerReaderThread multicastListenerReaderThread = new MulticastListenerReaderThread();
        multicastListenerReaderThread.getMessageControllers().add(defaultMessageController);

        final Message request = multicastListenerReaderThread.parseBuffer(hexStringToByteArray,
                hexStringToByteArray.length);
        final List<Message> response = multicastListenerReaderThread.sendMessageToController(request);
    }

    @Test
    public void testDeserialize_PropertyList() {

        final byte[] hexStringToByteArray = Utils.hexStringToByteArray("810a001201040275590c0c020027111a0173");

        final DefaultMessageController defaultMessageController = new DefaultMessageController();

        final MulticastListenerReaderThread multicastListenerReaderThread = new MulticastListenerReaderThread();
        multicastListenerReaderThread.getMessageControllers().add(defaultMessageController);

        final Message request = multicastListenerReaderThread.parseBuffer(hexStringToByteArray,
                hexStringToByteArray.length);
//		final Message response = multicastListenerReaderThread.sendMessageToController(request);

        assertEquals(371, request.getApdu().getPropertyIdentifier());
    }

    @Test
    public void testDeserialize_PropertyList_2() {

        final byte[] hexStringToByteArray = Utils.hexStringToByteArray("810a001201040275540c0c00011a0173");

        final DefaultMessageController defaultMessageController = new DefaultMessageController();

        final MulticastListenerReaderThread multicastListenerReaderThread = new MulticastListenerReaderThread();
        multicastListenerReaderThread.getMessageControllers().add(defaultMessageController);

        final Message request = multicastListenerReaderThread.parseBuffer(hexStringToByteArray,
                hexStringToByteArray.length);
//		final Message response = multicastListenerReaderThread.sendMessageToController(request);

        assertEquals(371, request.getApdu().getPropertyIdentifier());
    }

    @Test
    public void testDeserialize_ReadPropertyMultiple() {

        final byte[] hexStringToByteArray = Utils.hexStringToByteArray("810a001301040275530e0c00c000011e09081f");

        final DefaultMessageController defaultMessageController = new DefaultMessageController();

        final MulticastListenerReaderThread multicastListenerReaderThread = new MulticastListenerReaderThread();
        multicastListenerReaderThread.getMessageControllers().add(defaultMessageController);

        final Message request = multicastListenerReaderThread.parseBuffer(hexStringToByteArray,
                hexStringToByteArray.length);
//		final Message response = multicastListenerReaderThread.sendMessageToController(request);

        assertEquals(0, request.getApdu().getPropertyIdentifier());
        assertEquals(3, request.getApdu().getServiceParameters().size());

        // opening tag
        final ServiceParameter openingTagServiceParameter = request.getApdu().getServiceParameters().get(0);
        assertEquals(6, openingTagServiceParameter.getLengthValueType());

        // property identifier
        final ServiceParameter propertyIdentifierServiceParameter = request.getApdu().getServiceParameters().get(1);
        final byte[] expected = new byte[] { 0x08 };
        assertTrue(Arrays.equals(propertyIdentifierServiceParameter.getPayload(), expected));

        // closing tag
        final ServiceParameter closeingTagServiceParameter = request.getApdu().getServiceParameters().get(2);
        assertEquals(7, closeingTagServiceParameter.getLengthValueType());
    }

    @Test
    public void testDeserialize_ReadPropertyMultiple2() {

        final byte[] hexStringToByteArray = Utils.hexStringToByteArray("810A0012010402757A0C0C020027111A0173");

        final DefaultMessageController defaultMessageController = new DefaultMessageController();

        final MulticastListenerReaderThread multicastListenerReaderThread = new MulticastListenerReaderThread();
        multicastListenerReaderThread.getMessageControllers().add(defaultMessageController);

        final Message request = multicastListenerReaderThread.parseBuffer(hexStringToByteArray,
                hexStringToByteArray.length);
//		final Message response = multicastListenerReaderThread.sendMessageToController(request);

        assertEquals(371, request.getApdu().getPropertyIdentifier());
        assertEquals(0, request.getApdu().getServiceParameters().size());

//		// opening tag
//		final ServiceParameter openingTagServiceParameter = request.getApdu().getServiceParameters().get(0);
//		assertEquals(6, openingTagServiceParameter.getLengthValueType());
//
//		// property identifier
//		final ServiceParameter propertyIdentifierServiceParameter = request.getApdu().getServiceParameters().get(1);
//		final byte[] expected = new byte[] { 0x08 };
//		assertTrue(Arrays.equals(propertyIdentifierServiceParameter.getPayload(), expected));
//
//		// closing tag
//		final ServiceParameter closeingTagServiceParameter = request.getApdu().getServiceParameters().get(2);
//		assertEquals(7, closeingTagServiceParameter.getLengthValueType());
    }

    @Test
    public void testParseBuffer_MalformedRestartNotificationRecipients() {

        final byte[] hexStringToByteArray = Utils
                .hexStringToByteArray("810a0022010c012e030012680215fa0f0c0200271119ca3e1e22012e630012681f3f");

        final MessageController messageController = new DefaultMessageController();

        final MulticastListenerReaderThread multicastListenerReaderThread = new MulticastListenerReaderThread();
        multicastListenerReaderThread.getMessageControllers().add(messageController);

        final Message request = multicastListenerReaderThread.parseBuffer(hexStringToByteArray,
                hexStringToByteArray.length);
        final List<Message> response = multicastListenerReaderThread.sendMessageToController(request);

        assertEquals(PDUType.SIMPLE_ACK_PDU, response.get(0).getApdu().getPduType());
    }

    @Test
    public void testParseBuffer_ServiceStatus() {

        final byte[] hexStringToByteArray = Utils
                .hexStringToByteArray("810a0017010c012e030012680215000c0c020027111970");

        final Device device = new DefaultDevice();
        device.setId(10001);
        device.setObjectType(ObjectType.DEVICE);

        final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
        objectIdentifierServiceParameter.setInstanceNumber(10001);
        objectIdentifierServiceParameter.setObjectType(ObjectType.DEVICE);

        final DefaultMessageController messageController = new DefaultMessageController();
        when(device.findDevice(anyObject())).thenReturn(device);
        when(device.getObjectIdentifierServiceParameter()).thenReturn(objectIdentifierServiceParameter);

        final MulticastListenerReaderThread multicastListenerReaderThread = new MulticastListenerReaderThread();
        multicastListenerReaderThread.getMessageControllers().add(messageController);

        final Message request = multicastListenerReaderThread.parseBuffer(hexStringToByteArray,
                hexStringToByteArray.length);
        final List<Message> response = multicastListenerReaderThread.sendMessageToController(request);

        LOG.info(response);
    }

    /**
     * See bacnet_active_cov_subscriptions_real_answer.pcapng - message no. 2694
     */
    @Test
    public void testParseBuffer_ReadPropertyMultiple_MultipleDevices() {

        // 810a004101040215610e0c00c000011e0955096f1f0c00c000021e0955096f1f0c00c000031e0955096f1f0c00c000041e0955096f1f0c04c000011e096f09551f

        final byte[] hexStringToByteArray = Utils.hexStringToByteArray(
                "810a0030012403e70119ff0233530e0c020000191e0955096f1f0c03c000321e0955096f1f0c04c000051e096f09551f");

        final Device device = new DefaultDevice();
        device.setId(25);
        device.setObjectType(ObjectType.DEVICE);

        final Device notificationClassDevice = new DefaultDevice();
        notificationClassDevice.setId(50);
        notificationClassDevice.setObjectType(ObjectType.NOTIFICATION_CLASS);
        device.getChildDevices().add(notificationClassDevice);

        final Device multiStateValueDevice = new DefaultDevice();
        multiStateValueDevice.setId(5);
        multiStateValueDevice.setObjectType(ObjectType.MULTI_STATE_VALUE);
        device.getChildDevices().add(multiStateValueDevice);

        // present value
        final DeviceProperty<Integer> presentValueDeviceProperty = new DefaultDeviceProperty<Integer>("present-value",
                DevicePropertyType.PRESENT_VALUE.getCode(), (Integer) device.getPresentValue(),
                MessageType.UNSIGNED_INTEGER);
        multiStateValueDevice.getProperties().put(DevicePropertyType.PRESENT_VALUE.getCode(),
                presentValueDeviceProperty);

        // 0x6F = 111d - status-flags
        final DeviceProperty<Integer> statusFlagsDeviceProperty = new DefaultDeviceProperty<Integer>("status-flags",
                DeviceProperty.STATUS_FLAGS, 0x00, MessageType.UNSIGNED_INTEGER);
        multiStateValueDevice.getProperties().put(DevicePropertyType.STATUS_FLAGS.getCode(), statusFlagsDeviceProperty);

        final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
        objectIdentifierServiceParameter.setInstanceNumber(10001);
        objectIdentifierServiceParameter.setObjectType(ObjectType.DEVICE);

        final DefaultMessageController messageController = new DefaultMessageController();
        when(device.findDevice(anyObject())).thenReturn(device);
        when(device.getObjectIdentifierServiceParameter()).thenReturn(objectIdentifierServiceParameter);

        final MulticastListenerReaderThread multicastListenerReaderThread = new MulticastListenerReaderThread();
        multicastListenerReaderThread.getMessageControllers().add(messageController);

        final Message request = multicastListenerReaderThread.parseBuffer(hexStringToByteArray,
                hexStringToByteArray.length);
        final List<Message> response = multicastListenerReaderThread.sendMessageToController(request);

        LOG.info(response);
    }

}
