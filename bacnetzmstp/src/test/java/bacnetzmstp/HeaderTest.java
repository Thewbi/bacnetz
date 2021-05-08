package bacnetzmstp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class HeaderTest {

    @Test
    public void testCRC() {

//        int crc = 0x00;
        int crc = 0xFF;

//        crc = CRC_Calc_Header(0x55, crc);
//        crc = CRC_Calc_Header(0xff, crc);
        crc = CRC_Calc_Header(0x01, crc); // frame type
        crc = CRC_Calc_Header(0x39, crc); // destination address
        crc = CRC_Calc_Header(0x19, crc); // source address
        crc = CRC_Calc_Header(0x00, crc); // length
        crc = CRC_Calc_Header(0x00, crc);
//        crc = CRC_Calc_Header(0xe5, crc);

        // 229d = 0xe5
//        System.out.println(crc);
        System.out.println(onesComplement(crc));

    }

    @Test
    public void testCRC2() {

//        int crc = 0x00;
        int crc = 0xFF;

//        crc = CRC_Calc_Header(0x55, crc);
//        crc = CRC_Calc_Header(0xff, crc);
        crc = CRC_Calc_Header(0x01, crc); // frame type
        crc = CRC_Calc_Header(0x41, crc); // destination address
        crc = CRC_Calc_Header(0x19, crc); // source address
        crc = CRC_Calc_Header(0x00, crc); // length 1
        crc = CRC_Calc_Header(0x00, crc); // length 2
//        crc = CRC_Calc_Header(0xe5, crc);

        // 28d = 0x1c
//        System.out.println(onesComplement(crc));

        assertEquals(0x1c, onesComplement(crc));

        final Header header = new Header();
        header.setFrameType(0x01);
        header.setDestinationAddress(0x41);
        header.setSourceAddress(0x19);
        header.setLength1(0x00);
        header.setLength2(0x00);
        header.setCrc(0x1c);

        assertTrue(header.checkCRC());

    }

    private static int onesComplement(final int i) {
        return (~i) & 0xff;
    }

    static int CRC_Calc_Header(final int dataValue, final int crcValue) {
        int crc = crcValue ^ dataValue; /* XOR C7..C0 with D7..D0 */

        /* Exclusive OR the terms in the table (top down) */
        crc = crc ^ (crc << 1) ^ (crc << 2) ^ (crc << 3) ^ (crc << 4) ^ (crc << 5) ^ (crc << 6) ^ (crc << 7);

        /* Combine bits shifted out left hand end */
        return (crc & 0xfe) ^ ((crc >> 8) & 1);
    }

}
