package bacnetzmstp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import bacnetzmstp.messages.DefaultMessageListener;
import bacnetzmstp.messages.MessageListener;
import de.bacnetz.common.utils.Utils;
import de.bacnetz.controller.Message;
import de.bacnetz.controller.MessageController;
import de.bacnetz.devices.DevicePropertyType;
import de.bacnetz.devices.DeviceService;
import de.bacnetz.devices.ObjectType;
import de.bacnetz.mstp.Header;
import de.bacnetz.stack.APDU;
import de.bacnetz.stack.ConfirmedServiceChoice;
import de.bacnetz.stack.ErrorClass;
import de.bacnetz.stack.ErrorCode;
import de.bacnetz.stack.ObjectIdentifierServiceParameter;
import de.bacnetz.stack.ServiceParameter;
import de.bacnetz.stack.TagClass;

public class DefaultStateMachineTest {

    /**
     * Incomplete data, sequence does not start at preamble but in between messages
     * 64 (40) 169 (a9) 35 (23) 16 (10) 16 (10) 253 (fd) 85 (55) 255 (ff) 64 (40)
     * 169 (a9) 35 (23)
     * 
     * @throws IOException
     */
    @Test
    public void dataTest() throws IOException {

        final DefaultStateMachine stateMachine = new DefaultStateMachine();

        final byte[] hexStringToByteArray = Utils.hexStringToByteArray("40a9231010fd55ff40a923");

        for (final byte data : hexStringToByteArray) {
            stateMachine.input(data & 0xFF);
        }
    }

    /**
     * @throws IOException
     * 
     */
    @Test
    public void dataTest2() throws IOException {

        //
        // Arrange
        //
    	
    	final DeviceService deviceService = spy(DeviceService.class);

        final MessageListener messageListener = spy(DefaultMessageListener.class);
        messageListener.setDeviceService(deviceService);

        final DefaultStateMachine stateMachine = new DefaultStateMachine();
        stateMachine.setMessageListener(messageListener);

        // header.frametype = 64d = 0x40
        // header.destinationAddress = 0x28
        // header.sourceAddress = 0x04
        // header.length = 0x04 + 0x6a
        final String msg1 = "55ff0139190000e5";
        final String msg2 = "55ff020f520000c9";

        final byte[] hexStringToByteArray = Utils.hexStringToByteArray(msg1 + msg2);

        final ArgumentCaptor<Header> headerCaptor = ArgumentCaptor.forClass(Header.class);
        final ArgumentCaptor<byte[]> bufferCaptor = ArgumentCaptor.forClass(byte[].class);
        final ArgumentCaptor<Integer> bufferLengthCaptor = ArgumentCaptor.forClass(Integer.class);

        //
        // Act
        //

        for (final byte data : hexStringToByteArray) {
            stateMachine.input(data & 0xFF);
        }

        //
        // Assert
        //

        verify(messageListener, times(2)).message(headerCaptor.capture(), bufferCaptor.capture(),
                bufferLengthCaptor.capture());

        // reply-to-poll-for-master has no payload
        assertEquals(0, bufferLengthCaptor.getValue());

        final List<Header> headers = headerCaptor.getAllValues();

        Header header = headers.get(0);

        assertEquals(0x01, header.getFrameType());
        assertEquals(0x39, header.getDestinationAddress());
        assertEquals(0x19, header.getSourceAddress());
        assertEquals(0x00, header.getLength());
        assertEquals(0xe5, header.getCrc());

        header = headers.get(1);

        assertEquals(0x02, header.getFrameType());
        assertEquals(0x0f, header.getDestinationAddress());
        assertEquals(0x52, header.getSourceAddress());
        assertEquals(0x00, header.getLength());
        assertEquals(0xc9, header.getCrc());
    }

    /**
     * BACnet MS/TP - poll for master
     * 
     * https://store.chipkin.com/articles/how-does-bacnet-mstp-discover-new-devices-on-a-network
     * 
     * @throws IOException
     */
    @Test
    public void pollForMaster() throws IOException {

        //
        // Arrange
        //
    	
    	final DeviceService deviceService = spy(DeviceService.class);

        final MessageListener messageListener = spy(DefaultMessageListener.class);
        messageListener.setDeviceService(deviceService);

        final DefaultStateMachine stateMachine = new DefaultStateMachine();
        stateMachine.setMessageListener(messageListener);

        // header.frametype = 1d = 0x01 (Poll For Master)
        // header.destinationAddress = 57d = 0x39
        // header.sourceAddress = 26d = 0x19
        // header.length = 0x00 + 0x00
        // header.crc = 229d = 0xe5
        final String msg1 = "55ff0139190000e5"; // 0x55 0xff 0x01 0x39 0x19 0x00 0x00 0xe5

        final byte[] hexStringToByteArray = Utils.hexStringToByteArray(msg1);

        final ArgumentCaptor<Header> headerCaptor = ArgumentCaptor.forClass(Header.class);
        final ArgumentCaptor<byte[]> bufferCaptor = ArgumentCaptor.forClass(byte[].class);
        final ArgumentCaptor<Integer> bufferLengthCaptor = ArgumentCaptor.forClass(Integer.class);

        //
        // Act
        //

        for (final byte data : hexStringToByteArray) {
            stateMachine.input(data & 0xFF);
        }

        //
        // Assert
        //

        verify(messageListener).message(headerCaptor.capture(), bufferCaptor.capture(), bufferLengthCaptor.capture());

        // reply-to-poll-for-master has no payload
        assertEquals(0, bufferLengthCaptor.getValue());

        final Header header = headerCaptor.getValue();

        assertEquals(0x01, header.getFrameType());
        assertEquals(0x39, header.getDestinationAddress());
        assertEquals(0x19, header.getSourceAddress());
        assertEquals(0x00, header.getLength());
        assertEquals(0xe5, header.getCrc());
    }

