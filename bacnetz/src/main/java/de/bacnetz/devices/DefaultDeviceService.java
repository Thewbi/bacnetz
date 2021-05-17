package de.bacnetz.devices;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import de.bacnetz.factory.Factory;
import de.bacnetz.stack.LinkLayerType;
import de.bacnetz.stack.ObjectIdentifierServiceParameter;
import de.bacnetz.stack.VendorType;

public class DefaultDeviceService implements DeviceService {

    private final static Logger LOG = LoggerFactory.getLogger(DefaultDeviceService.class);

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

            final DeviceType deviceType = deviceCreationDescriptor.getDeviceType();
            final String deviceName = deviceCreationDescriptor.getDeviceName();
            final String modelName = deviceCreationDescriptor.getModelName();

            final int deviceId = deviceCreationDescriptor.getStartDeviceId() + deviceIdOffset;
            final Device device = deviceFactory.create(deviceType, deviceMap, vendorMap, deviceId, deviceName,
                    modelName, VendorType.GEZE_GMBH.getCode());
            devices.add(device);

            // device starts it's own server on it's own port, because BACnet Ids are not
            // globally unique but only locally unique within a device. One server cannot
            // host several devices because for any incoming Id, it does not know to which
            // device to send the messages because ids are not globally unique!
            //
            // When the Virtual Link Layer contains the address and port to on which the
            // device listens, BACnet IP communication partners will directly talk to that
            // port. On that port, BACnet is able to perfectly correlate the id's because
            // there is only a single device listening on that port!
            final int port = deviceId < 1024 ? 1024 + deviceId : deviceId;

            try {
                device.bindSocket(localIp, port);
            } catch (final java.net.BindException e) {
                LOG.error(e.getMessage(), e);
            }

            deviceIdOffset += deviceCreationDescriptor.getDeviceIdIncrement();
        }

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
    public void writeProperty(final WritePropertyDescriptor writePropertyDescriptor) {

        final ObjectIdentifierServiceParameter parentObjectIdentifier = ObjectIdentifierServiceParameter
                .createFromTypeAndInstanceNumber(ObjectType.DEVICE, writePropertyDescriptor.getParentDeviceId());

        final List<Device> parentDevices = findDevice(parentObjectIdentifier, LinkLayerType.IP);
        if (CollectionUtils.isEmpty(parentDevices)) {
            LOG.warn("No parent device found for " + parentObjectIdentifier);
            return;
        }

        final Device parentDevice = parentDevices.get(0);

        Device device = parentDevice;

        // if a child was requested, try to find the child
        if (writePropertyDescriptor.getChildDeviceId() != null) {

            final ObjectIdentifierServiceParameter childObjectIdentifier = ObjectIdentifierServiceParameter
                    .createFromTypeAndInstanceNumber(writePropertyDescriptor.getChildObjectType(),
                            writePropertyDescriptor.getChildDeviceId());
            device = device.findDevice(childObjectIdentifier);

            if (device == null) {
                LOG.warn("No child device found for " + childObjectIdentifier);
                return;
            }

        }

        device.writeProperty(writePropertyDescriptor.getPropertyKey(), writePropertyDescriptor.getValue());
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
