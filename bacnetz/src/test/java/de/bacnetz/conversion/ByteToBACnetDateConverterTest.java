package de.bacnetz.conversion;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.bacnetz.stack.BACnetDate;

public class ByteToBACnetDateConverterTest {

    @Test
    public void testConvertByteArrayToDate() {

        final byte[] byteArray = new byte[] { 0x70, 0x07, 0x0B, 0x03 };

        final ByteToBACnetDateConverter byteToBACnetDateConverter = new ByteToBACnetDateConverter();
        final BACnetDate bacnetDate = byteToBACnetDateConverter.convert(byteArray);

        System.out.println("LocalDate is: " + bacnetDate.toLocalDate());
        System.out.println("Date is: " + bacnetDate.toDate());

        assertEquals(2012, bacnetDate.getYear());
        assertEquals(07, bacnetDate.getMonth());
        assertEquals(11, bacnetDate.getDayOfMonth());
        assertEquals(3, bacnetDate.getDayOfWeek());
    }

}
