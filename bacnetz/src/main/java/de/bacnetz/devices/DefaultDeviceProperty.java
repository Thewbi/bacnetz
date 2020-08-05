package de.bacnetz.devices;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import de.bacnet.factory.MessageType;
import de.bacnetz.common.utils.BACnetUtils;
import de.bacnetz.common.utils.Utils;
import de.bacnetz.stack.ServiceParameter;
import de.bacnetz.stack.TagClass;

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

		} else if (messageType == MessageType.CHARACTER_STRING) {

			return ServiceParameter.EXTENDED_VALUE;

		} else {

			return getValueAsByteArray().length;

		}
	}

	@Override
	public byte[] getValueAsByteArray() {

		Integer valueAsInteger = null;

		switch (messageType) {

		case BOOLEAN:
			return new byte[] { (byte) (((Boolean) value) ? 0x01 : 0x00) };

		case BACNET_OBJECT_IDENTIFIER:
			valueAsInteger = (Integer) value;
			return Utils.intToBuffer(valueAsInteger);

		case UNSIGNED_INTEGER:
			valueAsInteger = (Integer) value;
			return BACnetUtils.intToByteArray(valueAsInteger);

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

	@Override
	public Collection<ServiceParameter> getServiceParameters() {

		final List<ServiceParameter> result = new ArrayList<>();

		ServiceParameter serviceParameter;

		serviceParameter = new ServiceParameter();
		serviceParameter.setMessageType(messageType);
		serviceParameter.setTagClass(TagClass.APPLICATION_TAG);
		serviceParameter.setTagNumber(getMessageType().getValue());
		serviceParameter.setLengthValueType(getLengthTagValue());

		final byte[] payload = getValueAsByteArray();
		if (messageType == MessageType.BOOLEAN) {
			serviceParameter.setPayload(null);
		} else if (ArrayUtils.isNotEmpty(payload)) {
			serviceParameter.setPayload(payload);
		}

		result.add(serviceParameter);

		return result;
	}

	@Override
	public String toString() {

		final StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("DeviceProperty ").append(propertyName).append(" (").append(propertyKey).append(") ")
				.append(messageType);

		return stringBuilder.toString();
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

	@Override
	public String getValueAsString() {
		if (value == null) {
			return StringUtils.EMPTY;
		}
		return value.toString();
	}

}
