package de.bacnetz.server.websocket.push;

import java.util.ArrayList;
import java.util.List;

import de.bacnetz.websocket.IUserSocket;
import de.bacnetz.websocket.push.IConnectedUser;

public class ConnectedUser implements IConnectedUser {

    private final String username;

//    private final Integer tenantId;

    private final List<IUserSocket> sockets = new ArrayList<>();

//    private final Map<String, Boolean> permissions = new ConcurrentHashMap<>();
//
//    private final Set<String> permittedGuids = new HashSet<>();

    public ConnectedUser(final String username) {
        super();
        this.username = username;
//        this.tenantId = tenantId;
    }

    @Override
    public boolean isConnected() {
        if (sockets == null || sockets.isEmpty()) {
            return false;
        }

        for (final IUserSocket socket : sockets) {
            if (socket.isConnected()) {
                return true;
            }
        }

        return false;
    }

//    @Override
//    public boolean hasPermission(final String permissionKey) {
//        // TODO: insert Apache MapUtils to the codebase and replace this code
//        if (permissions.isEmpty() || !permissions.containsKey(permissionKey)) {
//            return false;
//        }
//        final Boolean booleanResult = permissions.get(permissionKey);
//
//        return booleanResult == null ? false : booleanResult;
//    }

    @Override
    public String toString() {
        return "ConnectedUser [username=" + username + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ConnectedUser other = (ConnectedUser) obj;
        if (username == null) {
            if (other.username != null) {
                return false;
            }
        } else if (!username.equals(other.username)) {
            return false;
        }
        return true;
    }

    @Override
    public String getUsername() {
        return username;
    }

//    @Override
//    public Integer getTenantId() {
//        return tenantId;
//    }

    @Override
    public List<IUserSocket> getSockets() {
        return sockets;
    }

//    @Override
//    public Map<String, Boolean> getPermissions() {
//        return permissions;
//    }
//
//    @Override
//    public Set<String> getPermittedGuids() {
//        return permittedGuids;
//    }

}
