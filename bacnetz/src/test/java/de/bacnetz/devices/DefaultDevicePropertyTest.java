package de.bacnetz.devices;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import de.bacnet.factory.MessageType;
import de.bacnetz.common.utils.Utils;

public class DefaultDevicePropertyTest {

	@Test
	public void testEncodeUnsignedInteger_1Byte() {

		final String propertyName = "";
		final int propertyKey = 1;
		final int value = 100;
		final MessageType messageType = MessageType.UNSIGNED_INTEGER;

		final DefaultDeviceProperty<Integer> defaultDeviceProperty = new DefaultDeviceProperty<Integer>(propertyName,
				propertyKey, value, messageType);

		final byte[] bytes = defaultDeviceProperty.getValueAsByteArray();
		final byte[] expected = new byte[] { (byte) 0x64 };

		System.out.println("Result:   " + Utils.byteArrayToStringNoPrefix(bytes));
		System.out.println("Expected: " + Utils.byteArrayToStringNoPrefix(expected));

		assertTrue(Arrays.equals(bytes, expected));
	}

	@Test
	public void testEncodeUnsignedInteger_2Byte() {

		final String propertyName = "";
		final int propertyKey = 1;
		final int value = 3000;
		final MessageType messageType = MessageType.UNSIGNED_INTEGER;

		final DefaultDeviceProperty<Integer> defaultDeviceProperty = new DefaultDeviceProperty<Integer>(propertyName,
				propertyKey, value, messageType);

		final byte[] bytes = defaultDeviceProperty.getValueAsByteArray();
		final byte[] expected = new byte[] { (byte) 0x0B, (byte) 0xB8 };

		System.out.println("Result:   " + Utils.byteArrayToStringNoPrefix(bytes));
		System.out.println("Expected: " + Utils.byteArrayToStringNoPrefix(expected));

		assertTrue(Arrays.equals(bytes, expected));
	}

	@Test
	public void testEncodeUnsignedInteger_3Byte() {

		final String propertyName = "";
		final int propertyKey = 1;
		final int value = 7812441;
		final MessageType messageType = MessageType.UNSIGNED_INTEGER;

		final DefaultDeviceProperty<Integer> defaultDeviceProperty = new DefaultDeviceProperty<Integer>(propertyName,
				propertyKey, value, messageType);

		final byte[] bytes = defaultDeviceProperty.getValueAsByteArray();
		final byte[] expected = new byte[] { (byte) 0x77, (byte) 0x35, (byte) 0x59 };

		System.out.println("Result:   " + Utils.byteArrayToStringNoPrefix(bytes));
		System.out.println("Expected: " + Utils.byteArrayToStringNoPrefix(expected));

		assertTrue(Arrays.equals(bytes, expected));
	}

	@Test
	public void testEncodeUnsignedInteger_4Byte() {

		final String propertyName = "";
		final int propertyKey = 1;
		final int value = Integer.MAX_VALUE;
		final MessageType messageType = MessageType.UNSIGNED_INTEGER;

		final DefaultDeviceProperty<Integer> defaultDeviceProperty = new DefaultDeviceProperty<Integer>(propertyName,
				propertyKey, value, messageType);

		final byte[] bytes = defaultDeviceProperty.getValueAsByteArray();
		final byte[] expected = new byte[] { (byte) 0x7F, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };

		System.out.println("Result:   " + Utils.byteArrayToStringNoPrefix(bytes));
		System.out.println("Expected: " + Utils.byteArrayToStringNoPrefix(expected));

		assertTrue(Arrays.equals(bytes, expected));
	}

}
