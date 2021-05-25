package de.bacnetz.server.websocket.subscriptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;

import com.google.gson.Gson;

import de.bacnetz.common.websocket.subscriptions.Subscription;
import de.bacnetz.devices.Device;
import de.bacnetz.devices.DeviceService;
import de.bacnetz.devices.ObjectType;
import de.bacnetz.stack.LinkLayerType;
import de.bacnetz.stack.ObjectIdentifierServiceParameter;

public class DefaultSubscriptionManager implements SubscriptionManager {

    private final static Logger LOG = LoggerFactory.getLogger(DefaultSubscriptionManager.class);

    private final Map<String, Subscription> subscriptions = new HashMap<>();

    private final Gson gson = new Gson();

    private DeviceService deviceService;

    @Override
    public void addSubscription(final Subscription subscription) {
        subscriptions.put(subscription.getId(), subscription);
    }

    @Override
    public void removeSubscription(final Subscription subscription) {
        subscriptions.remove(subscription.getId());
    }

    @Override
    public void addSubscriptionToDevice(final Subscription subscription) {

        subscriptions.put(subscription.getId(), subscription);

        final String subcriptionId = subscription.getId();
        final String[] split = subcriptionId.split("_");
        final int deviceId = Integer.parseInt(split[1]);

        final ObjectIdentifierServiceParameter objectIdentifier = ObjectIdentifierServiceParameter
                .createFromTypeAndInstanceNumber(ObjectType.DEVICE, deviceId);

        final List<Device> devices = deviceService.findDevice(objectIdentifier, LinkLayerType.IP);

        if (CollectionUtils.isEmpty(devices)) {
            return;
        }

        final Device device = devices.get(0);
        if (MapUtils.isEmpty(device.getListeners())) {
            // if there is no listener for the subscription yet, add one
            final DefaultSubscriptionListener listener = new DefaultSubscriptionListener(subscription);
            device.getListeners().put(subscription, listener);
            LOG.info("WebSocket subscription added to device: " + device);
        } else {
            // check if there is a listener already
            if (!device.getListeners().containsKey(subscription)) {
                // if there is no listener for the subscription yet, add one
                final DefaultSubscriptionListener listener = new DefaultSubscriptionListener(subscription);
                device.getListeners().put(subscription, listener);
                LOG.info("WebSocket subscription added to device: " + device);
            }
        }

    }

    @Override
    public void removeSubscriptionFromDevice(final Subscription subscription) {

        subscriptions.remove(subscription.getId());

        final String subcriptionId = subscription.getId();
        final String[] split = subcriptionId.split("_");

        final String deviceId = split[1];

    }

    @Override
    public WebSocketMessage<?> getActiveSubscriptions() {

        final ActiveSubscriptionDescriptor activeSubscriptionDescriptor = new ActiveSubscriptionDescriptor();

        subscriptions.keySet().stream().forEach(k -> activeSubscriptionDescriptor.getSubscriptions().add(k));
        final int size = activeSubscriptionDescriptor.getSubscriptions().size();
        activeSubscriptionDescriptor.setSize(Integer.toString(size));

        final String resultValue = gson.toJson(activeSubscriptionDescriptor);

        LOG.trace(resultValue);

        final TextMessage textMessage = new TextMessage(resultValue);

        return textMessage;
    }

    @Override
    public void removeAllSubscriptions() {
    }

    public DeviceService getDeviceService() {
        return deviceService;
    }

    public void setDeviceService(final DeviceService deviceService) {
        this.deviceService = deviceService;
    }

}
