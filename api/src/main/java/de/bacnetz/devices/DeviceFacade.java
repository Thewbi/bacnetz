package de.bacnetz.devices;

import java.util.List;

public interface DeviceFacade {

    List<DeviceDto> getDevices();

    DeviceDto getDeviceDetails(long uid);

    void toggleAll();

    void writeProperty(WritePropertyDto writePropertyDto);

}
