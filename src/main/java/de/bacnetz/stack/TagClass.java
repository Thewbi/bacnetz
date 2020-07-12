package de.bacnetz.stack;

public enum TagClass {

	APPLICATION_TAG(0x00),

	CONTEXT_SPECIFIC_TAG(0x01);

	public static final int APPLICATION_TAG_CODE = 0x00;

	public static final int CONTEXT_SPECIFIC_TAG_CODE = 0x01;

	private final int id;

	private TagClass(final int id) {
		this.id = id;
	}

	public static TagClass fromInt(final int id) {

		switch (id) {

		case APPLICATION_TAG_CODE:
			return APPLICATION_TAG;

		case CONTEXT_SPECIFIC_TAG_CODE:
			return CONTEXT_SPECIFIC_TAG;

		default:
			throw new RuntimeException("Unknown id " + id);
		}
	}

	public int getId() {
		return id;
	}

}
