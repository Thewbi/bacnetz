package de.bacnetz.services;

import java.io.IOException;
import java.net.InetAddress;

import de.bacnetz.controller.Message;

public interface CommunicationService {

    void pointToPointMessage(Message responseMessage, InetAddress datagramPacketAddress) throws IOException;

}
