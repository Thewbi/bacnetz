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
import de.bacnetz.stack.LinkLayerType;
import de.bacnetz.stack.ObjectIdentifierServiceParameter;
import de.bacnetz.stack.VendorType;

public class DefaultDeviceService implements DeviceService {

    private static final int WILDCARD_MSTP_DEVICE_INSTANCE_NUMBER = 255;

    private final List<Device> devices = new ArrayList<Device>();

    private final Map<ObjectIdentifierServiceParameter, Device> deviceMap = new HashMap<>();

    @Autowired
    private Factory<Device> deviceFactory;

    @Override
    public List<Device> createDevices(final Map<Integer, String> vendorMap, final String localIp,
            final DeviceCreationDescriptor deviceCreationDescriptor) throws SocketException, UnknownHostException {

        int deviceIdOffset = deviceCreationDescriptor.getDeviceIdOffset();

        for (int i = 0; i < deviceCreationDescriptor.getAmountOfDevices(); i++) {

            final int deviceId = deviceCreationDescriptor.getStartDeviceId() + deviceIdOffset;
            final Device device = deviceFactory.create(deviceMap, vendorMap, deviceId, NetworkUtils.OBJECT_NAME,
                    VendorType.GEZE_GMBH.getCode());
            devices.add(device);
            device.bindSocket(localIp, deviceId);

            deviceIdOffset += deviceCreationDescriptor.getDeviceIdIncrement();
        }

//        deviceIdOffset += deviceIdIncrement;
//        deviceId = startDeviceId + deviceIdOffset;
//        device = deviceFactory.create(deviceMap, vendorMap, deviceId, NetworkUtils.OBJECT_NAME,
//                VendorType.GEZE_GMBH.getCode());
//        devices.add(device);
//        device.bindSocket(localIp, deviceId);
//
//        deviceIdOffset += deviceIdIncrement;
//        deviceId = startDeviceId + deviceIdOffset;
//        device = deviceFactory.create(deviceMap, vendorMap, deviceId, NetworkUtils.OBJECT_NAME,
//                VendorType.GEZE_GMBH.getCode());
//        devices.add(device);
//        device.bindSocket(localIp, deviceId);
//
//        deviceIdOffset += deviceIdIncrement;
//        deviceId = startDeviceId + deviceIdOffset;
//        device = deviceFactory.create(deviceMap, vendorMap, deviceId, NetworkUtils.OBJECT_NAME,
//                VendorType.GEZE_GMBH.getCode());
//        devices.add(device);
//        device.bindSocket(localIp, deviceId);
//
//        deviceIdOffset += deviceIdIncrement;
//        deviceId = startDeviceId + deviceIdOffset;
//        device = deviceFactory.create(deviceMap, vendorMap, deviceId, NetworkUtils.OBJECT_NAME,
//                VendorType.GEZE_GMBH.getCode());
//        devices.add(device);
//        device.bindSocket(localIp, deviceId);
//
//        deviceIdOffset += deviceIdIncrement;
//        deviceId = startDeviceId + deviceIdOffset;
//        device = deviceFactory.create(deviceMap, vendorMap, deviceId, NetworkUtils.OBJECT_NAME,
//                VendorType.GEZE_GMBH.getCode());
//        devices.add(device);
//        device.bindSocket(localIp, deviceId);
//
//        deviceIdOffset += deviceIdIncrement;
//        deviceId = startDeviceId + deviceIdOffset;
//        device = deviceFactory.create(deviceMap, vendorMap, deviceId, NetworkUtils.OBJECT_NAME,
//                VendorType.GEZE_GMBH.getCode());
//        devices.add(device);
//        device.bindSocket(localIp, deviceId);
//
//        deviceIdOffset += deviceIdIncrement;
//        deviceId = startDeviceId + deviceIdOffset;
//        device = deviceFactory.create(deviceMap, vendorMap, deviceId, NetworkUtils.OBJECT_NAME,
//                VendorType.GEZE_GMBH.getCode());
//        devices.add(device);
//        device.bindSocket(localIp, deviceId);

        return devices;
    }

    @Override
    public List<Device> findDevice(final ObjectIdentifierServiceParameter objectIdentifierServiceParameter,
            final LinkLayerType linkLayerType) {

        Device device = null;

        final ArrayList<Device> result = new ArrayList<>();

        switch (linkLayerType) {

        case IP:
            device = getDeviceMap().get(objectIdentifierServiceParameter);
            if (device != null) {
                result.add(device);
            }
            break;

        case MSTP:
            if (objectIdentifierServiceParameter.getInstanceNumber() == WILDCARD_MSTP_DEVICE_INSTANCE_NUMBER) {
                result.addAll(getDeviceMap().values());
            } else {
                device = getDeviceMap().get(objectIdentifierServiceParameter);
                if (device != null) {
                    result.add(device);
                }
            }
            break;
        }

        return result;
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
    public void setDeviceFactory(final Factory<Device> deviceFactory) {
        this.deviceFactory = deviceFactory;
    }

}
