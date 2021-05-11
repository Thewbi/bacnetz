package de.bacnetz.stack;

public enum ErrorClass {

    // @formatter:off
    
    DEVICE(0x00, "device"),
    
    OBJECT(0x01, "object"),

    PROPERTY(0x02, "property"),
    
    RESOURCES(0x03, "resources"),
    
    SECURITY(0x04, "security"),
    
    SERVICES(0x05, "services");
    
    // @formatter:on

    public static final int DEVICE_CODE = 0x00;

    public static final int OBJECT_CODE = 0x01;

    public static final int PROPERTY_CODE = 0x02;

    public static final int RESOURCES_CODE = 0x03;

    public static final int SECURITY_CODE = 0x04;

    public static final int SERVICES_CODE = 0x05;

    private int code;

    private String name;

    private ErrorClass(final int code, final String name) {
        this.code = code;
        this.name = name;
    }

    public static ErrorClass getByCode(final int id) {

        switch (id) {

        case DEVICE_CODE:
            return OBJECT;

        case OBJECT_CODE:
            return OBJECT;

        case PROPERTY_CODE:
            return PROPERTY;

        case RESOURCES_CODE:
            return RESOURCES;

        case SECURITY_CODE:
            return SECURITY;

        case SERVICES_CODE:
            return SERVICES;

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
