package de.bacnetz.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.bacnetz.stack.APDU;
import de.bacnetz.stack.NPDU;
import de.bacnetz.stack.VirtualLinkControl;

public class DefaultMessage implements Message {

	private static final Logger LOG = LogManager.getLogger(DefaultMessage.class);

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
