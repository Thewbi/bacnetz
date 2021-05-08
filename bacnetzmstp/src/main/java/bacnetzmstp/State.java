package bacnetzmstp;

public enum State {

    IDLE,

    PREAMBLE,

    HEADER,

    HEADER_CRC,

    DATA,

    DATA_CRC

}
