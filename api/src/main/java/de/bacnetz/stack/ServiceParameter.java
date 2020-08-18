package de.bacnetz.stack;

import org.apache.commons.lang3.ArrayUtils;

import de.bacnet.common.APIUtils;
import de.bacnetz.devices.DevicePropertyType;
import de.bacnetz.devices.ObjectType;
import de.bacnetz.factory.MessageType;

public class ServiceParameter {

    public static final int CLOSING_TAG_CODE = 7;

    public static final int OPENING_TAG_CODE = 6;

    public static final int EXTENDED_TAG_CODE = 5;

    public static final int ENUMERATED_CODE = 9;

    public static final int UNKOWN_TAG_NUMBER = 1;

    public static final int UNSIGNED_INTEGER_CODE = 2;

    public static final int BOOLEAN_CODE = 1;

    public static final int BACNET_OBJECT_IDENTIFIER = 12;

    public static final int EXTENDED_VALUE = 0x05;

    public static final int DATE = 0x0A;

    public static final int TIME = 0x0B;

    public static final int APPLICATION_TAG_NUMBER_CHARACTER_STRING = 7;

    public static final int APPLICATION_TAG_NUMBER_BIT_STRING = 8;

    public static final int SIGNED_INTEGER_TWOS_COMMPLEMENT_NOTATION = 3;

    private int tagNumber;

    private TagClass tagClass;

    private int lengthValueType;

    private byte[] payload;

    private MessageType messageType;

    public ServiceParameter() {
    }

    public ServiceParameter(final ServiceParameter other) {
        this.tagNumber = other.tagNumber;
        this.tagClass = other.tagClass;
        this.lengthValueType = other.lengthValueType;
        if (other.payload != null) {
            this.payload = other.payload.clone();
        }
    }

    /**
     * 
     * @param data
     * @param offset
     * 
     * @return number of bytes processed.
     */
    public int fromBytes(final byte[] data, final int offset) {

        int length = 0;

        // context tag number are upper four bit
        tagNumber = (data[offset + 0] & 0xF0) >> 4;

        // tag class is bit number three
        tagClass = TagClass.fromInt((data[offset + 0] & 0x08) >> 3);

        // lower three bits are either a length value or a type value
        lengthValueType = (data[offset + 0] & 0x07) >> 0;

        length++;

        if (tagNumber == 0 && tagClass == null && lengthValueType == 0) {
            return 0;
        }

        if (lengthValueType == OPENING_TAG_CODE) {

            // tag number 6 is opening bracket without any payload

        } else if (lengthValueType == CLOSING_TAG_CODE) {

            // tag number 7 is closing bracket without any payload

        } else if (lengthValueType == EXTENDED_TAG_CODE) {

            final int payloadLength = data[offset + 1];
            length++;

            payload = new byte[payloadLength];
            System.arraycopy(data, offset + 2, payload, 0, payloadLength);

            length += payloadLength;

        } else {

            payload = new byte[lengthValueType];
            System.arraycopy(data, offset + 1, payload, 0, lengthValueType);

            length += lengthValueType;

        }

        return length;
    }

