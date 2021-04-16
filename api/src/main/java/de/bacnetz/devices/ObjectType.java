package de.bacnetz.devices;

import java.util.HashMap;
import java.util.Map;

public enum ObjectType {

    // @formatter:off
    
    /**
     * 0x00 = 0d
     */
    ANALOG_INPUT(0x00, "ANALOG_INPUT"),
    
    /**
     * 0x01 = 1d
     */
    ANALOG_OUTPUT(0x01, "ANALOG_OUTPUT"),
    
    /**
     * 0x02 = 2d
     */
    ANALOG_VALUE(0x02, "ANALOG_VALUE"),

    /**
     * 0x03 = 3d
     */
	BINARY_INPUT(0x03, "BINARY_INPUT"),
	
	/**
     * 0x08 = 8d
     */
    DEVICE(0x08, "DEVICE"),
	
	/**
	 * 0x0A = 10d
	 */
	FILE(0x0A, "FILE"),
	
	/**
     * 0x0C = 12d
     */
    LOOP(0x0C, "LOOP"),
	
	/**
	 * 0x0F = 15d
	 */
	NOTIFICATION_CLASS(0x0F, "NOTIFICATION_CLASS"),
	
	/**
	 * 0x13 = 19d
	 */
	MULTI_STATE_VALUE(0x13, "MULTI_STATE_VALUE");

	// @formatter:on

    public static final int ANALOG_INPUT_CODE = 0x00;

    public static final int ANALOG_OUTPUT_CODE = 0x01;

    public static final int ANALOG_VALUE_CODE = 0x02;

    public static final int BINARY_INPUT_CODE = 0x03;

    public static final int DEVICE_CODE = 0x08;

    public static final int FILE_CODE = 0x0A;

    public static final int LOOP_CODE = 0x0C;

    public static final int NOTIFICATION_CLASS_CODE = 0x0F;

    public static final int MULTI_STATE_VALUE_CODE = 0x13;

    private int code;

    private String name;

    private static Map<Integer, ObjectType> codeMap = new HashMap<>();

    private ObjectType(final int code, final String name) {
        this.code = code;
        this.name = name;
    }

    static {
        for (final ObjectType objectType : values()) {
            codeMap.put(objectType.getCode(), objectType);
        }
    }

    public static ObjectType getByCode(final int code) {
        return codeMap.get(code);
    }

    public int getCode() {
        return code;
    }

    public void setCode(final int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

}