    /**
     * BACnet MS/TP - reply to poll for master
     * 
     * https://store.chipkin.com/articles/how-does-bacnet-mstp-discover-new-devices-on-a-network
     * 
     * Specification 9.3 - MS/TP Frame Format
     * 
     * @throws IOException
     */
    @Test
    public void replyToPollForMaster() throws IOException {

        //
        // Arrange
        //
    	
    	final DeviceService deviceService = spy(DeviceService.class);

        final MessageListener messageListener = spy(DefaultMessageListener.class);
        messageListener.setDeviceService(deviceService);

        final DefaultStateMachine stateMachine = new DefaultStateMachine();
        stateMachine.setMessageListener(messageListener);

        // header.frametype = 2d = 0x02 (Reply To Poll For Master)
        // header.destinationAddress = 15d = 0x0f
        // header.sourceAddress = ?d = 0x52
        // header.length = 0x00 + 0x00
        // header.crc = ?d = 0xc9
        final String msg1 = "55ff020f520000c9"; // 0x55 0xff 0x02 0x0f 0x52 0x00 0x00 0xc9

        final byte[] hexStringToByteArray = Utils.hexStringToByteArray(msg1);

        final ArgumentCaptor<Header> headerCaptor = ArgumentCaptor.forClass(Header.class);
        final ArgumentCaptor<byte[]> bufferCaptor = ArgumentCaptor.forClass(byte[].class);
        final ArgumentCaptor<Integer> bufferLengthCaptor = ArgumentCaptor.forClass(Integer.class);

        //
        // Act
        //

        for (final byte data : hexStringToByteArray) {
            stateMachine.input(data & 0xFF);
        }

        //
        // Assert
        //

        verify(messageListener).message(headerCaptor.capture(), bufferCaptor.capture(), bufferLengthCaptor.capture());

        // reply-to-poll-for-master has no payload
        assertEquals(0, bufferLengthCaptor.getValue());

        final Header header = headerCaptor.getValue();

        assertEquals(0x02, header.getFrameType());
        assertEquals(0x0f, header.getDestinationAddress());
        assertEquals(0x52, header.getSourceAddress());
        assertEquals(0x00, header.getLength());
        assertEquals(0xc9, header.getCrc());

    }

    /**
     * BACnet MS/TP - token
     * 
     * https://store.chipkin.com/articles/how-does-bacnet-mstp-discover-new-devices-on-a-network
     * 
     * @throws IOException
     * 
     */
    @Test
    public void token() throws IOException {

        //
        // Arrange
        //
    	
    	final DeviceService deviceService = spy(DeviceService.class);

        final MessageListener messageListener = spy(DefaultMessageListener.class);
    	messageListener.setDeviceService(deviceService);

        final DefaultStateMachine stateMachine = new DefaultStateMachine();
        stateMachine.setMessageListener(messageListener);

        // header.frametype = 0d = 0x00 (Token)
        // header.destinationAddress = 15d = 0x0f
        // header.sourceAddress = ?d = 0x03
        // header.length = 0x00 + 0x00
        // header.crc = ?d = 0xaf
        final String msg1 = "55ff000f030000af";

        final byte[] hexStringToByteArray = Utils.hexStringToByteArray(msg1);

        final ArgumentCaptor<Header> headerCaptor = ArgumentCaptor.forClass(Header.class);
        final ArgumentCaptor<byte[]> bufferCaptor = ArgumentCaptor.forClass(byte[].class);
        final ArgumentCaptor<Integer> bufferLengthCaptor = ArgumentCaptor.forClass(Integer.class);

        //
        // Act
        //

        for (final byte data : hexStringToByteArray) {
            stateMachine.input(data & 0xFF);
        }

        //
        // Assert
        //

        verify(messageListener).message(headerCaptor.capture(), bufferCaptor.capture(), bufferLengthCaptor.capture());

        // reply-to-poll-for-master has no payload
        assertEquals(0, bufferLengthCaptor.getValue());

        final Header header = headerCaptor.getValue();

        assertEquals(0x00, header.getFrameType());
        assertEquals(0x0f, header.getDestinationAddress());
        assertEquals(0x03, header.getSourceAddress());
        assertEquals(0x00, header.getLength());
        assertEquals(0xaf, header.getCrc());
    }

    /**
     * BACnet MS/TP - read property (request)
     * 
     * The device receives a request for it's DESCRIPTION property.
     * 
     * https://store.chipkin.com/articles/how-does-bacnet-mstp-discover-new-devices-on-a-network
     * 
     * @throws IOException
     * 
     */
    @Test
    public void request_readProperty_description() throws IOException {

        //
        // Arrange
        //
    	
    	final DeviceService deviceService = spy(DeviceService.class);
    	
    	final MessageController messageController = spy(MessageController.class);

        final MessageListener messageListener = spy(DefaultMessageListener.class);
        messageListener.setDeviceService(deviceService);
        messageListener.setMessageController(messageController);

        final DefaultStateMachine stateMachine = new DefaultStateMachine();
        stateMachine.setMessageListener(messageListener);

        // header.frametype = 0d = 0x00 (Token)
        // header.destinationAddress = 15d = 0x0f
        // header.sourceAddress = ?d = 0x03
        // header.length = 0x00 + 0x00
        // header.crc = ?d = 0xaf
        final String headerHexStream = "55ff055203001685";
        final String npduHexStream = "010c037806ac105603bac0"; // length: 11 byte
        final String apduHexStream = "0272de0c0c02014ff2191c"; // length: 11 byte
        final String footerHexStream = "a92d";

        final byte[] hexStringToByteArray = Utils
                .hexStringToByteArray(headerHexStream + npduHexStream + apduHexStream + footerHexStream);

        final ArgumentCaptor<Header> headerCaptor = ArgumentCaptor.forClass(Header.class);
        final ArgumentCaptor<byte[]> bufferCaptor = ArgumentCaptor.forClass(byte[].class);
        final ArgumentCaptor<Integer> bufferLengthCaptor = ArgumentCaptor.forClass(Integer.class);

        //
        // Act
        //

        for (final byte data : hexStringToByteArray) {
            stateMachine.input(data & 0xFF);
        }

        //
        // Assert
        //

        verify(messageListener).message(headerCaptor.capture(), bufferCaptor.capture(), bufferLengthCaptor.capture());

        // 22 byte of payload (= npdu + apdu)
        assertEquals(22, bufferLengthCaptor.getValue());

        final Header header = headerCaptor.getValue();

        assertEquals(FrameType.BACNET_DATA_EXPECTING_REPLY.getNumVal(), header.getFrameType());
        assertEquals(0x52, header.getDestinationAddress());
        assertEquals(0x03, header.getSourceAddress());
        assertEquals(0x16, header.getLength());
        assertEquals(0x85, header.getCrc());

        final Message defaultMessage = messageListener.getLastMessage();

        // no virtual link control for BACnet MS/TP
        assertNull(defaultMessage.getVirtualLinkControl());

        // invoke is 222
        assertEquals(222, defaultMessage.getApdu().getInvokeId());

        // service choice is readProperty
        assertNull(defaultMessage.getApdu().getUnconfirmedServiceChoice());
        assertEquals(ConfirmedServiceChoice.READ_PROPERTY, defaultMessage.getApdu().getConfirmedServiceChoice());

        // object identifier (context tag)
        final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = defaultMessage.getApdu()
                .getFirstObjectIdentifierServiceParameter();
        // context tag number 0
        assertEquals(TagClass.CONTEXT_SPECIFIC_TAG, objectIdentifierServiceParameter.getTagClass());
        assertEquals(0, objectIdentifierServiceParameter.getTagNumber());
        assertEquals(86002, objectIdentifierServiceParameter.getInstanceNumber());
        assertEquals(ObjectType.DEVICE, objectIdentifierServiceParameter.getObjectType());

        // property identifier: 'object list' (context tag)
        final ServiceParameter serviceParameter = defaultMessage.getApdu().getServiceParameters().get(1);
        assertEquals(TagClass.CONTEXT_SPECIFIC_TAG, serviceParameter.getTagClass());
        // context tag number 1
        assertEquals(1, serviceParameter.getTagNumber());
        // the first byte of the payload stores the property identifier. 76d is
        // object-list
        assertEquals(DevicePropertyType.DESCRIPTION, DevicePropertyType.getByCode(serviceParameter.getPayload()[0]));
    }

