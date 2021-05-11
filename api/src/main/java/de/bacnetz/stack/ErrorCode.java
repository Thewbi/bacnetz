package de.bacnetz.stack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum ErrorCode {

    // @formatter:off
    
    UNKNOWN_ERROR(0xFF, "unknown-error"),
    
    UNKNOWN_OBJECT(0x1f, "unknown-object"),

    UNKNOWN_PROPERTY(0x20, "unknown-property");
    
    // @formatter:on

    public static final int UNKNOWN_OBJECT_CODE = 0x1f;

    public static final int UNKNOWN_PROPERTY_CODE = 0x20;

    private static final Logger LOG = LogManager.getLogger(ErrorCode.class);

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
//            throw new RuntimeException("Unknown id " + id);
            LOG.error("Unknown error code " + id);
            return UNKNOWN_ERROR;
        }
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

}
