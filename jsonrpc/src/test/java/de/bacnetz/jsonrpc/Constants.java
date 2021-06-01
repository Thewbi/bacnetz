package de.bacnetz.jsonrpc;

public interface Constants {

    int VENDOR_GEZE = 538;

    String MESSAGE_ID = "ID";
    String MESSAGE_METHOD = "method";
    String MESSAGE_PARAMS = "params";
    String MESSAGE_RESULT = "result";
    String MESSAGE_ERROR_CLASS = "error_class";
    String MESSAGE_ERROR_CODE = "error_code";

    String METHOD_GET_APP_INFO = "getAppInfo";
    String METHOD_SEARCH_DEVICES = "searchDevices";
    String METHOD_GET_DEVICES = "getDevices";
    String METHOD_SET_DEVICE_INFO = "setDeviceInfo";
    String METHOD_GET_OBJECTS = "getObjects";
    String METHOD_GET_PROPERTY = "getProperty";
    String METHOD_GET_PROPERTIES = "getProperties";
    String METHOD_SET_PROPERTY = "setProperty";
    String METHOD_GET_FILE_DATA = "getFileData";
    String METHOD_SET_FILE_DATA = "setFileData";
    String METHOD_REINITIALIZE_DEVICE = "reinitializeDevice";
    String METHOD_DEVICE_COM_CONTROL = "deviceComControl";
    String METHOD_SUBSCRIBE_COV = "subscribeCOV";
    String METHOD_UNSUBSCRIBE_COV = "unsubscribeCOV";
    String METHOD_ADD_NOTIFICATION_RECIPIENT = "addNotificationRecipient";
    String METHOD_REMOVE_NOTIFICATION_RECIPIENT = "removeNotificationRecipient";
    String METHOD_NEW_TIME = "newTime";
    String METHOD_PRIVATE_TRANSFER = "privateTransfer";
    String METHOD_SET_OPTIONS = "setOptions";
    String METHOD_APPLICATION_BACKUP = "applicationBackup";

    String METHOD_SET_DEBUG_FLAGS = "setDebugFlags";
    String METHOD_BACKUP_LOG_FILES = "backupLogFiles";

    String METHOD_BACKUP_DEVICE = "backupBACnetDevice";
    String METHOD_RESTORE_DEVICE = "restoreBACnetDevice";

    String METHOD_CHANGE_OF_VALUE = "changeOfValue";
    String METHOD_EVENT_NOTIFICATION = "eventNotification";

    String METHOD_PREPARE_BACKUP = "prepareBackup";
    String METHOD_END_BACKUP = "endBackup";
    String METHOD_INITIATE_RESTORE = "initiateRestore";
    String METHOD_INITIATE_TIMESYNC = "initiateTimesync";
    String METHOD_INITIATE_REBOOT = "initiateReboot";

    String PARAM_APP_NAME = "appName";
    String PARAM_APP_VERSION = "appVersion";
    String PARAM_RPC_VERSION = "rpcVersion";
    String PARAM_DATALINKS = "datalinks";
    String PARAM_TYPE = "type";
    String PARAM_NETWORK_NUMBER = "networkNumber";

    String PARAM_LOW_LIMIT = "lowLimit";
    String PARAM_HIGH_LIMIT = "highLimit";
    String PARAM_ALL = "all";

    String PARAM_DEVICE_INSTANCE = "deviceInstance";
    String PARAM_VENDOR_ID = "vendorId";
    String PARAM_BACNET_ADDRESS = "bacnetAddress";
    String PARAM_DEVICE_INFO = "deviceInfo";

    String PARAM_INTERNAL = "internal";
    String PARAM_ONLINE = "online";
    String PARAM_LAST_CHANGE = "lastChange";

    String PARAM_OBJECT_NAME = "objectName";
    String PARAM_OBJECT_TYPE = "objectType";
    String PARAM_OBJECT_ID = "objectId";
    String PARAM_PROPERTY_ID = "propertyId";
    String PARAM_PROPERTY_INDEX = "propertyIndex";
    String PARAM_PROPERTY_VALUE = "propertyValue";

    String PARAM_OBJECT_PROPERTIES = "objectProperties";
    String PARAM_PROPERTY_CONTENT = "propertyContent";
    String PARAM_ID = "id";
    String PARAM_VALUE = "value";

    String PARAM_FILE = "file";

    String PARAM_STATE = "state";
    String PARAM_PASSWORD = "password";
    String PARAM_DURAION = "duration";

    String PARAM_POLL_INTERVAL = "pollInterval";

    String PARAM_DIFF = "diff";
    String PARAM_DISTRIBUTE = "distribute";

    String PARAM_NOTIFICATION_CLASS = "notificationClass";
    String PARAM_PRIORITY = "priority";
    String PARAM_EVENT_TYPE = "eventType";
    String PARAM_NOTIFY_TYPE = "notifyType";
    String PARAM_MESSAGE_TEXT = "messageText";
    String PARAM_ACK_REQUIRED = "ackRequired";
    String PARAM_FROM_STATE = "fromState";
    String PARAM_TO_STATE = "toState";
    String PARAM_EVENT_PARAMETER = "eventParameter";

    String PARAM_TIMESTAMP = "timestamp";
    String PARAM_MDAY = "mday";
    String PARAM_MONTH = "month";
    String PARAM_YEAR = "year";
    String PARAM_HOUR = "hour";
    String PARAM_MINUTE = "minute";
    String PARAM_SECOND = "second";
    String PARAM_HUNDREDTHS = "hundredths";

    String PARAM_COLDSTART = "coldstart";

    String PARAM_CONFIRMED = "confirmed";
    String PARAM_SERVICE_NUMBER = "serviceNumber";
    String PARAM_MESSAGE = "message";

    String PARAM_SYMBOL_NAMES = "symbolNames";
    String PARAM_COV_NOTIFICATIONS = "covNotifications";
    String PARAM_EVENT_NOTIFICATIONS = "eventNotifications";
    String PARAM_TIMESYNC = "timesync";
    String PARAM_REINIT_DEVICE = "reinitDevice";

    String PARAM_IN_PROGRESS = "inProgress";
    String PARAM_TIMEOUT = "timeout";

    String PARAM_JRPC = "jrpc";
    String PARAM_DEVICE = "device";
    String PARAM_TIME = "time";
    String PARAM_LINK = "link";
    String PARAM_DEBUG = "debug";

    String PARAM_DIRECTORY = "directory";

}
