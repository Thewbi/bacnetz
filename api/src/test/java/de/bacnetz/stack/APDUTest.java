package de.bacnetz.stack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.bacnetz.common.APIUtils;
import de.bacnetz.devices.DevicePropertyType;
import de.bacnetz.devices.ObjectType;

public class APDUTest {

    @Test
    public void testDeserialize() {

        final byte[] hexStringToByteArray = APIUtils.hexStringToByteArray("10080a1f471a1f47");

        final APDU apdu = new APDU();
        apdu.fromBytes(hexStringToByteArray, 0, hexStringToByteArray.length);
        apdu.processPayload(apdu.getPayload(), 0, apdu.getPayload().length, 0);

        assertEquals(PDUType.UNCONFIRMED_SERVICE_REQUEST_PDU, apdu.getPduType());
        assertFalse(apdu.isSegmentation());
        assertFalse(apdu.isMoreSegmentsFollow());
        assertFalse(apdu.isSegmentedResponseAccepted());

        assertEquals(UnconfirmedServiceChoice.WHO_IS, apdu.getUnconfirmedServiceChoice());

        final List<ServiceParameter> serviceParameters = apdu.getServiceParameters();
        assertEquals(2, serviceParameters.size());
    }
    
    @Test
    public void testDeserialize2() {
    	
        final byte[] hexStringToByteArray = APIUtils.hexStringToByteArray("810a001301040275530e0c00c000011e09081f");

        final APDU apdu = new APDU();
        apdu.fromBytes(hexStringToByteArray, 6, hexStringToByteArray.length);
        apdu.processPayload(apdu.getPayload(), 0, apdu.getPayload().length, 0);

        assertEquals(PDUType.CONFIRMED_SERVICE_REQUEST_PDU, apdu.getPduType());
        assertFalse(apdu.isSegmentation());
        assertFalse(apdu.isMoreSegmentsFollow());
        assertTrue(apdu.isSegmentedResponseAccepted());

        assertEquals(ConfirmedServiceChoice.READ_PROPERTY_MULTIPLE, apdu.getConfirmedServiceChoice());

        final List<ServiceParameter> serviceParameters = apdu.getServiceParameters();
        assertEquals(4, serviceParameters.size());
    }
    