    /**
     * BACnet MS/TP - read property (response), COMPLEX_ACK
     * 
     * The device receives a request for it's DESCRIPTION property.
     * 
     * https://store.chipkin.com/articles/how-does-bacnet-mstp-discover-new-devices-on-a-network
     * 
     * @throws IOException
     */
    @Test
    public void response_readProperty_description() throws IOException {

        //
        // Arrange
        //
    	
    	final DeviceService deviceService = spy(DeviceService.class);

        final MessageListener messageListener = spy(DefaultMessageListener.class);
        messageListener.setDeviceService(deviceService);

        final DefaultStateMachine stateMachine = new DefaultStateMachine();
        stateMachine.setMessageListener(messageListener);

        // header.frametype = 0d = 0x00 (Token)
        // header.destinationAddress = 15d = 0x0f
        // header.sourceAddress = ?d = 0x03
        // header.length = 0x00 + 0x00
        // header.crc = ?d = 0xaf

        final String headerHexStream = "55ff06035200345c";
        final String npduHexStream = "0120037806ac105603bac0ff";
        final String apduHexStream = "30de0c0c02014ff2191c3e751a00444c4d20526f6f6d204c69676874696e6720436f6e74726f6c3f";
        final String footerHexStream = "2733";

        final byte[] hexStringToByteArray = Utils
                .hexStringToByteArray(headerHexStream + npduHexStream + apduHexStream + footerHexStream);

        final ArgumentCaptor<Header> headerCaptor = ArgumentCaptor.forClass(Header.class);
        final ArgumentCaptor<byte[]> bufferCaptor = ArgumentCaptor.forClass(byte[].class);
        final ArgumentCaptor<Integer> bufferLengthCaptor = ArgumentCaptor.forClass(Integer.class);

        //
        // Act
        //

        for (final byte data : hexStringToByteArray) {
            stateMachine.input(data & 0xFF);
        }

        //
        // Assert
        //

        verify(messageListener).message(headerCaptor.capture(), bufferCaptor.capture(), bufferLengthCaptor.capture());

        // 22 byte of payload (= npdu + apdu)
        assertEquals(52, bufferLengthCaptor.getValue());

        final Header header = headerCaptor.getValue();

        assertEquals(FrameType.BACNET_DATA_NOT_EXPECTING_REPLY.getNumVal(), header.getFrameType());
        assertEquals(0x03, header.getDestinationAddress());
        assertEquals(0x52, header.getSourceAddress());
        assertEquals(0x34, header.getLength());
        assertEquals(0x5c, header.getCrc());

        final Message defaultMessage = messageListener.getLastMessage();

        // no virtual link control for BACnet MS/TP
        assertNull(defaultMessage.getVirtualLinkControl());

        // invoke is 222
        assertEquals(222, defaultMessage.getApdu().getInvokeId());

        // service choice is readProperty
        assertNull(defaultMessage.getApdu().getUnconfirmedServiceChoice());
        assertEquals(ConfirmedServiceChoice.READ_PROPERTY, defaultMessage.getApdu().getConfirmedServiceChoice());

        // object identifier (context tag)
        final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = defaultMessage.getApdu()
                .getFirstObjectIdentifierServiceParameter();
        // context tag number 0
        assertEquals(TagClass.CONTEXT_SPECIFIC_TAG, objectIdentifierServiceParameter.getTagClass());
        assertEquals(0, objectIdentifierServiceParameter.getTagNumber());
        assertEquals(86002, objectIdentifierServiceParameter.getInstanceNumber());
        assertEquals(ObjectType.DEVICE, objectIdentifierServiceParameter.getObjectType());

        // property identifier: 'object list' (context tag)
        ServiceParameter serviceParameter = defaultMessage.getApdu().getServiceParameters().get(1);
        assertEquals(TagClass.CONTEXT_SPECIFIC_TAG, serviceParameter.getTagClass());
        // context tag number 1
        assertEquals(1, serviceParameter.getTagNumber());
        // the first byte of the payload stores the property identifier. 76d is
        // object-list
        assertEquals(DevicePropertyType.DESCRIPTION, DevicePropertyType.getByCode(serviceParameter.getPayload()[0]));

        // {[3] opening-tag (context tag)
        serviceParameter = defaultMessage.getApdu().getServiceParameters().get(2);
        assertEquals(TagClass.CONTEXT_SPECIFIC_TAG, serviceParameter.getTagClass());
        // context tag number 3
        assertEquals(3, serviceParameter.getTagNumber());
        // opening tag
        assertEquals(ServiceParameter.OPENING_TAG_CODE, serviceParameter.getLengthValueType());

        // object-list (application tag)
        serviceParameter = defaultMessage.getApdu().getServiceParameters().get(3);
        assertEquals(TagClass.APPLICATION_TAG, serviceParameter.getTagClass());
        // application tag number has to be 7
        assertEquals(7, serviceParameter.getTagNumber());
        // string payload
        String payloadAsString = new String(serviceParameter.getPayload(), StandardCharsets.UTF_8);
        payloadAsString = StringUtils.trim(payloadAsString);

        final String expected = new String("DLM Room Lighting Control");
        assertTrue(expected.equalsIgnoreCase(payloadAsString));

        // [3]} closing-tag (context tag)
        serviceParameter = defaultMessage.getApdu().getServiceParameters().get(4);
        assertEquals(TagClass.CONTEXT_SPECIFIC_TAG, serviceParameter.getTagClass());
        // context tag number 3
        assertEquals(3, serviceParameter.getTagNumber());
        // closing tag
        assertEquals(ServiceParameter.CLOSING_TAG_CODE, serviceParameter.getLengthValueType());
    }

