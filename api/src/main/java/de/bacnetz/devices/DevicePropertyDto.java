package de.bacnetz.devices;

public class DevicePropertyDto {

    private Integer key;

    private String name;

    private Object value;

    @Override
    public String toString() {
        return "DevicePropertyDto [key=" + key + ", name=" + name + ", value=" + value + "]";
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(final Integer key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(final Object value) {
        this.value = value;
    }

}
