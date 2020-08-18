package de.bacnetz.stack;

import de.bacnetz.services.CommunicationService;

public interface COVSubscription {

    String getClientIp();

    void setClientIp(String clientIp);

    int getSubscriberProcessId();

    void setSubscriberProcessId(int subscriberProcessId);

    boolean isIssueConfirmedNotifications();

    void setIssueConfirmedNotifications(boolean issueConfirmedNotifications);

    CommunicationService getCommunicationService();

    void setCommunicationService(CommunicationService communicationService);

    void valueChanged(Object newValue);

}
