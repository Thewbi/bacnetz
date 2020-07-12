package de.bacnetz.stack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

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
		assertEquals(TagClass.CONTEXT_SPECIFIC_TAG, serviceParameter.getTagClass());
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
		assertEquals(TagClass.CONTEXT_SPECIFIC_TAG, serviceParameter.getTagClass());
		assertEquals(0x1F, serviceParameter.getPayload()[0]);
		assertEquals(0x47, serviceParameter.getPayload()[1]);
	}

	@Test
	public void testSerializeMaximumAPDU() {

		final ServiceParameter maximumAPDUServiceParameter = new ServiceParameter();
		maximumAPDUServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
		maximumAPDUServiceParameter.setTagNumber(ServiceParameter.UNSIGNED_INTEGER_CODE);
		maximumAPDUServiceParameter.setLengthValueType(2);
		maximumAPDUServiceParameter.setPayload(new byte[] { (byte) 0x01, (byte) 0xE0 }); // 0x01E0 = 480

		final byte[] data = new byte[10];
		maximumAPDUServiceParameter.toBytes(data, 2);

		final byte[] expected = new byte[] { 0x00, 0x00, (byte) 0x22, (byte) 0x01, (byte) 0xE0, (byte) 0x00, 0x00, 0x00,
				0x00, 0x00 };

		System.out.println("Result:   " + Utils.byteArrayToStringNoPrefix(data));
		System.out.println("Expected: " + Utils.byteArrayToStringNoPrefix(expected));

		// the NPDU Type serializes into a three byte long array
		assertEquals(3, maximumAPDUServiceParameter.getDataLength());
		assertTrue(Arrays.equals(data, expected));
	}

	@Test
	public void testSerializeSegmentation() {

		final ServiceParameter segmentationSupportedServiceParameter = new ServiceParameter();
		segmentationSupportedServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
		segmentationSupportedServiceParameter.setTagNumber(ServiceParameter.ENUMERATED_CODE);
		segmentationSupportedServiceParameter.setLengthValueType(1);
		segmentationSupportedServiceParameter.setPayload(new byte[] { (byte) 0x00 }); // segmented-both

		final byte[] data = new byte[10];
		segmentationSupportedServiceParameter.toBytes(data, 2);

		final byte[] expected = new byte[] { 0x00, 0x00, (byte) 0x91, (byte) 0x00, (byte) 0x00, (byte) 0x00, 0x00, 0x00,
				0x00, 0x00 };

		System.out.println("Result:   " + Utils.byteArrayToStringNoPrefix(data));
		System.out.println("Expected: " + Utils.byteArrayToStringNoPrefix(expected));

		// the NPDU Type serializes into a two byte long array
		assertEquals(2, segmentationSupportedServiceParameter.getDataLength());
		assertTrue(Arrays.equals(data, expected));
	}

	@Test
	public void testSerializeVendorId() {

		final ServiceParameter vendorIdServiceParameter = new ServiceParameter();
		vendorIdServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
		vendorIdServiceParameter.setTagNumber(ServiceParameter.UNSIGNED_INTEGER_CODE);
		vendorIdServiceParameter.setLengthValueType(1);
		vendorIdServiceParameter.setPayload(new byte[] { (byte) 0xB2 }); // 0xB2 = 178d = loytec

		final byte[] data = new byte[10];
		vendorIdServiceParameter.toBytes(data, 2);

		final byte[] expected = new byte[] { 0x00, 0x00, (byte) 0x21, (byte) 0xb2, (byte) 0x00, (byte) 0x00, 0x00, 0x00,
				0x00, 0x00 };

		System.out.println("Result:   " + Utils.byteArrayToStringNoPrefix(data));
		System.out.println("Expected: " + Utils.byteArrayToStringNoPrefix(expected));

		// the NPDU Type serializes into a two byte long array
		assertEquals(2, vendorIdServiceParameter.getDataLength());
		assertTrue(Arrays.equals(data, expected));
	}

	@Test
	public void testDeserializeSystemStatus() {

		final byte[] hexStringToByteArray = Utils.hexStringToByteArray("2970");

		final ServiceParameter serviceParameter = new ServiceParameter();
		final int bytesProcessed = serviceParameter.fromBytes(hexStringToByteArray, 0);

		assertEquals(2, bytesProcessed);

		// tag number 2
		assertEquals(2, serviceParameter.getTagNumber());

		// context specific tag
		assertEquals(TagClass.CONTEXT_SPECIFIC_TAG, serviceParameter.getTagClass());

		// tag length value = 1

		// system status 112
		assertEquals(0x70, serviceParameter.getPayload()[0]);
	}

}
