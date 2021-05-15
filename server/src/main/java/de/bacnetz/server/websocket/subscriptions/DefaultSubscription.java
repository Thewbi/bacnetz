package de.bacnetz.server.websocket.subscriptions;

import org.springframework.web.socket.WebSocketSession;

import de.bacnetz.common.websocket.subscriptions.Subscription;

public class DefaultSubscription implements Subscription {

    private String id;

    private WebSocketSession webSocketSession;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(final String id) {
        this.id = id;
    }

    @Override
    public WebSocketSession getWebSocketSession() {
        return webSocketSession;
    }

    public void setWebSocketSession(final WebSocketSession webSocketSession) {
        this.webSocketSession = webSocketSession;
    }

}
