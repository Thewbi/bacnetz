package bacnetzmstp;

/**
 * <pre>
 * 00 Token
 * 01 Poll For Master
 * 02 Reply To Poll For Master
 * 03 Test_Request
 * 04 Test_Response
 * 05 BACnet Data Expecting Reply
 * 06 BACnet Data Not Expecting Reply
 * 07 Reply Postponed
 * 32 BACnet Extended Data Expecting Reply
 * 33 BACnet Extended Data Not Expecting Reply
 * </pre>
 */
public enum FrameType {

    TOKEN(0),

    POLL_FOR_MASTER(1),

    REPLY_TO_POLL_FOR_MASTER(2),

    TEST_REQUEST(3),

    TEST_RESPONSE(4),

    BACNET_DATA_EXPECTING_REPLY(5),

    BACNET_DATA_NOT_EXPECTING_REPLY(6),

    REPLY_POSTPONED(7),

    BACNET_EXTENDED_DATA_EXPECTING_REPLY(32),

    BACNET_EXTENDED_DATA_NOT_EXPECTING_REPLY(33);

    private int numVal;

    FrameType(final int numVal) {
        this.numVal = numVal;
    }

    public int getNumVal() {
        return numVal;
    }

}
