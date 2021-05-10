package bacnetzmstp.messages;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

import bacnetzmstp.FrameType;
import bacnetzmstp.Header;
import de.bacnetz.controller.DefaultMessage;
import de.bacnetz.controller.Message;
import de.bacnetz.conversion.BACnetMSTPByteArrayToMessageConverter;
import de.bacnetz.devices.Device;

public class DefaultMessageListener implements MessageListener {

    private Message lastMessage;

    private OutputStream outputStream;

    private final Map<Integer, Device> masterDevices = new HashMap<>();

    @Override
    public void message(final Header header, final byte[] payloadBuffer, final int payloadDataRead) throws IOException {

        FrameType.BACNET_DATA_EXPECTING_REPLY.getNumVal();

        switch (header.getFrameType()) {
        case 0:
            System.out.println("Message received - TOKEN");
            break;

        case 1:
            final int deviceId = header.getDestinationAddress();
            System.out.println("Message received - POLL_FOR_MASTER - device id: " + deviceId);

            // find master with this id and send response
            if (masterDevices.containsKey(deviceId)) {
//                System.out.println("Message received - POLL_FOR_MASTER - device id: " + deviceId);

                final Device masterDevice = masterDevices.get(deviceId);

                final Header responseHeader = new Header();
                responseHeader.setFrameType(FrameType.REPLY_TO_POLL_FOR_MASTER.getNumVal());
                responseHeader.setDestinationAddress(header.getSourceAddress());
                responseHeader.setSourceAddress(masterDevice.getId());

                final byte reply[] = responseHeader.toBytes();

//                outputStream.write(reply);
            }
            break;

        case 2:
            System.out.println("Message received - REPLY_TO_POLL_FOR_MASTER");
            break;

        case 5:
        case 6:
//            System.out.println("Message received - BACNET_DATA_EXPECTING_REPLY");

            final DefaultMessage defaultMessage = new DefaultMessage();

            // parse message from byte buffer
            final BACnetMSTPByteArrayToMessageConverter converter = new BACnetMSTPByteArrayToMessageConverter();
            converter.setPayloadLength(payloadDataRead);
            converter.setPayloadOffset(0);
            converter.convert(payloadBuffer, defaultMessage);

            if (ArrayUtils.isNotEmpty(defaultMessage.getApdu().getPayload())) {
                // parse service parameters
                defaultMessage.getApdu().processPayload(defaultMessage.getApdu().getPayload(), 0,
                        defaultMessage.getApdu().getPayload().length, 0);
            }

            lastMessage = defaultMessage;
            break;

        default:
            System.out.println("Message received - UNKNOWN");
            break;
        }

    }

    @Override
    public Message getLastMessage() {
        return lastMessage;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    @Override
    public void setOutputStream(final OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public Map<Integer, Device> getMasterDevices() {
        return masterDevices;
    }

}
