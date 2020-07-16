package de.bacnetz.stack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.bacnetz.common.Utils;

public class APDUTest {

	@Test
	public void testDeserialize() {

		final byte[] hexStringToByteArray = Utils.hexStringToByteArray("10080a1f471a1f47");

		final APDU apdu = new APDU();
		apdu.fromBytes(hexStringToByteArray, 0, hexStringToByteArray.length);

		assertEquals(PDUType.UNCONFIRMED_SERVICE_REQUEST_PDU, apdu.getPduType());
		assertFalse(apdu.isSegmentation());
		assertFalse(apdu.isMoreSegmentsFollow());
		assertFalse(apdu.isSegmentedResponseAccepted());

		assertEquals(ServiceChoice.WHO_IS, apdu.getServiceChoice());

		final List<ServiceParameter> serviceParameters = apdu.getServiceParameters();
		assertEquals(2, serviceParameters.size());
	}

	@Test
	public void testDeserializeReadPropertyMultiple() {

		final byte[] hexStringToByteArray = Utils.hexStringToByteArray("0243990e0c020027101e09701f");

		final APDU apdu = new APDU();
		apdu.fromBytes(hexStringToByteArray, 0, hexStringToByteArray.length);

		assertEquals(PDUType.CONFIRMED_SERVICE_REQUEST_PDU, apdu.getPduType());
		assertFalse(apdu.isSegmentation());
		assertFalse(apdu.isMoreSegmentsFollow());
		assertTrue(apdu.isSegmentedResponseAccepted());

		assertEquals(ServiceChoice.READ_PROPERTY_MULTIPLE, apdu.getServiceChoice());

		final List<ServiceParameter> serviceParameters = apdu.getServiceParameters();
		assertEquals(3, serviceParameters.size());
	}

	@Test
	public void testSerialize() {

		final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
		objectIdentifierServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
		objectIdentifierServiceParameter.setTagNumber(ServiceParameter.BACNET_OBJECT_IDENTIFIER);
		objectIdentifierServiceParameter.setLengthValueType(4);
		objectIdentifierServiceParameter.setObjectType(ObjectIdentifierServiceParameter.OBJECT_TYPE_DEVICE);
		objectIdentifierServiceParameter.setInstanceNumber(10001);

		final ServiceParameter maximumAPDUServiceParameter = new ServiceParameter();
		maximumAPDUServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
		maximumAPDUServiceParameter.setTagNumber(ServiceParameter.UNSIGNED_INTEGER_CODE);
		maximumAPDUServiceParameter.setLengthValueType(2);
		maximumAPDUServiceParameter.setPayload(new byte[] { (byte) 0x01, (byte) 0xE0 }); // 0x01E0 = 480

		final ServiceParameter segmentationSupportedServiceParameter = new ServiceParameter();
		segmentationSupportedServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
		segmentationSupportedServiceParameter.setTagNumber(ServiceParameter.ENUMERATED_CODE);
		segmentationSupportedServiceParameter.setLengthValueType(1);
		segmentationSupportedServiceParameter.setPayload(new byte[] { (byte) 0x00 }); // segmented-both

		final ServiceParameter vendorIdServiceParameter = new ServiceParameter();
		vendorIdServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
		vendorIdServiceParameter.setTagNumber(ServiceParameter.UNSIGNED_INTEGER_CODE);
		vendorIdServiceParameter.setLengthValueType(1);
		vendorIdServiceParameter.setPayload(new byte[] { (byte) 0xB2 }); // 0xB2 = 178d = loytec

		final APDU apdu = new APDU();
//		apdu.setMoreSegmentsFollow(moreSegmentsFollow);
		apdu.setPduType(PDUType.UNCONFIRMED_SERVICE_REQUEST_PDU);
//		apdu.setSegmentation(segmentation);
//		apdu.setSegmentedResponseAccepted(segmentedResponseAccepted);
		apdu.setServiceChoice(ServiceChoice.I_AM);
		apdu.setVendorMap(new HashMap<Integer, String>());
		apdu.getServiceParameters().add(objectIdentifierServiceParameter);
		apdu.getServiceParameters().add(maximumAPDUServiceParameter);
		apdu.getServiceParameters().add(segmentationSupportedServiceParameter);
		apdu.getServiceParameters().add(vendorIdServiceParameter);

		final byte[] data = new byte[20];
		apdu.toBytes(data, 2);

		final byte[] expected = new byte[] { 0x00, 0x00, (byte) 0x10, (byte) 0x00, (byte) 0xc4, (byte) 0x02, 0x00, 0x27,
				0x11, 0x22, 0x01, (byte) 0xe0, (byte) 0x91, 0x00, 0x21, (byte) 0xb2, 0x00, 0x00, 0x00, 0x00 };

		System.out.println("Result:   " + Utils.byteArrayToStringNoPrefix(data));
		System.out.println("Expected: " + Utils.byteArrayToStringNoPrefix(expected));

		// 1000c4020027102201e0910021b2

		// the APDU Type serializes into a two byte long array
		assertEquals(14, apdu.getDataLength());
		assertTrue(Arrays.equals(data, expected));
	}

	@Test
	public void testDeserializeReadProperty() {

		final byte[] hexStringToByteArray = Utils.hexStringToByteArray("02457A0C0C020027111961");

		final APDU apdu = new APDU();
		apdu.fromBytes(hexStringToByteArray, 0, hexStringToByteArray.length);

		assertEquals(PDUType.UNCONFIRMED_SERVICE_REQUEST_PDU, apdu.getPduType());
		assertFalse(apdu.isSegmentation());
		assertFalse(apdu.isMoreSegmentsFollow());
		assertFalse(apdu.isSegmentedResponseAccepted());

		assertEquals(ServiceChoice.WHO_IS, apdu.getServiceChoice());

		final List<ServiceParameter> serviceParameters = apdu.getServiceParameters();
		assertEquals(2, serviceParameters.size());
	}

}
