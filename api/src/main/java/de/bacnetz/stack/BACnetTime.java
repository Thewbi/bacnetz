package de.bacnetz.stack;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

import de.bacnetz.common.utils.Utils;

/**
 * The encoding of a time value shall be primitive, with four contents octets.
 * Time values shall be encoded in the contents octets as four binary
 * integers.<br />
 * <br />
 * 
 * The first contents octet shall represent the hour, in the 24-hour system (1
 * P.M. = D'13');<br />
 * <br />
 * 
 * the second octet shall represent the minute of the hour;<br />
 * <br />
 * 
 * the third octet shall represent the second of the minute;<br />
 * <br />
 * 
 * and the fourth octet shall represent the fractional part of the second in
 * hundredths of a second.<br />
 * <br />
 * 
 * A value of X`FF' = D'255' in any of the four octets shall indicate that the
 * corresponding value is unspecified. unspecified and shall be considered a
 * wildcard when matching times. If all four octets = X'FF', the corresponding
 * time may be interpreted as "any" or "don't care."<br />
 * <br />
 * 
 * Neither an unspecified time nor a time pattern shall be used in time values
 * that convey actual time, such as those presented by the Local_Time property
 * of the Device object or in a TimeSynchronization-Request.
 */
public class BACnetTime {

    /** hour, in the 24-hour system. (1 P.M. = D'13') */
    private int hour;

    /** the minute of the hour */
    private int minute;

    /** the second of the minute */
    private int second;

    /** fractional part of the second in hundredths of a second */
    private int hundredths;

    /**
     * ctor
     */
    public BACnetTime() {

    }

    /**
     * ctor
     * 
     * @param hour
     * @param minute
     * @param second
     * @param hundredths
     */
    public BACnetTime(final int hour, final int minute, final int second, final int hundredths) {
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.hundredths = hundredths;
    }

    public void fromDate(final Date date) {

        final LocalDateTime dateToLocalDateTime = Utils.dateToLocalDateTime(date);

        this.hour = dateToLocalDateTime.getHour();
        this.minute = dateToLocalDateTime.getMinute();
        this.second = dateToLocalDateTime.getSecond();
        final int nanoSeconds = dateToLocalDateTime.getNano();
        this.hundredths = nanoSeconds / 1000 / 1000 / 10;
    }

    public void fromLocalDateTime(final LocalDateTime localDateTime) {

        this.hour = localDateTime.getHour();
        this.minute = localDateTime.getMinute();
        this.second = localDateTime.getSecond();
        final int nanoSeconds = localDateTime.getNano();
        this.hundredths = nanoSeconds / 1000 / 1000 / 10;
    }

    public LocalTime toLocalTime() {

        if (hour == 0xFF || minute == 0xFF || second == 0xFF || hundredths == 0xFF) {
            throw new IllegalStateException("Wildcards detected! Cannot convert to LocalTime!");
        }

        // convert hundredths to nanoseconds;
        final int nanoSeconds = hundredths * 1000 * 1000 * 10;

        return LocalTime.of(hour, minute, second, nanoSeconds);
    }

    public int getHour() {
        return hour;
    }

    public void setHour(final int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(final int minute) {
        this.minute = minute;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(final int second) {
        this.second = second;
    }

    public int getHundredths() {
        return hundredths;
    }

    public void setHundredths(final int hundredths) {
        this.hundredths = hundredths;
    }

}
