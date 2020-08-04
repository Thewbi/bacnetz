package de.bacnetz.devices;

import de.bacnet.factory.MessageType;
import de.bacnetz.common.utils.BACnetUtils;

public class DefaultDeviceProperty<T> implements DeviceProperty<T> {

	private String propertyName;

	private int propertyKey;

	private T value;

	private MessageType messageType;

	/**
	 * ctor
	 * 
	 * @param propertyKey
	 * @param value
	 * @param messageType
	 */
	public DefaultDeviceProperty(final String propertyName, final int propertyKey, final T value,
			final MessageType messageType) {
		super();
		this.propertyName = propertyName;
		this.propertyKey = propertyKey;
		this.value = value;
		this.messageType = messageType;
	}

	@Override
	public int getLengthTagValue() {

		if (messageType == MessageType.BOOLEAN) {
			return ((Boolean) value) ? 0x01 : 0x00;
		} else {
			return getValueAsByteArray().length;
		}
	}

	@Override
	public byte[] getValueAsByteArray() {

		switch (messageType) {

		case BOOLEAN:
			return new byte[] { (byte) (((Boolean) value) ? 0x01 : 0x00) };

		case UNSIGNED_INTEGER:
			final Integer valueAsInteger = (Integer) value;
			return IntToByteArray(valueAsInteger);

		case ENUMERATED:
			return (byte[]) value;

		case CHARACTER_STRING:
			return BACnetUtils.retrieveAsString((String) value);

		case SINGED_INTEGER_TWOS_COMPLEMENT_NOTATION:
			return (byte[]) value;

		default:
			throw new RuntimeException("Unimplemented type: " + messageType);
		}
	}

	byte[] IntToByteArray(final int data) {

		final byte byte0 = (byte) ((data & 0xFF000000) >> 24);
		final byte byte1 = (byte) ((data & 0x00FF0000) >> 16);
		final byte byte2 = (byte) ((data & 0x0000FF00) >> 8);
		final byte byte3 = (byte) ((data & 0x000000FF) >> 0);

		if (byte0 > 0) {
			final byte[] result = new byte[4];
			result[0] = byte0;
			result[1] = byte1;
			result[2] = byte2;
			result[3] = byte3;
			return result;
		}

		if (byte1 > 0) {
			final byte[] result = new byte[3];
			result[0] = byte1;
			result[1] = byte2;
			result[2] = byte3;
			return result;
		}

		if (byte2 > 0) {
			final byte[] result = new byte[2];
			result[0] = byte2;
			result[1] = byte3;
			return result;
		}

		return new byte[] { byte3 };
	}

	@Override
	public int getPropertyKey() {
		return propertyKey;
	}

	@Override
	public void setPropertyKey(final int propertyKey) {
		this.propertyKey = propertyKey;
	}

	@Override
	public T getValue() {
		return value;
	}

	@Override
	public void setValue(final T value) {
		this.value = value;
	}

	@Override
	public MessageType getMessageType() {
		return messageType;
	}

	@Override
	public void setMessageType(final MessageType messageType) {
		this.messageType = messageType;
	}

	@Override
	public String getPropertyName() {
		return propertyName;
	}

	@Override
	public void setPropertyName(final String propertyName) {
		this.propertyName = propertyName;
	}

}
