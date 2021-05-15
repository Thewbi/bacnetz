package de.bacnetz.websocket;

public interface IUserSocket {

    void sendObjectAsString(String jsonString);

    boolean isConnected();

    String getUsername();

}
