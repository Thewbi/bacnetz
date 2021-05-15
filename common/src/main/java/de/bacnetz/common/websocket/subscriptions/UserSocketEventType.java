package de.bacnetz.common.websocket.subscriptions;

/**
 * All possible event types, which can be send thru the usersocket to the user.
 * 
 * @author Alexander Wagner <alexander.wagner@inside-m2m.de>
 *
 */
public enum UserSocketEventType {

    // @formatter:off

	/**
	 * Raw HDM Property change Event
	 */
	DEVICE_ADDED,
	DEVICE_REMOVED,
	DEVICE_REFERENCE_CREATED,
	DEVICE_REFERENCE_UPDATED,
	DEVICE_REFERENCE_DELETED,
	GROUP_CREATED,
	GROUP_UPDATED,
	GROUP_DELETED,

	ALARM_POPUP,
	ALARM_POPUP_RESOLVE,
	ALARM_EMAIL_SENDING_FAILED,
	DATE_TIME_UPDATE,
	EVENTLOG,
	EVENTLOG_STORAGE_LIMIT_ESCALATION,
	COLOR_CHANGED,
	
	BACKGROUND_RUNNING,
	
	SUCCESS,
	ERROR,
	RESTART;
    
    // formatter:on
}
