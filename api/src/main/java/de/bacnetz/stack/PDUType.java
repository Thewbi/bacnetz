package de.bacnetz.stack;

public enum PDUType {

    /**
     * Value: 0x00. confirmed request, means the communication partner has to
     * acknowledge the request.
     */
    CONFIRMED_SERVICE_REQUEST_PDU(0x00),

    /** Value: 0x01 */
    UNCONFIRMED_SERVICE_REQUEST_PDU(0x01),

    /** Value: 0x02 */
    SIMPLE_ACK_PDU(0x02),

    /** Value: 0x03 */
    COMPLEX_ACK_PDU(0x03),

    /** Value: 0x04 */
    SEGMENT_ACK_PDU(0x04),

//    /** Value: 0x04 */
//    DEVICE_COMMUNICATION_CONTROL_PDU(0x04),

    /** Value: 0x05 */
    ERROR_PDU(0x05);

    public static final int CONFIRMED_SERVICE_REQUEST_PDU_CODE = 0x00;

    public static final int UNCONFIRMED_SERVICE_REQUEST_PDU_CODE = 0x01;

    public static final int SIMPLE_ACK_PDU_CODE = 0x02;

    public static final int COMPLEX_ACK_PDU_CODE = 0x03;

    public static final int SEGMENT_ACK_PDU_CODE = 0x04;

    public static final int ERROR_PDU_CODE = 0x05;

//    public static final int DEVICE_COMMUNICATION_CONTROL_PDU_CODE = 0x04;

    private final int id;

    private PDUType(final int id) {
        this.id = id;
    }

    public static PDUType fromInt(final int id) {

        switch (id) {

        case CONFIRMED_SERVICE_REQUEST_PDU_CODE:
            return CONFIRMED_SERVICE_REQUEST_PDU;

        case UNCONFIRMED_SERVICE_REQUEST_PDU_CODE:
            return UNCONFIRMED_SERVICE_REQUEST_PDU;

        case SIMPLE_ACK_PDU_CODE:
            return SIMPLE_ACK_PDU;

        case COMPLEX_ACK_PDU_CODE:
            return COMPLEX_ACK_PDU;

        case SEGMENT_ACK_PDU_CODE:
            return SEGMENT_ACK_PDU;

//        case DEVICE_COMMUNICATION_CONTROL_PDU_CODE:
//            return DEVICE_COMMUNICATION_CONTROL_PDU;

        case ERROR_PDU_CODE:
            return ERROR_PDU;

        default:
            throw new RuntimeException("Unknown id " + id);
        }
    }

    public int getId() {
        return id;
    }

}
