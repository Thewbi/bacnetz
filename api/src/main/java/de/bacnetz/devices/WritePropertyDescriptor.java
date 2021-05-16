package de.bacnetz.devices;

public class WritePropertyDescriptor {

    private Integer parentDeviceId;

    private Integer childDeviceId;

    private ObjectType childObjectType;

    private Integer propertyKey;

    private String propertyName;

    private Object value;

    @Override
    public String toString() {
        return "WritePropertyDescriptor [parentDeviceId=" + parentDeviceId + ", childDeviceId=" + childDeviceId
                + ", childObjectType=" + childObjectType + ", propertyKey=" + propertyKey + ", propertyName="
                + propertyName + ", value=" + value + "]";
    }

    public Integer getParentDeviceId() {
        return parentDeviceId;
    }

    public void setParentDeviceId(final Integer parentDeviceId) {
        this.parentDeviceId = parentDeviceId;
    }

    public Integer getChildDeviceId() {
        return childDeviceId;
    }

    public void setChildDeviceId(final Integer childDeviceId) {
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
