package de.bacnetz.common.websocket.subscriptions;

import org.springframework.web.socket.WebSocketSession;

public interface Subscription {

    String getId();

    void setId(String id);

    WebSocketSession getWebSocketSession();

}
