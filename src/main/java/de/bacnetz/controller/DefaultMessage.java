package de.bacnetz.controller;

import org.apache.commons.collections4.CollectionUtils;

import de.bacnetz.stack.APDU;
import de.bacnetz.stack.NPDU;
import de.bacnetz.stack.ServiceParameter;
import de.bacnetz.stack.VirtualLinkControl;

public class DefaultMessage implements Message {

	private VirtualLinkControl virtualLinkControl;

	private NPDU npdu;

	private APDU apdu;

	public DefaultMessage() {

	}

	public DefaultMessage(final VirtualLinkControl virtualLinkControl, final NPDU npdu, final APDU apdu) {
		this.virtualLinkControl = virtualLinkControl;
		this.npdu = npdu;
		this.apdu = apdu;
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

			if (CollectionUtils.isNotEmpty(apdu.getServiceParameters())) {
				for (final ServiceParameter serviceParameter : apdu.getServiceParameters()) {
					length += serviceParameter.getDataLength();
				}
			}
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

			if (CollectionUtils.isNotEmpty(apdu.getServiceParameters())) {

				for (final ServiceParameter serviceParameter : apdu.getServiceParameters()) {

					serviceParameter.toBytes(data, offset);
					offset += serviceParameter.getDataLength();
				}
			}
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
