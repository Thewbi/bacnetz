package de.bacnetz.devices;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import de.bacnetz.common.utils.NetworkUtils;
import de.bacnetz.factory.Factory;
import de.bacnetz.stack.ObjectIdentifierServiceParameter;
import de.bacnetz.stack.VendorType;

public class DefaultDeviceService implements DeviceService {

    private final List<Device> devices = new ArrayList<Device>();

    private final Map<ObjectIdentifierServiceParameter, Device> deviceMap = new HashMap<>();

    @Autowired
    private Factory<Device> deviceFactory;

    @Override
    public List<Device> createDevices(final Map<Integer, String> vendorMap, final String localIp)
            throws SocketException, UnknownHostException {

        // device 20000
        final int startDeviceId = 20000;

        final int deviceIdIncrement = 1;
        int deviceIdOffset = 0;
        int deviceId = startDeviceId + deviceIdOffset;
        Device device = deviceFactory.create(deviceMap, vendorMap, deviceId, NetworkUtils.OBJECT_NAME,
                VendorType.GEZE_GMBH.getCode());
        devices.add(device);
        device.bindSocket(localIp, deviceId);

        deviceIdOffset += deviceIdIncrement;
        deviceId = startDeviceId + deviceIdOffset;
        device = deviceFactory.create(deviceMap, vendorMap, deviceId, NetworkUtils.OBJECT_NAME,
                VendorType.GEZE_GMBH.getCode());
        devices.add(device);
        device.bindSocket(localIp, deviceId);

        deviceIdOffset += deviceIdIncrement;
        deviceId = startDeviceId + deviceIdOffset;
        device = deviceFactory.create(deviceMap, vendorMap, deviceId, NetworkUtils.OBJECT_NAME,
                VendorType.GEZE_GMBH.getCode());
        devices.add(device);
        device.bindSocket(localIp, deviceId);

        deviceIdOffset += deviceIdIncrement;
        deviceId = startDeviceId + deviceIdOffset;
        device = deviceFactory.create(deviceMap, vendorMap, deviceId, NetworkUtils.OBJECT_NAME,
                VendorType.GEZE_GMBH.getCode());
        devices.add(device);
        device.bindSocket(localIp, deviceId);

        deviceIdOffset += deviceIdIncrement;
        deviceId = startDeviceId + deviceIdOffset;
        device = deviceFactory.create(deviceMap, vendorMap, deviceId, NetworkUtils.OBJECT_NAME,
                VendorType.GEZE_GMBH.getCode());
        devices.add(device);
        device.bindSocket(localIp, deviceId);

        deviceIdOffset += deviceIdIncrement;
        deviceId = startDeviceId + deviceIdOffset;
        device = deviceFactory.create(deviceMap, vendorMap, deviceId, NetworkUtils.OBJECT_NAME,
                VendorType.GEZE_GMBH.getCode());
        devices.add(device);
        device.bindSocket(localIp, deviceId);

        deviceIdOffset += deviceIdIncrement;
        deviceId = startDeviceId + deviceIdOffset;
        device = deviceFactory.create(deviceMap, vendorMap, deviceId, NetworkUtils.OBJECT_NAME,
                VendorType.GEZE_GMBH.getCode());
        devices.add(device);
        device.bindSocket(localIp, deviceId);

        deviceIdOffset += deviceIdIncrement;
        deviceId = startDeviceId + deviceIdOffset;
        device = deviceFactory.create(deviceMap, vendorMap, deviceId, NetworkUtils.OBJECT_NAME,
                VendorType.GEZE_GMBH.getCode());
        devices.add(device);
        device.bindSocket(localIp, deviceId);

        return devices;
    }

    @Override
    public List<Device> getDevices() {
        return devices;
    }

    @Override
    public Map<ObjectIdentifierServiceParameter, Device> getDeviceMap() {
        return deviceMap;
    }

    @Override
    public void setDefaultDeviceFactory(final Factory<Device> deviceFactory) {
        this.deviceFactory = deviceFactory;
    }

}