    /**
     * BACnet MS/TP - read property (request)
     * 
     * The device receives a request for it's OBJECT_NAME property.
     * 
     * https://store.chipkin.com/articles/how-does-bacnet-mstp-discover-new-devices-on-a-network
     * 
     * @throws IOException
     * 
     */
    @Test
    public void request_readProperty_objectname() throws IOException {

        //
        // Arrange
        //
    	
    	final DeviceService deviceService = spy(DeviceService.class);
    	
    	final MessageController messageController = spy(MessageController.class);

        final MessageListener messageListener = spy(DefaultMessageListener.class);
        messageListener.setDeviceService(deviceService);
        messageListener.setMessageController(messageController);

        final DefaultStateMachine stateMachine = new DefaultStateMachine();
        stateMachine.setMessageListener(messageListener);

        // header.frametype = 0d = 0x00 (Token)
        // header.destinationAddress = 15d = 0x0f
        // header.sourceAddress = ?d = 0x03
        // header.length = 0x00 + 0x00
        // header.crc = ?d = 0xaf
        final String headerHexStream = "55ff055203001685";
        final String npduHexStream = "010c037806ac105603bac0"; // length: 11 byte
        final String apduHexStream = "0272df0c0c02014ff2194d"; // length: 11 byte
        final String footerHexStream = "5823";

        final byte[] hexStringToByteArray = Utils
                .hexStringToByteArray(headerHexStream + npduHexStream + apduHexStream + footerHexStream);

        final ArgumentCaptor<Header> headerCaptor = ArgumentCaptor.forClass(Header.class);
        final ArgumentCaptor<byte[]> bufferCaptor = ArgumentCaptor.forClass(byte[].class);
        final ArgumentCaptor<Integer> bufferLengthCaptor = ArgumentCaptor.forClass(Integer.class);

        //
        // Act
        //

        for (final byte data : hexStringToByteArray) {
            stateMachine.input(data & 0xFF);
        }

        //
        // Assert
        //

        verify(messageListener).message(headerCaptor.capture(), bufferCaptor.capture(), bufferLengthCaptor.capture());

        // 22 byte of payload (= npdu + apdu)
        assertEquals(22, bufferLengthCaptor.getValue());

        final Header header = headerCaptor.getValue();

        assertEquals(FrameType.BACNET_DATA_EXPECTING_REPLY.getNumVal(), header.getFrameType());
        assertEquals(0x52, header.getDestinationAddress());
        assertEquals(0x03, header.getSourceAddress());
        assertEquals(0x16, header.getLength());
        assertEquals(0x85, header.getCrc());

        final Message defaultMessage = messageListener.getLastMessage();

        // no virtual link control for BACnet MS/TP
        assertNull(defaultMessage.getVirtualLinkControl());

        // invoke is 223
        assertEquals(223, defaultMessage.getApdu().getInvokeId());

        // service choice is readProperty
        assertNull(defaultMessage.getApdu().getUnconfirmedServiceChoice());
        assertEquals(ConfirmedServiceChoice.READ_PROPERTY, defaultMessage.getApdu().getConfirmedServiceChoice());

        // object identifier (context tag)
        final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = defaultMessage.getApdu()
                .getFirstObjectIdentifierServiceParameter();
        // context tag number 0
        assertEquals(TagClass.CONTEXT_SPECIFIC_TAG, objectIdentifierServiceParameter.getTagClass());
        assertEquals(0, objectIdentifierServiceParameter.getTagNumber());
        assertEquals(86002, objectIdentifierServiceParameter.getInstanceNumber());
        assertEquals(ObjectType.DEVICE, objectIdentifierServiceParameter.getObjectType());

        // property identifier: 'object list' (context tag)
        final ServiceParameter serviceParameter = defaultMessage.getApdu().getServiceParameters().get(1);
        assertEquals(TagClass.CONTEXT_SPECIFIC_TAG, serviceParameter.getTagClass());
        // context tag number 1
        assertEquals(1, serviceParameter.getTagNumber());
        // the first byte of the payload stores the property identifier. 76d is
        // object-list
        assertEquals(DevicePropertyType.OBJECT_NAME, DevicePropertyType.getByCode(serviceParameter.getPayload()[0]));
    }

