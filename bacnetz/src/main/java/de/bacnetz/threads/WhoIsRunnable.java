package de.bacnetz.threads;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.List;

import de.bacnetz.common.utils.NetworkUtils;
import de.bacnetz.configuration.ConfigurationManager;
import de.bacnetz.controller.Message;
import de.bacnetz.factory.DefaultMessageFactory;
import de.bacnetz.factory.MessageFactory;

public class WhoIsRunnable implements Runnable {

    @Override
    public void run() {
        while (true) {
            try {
                runBroadcast();
            } catch (final SocketException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(10000);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void runBroadcast() throws SocketException {

//      LOG.info("runBroadcast() ...");

        // create the who-is message
        final MessageFactory messageFactory = new DefaultMessageFactory();
//      final Message whoIsMessage = messageFactory.whoIsMessage(0, 100);
        final Message whoIsMessage = messageFactory.whoIsMessage();

        // send the who-is message to all interfaces
        final List<InetAddress> listAllBroadcastAddresses = NetworkUtils.listAllBroadcastAddresses();

        // DEBUG
//      LOG.info(listAllBroadcastAddresses);

        listAllBroadcastAddresses.stream().forEach(a -> {
            try {
                broadcast(whoIsMessage.getBytes(), a);
            } catch (final IOException e) {
                e.printStackTrace();
            }
        });

//      LOG.info("runBroadcast() done.");
    }

    public static void broadcast(final byte[] buffer, final InetAddress address) throws IOException {

//      LOG.info(">>> broadcast: " + Utils.byteArrayToStringNoPrefix(buffer) + " to address: " + address);

        // this socket does not bind on a specific port
        final DatagramSocket socket = new DatagramSocket();
        socket.setBroadcast(true);
        final DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address,
                ConfigurationManager.BACNET_PORT_DEFAULT_VALUE);
        socket.send(packet);
        socket.close();
    }

}
