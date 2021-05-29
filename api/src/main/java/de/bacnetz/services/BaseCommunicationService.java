package de.bacnetz.services;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import de.bacnetz.configuration.ConfigurationManager;
import de.bacnetz.controller.Message;

public abstract class BaseCommunicationService implements CommunicationService {

    protected abstract DatagramSocket getDatagramSocket();

    @Override
    public void pointToPointMessage(final Message requestMessage, final Message responseMessage,
            final InetAddress datagramPacketAddress) throws IOException {

        final byte[] bytes = responseMessage.getBytes();

        if (responseMessage.getVirtualLinkControl().getLength() != bytes.length) {
            throw new RuntimeException(
                    "Message is invalid! The length in the virtual link control does not match the real data length!");
        }

        int port = ConfigurationManager.BACNET_PORT_DEFAULT_VALUE;
        if (requestMessage.getSourceInetSocketAddress() != null) {
            port = requestMessage.getSourceInetSocketAddress().getPort();
        }

        final InetAddress destinationAddress = datagramPacketAddress;
        final DatagramPacket responseDatagramPacket = new DatagramPacket(bytes, bytes.length, destinationAddress, port);

        getDatagramSocket().send(responseDatagramPacket);
    }

}
