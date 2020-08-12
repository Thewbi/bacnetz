package de.bacnetz.stack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import de.bacnet.common.APIUtils;

public class NPDUTest {

    private static final Logger LOG = LogManager.getLogger(NPDUTest.class);

    public static final int DEVICE_MAC_ADDRESS = 0x001268;

    private static final int DEVICE_INSTANCE_NUMBER = 10001;

    /**
     * <pre>
     * 01 20 ff ff 00 ff - Building Automation and Control Network NPDU
     * 01    - Version: 0x01 (ASHRAE 135-1995)
     * 20    - Control: 0x20, Destination Specifier
     * ff ff - Destination Network Address: 65535
     * 00    - Destination MAC Layer Address Length: 0 indicates Broadcast on Destination Network
     * ff    - Hop Count: 255
     * </pre>
     */
    @Test
    public void testDeserialize() {

        final byte[] hexStringToByteArray = APIUtils.hexStringToByteArray("0120ffff00ff");

        final NPDU npdu = new NPDU();
        npdu.fromBytes(hexStringToByteArray, 0);

        // version is 1
        assertEquals(0x01, npdu.getVersion());

        // control byte says that a APDU is present, destination information is present
        // and priority is normal
        assertEquals(0x20, npdu.getControl());
        assertTrue(npdu.isAPDUMessage());
        assertTrue(npdu.isDestinationSpecifierPresent());
        assertFalse(npdu.isSourceSpecifierPresent());
        assertFalse(npdu.isConfirmedRequestPDUPresent());
        assertEquals(NetworkPriority.NORMAL_MESSAGE, npdu.getNetworkPriority());

        // destination network information
        assertEquals(0xFFFF, npdu.getDestinationNetworkNumber());
        assertEquals(0x00, npdu.getDestinationMACLayerAddressLength());
        assertEquals(0xFF, npdu.getDestinationHopCount());
    }

    @Test
    public void testDeserializeReadPropertyMultiple() {

        final byte[] hexStringToByteArray = APIUtils.hexStringToByteArray("010c012e03001268");

        final NPDU npdu = new NPDU();
        npdu.fromBytes(hexStringToByteArray, 0);

        // version is 1
        assertEquals(0x01, npdu.getVersion());

        // control byte says that a APDU is present, destination information is present
        // and priority is normal
        assertEquals(0x0C, npdu.getControl());
        assertTrue(npdu.isAPDUMessage());
        assertFalse(npdu.isDestinationSpecifierPresent());
        assertTrue(npdu.isSourceSpecifierPresent());
        assertTrue(npdu.isConfirmedRequestPDUPresent());
        assertEquals(NetworkPriority.NORMAL_MESSAGE, npdu.getNetworkPriority());

        // destination network information
        assertEquals(0x00, npdu.getDestinationNetworkNumber());
        assertEquals(0x00, npdu.getDestinationMACLayerAddressLength());
        assertEquals(0x00, npdu.getDestinationHopCount());

        // source network information
        assertEquals(0x012E, npdu.getSourceNetworkAddress());
        assertEquals(0x03, npdu.getSourceMacLayerAddressLength());
        assertEquals(DEVICE_MAC_ADDRESS, npdu.getSourceMac());
    }

    @Test
    public void testSerialize() {

        final NPDU npdu = new NPDU();
        npdu.setVersion(0x01);
        npdu.setControl(0x00);

        final byte[] data = new byte[10];

        npdu.toBytes(data, 2);

        // the NPDU Type serializes into a two byte long array
        assertEquals(2, npdu.getDataLength());
        assertTrue(Arrays.equals(data,
                new byte[] { 0x00, 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, 0x00, 0x00, 0x00, 0x00 }));
    }

    @Test
    public void testObjectListSourceInformation() {

        final NPDU npdu = new NPDU();
        npdu.setVersion(0x01);
        npdu.setControl(0x08);
        npdu.setSourceNetworkAddress(999);
        npdu.setSourceMacLayerAddressLength(2);
        npdu.setSourceMac(DEVICE_INSTANCE_NUMBER);

        final byte[] data = new byte[10];

        npdu.toBytes(data, 0);

        LOG.info(APIUtils.byteArrayToStringNoPrefix(data));

        assertTrue(Arrays.equals(data,
                new byte[] { 0x01, 0x08, (byte) 0x03, (byte) 0xE7, (byte) 0x02, (byte) 0x27, 0x11, 0x00, 0x00, 0x00 }));

    }

}
