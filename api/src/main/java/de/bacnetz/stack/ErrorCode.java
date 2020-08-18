package de.bacnetz.stack;

public enum ErrorCode {

    // @formatter:off

    UNKNOWN_PROPERTY(0x20, "unknown-property");
    
    // @formatter:on

    private int code;

    private String name;

    private ErrorCode(final int code, final String name) {
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
