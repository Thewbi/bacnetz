package de.bacnetz.controller;

import de.bacnetz.stack.APDU;
import de.bacnetz.stack.NPDU;
import de.bacnetz.stack.VirtualLinkControl;

public interface Message {

	VirtualLinkControl getVirtualLinkControl();

	NPDU getNpdu();

	APDU getApdu();

	byte[] getBytes();

}
