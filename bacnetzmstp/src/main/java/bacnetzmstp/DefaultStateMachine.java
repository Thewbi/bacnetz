package bacnetzmstp;

public class DefaultStateMachine {

    private static final int PAYLOAD_BUFFER_LENGTH = 1024;

    private static final int PREAMBLE_1 = 0x55;

    private static final int PREAMBLE_2 = 0xFF;

    private State state = State.IDLE;

    private Header header = new Header();

//    private int dataRead = 0;

    private int dataCRC1 = -1;

    private int dataCRC2 = -1;

    private MessageListener messageListener;

    private final byte[] payloadBuffer = new byte[PAYLOAD_BUFFER_LENGTH];

    private int payloadDataRead = 0;

    public void input(final int data) {

//        System.out.println(data + " (" + Integer.toHexString(data) + ")");

        switch (state) {

        case IDLE:
            // preamble 1 -> PREAMBLE
            if (data == PREAMBLE_1) {
                state = State.PREAMBLE;
            }
            break;

        case PREAMBLE:
            if (data == PREAMBLE_1) {
                // repeated preamble 1 --> PREAMBLE
                state = State.PREAMBLE;
            } else if (data == PREAMBLE_2) {
                // preamble 2 --> HEADER
                reset();
                state = State.HEADER;
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
            break;

        case HEADER_CRC:
            header.setCrc(data);

//            System.out.println("New message header detected!");

//            // only frame type = "poll for master"
//            if (header.getFrameType() != 1) {
//                throw new RuntimeException("Unknown message");
////                backToIdle();
////                break;
//            }

            // TODO: check header CRC
            if (!header.checkCRC()) {
                throw new RuntimeException("Invalid header CRC");
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
                System.out.println(data + " (" + Integer.toHexString(data) + ")");

                // TODO: consume data
                if (payloadDataRead == PAYLOAD_BUFFER_LENGTH) {
                    throw new RuntimeException("Buffer too small!");
                }
                payloadBuffer[payloadDataRead] = (byte) (data & 0xFF);

                payloadDataRead++;
            }
            break;

        case DATA_CRC:
            dataCRC2 = data;

//            System.out.println(Utils.bytesToHex(payloadBuffer, 0, payloadDataRead + 2));

            // check data CRC
            final int crcValue = getCrc(payloadBuffer, payloadDataRead, 0);

            final int computedCRC1 = ((crcValue & 0xFF));
            final int computedCRC2 = ((crcValue & 0xFF00) >> 8);

            if ((computedCRC1 != dataCRC1) || (computedCRC2 != dataCRC2)) {
                throw new RuntimeException("Invalid data CRC");
            }

            final int crcValueSwitchedByteOrder = ((crcValue & 0xFF) << 8) + ((crcValue & 0xFF00) >> 8);

            System.out.println("0x" + Integer.toString(crcValue, 16) + " (" + crcValue + ")");
            System.out.println(
                    "0x" + Integer.toString(crcValueSwitchedByteOrder, 16) + " (" + crcValueSwitchedByteOrder + ")");

            // send message to listener
            messageListener.message(header, payloadBuffer, payloadDataRead);

            backToIdle();

            break;

        }
    }

    public int getCrc(final byte[] payloadBuffer, final int length, final int offset) {
        int value = 0xffff;
        for (int i = 0; i < length; i++) {
            final byte b = payloadBuffer[i + offset];
            value = calcDataCRC(b & 0xFF, value);
        }
        return onesComplement(value);
    }

    private static int calcDataCRC(final int dataValue, final int crcValue) {
        final int crcLow = (crcValue & 0xff) ^ dataValue; /* XOR C7..C0 with D7..D0 */
        /* Exclusive OR the terms in the table (top down) */
        final int crc = (crcValue >> 8) ^ (crcLow << 8) ^ (crcLow << 3) ^ (crcLow << 12) ^ (crcLow >> 4)
                ^ (crcLow & 0x0f) ^ ((crcLow & 0x0f) << 7);
        return crc & 0xffff;
    }

    private static int onesComplement(final int i) {
        return (~i) & 0xffff;
    }

    private void backToIdle() {
        System.out.println("Back to IDLE");

        reset();
        state = State.IDLE;
    }

    private void reset() {
        header = new Header();
//        header.reset();
        dataCRC1 = -1;
        dataCRC2 = -1;
        payloadDataRead = 0;
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
