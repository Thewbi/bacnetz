package de.bacnetz.devices;

import de.bacnetz.conversion.Converter;

public class DeviceDeviceDtoConverter implements Converter<Device, DeviceDto> {

    @Override
    public void convert(final Device source, final DeviceDto target) {
        target.setId(source.getId());
    }

    @Override
    public DeviceDto convert(final Device source) {

        final DeviceDto deviceDto = new DeviceDto();
        convert(source, deviceDto);

        return deviceDto;
    }

}
