package de.bacnetz.server.websocket.push;

import java.util.Optional;
import java.util.Set;

import de.bacnetz.websocket.IUserSocket;
import de.bacnetz.websocket.push.IConnectedUser;

public interface IUserSocketManager {

    Set<IConnectedUser> getConnectedUsers();

    void sendObject(String username, String eventName, Object obj);

    void sendToAll(String eventName, Object eventObject);

    void sendToAuthorizedUsers(Optional<Integer> tenantId, String eventName, Object eventObject);

    void removeSocket(String username, IUserSocket socket);

}
