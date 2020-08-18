package de.bacnetz.stack;

public enum ErrorClass {

    // @formatter:off

    PROPERTY(0x02, "property");
    
    // @formatter:on

    private int code;

    private String name;

    private ErrorClass(final int code, final String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

}
