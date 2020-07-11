package de.bacnetz.stack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import de.bacnetz.common.Utils;

public class NPDUTest {

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

		final byte[] hexStringToByteArray = Utils.hexStringToByteArray("0120ffff00ff");

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

}
