package de.bacnetz.devices;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import de.bacnetz.conversion.Converter;

/**
 * Populates full information about a device and it's child devices for detailed
 * display.
 */
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
        target.setObjectType(source.getObjectType());
        target.setName(source.getName());
        target.setDescription(source.getDescription());
        target.setPresentValue(source.getPresentValue());
        target.getStates().addAll(source.getStates());

        if (MapUtils.isNotEmpty(source.getProperties())) {
            final List<DevicePropertyDto> childrenProperties = source.getProperties().values().stream().map(p -> {
                final DevicePropertyDto devicePropertyDto = new DevicePropertyDto();

                devicePropertyDto.setKey(p.getPropertyKey());
                devicePropertyDto.setName(p.getPropertyName());
                devicePropertyDto.setValue(p.getValue());

                return devicePropertyDto;
            }).collect(Collectors.toList());

            target.getDeviceProperties().addAll(childrenProperties);
        }

        if (CollectionUtils.isNotEmpty(source.getChildDevices())) {
            final List<DeviceDto> childrenDtos = source.getChildDevices().stream().map(c -> {
                final DeviceDto childDeviceDto = new DeviceDto();
                convert(c, childDeviceDto);

                return childDeviceDto;
            }).collect(Collectors.toList());

            target.getChildDevices().addAll(childrenDtos);
        }

    }

}
