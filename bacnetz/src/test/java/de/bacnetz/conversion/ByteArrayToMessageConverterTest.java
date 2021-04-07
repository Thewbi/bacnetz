package de.bacnetz.conversion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import de.bacnetz.common.utils.Utils;
import de.bacnetz.controller.DefaultMessage;
import de.bacnetz.devices.DevicePropertyType;
import de.bacnetz.devices.ObjectType;
import de.bacnetz.stack.ConfirmedServiceChoice;
import de.bacnetz.stack.ObjectIdentifierServiceParameter;
import de.bacnetz.stack.ServiceParameter;
import de.bacnetz.stack.TagClass;

public class ByteArrayToMessageConverterTest {

    /**
     * The data encoded as hex is the response to a object list request towards a
     * BACnet device object. It is not a regular object list request, but the object
     * list request for index 0, which in BACnet has a special meaning. Index 0
     * contains the length of the object list. That means if you query the object
     * list property at index 0 specifically, the BACnet device object will answer
     * not with the objects but with the amount of object, namely the size of the
     * object list.<br />
     * <br />
     * 
     * The response consists of five service parameters stored inside the APDU. The
     * service parameters in that exact order are:
     * 
     * <ol>
     * <li />object identifier
     * <li />property identifier
     * <li />property Array Index
     * <li />opening bracket (management information)
     * <li />object-list values (The actual payload, the length of the object list
     * in this case!)
     * <li />closing bracket (management information)
     * </ol>
     * 
     * This test looks at each individual service parameter and makes sure the
     * parses parses correctly.
     */
    @Test
    public void testParseReadPropertyResponse() {

        // 81 0A 00 16 01 00 30 01 0C 0C 02 38 A2 8E 19 4C 29 00 3E 21 03 3f
        final byte[] hexStringToByteArray = Utils.hexStringToByteArray("810A0016010030010C0C0238A28E194C29003E21033f");

        final ByteArrayToMessageConverter byteArrayToMessageConverter = new ByteArrayToMessageConverter();
        byteArrayToMessageConverter.setPayloadOffset(0);
        byteArrayToMessageConverter.setPayloadLength(22);
        final DefaultMessage defaultMessage = byteArrayToMessageConverter.convert(hexStringToByteArray);

        // invoke is 1
        assertEquals(1, defaultMessage.getApdu().getInvokeId());

        // service choice is readProperty
        assertNull(defaultMessage.getApdu().getUnconfirmedServiceChoice());
        assertEquals(ConfirmedServiceChoice.READ_PROPERTY, defaultMessage.getApdu().getConfirmedServiceChoice());

        // object identifier (context tag)
        final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = defaultMessage.getApdu()
                .getFirstObjectIdentifierServiceParameter();
        // context tag number 0
        assertEquals(TagClass.CONTEXT_SPECIFIC_TAG, objectIdentifierServiceParameter.getTagClass());
        assertEquals(0, objectIdentifierServiceParameter.getTagNumber());
        assertEquals(3711630, objectIdentifierServiceParameter.getInstanceNumber());
        assertEquals(ObjectType.DEVICE, objectIdentifierServiceParameter.getObjectType());

        // property identifier: 'object list' (context tag)
        ServiceParameter serviceParameter = defaultMessage.getApdu().getServiceParameters().get(1);
        assertEquals(TagClass.CONTEXT_SPECIFIC_TAG, serviceParameter.getTagClass());
        // context tag number 1
        assertEquals(1, serviceParameter.getTagNumber());
        // the first byte of the payload stores the property identifier. 76d is
        // object-list
        assertEquals(DevicePropertyType.OBJECT_LIST, DevicePropertyType.getByCode(serviceParameter.getPayload()[0]));

        // property array index (context tag)
        serviceParameter = defaultMessage.getApdu().getServiceParameters().get(2);
        assertEquals(TagClass.CONTEXT_SPECIFIC_TAG, serviceParameter.getTagClass());
        // context tag number 2
        assertEquals(2, serviceParameter.getTagNumber());
        // first byte of the payload stores the requested array index which in this
        // example has to be zero
        // requesting the array index 0 means, requesting the length of the object
        // array! BACnet wierdness galore.
        assertEquals(0, serviceParameter.getPayload()[0]);

        // {[3] opening-tag (context tag)
        serviceParameter = defaultMessage.getApdu().getServiceParameters().get(3);
        assertEquals(TagClass.CONTEXT_SPECIFIC_TAG, serviceParameter.getTagClass());
        // context tag number 3
        assertEquals(3, serviceParameter.getTagNumber());
        // opening tag
        assertEquals(ServiceParameter.OPENING_TAG_CODE, serviceParameter.getLengthValueType());

        // object-list (application tag)
        serviceParameter = defaultMessage.getApdu().getServiceParameters().get(4);
        assertEquals(TagClass.APPLICATION_TAG, serviceParameter.getTagClass());
        // application tag number has to be 2
        assertEquals(2, serviceParameter.getTagNumber());
        // the payload contains the value 3, which is the length of the object list in
        // this example
        assertEquals(3, serviceParameter.getPayload()[0]);

        // [3]} closing-tag (context tag)
        serviceParameter = defaultMessage.getApdu().getServiceParameters().get(5);
        assertEquals(TagClass.CONTEXT_SPECIFIC_TAG, serviceParameter.getTagClass());
        // context tag number 3
        assertEquals(3, serviceParameter.getTagNumber());
        // closing tag
        assertEquals(ServiceParameter.CLOSING_TAG_CODE, serviceParameter.getLengthValueType());

    }

}
