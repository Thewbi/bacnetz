package de.bacnetz.stack;

import java.util.BitSet;

public abstract class BaseBitString {

    private final BitSet bitSet = new BitSet();

    public BitSet getBitSet() {
        return bitSet;
    }

    public void setBit(final boolean data, final int start) {

        final int byteIndex = start / 8;
        final int bitIndex = 7 - (start % 8);
        final int startIndex = byteIndex * 8 + bitIndex;

        setBooleanValueAt(bitSet, startIndex, data);
    }

    public void setBooleanValueAt(final BitSet bitSet, final int start, final boolean data) {
        bitSet.set(start, data);
    }

    public String getStringValue() {

        // why does length return the amount of bits -1 ?
        // Because: if the last bits are of zeroe value, the bitset will silently drop
        // them to save space!
        final int nbits = bitSet.length();

        final StringBuilder buffer = new StringBuilder(nbits);

        for (int i = 0; i < nbits / 8; i++) {

            final int byteIndex = i;

            for (int j = 1; j <= 8; j++) {

                final int temp = ((byteIndex + 1) * 8 - j);
                if (bitSet.get(temp)) {
                    buffer.append("1");
                } else {
                    buffer.append("0");
                }
            }
        }

        return buffer.toString();
    }

}
