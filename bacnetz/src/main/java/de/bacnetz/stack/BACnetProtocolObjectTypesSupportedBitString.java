package de.bacnetz.stack;

public class BACnetProtocolObjectTypesSupportedBitString extends BaseBitString {

	/** 0 */
	private boolean analogInput;

	/** 1 */
	private boolean analogOutput;

	/** 2 */
	private boolean analogValue;

	/** 3 */
	private boolean binaryInput;

	/** 4 */
	private boolean binaryOutput;

	/** 5 */
	private boolean binaryValue;

	/** 6 */
	private boolean calendar;

	/** 7 */
	private boolean command;

	/** 8 */
	private boolean device;

	/** 9 */
	private boolean eventEnrollment;

	/** 10 */
	private boolean file;

	/** 11 */
	private boolean group;

	/** 12 */
	private boolean loop;

	/** 13 */
	private boolean multiStateInput;

	/** 14 */
	private boolean multiStateOutput;

	/** 15 */
	private boolean notificationClass;

	/** 16 */
	private boolean program;

	/** 17 */
	private boolean schedule;

	/** 18 */
	private boolean averaging;

	/** 19 */
	private boolean multiStateValue;

	/** 1 */
	private boolean trendLog;

	/** 1 */
	private boolean lifeSafetyPoint;

	/** 1 */
	private boolean lifeSafetyZone;

	/** 1 */
	private boolean accumulator;

	/** 1 */
	private boolean pulseConverter;

	/** 1 */
	private boolean eventLog;

	/** 1 */
	private boolean globalGroup;

	/** 1 */
	private boolean trendLogMultiple;

	/** 1 */
	private boolean loadControl;

	/** 1 */
	private boolean structuredView;

	/** 1 */
	private boolean accessDoor;

	/** 1 */
	private boolean timer;

	/** 1 */
	private boolean accessCredential;

	/** 1 */
	private boolean accessPoint;

	/** 1 */
	private boolean accessRights;

	/** 1 */
	private boolean accessUser;

	/** 1 */
	private boolean accessZone;

	/** 1 */
	private boolean credentialDataInput;

	public boolean isAnalogInput() {
		return analogInput;
	}

	public void setAnalogInput(final boolean analogInput) {
		this.analogInput = analogInput;
		final int start = 0;
		setBit(analogInput, start);
	}

	public boolean isAnalogOutput() {
		return analogOutput;
	}

	public void setAnalogOutput(final boolean analogOutput) {
		this.analogOutput = analogOutput;
		final int start = 1;
		setBit(analogOutput, start);
	}

	public boolean isAnalogValue() {
		return analogValue;
	}

	public void setAnalogValue(final boolean analogValue) {
		this.analogValue = analogValue;
		final int start = 2;
		setBit(analogValue, start);
	}

	public boolean isBinaryInput() {
		return binaryInput;
	}

	public void setBinaryInput(final boolean binaryInput) {
		this.binaryInput = binaryInput;
		final int start = 3;
		setBit(binaryInput, start);
	}

	public boolean isBinaryOutput() {
		return binaryOutput;
	}

	public void setBinaryOutput(final boolean binaryOutput) {
		this.binaryOutput = binaryOutput;
		final int start = 4;
		setBit(binaryOutput, start);
	}

	public boolean isBinaryValue() {
		return binaryValue;
	}

	public void setBinaryValue(final boolean binaryValue) {
		this.binaryValue = binaryValue;
		final int start = 5;
		setBit(binaryValue, start);
	}

	public boolean isCalendar() {
		return calendar;
	}

	public void setCalendar(final boolean calendar) {
		this.calendar = calendar;
		final int start = 6;
		setBit(calendar, start);
	}

	public boolean isCommand() {
		return command;
	}

	public void setCommand(final boolean command) {
		this.command = command;
		final int start = 7;
		setBit(command, start);
	}

	public boolean isDevice() {
		return device;
	}

	public void setDevice(final boolean device) {
		this.device = device;
		final int start = 8;
		setBit(device, start);
	}

	public boolean isEventEnrollment() {
		return eventEnrollment;
	}

	public void setEventEnrollment(final boolean eventEnrollment) {
		this.eventEnrollment = eventEnrollment;
		final int start = 9;
		setBit(eventEnrollment, start);
	}

	public boolean isFile() {
		return file;
	}

	public void setFile(final boolean file) {
		this.file = file;
		final int start = 10;
		setBit(file, start);
	}

	public boolean isGroup() {
		return group;
	}

	public void setGroup(final boolean group) {
		this.group = group;
		final int start = 11;
		setBit(group, start);
	}

	public boolean isLoop() {
		return loop;
	}

	public void setLoop(final boolean loop) {
		this.loop = loop;
		final int start = 12;
		setBit(loop, start);
	}

	public boolean isMultiStateInput() {
		return multiStateInput;
	}

	public void setMultiStateInput(final boolean multiStateInput) {
		this.multiStateInput = multiStateInput;
		final int start = 13;
		setBit(multiStateInput, start);
	}

	public boolean isMultiStateOutput() {
		return multiStateOutput;
	}

	public void setMultiStateOutput(final boolean multiStateOutput) {
		this.multiStateOutput = multiStateOutput;
		final int start = 14;
		setBit(multiStateOutput, start);
	}

