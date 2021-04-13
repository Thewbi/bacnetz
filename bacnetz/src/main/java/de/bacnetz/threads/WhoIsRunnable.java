package de.bacnetz.threads;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.bacnetz.common.utils.NetworkUtils;
import de.bacnetz.common.utils.Utils;
import de.bacnetz.configuration.ConfigurationManager;
import de.bacnetz.controller.Message;
import de.bacnetz.factory.DefaultMessageFactory;
import de.bacnetz.factory.MessageFactory;

/**
 * Sends a bacnet who-is broadcast on a periodic interval.
 */
public class WhoIsRunnable implements Runnable {

    private static final int WHO_IS_INTERVAL_IN_MS_DEFAULT = 10000;

    private static final Logger LOG = LogManager.getLogger(WhoIsRunnable.class);

    private int intervalInMS = WHO_IS_INTERVAL_IN_MS_DEFAULT;

    /**
     * ctor
     */
    public WhoIsRunnable() {

    }

    /**
     * ctor
     * 
     * @param interval amount in ms between broadcasts
     */
    public WhoIsRunnable(final int intervalInMS) {
        this.intervalInMS = intervalInMS;
    }

    @Override
    public void run() {
        while (true) {

            try {
                runBroadcast();
            } catch (final SocketException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(intervalInMS);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void runBroadcast() throws SocketException {

        LOG.trace("runBroadcast() ...");

        // create the who-is message
        final MessageFactory messageFactory = new DefaultMessageFactory();

        // send address range
//      final Message whoIsMessage = messageFactory.whoIsMessage(0, 100);

        // send unbounded request
        final Message whoIsMessage = messageFactory.whoIsMessage();

        // send the who-is message to all interfaces
        final List<InetAddress> listAllBroadcastAddresses = NetworkUtils.listAllBroadcastAddresses();

        // DEBUG
        LOG.trace(listAllBroadcastAddresses);

        listAllBroadcastAddresses.stream().forEach(a -> {
            try {
                broadcast(whoIsMessage.getBytes(), a);
            } catch (final IOException e) {
                e.printStackTrace();
            }
        });

        LOG.trace("runBroadcast() done.");
    }

    /**
     * Broadcast a message. Broadcasts are sent on the special broadcast address
     * which ends with 255 in most cases.
     * 
     * Remember if networks are connected by routers, routers will be configured by
     * the admins to block broadcasts and to contain them inside local networks to
     * prevent flooding of entire internetworks. If you want to broadcast bacnet
     * messages accross networks separated by routers, you will have to setup a BBMD
     * (BACnet Broadcast Management Device) infrastructure! They wrap broadcasts
     * into point to point messages and carry them accross routers into other
     * networks!
     * 
     * @param buffer
     * @param address
     * @throws IOException
     */
    public static void broadcast(final byte[] buffer, final InetAddress address) throws IOException {

        LOG.trace(">>> broadcast: " + Utils.byteArrayToStringNoPrefix(buffer) + " to address: " + address);

        // this socket does not bind on a specific port
        final DatagramSocket socket = new DatagramSocket();
        socket.setBroadcast(true);
        final DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address,
                ConfigurationManager.BACNET_PORT_DEFAULT_VALUE);
        socket.send(packet);
        socket.close();
    }

    public int getIntervalInMS() {
        return intervalInMS;
    }

    public void setIntervalInMS(final int intervalInMS) {
        this.intervalInMS = intervalInMS;
    }

}
