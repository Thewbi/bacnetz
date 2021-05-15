package de.bacnetz.server.websocket.push;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.bacnetz.websocket.IUserSocket;
import de.bacnetz.websocket.push.IConnectedUser;

public class UserSocketManager implements IUserSocketManager {

    private static final Logger LOG = LoggerFactory.getLogger(UserSocketManager.class);

    private static final UserSocketManager INSTANCE = new UserSocketManager();

    private final Map<String, IConnectedUser> sockets;

    public static UserSocketManager getInstance() {
        return INSTANCE;
    }

    private UserSocketManager() {
        sockets = new ConcurrentHashMap<>();
    }

    public void addSocket(final String username, final IUserSocket socket) {
        LOG.info("addSocket() username: '{}'", username);

        IConnectedUser connectedUser = null;
        if (sockets.containsKey(username)) {
            connectedUser = sockets.get(username);
        } else {
            connectedUser = createConnectedUser(username);
            sockets.put(username, connectedUser);
        }
        connectedUser.getSockets().add(socket);
    }

    @Override
    public void sendObject(final String username, final String eventName, final Object obj) {
        throw new RuntimeException("sendObject()");
    }

    @Override
    public void sendToAll(final String eventName, final Object eventObject) {
        throw new RuntimeException("sendToAll()");
    }

    @Override
    public void sendToAuthorizedUsers(final Optional<Integer> tenantId, final String eventName,
            final Object eventObject) {
        throw new RuntimeException("sendToAuthorizedUsers()");
    }

    private IConnectedUser createConnectedUser(final String username) {
        return new ConnectedUser(username);
    }

    @Override
    public void removeSocket(final String username, final IUserSocket socket) {
        if (sockets.containsKey(username)) {
            sockets.get(username).getSockets().remove(socket);
        }
    }

    /**
     * Returns the users which are currently connected with a Websocket.
     * 
     * @return set of currently connected usernames
     */
    @Override
    public Set<IConnectedUser> getConnectedUsers() {
        return sockets.values().stream().filter(cu -> cu.isConnected()).collect(Collectors.toSet());
    }

}
