package de.bacnetz.devices;

import java.util.HashMap;
import java.util.Map;

public enum ObjectType {

	// @formatter:off

	BINARY_INPUT(3, "BINARY_INPUT"),
	
	DEVICE(8, "DEVICE"),
	
	NOTIFICATION_CLASS(15, "NOTIFICATION_CLASS"),
	
	MULTI_STATE_VALUE(19, "MULTI_STATE_VALUE");

	// @formatter:on

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
