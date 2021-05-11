package bacnetzmstp.messages;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bacnetzmstp.DataCRC;
import bacnetzmstp.FrameType;
import bacnetzmstp.Header;
import de.bacnetz.common.utils.Utils;
import de.bacnetz.controller.DefaultMessage;
import de.bacnetz.controller.Message;
import de.bacnetz.conversion.BACnetMSTPByteArrayToMessageConverter;
import de.bacnetz.devices.Device;
import de.bacnetz.devices.ObjectType;
import de.bacnetz.factory.DefaultMessageFactory;

public class DefaultMessageListener implements MessageListener {

    private static final Logger LOG = LogManager.getLogger(DefaultMessageListener.class);

    private Message lastMessage;

    private OutputStream outputStream;

    private final Map<Integer, Device> masterDevices = new HashMap<>();

    private boolean onceOnly = false;

    @Override
    public void message(final Header header, final byte[] payloadBuffer, final int payloadDataRead) throws IOException {

        FrameType.BACNET_DATA_EXPECTING_REPLY.getNumVal();

        switch (header.getFrameType()) {

        // 0 is token
        case 0:
            LOG.info(">>> Message received - TOKEN - SA: " + header.getSourceAddress() + " DA: "
                    + header.getDestinationAddress());

            final int destinationDeviceId = header.getDestinationAddress();
//          LOG.trace("Message received - POLL_FOR_MASTER - device id: " + deviceId);

            // find master with this id and send response
            // if this simulator simulates this master, the master will answer
            if (masterDevices.containsKey(destinationDeviceId)) {
//              LOG.trace("Message received - POLL_FOR_MASTER - destination device id: " + destinationDeviceId);

                final Device masterDevice = masterDevices.get(destinationDeviceId);

                // send Object List request
                if (!onceOnly) {

                    LOG.info("<<< Sending message - object list to {}, deviceId {} from {}", header.getSourceAddress(),
                            25, masterDevice.getId());

                    final byte[] createObjectListRequest = createObjectListRequest(header.getSourceAddress(),
                            masterDevice.getId(), 25);
                    outputStream.write(createObjectListRequest);

                    onceOnly = true;
                }

                outputStream.write(passToken(header.getSourceAddress(), masterDevice.getId()));
            }
            break;

        // 1 is POLL_FOR_MASTER
        case 1:
            final int deviceId = header.getDestinationAddress();
//           LOG.trace(">>> Message received - POLL_FOR_MASTER - device id: " + deviceId);

            // find master with this id and send response
            if (masterDevices.containsKey(deviceId)) {
                LOG.trace(">>> Message received - POLL_FOR_MASTER - device id: " + deviceId);

                final Device masterDevice = masterDevices.get(deviceId);

                // send response - REPLY_TO_POLL_FOR_MASTER
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
            LOG.trace(">>> Message received - REPLY_TO_POLL_FOR_MASTER");
            break;

        // BACNET_DATA_EXPECTING_REPLY(5),
        // BACNET_DATA_NOT_EXPECTING_REPLY(6),
        case 5:
        case 6:
            LOG.trace(">>> Message received - BACNET_DATA_EXPECTING_REPLY");

            LOG.trace("Header: " + Utils.bytesToHex(header.toBytes()));
            LOG.trace("Payload: " + Utils.bytesToHex(payloadBuffer, 0, payloadDataRead));

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

            LOG.trace(defaultMessage);
            break;

        default:
            LOG.trace(">>> Message received - UNKNOWN");
            break;
        }

    }

    private byte[] createObjectListRequest(final int destinationAddress, final int sourceAddress, final int deviceId) {

        final Header header = new Header();
        header.setFrameType(FrameType.BACNET_DATA_EXPECTING_REPLY.getNumVal());
        header.setDestinationAddress(destinationAddress);
        header.setSourceAddress(sourceAddress);

        final DefaultMessageFactory defaultMessageFactory = new DefaultMessageFactory();
        final DefaultMessage requestObjectListMessage = (DefaultMessage) defaultMessageFactory
                .requestObjectList(ObjectType.DEVICE, deviceId);

        // no virtual link control for BACnet MS/TP
        requestObjectListMessage.setVirtualLinkControl(null);
//        requestObjectListMessage.getNpdu().setControl(0x04);
        requestObjectListMessage.getNpdu().setControl(0x00);
        requestObjectListMessage.getApdu().setInvokeId(0);
        requestObjectListMessage.getApdu().setSegmentationControl(0x73);

//      Header: 55 FF 05 02 01 00 0D 11 
//      Payload: 01 04 02 73 13 0C 0C 02 3F FF FF 19 4C 2f 91

//        final String headerHexStream = "55FF050201000D11";
//        final String npduHexStream = "0104";
//        final String apduHexStream = "0273130C0C023FFFFF194C";
//        final String footerHexStream = "37E3";

        final byte[] payloadBytes = requestObjectListMessage.getBytes();

        header.setLength1((payloadBytes.length & 0xFF00) >> 8);
        header.setLength2((payloadBytes.length & 0xFF));

        final byte[] headerBytes = header.toBytes();

        // header, with CRC
//        System.out.println(Utils.bytesToHex(headerBytes));

        // payload, without CRC
//        System.out.println(Utils.bytesToHex(payloadBytes));

        final int bufferSize = headerBytes.length + payloadBytes.length + 2;
        final byte[] resultBuffer = new byte[bufferSize];

        int length = 0;
        final int headerLength = header.toBytes(resultBuffer, length);
        length += headerLength;
        length += requestObjectListMessage.getNpdu().toBytes(resultBuffer, length);
        length += requestObjectListMessage.getApdu().toBytes(resultBuffer, length);

        final int dataCrc = DataCRC.getCrc(resultBuffer, length - headerLength, headerLength);

        // expected: 0x37E3 = 14307
//        System.out.println(dataCrc);

        resultBuffer[length] = (byte) (dataCrc & 0xFF);
        resultBuffer[length + 1] = (byte) ((dataCrc & 0xFF00) >> 8);

        return resultBuffer;
    }

    private byte[] passToken(final int destinationAddress, final int sourceAddress) {

        final Header responseHeader = new Header();
        responseHeader.setFrameType(FrameType.TOKEN.getNumVal());
        responseHeader.setDestinationAddress(destinationAddress);
        responseHeader.setSourceAddress(sourceAddress);
        responseHeader.setLength1(0x00);
        responseHeader.setLength2(0x00);

        final byte reply[] = responseHeader.toBytes();

        responseHeader.setCrc(reply[7]);

        return reply;
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
