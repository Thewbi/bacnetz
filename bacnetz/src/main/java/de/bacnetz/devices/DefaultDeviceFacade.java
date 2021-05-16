package de.bacnetz.devices;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.bacnetz.conversion.Converter;

public class DefaultDeviceFacade implements DeviceFacade {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    @Qualifier("deviceDeviceDtoConverter")
    private Converter<Device, DeviceDto> deviceDeviceDtoConverter;

    @Autowired
    @Qualifier("detailedDeviceDeviceDtoConverter")
    private Converter<Device, DeviceDto> detailedDeviceDeviceDtoConverter;

    @Override
    public List<DeviceDto> getDevices() {
        return deviceService.getDevices().stream().map(d -> deviceDeviceDtoConverter.convert(d))
                .collect(Collectors.toList());
    }

    @Override
    public DeviceDto getDeviceDetails(final long uid) {

        // @formatter:off

        return deviceService.getDevices().stream()
                .filter(d -> d.getId() == uid)
                .findFirst()
                .map(d -> detailedDeviceDeviceDtoConverter.convert(d))
                .get();

        // @formatter:on
    }

    @Override
    public void toggleAll() {
        deviceService.getDevices().stream().forEach(d -> d.executeAction());
    }

}
