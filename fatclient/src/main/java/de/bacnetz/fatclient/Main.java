package de.bacnetz.fatclient;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import org.apache.pivot.wtk.DesktopApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import de.bacnetz.configuration.ConfigurationManager;
import de.bacnetz.controller.DefaultMessageController;
import de.bacnetz.devices.DeviceService;
import de.bacnetz.threads.MulticastListenerReaderThread;
import de.bacnetz.threads.WhoIsRunnable;
import de.bacnetz.vendor.VendorMap;

public class Main {

    /**
     * Next steps: When the DefaultMessageController receives a I_AM response in
     * processAPDUMessage(), then call a listener and hand over the device that
     * answered. The listener has to insert a node into the GUI device tree for that
     * device.
     * 
     * @param args
     * @throws FileNotFoundException
     * @throws IOException
     * @throws InterruptedException
     */
    public static void main(final String[] args) throws FileNotFoundException, IOException, InterruptedException {

        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
                FatClientConfiguration.class);

        final DeviceService deviceService = context.getBean(DeviceService.class);
        final ConfigurationManager configurationManager = context.getBean(ConfigurationManager.class);

        final Map<Integer, String> vendorMap = VendorMap.processVendorMap();

        startListenerThread(configurationManager, deviceService, vendorMap);

        DesktopApplicationContext.main(App.class, args);

        Thread.sleep(1000);

        runWhoIsThread();
    }

    private static void runWhoIsThread() {
        new Thread(new WhoIsRunnable()).run();
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
