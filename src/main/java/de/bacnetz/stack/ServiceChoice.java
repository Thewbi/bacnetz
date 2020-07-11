package de.bacnetz.stack;

/**
 * ANSI/ASHRAE Standard 135-2012 Page 650.
 * 
 * ******************* Unconfirmed Request Productions ********************
 * 
 * <pre>
 * BACnetUnconfirmedServiceChoice ::= ENUMERATED {
 * i-Am (0),
 * i-Have (1),
 * unconfirmedCOVNotification (2),
 * unconfirmedEventNotification (3),
 * unconfirmedPrivateTransfer (4),
 * unconfirmedTextMessage (5),
 * timeSynchronization (6),
 * who-Has (7),
 * who-Is (8),
 * utcTimeSynchronization (9),
 * writeGroup (10)
 * </pre>
 */
public enum ServiceChoice {

	/** 20.1.2 BACnet-Confirmed-Request-PDU */
	I_AM(0x00),

	/** 20.1.3 BACnet-Unconfirmed-Request-PDU */
	I_HAVE(0x01),

	/** 20.1.4 BACnet-SimpleACK-PDU */
	UNCONFIRMED_COV_NOTIFICATION(0x02),

	/** 20.1.5 BACnet-ComplexACK-PDU */
	UNCONFIRMED_EVENT_NOTIFICATION(0x03),

	/** 20.1.6 BACnet-SegmentACK-PDU */
	UNCONFIRMED_PRIVATE_TRANSFER(0x04),

	/** 20.1.7 BACnet-Error-PDU */
	UNCONFIRMED_TEXT_MESSAGE(0x05),

	/** 20.1.8 BACnet-Reject-PDU */
	TIME_SYNCHRONIZATION(0x06),

	/** 20.1.9 BACnet-Abort-PDU */
	WHO_HAS(0x07),

	/** 20.1.2 BACnet-Confirmed-Request-PDU */
	WHO_IS(0x08),

	UTC_TIME_SYNCHRONIZATION(0x09),

	WRITE_GROUP(0x0A);

	public static final int I_AM_CODE = 0x00;

	public static final int I_HAVE_CODE = 0x01;

	/** 20.1.4 BACnet-SimpleACK-PDU */
	public static final int UNCONFIRMED_COV_NOTIFICATION_CODE = 0x02;

	/** 20.1.5 BACnet-ComplexACK-PDU */
	public static final int UNCONFIRMED_EVENT_NOTIFICATION_CODE = 0x03;

	/** 20.1.6 BACnet-SegmentACK-PDU */
	public static final int UNCONFIRMED_PRIVATE_TRANSFER_CODE = 0x04;

	/** 20.1.7 BACnet-Error-PDU */
	public static final int UNCONFIRMED_TEXT_MESSAGE_CODE = 0x05;

	/** 20.1.8 BACnet-Reject-PDU */
	public static final int TIME_SYNCHRONIZATION_CODE = 0x06;

	/** 20.1.9 BACnet-Abort-PDU */
	public static final int WHO_HAS_CODE = 0x07;

	/** 20.1.2 BACnet-Confirmed-Request-PDU */
	public static final int WHO_IS_CODE = 0x08;

	public static final int UTC_TIME_SYNCHRONIZATION_CODE = 0x09;

	public static final int WRITE_GROUP_CODE = 0x0A;

	private final int id;

	ServiceChoice(final int id) {
		this.id = id;
	}

	public static ServiceChoice fromInt(final int id) {

		switch (id) {

		case I_AM_CODE:
			return I_AM;

		case I_HAVE_CODE:
			return I_HAVE;

		case UNCONFIRMED_COV_NOTIFICATION_CODE:
			return UNCONFIRMED_COV_NOTIFICATION;

		case UNCONFIRMED_EVENT_NOTIFICATION_CODE:
			return UNCONFIRMED_EVENT_NOTIFICATION;

		case UNCONFIRMED_PRIVATE_TRANSFER_CODE:
			return UNCONFIRMED_PRIVATE_TRANSFER;

		case UNCONFIRMED_TEXT_MESSAGE_CODE:
			return UNCONFIRMED_TEXT_MESSAGE;

		case TIME_SYNCHRONIZATION_CODE:
			return TIME_SYNCHRONIZATION;

		case WHO_HAS_CODE:
			return WHO_HAS;

		case WHO_IS_CODE:
			return WHO_IS;

		case UTC_TIME_SYNCHRONIZATION_CODE:
			return UTC_TIME_SYNCHRONIZATION;

		case WRITE_GROUP_CODE:
			return WRITE_GROUP;

		default:
			throw new RuntimeException("Unknown id " + id);
		}
	}

	public int getId() {
		return id;
	}
}
