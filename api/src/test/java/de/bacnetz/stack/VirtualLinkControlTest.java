package de.bacnetz.stack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import de.bacnetz.common.APIUtils;

public class VirtualLinkControlTest {

    @Test
    public void testDeserialize() {

        final byte[] hexStringToByteArray = APIUtils.hexStringToByteArray("810b0012");

        final VirtualLinkControl virtualLinkControl = new VirtualLinkControl();
        virtualLinkControl.fromBytes(hexStringToByteArray, 0);

        assertEquals(0x81, virtualLinkControl.getType());
        assertEquals(0x0B, virtualLinkControl.getFunction());
        assertEquals(18, virtualLinkControl.getLength());
    }

    @Test
    public void testSerialize() {

        final VirtualLinkControl virtualLinkControl = new VirtualLinkControl();
        virtualLinkControl.setType(0x81);
        virtualLinkControl.setFunction(0x0B);
        virtualLinkControl.setLength(0x14);

        final byte[] data = new byte[10];

        virtualLinkControl.toBytes(data, 2);

        // the VirtualLinkControl Type serializes into a four byte long array
        assertEquals(4, virtualLinkControl.getDataLength());
        assertTrue(Arrays.equals(data,
                new byte[] { 0x00, 0x00, (byte) 0x81, (byte) 0x0B, (byte) 0x00, (byte) 0x14, 0x00, 0x00, 0x00, 0x00 }));
    }

}