    /**
     * BACnet MS/TP - read property (response), COMPLEX_ACK
     * 
     * The device receives a request for it's DESCRIPTION property.
     * 
     * https://store.chipkin.com/articles/how-does-bacnet-mstp-discover-new-devices-on-a-network
     * 
     * @throws IOException
     */
    @Test
    public void response_readProperty_objectname() throws IOException {

        //
        // Arrange
        //
    	
    	final DeviceService deviceService = spy(DeviceService.class);

        final MessageListener messageListener = spy(DefaultMessageListener.class);
        messageListener.setDeviceService(deviceService);

        final DefaultStateMachine stateMachine = new DefaultStateMachine();
        stateMachine.setMessageListener(messageListener);

        // header.frametype = 0d = 0x00 (Token)
        // header.destinationAddress = 15d = 0x0f
        // header.sourceAddress = ?d = 0x03
        // header.length = 0x00 + 0x00
        // header.crc = ?d = 0xaf

        final String headerHexStream = "55ff060352002ba8";
        final String npduHexStream = "0120037806ac105603bac0ff";
        final String apduHexStream = "30df0c0c02014ff2194d3e7511004c4d42432d31303020202020202020203f";
        final String footerHexStream = "e2d5";

        final byte[] hexStringToByteArray = Utils
                .hexStringToByteArray(headerHexStream + npduHexStream + apduHexStream + footerHexStream);

        final ArgumentCaptor<Header> headerCaptor = ArgumentCaptor.forClass(Header.class);
        final ArgumentCaptor<byte[]> bufferCaptor = ArgumentCaptor.forClass(byte[].class);
        final ArgumentCaptor<Integer> bufferLengthCaptor = ArgumentCaptor.forClass(Integer.class);

        //
        // Act
        //

        for (final byte data : hexStringToByteArray) {
            stateMachine.input(data & 0xFF);
        }

        //
        // Assert
        //

        verify(messageListener).message(headerCaptor.capture(), bufferCaptor.capture(), bufferLengthCaptor.capture());

        // 22 byte of payload (= npdu + apdu)
        assertEquals(43, bufferLengthCaptor.getValue());

        final Header header = headerCaptor.getValue();

        assertEquals(FrameType.BACNET_DATA_NOT_EXPECTING_REPLY.getNumVal(), header.getFrameType());
        assertEquals(0x03, header.getDestinationAddress());
        assertEquals(0x52, header.getSourceAddress());
        assertEquals(0x2B, header.getLength());
        assertEquals(0xa8, header.getCrc());

        final Message defaultMessage = messageListener.getLastMessage();

        // no virtual link control for BACnet MS/TP
        assertNull(defaultMessage.getVirtualLinkControl());

        // invoke is 223
        assertEquals(223, defaultMessage.getApdu().getInvokeId());

        // service choice is readProperty
        assertNull(defaultMessage.getApdu().getUnconfirmedServiceChoice());
        assertEquals(ConfirmedServiceChoice.READ_PROPERTY, defaultMessage.getApdu().getConfirmedServiceChoice());

        // object identifier (context tag)
        final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = defaultMessage.getApdu()
                .getFirstObjectIdentifierServiceParameter();
        // context tag number 0
        assertEquals(TagClass.CONTEXT_SPECIFIC_TAG, objectIdentifierServiceParameter.getTagClass());
        assertEquals(0, objectIdentifierServiceParameter.getTagNumber());
        assertEquals(86002, objectIdentifierServiceParameter.getInstanceNumber());
        assertEquals(ObjectType.DEVICE, objectIdentifierServiceParameter.getObjectType());

        // property identifier: 'object list' (context tag)
        ServiceParameter serviceParameter = defaultMessage.getApdu().getServiceParameters().get(1);
        assertEquals(TagClass.CONTEXT_SPECIFIC_TAG, serviceParameter.getTagClass());
        // context tag number 1
        assertEquals(1, serviceParameter.getTagNumber());
        // the first byte of the payload stores the property identifier. 76d is
        // object-list
        assertEquals(DevicePropertyType.OBJECT_NAME, DevicePropertyType.getByCode(serviceParameter.getPayload()[0]));

        // {[3] opening-tag (context tag)
        serviceParameter = defaultMessage.getApdu().getServiceParameters().get(2);
        assertEquals(TagClass.CONTEXT_SPECIFIC_TAG, serviceParameter.getTagClass());
        // context tag number 3
        assertEquals(3, serviceParameter.getTagNumber());
        // opening tag
        assertEquals(ServiceParameter.OPENING_TAG_CODE, serviceParameter.getLengthValueType());

        // object-list (application tag)
        serviceParameter = defaultMessage.getApdu().getServiceParameters().get(3);
        assertEquals(TagClass.APPLICATION_TAG, serviceParameter.getTagClass());
        // application tag number has to be 7
        assertEquals(7, serviceParameter.getTagNumber());
        // string payload
        String payloadAsString = new String(serviceParameter.getPayload(), StandardCharsets.UTF_8);
        payloadAsString = StringUtils.trim(payloadAsString);

        final String expected = new String("LMBC-100");
        assertTrue(expected.equalsIgnoreCase(payloadAsString));

        // [3]} closing-tag (context tag)
        serviceParameter = defaultMessage.getApdu().getServiceParameters().get(4);
        assertEquals(TagClass.CONTEXT_SPECIFIC_TAG, serviceParameter.getTagClass());
        // context tag number 3
        assertEquals(3, serviceParameter.getTagNumber());
        // closing tag
        assertEquals(ServiceParameter.CLOSING_TAG_CODE, serviceParameter.getLengthValueType());
    }

    /**
     * BACnet MS/TP - error (response)
     * 
     * The device receives a request for it's DESCRIPTION property.
     * 
     * https://store.chipkin.com/articles/how-does-bacnet-mstp-discover-new-devices-on-a-network
     * 
     * @throws IOException
     */
    @Test
    public void response_error() throws IOException {

        //
        // Arrange
        //
    	
    	final DeviceService deviceService = spy(DeviceService.class);

        final MessageListener messageListener = spy(DefaultMessageListener.class);
        messageListener.setDeviceService(deviceService);

        final DefaultStateMachine stateMachine = new DefaultStateMachine();
        stateMachine.setMessageListener(messageListener);

        final String headerHexStream = "55ff060352001341";
        final String npduHexStream = "0120037806ac105603bac0ff";
        final String apduHexStream = "50ef0c91029120";
        final String footerHexStream = "af94";

        final byte[] hexStringToByteArray = Utils
                .hexStringToByteArray(headerHexStream + npduHexStream + apduHexStream + footerHexStream);

        final ArgumentCaptor<Header> headerCaptor = ArgumentCaptor.forClass(Header.class);
        final ArgumentCaptor<byte[]> bufferCaptor = ArgumentCaptor.forClass(byte[].class);
        final ArgumentCaptor<Integer> bufferLengthCaptor = ArgumentCaptor.forClass(Integer.class);

        //
        // Act
        //

        for (final byte data : hexStringToByteArray) {
            stateMachine.input(data & 0xFF);
        }

        //
        // Assert
        //

        verify(messageListener).message(headerCaptor.capture(), bufferCaptor.capture(), bufferLengthCaptor.capture());

        // 22 byte of payload (= npdu + apdu)
        assertEquals(19, bufferLengthCaptor.getValue());

        final Header header = headerCaptor.getValue();

        assertEquals(FrameType.BACNET_DATA_NOT_EXPECTING_REPLY.getNumVal(), header.getFrameType());
        assertEquals(0x03, header.getDestinationAddress());
        assertEquals(0x52, header.getSourceAddress());
        assertEquals(19, header.getLength());
        assertEquals(0x41, header.getCrc());

        final Message defaultMessage = messageListener.getLastMessage();

        // no virtual link control for BACnet MS/TP
        assertNull(defaultMessage.getVirtualLinkControl());

        // check NPDU
        assertEquals(255, defaultMessage.getNpdu().getDestinationHopCount());
        assertEquals(888, defaultMessage.getNpdu().getDestinationNetworkAddress());
        assertEquals(6, defaultMessage.getNpdu().getDestinationMACLayerAddressLength());

        // invoke
        assertEquals(239, defaultMessage.getApdu().getInvokeId());

        // service choice is readProperty
        assertNull(defaultMessage.getApdu().getUnconfirmedServiceChoice());
        assertEquals(ConfirmedServiceChoice.READ_PROPERTY, defaultMessage.getApdu().getConfirmedServiceChoice());

        ServiceParameter serviceParameter = defaultMessage.getApdu().getServiceParameters().get(0);
        assertEquals(TagClass.APPLICATION_TAG, serviceParameter.getTagClass());
        // context tag number 9
        assertEquals(9, serviceParameter.getTagNumber());
        assertEquals(1, serviceParameter.getLengthValueType());
        // the first byte of the payload stores the property identifier. 76d is
        // object-list
        assertEquals(ErrorClass.PROPERTY, ErrorClass.getByCode(serviceParameter.getPayload()[0]));

        serviceParameter = defaultMessage.getApdu().getServiceParameters().get(1);
        assertEquals(TagClass.APPLICATION_TAG, serviceParameter.getTagClass());
        // context tag number 9
        assertEquals(9, serviceParameter.getTagNumber());
        assertEquals(1, serviceParameter.getLengthValueType());
        // the first byte of the payload stores the property identifier. 76d is
        // object-list
        assertEquals(ErrorCode.UNKNOWN_PROPERTY, ErrorCode.getByCode(serviceParameter.getPayload()[0]));
    }

