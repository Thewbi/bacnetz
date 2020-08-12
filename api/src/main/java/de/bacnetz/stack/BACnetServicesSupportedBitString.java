package de.bacnetz.stack;

public class BACnetServicesSupportedBitString extends BaseBitString {

    /** 0 */
    private boolean acknowledgeAlarm;

    /** 1 */
    private boolean confirmedCOVNotification;

    /** 2 */
    private boolean confirmedEventNotification;

    /** 3 */
    private boolean getAlarmSummary;

    /** 4 */
    private boolean getEnrollmentSummary;
//	-- getEventInformation (39),

    /** 5 */
    private boolean subscribeCOV;

//	-- subscribeCOVProperty (38),
//	-- lifeSafetyOperation (37),
//	-- File Access Services

    /** 6 */
    private boolean atomicReadFile;

    /** 7 */
    private boolean atomicWriteFile;

//	-- Object Access Services

    /** 8 */
    private boolean addListElement;

    /** 9 */
    private boolean removeListElement;

    /** 10 */
    private boolean createObject;

    /** 11 */
    private boolean deleteObject;

    /** 12 */
    private boolean readProperty;

    /** 13 */
    private boolean readPropertyConditional;

    /** 14 */
    private boolean readPropertyMultiple;

//	-- readRange (35),
//	-- writeGroup (40),

    /** 15 */
    private boolean writeProperty;

    /** 16 */
    private boolean writePropertyMultiple;

//	-- Remote Device Management Services

    /** 17 */
    private boolean deviceCommunicationControl;

    /** 18 */
    private boolean confirmedPrivateTransfer;

    /** 19 */
    private boolean confirmedTextMessage;

    /** 20 */
    private boolean reinitializeDevice;

//	-- Virtual Terminal Services

    /** 21 */
    private boolean vtOpen;

    /** 22 */
    private boolean vtClose;

    /** 23 */
    private boolean vtData;

    /** 24 */
    private boolean authenticate;

    /** 25 */
    private boolean requestKey;

//	-- Removed Services
//	-- formerly: readPropertyConditional (13), removed in version 1 revision 12
//	-- formerly: authenticate (24), removed in version 1 revision 11
//	-- formerly: requestKey (25), removed in version 1 revision 11
//	-- Unconfirmed Services

    /** 26 */
    private boolean iAm;

    /** 27 */
    private boolean iHave;

    /** 28 */
    private boolean unconfirmedCOVNotification;

    /** 29 */
    private boolean unconfirmedEventNotification;

    /** 30 */
    private boolean unconfirmedPrivateTransfer;

    /** 31 */
    private boolean unconfirmedTextMessage;

    /** 32 */
    private boolean timeSynchronization;

    /** 36 */
//	-- utcTimeSynchronization (36),

    /** 33 */
    private boolean whoHas;

    /** 34 */
    private boolean whoIs;

//	-- Services added after 1995

    /** 35 Object Access Service */
    private boolean readRange;

    /** 36 Remote Device Management Service */
    private boolean utcTimeSynchronization;

    /** 37 Alarm and Event Service */
    private boolean lifeSafetyOperation;

    /** 38 Alarm and Event Service */
    private boolean subscribeCOVProperty;

    /** 39 Alarm and Event Service */
    private boolean getEventInformation;

    /** 40 Object Access Services */
    private boolean writeGroup;

    public boolean isAcknowledgeAlarm() {
        return acknowledgeAlarm;
    }

    /**
     * index 0
     * 
     * @param acknowledgeAlarm
     */
    public void setAcknowledgeAlarm(final boolean acknowledgeAlarm) {
        this.acknowledgeAlarm = acknowledgeAlarm;
        final int start = 0;
        setBit(acknowledgeAlarm, start);
    }

    public boolean isConfirmedCOVNotification() {
        return confirmedCOVNotification;
    }

    public void setConfirmedCOVNotification(final boolean confirmedCOVNotification) {
        this.confirmedCOVNotification = confirmedCOVNotification;
        final int start = 1;
        setBit(confirmedCOVNotification, start);
    }

    public boolean isConfirmedEventNotification() {
        return confirmedEventNotification;
    }

    public void setConfirmedEventNotification(final boolean confirmedEventNotification) {
        this.confirmedEventNotification = confirmedEventNotification;
        final int start = 2;
        setBit(confirmedEventNotification, start);
    }

    public boolean isGetAlarmSummary() {
        return getAlarmSummary;
    }

    public void setGetAlarmSummary(final boolean getAlarmSummary) {
        this.getAlarmSummary = getAlarmSummary;
        final int start = 3;
        setBit(getAlarmSummary, start);
    }

    public boolean isGetEnrollmentSummary() {
        return getEnrollmentSummary;
    }

    public void setGetEnrollmentSummary(final boolean getEnrollmentSummary) {
        this.getEnrollmentSummary = getEnrollmentSummary;
        final int start = 4;
        setBit(getEnrollmentSummary, start);
    }

