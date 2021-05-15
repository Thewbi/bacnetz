package de.bacnetz.server.websocket.subscriptions;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;

import com.google.gson.Gson;

import de.bacnetz.common.websocket.subscriptions.Subscription;

public class DefaultSubscriptionManager implements SubscriptionManager {

    private final static Logger LOG = LoggerFactory.getLogger(DefaultSubscriptionManager.class);

    private final Map<String, Subscription> subscriptions = new HashMap<>();

    private final Gson gson = new Gson();

    @Override
    public void addSubscription(final Subscription subscription) {
        subscriptions.put(subscription.getId(), subscription);
    }

    @Override
    public void removeSubscription(final Subscription subscription) {
        subscriptions.remove(subscription.getId());
    }

    @Override
    public void addSubscriptionToDevice(final Subscription subscription) {

        subscriptions.put(subscription.getId(), subscription);

        final String subcriptionId = subscription.getId();
        final String[] split = subcriptionId.split("_");

        final String deviceId = split[1];

    }

    @Override
    public void removeSubscriptionFromDevice(final Subscription subscription) {

        subscriptions.remove(subscription.getId());

        final String subcriptionId = subscription.getId();
        final String[] split = subcriptionId.split("_");

        final String deviceId = split[1];

    }

    @Override
    public WebSocketMessage<?> getActiveSubscriptions() {

        final ActiveSubscriptionDescriptor activeSubscriptionDescriptor = new ActiveSubscriptionDescriptor();

        subscriptions.keySet().stream().forEach(k -> activeSubscriptionDescriptor.getSubscriptions().add(k));
        final int size = activeSubscriptionDescriptor.getSubscriptions().size();
        activeSubscriptionDescriptor.setSize(Integer.toString(size));

        final String resultValue = gson.toJson(activeSubscriptionDescriptor);

        LOG.trace(resultValue);

        final TextMessage textMessage = new TextMessage(resultValue);

        return textMessage;
    }

    @Override
    public void removeAllSubscriptions() {
    }

}
