package de.bacnetz.stack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.bacnetz.common.Utils;

public class APDUTest {

	@Test
	public void testDeserialize() {

		final byte[] hexStringToByteArray = Utils.hexStringToByteArray("10080a1f471a1f47");

		final APDU apdu = new APDU();
		apdu.fromBytes(hexStringToByteArray, 0);

		assertEquals(PDUType.UNCONFIRMED_SERVICE_REQUEST_PDU, apdu.getPduType());
		assertFalse(apdu.isSegmentation());
		assertFalse(apdu.isMoreSegmentsFollow());
		assertFalse(apdu.isSegmentedResponseAccepted());

		assertEquals(ServiceChoice.WHO_IS, apdu.getServiceChoice());

		final List<ServiceParameter> serviceParameters = apdu.getServiceParameters();
		assertEquals(2, serviceParameters.size());
	}

}
