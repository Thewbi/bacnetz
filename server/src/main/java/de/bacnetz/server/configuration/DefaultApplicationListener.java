package de.bacnetz.server.configuration;

import java.io.IOException;
import java.util.Map;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import de.bacnetz.App;
import de.bacnetz.controller.MessageController;
import de.bacnetz.devices.DeviceService;
import de.bacnetz.server.persistence.covsubscriptions.COVSubscriptionData;
import de.bacnetz.server.persistence.covsubscriptions.COVSubscriptionRepository;
import de.bacnetz.threads.MulticastListenerReaderThread;
import de.bacnetz.vendor.VendorMap;

/**
 * This class is part of the Spring infrastructure and it is called when the spring boot
 * server has started.
 * <br /><br />
 * This class is used to create the bacnet devices that the server will provide to the bacnet.
 * It also starts the thread that binds to a network interface and listens for packets.
 */
@Component
public class DefaultApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    private final static Logger LOG = LoggerFactory.getLogger(DefaultApplicationListener.class);

    @Autowired
    private MulticastListenerReaderThread multicastListenerReaderThread;

    @Autowired
    private MessageController messageController;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private COVSubscriptionRepository covSubscriptionRepository;

    /**
     * from application.properties defines the multicast IP address of the ethernet
     * subnetwork that this BACnet server takes part in
     */
    @Value("${bind.ip}")
    private String bindIp;

    @Value("${multicast.ip}")
    private String multicastIp;

    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        LOG.info("DefaultApplicationListener.onApplicationEvent()");
//        testDatabase();
        createObjects();
    }

    private void createObjects() {
        try {

            final Map<Integer, String> vendorMap = VendorMap.processVendorMap();
//            App.setupCento(vendorMap, deviceService, bindIp);
            App.setupVendorTest(vendorMap, deviceService, bindIp);

            multicastListenerReaderThread.getMessageControllers().add(messageController);
            multicastListenerReaderThread.setVendorMap(vendorMap);
            multicastListenerReaderThread.openBroadCastSocket();

            new Thread(multicastListenerReaderThread).start();

        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @SuppressWarnings("unused")
    private void testDatabase() {
    	
        final Iterable<COVSubscriptionData> findAll = covSubscriptionRepository.findAll();
        final boolean parallel = false;
        StreamSupport.stream(findAll.spliterator(), parallel)
                .forEach(cov -> LOG.info("Persisted object: '{}'", cov.toString()));
        final COVSubscriptionData covSubscriptionData = new COVSubscriptionData();
        covSubscriptionData.setIp("127.0.0.1");
        LOG.info("New Object before save:'{}'", covSubscriptionData.toString());

        final COVSubscriptionData saved = covSubscriptionRepository.save(covSubscriptionData);
        LOG.info("New Object after save:'{}'", saved.toString());

        covSubscriptionRepository.deleteAll();
        LOG.info("All objects have been deleted.");
    }

}
