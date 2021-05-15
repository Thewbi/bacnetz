package de.bacnetz.common.websocket.subscriptions;

public class UserSocketEvent {

    private final String name;

    private final Object event;

    public UserSocketEvent(final String name, final Object event) {
        this.name = name;
        this.event = event;
    }

    public String getName() {
        return name;
    }

    public Object getEvent() {
        return event;
    }

}
