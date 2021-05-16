package de.bacnetz.server.configuration;

import org.springframework.beans.factory.annotation.Value;
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
import de.bacnetz.devices.DetailedDeviceDeviceDtoConverter;
import de.bacnetz.devices.Device;
import de.bacnetz.devices.DeviceDeviceDtoConverter;
import de.bacnetz.devices.DeviceDto;
import de.bacnetz.devices.DeviceFacade;
import de.bacnetz.devices.DeviceService;
import de.bacnetz.factory.DefaultMessageFactory;
import de.bacnetz.factory.Factory;
import de.bacnetz.factory.MessageFactory;
import de.bacnetz.threads.MulticastListenerReaderThread;

@Component
public class BasicConfiguration {

    /**
     * from application.properties defines the multicast IP address of the ethernet
     * subnetwork that this BACnet server takes part in
     */
    @Value("${multicast.ip}")
    private String multicastIP;

    @Value("${bind.ip}")
    private String bindIp;

    @Bean
    public ConfigurationManager getConfigurationManager() {
        final ConfigurationManager configurationManager = new DefaultConfigurationManager();
        configurationManager.setProperty(ConfigurationManager.MULTICAST_IP_CONFIG_KEY, multicastIP);
        configurationManager.setProperty(ConfigurationManager.LOCAL_IP_CONFIG_KEY, bindIp);
        return configurationManager;
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
    public MessageFactory getDefaultMessageFactory() {
        return new DefaultMessageFactory();
    }

    @Bean
    public MulticastListenerReaderThread getMulticastListenerReaderThread() {
        return new MulticastListenerReaderThread();
    }

    @Bean("deviceDeviceDtoConverter")
    public Converter<Device, DeviceDto> getDeviceDeviceDtoConverter() {
        return new DeviceDeviceDtoConverter();
    }

    @Bean("detailedDeviceDeviceDtoConverter")
    public Converter<Device, DeviceDto> getDetailedDeviceDeviceDtoConverter() {
        return new DetailedDeviceDeviceDtoConverter();
    }

}
