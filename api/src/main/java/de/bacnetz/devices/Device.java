package de.bacnetz.devices;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.bacnetz.controller.Message;
import de.bacnetz.factory.MessageFactory;
import de.bacnetz.stack.BACnetServicesSupportedBitString;
import de.bacnetz.stack.COVSubscription;
import de.bacnetz.stack.LinkLayerType;
import de.bacnetz.stack.ObjectIdentifierServiceParameter;
import de.bacnetz.stack.ServiceParameter;

public interface Device {

    ObjectIdentifierServiceParameter getObjectIdentifierServiceParameter();

    Map<Integer, DeviceProperty<?>> getProperties();

    Collection<Device> getChildDevices();

    int getId();

    void setId(int id);

    ObjectType getObjectType();

    void setObjectType(ObjectType objectType);

    String getName();

    void setName(String name);

    String getModelName();

    void setModelName(String modelName);

    Device findDevice(ServiceParameter objectIdentifierServiceParameter);

    Message getPropertyValue(Message requestMessage, int propertyIdentifierCode);

    void writeProperty(Integer propertyKey, Object value);

    Map<Integer, String> getVendorMap();

    void setVendorMap(Map<Integer, String> vendorMap);

    int retrieveNextInvokeId();

    ServiceParameter getStatusFlagsServiceParameter();

    BACnetServicesSupportedBitString retrieveServicesSupported();

    Object getPresentValue();

    boolean isOutOfService();

    void setOutOfService(boolean outOfService);

    List<String> getStates();

    String getDescription();

    void setDescription(String description);

    String getFirmwareRevision();

    void setFirmwareRevision(String firmwareRevision);

    String getLocation();

    void setLocation(String location);

    Message processPresentValueProperty(DeviceProperty<?> deviceProperty, Message requestMessage);

    LocalDateTime getTimeOfDeviceRestart();

    void setTimeOfDeviceRestart(LocalDateTime timeOfDeviceRestart);

    Set<COVSubscription> getCovSubscriptions();

    Device getParentDevice();

    void setParentDevice(Device parentDevice);

    void executeAction();

    int getVendorId();

    void setVendorId(int vendorId);

    void bindSocket(String ip, int port) throws SocketException, UnknownHostException;

    void cleanUp();

    void sendIamMessage(LinkLayerType linkLayerType) throws IOException;

    Map<ObjectIdentifierServiceParameter, Device> getDeviceMap();

    void setMessageFactory(MessageFactory messageFactory);

    void onValueChanged(Device device, DeviceProperty<Object> presentValueDeviceProperty, Object oldPresentValue,
            Object newPresentValue);

}
