package de.bacnetz.devices;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import de.bacnetz.conversion.Converter;

public class DefaultDeviceFacade implements DeviceFacade {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private Converter<Device, DeviceDto> deviceDeviceDtoConverter;

    @Override
    public List<DeviceDto> getDevices() {
        return deviceService.getDevices().stream().map(d -> deviceDeviceDtoConverter.convert(d))
                .collect(Collectors.toList());
    }

    @Override
    public void toggleAll() {
        deviceService.getDevices().stream().forEach(d -> d.executeAction());
    }

}
