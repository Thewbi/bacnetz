package de.bacnetz.conversion;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.bacnetz.stack.BACnetTime;

public class ByteToBACnetTimeConverterTest {

    @Test
    public void testConvertByteArrayToDate() {

        final byte[] byteArray = new byte[] { 0x16, 0x11, 0x0E, 0x01 };

        final ByteToBACnetTimeConverter byteToBACnetTimeConverter = new ByteToBACnetTimeConverter();
        final BACnetTime bacnetTime = byteToBACnetTimeConverter.convert(byteArray);

        System.out.println("LocalTime is: " + bacnetTime.toLocalTime());
//        System.out.println("Date is: " + bacnetTime.toDate());

        assertEquals(22, bacnetTime.getHour());
        assertEquals(17, bacnetTime.getMinute());
        assertEquals(14, bacnetTime.getSecond());
        assertEquals(1, bacnetTime.getHundredths());
    }

}