    @Test
    public void testDeserialize3() {
    	
        final byte[] hexStringToByteArray = APIUtils.hexStringToByteArray("810a001201040275540c0c00011a0173");

        final APDU apdu = new APDU();
        apdu.fromBytes(hexStringToByteArray, 6, hexStringToByteArray.length);
        Exception exception = assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
        	apdu.processPayload(apdu.getPayload(), 0, apdu.getPayload().length, 0);
        });

        String expectedMessage = "arraycopy: last source index 9 out of bounds for byte[6]";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testDeserialize_addListElement() {

        final byte[] hexStringToByteArray = APIUtils
                .hexStringToByteArray("02156f080c0200271119ca3e1e22012d6506c0a800eabac01f3f");

        final APDU apdu = new APDU();
        apdu.fromBytes(hexStringToByteArray, 0, hexStringToByteArray.length);
        apdu.processPayload(apdu.getPayload(), 0, apdu.getPayload().length, 0);

        assertEquals(PDUType.CONFIRMED_SERVICE_REQUEST_PDU, apdu.getPduType());
        assertFalse(apdu.isSegmentation());
        assertFalse(apdu.isMoreSegmentsFollow());
        assertTrue(apdu.isSegmentedResponseAccepted());

        assertEquals(ConfirmedServiceChoice.ADD_LIST_ELEMENT, apdu.getConfirmedServiceChoice());

        final List<ServiceParameter> serviceParameters = apdu.getServiceParameters();
        assertEquals(8, serviceParameters.size());

        // There are 8 service parameters
        //
        // object identifier
        // property identifier restart-notification-recipients 202d
        // opening bracket 3
        // opening bracket 1
        // network-number
        // MAC-Address
        // closing bracket 1
        // closing bracket 3
    }

    @Test
    public void testDeserializeReadPropertyMultiple() {

        final byte[] hexStringToByteArray = APIUtils.hexStringToByteArray("0243990e0c020027101e09701f");

        final APDU apdu = new APDU();
        apdu.fromBytes(hexStringToByteArray, 0, hexStringToByteArray.length);
        apdu.processPayload(apdu.getPayload(), 0, apdu.getPayload().length, 0);

        assertEquals(PDUType.CONFIRMED_SERVICE_REQUEST_PDU, apdu.getPduType());
        assertFalse(apdu.isSegmentation());
        assertFalse(apdu.isMoreSegmentsFollow());
        assertTrue(apdu.isSegmentedResponseAccepted());

        assertEquals(ConfirmedServiceChoice.READ_PROPERTY_MULTIPLE, apdu.getConfirmedServiceChoice());

        final List<ServiceParameter> serviceParameters = apdu.getServiceParameters();
        assertEquals(4, serviceParameters.size());
    }

    @Test
    public void testDeserializeReadPropertyMultiple_2() {

        final byte[] hexStringToByteArray = APIUtils.hexStringToByteArray("0245680e0c020027111e096b093e09a7090b090a1f");

        final APDU apdu = new APDU();
        apdu.fromBytes(hexStringToByteArray, 0, hexStringToByteArray.length);
        apdu.processPayload(apdu.getPayload(), 0, apdu.getPayload().length, 0);

        assertEquals(PDUType.CONFIRMED_SERVICE_REQUEST_PDU, apdu.getPduType());
        assertFalse(apdu.isSegmentation());
        assertFalse(apdu.isMoreSegmentsFollow());
        assertTrue(apdu.isSegmentedResponseAccepted());

        assertEquals(ConfirmedServiceChoice.READ_PROPERTY_MULTIPLE, apdu.getConfirmedServiceChoice());

        final List<ServiceParameter> serviceParameters = apdu.getServiceParameters();
        assertEquals(8, serviceParameters.size());

        // service parameter 0 is the object identifier
        ServiceParameter serviceParameter = serviceParameters.get(0);
        assertTrue(serviceParameter instanceof ObjectIdentifierServiceParameter);
        final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = (ObjectIdentifierServiceParameter) serviceParameter;
        assertEquals(objectIdentifierServiceParameter.getObjectType(), ObjectType.DEVICE);
        assertEquals(objectIdentifierServiceParameter.getInstanceNumber(), 10001);

        // opening tag
        serviceParameter = serviceParameters.get(1);
        assertEquals(serviceParameter.getTagClass(), TagClass.CONTEXT_SPECIFIC_TAG);
        assertEquals(serviceParameter.getTagNumber(), 1);
        assertEquals(serviceParameter.getLengthValueType(), 6);

        // 107d - segmentation-supported
        serviceParameter = serviceParameters.get(2);
        assertEquals(serviceParameter.getTagClass(), TagClass.CONTEXT_SPECIFIC_TAG);
        assertEquals(serviceParameter.getTagNumber(), 0);
        assertEquals(serviceParameter.getLengthValueType(), 1);
        assertEquals((serviceParameter.getPayload()[0]) & 0xFF, 107);

        // 62d - max-apdu-length-accepted
        serviceParameter = serviceParameters.get(3);
        assertEquals(serviceParameter.getTagClass(), TagClass.CONTEXT_SPECIFIC_TAG);
        assertEquals(serviceParameter.getTagNumber(), 0);
        assertEquals(serviceParameter.getLengthValueType(), 1);
        assertEquals((serviceParameter.getPayload()[0]) & 0xFF, 62);

        // 167d - max-segments-accepted
        serviceParameter = serviceParameters.get(4);
        assertEquals(serviceParameter.getTagClass(), TagClass.CONTEXT_SPECIFIC_TAG);
        assertEquals(serviceParameter.getTagNumber(), 0);
        assertEquals(serviceParameter.getLengthValueType(), 1);
        assertEquals((serviceParameter.getPayload()[0]) & 0xFF, 167);

        // 11d - apdu-timeout
        serviceParameter = serviceParameters.get(5);
        assertEquals(serviceParameter.getTagClass(), TagClass.CONTEXT_SPECIFIC_TAG);
        assertEquals(serviceParameter.getTagNumber(), 0);
        assertEquals(serviceParameter.getLengthValueType(), 1);
        assertEquals((serviceParameter.getPayload()[0]) & 0xFF, 11);

        // 10d - apdu-segment-timeout
        serviceParameter = serviceParameters.get(6);
        assertEquals(serviceParameter.getTagClass(), TagClass.CONTEXT_SPECIFIC_TAG);
        assertEquals(serviceParameter.getTagNumber(), 0);
        assertEquals(serviceParameter.getLengthValueType(), 1);
        assertEquals((serviceParameter.getPayload()[0]) & 0xFF, 10);

        // closing tag
        serviceParameter = serviceParameters.get(7);
        assertEquals(serviceParameter.getTagClass(), TagClass.CONTEXT_SPECIFIC_TAG);
        assertEquals(serviceParameter.getTagNumber(), 1);
        assertEquals(serviceParameter.getLengthValueType(), 7);
    }

    /**
     * See bacnet_virtual_device_10001_it_works_almost.pcapng - message no. 98
     */
    @Test
    public void testDeserializeReadPropertyMultiple_3() {

        final byte[] hexStringToByteArray = APIUtils
                .hexStringToByteArray("0215640e0c00c000011e0955096f1f0c00c000021e0955096f1f0c00c000031e0955096f1f");

        final APDU apdu = new APDU();
        apdu.fromBytes(hexStringToByteArray, 0, hexStringToByteArray.length);
        apdu.processPayload(apdu.getPayload(), 0, apdu.getPayload().length, 0);

        assertEquals(PDUType.CONFIRMED_SERVICE_REQUEST_PDU, apdu.getPduType());
        assertFalse(apdu.isSegmentation());
        assertFalse(apdu.isMoreSegmentsFollow());
        assertTrue(apdu.isSegmentedResponseAccepted());

        assertEquals(ConfirmedServiceChoice.READ_PROPERTY_MULTIPLE, apdu.getConfirmedServiceChoice());

        final List<ServiceParameter> serviceParameters = apdu.getServiceParameters();
        assertEquals(15, serviceParameters.size());

        int index = -1;

        // object identifier service parameter
        index++;
        ObjectIdentifierServiceParameter objectIdentifier = (ObjectIdentifierServiceParameter) serviceParameters
                .get(index);
        assertEquals(ObjectType.BINARY_INPUT, objectIdentifier.getObjectType());
        assertEquals(1, objectIdentifier.getInstanceNumber());

        // opening
        index++;
        ServiceParameter serviceParameter = serviceParameters.get(index);
        assertEquals(TagClass.CONTEXT_SPECIFIC_TAG, serviceParameter.getTagClass());
        assertEquals(1, serviceParameter.getTagNumber());
        assertEquals(ServiceParameter.OPENING_TAG_CODE, serviceParameter.getLengthValueType());

        index++;
        serviceParameter = serviceParameters.get(index);
        assertEquals(TagClass.CONTEXT_SPECIFIC_TAG, serviceParameter.getTagClass());
        assertEquals(0, serviceParameter.getTagNumber());
        assertEquals(1, serviceParameter.getLengthValueType());
        assertEquals(DevicePropertyType.PRESENT_VALUE.getCode(), serviceParameter.getPayload()[0]);

        index++;
        serviceParameter = serviceParameters.get(index);
        assertEquals(TagClass.CONTEXT_SPECIFIC_TAG, serviceParameter.getTagClass());
        assertEquals(0, serviceParameter.getTagNumber());
        assertEquals(1, serviceParameter.getLengthValueType());
        assertEquals(DevicePropertyType.STATUS_FLAGS.getCode(), serviceParameter.getPayload()[0]);

        // opening
        index++;
        serviceParameter = serviceParameters.get(index);
        assertEquals(TagClass.CONTEXT_SPECIFIC_TAG, serviceParameter.getTagClass());
        assertEquals(1, serviceParameter.getTagNumber());
        assertEquals(ServiceParameter.CLOSING_TAG_CODE, serviceParameter.getLengthValueType());

        // object identifier service parameter
        index++;
        objectIdentifier = (ObjectIdentifierServiceParameter) serviceParameters.get(index);
        assertEquals(ObjectType.BINARY_INPUT, objectIdentifier.getObjectType());
        assertEquals(2, objectIdentifier.getInstanceNumber());

        // opening
        index++;
        serviceParameter = serviceParameters.get(index);
        assertEquals(TagClass.CONTEXT_SPECIFIC_TAG, serviceParameter.getTagClass());
        assertEquals(1, serviceParameter.getTagNumber());
        assertEquals(ServiceParameter.OPENING_TAG_CODE, serviceParameter.getLengthValueType());

        index++;
        serviceParameter = serviceParameters.get(index);
        assertEquals(TagClass.CONTEXT_SPECIFIC_TAG, serviceParameter.getTagClass());
        assertEquals(0, serviceParameter.getTagNumber());
        assertEquals(1, serviceParameter.getLengthValueType());
        assertEquals(DevicePropertyType.PRESENT_VALUE.getCode(), serviceParameter.getPayload()[0]);

        index++;
        serviceParameter = serviceParameters.get(index);
        assertEquals(TagClass.CONTEXT_SPECIFIC_TAG, serviceParameter.getTagClass());
        assertEquals(0, serviceParameter.getTagNumber());
        assertEquals(1, serviceParameter.getLengthValueType());
        assertEquals(DevicePropertyType.STATUS_FLAGS.getCode(), serviceParameter.getPayload()[0]);

        // opening
        index++;
        serviceParameter = serviceParameters.get(index);
        assertEquals(TagClass.CONTEXT_SPECIFIC_TAG, serviceParameter.getTagClass());
        assertEquals(1, serviceParameter.getTagNumber());
        assertEquals(ServiceParameter.CLOSING_TAG_CODE, serviceParameter.getLengthValueType());

        // object identifier service parameter
        index++;
        objectIdentifier = (ObjectIdentifierServiceParameter) serviceParameters.get(index);
        assertEquals(ObjectType.BINARY_INPUT, objectIdentifier.getObjectType());
        assertEquals(3, objectIdentifier.getInstanceNumber());

        // opening
        index++;
        serviceParameter = serviceParameters.get(index);
        assertEquals(TagClass.CONTEXT_SPECIFIC_TAG, serviceParameter.getTagClass());
        assertEquals(1, serviceParameter.getTagNumber());
        assertEquals(ServiceParameter.OPENING_TAG_CODE, serviceParameter.getLengthValueType());

        index++;
        serviceParameter = serviceParameters.get(index);
        assertEquals(TagClass.CONTEXT_SPECIFIC_TAG, serviceParameter.getTagClass());
        assertEquals(0, serviceParameter.getTagNumber());
        assertEquals(1, serviceParameter.getLengthValueType());
        assertEquals(DevicePropertyType.PRESENT_VALUE.getCode(), serviceParameter.getPayload()[0]);

        index++;
        serviceParameter = serviceParameters.get(index);
        assertEquals(TagClass.CONTEXT_SPECIFIC_TAG, serviceParameter.getTagClass());
        assertEquals(0, serviceParameter.getTagNumber());
        assertEquals(1, serviceParameter.getLengthValueType());
        assertEquals(DevicePropertyType.STATUS_FLAGS.getCode(), serviceParameter.getPayload()[0]);

        // opening
        index++;
        serviceParameter = serviceParameters.get(index);
        assertEquals(TagClass.CONTEXT_SPECIFIC_TAG, serviceParameter.getTagClass());
        assertEquals(1, serviceParameter.getTagNumber());
        assertEquals(ServiceParameter.CLOSING_TAG_CODE, serviceParameter.getLengthValueType());
    }

    /**
     * See bacnet_active_cov_subscriptions_real_answer.pcapng - message no. 2694
     */
    @Test
    public void testDeserializeReadPropertyMultiple_4() {

        final byte[] hexStringToByteArray = APIUtils
                .hexStringToByteArray("0233530e0c020000191e0955096f1f0c03c000321e0955096f1f0c04c000051e096f09551f");

        final APDU apdu = new APDU();
        apdu.fromBytes(hexStringToByteArray, 0, hexStringToByteArray.length);
        apdu.processPayload(apdu.getPayload(), 0, apdu.getPayload().length, 0);

        assertEquals(PDUType.CONFIRMED_SERVICE_REQUEST_PDU, apdu.getPduType());
        assertFalse(apdu.isSegmentation());
        assertFalse(apdu.isMoreSegmentsFollow());
        assertTrue(apdu.isSegmentedResponseAccepted());

        assertEquals(ConfirmedServiceChoice.READ_PROPERTY_MULTIPLE, apdu.getConfirmedServiceChoice());

        final List<ServiceParameter> serviceParameters = apdu.getServiceParameters();
        assertEquals(15, serviceParameters.size());

        int index = -1;

        // object identifier service parameter
        index++;
        ObjectIdentifierServiceParameter objectIdentifier = (ObjectIdentifierServiceParameter) serviceParameters
                .get(index);
        assertEquals(ObjectType.DEVICE, objectIdentifier.getObjectType());
        assertEquals(25, objectIdentifier.getInstanceNumber());

        // opening
        index++;
        ServiceParameter serviceParameter = serviceParameters.get(index);
        assertEquals(TagClass.CONTEXT_SPECIFIC_TAG, serviceParameter.getTagClass());
        assertEquals(1, serviceParameter.getTagNumber());
        assertEquals(ServiceParameter.OPENING_TAG_CODE, serviceParameter.getLengthValueType());

        index++;
        serviceParameter = serviceParameters.get(index);
        assertEquals(TagClass.CONTEXT_SPECIFIC_TAG, serviceParameter.getTagClass());
        assertEquals(0, serviceParameter.getTagNumber());
        assertEquals(1, serviceParameter.getLengthValueType());
        assertEquals(DevicePropertyType.PRESENT_VALUE.getCode(), serviceParameter.getPayload()[0]);

        index++;
        serviceParameter = serviceParameters.get(index);
        assertEquals(TagClass.CONTEXT_SPECIFIC_TAG, serviceParameter.getTagClass());
        assertEquals(0, serviceParameter.getTagNumber());
        assertEquals(1, serviceParameter.getLengthValueType());
        assertEquals(DevicePropertyType.STATUS_FLAGS.getCode(), serviceParameter.getPayload()[0]);

        // opening
        index++;
        serviceParameter = serviceParameters.get(index);
        assertEquals(TagClass.CONTEXT_SPECIFIC_TAG, serviceParameter.getTagClass());
        assertEquals(1, serviceParameter.getTagNumber());
        assertEquals(ServiceParameter.CLOSING_TAG_CODE, serviceParameter.getLengthValueType());

        // object identifier service parameter
        index++;
        objectIdentifier = (ObjectIdentifierServiceParameter) serviceParameters.get(index);
        assertEquals(ObjectType.NOTIFICATION_CLASS, objectIdentifier.getObjectType());
        assertEquals(50, objectIdentifier.getInstanceNumber());

        // opening
        index++;
        serviceParameter = serviceParameters.get(index);
        assertEquals(TagClass.CONTEXT_SPECIFIC_TAG, serviceParameter.getTagClass());
        assertEquals(1, serviceParameter.getTagNumber());
        assertEquals(ServiceParameter.OPENING_TAG_CODE, serviceParameter.getLengthValueType());

        index++;
        serviceParameter = serviceParameters.get(index);
        assertEquals(TagClass.CONTEXT_SPECIFIC_TAG, serviceParameter.getTagClass());
        assertEquals(0, serviceParameter.getTagNumber());
        assertEquals(1, serviceParameter.getLengthValueType());
        assertEquals(DevicePropertyType.PRESENT_VALUE.getCode(), serviceParameter.getPayload()[0]);

        index++;
        serviceParameter = serviceParameters.get(index);
        assertEquals(TagClass.CONTEXT_SPECIFIC_TAG, serviceParameter.getTagClass());
        assertEquals(0, serviceParameter.getTagNumber());
        assertEquals(1, serviceParameter.getLengthValueType());
        assertEquals(DevicePropertyType.STATUS_FLAGS.getCode(), serviceParameter.getPayload()[0]);

        // opening
        index++;
        serviceParameter = serviceParameters.get(index);
        assertEquals(TagClass.CONTEXT_SPECIFIC_TAG, serviceParameter.getTagClass());
        assertEquals(1, serviceParameter.getTagNumber());
        assertEquals(ServiceParameter.CLOSING_TAG_CODE, serviceParameter.getLengthValueType());

        // object identifier service parameter
        index++;
        objectIdentifier = (ObjectIdentifierServiceParameter) serviceParameters.get(index);
        assertEquals(ObjectType.MULTI_STATE_VALUE, objectIdentifier.getObjectType());
        assertEquals(5, objectIdentifier.getInstanceNumber());

        // opening
        index++;
        serviceParameter = serviceParameters.get(index);
        assertEquals(TagClass.CONTEXT_SPECIFIC_TAG, serviceParameter.getTagClass());
        assertEquals(1, serviceParameter.getTagNumber());
        assertEquals(ServiceParameter.OPENING_TAG_CODE, serviceParameter.getLengthValueType());

        index++;
        serviceParameter = serviceParameters.get(index);
        assertEquals(TagClass.CONTEXT_SPECIFIC_TAG, serviceParameter.getTagClass());
        assertEquals(0, serviceParameter.getTagNumber());
        assertEquals(1, serviceParameter.getLengthValueType());
        assertEquals(DevicePropertyType.STATUS_FLAGS.getCode(), serviceParameter.getPayload()[0]);

        index++;
        serviceParameter = serviceParameters.get(index);
        assertEquals(TagClass.CONTEXT_SPECIFIC_TAG, serviceParameter.getTagClass());
        assertEquals(0, serviceParameter.getTagNumber());
        assertEquals(1, serviceParameter.getLengthValueType());
        assertEquals(DevicePropertyType.PRESENT_VALUE.getCode(), serviceParameter.getPayload()[0]);

        // opening
        index++;
        serviceParameter = serviceParameters.get(index);
        assertEquals(TagClass.CONTEXT_SPECIFIC_TAG, serviceParameter.getTagClass());
        assertEquals(1, serviceParameter.getTagNumber());
        assertEquals(ServiceParameter.CLOSING_TAG_CODE, serviceParameter.getLengthValueType());
    }

    @Test
    public void testSerialize() {

        final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
        objectIdentifierServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
        objectIdentifierServiceParameter.setTagNumber(ServiceParameter.BACNET_OBJECT_IDENTIFIER);
        objectIdentifierServiceParameter.setLengthValueType(4);
        objectIdentifierServiceParameter.setObjectType(ObjectType.DEVICE);
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
        apdu.setPduType(PDUType.UNCONFIRMED_SERVICE_REQUEST_PDU);
        apdu.setUnconfirmedServiceChoice(UnconfirmedServiceChoice.I_AM);
        apdu.setVendorMap(new HashMap<Integer, String>());
        apdu.getServiceParameters().add(objectIdentifierServiceParameter);
        apdu.getServiceParameters().add(maximumAPDUServiceParameter);
        apdu.getServiceParameters().add(segmentationSupportedServiceParameter);
        apdu.getServiceParameters().add(vendorIdServiceParameter);

        final byte[] data = new byte[20];
        apdu.toBytes(data, 2);

        final byte[] expected = new byte[] { 0x00, 0x00, (byte) 0x10, (byte) 0x00, (byte) 0xc4, (byte) 0x02, 0x00, 0x27,
                0x11, 0x22, 0x01, (byte) 0xe0, (byte) 0x91, 0x00, 0x21, (byte) 0xb2, 0x00, 0x00, 0x00, 0x00 };

        System.out.println("Result:   " + APIUtils.byteArrayToStringNoPrefix(data));
        System.out.println("Expected: " + APIUtils.byteArrayToStringNoPrefix(expected));

        // 1000c4020027102201e0910021b2

        // the APDU Type serializes into a two byte long array
        assertEquals(14, apdu.getDataLength());
        assertTrue(Arrays.equals(data, expected));
    }

    @Test
    public void testDeserializeReadProperty() {

        final byte[] hexStringToByteArray = APIUtils.hexStringToByteArray("02457A0C0C020027111961");

        final APDU apdu = new APDU();
        apdu.fromBytes(hexStringToByteArray, 0, hexStringToByteArray.length);
        apdu.processPayload(apdu.getPayload(), 0, apdu.getPayload().length, 0);

        assertEquals(PDUType.UNCONFIRMED_SERVICE_REQUEST_PDU, apdu.getPduType());
        assertFalse(apdu.isSegmentation());
        assertFalse(apdu.isMoreSegmentsFollow());
        assertFalse(apdu.isSegmentedResponseAccepted());

        assertEquals(UnconfirmedServiceChoice.WHO_IS, apdu.getUnconfirmedServiceChoice());

        final List<ServiceParameter> serviceParameters = apdu.getServiceParameters();
        assertEquals(2, serviceParameters.size());
    }

    @Test
    public void testSerializeSegmentationInformation() {

        final APDU apdu = new APDU();
        apdu.setPduType(PDUType.CONFIRMED_SERVICE_REQUEST_PDU);
        apdu.setUnconfirmedServiceChoice(UnconfirmedServiceChoice.CONFIRMED_COV_NOTIFICATION);
        apdu.setSegmentedResponseAccepted(true);
        apdu.setSegmentation(false);
        apdu.setMaxResponseSegmentsAccepted(8);
        apdu.setSizeOfMaximumAPDUAccepted(3);
        apdu.setInvokeId(3);

        final byte[] data = apdu.getBytes();
        final byte[] expected = new byte[] { 0x02, 0x33, 0x03, 0x01 };

        System.out.println("Result:   " + APIUtils.byteArrayToStringNoPrefix(data));
        System.out.println("Expected: " + APIUtils.byteArrayToStringNoPrefix(expected));

        assertTrue(Arrays.equals(data, expected));
    }

    @Test
    public void testDeserialize_ComplexAck() {

        final String msg1 = "300e0c0c0040000119553e44000000003f";

        final byte[] hexStringToByteArray = APIUtils.hexStringToByteArray(msg1);

        final APDU apdu = new APDU();
        apdu.fromBytes(hexStringToByteArray, 0, hexStringToByteArray.length);
        apdu.processPayload(apdu.getPayload(), 0, apdu.getPayload().length, 0);

        assertEquals(PDUType.COMPLEX_ACK_PDU, apdu.getPduType());
        assertFalse(apdu.isSegmentation());
        assertFalse(apdu.isMoreSegmentsFollow());
        assertFalse(apdu.isSegmentedResponseAccepted());

        assertEquals(14, apdu.getInvokeId());
        assertEquals(ConfirmedServiceChoice.READ_PROPERTY, apdu.getConfirmedServiceChoice());

        final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = apdu
                .getFirstObjectIdentifierServiceParameter();
        assertEquals(ObjectType.ANALOG_OUTPUT, objectIdentifierServiceParameter.getObjectType());
        assertEquals(1, objectIdentifierServiceParameter.getInstanceNumber());

        assertEquals(DevicePropertyType.PRESENT_VALUE.getCode(), apdu.getPropertyIdentifier());

        assertEquals(5, apdu.getServiceParameters().size());

        // service parameter 1
        ServiceParameter serviceParameter = apdu.getServiceParameters().get(0);
        assertEquals(TagClass.CONTEXT_SPECIFIC_TAG, serviceParameter.getTagClass());
        assertEquals(0, serviceParameter.getTagNumber());
        assertEquals(4, serviceParameter.getLengthValueType());

        // service parameter 2
        serviceParameter = apdu.getServiceParameters().get(1);
        assertEquals(TagClass.CONTEXT_SPECIFIC_TAG, serviceParameter.getTagClass());
        assertEquals(1, serviceParameter.getTagNumber());
        assertEquals(1, serviceParameter.getLengthValueType());

        // service parameter 3 - opening tag {[3]
        serviceParameter = apdu.getServiceParameters().get(2);
        assertEquals(TagClass.CONTEXT_SPECIFIC_TAG, serviceParameter.getTagClass());
        assertEquals(3, serviceParameter.getTagNumber());
        assertEquals(6, serviceParameter.getLengthValueType());

        // service parameter 4 - REAL Payload 0x00000000
        serviceParameter = apdu.getServiceParameters().get(3);
        assertEquals(TagClass.APPLICATION_TAG, serviceParameter.getTagClass());
        assertEquals(4, serviceParameter.getTagNumber());
        assertEquals(ServiceParameter.APPLICATION_TAG_REAL, serviceParameter.getLengthValueType());
        assertEquals(4, serviceParameter.getPayload().length);
        assertEquals(0, serviceParameter.getPayload()[0]);
        assertEquals(0, serviceParameter.getPayload()[1]);
        assertEquals(0, serviceParameter.getPayload()[2]);
        assertEquals(0, serviceParameter.getPayload()[3]);

        // service parameter 5 - closing tag {[3]
        serviceParameter = apdu.getServiceParameters().get(4);
        assertEquals(TagClass.CONTEXT_SPECIFIC_TAG, serviceParameter.getTagClass());
        assertEquals(3, serviceParameter.getTagNumber());
        assertEquals(7, serviceParameter.getLengthValueType());

    }

}
