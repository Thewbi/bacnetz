package de.bacnetz.stack;

public enum ErrorClass {

    // @formatter:off
    
    OBJECT(0x01, "object"),

    PROPERTY(0x02, "property");
    
    // @formatter:on

    public static final int OBJECT_CODE = 0x01;

    public static final int PROPERTY_CODE = 0x02;

    private int code;

    private String name;

    private ErrorClass(final int code, final String name) {
        this.code = code;
        this.name = name;
    }

    public static ErrorClass getByCode(final int id) {

        switch (id) {

        case OBJECT_CODE:
            return OBJECT;

        case PROPERTY_CODE:
            return PROPERTY;

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
