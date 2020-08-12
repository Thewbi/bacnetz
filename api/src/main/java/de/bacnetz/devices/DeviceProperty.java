package de.bacnetz.devices;

import java.util.Collection;

import de.bacnetz.factory.MessageType;
import de.bacnetz.stack.ServiceParameter;

public interface DeviceProperty<T> {

    /** 0x0C = 12d = application-software-version */
    public static final int APPLICATION_SOFTWARE_VERSION = 0x0C;

    /** 0x11 = 17d */
    public static final int NOTIFICATION_CLASS = 0x11;

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

    /** 0xD1 = 209d */
    public static final int STRUCTURED_OBJECT_LIST = 0xD1;

    /** 0x4B = 75d */
    public static final int OBJECT_IDENTIFIER = 0x4B;

    /** 0x4c = 76d */
    public static final int OBJECT_LIST = 0x4c;

    /** 0x4d = 77d */
    public static final int OBJECT_NAME = 0x4d;

    /** 0x55 = 85 */
    public static final int PRESENT_VALUE = 0x55;

    public static final int STATUS_FLAGS = 0x6F;

    public static final int PROPERTY_LIST = 0x0173;

    public static final int STATUS_TEXT = 0x6E;

    public static final int POLARITY = 0x54;

    /** 0x2E = 46d - inactive-text */
    public static final int INACTIVE_TEXT = 0x2E;

    /** 0x04 = 4d - active-text */
    public static final int ACTIVE_TEXT = 0x04;

    public static final int MODEL_NAME = 0x46;

    public static final int FIRMWARE_REVISION = 0x2C;

    public static final int TIME_OF_STATE_COUNT_RESET = 0x73;

    /** 0x61 = 97d - protocol-services-supported */
    public static final int PROTOCOL_SERVICES_SUPPORTED = 0x61;

    public static final int TIME_OF_DEVICE_RESTART = 0xCB;

    public static final int DEVICE_ADDRESS_BINDING = 0x1E;

    public static final int LOCAL_TIME = 0x39;

    public static final int LOCAL_DATE = 0x38;

    public static final int LOCATION = 0x3A;

    /** 0xCA = 202d - restart-notification-recipients */
    public static final int RESTART_NOTIFICATION_RECIPIENTS = 0xCA;

    /** 0x98 = 152d - active-cov-subscriptions */
    public static final int ACTIVE_COV_SUBSCRIPTION = 0x98;

    /** 0x56 = 86d - priority */
    public static final int PRIORITY = 0x56;

    int getPropertyKey();

    void setPropertyKey(int propertyKey);

    T getValue();

    void setValue(T value);

    byte[] getValueAsByteArray();

    MessageType getMessageType();

    void setMessageType(MessageType messageType);

    String getPropertyName();

    void setPropertyName(String propertyName);

    int getLengthTagValue();

    Collection<ServiceParameter> getServiceParameters();

    String getValueAsString();

}
