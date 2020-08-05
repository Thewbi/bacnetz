package de.bacnetz.stack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import de.bacnetz.common.utils.Utils;
import de.bacnetz.devices.ObjectType;

public class ObjectIdentifierServiceParameterTest {

	@Test
	public void testSerialize() {

		final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
		objectIdentifierServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
		objectIdentifierServiceParameter.setTagNumber(ServiceParameter.BACNET_OBJECT_IDENTIFIER);
		objectIdentifierServiceParameter.setLengthValueType(4);
		objectIdentifierServiceParameter.setObjectType(ObjectType.DEVICE);
		objectIdentifierServiceParameter.setInstanceNumber(10001);

		final byte[] data = new byte[10];

		objectIdentifierServiceParameter.toBytes(data, 2);

		// the NPDU Type serializes into a two byte long array
		assertEquals(5, objectIdentifierServiceParameter.getDataLength());

		final byte[] expected = new byte[] { 0x00, 0x00, (byte) 0xC4, (byte) 0x02, (byte) 0x00, (byte) 0x27,
				(byte) 0x11, 0x00, 0x00, 0x00 };

		System.out.println("Result:   " + Utils.byteArrayToStringNoPrefix(data));
		System.out.println("Expected: " + Utils.byteArrayToStringNoPrefix(expected));

		assertTrue(Arrays.equals(data, expected));
	}

	@Test
	public void testDeserialize() {

		final byte[] hexStringToByteArray = Utils.hexStringToByteArray("0C02002710");

		final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
		objectIdentifierServiceParameter.fromBytes(hexStringToByteArray, 0);

		assertEquals(5, objectIdentifierServiceParameter.getDataLength());
		assertEquals(10000, objectIdentifierServiceParameter.getInstanceNumber());
		assertEquals(4, objectIdentifierServiceParameter.getLengthValueType());
		objectIdentifierServiceParameter.getObjectType();
		assertEquals(1, objectIdentifierServiceParameter.getTagClass().getId());
		assertEquals(0, objectIdentifierServiceParameter.getTagNumber());

		final byte[] actual = objectIdentifierServiceParameter.getPayload();
		final byte[] expected = new byte[] { (byte) 0x02, (byte) 0x00, (byte) 0x27, (byte) 0x10 };

		System.out.println("Actual:   " + Utils.byteArrayToStringNoPrefix(actual));
		System.out.println("Expected: " + Utils.byteArrayToStringNoPrefix(expected));

		assertTrue(Arrays.equals(actual, expected));
	}

}
