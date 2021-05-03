package de.bacnetz.stack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.bacnetz.common.APIUtils;
import de.bacnetz.devices.ObjectType;

public class ObjectIdentifierServiceParameter extends ServiceParameter {

    private static final Logger LOG = LogManager.getLogger(ObjectIdentifierServiceParameter.class);

    private ObjectType objectType;

    private int instanceNumber;

    public ObjectIdentifierServiceParameter() {
    }

    public ObjectIdentifierServiceParameter(final ServiceParameter other) {
        super(other);
        final int bufferToInt = APIUtils.bufferToInt(other.getPayload(), 0);
        objectType = ObjectType.getByCode(bufferToInt >> 22);
        instanceNumber = (bufferToInt & 0x3FFFFF);
    }

    public ObjectIdentifierServiceParameter(final ObjectIdentifierServiceParameter other) {
        super(other);
        this.objectType = other.getObjectType();
        this.instanceNumber = other.getInstanceNumber();
    }

    @Override
    public int getDataLength() {
        // 1 byte application tag
        // 4 byte ObjectType + InstanceNumber
        return 5;
    }

    @Override
    public int fromBytes(final byte[] data, final int offset) {

        final int result = super.fromBytes(data, offset);

        LOG.trace(APIUtils.byteArrayToStringNoPrefix(getPayload()));

        final int bufferToInt = APIUtils.bufferToInt(getPayload(), 0);

        objectType = ObjectType.getByCode(bufferToInt >> 22);
        instanceNumber = (bufferToInt & 0x3FFFFF);

        return result;
    }

    @Override
    public void toBytes(final byte[] data, final int offset) {

//		// the application tag is a byte that encodes the information type of this
//		// service parameter, the type of this service parameter (Application or context
//		// specific) and the length of the payload inside this service parameter
//		final int payloadLength = 4;
//		final int applicationTag = (BACNET_OBJECT_IDENTIFIER << 4) | (TagClass.APPLICATION_TAG_CODE << 3)
//				| (payloadLength);
//
//		int index = 0;
//		data[offset + index++] = (byte) applicationTag;

        // the application tag is a byte that encodes the information type of this
        // service parameter, the type of this service parameter (Application or context
        // specific) and the length of the payload inside this service parameter
        LOG.trace("TagNumber: " + getTagNumber());
        LOG.trace("TagClass " + getTagClass());
        LOG.trace("TagLengthValue: " + getLengthValueType());
        final int applicationTag = (getTagNumber() << 4) | (getTagClass().getId() << 3) | (getLengthValueType());

        int index = 0;
        data[offset + index++] = (byte) applicationTag;

        final int payload = encodeObjectTypeAndInstanceNumber(objectType, instanceNumber);

        APIUtils.intToBuffer(payload, data, offset + index);
        index += 4;
    }

    public static int encodeObjectTypeAndInstanceNumber(final ObjectType objectType, final int instanceNumber) {
        return (objectType.getCode() << 22) | instanceNumber;
    }

    public static ObjectIdentifierServiceParameter createFromTypeAndInstanceNumber(final ObjectType objectType,
            final int instanceNumber) {

        final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
        objectIdentifierServiceParameter.setTagClass(TagClass.CONTEXT_SPECIFIC_TAG);
        objectIdentifierServiceParameter.setTagNumber(0x02);
        objectIdentifierServiceParameter.setLengthValueType(4);
        objectIdentifierServiceParameter.setObjectType(objectType);
        objectIdentifierServiceParameter.setInstanceNumber(instanceNumber);

        return objectIdentifierServiceParameter;
    }

    @Override
    public String toString() {

        if (objectType == null) {
            return super.toString();
        }

        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Object Type: ").append(objectType).append("(").append(objectType.getName())
                .append(") Instance Number: ").append(instanceNumber);

        return stringBuilder.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + instanceNumber;
        result = prime * result + ((objectType == null) ? 0 : objectType.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final ObjectIdentifierServiceParameter other = (ObjectIdentifierServiceParameter) obj;
        if (instanceNumber != other.instanceNumber)
            return false;
        if (objectType != other.objectType)
            return false;
        return true;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public void setObjectType(final ObjectType objectType) {
        this.objectType = objectType;
    }

    @Override
    public int getInstanceNumber() {
        return instanceNumber;
    }

    public void setInstanceNumber(final int instanceNumber) {
        this.instanceNumber = instanceNumber;
    }

}
