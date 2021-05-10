package bacnetzmstp.messages;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

import bacnetzmstp.FrameType;
import bacnetzmstp.Header;
import de.bacnetz.common.utils.Utils;
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

        // 0 is token
        case 0:
//            System.out.println("Message received - TOKEN - SA: " + header.getSourceAddress() + " DA: "
//                    + header.getDestinationAddress());

            final int destinationDeviceId = header.getDestinationAddress();
//          System.out.println("Message received - POLL_FOR_MASTER - device id: " + deviceId);

            // find master with this id and send response
            if (masterDevices.containsKey(destinationDeviceId)) {
//                System.out
//                        .println("Message received - POLL_FOR_MASTER - destination device id: " + destinationDeviceId);

                final Device masterDevice = masterDevices.get(destinationDeviceId);

                final Header responseHeader = new Header();
                responseHeader.setFrameType(FrameType.TOKEN.getNumVal());
                responseHeader.setDestinationAddress(header.getSourceAddress());
                responseHeader.setSourceAddress(masterDevice.getId());
                responseHeader.setLength1(0x00);
                responseHeader.setLength2(0x00);

                final byte reply[] = responseHeader.toBytes();

                responseHeader.setCrc(reply[7]);

                outputStream.write(reply);
            }
            break;

        // 1 is POLL_FOR_MASTER
        case 1:
            final int deviceId = header.getDestinationAddress();
//            System.out.println("Message received - POLL_FOR_MASTER - device id: " + deviceId);

            // find master with this id and send response
            if (masterDevices.containsKey(deviceId)) {
                System.out.println("Message received - POLL_FOR_MASTER - device id: " + deviceId);

                final Device masterDevice = masterDevices.get(deviceId);

                final Header responseHeader = new Header();
                responseHeader.setFrameType(FrameType.REPLY_TO_POLL_FOR_MASTER.getNumVal());
                responseHeader.setDestinationAddress(header.getSourceAddress());
                responseHeader.setSourceAddress(masterDevice.getId());
                responseHeader.setLength1(0x00);
                responseHeader.setLength2(0x00);

                final byte reply[] = responseHeader.toBytes();

                responseHeader.setCrc(reply[7]);

                outputStream.write(reply);
            }
            break;

        // REPLY_TO_POLL_FOR_MASTER
        case 2:
            System.out.println("Message received - REPLY_TO_POLL_FOR_MASTER");
            break;

        // BACNET_DATA_EXPECTING_REPLY(5),
        // BACNET_DATA_NOT_EXPECTING_REPLY(6),
        case 5:
        case 6:
            System.out.println("Message received - BACNET_DATA_EXPECTING_REPLY");

            System.out.println("Header: " + Utils.bytesToHex(header.toBytes()));
            System.out.println("Payload: " + Utils.bytesToHex(payloadBuffer, 0, payloadDataRead));

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

            System.out.println(defaultMessage);
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
