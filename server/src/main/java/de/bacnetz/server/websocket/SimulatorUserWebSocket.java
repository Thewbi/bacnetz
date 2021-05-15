package de.bacnetz.server.websocket;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import de.bacnetz.websocket.IUserSocket;

public class SimulatorUserWebSocket implements IUserSocket {

    private final static Logger LOG = LoggerFactory.getLogger(SimulatorUserWebSocket.class);

    private String username;

    private boolean connected = true;

    private WebSocketSession webSocketSession;

    @Override
    public void sendObjectAsString(final String jsonString) {

        LOG.info("--> Sending json over the websocket: \n" + prettyPrintJson(jsonString));
        LOG.info("webSocketSession.isOpen() = " + webSocketSession.isOpen());

        try {
            webSocketSession.sendMessage(new TextMessage(jsonString));
        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @SuppressWarnings("deprecation")
    private String prettyPrintJson(final String jsonString) {

        final JsonParser parser = new JsonParser();
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();

        final JsonElement jsonElement = parser.parse(jsonString);

        return gson.toJson(jsonElement);
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    public void setConnected(final boolean connected) {
        this.connected = connected;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public WebSocketSession getWebSocketSession() {
        return webSocketSession;
    }

    public void setWebSocketSession(final WebSocketSession webSocketSession) {
        this.webSocketSession = webSocketSession;
    }

}
