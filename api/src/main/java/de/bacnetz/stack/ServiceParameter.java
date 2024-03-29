package de.bacnetz.stack;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.bacnetz.common.APIUtils;
import de.bacnetz.devices.DevicePropertyType;
import de.bacnetz.devices.ObjectType;
import de.bacnetz.factory.MessageType;

public class ServiceParameter {

    @SuppressWarnings("unused")
    private static final Logger LOG = LogManager.getLogger(ServiceParameter.class);

    public static final int CLOSING_TAG_CODE = 7;

    public static final int OPENING_TAG_CODE = 6;

    public static final int EXTENDED_TAG_CODE = 5;

    public static final int ENUMERATED_CODE = 9;

    public static final int UNKOWN_TAG_NUMBER = 1;

    public static final int UNSIGNED_INTEGER_CODE = 2;

    public static final int BOOLEAN_CODE = 1;

    public static final int REAL_CODE = 4;

    public static final int BACNET_OBJECT_IDENTIFIER = 12;

    public static final int EXTENDED_VALUE = 0x05;

    public static final int DATE = 0x0A;

    public static final int TIME = 0x0B;

    public static final int APPLICATION_TAG_BOOLEAN = 0x01;

    public static final int APPLICATION_TAG_REAL = 0x04;

    public static final int APPLICATION_TAG_BIT_STRING = 0x08;

    public static final int APPLICATION_TAG_DATE = 0x0A;

    public static final int APPLICATION_TAG_TIME = 0x0B;

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

        // tag class (ContextSpecific or Application) is bit number three
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

