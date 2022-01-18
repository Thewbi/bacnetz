package de.bacnetz.fatclient;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.bacnetz.configuration.ConfigurationManager;
import de.bacnetz.configuration.DefaultConfigurationManager;
import de.bacnetz.devices.DefaultDeviceFactory;
import de.bacnetz.devices.DefaultDeviceService;
import de.bacnetz.devices.Device;
import de.bacnetz.devices.DeviceService;
import de.bacnetz.factory.DefaultMessageFactory;
import de.bacnetz.factory.Factory;
import de.bacnetz.factory.MessageFactory;
import de.bacnetz.fatclient.buttonhandler.TestButtonHandler;
import de.bacnetz.vendor.VendorMap;

@Configuration
public class FatClientConfiguration {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * This method is not creating a bean but it is here to initialize the
     * ApplicationContextProvider.
     * 
     * @return
     */
    @Bean
    public Object startBean() {
        ApplicationContextProvider.setApplicationContext(applicationContext);
        return null;
    }

    @Bean
    public DeviceService deviceService() {
        return new DefaultDeviceService();
    }

    @Bean
    public ConfigurationManager configurationManager() {
        final ConfigurationManager configurationManager = new DefaultConfigurationManager();
        configurationManager.setProperty(ConfigurationManager.LOCAL_IP_CONFIG_KEY, "192.168.2.2");
        configurationManager.setProperty(ConfigurationManager.MULTICAST_IP_CONFIG_KEY, "192.168.2.255");
        return configurationManager;
    }

    @Bean
    public Factory<Device> deviceFactory(final ConfigurationManager configurationManager) {
        final DefaultDeviceFactory defaultDeviceFactory = new DefaultDeviceFactory();
        defaultDeviceFactory.setConfigurationManager(configurationManager);
        return defaultDeviceFactory;
    }

    @Bean
    public TestButtonHandler testButtonHandler() {
        return new TestButtonHandler("ThecnoCarolaSchmidt!");
    }

    @Bean
    public DefaultApplicationListener defaultApplicationListener() {
        return new DefaultApplicationListener();
    }

    @Bean
    public MessageFactory DefaultMessageFactory() throws FileNotFoundException, IOException {
        final Map<Integer, String> vendorMap = VendorMap.processVendorMap();

        final DefaultMessageFactory defaultMessageFactory = new DefaultMessageFactory();
        defaultMessageFactory.setVendorMap(vendorMap);
        return defaultMessageFactory;
    }

}
