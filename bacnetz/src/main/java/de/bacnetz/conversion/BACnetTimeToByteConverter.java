package de.bacnetz.conversion;

import de.bacnetz.stack.BACnetTime;

public class BACnetTimeToByteConverter implements Converter<BACnetTime, byte[]> {

    @Override
    public byte[] convert(final BACnetTime source) {

        final byte[] target = new byte[4];
        convert(source, target);

        return target;
    }

    @Override
    public void convert(final BACnetTime source, final byte[] target) {

        final int hour = (byte) (source.getHour() & 0xFF);
        final int minute = (byte) (source.getMinute() & 0xFF);
        final int second = (byte) (source.getSecond() & 0xFF);
        final int hundredths = (byte) (source.getHundredths() & 0xFF);

        target[0] = (byte) (hour & 0xFF);
        target[1] = (byte) (minute & 0xFF);
        target[2] = (byte) (second & 0xFF);
        target[3] = (byte) (hundredths & 0xFF);
    }

    public void convert(final BACnetTime source, final byte[] target, final int offset) {

        final int hour = (byte) (source.getHour() & 0xFF);
        final int minute = (byte) (source.getMinute() & 0xFF);
        final int second = (byte) (source.getSecond() & 0xFF);
        final int hundredths = (byte) (source.getHundredths() & 0xFF);

        target[offset + 0] = (byte) (hour & 0xFF);
        target[offset + 1] = (byte) (minute & 0xFF);
        target[offset + 2] = (byte) (second & 0xFF);
        target[offset + 3] = (byte) (hundredths & 0xFF);
    }

}
