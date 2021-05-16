package de.bacnetz.devices;

public class WritePropertyDto {

    private String parentDeviceId;

    private String childDeviceId;

    private ObjectType childObjectType;

    private Integer propertyKey;

    private String propertyName;

    private Object value;

    @Override
    public String toString() {
        return "WritePropertyDto [parentDeviceId=" + parentDeviceId + ", childDeviceId=" + childDeviceId
                + ", childObjectType=" + childObjectType + ", propertyKey=" + propertyKey + ", propertyName="
                + propertyName + ", value=" + value + "]";
    }

    public String getParentDeviceId() {
        return parentDeviceId;
    }

    public void setParentDeviceId(final String parentDeviceId) {
        this.parentDeviceId = parentDeviceId;
    }

    public String getChildDeviceId() {
        return childDeviceId;
    }

    public void setChildDeviceId(final String childDeviceId) {
        this.childDeviceId = childDeviceId;
    }

    public ObjectType getChildObjectType() {
        return childObjectType;
    }

    public void setChildObjectType(final ObjectType childObjectType) {
        this.childObjectType = childObjectType;
    }

    public Integer getPropertyKey() {
        return propertyKey;
    }

    public void setPropertyKey(final Integer propertyKey) {
        this.propertyKey = propertyKey;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(final String propertyName) {
        this.propertyName = propertyName;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(final Object value) {
        this.value = value;
    }

}
