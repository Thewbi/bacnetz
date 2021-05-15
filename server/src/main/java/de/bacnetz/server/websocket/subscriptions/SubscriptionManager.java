package de.bacnetz.server.websocket.subscriptions;

import org.springframework.web.socket.WebSocketMessage;

import de.bacnetz.common.websocket.subscriptions.Subscription;

public interface SubscriptionManager {

    void addSubscription(Subscription subscription);

    void removeSubscription(Subscription subscription);

    void addSubscriptionToDevice(Subscription subscription);

    void removeSubscriptionFromDevice(Subscription subscription);

    WebSocketMessage<?> getActiveSubscriptions();

    void removeAllSubscriptions();

}
