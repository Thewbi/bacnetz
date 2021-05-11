package de.bacnetz.controller;

import java.util.List;

import de.bacnetz.mstp.Header;
import de.bacnetz.services.CommunicationService;

public interface MessageController {

    List<Message> processMessage(Message message);

    List<Message> processMessage(Header mstpHeader, Message message);

    void setCommunicationService(CommunicationService communicationService);

}
