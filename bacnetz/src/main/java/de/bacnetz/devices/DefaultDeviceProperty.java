package de.bacnetz.devices;

import de.bacnet.factory.MessageType;

public class DefaultDeviceProperty<T> implements DeviceProperty<T> {

    private String propertyName;

    private int propertyKey;

    private T value;

//    private boolean booleanValue;

    private MessageType messageType;

    /**
     * ctor
     * 
     * @param propertyKey
     * @param value
     * @param messageType
     */
    public DefaultDeviceProperty(final String propertyName, final int propertyKey, final T value,
            final MessageType messageType) {
        super();
        this.propertyName = propertyName;
        this.propertyKey = propertyKey;
        this.value = value;
        this.messageType = messageType;
    }

//    public DefaultDeviceProperty(final String propertyName, final int propertyKey, final boolean booleanValue,
//            final MessageType messageType) {
//        super();
//        this.propertyName = propertyName;
//        this.propertyKey = propertyKey;
////        this.booleanValue = booleanValue;
//        this.messageType = messageType;
//    }

    @Override
    public int getLengthTagValue() {

        if (messageType == MessageType.BOOLEAN) {
            return ((Boolean) value) ? 0x01 : 0x00;
        } else {
            return ((byte[]) getValue()).length;
        }
    }

    @Override
    public int getPropertyKey() {
        return propertyKey;
    }

    @Override
    public void setPropertyKey(final int propertyKey) {
        this.propertyKey = propertyKey;
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public void setValue(final T value) {
        this.value = value;
    }

    @Override
    public MessageType getMessageType() {
        return messageType;
    }

    @Override
    public void setMessageType(final MessageType messageType) {
        this.messageType = messageType;
    }

    @Override
    public String getPropertyName() {
        return propertyName;
    }

    @Override
    public void setPropertyName(final String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    public byte[] getValueAsByteArray() {

        switch (messageType) {

        case BOOLEAN:
            return new byte[] { (byte) (((Boolean) value) ? 0x01 : 0x00) };

        default:
            throw new RuntimeException("Unimplemented type: " + messageType);
        }
    }

//    @Override
//    public boolean getBooleanValue() {
//        return booleanValue;
//    }
//
//    @Override
//    public void setBooleanValue(final boolean booleanValue) {
//        this.booleanValue = booleanValue;
//    }

}
