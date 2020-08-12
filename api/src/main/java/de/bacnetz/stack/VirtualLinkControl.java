package de.bacnetz.stack;

import de.bacnet.common.APIUtils;

public class VirtualLinkControl {

    private int type;

    /**
     * <ul>
     * <li />Original-Unicast-NPDU (0x0A) J.2.11.1 Original-Unicast-NPDU: Format
     * <li />Original-Broadcast-NPDU (0x0B) J.2.12.1 Original-Broadcast-NPDU: Format
     * <li />Forwarded-NPDU (0x04)
     * </ul>
     */
    private int function;

    private int length;

    public VirtualLinkControl() {

    }

    public VirtualLinkControl(final VirtualLinkControl other) {
        this.type = other.getType();
        this.function = other.getFunction();
        this.length = other.getLength();
    }

    public void fromBytes(final byte[] data, final int startIndex) {

        int offset = 0;

        type = data[startIndex + offset++] & 0xFF;
        function = data[startIndex + offset++] & 0xFF;
        length = APIUtils.bytesToUnsignedShort(data[startIndex + offset++], data[startIndex + offset++], true);
    }

    public int getDataLength() {
        return 4;
    }

    public void toBytes(final byte[] data, final int offset) {
        data[offset + 0] = (byte) type;
        data[offset + 1] = (byte) function;
        APIUtils.addShortToBuffer(data, offset + 2, (short) length);
    }

    public byte[] getBytes() {
        final byte[] bytes = new byte[4];
        toBytes(bytes, 0);

        return bytes;
    }

    public int getType() {
        return type;
    }

    public void setType(final int type) {
        this.type = type;
    }

    public int getFunction() {
        return function;
    }

    public void setFunction(final int function) {
        this.function = function;
    }

    public int getLength() {
        return length;
    }

    public void setLength(final int length) {
        this.length = length;
    }

    public int getStructureLength() {
        return 4;
    }

}
