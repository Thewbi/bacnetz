package de.bacnet.factory;

public enum MessageType {

	BOOLEAN_PROPERTY(0x01),

	INTEGER_PROPERTY(0x02),

	SINGED_INTEGER_TWOS_COMPLEMENT_NOTATION_PROPERTY(0x03),

	ENUMERATED(0x09),

	WHO_IS(0xFF);

	private final int id;

	MessageType(final int id) {
		this.id = id;
	}

	public static MessageType fromInt(final int id) {

		switch (id) {

		case 0x02:
			return INTEGER_PROPERTY;

		case 0x03:
			return BOOLEAN_PROPERTY;

		case 0x09:
			return ENUMERATED;

		case 0xFF:
			return WHO_IS;

		default:
			throw new RuntimeException("Unknown id " + id);
		}
	}

	public int getValue() {
		return id;
	}

}
