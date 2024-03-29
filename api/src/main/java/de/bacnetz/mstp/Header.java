package de.bacnetz.mstp;

import de.bacnetz.common.APIUtils;

/**
 * https://sourceforge.net/p/bacnet/mailman/message/1259086/
 */
public class Header {

    public static final int HEADER_LENGTH = 8;

    private int headerOctetCount = 0;

    private int frameType = -1;

    private int destinationAddress = -1;

    private int sourceAddress = -1;

    private int length1 = -1;

    private int length2 = -1;

    private int length = -1;

    private int crc = -1;

    public void input(final int data) {

        if (done()) {
            throw new RuntimeException("HeaderOctetCount is " + headerOctetCount + "! Header is done already!");
        }

        switch (headerOctetCount) {
        case 0:
            frameType = data;
            headerOctetCount++;
            break;

        case 1:
            destinationAddress = data;
            headerOctetCount++;
            break;

        case 2:
            sourceAddress = data;
            headerOctetCount++;
            break;

        case 3:
            length1 = data;
            headerOctetCount++;
            break;

        case 4:
            length2 = data;
            length = APIUtils.bytesToUnsignedShort((byte) length1, (byte) length2, true);
            headerOctetCount++;
            break;
        }
    }

    public boolean done() {
        if (headerOctetCount > 5) {
            throw new RuntimeException("HeaderOctetCount is " + headerOctetCount + "! Allowed max is 5!");
        }
        return headerOctetCount == 5;
    }

    public boolean checkCRC() {
        int tempCRC = 0xFF;

        tempCRC = CRC_Calc_Header(frameType, tempCRC); // frame type
        tempCRC = CRC_Calc_Header(destinationAddress, tempCRC); // destination address
        tempCRC = CRC_Calc_Header(sourceAddress, tempCRC); // source address
        tempCRC = CRC_Calc_Header(length1, tempCRC); // length
        tempCRC = CRC_Calc_Header(length2, tempCRC);

        final int expected = onesComplement(tempCRC);
//        System.out.println(expected + " " + crc);

        return expected == crc;
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

    public void reset() {
        headerOctetCount = 0;
        frameType = -1;
        destinationAddress = -1;
        sourceAddress = -1;
        length1 = -1;
        length2 = -1;
        length = -1;
        crc = -1;
    }

    public byte[] toBytes() {

        int tempCRC = 0xFF;
        tempCRC = CRC_Calc_Header(frameType, tempCRC); // frame type
        tempCRC = CRC_Calc_Header(destinationAddress, tempCRC); // destination address
        tempCRC = CRC_Calc_Header(sourceAddress, tempCRC); // source address
        tempCRC = CRC_Calc_Header(length1, tempCRC); // length
        tempCRC = CRC_Calc_Header(length2, tempCRC);

        final byte reply[] = { (byte) 0x55, (byte) 0xFF, (byte) frameType, (byte) destinationAddress,
                (byte) sourceAddress, (byte) length1, (byte) length2, (byte) onesComplement(tempCRC) };

        return reply;
    }

    public int toBytes(final byte[] data, final int offset) {

        int tempCRC = 0xFF;
        tempCRC = CRC_Calc_Header(frameType, tempCRC); // frame type
        tempCRC = CRC_Calc_Header(destinationAddress, tempCRC); // destination address
        tempCRC = CRC_Calc_Header(sourceAddress, tempCRC); // source address
        tempCRC = CRC_Calc_Header(length1, tempCRC); // length
        tempCRC = CRC_Calc_Header(length2, tempCRC);

//        final byte reply[] = { (byte) 0x55, (byte) 0xFF, (byte) frameType, (byte) destinationAddress,
//                (byte) sourceAddress, (byte) length1, (byte) length2, (byte) onesComplement(tempCRC) };

        data[0] = (byte) 0x55;
        data[1] = (byte) 0xFF;
        data[2] = (byte) frameType;
        data[3] = (byte) destinationAddress;
        data[4] = (byte) sourceAddress;
        data[5] = (byte) length1;
        data[6] = (byte) length2;
        data[7] = (byte) onesComplement(tempCRC);

        return HEADER_LENGTH;
    }

    @Override
    public String toString() {
        return "Header [frameType=" + frameType + ", destinationAddress=" + destinationAddress + ", sourceAddress="
                + sourceAddress + ", length1=" + length1 + ", length2=" + length2 + ", crc=" + crc + "]";
    }

    public int getFrameType() {
        return frameType;
    }

    public void setFrameType(final int frameType) {
        this.frameType = frameType;
    }

    public int getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(final int destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public int getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(final int sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    public int getLength() {
        return length;
    }

    public void setLength(final int length) {
        this.length = length;
    }

    public int getCrc() {
        return crc;
    }

    public void setCrc(final int crc) {
        this.crc = crc;
    }

    public int getLength1() {
        return length1;
    }

    public void setLength1(final int length1) {
        this.length1 = length1;
    }

    public int getLength2() {
        return length2;
    }

    public void setLength2(final int length2) {
        this.length2 = length2;
    }

}