    /**
     * BACnet MS/TP - read property (request)
     * 
     * The device receives a request for it's OBJECT_NAME property.
     * 
     * https://store.chipkin.com/articles/how-does-bacnet-mstp-discover-new-devices-on-a-network
     * 
     * @throws IOException
     * 
     */
    @Test
    public void request_readPropertyMultiple() throws IOException {

        //
        // Arrange
        //
    	
    	final DeviceService deviceService = spy(DeviceService.class);
    	
    	final MessageController messageController = spy(MessageController.class);

        final MessageListener messageListener = spy(DefaultMessageListener.class);
        messageListener.setDeviceService(deviceService);
        messageListener.setMessageController(messageController);

        final DefaultStateMachine stateMachine = new DefaultStateMachine();
        stateMachine.setMessageListener(messageListener);

        // header.frametype = 0d = 0x00 (Token)
        // header.destinationAddress = 15d = 0x0f
        // header.sourceAddress = ?d = 0x03
        // header.length = 0x00 + 0x00
        // header.crc = ?d = 0xaf
        final String headerHexStream = "55ff05510300866d";
        final String npduHexStream = "0104"; // length: 11 byte
        final String apduHexStream = "0203140e0c00c000201e095409510924096f0955094f094d094b1f0c00c000211e095409510924096f0955094f094d094b1f0c00c000291e095409510924096f0955094f094d094b1f0c00c0002a1e095409510924096f0955094f094d094b1f0c00c0002b1e095409510924096f0955094f094d094b1f0c00c0002c1e094f094d094b1f";
        final String footerHexStream = "9a0c";

        final byte[] hexStringToByteArray = Utils
                .hexStringToByteArray(headerHexStream + npduHexStream + apduHexStream + footerHexStream);

        final ArgumentCaptor<Header> headerCaptor = ArgumentCaptor.forClass(Header.class);
        final ArgumentCaptor<byte[]> bufferCaptor = ArgumentCaptor.forClass(byte[].class);
        final ArgumentCaptor<Integer> bufferLengthCaptor = ArgumentCaptor.forClass(Integer.class);

        //
        // Act
        //

        for (final byte data : hexStringToByteArray) {
            stateMachine.input(data & 0xFF);
        }

        //
        // Assert
        //

        verify(messageListener).message(headerCaptor.capture(), bufferCaptor.capture(), bufferLengthCaptor.capture());

        // byte of payload (= npdu + apdu)
        assertEquals(134, bufferLengthCaptor.getValue());

        final Header header = headerCaptor.getValue();

        assertEquals(FrameType.BACNET_DATA_EXPECTING_REPLY.getNumVal(), header.getFrameType());
        assertEquals(0x51, header.getDestinationAddress());
        assertEquals(0x03, header.getSourceAddress());
        assertEquals(134, header.getLength());
        assertEquals(109, header.getCrc());

        final Message defaultMessage = messageListener.getLastMessage();

        // no virtual link control for BACnet MS/TP
        assertNull(defaultMessage.getVirtualLinkControl());

        // invoke id
        assertEquals(20, defaultMessage.getApdu().getInvokeId());

        // service choice is readProperty
        assertNull(defaultMessage.getApdu().getUnconfirmedServiceChoice());
        assertEquals(ConfirmedServiceChoice.READ_PROPERTY_MULTIPLE,
                defaultMessage.getApdu().getConfirmedServiceChoice());

        // object identifier (context tag)
        final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = defaultMessage.getApdu()
                .getFirstObjectIdentifierServiceParameter();
        // context tag number 0
        assertEquals(TagClass.CONTEXT_SPECIFIC_TAG, objectIdentifierServiceParameter.getTagClass());
        assertEquals(0, objectIdentifierServiceParameter.getTagNumber());
        assertEquals(32, objectIdentifierServiceParameter.getInstanceNumber());
        assertEquals(ObjectType.BINARY_INPUT, objectIdentifierServiceParameter.getObjectType());

//        System.out.println(defaultMessage);

        // TODO verify all service parameters
        assertEquals(61, defaultMessage.getApdu().getServiceParameters().size());

//        System.out.println("done");
    }

    /**
     * BACnet MS/TP - error (response)
     * 
     * The device receives a request for it's DESCRIPTION property.
     * 
     * https://store.chipkin.com/articles/how-does-bacnet-mstp-discover-new-devices-on-a-network
     * 
     * @throws IOException
     */
    @Test
    public void response_readPropertyMultiple() throws IOException {

        //
        // Arrange
        //
    	
    	final DeviceService deviceService = spy(DeviceService.class);

        final MessageListener messageListener = spy(DefaultMessageListener.class);
        messageListener.setDeviceService(deviceService);

        final DefaultStateMachine stateMachine = new DefaultStateMachine();
        stateMachine.setMessageListener(messageListener);

        final String headerHexStream = "55ff06035101676d";
        final String npduHexStream = "0100";
        final String apduHexStream = "30140e0c00c000201e29544e91004f29514e104f29244e91004f296f4e8204004f29554e91004f294f4e91034f294d4e75060042492d33324f294b4ec400c000204f1f0c00c000211e29544e91004f29514e104f29244e91004f296f4e8204004f29554e91004f294f4e91034f294d4e75060042492d33334f294b4ec400c000214f1f0c00c000291e29544e91004f29514e104f29244e91004f296f4e8204004f29554e91004f294f4e91034f294d4e75060042492d34314f294b4ec400c000294f1f0c00c0002a1e29544e91004f29514e104f29244e91004f296f4e8204004f29554e91004f294f4e91034f294d4e75060042492d34324f294b4ec400c0002a4f1f0c00c0002b1e29544e91004f29514e104f29244e91004f296f4e8204004f29554e91004f294f4e91034f294d4e75060042492d34334f294b4ec400c0002b4f1f0c00c0002c1e294f4e91034f294d4e75060042492d34344f294b4ec400c0002c4f1f";
        final String footerHexStream = "ae3c";

        final byte[] hexStringToByteArray = Utils
                .hexStringToByteArray(headerHexStream + npduHexStream + apduHexStream + footerHexStream);

        final ArgumentCaptor<Header> headerCaptor = ArgumentCaptor.forClass(Header.class);
        final ArgumentCaptor<byte[]> bufferCaptor = ArgumentCaptor.forClass(byte[].class);
        final ArgumentCaptor<Integer> bufferLengthCaptor = ArgumentCaptor.forClass(Integer.class);

        //
        // Act
        //

        for (final byte data : hexStringToByteArray) {
            stateMachine.input(data & 0xFF);
        }

        //
        // Assert
        //

        verify(messageListener).message(headerCaptor.capture(), bufferCaptor.capture(), bufferLengthCaptor.capture());

        // 22 byte of payload (= npdu + apdu)
        assertEquals(359, bufferLengthCaptor.getValue());

        final Header header = headerCaptor.getValue();

        assertEquals(FrameType.BACNET_DATA_NOT_EXPECTING_REPLY.getNumVal(), header.getFrameType());
        assertEquals(0x03, header.getDestinationAddress());
        assertEquals(0x51, header.getSourceAddress());
        assertEquals(359, header.getLength());
        assertEquals(0x6d, header.getCrc());

        final Message defaultMessage = messageListener.getLastMessage();

        // no virtual link control for BACnet MS/TP
        assertNull(defaultMessage.getVirtualLinkControl());

        // check NPDU
        assertEquals(1, defaultMessage.getNpdu().getVersion());
        assertEquals(0, defaultMessage.getNpdu().getControl());

        // invoke id
        assertEquals(20, defaultMessage.getApdu().getInvokeId());

        // service choice is readProperty
        assertNull(defaultMessage.getApdu().getUnconfirmedServiceChoice());
        assertEquals(ConfirmedServiceChoice.READ_PROPERTY_MULTIPLE,
                defaultMessage.getApdu().getConfirmedServiceChoice());

        // object identifier (context tag)
        final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = defaultMessage.getApdu()
                .getFirstObjectIdentifierServiceParameter();
        // context tag number 0
        assertEquals(TagClass.CONTEXT_SPECIFIC_TAG, objectIdentifierServiceParameter.getTagClass());
        assertEquals(0, objectIdentifierServiceParameter.getTagNumber());
        assertEquals(32, objectIdentifierServiceParameter.getInstanceNumber());
        assertEquals(ObjectType.BINARY_INPUT, objectIdentifierServiceParameter.getObjectType());

        // TODO verify all service parameters
        assertEquals(190, defaultMessage.getApdu().getServiceParameters().size());

//        System.out.println(defaultMessage);
//        System.out.println("done");
    }

