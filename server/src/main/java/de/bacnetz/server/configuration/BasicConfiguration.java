package de.bacnetz.server.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import de.bacnetz.configuration.ConfigurationManager;
import de.bacnetz.configuration.DefaultConfigurationManager;
import de.bacnetz.controller.DefaultMessageController;
import de.bacnetz.controller.MessageController;
import de.bacnetz.conversion.Converter;
import de.bacnetz.devices.DefaultDeviceFacade;
import de.bacnetz.devices.DefaultDeviceFactory;
import de.bacnetz.devices.DefaultDeviceService;
import de.bacnetz.devices.Device;
import de.bacnetz.devices.DeviceDeviceDtoConverter;
import de.bacnetz.devices.DeviceDto;
import de.bacnetz.devices.DeviceFacade;
import de.bacnetz.devices.DeviceService;
import de.bacnetz.factory.Factory;
import de.bacnetz.threads.MulticastListenerReaderThread;

@Component
public class BasicConfiguration {

    @Bean
    public ConfigurationManager getConfigurationManager() {
        return new DefaultConfigurationManager();
    }

    @Bean
    public Factory<Device> getDeviceFactory() {
        return new DefaultDeviceFactory();
    }

    @Bean
    public DeviceService getDeviceService() {
        return new DefaultDeviceService();
    }

    @Bean
    public DeviceFacade getDeviceFacade() {
        return new DefaultDeviceFacade();
    }

    @Bean
    public MessageController getDefaultMessageController() {
        return new DefaultMessageController();
    }

    @Bean
    public MulticastListenerReaderThread getMulticastListenerReaderThread() {
        return new MulticastListenerReaderThread();
    }

    @Bean
    public Converter<Device, DeviceDto> getDeviceDeviceDtoConverter() {
        return new DeviceDeviceDtoConverter();
    }

}
