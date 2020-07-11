package de.bacnetz.stack;

public class ObjectIdentifierServiceParameter extends ServiceParameter {

	public static final int OBJECT_TYPE_DEVICE = 8;

	private int objectType;

	private int instanceNumber;

	@Override
	public int getDataLength() {
		throw new RuntimeException("Not implemented exception!");
	}

	@Override
	public void toBytes(final byte[] data, final int offset) {
		throw new RuntimeException("Not implemented exception!");
	}

	public int getObjectType() {
		return objectType;
	}

	public void setObjectType(final int objectType) {
		this.objectType = objectType;
	}

	public int getInstanceNumber() {
		return instanceNumber;
	}

	public void setInstanceNumber(final int instanceNumber) {
		this.instanceNumber = instanceNumber;
	}

}
