package de.bacnetz.stack;

public enum PDUType {

	CONFIRMED_SERVICE_REQUEST_PDU(0x00),

	UNCONFIRMED_SERVICE_REQUEST_PDU(0x01);

	public static final int CONFIRMED_SERVICE_REQUEST_PDU_CODE = 0x00;

	public static final int UNCONFIRMED_SERVICE_REQUEST_PDU_CODE = 0x01;

	private final int id;

	PDUType(final int id) {
		this.id = id;
	}

	public static PDUType fromInt(final int id) {

		switch (id) {

		case CONFIRMED_SERVICE_REQUEST_PDU_CODE:
			return CONFIRMED_SERVICE_REQUEST_PDU;

		case UNCONFIRMED_SERVICE_REQUEST_PDU_CODE:
			return UNCONFIRMED_SERVICE_REQUEST_PDU;

		default:
			throw new RuntimeException("Unknown id " + id);
		}
	}

	public int getId() {
		return id;
	}

}
