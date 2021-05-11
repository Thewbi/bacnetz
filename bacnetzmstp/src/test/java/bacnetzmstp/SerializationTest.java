package bacnetzmstp;

import org.junit.jupiter.api.Test;

import de.bacnetz.common.utils.Utils;
import de.bacnetz.controller.DefaultMessage;
import de.bacnetz.devices.ObjectType;
import de.bacnetz.factory.DefaultMessageFactory;
import de.bacnetz.mstp.Header;

public class SerializationTest {

    /**
     * <pre>
     * final String headerHexStream = "55FF050201000D11";
     * final String npduHexStream = "0104";
     * final String apduHexStream = "0273130C0C023FFFFF194C";
     * final String footerHexStream = "37E3";
     * 
     * apdu: PDU Type: CONFIRMED_SERVICE_REQUEST_PDU
     * READ_PROPERTYConfirmedServiceChoice: READ_PROPERTY
     * Property: OBJECT_LIST (76)
     * Object Type: DEVICE(DEVICE) Instance Number: 4194303
     * [CONTEXT_SPECIFIC_TAG][DeviceProperty:object-list, Code: 76]
     * </pre>
     */
    @Test
    public void test() {

        final Header header = new Header();
        header.setFrameType(0x05);
        header.setDestinationAddress(0x02);
        header.setSourceAddress(0x01);
//        header.setLength1(length1);
//        header.setLength2(length1);

//        final NPDU npdu = new NPDU();
//        npdu.setVersion(0x01);
//        npdu.setControl(0x04);
//        
//        final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
//        objectIdentifierServiceParameter.setTagClass(TagClass.APPLICATION_TAG);
//        objectIdentifierServiceParameter.setTagNumber(ServiceParameter.BACNET_OBJECT_IDENTIFIER);
//        objectIdentifierServiceParameter.setLengthValueType(4);
//        objectIdentifierServiceParameter.setObjectType(ObjectType.DEVICE);
//        objectIdentifierServiceParameter.setInstanceNumber(4194303);
//        
//        final ServiceParameter serviceParameter = new ServiceParameter();
//        serviceParameter.setLengthValueType(lengthValueType);
//        
//     // property identifier: 'object list' (context tag)
//        assertEquals(TagClass.CONTEXT_SPECIFIC_TAG, serviceParameter.getTagClass());
//        assertEquals(1, serviceParameter.getTagNumber());
//        assertEquals(DevicePropertyType.OBJECT_LIST, DevicePropertyType.getByCode(serviceParameter.getPayload()[0]));
//
//        final APDU apdu = new APDU();
//        apdu.setInvokeId(19);
//        apdu.setConfirmedServiceChoice(ConfirmedServiceChoice.READ_PROPERTY);
//        apdu.setPduType(PDUType.CONFIRMED_SERVICE_REQUEST_PDU);
//        apdu.setPropertyIdentifier(76);

        final DefaultMessageFactory defaultMessageFactory = new DefaultMessageFactory();
        final DefaultMessage requestObjectListMessage = (DefaultMessage) defaultMessageFactory
                .requestObjectList(ObjectType.DEVICE, 4194303);

        // no virtual link control for BACnet MS/TP
        requestObjectListMessage.setVirtualLinkControl(null);
        requestObjectListMessage.getNpdu().setControl(0x04);
        requestObjectListMessage.getApdu().setInvokeId(19);
        requestObjectListMessage.getApdu().setSegmentationControl(0x73);

//      Header: 55 FF 05 02 01 00 0D 11 
//      Payload: 01 04 02 73 13 0C 0C 02 3F FF FF 19 4C 2f 91

//        final String headerHexStream = "55FF050201000D11";
//        final String npduHexStream = "0104";
//        final String apduHexStream = "0273130C0C023FFFFF194C";
//        final String footerHexStream = "37E3";

        final byte[] payloadBytes = requestObjectListMessage.getBytes();

        header.setLength1((payloadBytes.length & 0xFF00) >> 8);
        header.setLength2((payloadBytes.length & 0xFF));

        final byte[] headerBytes = header.toBytes();

        // header, with CRC
        System.out.println(Utils.bytesToHex(headerBytes));

        // payload, without CRC
        System.out.println(Utils.bytesToHex(payloadBytes));

        final int bufferSize = headerBytes.length + payloadBytes.length + 2;
        final byte[] resultBuffer = new byte[bufferSize];

        int length = 0;
        final int headerLength = header.toBytes(resultBuffer, length);
        length += headerLength;
        length += requestObjectListMessage.getNpdu().toBytes(resultBuffer, length);
        length += requestObjectListMessage.getApdu().toBytes(resultBuffer, length);

        final int dataCrc = DataCRC.getCrc(resultBuffer, length - headerLength, headerLength);

        // expected: 0x37E3 = 14307
        System.out.println(dataCrc);

        resultBuffer[length] = (byte) (dataCrc & 0xFF);
        resultBuffer[length + 1] = (byte) ((dataCrc & 0xFF00) >> 8);

        System.out.println(Utils.bytesToHex(resultBuffer));

    }

}
