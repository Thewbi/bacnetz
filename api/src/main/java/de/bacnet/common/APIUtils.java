package de.bacnet.common;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

public class APIUtils {

    /**
     * ctor
     */
    private APIUtils() {
        // no instances of this class
    }

    /**
     * Converts a LocalDate to a date. The time component is set to startOfDay which
     * is 00:00:00
     * 
     * @param localDate
     * @return
     */
    public static Date localDateToDate(final LocalDate localDate) {
        final ZoneId defaultZoneId = ZoneId.systemDefault();
        return Date.from(localDate.atStartOfDay(defaultZoneId).toInstant());
    }

    /**
     * Converts a date to a LocalDate (= date only) which is part of the update Java
     * date API.
     * 
     * @param date
     * @return
     */
    public static LocalDate dateToLocalDate(final Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Converts a date to a LocalDateTime (= date and time) which is part of the
     * update Java date API.
     * 
     * @param date
     * @return
     */
    public static LocalDateTime dateToLocalDateTime(final Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * Adds a short value into an array at the specified index. Advances the index
     * and returns the advanced value.
     * 
     * @param buffer
     * @param idx
     * @param data
     * @return
     */
    public static int addShortToBuffer(final byte[] buffer, int idx, final short data) {

        buffer[idx] = (byte) (0xFF & (data >> 8));
        idx++;
        buffer[idx] = (byte) (0xFF & (data));
        idx++;

        return idx;
    }

    /**
     * Converts to bytes to a unsigned short. As Java does not have unsigned types
     * the value is stored and returned in an int.
     * 
     * @param byte1
     * @param byte2
     * @param bigEndian
     * @return
     */
    public static int bytesToUnsignedShort(final byte byte1, final byte byte2, final boolean bigEndian) {
        if (bigEndian) {
            return (((byte1 & 0xFF) << 8) | (byte2 & 0xFF));
        }
        return (((byte2 & 0xFF) << 8) | (byte1 & 0xFF));
    }

    /**
     * Prints a byte array to a string of bytes in hexadecimal notation without 0x
     * as a prefix.
     * 
     * @param data
     * @return
     */
    public static String byteArrayToStringNoPrefix(final byte[] data) {

        if (data == null) {
            return StringUtils.EMPTY;
        }
        final StringBuffer stringBuffer = new StringBuffer();
        for (final byte tempByte : data) {
            stringBuffer.append(String.format("%1$02X", tempByte).toUpperCase(Locale.getDefault())).append(" ");
        }

        return stringBuffer.toString();
    }

    /**
     * Converts four consecutive bytes starting at idx from within the buffer to a
     * single integer value.
     * 
     * @param buffer
     * @param idx
     * @return
     */
    public static int bufferToInt(final byte[] buffer, final int idx) {
        return ((buffer[idx] & 0xff) << 24) + ((buffer[idx + 1] & 0xff) << 16) + ((buffer[idx + 2] & 0xff) << 8)
                + (buffer[idx + 3] & 0xff);
    }

    /**
     * Converts an integer to four bytes and inserts those into a buffer at offset.
     * 
     * @param data
     * @param buffer
     * @param offset
     */
    public static void intToBuffer(final int data, final byte[] buffer, final int offset) {

        final byte a = (byte) ((data >> 24) & 0xff);
        final byte b = (byte) ((data >> 16) & 0xff);
        final byte c = (byte) ((data >> 8) & 0xff);
        final byte d = (byte) ((data >> 0) & 0xff);

        buffer[offset + 0] = a;
        buffer[offset + 1] = b;
        buffer[offset + 2] = c;
        buffer[offset + 3] = d;
    }

    /**
     * Example:
     *
     * <pre>
     * final byte[] hexStringToByteArray = Utils.hexStringToByteArray(
     *         "360102001101000000c50102d84ce000170c00246d01d80a4b4e582049502042414f5320373737000000000000000000000000000000");
     * </pre>
     *
     * @param s
     * @return
     */
    public static byte[] hexStringToByteArray(final String s) {
        final int len = s.length();
        final byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
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

}
