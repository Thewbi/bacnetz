package de.bacnetz.stack;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.bacnetz.common.Utils;

public class ServiceParameterTest {

	@Test
	public void testDeserialize() {

		final byte[] hexStringToByteArray = Utils.hexStringToByteArray("0a1f47");

		final ServiceParameter serviceParameter = new ServiceParameter();
		final int bytesProcessed = serviceParameter.fromBytes(hexStringToByteArray, 0);

		assertEquals(3, bytesProcessed);
		assertEquals(0, serviceParameter.getTagNumber());
		assertEquals(1, serviceParameter.getClassValue());
		assertEquals(0x1F, serviceParameter.getPayload()[0]);
		assertEquals(0x47, serviceParameter.getPayload()[1]);
	}

	@Test
	public void testDeserializeSecondParameter() {

		final byte[] hexStringToByteArray = Utils.hexStringToByteArray("1a1f47");

		final ServiceParameter serviceParameter = new ServiceParameter();
		final int bytesProcessed = serviceParameter.fromBytes(hexStringToByteArray, 0);

		assertEquals(3, bytesProcessed);
		assertEquals(1, serviceParameter.getTagNumber());
		assertEquals(1, serviceParameter.getClassValue());
		assertEquals(0x1F, serviceParameter.getPayload()[0]);
		assertEquals(0x47, serviceParameter.getPayload()[1]);
	}

}
