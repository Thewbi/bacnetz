package de.bacnetz.server.websocket;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.bacnetz.common.websocket.SocketHandler;
import de.bacnetz.common.websocket.subscriptions.UserSocketEvent;
import de.bacnetz.common.websocket.subscriptions.UserSocketExclude;
import de.bacnetz.server.websocket.push.UserSocketManager;
import de.bacnetz.server.websocket.subscriptions.DefaultSubscription;
import de.bacnetz.server.websocket.subscriptions.SubscriptionManager;
import de.bacnetz.websocket.IUserSocket;

/**
 * <pre>
 * ws = new WebSocket("ws://localhost:8080/inac/push");
 * console.log(ws);
 * ws.onopen = function () {
 * 	console.log('open');
 *  ws.send("This data makes no sense!");
 *  connection.send("Ping");
 *  connection.send('a');
 * };
 * ws.send('a');
 * </pre>
 */
@Component
public class DefaultSocketHandler extends TextWebSocketHandler implements SocketHandler {

    private static final String USERNAME = "admin";

    private final static Logger LOG = LoggerFactory.getLogger(DefaultSocketHandler.class);

    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    private final Map<WebSocketSession, SimulatorUserWebSocket> userSockets = new HashMap<>();

    private final Gson gson;

    @Autowired
    private SubscriptionManager subscriptionManager;

    /**
     * ctor
     */
    public DefaultSocketHandler() {

        LOG.debug("DefaultSocketHandler() - ctor");

        final ExclusionStrategy exclusionStrategy = new ExclusionStrategy() {
            @Override
            public boolean shouldSkipClass(final Class<?> clazz) {
                return false;
            }

            @Override
            public boolean shouldSkipField(final FieldAttributes field) {
                return field.getAnnotation(UserSocketExclude.class) != null;
            }
        };

        gson = new GsonBuilder().addSerializationExclusionStrategy(exclusionStrategy).create();
    }

    @Override
    public void handleTextMessage(final WebSocketSession session, final TextMessage message)
            throws InterruptedException, IOException {

        LOG.trace("handleTextMessage");

        // DEBUG output but ignore action list messages
        if (StringUtils.isNotBlank(message.getPayload())
                && !message.getPayload().equalsIgnoreCase("{\"action\":\"list\"}")) {
            LOG.trace(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            LOG.trace("handleTextMessage");
            LOG.trace(message.toString());
            LOG.trace(message.getPayload());
        }

        // parse message
        @SuppressWarnings("unchecked")
        final Map<String, Object> webSocketMessage = new Gson().fromJson(message.getPayload(), Map.class);

        // DEBUG
        for (final Map.Entry<String, Object> entry : webSocketMessage.entrySet()) {
            LOG.info("Key: " + entry.getKey() + " Value: " + entry.getValue());
        }

        final String action = (String) webSocketMessage.get("action");

        LOG.info("action = '{}'", action);

        if ("subscribe".equalsIgnoreCase(action)) {
            handleSubscription(session, webSocketMessage);
        } else if ("unsubscribe".equalsIgnoreCase(action)) {
            handleUnsubscribe(session, webSocketMessage);
        } else if ("list".equalsIgnoreCase(action)) {
            handleListSubscriptions(session);
        } else {
            LOG.error("Unknown Action: '{}'", action);
        }
    }

    /**
     * the frontend wants to know about all active subscriptions. Return all active
     * subscriptions.
     * 
     * @param session
     * @throws IOException
     */
    private void handleListSubscriptions(final WebSocketSession session) throws IOException {
        session.sendMessage(subscriptionManager.getActiveSubscriptions());
    }

    /**
     * stop the service with the unsubscribe name or remove the session that
     * unsubscribed be careful not to stop the service if there are still sessions
     * available.
     * 
     * @param session
     * @param webSocketMessage
     */
    private void handleUnsubscribe(final WebSocketSession session, final Map<String, Object> webSocketMessage) {
        final String id = (String) webSocketMessage.get("id");

        final DefaultSubscription subscription = new DefaultSubscription();
        subscription.setId(id);
        subscription.setWebSocketSession(session);

        if (id.contains("RWS_hdm")) {
            subscriptionManager.removeSubscriptionFromDevice(subscription);
        } else {
            subscriptionManager.removeSubscription(subscription);
        }
    }

    private void handleSubscription(final WebSocketSession session, final Map<String, Object> webSocketMessage) {
        if (!webSocketMessage.containsKey("id")) {
            LOG.error("NO_ID_IN_SUBSCRIPTION_MESSAGE");
            return;
        }

        final String id = (String) webSocketMessage.get("id");

        final DefaultSubscription subscription = new DefaultSubscription();
        subscription.setId(id);
        subscription.setWebSocketSession(session);

        if (id.contains("RWS_hdm")) {
            subscriptionManager.addSubscriptionToDevice(subscription);
        } else {
            subscriptionManager.addSubscription(subscription);
        }
    }

    @Override
    public void afterConnectionEstablished(final WebSocketSession webSocketSession) throws Exception {

        LOG.info("afterConnectionEstablished session.id: " + webSocketSession.getId() + " isOpen: "
                + webSocketSession.isOpen() + " URI: " + webSocketSession.getUri());

        sessions.add(webSocketSession);

        final SimulatorUserWebSocket simulatorUserWebSocket = new SimulatorUserWebSocket();
        simulatorUserWebSocket.setWebSocketSession(webSocketSession);
        simulatorUserWebSocket.setUsername(USERNAME);
        userSockets.put(webSocketSession, simulatorUserWebSocket);

        UserSocketManager.getInstance().addSocket(USERNAME, simulatorUserWebSocket);
    }

    /**
     * do something on connection closed
     */
    @Override
    public void afterConnectionClosed(final WebSocketSession webSocketSession, final CloseStatus status) {
        LOG.info("afterConnectionClosed() - websocket disconnected!");

        subscriptionManager.removeAllSubscriptions();

        sessions.remove(webSocketSession);

        if (userSockets.containsKey(webSocketSession)) {
            final IUserSocket simulatorUserWebSocket = userSockets.get(webSocketSession);
            UserSocketManager.getInstance().removeSocket(USERNAME, simulatorUserWebSocket);

            userSockets.remove(webSocketSession);
        }
    }

    /**
     * handle binary message
     */
    @Override
    protected void handleBinaryMessage(final WebSocketSession session, final BinaryMessage message) {
        LOG.info("handleBinaryMessage");
    }

    /**
     * Alive ping pong
     */
    @Override
    protected void handlePongMessage(final WebSocketSession session, final PongMessage message) throws Exception {
        LOG.info("handlePongMessage");
    }

    /**
     * Handle transport error
     */
    @Override
    public void handleTransportError(final WebSocketSession session, final Throwable exception) {
        LOG.info("handleTransportError");
    }

    public List<WebSocketSession> getSessions() {
        return sessions;
    }

    @Override
    public void sendUserSocketEventToAllSessions(final String name, final Object payload) {

        LOG.info("sendUserSocketEventToAllSessions() name: '{}', payload: '{}'", name, payload);

        if (CollectionUtils.isEmpty(getSessions())) {
            return;
        }

        final UserSocketEvent userSocketEvent = new UserSocketEvent(name, payload);
        final String jsonString = gson.toJson(userSocketEvent);

        LOG.info("sendUserSocketEventToAllSessions() jsonString: '{}'", jsonString);

        getSessions().stream().filter(s -> s.isOpen()).forEach(s -> {
            try {
                s.sendMessage(new TextMessage(jsonString));
            } catch (final IOException e) {
                LOG.error(e.getMessage(), e);
            }
        });
    }
}
