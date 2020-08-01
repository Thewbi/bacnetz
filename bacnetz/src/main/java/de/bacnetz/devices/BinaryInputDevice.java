package de.bacnetz.devices;

import de.bacnet.factory.MessageType;
import de.bacnetz.controller.Message;

public class BinaryInputDevice extends DefaultDevice {

    private boolean presentValue = false;

    @Override
    protected Message processPresentValueProperty(final int propertyIdentifierCode, final Message requestMessage) {

        final int value = presentValue ? 1 : 0;

        return getMessageFactory().create(MessageType.ENUMERATED, this, requestMessage.getApdu().getInvokeId(),
                propertyIdentifierCode, new byte[] { (byte) value });
    }

    public boolean isPresentValue() {
        return presentValue;
    }

    public void setPresentValue(final boolean presentValue) {
        this.presentValue = presentValue;
    }

}
