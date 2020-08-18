package de.bacnetz.devices;

import java.util.HashMap;
import java.util.Map;

public enum ObjectType {

    // @formatter:off

	BINARY_INPUT(0x03, "BINARY_INPUT"),
	
	DEVICE(0x08, "DEVICE"),
	
	NOTIFICATION_CLASS(0x0F, "NOTIFICATION_CLASS"),
	
	MULTI_STATE_VALUE(0x13, "MULTI_STATE_VALUE");

	// @formatter:on

    public static final int BINARY_INPUT_CODE = 0x03;

    public static final int DEVICE_CODE = 0x08;

    public static final int NOTIFICATION_CLASS_CODE = 0x0F;

    public static final int MULTI_STATE_VALUE_CODE = 0x13;

    private int code;

    private String name;

    private static Map<Integer, ObjectType> codeMap = new HashMap<Integer, ObjectType>();

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
