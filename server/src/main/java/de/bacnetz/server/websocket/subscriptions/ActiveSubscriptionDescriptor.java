package de.bacnetz.server.websocket.subscriptions;

import java.util.ArrayList;
import java.util.List;

public class ActiveSubscriptionDescriptor {

    private List<String> subscriptions = new ArrayList<>();

    private String size;

    public List<String> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(final List<String> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public String getSize() {
        return size;
    }

    public void setSize(final String size) {
        this.size = size;
    }

}
