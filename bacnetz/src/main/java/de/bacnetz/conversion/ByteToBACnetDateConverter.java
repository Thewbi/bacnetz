package de.bacnetz.conversion;

import de.bacnetz.stack.BACnetDate;

/**
 * 20.2.12 Encoding of a Date Value<br />
 * <br />
 * 
 * The encoding of a date value shall be primitive, with four contents
 * octets.<br />
 * <br />
 * Date values shall be encoded in the contents octets as four binary
 * integers.<br />
 * <br />
 * The first contents octet shall represent the year minus 1900;<br />
 * <br />
 * the second octet shall represent the month, with January = 1;<br />
 * <br />
 * the third octet shall represent the day of the month;<br />
 * <br />
 * and the fourth octet shall represent the day of the week, with Monday = 1.
 * <br />
 * <br />
 * 
 * A value of X'FF' = D'255' in any of the four octets shall indicate that the
 * corresponding value is unspecified and shall be considered a wildcard when
 * matching dates. <br />
 * <br />
 * 
 * If all four octets = X'FF', the corresponding date may be interpreted as
 * "any" or "don't care." <br />
 * <br />
 * 
 * Neither an unspecified date nor a date pattern shall be used in date values
 * that convey actual dates, such as in a TimeSynchronization-Request.
 */
public class ByteToBACnetDateConverter implements Converter<byte[], BACnetDate> {

    @Override
    public BACnetDate convert(final byte[] source) {

        final BACnetDate bacnetDate = new BACnetDate();
        convert(source, bacnetDate);

        return bacnetDate;
    }

    @Override
    public void convert(final byte[] source, final BACnetDate target) {

        int value = -1;

        value = source[0];
        if (value == 0xFF) {
            target.setYear(0xFF);
        } else {
            target.setYear(1900 + value);
        }
        target.setMonth(source[1]);
        target.setDayOfMonth(source[2]);
        target.setDayOfWeek(source[3]);
    }

}
