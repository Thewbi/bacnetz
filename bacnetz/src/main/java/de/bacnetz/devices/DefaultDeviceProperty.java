package de.bacnetz.devices;

import de.bacnet.factory.MessageType;

public class DefaultDeviceProperty implements DeviceProperty {

	private String propertyName;

	private int propertyKey;

	private byte[] value;

	private MessageType messageType;

	/**
	 * ctor
	 * 
	 * @param propertyKey
	 * @param value
	 * @param messageType
	 */
	public DefaultDeviceProperty(final String propertyName, final int propertyKey, final byte[] value,
			final MessageType messageType) {
		super();
		this.propertyName = propertyName;
		this.propertyKey = propertyKey;
		this.value = value;
		this.messageType = messageType;
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
	public byte[] getValue() {
		return value;
	}

	@Override
	public void setValue(final byte[] value) {
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
