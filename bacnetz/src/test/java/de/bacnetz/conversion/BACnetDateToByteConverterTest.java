package de.bacnetz.conversion;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import de.bacnetz.common.utils.Utils;
import de.bacnetz.stack.BACnetDate;

public class BACnetDateToByteConverterTest {

    @Test
    public void testConvertBACnetDateToByteArray() {

        final int year = 2012;
        final int month = 07;
        final int day = 11;

        final BACnetDate bacnetDate = new BACnetDate(year, month, day);

        final BACnetDateToByteConverter bacnetDateToByteConverter = new BACnetDateToByteConverter();
        final byte[] byteArray = bacnetDateToByteConverter.convert(bacnetDate);
        final byte[] expected = new byte[] { 0x70, 0x07, 0x0B, 0x03 };

        System.out.println("Result:   " + Utils.byteArrayToStringNoPrefix(byteArray));
        System.out.println("Expected: " + Utils.byteArrayToStringNoPrefix(expected));

        assertTrue(Arrays.equals(byteArray, expected));
    }
}
