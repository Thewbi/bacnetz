package de.bacnetz.stack;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.Date;

import org.junit.jupiter.api.Test;

import de.bacnetz.common.utils.Utils;

public class BACnetDateTest {

    @Test
    public void testBACnetDateFromDate() {

        final int year = 2015;
        final int month = 12;
        final int day = 22;
        final LocalDate localDate = LocalDate.of(year, month, day);
        final Date date = Utils.localDateToDate(localDate);

        final BACnetDate bacnetDate = new BACnetDate(date);

        assertEquals(2015, bacnetDate.getYear());
        assertEquals(12, bacnetDate.getMonth());
        assertEquals(22, bacnetDate.getDayOfMonth());
        assertEquals(2, bacnetDate.getDayOfWeek());
    }

    @Test
    public void testBACnetDateFromYearMonthDay() {

        final int year = 2015;
        final int month = 12;
        final int day = 22;

        final BACnetDate bacnetDate = new BACnetDate(year, month, day);

        assertEquals(2015, bacnetDate.getYear());
        assertEquals(12, bacnetDate.getMonth());
        assertEquals(22, bacnetDate.getDayOfMonth());
        assertEquals(2, bacnetDate.getDayOfWeek());
    }

}
