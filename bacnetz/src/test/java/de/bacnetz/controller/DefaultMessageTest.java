package de.bacnetz.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashMap;

import org.junit.jupiter.api.Test;

import de.bacnetz.common.utils.Utils;
import de.bacnetz.devices.ObjectType;
import de.bacnetz.stack.APDU;
import de.bacnetz.stack.NPDU;
import de.bacnetz.stack.ObjectIdentifierServiceParameter;
import de.bacnetz.stack.PDUType;
import de.bacnetz.stack.ServiceParameter;
import de.bacnetz.stack.TagClass;
import de.bacnetz.stack.UnconfirmedServiceChoice;
import de.bacnetz.stack.VirtualLinkControl;

public class DefaultMessageTest {

    @Test
    public void testSerialize_IAM_10001() {

        final int deviceInstanceNumber = 10001;

        final VirtualLinkControl virtualLinkControl = new VirtualLinkControl();
        virtualLinkControl.setType(0x81);
        virtualLinkControl.setFunction(0x0B);
        virtualLinkControl.setLength(0x14);

        final NPDU npdu = new NPDU();
        npdu.setVersion(0x01);
        npdu.setControl(0x00);

        final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
        objectIdentifierServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
        objectIdentifierServiceParameter.setTagNumber(ServiceParameter.BACNET_OBJECT_IDENTIFIER);
        objectIdentifierServiceParameter.setLengthValueType(4);
        objectIdentifierServiceParameter.setObjectType(ObjectType.DEVICE);
        objectIdentifierServiceParameter.setInstanceNumber(deviceInstanceNumber);

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
        apdu.setVendorMap(new HashMap<>());
        apdu.setInvokeId(-2);
        apdu.getServiceParameters().add(objectIdentifierServiceParameter);
        apdu.getServiceParameters().add(maximumAPDUServiceParameter);
        apdu.getServiceParameters().add(segmentationSupportedServiceParameter);
        apdu.getServiceParameters().add(vendorIdServiceParameter);

        final DefaultMessage defaultMessage = new DefaultMessage();
        defaultMessage.setVirtualLinkControl(virtualLinkControl);
        defaultMessage.setNpdu(npdu);
        defaultMessage.setApdu(apdu);

        final byte[] bytes = defaultMessage.getBytes();

        // 81 0B 00 14 01 00 10 00 C4 02 00 27 11 22 01 E0 91 00 21 B2
        final byte[] expected = new byte[] { (byte) 0x81, 0x0B, (byte) 0x00, (byte) 0x14, (byte) 0x01, (byte) 0x00,
                0x10, 0x00, (byte) 0xC4, 0x02, 0x00, (byte) 0x27, (byte) 0x11, 0x22, (byte) 0x01, (byte) 0xE0,
                (byte) 0x91, 0x00, 0x21, (byte) 0xB2 };

        System.out.println("Result:   " + Utils.byteArrayToStringNoPrefix(bytes));
        System.out.println("Expected: " + Utils.byteArrayToStringNoPrefix(expected));

        assertTrue(Arrays.equals(bytes, expected));
    }

}
