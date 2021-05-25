package de.bacnetz.server.websocket;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistration;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import de.bacnetz.devices.DeviceService;
import de.bacnetz.server.websocket.subscriptions.DefaultSubscriptionManager;
import de.bacnetz.server.websocket.subscriptions.SubscriptionManager;

/**
 * For IE8, 9 support
 * https://docs.spring.io/spring-framework/docs/5.0.0.M1/spring-framework-reference/html/websocket.html#websocket-fallback-xhr-vs-iframe
 * .withSockJS();
 */
@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {

    private static final String WEBSOCKET_URL = "/bacnetz/push";

    private static final Logger LOG = LoggerFactory.getLogger(WebSocketConfiguration.class);

    @Autowired
    private DefaultSocketHandler socketHandler;

    @Override
    public void registerWebSocketHandlers(final WebSocketHandlerRegistry registry) {

        LOG.info("registerWebSocketHandlers()");

        final WebSocketHandlerRegistration webSocketHandlerRegistration = registry.addHandler(socketHandler,
                WEBSOCKET_URL);

        webSocketHandlerRegistration.setAllowedOrigins("*");

        // initial Request/Handshake interceptor
        webSocketHandlerRegistration.addInterceptors(new HttpSessionHandshakeInterceptor() {

            @Override
            public void afterHandshake(final ServerHttpRequest request, final ServerHttpResponse response,
                    final WebSocketHandler wsHandler, @Nullable final Exception ex) {
                super.afterHandshake(request, response, wsHandler, ex);
            }

            @Override
            public boolean beforeHandshake(final ServerHttpRequest request, final ServerHttpResponse response,
                    final WebSocketHandler wsHandler, final Map<String, Object> attributes) throws Exception {
                return super.beforeHandshake(request, response, wsHandler, attributes);
            }

        });

    }

    @Bean
    public SubscriptionManager subscriptionManager(final DeviceService deviceService) {
        final DefaultSubscriptionManager defaultSubscriptionManager = new DefaultSubscriptionManager();
        defaultSubscriptionManager.setDeviceService(deviceService);

        return defaultSubscriptionManager;
    }

}
