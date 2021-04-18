package de.bacnetz.template;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import org.apache.logging.log4j.Logger;

import de.bacnetz.common.utils.Utils;
import de.bacnetz.controller.DefaultMessage;
import de.bacnetz.controller.Message;
import de.bacnetz.conversion.ByteArrayToMessageConverter;
import de.bacnetz.stack.APDU;
import de.bacnetz.stack.NPDU;
import de.bacnetz.stack.PDUType;
import de.bacnetz.stack.VirtualLinkControl;
import de.bacnetz.template.callback.BACNetzCallbackHandler;
import de.bacnetz.vendor.VendorMap;

/**
 * Request / Response infrastructure which support segmented messaging. Calls an
 * optional callback once the response is in.
 */
public abstract class BaseBACnetzTemplate implements BACnetzTemplate {

    private String sourceIP;

    private int sourcePort;

    private String destinationIP;

    private int destinationPort;

    private BACNetzCallbackHandler bacnetzCallbackHandler;

    public abstract Logger getLogger();

    /**
     * <ol>
     * <li />Uses the message factory to create a BACnet message
     * <li />Creates a UDP socket to talk to the communication partner
     * <li />Converts the BACnet message into a byte buffer and sends the byte
     * buffer via a UDP packet over the UDP socket
     * <li />Retrieves the (potentially segmented) response packets and reassembles
     * them into a byte buffer.
     * <li />Parses a BACnet messages from the byte buffer.
     * <li />Hands over the response BACnet message to a optional callback handler
     * for further processing.
     * </ol>
     */
    protected void sendInternal(final Message outMessage)
            throws UnknownHostException, SocketException, IOException, FileNotFoundException {
        // create a UDP packet from the BACnet request message
        final byte[] outBuffer = outMessage.getBytes();
        final InetAddress destinationAddress = InetAddress.getByName(destinationIP);
        final DatagramPacket outPacket = new DatagramPacket(outBuffer, outBuffer.length, destinationAddress,
                destinationPort);

        // create a UDP socket to send the UDP packet from
        final DatagramSocket socket = socketByInterfaceIPAndPort(sourceIP, sourcePort);

        // full send
        getLogger().info("Sending Packet to '{}' ...", destinationIP);
        socket.send(outPacket);
        getLogger().info("Sending Packet to '{}' done.", destinationIP);

        // response
        processResponse(sourceIP, sourcePort, destinationIP, destinationPort, socket);
    }

    private void processResponse(final String sourceIP, final int sourcePort, final String destinationIP,
            final int destinationPort, final DatagramSocket socket) throws IOException, FileNotFoundException {

        DefaultMessage sequencedMessage = null;

        boolean done = false;
        while (!done) {

            getLogger().info("Packet receiving ...");
            final byte[] inBuffer = new byte[512];
            final DatagramPacket inPacket = new DatagramPacket(inBuffer, inBuffer.length);
            socket.receive(inPacket);
            getLogger().info("Packet receiving done.");

            final byte[] data = inPacket.getData();
            final int length = inPacket.getLength();
            final int offset = inPacket.getOffset();

            final String bytesToHex = Utils.bytesToHex(data, offset, length);
            getLogger().info("Response Packet received as hex bytes: '{}'", bytesToHex);

            final ByteArrayToMessageConverter converter = new ByteArrayToMessageConverter();
            converter.setPayloadLength(length);
            converter.setPayloadOffset(offset);
            converter.setVendorMap(VendorMap.processVendorMap());

            final DefaultMessage responseDefaultMessage = converter.convert(data);

            getLogger().info(responseDefaultMessage);
            getLogger().info("MoreSegmentsFollow: " + responseDefaultMessage.getApdu().isMoreSegmentsFollow());

            done = true;

            // segmented response
            if (responseDefaultMessage.getApdu().isMoreSegmentsFollow()) {

                done = false;

                // reassemble the sequence, start by storing the first message in the sequence
                // into a local variable
                if (sequencedMessage == null) {
                    sequencedMessage = responseDefaultMessage;
                } else {
                    sequencedMessage.merge(responseDefaultMessage);
                }

            } else {

                // merge the last packet it
                if (sequencedMessage != null) {
                    sequencedMessage.merge(responseDefaultMessage);
                } else {
                    sequencedMessage = responseDefaultMessage;
                }

            }

            // this segment has to be acknowledged
            if (responseDefaultMessage.getNpdu().isConfirmedRequestPDUPresent()) {
                sendAck(socket, sourceIP, sourcePort, destinationIP, destinationPort,
                        responseDefaultMessage.getApdu().getInvokeId(),
                        responseDefaultMessage.getApdu().getSequenceNumber());
            }
        }

        // After all segments have been reassembled...
        //
        // process service parameters inside the APDU. The APDU will parse the service
        // parameters dump them to the console and store them in it's service parameter
        // list for further processing
        sequencedMessage.getApdu().processPayload(sequencedMessage.getApdu().getPayload(), 0,
                sequencedMessage.getApdu().getPayload().length, 0);

        // After all segments have been reassembled...
        //
        // pass the APDU to the bacnetzCallbackHandler
        if (bacnetzCallbackHandler != null) {
            bacnetzCallbackHandler.process(sequencedMessage.getApdu());
        }
    }

