package de.bacnetz.conversion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import de.bacnetz.common.utils.Utils;
import de.bacnetz.controller.DefaultMessage;
import de.bacnetz.devices.DevicePropertyType;
import de.bacnetz.devices.ObjectType;
import de.bacnetz.stack.APDU;
import de.bacnetz.stack.NPDU;
import de.bacnetz.stack.ObjectIdentifierServiceParameter;
import de.bacnetz.stack.ServiceParameter;
import de.bacnetz.stack.TagClass;
import de.bacnetz.stack.VirtualLinkControl;

public class BACnetMSTPByteArrayToMessageConverterTest {

    /**
     * 
     */
    @Test
    public void commandTest() {

        // header.frametype =
        // header.destinationAddress =
        // header.sourceAddress =
        // header.length =
        // header.crc =
        final String npduMsg = "0100";

        final String apduMsg = "300e0c0c0040000119553e44000000003f";

        final byte[] hexStringToByteArray = Utils.hexStringToByteArray(npduMsg + apduMsg);

        final BACnetMSTPByteArrayToMessageConverter byteArrayToMessageConverter = new BACnetMSTPByteArrayToMessageConverter();
        byteArrayToMessageConverter.setPayloadLength(hexStringToByteArray.length);
        byteArrayToMessageConverter.setPayloadOffset(0);

        final DefaultMessage defaultMessage = byteArrayToMessageConverter.convert(hexStringToByteArray);
        defaultMessage.getApdu().processPayload(defaultMessage.getApdu().getPayload(), 0,
                defaultMessage.getApdu().getPayload().length, 0);

//        System.out.println(defaultMessage);

        // BACnet MS/TP has no virtual link control
        final VirtualLinkControl virtualLinkControl = defaultMessage.getVirtualLinkControl();
        assertNull(virtualLinkControl);

        final NPDU npdu = defaultMessage.getNpdu();
        assertEquals(1, npdu.getVersion());
        assertEquals(0, npdu.getControl());

        final APDU apdu = defaultMessage.getApdu();

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
