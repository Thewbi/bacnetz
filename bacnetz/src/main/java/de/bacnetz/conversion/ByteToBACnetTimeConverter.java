package de.bacnetz.conversion;

import de.bacnetz.stack.BACnetTime;

public class ByteToBACnetTimeConverter implements Converter<byte[], BACnetTime> {

    @Override
    public BACnetTime convert(final byte[] source) {

        final BACnetTime target = new BACnetTime();
        convert(source, target);

        return target;
    }

    @Override
    public void convert(final byte[] source, final BACnetTime target) {
        target.setHour(source[0]);
        target.setMinute(source[1]);
        target.setSecond(source[2]);
        target.setHundredths(source[3]);
    }

}
