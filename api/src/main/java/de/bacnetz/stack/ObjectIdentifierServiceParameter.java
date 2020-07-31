package de.bacnetz.stack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.bacnetz.common.Utils;

public class ObjectIdentifierServiceParameter extends ServiceParameter {

	private static final Logger LOG = LogManager.getLogger(ObjectIdentifierServiceParameter.class);

	public static final int OBJECT_TYPE_BINARY_INPUT = 3;

	public static final int OBJECT_TYPE_DEVICE = 8;

	public static final int OBJECT_TYPE_NOTIFICATION_CLASS = 15;

	public static final int OBJECT_TYPE_MULTI_STATE_VALUE = 19;

	private int objectType;

	private int instanceNumber;

	public ObjectIdentifierServiceParameter() {
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

		LOG.trace(Utils.byteArrayToStringNoPrefix(getPayload()));

		final int bufferToInt = Utils.bufferToInt(getPayload(), 0);

		objectType = (bufferToInt >> 22);
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
		final int applicationTag = (getTagNumber() << 4) | (getTagClass().getId() << 3) | (getLengthValueType());

		int index = 0;
		data[offset + index++] = (byte) applicationTag;

//		final int payload = (OBJECT_TYPE_DEVICE << 22) | instanceNumber;
		final int payload = (objectType << 22) | instanceNumber;

		Utils.intToBuffer(payload, data, offset + index);
		index += 4;
	}

	@Override
	public String toString() {

		final StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Object Type: ").append(objectType).append(" Instance Number: ").append(instanceNumber);

		return stringBuilder.toString();
	}

	public int getObjectType() {
		return objectType;
	}

	public void setObjectType(final int objectType) {
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
