package de.bacnetz.stack;

public enum VendorType {

    // @formatter:off

    GEZE_GMBH(538, "GEZE GmbH");
    
    // @formatter:on

    private int code;

    private String name;

    private VendorType(final int code, final String name) {
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
