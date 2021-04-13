package de.bacnetz.devices;

import java.util.HashMap;
import java.util.Map;

public enum SystemStatus {

    // @formatter:off
    
    OPERATIONAL(0, "operational");
    
    // @formatter:on

    private int code;

    private String name;

    private static Map<Integer, SystemStatus> codeMap = new HashMap<>();

    private SystemStatus(final int code, final String name) {
        this.code = code;
        this.name = name;
    }

    static {
        for (final SystemStatus devicePropertyType : values()) {
            codeMap.put(devicePropertyType.getCode(), devicePropertyType);
        }
    }

    public static SystemStatus getByCode(final int code) {
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
