package de.bacnetz.devices;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

import de.bacnetz.factory.Factory;
import de.bacnetz.stack.LinkLayerType;
import de.bacnetz.stack.ObjectIdentifierServiceParameter;

public interface DeviceService {

    List<Device> createDevices(Map<Integer, String> vendorMap, String localIp,
            DeviceCreationDescriptor deviceCreationDescriptor) throws SocketException, UnknownHostException;

    List<Device> getDevices();

    Map<ObjectIdentifierServiceParameter, Device> getDeviceMap();

    void setDeviceFactory(Factory<Device> deviceFactory);

    List<Device> findDevice(ObjectIdentifierServiceParameter objectIdentifierServiceParameter,
            LinkLayerType linkLayerType);

}
