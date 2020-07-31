package de.bacnetz.devices;

import java.util.Collection;
import java.util.Map;

import de.bacnetz.controller.Message;
import de.bacnetz.stack.ServiceParameter;

public interface Device {

	ServiceParameter getObjectIdentifierServiceParameter();

	Map<Integer, DeviceProperty> getProperties();

	Collection<Device> getChildDevices();

	int getId();

	void setId(int id);

	int getObjectType();

	void setObjectType(int objectType);

	String getName();

	void setName(String name);

	Device findDevice(ServiceParameter objectIdentifierServiceParameter);

	Message getPropertyValue(Message requestMessage, int propertyIdentifierCode);

	Map<Integer, String> getVendorMap();

	void setVendorMap(Map<Integer, String> vendorMap);

}
