package de.bacnetz.stack;

import de.bacnetz.common.Utils;

public class ServiceParameter {

	public static final int ENUMERATED = 9;

	public static final int UNSIGNED_INTEGER = 2;

	public static final int BACNET_OBJECT_IDENTIFIER = 12;

	public static final int CONTEXT_SPECIFIC_TAG = 1;

	public static final int APPLICATION_TAG = 0;

	private int tagNumber;

	private int classValue;

	private int lengthValueType;

	private byte[] payload;

	/**
	 * 
	 * @param data
	 * @param offset
	 * 
	 * @return number of bytes processed.
	 */
	public int fromBytes(final byte[] data, final int offset) {

		tagNumber = (data[offset + 0] & 0xF0) >> 4;
		classValue = (data[offset + 0] & 0x08) >> 3;
		lengthValueType = (data[offset + 0] & 0x07) >> 0;

		if (tagNumber == 0 && classValue == 0 && lengthValueType == 0) {
			return 0;
		}

		payload = new byte[lengthValueType];
		System.arraycopy(data, offset + 1, payload, 0, lengthValueType);

		return lengthValueType + 1;
	}

	@Override
	public String toString() {

		final StringBuffer stringBuffer = new StringBuffer();

		if (classValue == APPLICATION_TAG) {

			switch (tagNumber) {

			case UNSIGNED_INTEGER:
				stringBuffer.append("Unsigned Integer (2)");
				break;

			case ENUMERATED:
				stringBuffer.append("Enumerated (9)");
				break;

			case BACNET_OBJECT_IDENTIFIER:
				stringBuffer.append("BACnetObjectIdentifier (12)");
				// the first ten bit contain the type of object this object identifier describes
				int objectType = (payload[0] & 0xFF) << 2;
				objectType += (payload[1] & 0xC0) >> 6;

				switch (objectType) {
				case 0x08:
					stringBuffer.append(", ObjectType: Object");
					break;

				default:
					throw new RuntimeException("Unknown ObjectType: " + objectType);
				}

				int instanceNumber = (payload[1] & 0x3F) << 16;
				instanceNumber += (payload[2] & 0xFF) << 8;
				instanceNumber += (payload[3] & 0xFF) << 0;
				stringBuffer.append(", InstanceNumber: " + instanceNumber);
				break;

			default:
				throw new RuntimeException("Unknown Application Tag: " + tagNumber);
			}

		} else if (classValue == CONTEXT_SPECIFIC_TAG) {

			final boolean bigEndian = true;
			stringBuffer.append(Utils.bytesToUnsignedShort(payload[0], payload[1], bigEndian));

		}

		return stringBuffer.toString();
	}

	public int getDataLength() {
		throw new RuntimeException("Not implemented exception!");
	}

	public void toBytes(final byte[] data, final int offset) {
		throw new RuntimeException("Not implemented exception!");
	}

	public int getTagNumber() {
		return tagNumber;
	}

	public void setTagNumber(final int tagNumber) {
		this.tagNumber = tagNumber;
	}

	public int getClassValue() {
		return classValue;
	}

	public void setClassValue(final int classValue) {
		this.classValue = classValue;
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

}
