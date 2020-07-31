package de.bacnetz.stack;

public class StatusFlagsBitString extends BaseBitString {

	/** 0 */
	private boolean inAlarm;

	/** 1 */
	private boolean fault;

	/** 2 */
	private boolean overridden;

	/** 3 */
	private boolean outOfService;

	public boolean isInAlarm() {
		return inAlarm;
	}

	public void setInAlarm(final boolean inAlarm) {
		this.inAlarm = inAlarm;
		final int start = 0;
		setBit(inAlarm, start);
	}

	public boolean isFault() {
		return fault;
	}

	public void setFault(final boolean fault) {
		this.fault = fault;
		final int start = 1;
		setBit(fault, start);
	}

	public boolean isOverridden() {
		return overridden;
	}

	public void setOverridden(final boolean overridden) {
		this.overridden = overridden;
		final int start = 2;
		setBit(overridden, start);
	}

	public boolean isOutOfService() {
		return outOfService;
	}

	public void setOutOfService(final boolean outOfService) {
		this.outOfService = outOfService;
		final int start = 3;
		setBit(outOfService, start);
	}

}
