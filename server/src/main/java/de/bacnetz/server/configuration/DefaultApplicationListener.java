package de.bacnetz.server.configuration;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import de.bacnetz.App;
import de.bacnetz.configuration.ConfigurationManager;
import de.bacnetz.controller.MessageController;
import de.bacnetz.devices.Device;
import de.bacnetz.devices.DeviceService;
import de.bacnetz.threads.MulticastListenerReaderThread;

@Component
public class DefaultApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    private final static Logger LOG = LoggerFactory.getLogger(DefaultApplicationListener.class);

    @Autowired
    private ConfigurationManager configurationManager;

    @Autowired
    private MulticastListenerReaderThread multicastListenerReaderThread;

    @Autowired
    private MessageController messageController;

    @Autowired
    private DeviceService deviceService;

    @Value("${bind.ip}")
    private String bindIp;

    @Value("${multicast.ip}")
    private String multicastIp;

    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        LOG.info("DefaultApplicationListener.onApplicationEvent()");

        try {

            configurationManager.setProperty(ConfigurationManager.LOCAL_IP_CONFIG_KEY, bindIp);
            configurationManager.setProperty(ConfigurationManager.MULTICAST_IP_CONFIG_KEY, multicastIp);

            final Map<Integer, String> vendorMap = App.processVendorMap();

            final List<Device> devices = deviceService.createDevices(vendorMap, bindIp);

            multicastListenerReaderThread.getMessageControllers().add(messageController);
            multicastListenerReaderThread.setVendorMap(vendorMap);
            multicastListenerReaderThread.openBroadCastSocket();

            new Thread(multicastListenerReaderThread).start();

        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

}
