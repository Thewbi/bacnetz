package de.bacnetz.controller;

import de.bacnetz.stack.APDU;
import de.bacnetz.stack.NPDU;
import de.bacnetz.stack.VirtualLinkControl;

public class DefaultMessage implements Message {

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
		this.apdu = new APDU(message.getApdu());
	}

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
	public byte[] getBytes() {

		int length = 0;

		if (virtualLinkControl != null) {
			length += virtualLinkControl.getDataLength();
		}

		if (npdu != null) {
			length += npdu.getDataLength();
		}

		if (apdu != null) {
			length += apdu.getDataLength();
		}

		final byte[] data = new byte[length];
		int offset = 0;

		if (virtualLinkControl != null) {
			virtualLinkControl.toBytes(data, offset);
			offset += virtualLinkControl.getDataLength();
		}

		if (npdu != null) {
			npdu.toBytes(data, offset);
			offset += npdu.getDataLength();
		}

		if (apdu != null) {
			apdu.toBytes(data, offset);
			offset += apdu.getDataLength();
		}

		return data;
	}

	@Override
	public VirtualLinkControl getVirtualLinkControl() {
		return virtualLinkControl;
	}

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

}
