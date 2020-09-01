package de.bacnetz.devices;

import java.util.List;

public interface DeviceFacade {

    List<DeviceDto> getDevices();

    void toggleAll();

}
