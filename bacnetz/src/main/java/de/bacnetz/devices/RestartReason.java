package de.bacnetz.devices;

import java.util.HashMap;
import java.util.Map;

public enum RestartReason {

    // @formatter:off

    COLD_START(1, "cold-start"),
    DETECTED_POWERED_OFF(4, "detected-powered-off");
    
    // @formatter:on

    private int code;

    private String name;

    private static Map<Integer, RestartReason> codeMap = new HashMap<>();

    private RestartReason(final int code, final String name) {
        this.code = code;
        this.name = name;
    }

    static {
        for (final RestartReason restartReason : values()) {
            codeMap.put(restartReason.getCode(), restartReason);
        }
    }

    public static RestartReason getByCode(final int code) {
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
