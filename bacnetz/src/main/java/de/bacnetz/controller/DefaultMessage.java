package de.bacnetz.controller;

import java.net.InetSocketAddress;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.bacnetz.common.utils.Utils;
import de.bacnetz.stack.APDU;
import de.bacnetz.stack.NPDU;
import de.bacnetz.stack.VirtualLinkControl;
import de.bacnetz.stack.exception.BACnetzException;

public class DefaultMessage implements Message {

    private static final Logger LOG = LogManager.getLogger(DefaultMessage.class);

    private InetSocketAddress sourceInetSocketAddress;

    private VirtualLinkControl virtualLinkControl;

    private NPDU npdu;

    private APDU apdu;

    public DefaultMessage() {

    }

    public DefaultMessage(final VirtualLinkControl virtualLinkControl, final NPDU npdu, final APDU apdu) {
        this.virtualLinkControl = new VirtualLinkControl(virtualLinkControl);
        this.npdu = new NPDU(npdu);
        this.apdu = new APDU(apdu);
    }

    public DefaultMessage(final Message message) {
        this.virtualLinkControl = new VirtualLinkControl(message.getVirtualLinkControl());
        this.npdu = new NPDU(message.getNpdu());
        if (message.getApdu() != null) {
            this.apdu = new APDU(message.getApdu());
        }
    }

    @Override
    public int getDataLength() {

        int dataLength = 0;

        if (virtualLinkControl != null) {
            dataLength += virtualLinkControl.getDataLength();
        }
        if (npdu != null) {
            dataLength += npdu.getDataLength();
        }
        if (apdu != null) {
            dataLength += apdu.getDataLength();
        }

        return dataLength;
    }

    @Override
    public byte[] getBytes() throws BACnetzException {

        int length = 0;

        int virtualLinkControlDataLength = 0;
        int npduDataLength = 0;
        int apduDataLength = 0;

        if (virtualLinkControl != null) {
            virtualLinkControlDataLength = virtualLinkControl.getDataLength();
            LOG.trace("VirtualLinkControl.getDataLenght() {}", virtualLinkControlDataLength);

            length += virtualLinkControlDataLength;
        }

        if (npdu != null) {
            npduDataLength = npdu.getDataLength();
            LOG.trace("NPDU.getDataLenght() {}", npduDataLength);

            length += npduDataLength;
        }

        if (apdu != null) {
            apduDataLength = apdu.getDataLength();
            LOG.trace("APDU.getDataLenght() {}", apduDataLength);

            length += apduDataLength;
        }

        final byte[] data = new byte[length];
        int offset = 0;

        if (virtualLinkControl != null) {
            virtualLinkControl.toBytes(data, offset);
            offset += virtualLinkControlDataLength;
        }

        if (npdu != null) {
            npdu.toBytes(data, offset);
            offset += npduDataLength;
        }

        if (apdu != null) {
            apdu.toBytes(data, offset);
            offset += apduDataLength;
        }

        return data;
    }

    public void merge(final DefaultMessage rhs) {

        final byte[] payload = apdu.getPayload();
        final byte[] mergedPayload = ArrayUtils.addAll(payload, rhs.getApdu().getPayload());

        LOG.info("OldPayload: " + Utils.bytesToHex(payload));
        LOG.info("NewPayload: " + Utils.bytesToHex(rhs.getApdu().getPayload()));
        LOG.info("MergedPayload: " + Utils.bytesToHex(mergedPayload));

        apdu.setPayload(mergedPayload);
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("\n");
        stringBuilder.append("sourceInetSocketAddress: ").append(sourceInetSocketAddress).append("\n");
        stringBuilder.append("virtualLinkControl: ").append(virtualLinkControl).append("\n");
        stringBuilder.append("npdu: ").append(npdu).append("\n");
        stringBuilder.append("apdu: ").append(apdu).append("\n");

        return stringBuilder.toString();
    }

    @Override
    public void recomputeLength() {
        virtualLinkControl.setLength(this.getDataLength());
    }

    @Override
    public VirtualLinkControl getVirtualLinkControl() {
        return virtualLinkControl;
    }

    @Override
    public void setVirtualLinkControl(final VirtualLinkControl virtualLinkControl) {
        this.virtualLinkControl = virtualLinkControl;
    }

    @Override
    public NPDU getNpdu() {
        return npdu;
    }

    public void setNpdu(final NPDU npdu) {
        this.npdu = npdu;
    }

    @Override
    public APDU getApdu() {
        return apdu;
    }

    public void setApdu(final APDU apdu) {
        this.apdu = apdu;
    }

    @Override
    public InetSocketAddress getSourceInetSocketAddress() {
        return sourceInetSocketAddress;
    }

    @Override
    public void setSourceInetSocketAddress(final InetSocketAddress sourceInetSocketAddress) {
        this.sourceInetSocketAddress = sourceInetSocketAddress;
    }

}
