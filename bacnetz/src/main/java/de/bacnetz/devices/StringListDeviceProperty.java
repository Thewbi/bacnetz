package de.bacnetz.devices;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.bacnet.factory.MessageType;
import de.bacnetz.stack.ServiceParameter;
import de.bacnetz.stack.TagClass;

public class StringListDeviceProperty extends DefaultDeviceProperty<Object> {

	public StringListDeviceProperty(final String propertyName, final int propertyKey, final Object value,
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
			serviceParameter.setTagClass(TagClass.APPLICATION_TAG);
			serviceParameter.setTagNumber(deviceProperty.getMessageType().getValue());
			serviceParameter.setLengthValueType(deviceProperty.getLengthTagValue());
			serviceParameter.setPayload(deviceProperty.getValueAsByteArray());
			result.add(serviceParameter);
		}

		return result;
	}

	public List<DefaultDeviceProperty<?>> getCompositeList() {
		return compositeList;
	}

}
