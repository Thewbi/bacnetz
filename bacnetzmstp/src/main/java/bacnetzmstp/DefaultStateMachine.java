package bacnetzmstp;

public class DefaultStateMachine {

    private static final int PREAMBLE_1 = 0x55;

    private static final int PREAMBLE_2 = 0xFF;

    private State state = State.IDLE;

    private final Header header = new Header();

    private int dataRead = 0;

    private int dataCRC1 = -1;

    private int dataCRC2 = -1;

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
                // no data --> IDLE
                backToIdle();
            } else {
                state = State.DATA;
            }
            break;

        case DATA:
            if (dataRead == header.getLength()) {
                dataCRC1 = data;
                state = State.DATA_CRC;
            } else {
                // TODO: consume data
                System.out.println(data + " (" + Integer.toHexString(data) + ")");
                dataRead++;
            }
            break;

        case DATA_CRC:
            dataCRC2 = data;
            // TODO: check data CRC
            backToIdle();

            break;

        }
    }

    private void backToIdle() {
        System.out.println("Back to IDLE");

        reset();
        state = State.IDLE;
    }

    private void reset() {
        header.reset();
        dataRead = 0;
        dataCRC1 = -1;
        dataCRC2 = -1;
    }

    public State getState() {
        return state;
    }

    public void setState(final State state) {
        this.state = state;
    }

}
