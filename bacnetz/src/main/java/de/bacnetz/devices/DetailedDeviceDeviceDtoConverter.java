package de.bacnetz.devices;

import de.bacnetz.conversion.Converter;

public class DetailedDeviceDeviceDtoConverter implements Converter<Device, DeviceDto> {

    @Override
    public DeviceDto convert(final Device source) {

        final DeviceDto deviceDto = new DeviceDto();
        convert(source, deviceDto);

        return deviceDto;
    }

    @Override
    public void convert(final Device source, final DeviceDto target) {

        target.setId(source.getId());

    }

}