    /**
     * Send acknowledges so the partner knows this message was successfully
     * received.<br />
     * <br />
     * 
     * UDP packets can get lost because there is no inherent acknowledge mechanism
     * defined in the UDP protocol (such as is the case for TCP).<br />
     * <br />
     * 
     * For that reason, BACnet defines it's own (optional) acknowelding mechanism.
     * If the partner wants their messages to be acknowledged, they send confirmed
     * request via a setting in the NPDU. Confirmed requests have to be answered by
     * acknowledge messages by the communication partner.
     * 
     * @param socket
     * @param sourceIP
     * @param sourcePort
     * @param destinationIP
     * @param destinationPort
     * @param invokeId
     * @param sequenceNumber
     * @throws IOException
     */
    private void sendAck(final DatagramSocket socket, final String sourceIP, final int sourcePort,
            final String destinationIP, final int destinationPort, final int invokeId, final int sequenceNumber)
            throws IOException {

        final VirtualLinkControl virtualLinkControl = new VirtualLinkControl();
        virtualLinkControl.setType(0x81);
        virtualLinkControl.setFunction(0x0A);
        // is set later, when the full package data was added
        virtualLinkControl.setLength(0x00);

        // NPDU including destination network information
        final NPDU outNpdu = new NPDU();
        outNpdu.setVersion(0x01);

        // no additional information
        // this works, if the cp is connected to the device directly via 192.168.2.1
        outNpdu.setControl(0x00);
        // npdu.setControl(0x2c);

        final APDU outApdu = new APDU();
        outApdu.setPduType(PDUType.SEGMENT_ACK_PDU);
        outApdu.setInvokeId(invokeId);
        outApdu.setSequenceNumber(sequenceNumber);
        outApdu.setProposedWindowSize(16);

        final DefaultMessage outMessage = new DefaultMessage();
        outMessage.setVirtualLinkControl(virtualLinkControl);
        outMessage.setNpdu(outNpdu);
        outMessage.setApdu(outApdu);

        virtualLinkControl.setLength(outMessage.getDataLength());

        final byte[] outBuffer = outMessage.getBytes();

        final InetAddress address = InetAddress.getByName(destinationIP);
        final DatagramPacket outPacket = new DatagramPacket(outBuffer, outBuffer.length, address, destinationPort);

        socket.send(outPacket);

        getLogger().info("Packet sent!");
    }

    /**
     * Create a UDP socket for the source ip and port to send UDP packets from.
     * 
     * ONLY WORKS WITH UDP!!! ONLY WORKS WITH UDP!!! ONLY WORKS WITH UDP!!! ONLY
     * WORKS WITH UDP!!! ONLY WORKS WITH UDP!!! ONLY WORKS WITH UDP!!! ONLY WORKS
     * WITH UDP!!! ONLY WORKS WITH UDP!!! ONLY WORKS WITH UDP!!! ONLY WORKS WITH
     * UDP!!! ONLY WORKS WITH UDP!!! ONLY WORKS WITH UDP!!!
     * 
     * @param ip
     * @param port
     * @return
     * @throws SocketException
     * @throws UnknownHostException
     */
    private DatagramSocket socketByInterfaceIPAndPort(final String ip, final int port)
            throws SocketException, UnknownHostException {

        final InetAddress inetAddress = findNetworkInterfaceForIp(ip);

        if (inetAddress == null) {
            getLogger().error("Error getting the Network Interface");
            return null;
        }

        getLogger().trace("Using the Address: " + inetAddress);

        // assemble and return a socket for this address
        final InetSocketAddress inetSocketAddress = new InetSocketAddress(inetAddress, port);

        return new DatagramSocket(inetSocketAddress);
    }

    /**
     * Find the interface amongst all the PCs interfaces that is responsible for
     * this source ip.
     * 
     * @param ip
     * @return
     * @throws SocketException
     * @throws UnknownHostException
     */
    private InetAddress findNetworkInterfaceForIp(final String ip) throws SocketException, UnknownHostException {

        NetworkInterface networkInterface = null;
        InetAddress inetAddress = null;

        // look at all network interfaces
        final Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        while (networkInterfaces.hasMoreElements()) {

            final NetworkInterface tempNetworkInterface = networkInterfaces.nextElement();

            // check if this network interface supports the ip
            final Enumeration<InetAddress> networkInterfaceAddresses = tempNetworkInterface.getInetAddresses();
            while (networkInterfaceAddresses.hasMoreElements()) {

                final InetAddress networkInterfaceAddress = networkInterfaceAddresses.nextElement();

                getLogger().info(tempNetworkInterface + " -- " + networkInterfaceAddress);

                // if a match is found, abort the search and return the address
                if (networkInterfaceAddress.equals(InetAddress.getByName(ip))) {
                    networkInterface = tempNetworkInterface;
                    inetAddress = networkInterfaceAddress;
                    break;
                }
            }

            if (networkInterface != null) {
                break;
            }
        }

        return inetAddress;
    }

    public BACNetzCallbackHandler getBacnetzCallbackHandler() {
        return bacnetzCallbackHandler;
    }

    public void setBacnetzCallbackHandler(final BACNetzCallbackHandler bacnetzCallbackHandler) {
        this.bacnetzCallbackHandler = bacnetzCallbackHandler;
    }

    public String getSourceIP() {
        return sourceIP;
    }

    public void setSourceIP(final String sourceIP) {
        this.sourceIP = sourceIP;
    }

    public int getSourcePort() {
        return sourcePort;
    }

    public void setSourcePort(final int sourcePort) {
        this.sourcePort = sourcePort;
    }

    public String getDestinationIP() {
        return destinationIP;
    }

    public void setDestinationIP(final String destinationIP) {
        this.destinationIP = destinationIP;
    }

    public int getDestinationPort() {
        return destinationPort;
    }

    public void setDestinationPort(final int destinationPort) {
        this.destinationPort = destinationPort;
    }

}
