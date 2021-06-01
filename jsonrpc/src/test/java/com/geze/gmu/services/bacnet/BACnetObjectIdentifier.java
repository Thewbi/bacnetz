package com.geze.gmu.services.bacnet;

public final class BACnetObjectIdentifier {

  private final BACnetObjectType objectType;
  private final int              instanceId;

  public BACnetObjectIdentifier(BACnetObjectType objectType, int instanceId) {
    if (objectType == null) {
      throw new NullPointerException("objectType is null");
    }
    this.objectType = objectType;
    this.instanceId = instanceId;
  }

  public BACnetObjectIdentifier(String objectType, int instanceId) {
    this.objectType = BACnetObjectType.getByText(objectType);
    this.instanceId = instanceId;
  }

  public BACnetObjectIdentifier(int objectType, int instanceId) {
    this.objectType = BACnetObjectType.getByCode(objectType);
    this.instanceId = instanceId;
  }

  public BACnetObjectType getObjectType() {
    return objectType;
  }

  public int getInstanceId() {
    return instanceId;
  }

  public String toString() {
    return '(' + objectType.toString() + ',' + instanceId + ')';
  }

  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + instanceId;
    result = prime * result + objectType.hashCode();
    return result;
  }

  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || !(obj instanceof BACnetObjectIdentifier)) {
      return false;
    }
    BACnetObjectIdentifier other = (BACnetObjectIdentifier) obj;
    return (instanceId == other.instanceId) && (objectType == other.objectType);
  }

}
