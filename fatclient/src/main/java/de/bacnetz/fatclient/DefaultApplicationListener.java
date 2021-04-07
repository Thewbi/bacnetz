package de.bacnetz.fatclient;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import de.bacnetz.configuration.ConfigurationManager;
import de.bacnetz.controller.DefaultMessageController;
import de.bacnetz.devices.DeviceService;
import de.bacnetz.threads.MulticastListenerReaderThread;
import de.bacnetz.vendor.VendorMap;

public class DefaultApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(final ContextRefreshedEvent contextRefreshedEvent) {

        try {

            final ApplicationContext applicationContext = contextRefreshedEvent.getApplicationContext();
            final DeviceService deviceService = applicationContext.getBean(DeviceService.class);
            final ConfigurationManager configurationManager = applicationContext.getBean(ConfigurationManager.class);

            final Map<Integer, String> vendorMap = VendorMap.processVendorMap();

            startListenerThread(configurationManager, deviceService, vendorMap);

        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
//            e.printStackTrace();
        }
    }

    private static void startListenerThread(final ConfigurationManager configurationManager,
            final DeviceService deviceService, final Map<Integer, String> vendorMap)
            throws FileNotFoundException, IOException {

        final DefaultMessageController defaultMessageController = new DefaultMessageController();
        defaultMessageController.setDeviceService(deviceService);
        defaultMessageController.setVendorMap(vendorMap);

        final MulticastListenerReaderThread multicastListenerReaderThread = new MulticastListenerReaderThread();
        multicastListenerReaderThread.setConfigurationManager(configurationManager);
        multicastListenerReaderThread.setVendorMap(vendorMap);
        multicastListenerReaderThread
                .setBindPort(configurationManager.getPropertyAsInt(ConfigurationManager.PORT_CONFIG_KEY));
        multicastListenerReaderThread.getMessageControllers().add(defaultMessageController);
        multicastListenerReaderThread.openBroadCastSocket();

        defaultMessageController.setCommunicationService(multicastListenerReaderThread);

        new Thread(multicastListenerReaderThread).start();
    }

}