    @Override
    public String toString() {

        final StringBuffer stringBuffer = new StringBuffer();

//        // application or context
//        switch (tagClass) {
//        case APPLICATION_TAG:
//            stringBuffer.append("[APPLICATION_TAG]");
//            break;
//
//        case CONTEXT_SPECIFIC_TAG:
//            stringBuffer.append("[CONTEXT_SPECIFIC_TAG]");
//            break;
//
//        default:
//            stringBuffer.append("[UNKNOWN_TAG_CLASS:").append(tagClass).append("]");
//        }
//
//        // device property
//        stringBuffer.append("[DeviceProperty:").append(DevicePropertyType.getByCode(tagNumber).getName()).append("]");

        switch (tagClass) {
        case APPLICATION_TAG:

            stringBuffer.append("[APPLICATION_TAG]");

            switch (tagNumber) {

            case UNSIGNED_INTEGER_CODE:
                stringBuffer.append("Unsigned Integer (2)");
                break;

            case ENUMERATED_CODE:
                stringBuffer.append("Enumerated (9)");
                break;

            case BACNET_OBJECT_IDENTIFIER:
                stringBuffer.append("BACnetObjectIdentifier (12)");
                // the first ten bit contain the type of object this object identifier describes
                int objectType = (payload[0] & 0xFF) << 2;
                objectType += (payload[1] & 0xC0) >> 6;

                switch (objectType) {

                case ObjectType.BINARY_INPUT_CODE:
                    stringBuffer.append(", ObjectType: binary-input");
                    break;

                case ObjectType.DEVICE_CODE:
                    stringBuffer.append(", ObjectType: device");
                    break;

                case ObjectType.NOTIFICATION_CLASS_CODE:
                    stringBuffer.append(", ObjectType: notification-class");
                    break;

                case ObjectType.MULTI_STATE_VALUE_CODE:
                    stringBuffer.append(", ObjectType: multi-state-value");
                    break;

                default:
                    throw new RuntimeException("Unknown ObjectType: " + objectType);
                }

                final int instanceNumber = getInstanceNumber();
                stringBuffer.append(", InstanceNumber: " + instanceNumber);
                break;

            default:
                stringBuffer.append("Unknown Application Tag: ").append(tagNumber).append("]");
            }
            break;

        case CONTEXT_SPECIFIC_TAG:

            stringBuffer.append("[CONTEXT_SPECIFIC_TAG]");

            switch (lengthValueType) {

            case 1:
                stringBuffer.append("[DeviceProperty:").append(DevicePropertyType.getByCode(payload[0]).getName())
                        .append("]");
                break;

            case ServiceParameter.OPENING_TAG_CODE:
                stringBuffer.append("{[").append(tagNumber).append("]");
                break;

            case ServiceParameter.CLOSING_TAG_CODE:
                stringBuffer.append("}[").append(tagNumber).append("]");
                break;

            default:
                stringBuffer.append("[Unknown Context Specific Tag: ").append(lengthValueType).append("]");
            }

            break;

        default:
            stringBuffer.append("[UNKNOWN_TAG_CLASS:").append(tagClass).append("]");
            break;
        }

        return stringBuffer.toString();
    }

    @SuppressWarnings("unused")
    private void outputPayload(final StringBuffer stringBuffer) {

        if (ArrayUtils.isEmpty(payload)) {

            // nop

        } else if (payload.length == 1) {

            stringBuffer.append(APIUtils.byteArrayToStringNoPrefix(payload));

        } else if (payload.length == 2) {

            final boolean bigEndian = true;
            stringBuffer.append(APIUtils.bytesToUnsignedShort(payload[0], payload[1], bigEndian));
        }
    }

    public int getInstanceNumber() {

        if (ArrayUtils.isEmpty(payload)) {
            return -1;
        }

        int instanceNumber = (payload[1] & 0x3F) << 16;
        instanceNumber += (payload[2] & 0xFF) << 8;
        instanceNumber += (payload[3] & 0xFF) << 0;

        return instanceNumber;
    }

    public int getDataLength() {

        if (tagClass == TagClass.CONTEXT_SPECIFIC_TAG) {

            if ((lengthValueType == OPENING_TAG_CODE) || (lengthValueType == CLOSING_TAG_CODE)) {
                return 1;
            }

        } else if (tagClass == TagClass.APPLICATION_TAG) {

            if (lengthValueType == ServiceParameter.EXTENDED_VALUE) {

                return getPayload().length + 2;

            } else if (messageType != null && messageType == MessageType.BOOLEAN) {

                return 1;

            }
        }

        return lengthValueType + 1;
    }

    public void toBytes(final byte[] data, final int offset) {

        int index = 0;

        // the application tag is a byte that encodes the information type of this
        // service parameter, the type of this service parameter (Application or context
        // specific) and the length of the payload inside this service parameter
        final int applicationTag = (tagNumber << 4) | (tagClass.getId() << 3) | (lengthValueType);
        data[offset + index++] = (byte) applicationTag;

        // copy the payload in
        if (ArrayUtils.isNotEmpty(payload)) {

            // for extended values, preface the actual payload with the payload's length
            if (lengthValueType == ServiceParameter.EXTENDED_VALUE) {
                data[offset + index++] = (byte) (payload.length);
            }

            System.arraycopy(payload, 0, data, offset + index, payload.length);
            index += payload.length;
        }
    }

    public int getTagNumber() {
        return tagNumber;
    }

    public void setTagNumber(final int tagNumber) {
        this.tagNumber = tagNumber;
    }

    public TagClass getTagClass() {
        return tagClass;
    }

    public void setTagClass(final TagClass tagClass) {
        if (tagClass == null) {
            throw new RuntimeException("Invalid");
        }
        this.tagClass = tagClass;
    }

    public int getLengthValueType() {
        return lengthValueType;
    }

    public void setLengthValueType(final int lengthValueType) {
        this.lengthValueType = lengthValueType;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(final byte[] payload) {
        this.payload = payload;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(final MessageType messageType) {
        this.messageType = messageType;
    }

}