            try {

                payload = new byte[lengthValueType];
                System.arraycopy(data, offset + 1, payload, 0, lengthValueType);

                length += lengthValueType;

            } catch (final Exception e) {
                LOG.error("lengthValueType: {}, offset: {}, payload: {}, data: {}", lengthValueType, offset, payload,
                        APIUtils.bytesToHex(data));

                LOG.error(e.getMessage(), e);
                throw e;
            }

        }

        return length;
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

    @Override
    public String toString() {

        final StringBuffer stringBuffer = new StringBuffer();

        switch (tagClass) {
        case APPLICATION_TAG:

            stringBuffer.append("[APPLICATION_TAG]");

            switch (tagNumber) {

            // 0x01 = 1d
            case APPLICATION_TAG_BOOLEAN:
                stringBuffer.append(" BOOLEAN");
                break;

            // 0x02 = 2d
            case UNSIGNED_INTEGER_CODE:
                stringBuffer.append(" Unsigned Integer (2) - VALUE: ").append("" + (payload[0] & 0xFF));
                break;

            // 0x04 = 4d
            case APPLICATION_TAG_REAL:
                stringBuffer.append(" REAL");
//                final float realValue = ByteBuffer.wrap(payload).order(ByteOrder.BIG_ENDIAN).getFloat();
//                stringBuffer.append(" Value: ").append(realValue);
                break;

            // 0x07 = 7d
            case APPLICATION_TAG_NUMBER_CHARACTER_STRING:
                String temp = new String(payload);
                temp = StringUtils.trim(temp);
                stringBuffer.append(" Character String (7) '").append(temp).append("'");
                break;

            // 0x08 = 8d
            case APPLICATION_TAG_BIT_STRING:
                stringBuffer.append(" Bit String (8) '");
                if (payload.length > 0) {
                    stringBuffer.append(" Payload/Value: ").append(APIUtils.byteArrayToStringNoPrefix(payload));
                }
                break;

            // 0x09 = 9d
            case ENUMERATED_CODE:
                stringBuffer.append(" Enumerated (9) ");
                if (payload.length > 0) {
                    stringBuffer.append(" Payload/Value: ").append(APIUtils.byteArrayToStringNoPrefix(payload));
                }
                break;

            // 0x0A = 10d
            case APPLICATION_TAG_DATE:
                stringBuffer.append(" DATE");
                break;

            // 0x0B = 11d
            case APPLICATION_TAG_TIME:
                stringBuffer.append(" TIME");
                break;

            // 0x0C = 12d
            case BACNET_OBJECT_IDENTIFIER:
                stringBuffer.append(" BACnetObjectIdentifier (12)");
                // the first ten bit contain the type of object this object identifier describes
                int objectType = (payload[0] & 0xFF) << 2;
                objectType += (payload[1] & 0xC0) >> 6;

                switch (objectType) {

                case ObjectType.ANALOG_INPUT_CODE:
                    stringBuffer.append(", ObjectType: analog-input");
                    break;

                case ObjectType.ANALOG_VALUE_CODE:
                    stringBuffer.append(", ObjectType: analog-value");
                    break;

                case ObjectType.BINARY_INPUT_CODE:
                    stringBuffer.append(", ObjectType: binary-input");
                    break;

                case ObjectType.BINARY_VALUE_CODE:
                    stringBuffer.append(", ObjectType: binary-value");
                    break;

                case ObjectType.DEVICE_CODE:
                    stringBuffer.append(", ObjectType: device");
                    break;

                case ObjectType.FILE_CODE:
                    stringBuffer.append(", ObjectType: file");
                    break;

                case ObjectType.LOOP_CODE:
                    stringBuffer.append(", ObjectType: loop");
                    break;

                case ObjectType.MULTI_STATE_INPUT_CODE:
                    stringBuffer.append(", ObjectType: multi-state-input");
                    break;

                case ObjectType.MULTI_STATE_OUTPUT_CODE:
                    stringBuffer.append(", ObjectType: multi-state-output");
                    break;

                case ObjectType.NOTIFICATION_CLASS_CODE:
                    stringBuffer.append(", ObjectType: notification-class");
                    break;

                case ObjectType.SCHEDULE_CODE:
                    stringBuffer.append(", ObjectType: multi-state-value");
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
                stringBuffer.append(" Unknown Application Tag: ").append(tagNumber).append(" ");
                if (payload.length > 0) {
                    stringBuffer.append(DevicePropertyType.getByCode(payload[0] & 0xFF));
                }
            }
            break;

        case CONTEXT_SPECIFIC_TAG:

            stringBuffer.append("[CONTEXT_SPECIFIC_TAG] lengthValueType = " + lengthValueType);

            switch (lengthValueType) {

            // 0x01 = 1d - Property or TimeRemaining
            case 0x01:
                // This is either a property or, in the case of a COV Subscription update, this
                // is the TIME-REMAINING!
                final int codeAsUnsignedInt = payload[0] & 0xff;
                try {
                    stringBuffer.append(" [DeviceProperty:")
                            .append(DevicePropertyType.getByCode(codeAsUnsignedInt).getName()).append(", Code: ")
                            .append(codeAsUnsignedInt).append("]");
                } catch (final Exception e) {
                    ; // ignored
                }
                try {
                    if (payload.length > 0) {
                        stringBuffer.append(" [TimeRemaining: ").append(APIUtils.byteArrayToStringNoPrefix(payload))
                                .append("]");
                    }
                } catch (final Exception e) {
                    ; // ignored
                }
                break;

            // 0x04 = 4d - ObjectIdentifier
            case 0x04:
                final int bufferToInt = APIUtils.bufferToInt(getPayload(), 0);
                final ObjectType objectType = ObjectType.getByCode(bufferToInt >> 22);
                final int instanceNumber = (bufferToInt & 0x3FFFFF);
                stringBuffer.append(" objectType " + objectType + " instanceNumber " + instanceNumber);
                break;

            // 0x06 = 6d
            case ServiceParameter.OPENING_TAG_CODE:
                stringBuffer.append(" {[").append(tagNumber).append("]");
                break;

            // 0x07 = 7d
            case ServiceParameter.CLOSING_TAG_CODE:
                stringBuffer.append(" }[").append(tagNumber).append("]");
                break;

            default:
                stringBuffer.append(" [Unknown Context Specific Tag: ").append(lengthValueType).append("]");
                if (lengthValueType == 1) {
                    stringBuffer.append(DevicePropertyType.getByCode(payload[0]));
                } else if (lengthValueType == 2) {
                    final int tempint = APIUtils.bytesToUnsignedShort(payload[0], payload[1], true);
                    stringBuffer.append(DevicePropertyType.getByCode(tempint));
                }
            }

            break;

        default:
            stringBuffer.append(" [UNKNOWN_TAG_CLASS:").append(tagClass).append("]");
            stringBuffer.append(DevicePropertyType.getByCode(payload[0] & 0xFF));
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
