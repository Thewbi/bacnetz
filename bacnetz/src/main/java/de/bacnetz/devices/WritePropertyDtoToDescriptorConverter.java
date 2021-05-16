package de.bacnetz.devices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.bacnetz.conversion.Converter;

public class WritePropertyDtoToDescriptorConverter implements Converter<WritePropertyDto, WritePropertyDescriptor> {

    private final static Logger LOG = LoggerFactory.getLogger(WritePropertyDtoToDescriptorConverter.class);

    @Override
    public WritePropertyDescriptor convert(final WritePropertyDto source) {
        final WritePropertyDescriptor writePropertyDescriptor = new WritePropertyDescriptor();
        convert(source, writePropertyDescriptor);
        return writePropertyDescriptor;
    }

    @Override
    public void convert(final WritePropertyDto writePropertyDto,
            final WritePropertyDescriptor writePropertyDescriptor) {

        try {
            writePropertyDescriptor.setParentDeviceId(Integer.valueOf(writePropertyDto.getParentDeviceId()));
        } catch (final Exception e) {
            LOG.error("Cannot parse {} into an integer value!", writePropertyDto.getParentDeviceId());
            LOG.error(e.getMessage(), e);

            // TODO REST API should return error code!
            throw e;
        }
        try {
            writePropertyDescriptor.setChildDeviceId(Integer.valueOf(writePropertyDto.getChildDeviceId()));
        } catch (final Exception e) {
            LOG.error("Cannot parse {} into an integer value!", writePropertyDto.getChildDeviceId());
            LOG.error(e.getMessage(), e);

            // TODO REST API should return error code!
            throw e;
        }
        writePropertyDescriptor.setChildObjectType(writePropertyDto.getChildObjectType());
        writePropertyDescriptor.setPropertyKey(writePropertyDto.getPropertyKey());
        writePropertyDescriptor.setPropertyName(writePropertyDto.getPropertyName());
        writePropertyDescriptor.setValue(writePropertyDto.getValue());
    }

}
