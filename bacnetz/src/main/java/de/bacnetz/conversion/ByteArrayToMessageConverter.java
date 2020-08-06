package de.bacnetz.conversion;

import java.util.Map;

import de.bacnetz.controller.DefaultMessage;
import de.bacnetz.stack.APDU;
import de.bacnetz.stack.NPDU;
import de.bacnetz.stack.VirtualLinkControl;

public class ByteArrayToMessageConverter implements Converter<byte[], DefaultMessage> {

    private int payloadLength;

    private Map<Integer, String> vendorMap;

    @Override
    public void convert(final byte[] data, final DefaultMessage defaultMessage) {

        int offset = 0;

        // deserialize the virtual link control part of the message
        final VirtualLinkControl virtualLinkControl = new VirtualLinkControl();
        virtualLinkControl.fromBytes(data, 0);
        offset += virtualLinkControl.getStructureLength();

        // deserialize the NPDU part of the message
        final NPDU npdu = new NPDU();
        npdu.fromBytes(data, offset);
        offset += npdu.getStructureLength();

        APDU apdu = null;
        if (npdu.isAPDUMessage()) {

            // deserialize the APDU part of the message
            apdu = new APDU();
            apdu.setVendorMap(vendorMap);
            apdu.fromBytes(data, offset, payloadLength);
            offset += apdu.getStructureLength();
        }

        defaultMessage.setVirtualLinkControl(virtualLinkControl);
        defaultMessage.setNpdu(npdu);
        defaultMessage.setApdu(apdu);
    }

    @Override
    public DefaultMessage convert(final byte[] data) {

        final DefaultMessage defaultMessage = new DefaultMessage();
        convert(data, defaultMessage);

        return defaultMessage;
    }

    public int getPayloadLength() {
        return payloadLength;
    }

    public void setPayloadLength(final int payloadLength) {
        this.payloadLength = payloadLength;
    }

    public Map<Integer, String> getVendorMap() {
        return vendorMap;
    }

    public void setVendorMap(final Map<Integer, String> vendorMap) {
        this.vendorMap = vendorMap;
    }

}