	public boolean isNotificationClass() {
		return notificationClass;
	}

	public void setNotificationClass(final boolean notificationClass) {
		this.notificationClass = notificationClass;
		final int start = 15;
		setBit(notificationClass, start);
	}

	public boolean isProgram() {
		return program;
	}

	public void setProgram(final boolean program) {
		this.program = program;
		final int start = 16;
		setBit(program, start);
	}

	public boolean isSchedule() {
		return schedule;
	}

	public void setSchedule(final boolean schedule) {
		this.schedule = schedule;
		final int start = 17;
		setBit(schedule, start);
	}

	public boolean isAveraging() {
		return averaging;
	}

	public void setAveraging(final boolean averaging) {
		this.averaging = averaging;
		final int start = 18;
		setBit(averaging, start);
	}

	public boolean isMultiStateValue() {
		return multiStateValue;
	}

	public void setMultiStateValue(final boolean multiStateValue) {
		this.multiStateValue = multiStateValue;
		final int start = 19;
		setBit(multiStateValue, start);
	}

	public boolean isTrendLog() {
		return trendLog;
	}

	public void setTrendLog(final boolean trendLog) {
		this.trendLog = trendLog;
		final int start = 20;
		setBit(trendLog, start);
	}

	public boolean isLifeSafetyPoint() {
		return lifeSafetyPoint;
	}

	public void setLifeSafetyPoint(final boolean lifeSafetyPoint) {
		this.lifeSafetyPoint = lifeSafetyPoint;
		final int start = 21;
		setBit(lifeSafetyPoint, start);
	}

	public boolean isLifeSafetyZone() {
		return lifeSafetyZone;
	}

	public void setLifeSafetyZone(final boolean lifeSafetyZone) {
		this.lifeSafetyZone = lifeSafetyZone;
		final int start = 22;
		setBit(lifeSafetyZone, start);
	}

	public boolean isAccumulator() {
		return accumulator;
	}

	public void setAccumulator(final boolean accumulator) {
		this.accumulator = accumulator;
		final int start = 23;
		setBit(accumulator, start);
	}

	public boolean isPulseConverter() {
		return pulseConverter;
	}

	public void setPulseConverter(final boolean pulseConverter) {
		this.pulseConverter = pulseConverter;
		final int start = 24;
		setBit(pulseConverter, start);
	}

	public boolean isEventLog() {
		return eventLog;
	}

	public void setEventLog(final boolean eventLog) {
		this.eventLog = eventLog;
		final int start = 25;
		setBit(eventLog, start);
	}

	public boolean isGlobalGroup() {
		return globalGroup;
	}

	public void setGlobalGroup(final boolean globalGroup) {
		this.globalGroup = globalGroup;
		final int start = 26;
		setBit(globalGroup, start);
	}

	public boolean isTrendLogMultiple() {
		return trendLogMultiple;
	}

	public void setTrendLogMultiple(final boolean trendLogMultiple) {
		this.trendLogMultiple = trendLogMultiple;
		final int start = 27;
		setBit(trendLogMultiple, start);
	}

	public boolean isLoadControl() {
		return loadControl;
	}

	public void setLoadControl(final boolean loadControl) {
		this.loadControl = loadControl;
		final int start = 28;
		setBit(loadControl, start);
	}

	public boolean isStructuredView() {
		return structuredView;
	}

	public void setStructuredView(final boolean structuredView) {
		this.structuredView = structuredView;
		final int start = 29;
		setBit(structuredView, start);
	}

	public boolean isAccessDoor() {
		return accessDoor;
	}

	public void setAccessDoor(final boolean accessDoor) {
		this.accessDoor = accessDoor;
		final int start = 30;
		setBit(accessDoor, start);
	}

	public boolean isTimer() {
		return timer;
	}

	public void setTimer(final boolean timer) {
		this.timer = timer;
		final int start = 31;
		setBit(timer, start);
	}

	public boolean isAccessCredential() {
		return accessCredential;
	}

	public void setAccessCredential(final boolean accessCredential) {
		this.accessCredential = accessCredential;
		final int start = 32;
		setBit(accessCredential, start);
	}

	public boolean isAccessPoint() {
		return accessPoint;
	}

	public void setAccessPoint(final boolean accessPoint) {
		this.accessPoint = accessPoint;
		final int start = 33;
		setBit(accessPoint, start);
	}

	public boolean isAccessRights() {
		return accessRights;
	}

	public void setAccessRights(final boolean accessRights) {
		this.accessRights = accessRights;
		final int start = 34;
		setBit(accessRights, start);
	}

	public boolean isAccessUser() {
		return accessUser;
	}

	public void setAccessUser(final boolean accessUser) {
		this.accessUser = accessUser;
		final int start = 35;
		setBit(accessUser, start);
	}

	public boolean isAccessZone() {
		return accessZone;
	}

	public void setAccessZone(final boolean accessZone) {
		this.accessZone = accessZone;
		final int start = 36;
		setBit(accessZone, start);
	}

	public boolean isCredentialDataInput() {
		return credentialDataInput;
	}

	public void setCredentialDataInput(final boolean credentialDataInput) {
		this.credentialDataInput = credentialDataInput;
		final int start = 37;
		setBit(credentialDataInput, start);
	}

}
