package de.bacnetz.devices;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import de.bacnet.factory.MessageType;
import de.bacnetz.stack.ServiceParameter;
import de.bacnetz.stack.TagClass;

public class CompositeDeviceProperty extends DefaultDeviceProperty<Object> {

	/**
	 * ctor
	 * 
	 * @param propertyName
	 * @param propertyKey
	 * @param value
	 * @param messageType
	 */
	public CompositeDeviceProperty(final String propertyName, final int propertyKey, final Object value,
			final MessageType messageType) {
		super(propertyName, propertyKey, value, messageType);
	}

	private final List<DefaultDeviceProperty<?>> compositeList = new ArrayList<>();

	@Override
	public Collection<ServiceParameter> getServiceParameters() {

		final List<ServiceParameter> result = new ArrayList<>();

		ServiceParameter serviceParameter;

		for (final DefaultDeviceProperty<?> deviceProperty : compositeList) {

			serviceParameter = new ServiceParameter();
			serviceParameter.setMessageType(deviceProperty.getMessageType());
			serviceParameter.setTagClass(TagClass.APPLICATION_TAG);
			serviceParameter.setTagNumber(deviceProperty.getMessageType().getValue());
			serviceParameter.setLengthValueType(deviceProperty.getLengthTagValue());

			final byte[] payload = deviceProperty.getValueAsByteArray();
			if (deviceProperty.getMessageType() == MessageType.BOOLEAN) {
				serviceParameter.setPayload(null);
			} else if (ArrayUtils.isNotEmpty(payload)) {
				serviceParameter.setPayload(payload);
			}

			result.add(serviceParameter);
		}

		return result;
	}

	public List<DefaultDeviceProperty<?>> getCompositeList() {
		return compositeList;
	}

}
