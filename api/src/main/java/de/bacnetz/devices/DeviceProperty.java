package de.bacnetz.devices;

import de.bacnet.factory.MessageType;

public interface DeviceProperty<T> {

    /** 0x0C = 12d = application-software-version */
    public static final int APPLICATION_SOFTWARE_VERSION = 0x0C;

    /** 0x1C = 28d description */
    public static final int DESCRIPTION = 0x1C;

    /** 0xC4 = 196d = last-restart-reason */
    public static final int LAST_RESTART_REASON = 0xC4;

    /** 0x70 = 112d = system status */
    public static final int SYSTEM_STATUS = 0x70;

    public static final int SEGMENTATION_SUPPORTED = 0x6B;

    public static final int MAX_APDU_LENGTH_ACCEPTED = 0x3E;

    public static final int MAX_INFO_FRAMES = 0x3F;

    public static final int MAX_MASTER = 0x40;

    public static final int MAX_SEGMENTS_ACCEPTED = 0xA7;

    public static final int DAYLIGHT_SAVINGS_STATUS = 0x18;

    public static final int PROTOCOL_VERSION = 0x62;

    public static final int PROTOCOL_REVISION = 0x8B;

    public static final int DATABASE_REVISION = 0x9B;

    public static final int APDU_TIMEOUT = 0x0B;

    public static final int APDU_SEGMENT_TIMEOUT = 0x0A;

    public static final int ALL = 0x08;

    public static final int NUMBER_OF_APDU_RETRIES = 0x49;

    public static final int UTC_OFFSET = 0x77;

    public static final int ALIGN_INTERVALS = 0xC1;

    public static final int OBJECT_TYPE = 0x4F;

    public static final int PROTOCOL_CONFORMANCE_CLASS = 0x5F;

    public static final int VENDOR_IDENTIFIER = 0x78;

    public static final int BACKUP_FAILURE_TIMEOUT = 0x99;

    public static final int CONFIGURATION_FILES = 0x9A;

    public static final int AUTO_SLAVE_DISCOVERY = 0xA9;

    public static final int SLAVE_PROXY_ENABLE = 0xAC;

    public static final int INTERVALL_OFFSET = 0xC3;

    public static final int TIME_SYNCHRONIZATION_INTERVALL = 0xCC;

    public static final int EVENT_STATE = 0x24;

    public static final int OUT_OF_SERVICE = 0x51;

    public static final int NUMBER_OF_STATES = 0x4A;

    public static final int STATE_TEXT = 0x6E;

    public static final int VENDOR_NAME = 0x79;

    int getPropertyKey();

    void setPropertyKey(int propertyKey);

    T getValue();

    void setValue(T value);

    byte[] getValueAsByteArray();

    MessageType getMessageType();

    void setMessageType(MessageType messageType);

    String getPropertyName();

    void setPropertyName(String propertyName);

//    boolean getBooleanValue();
//
//    void setBooleanValue(boolean booleanValue);

    int getLengthTagValue();

}
