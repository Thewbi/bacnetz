package de.bacnetz.stack;

public enum ErrorCode {

    // @formatter:off
    
    UNKNOWN_OBJECT(0x1f, "unknown-object"),

    UNKNOWN_PROPERTY(0x20, "unknown-property");
    
    // @formatter:on

    public static final int UNKNOWN_OBJECT_CODE = 0x1f;

    public static final int UNKNOWN_PROPERTY_CODE = 0x20;

    private int code;

    private String name;

    private ErrorCode(final int code, final String name) {
        this.code = code;
        this.name = name;
    }

    public static ErrorCode getByCode(final int id) {

        switch (id) {

        case UNKNOWN_OBJECT_CODE:
            return UNKNOWN_OBJECT;

        case UNKNOWN_PROPERTY_CODE:
            return UNKNOWN_PROPERTY;

        default:
            throw new RuntimeException("Unknown id " + id);
        }
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

}
