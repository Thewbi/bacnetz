package de.bacnetz.factory;

import java.util.Map;

import de.bacnetz.controller.Message;
import de.bacnetz.devices.Device;
import de.bacnetz.devices.ObjectType;

public interface MessageFactory extends Factory<Message> {

    Message createEnumeratedProperty(Device device, int invokeId, int propertyKey, byte[] payload);

    void setVendorMap(Map<Integer, String> vendorMap);

    Message createErrorMessage(Message requestMessage, int errorClass, int errorCode);

    Message whoIsMessage();

    Message whoIsMessage(int lowerBound, int upperBound);

    Message requestObjectList(ObjectType objectType, int bacnetID);

}