    /**
     * BACnet MS/TP - error (response)
     * 
     * The device receives a request for it's DESCRIPTION property.
     * 
     * https://store.chipkin.com/articles/how-does-bacnet-mstp-discover-new-devices-on-a-network
     * 
     * @throws IOException
     */
    @Test
    public void response_error_unknownProperty() throws IOException {

        //
        // Arrange
        //
    	
    	final DeviceService deviceService = spy(DeviceService.class);
    	
    	final MessageController messageController = spy(MessageController.class);

        final MessageListener messageListener = spy(DefaultMessageListener.class);
        messageListener.setDeviceService(deviceService);
        messageListener.setMessageController(messageController);

        final DefaultStateMachine stateMachine = new DefaultStateMachine();
        stateMachine.setMessageListener(messageListener);

        final String headerHexStream = "55FF050201000D11";
        final String npduHexStream = "0104";
        final String apduHexStream = "0273150C0C023FFFFF19D1";
        final String footerHexStream = "4408";

        final byte[] hexStringToByteArray = Utils
                .hexStringToByteArray(headerHexStream + npduHexStream + apduHexStream + footerHexStream);

        final ArgumentCaptor<Header> headerCaptor = ArgumentCaptor.forClass(Header.class);
        final ArgumentCaptor<byte[]> bufferCaptor = ArgumentCaptor.forClass(byte[].class);
        final ArgumentCaptor<Integer> bufferLengthCaptor = ArgumentCaptor.forClass(Integer.class);

        //
        // Act
        //

        for (final byte data : hexStringToByteArray) {
            stateMachine.input(data & 0xFF);
        }

        //
        // Assert
        //

        verify(messageListener).message(headerCaptor.capture(), bufferCaptor.capture(), bufferLengthCaptor.capture());

        // 22 byte of payload (= npdu + apdu)
        assertEquals(13, bufferLengthCaptor.getValue());

        final Header header = headerCaptor.getValue();

        assertEquals(FrameType.BACNET_DATA_EXPECTING_REPLY.getNumVal(), header.getFrameType());
        assertEquals(0x02, header.getDestinationAddress());
        assertEquals(0x01, header.getSourceAddress());
        assertEquals(13, header.getLength());
        assertEquals(0x11, header.getCrc());

        final Message defaultMessage = messageListener.getLastMessage();

        // no virtual link control for BACnet MS/TP
        assertNull(defaultMessage.getVirtualLinkControl());

        // check NPDU
        assertEquals(1, defaultMessage.getNpdu().getVersion());
        assertEquals(4, defaultMessage.getNpdu().getControl());

        // invoke id
        assertEquals(21, defaultMessage.getApdu().getInvokeId());

        // service choice is readProperty
        assertNull(defaultMessage.getApdu().getUnconfirmedServiceChoice());
        assertEquals(ConfirmedServiceChoice.READ_PROPERTY, defaultMessage.getApdu().getConfirmedServiceChoice());

//        // object identifier (context tag)
//        final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = defaultMessage.getApdu()
//                .getFirstObjectIdentifierServiceParameter();
//        // context tag number 0
//        assertEquals(TagClass.CONTEXT_SPECIFIC_TAG, objectIdentifierServiceParameter.getTagClass());
//        assertEquals(0, objectIdentifierServiceParameter.getTagNumber());
//        assertEquals(32, objectIdentifierServiceParameter.getInstanceNumber());
//        assertEquals(ObjectType.BINARY_INPUT, objectIdentifierServiceParameter.getObjectType());
//
//        // TODO verify all service parameters
//        assertEquals(190, defaultMessage.getApdu().getServiceParameters().size());

//        System.out.println(defaultMessage);
//        System.out.println("done");
    }

