package bacnetzmstp;

public class DataCRC {

    private DataCRC() {
        // no instances of this class
    }

    public static int getCrc(final byte[] payloadBuffer, final int length, final int offset) {
        int value = 0xffff;
        for (int i = 0; i < length; i++) {
            final byte b = payloadBuffer[i + offset];

            // DEBUG
            System.out.println(Integer.toString(b & 0xFF, 16));

            value = calcDataCRC(b & 0xFF, value);
        }
        return onesComplement(value);
//        return value;
    }

    private static int calcDataCRC(final int dataValue, final int crcValue) {
        // XOR C7..C0 with D7..D0
        final int crcLow = (crcValue & 0xff) ^ dataValue;
        // Exclusive OR the terms in the table (top down)
        final int crc = (crcValue >> 8) ^ (crcLow << 8) ^ (crcLow << 3) ^ (crcLow << 12) ^ (crcLow >> 4)
                ^ (crcLow & 0x0f) ^ ((crcLow & 0x0f) << 7);
        return crc & 0xffff;
    }

    private static int onesComplement(final int i) {
        return (~i) & 0xffff;
    }

}