    public boolean isSubscribeCOV() {
        return subscribeCOV;
    }

    public void setSubscribeCOV(final boolean subscribeCOV) {
        this.subscribeCOV = subscribeCOV;
        final int start = 5;
        setBit(subscribeCOV, start);
    }

    public boolean isAtomicReadFile() {
        return atomicReadFile;
    }

    public void setAtomicReadFile(final boolean atomicReadFile) {
        this.atomicReadFile = atomicReadFile;
        final int start = 6;
        setBit(atomicReadFile, start);
    }

    public boolean isAtomicWriteFile() {
        return atomicWriteFile;
    }

    public void setAtomicWriteFile(final boolean atomicWriteFile) {
        this.atomicWriteFile = atomicWriteFile;
        final int start = 7;
        setBit(atomicWriteFile, start);
    }

    public boolean isAddListElement() {
        return addListElement;
    }

    public void setAddListElement(final boolean addListElement) {
        this.addListElement = addListElement;
        final int start = 8;
        setBit(addListElement, start);
    }

    public boolean isRemoveListElement() {
        return removeListElement;
    }

    public void setRemoveListElement(final boolean removeListElement) {
        this.removeListElement = removeListElement;
        final int start = 9;
        setBit(removeListElement, start);
    }

    public boolean isCreateObject() {
        return createObject;
    }

    public void setCreateObject(final boolean createObject) {
        this.createObject = createObject;
        final int start = 10;
        setBit(createObject, start);
    }

    public boolean isDeleteObject() {
        return deleteObject;
    }

    public void setDeleteObject(final boolean deleteObject) {
        this.deleteObject = deleteObject;
        final int start = 11;
        setBit(deleteObject, start);
    }

    public boolean isReadProperty() {
        return readProperty;
    }

    public void setReadProperty(final boolean readProperty) {
        this.readProperty = readProperty;
        final int start = 12;
        setBit(readProperty, start);
    }

    public boolean isReadPropertyConditional() {
        return readPropertyConditional;
    }

    public void setReadPropertyConditional(final boolean readPropertyConditional) {
        this.readPropertyConditional = readPropertyConditional;
        final int start = 13;
        setBit(readPropertyConditional, start);
    }

    public boolean isReadPropertyMultiple() {
        return readPropertyMultiple;
    }

    public void setReadPropertyMultiple(final boolean readPropertyMultiple) {
        this.readPropertyMultiple = readPropertyMultiple;
        final int start = 14;
        setBit(readPropertyMultiple, start);
    }

    public boolean isWriteProperty() {
        return writeProperty;
    }

    public void setWriteProperty(final boolean writeProperty) {
        this.writeProperty = writeProperty;
        final int start = 15;
        setBit(writeProperty, start);
    }

    public boolean isWritePropertyMultiple() {
        return writePropertyMultiple;
    }

    public void setWritePropertyMultiple(final boolean writePropertyMultiple) {
        this.writePropertyMultiple = writePropertyMultiple;
        final int start = 16;
        setBit(writePropertyMultiple, start);
    }

    public boolean isDeviceCommunicationControl() {
        return deviceCommunicationControl;
    }

    public void setDeviceCommunicationControl(final boolean deviceCommunicationControl) {
        this.deviceCommunicationControl = deviceCommunicationControl;
        final int start = 17;
        setBit(deviceCommunicationControl, start);
    }

    public boolean isConfirmedPrivateTransfer() {
        return confirmedPrivateTransfer;
    }

    public void setConfirmedPrivateTransfer(final boolean confirmedPrivateTransfer) {
        this.confirmedPrivateTransfer = confirmedPrivateTransfer;
        final int start = 18;
        setBit(confirmedPrivateTransfer, start);
    }

    public boolean isConfirmedTextMessage() {
        return confirmedTextMessage;
    }

    public void setConfirmedTextMessage(final boolean confirmedTextMessage) {
        this.confirmedTextMessage = confirmedTextMessage;
        final int start = 19;
        setBit(confirmedTextMessage, start);
    }

    public boolean isReinitializeDevice() {
        return reinitializeDevice;
    }

    public void setReinitializeDevice(final boolean reinitializeDevice) {
        this.reinitializeDevice = reinitializeDevice;
        final int start = 20;
        setBit(reinitializeDevice, start);
    }

    public boolean isVtOpen() {
        return vtOpen;
    }

    public void setVtOpen(final boolean vtOpen) {
        this.vtOpen = vtOpen;
        final int start = 21;
        setBit(vtOpen, start);
    }

    public boolean isVtClose() {
        return vtClose;
    }

    public void setVtClose(final boolean vtClose) {
        this.vtClose = vtClose;
        final int start = 22;
        setBit(vtClose, start);
    }

    public boolean isVtData() {
        return vtData;
    }

    public void setVtData(final boolean vtData) {
        this.vtData = vtData;
        final int start = 23;
        setBit(vtData, start);
    }