    @Test
    public void response_structured_object_list() throws IOException {

        //
        // Arrange
        //
    	
    	final DeviceService deviceService = spy(DeviceService.class);
    	
    	final MessageController messageController = spy(MessageController.class);

        final MessageListener messageListener = spy(DefaultMessageListener.class);
        messageListener.setDeviceService(deviceService);
        messageListener.setMessageController(messageController);

        final DefaultStateMachine stateMachine = new DefaultStateMachine();
        stateMachine.setMessageListener(messageListener);

//        Header: 55 FF 05 02 01 00 0D 11 
//        Payload: 01 04 02 73 0A 0C 0C 02 3F FF FF 19 4C 

        final String headerHexStream = "55FF050201000D11";
        final String npduHexStream = "0104";
        final String apduHexStream = "0273000C0C023FFFFF19D1";
        final String footerHexStream = "C885";

        final byte[] hexStringToByteArray = Utils
                .hexStringToByteArray(headerHexStream + npduHexStream + apduHexStream + footerHexStream);

        final ArgumentCaptor<Header> headerCaptor = ArgumentCaptor.forClass(Header.class);
        final ArgumentCaptor<byte[]> bufferCaptor = ArgumentCaptor.forClass(byte[].class);
        final ArgumentCaptor<Integer> bufferLengthCaptor = ArgumentCaptor.forClass(Integer.class);

        //
        // Act
        //

        for (final byte data : hexStringToByteArray) {
            stateMachine.input(data & 0xFF);
        }

        //
        // Assert
        //

        verify(messageListener).message(headerCaptor.capture(), bufferCaptor.capture(), bufferLengthCaptor.capture());

        // 22 byte of payload (= npdu + apdu)
        assertEquals(13, bufferLengthCaptor.getValue());

        final Header header = headerCaptor.getValue();

        assertEquals(FrameType.BACNET_DATA_EXPECTING_REPLY.getNumVal(), header.getFrameType());
        assertEquals(0x02, header.getDestinationAddress());
        assertEquals(0x01, header.getSourceAddress());
        assertEquals(13, header.getLength());
        assertEquals(0x11, header.getCrc());

        final Message defaultMessage = messageListener.getLastMessage();

        // no virtual link control for BACnet MS/TP
        assertNull(defaultMessage.getVirtualLinkControl());

        // check NPDU
        assertEquals(1, defaultMessage.getNpdu().getVersion());
        assertEquals(4, defaultMessage.getNpdu().getControl());

        // invoke id
        assertEquals(21, defaultMessage.getApdu().getInvokeId());

        // service choice is readProperty
        assertNull(defaultMessage.getApdu().getUnconfirmedServiceChoice());
        assertEquals(ConfirmedServiceChoice.READ_PROPERTY, defaultMessage.getApdu().getConfirmedServiceChoice());

//        // object identifier (context tag)
//        final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = defaultMessage.getApdu()
//                .getFirstObjectIdentifierServiceParameter();
//        // context tag number 0
//        assertEquals(TagClass.CONTEXT_SPECIFIC_TAG, objectIdentifierServiceParameter.getTagClass());
//        assertEquals(0, objectIdentifierServiceParameter.getTagNumber());
//        assertEquals(32, objectIdentifierServiceParameter.getInstanceNumber());
//        assertEquals(ObjectType.BINARY_INPUT, objectIdentifierServiceParameter.getObjectType());
//
//        // TODO verify all service parameters
//        assertEquals(190, defaultMessage.getApdu().getServiceParameters().size());

//        System.out.println(defaultMessage);
//        System.out.println("done");
    }

    @Test
    public void response_object_list() throws IOException {

        //
        // Arrange
        //
    	
    	final DeviceService deviceService = spy(DeviceService.class);
    	
    	final MessageController messageController = spy(MessageController.class);

        final MessageListener messageListener = spy(DefaultMessageListener.class);
        messageListener.setDeviceService(deviceService);
        messageListener.setMessageController(messageController);

        final DefaultStateMachine stateMachine = new DefaultStateMachine();
        stateMachine.setMessageListener(messageListener);

//        Header: 55 FF 05 02 01 00 0D 11 
//        Payload: 01 04 02 73 10 0C 0C 02 3F FF FF 19 4C

        final String headerHexStream = "55FF050201000D11";
        final String npduHexStream = "0104";
        final String apduHexStream = "0273130C0C023FFFFF194C";
        final String footerHexStream = "37E3";

        final byte[] hexStringToByteArray = Utils
                .hexStringToByteArray(headerHexStream + npduHexStream + apduHexStream + footerHexStream);

        final ArgumentCaptor<Header> headerCaptor = ArgumentCaptor.forClass(Header.class);
        final ArgumentCaptor<byte[]> bufferCaptor = ArgumentCaptor.forClass(byte[].class);
        final ArgumentCaptor<Integer> bufferLengthCaptor = ArgumentCaptor.forClass(Integer.class);

        //
        // Act
        //

        for (final byte data : hexStringToByteArray) {
            stateMachine.input(data & 0xFF);
        }

        //
        // Assert
        //

        verify(messageListener).message(headerCaptor.capture(), bufferCaptor.capture(), bufferLengthCaptor.capture());

        // 22 byte of payload (= npdu + apdu)
        assertEquals(13, bufferLengthCaptor.getValue());

        final Header header = headerCaptor.getValue();

        // output pattern so it is possible to check that the serialization will work as
        // expected
        System.out.println(Utils.bytesToHex(header.toBytes()));

        assertEquals(FrameType.BACNET_DATA_EXPECTING_REPLY.getNumVal(), header.getFrameType());
        assertEquals(0x02, header.getDestinationAddress());
        assertEquals(0x01, header.getSourceAddress());
        assertEquals(13, header.getLength());
        assertEquals(0x11, header.getCrc());

        final Message defaultMessage = messageListener.getLastMessage();

        // no virtual link control for BACnet MS/TP
        assertNull(defaultMessage.getVirtualLinkControl());

        // check NPDU
        assertEquals(1, defaultMessage.getNpdu().getVersion());
        assertEquals(4, defaultMessage.getNpdu().getControl());

        //
        // APDU
        //

        final APDU apdu = defaultMessage.getApdu();

        // invoke id
        assertEquals(19, apdu.getInvokeId());

        // service choice is readProperty
        assertNull(apdu.getUnconfirmedServiceChoice());
        assertEquals(ConfirmedServiceChoice.READ_PROPERTY, apdu.getConfirmedServiceChoice());

        // DEBUG
        System.out.println(defaultMessage.getNpdu().getStructureLength());
        System.out.println(Utils.bytesToHex(defaultMessage.getNpdu().getBytes()));

        // DEBUG
        System.out.println(apdu.getStructureLength());
        System.out.println(Utils.bytesToHex(apdu.getBytes()));

//        // object identifier (context tag)
//        final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = defaultMessage.getApdu()
//                .getFirstObjectIdentifierServiceParameter();
//        // context tag number 0
//        assertEquals(TagClass.CONTEXT_SPECIFIC_TAG, objectIdentifierServiceParameter.getTagClass());
//        assertEquals(0, objectIdentifierServiceParameter.getTagNumber());
//        assertEquals(32, objectIdentifierServiceParameter.getInstanceNumber());
//        assertEquals(ObjectType.BINARY_INPUT, objectIdentifierServiceParameter.getObjectType());
//
//        // TODO verify all service parameters
//        assertEquals(190, defaultMessage.getApdu().getServiceParameters().size());

//        System.out.println(defaultMessage);
//        System.out.println("done");
    }

}
