package bacnetzmstp;

import de.bacnetz.controller.DefaultMessage;
import de.bacnetz.controller.Message;
import de.bacnetz.conversion.BACnetMSTPByteArrayToMessageConverter;

public class DefaultMessageListener implements MessageListener {

    private Message lastMessage;

    @Override
    public void message(final Header header, final byte[] payloadBuffer, final int payloadDataRead) {

        FrameType.BACNET_DATA_EXPECTING_REPLY.getNumVal();

        switch (header.getFrameType()) {
        case 0:
            System.out.println("Message received - TOKEN");
            break;

        case 1:
            System.out.println("Message received - POLL_FOR_MASTER");
            break;

        case 2:
            System.out.println("Message received - REPLY_TO_POLL_FOR_MASTER");
            break;

        case 5:
            System.out.println("Message received - BACNET_DATA_EXPECTING_REPLY");

            final DefaultMessage defaultMessage = new DefaultMessage();

            // parse message from byte buffer
            final BACnetMSTPByteArrayToMessageConverter converter = new BACnetMSTPByteArrayToMessageConverter();
            converter.setPayloadLength(payloadDataRead);
            converter.setPayloadOffset(0);
            converter.convert(payloadBuffer, defaultMessage);

            // parse service parameters
            defaultMessage.getApdu().processPayload(defaultMessage.getApdu().getPayload(), 0,
                    defaultMessage.getApdu().getPayload().length, 0);

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

}
