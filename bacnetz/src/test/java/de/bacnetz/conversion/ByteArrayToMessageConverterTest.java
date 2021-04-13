package de.bacnetz.conversion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import de.bacnetz.common.utils.Utils;
import de.bacnetz.controller.DefaultMessage;
import de.bacnetz.devices.DevicePropertyType;
import de.bacnetz.devices.ObjectType;
import de.bacnetz.devices.SystemStatus;
import de.bacnetz.stack.ConfirmedServiceChoice;
import de.bacnetz.stack.ObjectIdentifierServiceParameter;
import de.bacnetz.stack.ServiceParameter;
import de.bacnetz.stack.TagClass;

public class ByteArrayToMessageConverterTest {

    private static final Logger LOG = LogManager.getLogger(ByteArrayToMessageConverterTest.class);

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

        LOG.info(defaultMessage);

    }

    /**
     * 81 0A 00 16 01 00 30 01 0E 0C 02 38 A2 8E 1E 29 70 4E 91 00 4F 1F
     * 
     * <pre>
     * Virtual Link Control
     * 81 0A 00 16 
     * 
     * NPDU
     * 01 00 
     * 
     * APDU
     * 30 01 0E 0C 02 38 A2 8E 1E 29 70 4E 91 00 4F 1F
     * 
     * APDU - Details:
     * 30 - Complex Ack + PDU Flags 0
     * 01 - Invoke ID (Here: 1)
     * 0E - Service Choice (Here: readPropertyMultiple (14))
     * APDU-ObjectIdentifier ServiceParameter
     * 0C 02 38 A2 8E 
     * 
     * Service Parameter - {[1]
     * 1E 
     * 
     * Service Parameter - system-status (112)
     * 29 70 
     * 
     * Service Parameter - }[4]
     * 4E 
     * 
     * Service Parameter - system-status operational
     * 91 00 
     * 
     * Service Parameter - }[4]
     * 4F 
     * 
     * Service Parameter - }[1]
     * 1F
     * </pre>
     */
    @Test
    public void testParseReadPropertyMultipleResponse_SystemStatus() {

        // 81 0A 00 16 01 00 30 01 0E 0C 02 38 A2 8E 1E 29 70 4E 91 00 4F 1F
        final byte[] hexStringToByteArray = Utils.hexStringToByteArray("810A0016010030010E0C0238A28E1E29704E91004F1F");

        final ByteArrayToMessageConverter byteArrayToMessageConverter = new ByteArrayToMessageConverter();
        byteArrayToMessageConverter.setPayloadOffset(0);
        byteArrayToMessageConverter.setPayloadLength(22);
        final DefaultMessage defaultMessage = byteArrayToMessageConverter.convert(hexStringToByteArray);

        // the 4th service parameter is the value of the device's system-status
        final ServiceParameter systemStatusServiceParameter = defaultMessage.getApdu().getServiceParameters().get(4);

        // the system status is encoded in a single byte
        final byte systemStatusValue = systemStatusServiceParameter.getPayload()[0];

        assertEquals(SystemStatus.OPERATIONAL, SystemStatus.getByCode(systemStatusValue));

    }

    /**
     * 81 0A 01 B7 01 00 30 01 0E 0C 02 38 A2 8E 1E 29 1C 4E 71 00 4F 29 3A 4E 71 00
     * 4F 29 4D 4E 75 11 00 42 65 63 6B 68 6F 66 66 5F 33 37 31 31 36 33 30 4F 29 4B
     * 4E C4 02 38 A2 8E 4F 29 4F 4E 91 08 4F 2A 01 52 4E 91 00 4F 2A 01 54 4E 21 01
     * 4F 2A 01 55 4E 21 05 4F 2A 01 53 4E 21 05 4F 2A 02 06 4E 75 0C 00 31 39 32 2E
     * 31 36 38 2E 32 2E 32 4F 29 CB 4E 2E A4 79 04 0C 01 B4 0E 26 22 11 2F 4F 29 C4
     * 4E 91 00 4F 29 C3 4E 21 00 4F 29 C1 4E 10 4F 29 CC 4E 21 00 4F 29 CE 4E 4F 29
     * 74 4E 4F 29 D1 4E 4F 29 CA 4E 4F 29 9A 4E 4F 29 98 4E 4F 29 1E 4E 4F 29 4C 4E
     * C4 02 38 A2 8E C4 02 80 00 00 C4 02 80 00 01 4F 29 99 4E 21 3C 4F 29 9D 4E 2E
     * A4 FF FF FF FF B4 FF FF FF FF 2F 4F 29 9B 4E 21 00 4F 29 49 4E 21 03 4F 29 0B
     * 4E 22 0B B8 4F 29 0A 4E 22 07 D0 4F 29 18 4E 11 4F 29 77 4E 34 FF FF FF C4 4F
     * 29 38 4E A4 79 04 0C 01 4F 29 39 4E B4 0E 27 15 51 4F 29 62 4E 21 01 4F 29 79
     * 4E 75 19 00 42 65 63 6B 68 6F 66 66 20 41 75 74 6F 6D 61 74 69 6F 6E 20 47 6D
     * 62 48 4F 29 0C 4E 75 09 00 34 2E 30 2E 31 28 30 29 4F 29 2C 4E 75 09 00 34 2E
     * 30 2E 31 28 30 29 4F 29 46 4E 75 16 00 42 41 43 6E 65 74 2F 49 50 20 66 6F 72
     * 20 54 77 69 6E 43 41 54 4F 29 78 4E 22 01 9F 4F 29 A7 4E 21 FF 4F 29 6B 4E 91
     * 00 4F 29 3E 4E 22 05 C4 4F 29 60 4E 85 08 01 FF FF F9 D4 01 FF E0 4F 29 61 4E
     * 85 07 07 FF FB F8 2D FB 00 4F 29 8B 4E 21 0E 4F 29 70 4E 91 00 4F 1F
     */
    @Test
    public void testParseReadPropertyMultipleResponse_All() {

        final byte[] hexStringToByteArray = Utils.hexStringToByteArray(
                "810A01B7010030010E0C0238A28E1E291C4E71004F293A4E71004F294D4E7511004265636B686F66665F333731313633304F294B4EC40238A28E4F294F4E91084F2A01524E91004F2A01544E21014F2A01554E21054F2A01534E21054F2A02064E750C003139322E3136382E322E324F29CB4E2EA479040C01B40E2622112F4F29C44E91004F29C34E21004F29C14E104F29CC4E21004F29CE4E4F29744E4F29D14E4F29CA4E4F299A4E4F29984E4F291E4E4F294C4EC40238A28EC402800000C4028000014F29994E213C4F299D4E2EA4FFFFFFFFB4FFFFFFFF2F4F299B4E21004F29494E21034F290B4E220BB84F290A4E2207D04F29184E114F29774E34FFFFFFC44F29384EA479040C014F29394EB40E2715514F29624E21014F29794E7519004265636B686F6666204175746F6D6174696F6E20476D62484F290C4E750900342E302E312830294F292C4E750900342E302E312830294F29464E7516004241436E65742F495020666F72205477696E4341544F29784E22019F4F29A74E21FF4F296B4E91004F293E4E2205C44F29604E850801FFFFF9D401FFE04F29614E850707FFFBF82DFB004F298B4E210E4F29704E91004F1F");

        final ByteArrayToMessageConverter byteArrayToMessageConverter = new ByteArrayToMessageConverter();
        byteArrayToMessageConverter.setPayloadOffset(0);
        byteArrayToMessageConverter.setPayloadLength(22);
        final DefaultMessage defaultMessage = byteArrayToMessageConverter.convert(hexStringToByteArray);

        // the 4th service parameter is the value of the device's system-status
        final ServiceParameter systemStatusServiceParameter = defaultMessage.getApdu().getServiceParameters().get(4);

        // the system status is encoded in a single byte
        final byte systemStatusValue = systemStatusServiceParameter.getPayload()[0];

        assertEquals(SystemStatus.OPERATIONAL, SystemStatus.getByCode(systemStatusValue));

    }

}
