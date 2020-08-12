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
}
