package de.bacnetz.common.websocket;

public interface SocketHandler {

    void sendUserSocketEventToAllSessions(String name, Object payload);

}
