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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final DefaultSubscription other = (DefaultSubscription) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}
