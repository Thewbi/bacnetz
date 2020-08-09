package de.bacnetz.conversion;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import de.bacnetz.common.utils.Utils;
import de.bacnetz.stack.BACnetTime;

public class BACnetTimeToByteConverterTest {

    @Test
    public void testConvertBACnetDateToByteArray() {

        final int hour = 22;
        final int minute = 17;
        final int second = 14;
        final int hundredths = 1;

        final BACnetTime bacnetTime = new BACnetTime(hour, minute, second, hundredths);

        final BACnetTimeToByteConverter bacnetTimeToByteConverter = new BACnetTimeToByteConverter();
        final byte[] byteArray = bacnetTimeToByteConverter.convert(bacnetTime);
        final byte[] expected = new byte[] { 0x16, 0x11, 0x0E, 0x01 };

        System.out.println("Result:   " + Utils.byteArrayToStringNoPrefix(byteArray));
        System.out.println("Expected: " + Utils.byteArrayToStringNoPrefix(expected));

        assertTrue(Arrays.equals(byteArray, expected));
    }

}
