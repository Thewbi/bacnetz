package de.bacnetz.server.websocket.subscriptions;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.socket.TextMessage;

import com.google.gson.Gson;

import de.bacnetz.common.websocket.subscriptions.Subscription;
import de.bacnetz.devices.Device;
import de.bacnetz.listener.Listener;

public class DefaultSubscriptionListener implements Listener {

    private static final Logger LOG = LogManager.getLogger(DefaultSubscriptionListener.class);

    private Subscription subscription;

    private final Gson gson = new Gson();

    /**
     * ctor
     * 
     * @param subscription
     */
    public DefaultSubscriptionListener(final Subscription subscription) {
        this.subscription = subscription;
    }

    @Override
    public void event(final Object sender, final Object... args) {
        try {

            final Device device = (Device) sender;

            final EventDto eventDto = new EventDto();
            eventDto.setKey(String.valueOf(device.getId()));
            eventDto.setValue(args[0].toString());

            final String eventDtoAsJson = gson.toJson(eventDto);
            LOG.info(eventDtoAsJson);

            this.subscription.getWebSocketSession().sendMessage(new TextMessage(eventDtoAsJson));
        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(final Subscription subscription) {
        this.subscription = subscription;
    }

}
