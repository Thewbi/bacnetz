package de.bacnetz.devices;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

import de.bacnetz.factory.Factory;
import de.bacnetz.stack.ObjectIdentifierServiceParameter;

public interface DeviceService {

    List<Device> createDevices(Map<Integer, String> vendorMap, String localIp)
            throws SocketException, UnknownHostException;

    List<Device> getDevices();

    Map<ObjectIdentifierServiceParameter, Device> getDeviceMap();

    void setDefaultDeviceFactory(Factory<Device> deviceFactory);

}
