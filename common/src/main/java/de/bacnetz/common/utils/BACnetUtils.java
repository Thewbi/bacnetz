package de.bacnetz.common.utils;

public class BACnetUtils {

    /**
     * ctor
     */
    private BACnetUtils() {
        // no instances of this class
    }

    public static byte[] retrieveAsString(final String data) {

        final int dataLength = data.getBytes().length;

        // +1 for the leading zero (maybe this is the encoding code 0x00 for the
        // encoding ANSI X3.4 / UTF-8 (since 2010))
        final byte[] result = new byte[dataLength + 1];

        System.arraycopy(data.getBytes(), 0, result, 1, dataLength);

        // add a leading zero
        result[0] = 0;

        return result;
    }

    public static byte[] intToByteArray(final int data) {

        final byte byte0 = (byte) ((data & 0xFF000000) >> 24);
        final byte byte1 = (byte) ((data & 0x00FF0000) >> 16);
        final byte byte2 = (byte) ((data & 0x0000FF00) >> 8);
        final byte byte3 = (byte) ((data & 0x000000FF) >> 0);

        if (byte0 > 0) {
            final byte[] result = new byte[4];
            result[0] = byte0;
            result[1] = byte1;
            result[2] = byte2;
            result[3] = byte3;
            return result;
        }

        if (byte1 > 0) {
            final byte[] result = new byte[3];
            result[0] = byte1;
            result[1] = byte2;
            result[2] = byte3;
            return result;
        }

        if (byte2 > 0) {
            final byte[] result = new byte[2];
            result[0] = byte2;
            result[1] = byte3;
            return result;
        }

        return new byte[] { byte3 };
    }

}
