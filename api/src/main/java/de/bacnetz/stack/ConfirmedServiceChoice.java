package de.bacnetz.stack;

/**
 * <pre>
 * BACnetConfirmedServiceChoice ::= ENUMERATED {
 * -- Alarm and Event Services
 * acknowledgeAlarm (0),
 * confirmedCOVNotification (1),
 * confirmedEventNotification (2),
 * getAlarmSummary (3),
 * getEnrollmentSummary (4),
 * getEventInformation (29),
 * subscribeCOV (5),
 * subscribeCOVProperty (28),
 * lifeSafetyOperation (27),
 * -- File Access Services
 * atomicReadFile (6),
 * atomicWriteFile (7),
 * -- Object Access Services
 * addListElement (8),
 * removeListElement (9),
 * createObject (10),
 * deleteObject (11),
 * readProperty (12),
 * readPropertyMultiple (14),
 * readRange (26),
 * writeProperty (15),
 * writePropertyMultiple (16),
 * -- Remote Device Management Services
 * deviceCommunicationControl (17),
 * confirmedPrivateTransfer (18),
 * confirmedTextMessage (19),
 * reinitializeDevice (20),
 * -- Virtual Terminal Services
 * vtOpen (21),
 * vtClose (22),
 * vtData (23)
 * -- Removed Services
 * -- formerly: authenticate (24), removed in version 1 revision 11
 * -- formerly: requestKey (25), removed in version 1 revision 11
 * -- formerly: readPropertyConditional (13), removed in version 1 revision 12
 * -- Services added after 1995
 * -- readRange (26) see Object Access Services
 * -- lifeSafetyOperation (27) see Alarm and Event Services
 * -- subscribeCOVProperty (28) see Alarm and Event Services
 * -- getEventInformation (29) see Alarm and Event Services
 * }
 * -- Other services to be added as they are defined. All enumeration values in this production are reserved for definition by
 * -- ASHRAE. Proprietary extensions are made by using the ConfirmedPrivateTransfer or UnconfirmedPrivateTransfer
 *  * -- services. See Clause 23.
 * </pre>
 */
public enum ConfirmedServiceChoice {

    // @formatter:off

    // Alarm and Event Services
    acknowledgeAlarm(0),
    confirmedCOVNotification (1),
    confirmedEventNotification (2),
    getAlarmSummary (3),
    getEnrollmentSummary (4),
    getEventInformation (29),
    SUBSCRIBE_COV (5),
    subscribeCOVProperty (28),
    lifeSafetyOperation (27),
    
    // File Access Services
    atomicReadFile (6),
    atomicWriteFile (7),
    
    //  Object Access Services
    ADD_LIST_ELEMENT (8),
    removeListElement (9),
    createObject (10),
    deleteObject (11),
    READ_PROPERTY (12),
    READ_PROPERTY_MULTIPLE (14),
    readRange (26),
    WRITE_PROPERTY (15),
    writePropertyMultiple (16),
    
    //  Remote Device Management Services
    deviceCommunicationControl (17),
    confirmedPrivateTransfer (18),
    confirmedTextMessage (19),
    REINITIALIZE_DEVICE (20),
    
    //  Virtual Terminal Services
    vtOpen (21),
    vtClose (22),
    vtData (23);
    
    // @formatter:on

    private final int id;

    ConfirmedServiceChoice(final int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static ConfirmedServiceChoice fromInt(final int id) {

        switch (id) {

        case 0:
            return acknowledgeAlarm;

        case 1:
            return confirmedCOVNotification;

        case (2):
            return confirmedEventNotification;

        case (3):
            return getAlarmSummary;

        case (4):
            return getEnrollmentSummary;

        case (5):
            return SUBSCRIBE_COV;

        case (6):
            return atomicReadFile;

        case (7):
            return atomicWriteFile;

        case (8):
            return ADD_LIST_ELEMENT;

        case (9):
            return removeListElement;

        case (10):
            return createObject;

        case (11):
            return deleteObject;

        case (12):
            return READ_PROPERTY;

        case (14):
            return READ_PROPERTY_MULTIPLE;

        case (15):
            return WRITE_PROPERTY;

        case (16):
            return writePropertyMultiple;

        case (17):
            return deviceCommunicationControl;

        case (18):
            return confirmedPrivateTransfer;

        case (19):
            return confirmedTextMessage;

        case (20):
            return REINITIALIZE_DEVICE;

        case (21):
            return vtOpen;

        case (22):
            return vtClose;

        case (23):
            return vtData;

        case (26):
            return readRange;

        case (28):
            return subscribeCOVProperty;

        case (27):
            return lifeSafetyOperation;

        case (29):
            return getEventInformation;

        default:
            return null;
        }
    }
}
