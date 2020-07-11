package de.bacnetz.stack;

public enum NetworkPriority {

	LIFE_SAFETY(0x03),

	CRITICAL_EQUIPMENT_MESSAGE(0x02),

	URGENT_MESSAGE(0x01),

	NORMAL_MESSAGE(0x00);

	public static final int LIFE_SAFETY_CODE = 0x03;

	public static final int CRITICAL_EQUIPMENT_MESSAGE_CODE = 0x02;

	public static final int URGENT_MESSAGE_CODE = 0x01;

	public static final int NORMAL_MESSAGE_CODE = 0x00;

	private final int id;

	NetworkPriority(final int id) {
		this.id = id;
	}

	public static NetworkPriority fromInt(final int id) {

		switch (id) {

		case LIFE_SAFETY_CODE:
			return LIFE_SAFETY;

		case CRITICAL_EQUIPMENT_MESSAGE_CODE:
			return CRITICAL_EQUIPMENT_MESSAGE;

		case URGENT_MESSAGE_CODE:
			return URGENT_MESSAGE;

		case NORMAL_MESSAGE_CODE:
			return NORMAL_MESSAGE;

		default:
			throw new RuntimeException("Unknown id " + id);
		}
	}

	public int getId() {
		return id;
	}

}
