package de.bacnetz.websocket.push;

import java.util.List;

import de.bacnetz.websocket.IUserSocket;

public interface IConnectedUser {

    List<IUserSocket> getSockets();

    boolean isConnected();

    String getUsername();

}
