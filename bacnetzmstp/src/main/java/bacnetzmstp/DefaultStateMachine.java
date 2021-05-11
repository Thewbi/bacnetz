package bacnetzmstp;

import java.io.IOException;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bacnetzmstp.messages.MessageListener;

public class DefaultStateMachine {

    private static final int PAYLOAD_BUFFER_LENGTH = 1024 * 5;

    private static final int PREAMBLE_1 = 0x55;

    private static final int PREAMBLE_2 = 0xFF;

    private static final Logger LOG = LogManager.getLogger(DefaultStateMachine.class);

    private State state = State.IDLE;

    private Header header = new Header();

    private int dataCRC1 = -1;

    private int dataCRC2 = -1;

    private MessageListener messageListener;

    private final byte[] payloadBuffer = new byte[PAYLOAD_BUFFER_LENGTH];

    private final byte[] headerBuffer = new byte[100];
    private int headerBufferIndex = 0;

    private int payloadDataRead = 0;

    public void input(final int data) throws IOException {

//        LOG.trace(data + " (" + Integer.toHexString(data) + ")");

        switch (state) {

        case IDLE:
            // preamble 1 -> PREAMBLE
            if (data == PREAMBLE_1) {
                state = State.PREAMBLE;
            }

            // DEBUG
            headerBuffer[headerBufferIndex] = (byte) data;
            headerBufferIndex++;
            break;

        case PREAMBLE:
            if (data == PREAMBLE_1) {
                // repeated preamble 1 --> PREAMBLE
                state = State.PREAMBLE;
            } else if (data == PREAMBLE_2) {
                // preamble 2 --> HEADER
                reset();
                state = State.HEADER;

                // DEBUG
                headerBuffer[headerBufferIndex] = (byte) data;
                headerBufferIndex++;
            } else {
                state = State.IDLE;
            }
            break;

        case HEADER:
            header.input(data);
            if (header.done()) {
                // go to next state
                state = State.HEADER_CRC;
            }

            // DEBUG
            headerBuffer[headerBufferIndex] = (byte) data;
            headerBufferIndex++;
            break;

        case HEADER_CRC:
            header.setCrc(data);

//            LOG.trace("New message header detected!");

//            // only frame type = "poll for master"
//            if (header.getFrameType() != 1) {
//                throw new RuntimeException("Unknown message");
////                backToIdle();
////                break;
//            }

            // TODO: check header CRC
            if (!header.checkCRC()) {
                LOG.warn("Invalid CRC!");
//                throw new RuntimeException(
//                        "Invalid header CRC " + Utils.bytesToHex(headerBuffer, 0, headerBufferIndex));
                backToIdle();
                return;
            }

//            System.out.println("BACnet MS/TP Poll For Master");

            // find next state
            if (header.getLength() == 0) {

                // send message to listener
                messageListener.message(header, payloadBuffer, payloadDataRead);

                // no data --> IDLE
                backToIdle();
            } else {
                state = State.DATA;
            }
            break;

        case DATA:
            if (payloadDataRead == header.getLength()) {
                dataCRC1 = data;
                state = State.DATA_CRC;
            } else {

                // DEBUG
//                LOG.trace(data + " (" + Integer.toHexString(data) + ")");

                // TODO consume data
                if (payloadDataRead == PAYLOAD_BUFFER_LENGTH) {
                    throw new RuntimeException("Buffer too small!");
                }
                payloadBuffer[payloadDataRead] = (byte) (data & 0xFF);

                payloadDataRead++;
            }
            break;

        case DATA_CRC:
            dataCRC2 = data;

//            LOG.trace(Utils.bytesToHex(payloadBuffer, 0, payloadDataRead + 2));

            // check data CRC
            final int crcValue = DataCRC.getCrc(payloadBuffer, payloadDataRead, 0);

            final int computedCRC1 = ((crcValue & 0xFF));
            final int computedCRC2 = ((crcValue & 0xFF00) >> 8);

            if ((computedCRC1 != dataCRC1) || (computedCRC2 != dataCRC2)) {
                LOG.warn("Invalid CRC!");
                // throw new RuntimeException("Invalid data CRC");
                backToIdle();
                return;
            }

//            final int crcValueSwitchedByteOrder = ((crcValue & 0xFF) << 8) + ((crcValue & 0xFF00) >> 8);
//            LOG.trace("0x" + Integer.toString(crcValue, 16) + " (" + crcValue + ")");
//            LOG.trace(
//                    "0x" + Integer.toString(crcValueSwitchedByteOrder, 16) + " (" + crcValueSwitchedByteOrder + ")");

            // send message to listener
            messageListener.message(header, payloadBuffer, payloadDataRead);

            backToIdle();

            break;

        }
    }

    private void backToIdle() {
        reset();
        state = State.IDLE;
    }

    private void reset() {
        header = new Header();
        dataCRC1 = -1;
        dataCRC2 = -1;
        payloadDataRead = 0;

        Arrays.fill(payloadBuffer, (byte) 0x00);

        // DEBUG
        headerBufferIndex = 0;
    }

    public State getState() {
        return state;
    }

    public void setState(final State state) {
        this.state = state;
    }

    public MessageListener getMessageListener() {
        return messageListener;
    }

    public void setMessageListener(final MessageListener messageListener) {
        this.messageListener = messageListener;
    }

}
