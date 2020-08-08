package de.bacnetz.conversion;

import de.bacnetz.stack.BACnetDate;

public class BACnetDateToByteConverter implements Converter<BACnetDate, byte[]> {

    @Override
    public byte[] convert(final BACnetDate source) {

        final byte[] target = new byte[4];
        convert(source, target);

        return target;
    }

    @Override
    public void convert(final BACnetDate source, final byte[] target) {

        final int year = source.getYear() - 1900;
        final int month = source.getMonth();
        final int dayOfMonth = source.getDayOfMonth();
        final int dayOfWeek = source.getDayOfWeek();

        target[0] = (byte) (year & 0xFF);
        target[1] = (byte) (month & 0xFF);
        target[2] = (byte) (dayOfMonth & 0xFF);
        target[3] = (byte) (dayOfWeek & 0xFF);
    }

    public void convert(final BACnetDate source, final byte[] target, final int offset) {

        final int year = source.getYear() - 1900;
        final int month = source.getMonth();
        final int dayOfMonth = source.getDayOfMonth();
        final int dayOfWeek = source.getDayOfWeek();

        target[offset + 0] = (byte) (year & 0xFF);
        target[offset + 1] = (byte) (month & 0xFF);
        target[offset + 2] = (byte) (dayOfMonth & 0xFF);
        target[offset + 3] = (byte) (dayOfWeek & 0xFF);
    }

}
