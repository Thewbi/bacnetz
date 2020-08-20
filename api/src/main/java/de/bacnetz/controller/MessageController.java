package de.bacnetz.controller;

import java.util.List;

import de.bacnetz.services.CommunicationService;

public interface MessageController {

    List<Message> processMessage(Message message);

    void setCommunicationService(CommunicationService communicationService);

}
