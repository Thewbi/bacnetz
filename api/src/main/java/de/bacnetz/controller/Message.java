package de.bacnetz.controller;

import java.net.InetSocketAddress;

import de.bacnetz.stack.APDU;
import de.bacnetz.stack.NPDU;
import de.bacnetz.stack.VirtualLinkControl;
import de.bacnetz.stack.exception.BACnetzException;

public interface Message {

    VirtualLinkControl getVirtualLinkControl();

    NPDU getNpdu();

    APDU getApdu();

    byte[] getBytes() throws BACnetzException;

    void recomputeLength();

    InetSocketAddress getSourceInetSocketAddress();

    void setSourceInetSocketAddress(final InetSocketAddress sourceInetSocketAddress);

    int getDataLength();

}
