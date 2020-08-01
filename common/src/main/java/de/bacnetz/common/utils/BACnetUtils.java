package de.bacnetz.common.utils;

public class BACnetUtils {

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

}
