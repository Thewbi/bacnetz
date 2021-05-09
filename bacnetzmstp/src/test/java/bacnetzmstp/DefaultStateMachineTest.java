package bacnetzmstp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import de.bacnetz.common.utils.Utils;
import de.bacnetz.controller.Message;
import de.bacnetz.devices.DevicePropertyType;
import de.bacnetz.devices.ObjectType;
import de.bacnetz.stack.ConfirmedServiceChoice;
import de.bacnetz.stack.ObjectIdentifierServiceParameter;
import de.bacnetz.stack.ServiceParameter;
import de.bacnetz.stack.TagClass;

public class DefaultStateMachineTest {

    /**
     * Incomplete data, sequence does not start at preamble but in between messages
     * 64 (40) 169 (a9) 35 (23) 16 (10) 16 (10) 253 (fd) 85 (55) 255 (ff) 64 (40)
     * 169 (a9) 35 (23)
     */
    @Test
    public void dataTest() {

        final DefaultStateMachine stateMachine = new DefaultStateMachine();

        final byte[] hexStringToByteArray = Utils.hexStringToByteArray("40a9231010fd55ff40a923");

        for (final byte data : hexStringToByteArray) {
            stateMachine.input(data & 0xFF);
        }
    }

    /**
     * 
     */
    @Test
    public void dataTest2() {

        //
        // Arrange
        //

        final MessageListener messageListener = spy(DefaultMessageListener.class);

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
     */
    @Test
    public void pollForMaster() {

        //
        // Arrange
        //

        final MessageListener messageListener = spy(DefaultMessageListener.class);

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
     */
    @Test
    public void replyToPollForMaster() {

        //
        // Arrange
        //

        final MessageListener messageListener = spy(DefaultMessageListener.class);

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
     */
    @Test
    public void token() {

        //
        // Arrange
        //

        final MessageListener messageListener = spy(DefaultMessageListener.class);

        final DefaultStateMachine stateMachine = new DefaultStateMachine();
        stateMachine.setMessageListener(messageListener);

        // header.frametype = 0d = 0x00 (Token)
        // header.destinationAddress = 15d = 0x0f
        // header.sourceAddress = ?d = 0x03
        // header.length = 0x00 + 0x00
        // header.crc = ?d = 0xaf
        final String msg1 = "55ff000f030000af"; // 0x55 0xff 0x02 0x0f 0x52 0x00 0x00 0xc9

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
     */
    @Test
    public void readProperty() {

        //
        // Arrange
        //

        final MessageListener messageListener = spy(DefaultMessageListener.class);

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

//        // property array index (context tag)
//        serviceParameter = defaultMessage.getApdu().getServiceParameters().get(2);
//        assertEquals(TagClass.CONTEXT_SPECIFIC_TAG, serviceParameter.getTagClass());
//        // context tag number 2
//        assertEquals(2, serviceParameter.getTagNumber());
//        // first byte of the payload stores the requested array index which in this
//        // example has to be zero
//        // requesting the array index 0 means, requesting the length of the object
//        // array! BACnet wierdness galore.
//        assertEquals(0, serviceParameter.getPayload()[0]);
//
//        // {[3] opening-tag (context tag)
//        serviceParameter = defaultMessage.getApdu().getServiceParameters().get(3);
//        assertEquals(TagClass.CONTEXT_SPECIFIC_TAG, serviceParameter.getTagClass());
//        // context tag number 3
//        assertEquals(3, serviceParameter.getTagNumber());
//        // opening tag
//        assertEquals(ServiceParameter.OPENING_TAG_CODE, serviceParameter.getLengthValueType());
//
//        // object-list (application tag)
//        serviceParameter = defaultMessage.getApdu().getServiceParameters().get(4);
//        assertEquals(TagClass.APPLICATION_TAG, serviceParameter.getTagClass());
//        // application tag number has to be 2
//        assertEquals(2, serviceParameter.getTagNumber());
//        // the payload contains the value 3, which is the length of the object list in
//        // this example
//        assertEquals(3, serviceParameter.getPayload()[0]);
//
//        // [3]} closing-tag (context tag)
//        serviceParameter = defaultMessage.getApdu().getServiceParameters().get(5);
//        assertEquals(TagClass.CONTEXT_SPECIFIC_TAG, serviceParameter.getTagClass());
//        // context tag number 3
//        assertEquals(3, serviceParameter.getTagNumber());
//        // closing tag
//        assertEquals(ServiceParameter.CLOSING_TAG_CODE, serviceParameter.getLengthValueType());
    }
}