    public boolean isAuthenticate() {
        return authenticate;
    }

    public void setAuthenticate(final boolean authenticate) {
        this.authenticate = authenticate;
        final int start = 24;
        setBit(authenticate, start);
    }

    public boolean isRequestKey() {
        return requestKey;
    }

    public void setRequestKey(final boolean requestKey) {
        this.requestKey = requestKey;
        final int start = 25;
        setBit(requestKey, start);
    }

    public boolean isiAm() {
        return iAm;
    }

    public void setiAm(final boolean iAm) {
        this.iAm = iAm;
        final int start = 26;
        setBit(iAm, start);
    }

    public boolean isiHave() {
        return iHave;
    }

    public void setiHave(final boolean iHave) {
        this.iHave = iHave;
        final int start = 27;
        setBit(iHave, start);
    }

    public boolean isUnconfirmedCOVNotification() {
        return unconfirmedCOVNotification;
    }

    public void setUnconfirmedCOVNotification(final boolean unconfirmedCOVNotification) {
        this.unconfirmedCOVNotification = unconfirmedCOVNotification;
        final int start = 28;
        setBit(unconfirmedCOVNotification, start);
    }

    public boolean isUnconfirmedEventNotification() {
        return unconfirmedEventNotification;
    }

    public void setUnconfirmedEventNotification(final boolean unconfirmedEventNotification) {
        this.unconfirmedEventNotification = unconfirmedEventNotification;
        final int start = 29;
        setBit(unconfirmedEventNotification, start);
    }

    public boolean isUnconfirmedPrivateTransfer() {
        return unconfirmedPrivateTransfer;
    }

    public void setUnconfirmedPrivateTransfer(final boolean unconfirmedPrivateTransfer) {
        this.unconfirmedPrivateTransfer = unconfirmedPrivateTransfer;
        final int start = 30;
        setBit(unconfirmedPrivateTransfer, start);
    }

    public boolean isUnconfirmedTextMessage() {
        return unconfirmedTextMessage;
    }

    public void setUnconfirmedTextMessage(final boolean unconfirmedTextMessage) {
        this.unconfirmedTextMessage = unconfirmedTextMessage;
        final int start = 31;
        setBit(unconfirmedTextMessage, start);
    }

    public boolean isTimeSynchronization() {
        return timeSynchronization;
    }

    /**
     * start = 32
     * 
     * @param timeSynchronization
     */
    public void setTimeSynchronization(final boolean timeSynchronization) {
        this.timeSynchronization = timeSynchronization;
        final int start = 32;
        setBit(timeSynchronization, start);
    }

    public boolean isWhoHas() {
        return whoHas;
    }

    public void setWhoHas(final boolean whoHas) {
        this.whoHas = whoHas;
        final int start = 33;
        setBit(whoHas, start);
    }

    public boolean isWhoIs() {
        return whoIs;
    }

    public void setWhoIs(final boolean whoIs) {
        this.whoIs = whoIs;
        final int start = 34;
        setBit(whoIs, start);
    }

    public boolean isReadRange() {
        return readRange;
    }

    public void setReadRange(final boolean readRange) {
        this.readRange = readRange;
        final int start = 35;
        setBit(readRange, start);
    }

    public boolean isUtcTimeSynchronization() {
        return utcTimeSynchronization;
    }

    public void setUtcTimeSynchronization(final boolean utcTimeSynchronization) {
        this.utcTimeSynchronization = utcTimeSynchronization;
        final int start = 36;
        setBit(utcTimeSynchronization, start);
    }

    public boolean isLifeSafetyOperation() {
        return lifeSafetyOperation;
    }

    /**
     * bitindex = 37
     * 
     * @param lifeSafetyOperation
     */
    public void setLifeSafetyOperation(final boolean lifeSafetyOperation) {
        this.lifeSafetyOperation = lifeSafetyOperation;
        final int start = 37;
        setBit(lifeSafetyOperation, start);
    }

    public boolean isSubscribeCOVProperty() {
        return subscribeCOVProperty;
    }

    /**
     * bitindex = 38
     * 
     * @param subscribeCOVProperty
     */
    public void setSubscribeCOVProperty(final boolean subscribeCOVProperty) {
        this.subscribeCOVProperty = subscribeCOVProperty;
        final int start = 38;
        setBit(subscribeCOVProperty, start);
    }

    public boolean isGetEventInformation() {
        return getEventInformation;
    }

    /**
     * bitindex = 39
     * 
     * @param getEventInformation
     */
    public void setGetEventInformation(final boolean getEventInformation) {
        this.getEventInformation = getEventInformation;
        final int start = 39;
        setBit(getEventInformation, start);
    }

    public boolean isWriteGroup() {
        return writeGroup;
    }

    /**
     * bitindex = 40
     * 
     * @param writeGroup
     */
    public void setWriteGroup(final boolean writeGroup) {
        this.writeGroup = writeGroup;
        final int start = 40;
        setBit(writeGroup, start);
    }

}
