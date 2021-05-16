package de.bacnetz.devices;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class DeviceDto {

    private int id;

    private ObjectType objectType;

    private String name;

    private String description;

    private Object presentValue;

    private final List<String> states = new ArrayList<>();

    private final Collection<DevicePropertyDto> deviceProperties = new ArrayList<>();

    private final Collection<DeviceDto> childDevices = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public void setObjectType(final ObjectType objectType) {
        this.objectType = objectType;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public Collection<DeviceDto> getChildDevices() {
        return childDevices;
    }

    public Object getPresentValue() {
        return presentValue;
    }

    public void setPresentValue(final Object presentValue) {
        this.presentValue = presentValue;
    }

    public Collection<DevicePropertyDto> getDeviceProperties() {
        return deviceProperties;
    }

    public List<String> getStates() {
        return states;
    }

}
