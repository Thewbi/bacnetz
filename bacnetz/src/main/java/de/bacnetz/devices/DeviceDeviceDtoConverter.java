package de.bacnetz.devices;

import de.bacnetz.conversion.Converter;

/**
 * Populates basic information only for fast access.
 */
public class DeviceDeviceDtoConverter implements Converter<Device, DeviceDto> {

    @Override
    public DeviceDto convert(final Device source) {

        final DeviceDto deviceDto = new DeviceDto();
        convert(source, deviceDto);

        return deviceDto;
    }

    @Override
    public void convert(final Device source, final DeviceDto target) {
        target.setId(source.getId());
        target.setObjectType(source.getObjectType());
        target.setName(source.getName());
        target.setDescription(source.getDescription());
    }

}
