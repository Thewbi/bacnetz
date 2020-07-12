package de.bacnetz.stack;

import org.apache.commons.lang3.ArrayUtils;

import de.bacnetz.common.Utils;

public class ServiceParameter {

	public static final int CLOSING_TAG_CODE = 7;

	public static final int OPENING_TAG_CODE = 6;

	public static final int ENUMERATED_CODE = 9;

	public static final int UNSIGNED_INTEGER_CODE = 2;

	public static final int BACNET_OBJECT_IDENTIFIER = 12;

	private int tagNumber;

	private TagClass tagClass;

	private int lengthValueType;

	private byte[] payload;

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

		if (tagNumber == 0 && tagClass == null && lengthValueType == 0) {
			return 0;
		}

		if (lengthValueType == OPENING_TAG_CODE) {

			// tag number 6 is opening bracket without any payload

		} else if (lengthValueType == CLOSING_TAG_CODE) {

			// tag number 7 is closing bracket without any payload

		} else {

			payload = new byte[lengthValueType];
			System.arraycopy(data, offset + 1, payload, 0, lengthValueType);

			length = lengthValueType;

		}

		return length + 1;
	}

	@Override
	public String toString() {

		final StringBuffer stringBuffer = new StringBuffer();

		switch (tagClass) {
		case APPLICATION_TAG:

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
			break;

		case CONTEXT_SPECIFIC_TAG:

			if (ArrayUtils.isEmpty(payload)) {

			} else if (payload.length == 1) {

				stringBuffer.append(Utils.byteArrayToStringNoPrefix(payload));

			} else if (payload.length == 2) {

				final boolean bigEndian = true;
				stringBuffer.append(Utils.bytesToUnsignedShort(payload[0], payload[1], bigEndian));
			}
			break;
		}

		return stringBuffer.toString();
	}

	public int getDataLength() {

		if (tagClass == TagClass.CONTEXT_SPECIFIC_TAG) {

			if ((lengthValueType == OPENING_TAG_CODE) || (lengthValueType == CLOSING_TAG_CODE)) {

				return 1;
			}

//			throw new RuntimeException("Not implemented!");
		}

		return lengthValueType + 1;
	}

	public void toBytes(final byte[] data, final int offset) {

		// the application tag is a byte that encodes the information type of this
		// service parameter, the type of this service parameter (Application or context
		// specific) and the length of the payload inside this service parameter
		final int applicationTag = (tagNumber << 4) | (tagClass.getId() << 3) | (lengthValueType);

		int index = 0;
		data[offset + index++] = (byte) applicationTag;

		if (ArrayUtils.isNotEmpty(payload)) {
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

}
