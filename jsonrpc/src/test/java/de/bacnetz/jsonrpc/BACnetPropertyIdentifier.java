package de.bacnetz.jsonrpc;

import java.util.HashMap;
import java.util.Map;

public enum BACnetPropertyIdentifier {

    ACKED_TRANSITIONS(0, "acked-transitions"), //
    ACK_REQUIRED(1, "ack-required"), //
    ACTION(2, "action"), //
    ACTION_TEXT(3, "action-text"), //
    ACTIVE_TEXT(4, "active-text"), //
    ACTIVE_VT_SESSIONS(5, "active-vt-sessions"), //
    ALARM_VALUE(6, "alarm-value"), //
    ALARM_VALUES(7, "alarm-values"), //
    ALL(8, "all"), //
    ALL_WRITES_SUCCESSFUL(9, "all-writes-successful"), //
    APDU_SEGMENT_TIMEOUT(10, "apdu-segment-timeout"), //
    APDU_TIMEOUT(11, "apdu-timeout"), //
    APPLICATION_SOFTWARE_VERSION(12, "application-software-version"), //
    ARCHIVE(13, "archive"), //
    BIAS(14, "bias"), //
    CHANGE_OF_STATE_COUNT(15, "change-of-state-count"), //
    CHANGE_OF_STATE_TIME(16, "change-of-state-time"), //
    NOTIFICATION_CLASS(17, "notification-class"), //
    CONTROLLED_VARIABLE_REFERENCE(19, "controlled-variable-reference"), //
    CONTROLLED_VARIABLE_UNITS(20, "controlled-variable-units"), //
    CONTROLLED_VARIABLE_VALUE(21, "controlled-variable-value"), //
    COV_INCREMENT(22, "cov-increment"), //
    DATE_LIST(23, "date-list"), //
    DAYLIGHT_SAVINGS_STATUS(24, "daylight-savings-status"), //
    DEADBAND(25, "deadband"), //
    DERIVATIVE_CONSTANT(26, "derivative-constant"), //
    DERIVATIVE_CONSTANT_UNITS(27, "derivative-constant-units"), //
    DESCRIPTION(28, "description"), //
    DESCRIPTION_OF_HALT(29, "description-of-halt"), //
    DEVICE_ADDRESS_BINDING(30, "device-address-binding"), //
    DEVICE_TYPE(31, "device-type"), //
    EFFECTIVE_PERIOD(32, "effective-period"), //
    ELAPSED_ACTIVE_TIME(33, "elapsed-active-time"), //
    ERROR_LIMIT(34, "error-limit"), //
    EVENT_ENABLE(35, "event-enable"), //
    EVENT_STATE(36, "event-state"), //
    EVENT_TYPE(37, "event-type"), //
    EXCEPTION_SCHEDULE(38, "exception-schedule"), //
    FAULT_VALUES(39, "fault-values"), //
    FEEDBACK_VALUE(40, "feedback-value"), //
    FILE_ACCESS_METHOD(41, "file-access-method"), //
    FILE_SIZE(42, "file-size"), //
    FILE_TYPE(43, "file-type"), //
    FIRMWARE_REVISION(44, "firmware-revision"), //
    HIGH_LIMIT(45, "high-limit"), //
    INACTIVE_TEXT(46, "inactive-text"), //
    IN_PROCESS(47, "in-process"), //
    INSTANCE_OF(48, "instance-of"), //
    INTEGRAL_CONSTANT(49, "integral-constant"), //
    INTEGRAL_CONSTANT_UNITS(50, "integral-constant-units"), //
    LIMIT_ENABLE(52, "limit-enable"), //
    LIST_OF_GROUP_MEMBERS(53, "list-of-group-members"), //
    LIST_OF_OBJECT_PROPERTY_REFERENCES(54, "list-of-object-property-references"), //
    LOCAL_DATE(56, "local-date"), //
    LOCAL_TIME(57, "local-time"), //
    LOCATION(58, "location"), //
    LOW_LIMIT(59, "low-limit"), //
    MANIPULATED_VARIABLE_REFERENCE(60, "manipulated-variable-reference"), //
    MAXIMUM_OUTPUT(61, "maximum-output"), //
    MAX_APDU_LENGTH_ACCEPTED(62, "max-apdu-length-accepted"), //
    MAX_INFO_FRAMES(63, "max-info-frames"), //
    MAX_MASTER(64, "max-master"), //
    MAX_PRES_VALUE(65, "max-pres-value"), //
    MINIMUM_OFF_TIME(66, "minimum-off-time"), //
    MINIMUM_ON_TIME(67, "minimum-on-time"), //
    MINIMUM_OUTPUT(68, "minimum-output"), //
    MIN_PRES_VALUE(69, "min-pres-value"), //
    MODEL_NAME(70, "model-name"), //
    MODIFICATION_DATE(71, "modification-date"), //
    NOTIFY_TYPE(72, "notify-type"), //
    NUMBER_OF_APDU_RETRIES(73, "number-of-apdu-retries"), //
    NUMBER_OF_STATES(74, "number-of-states"), //
    OBJECT_IDENTIFIER(75, "object-identifier"), //
    OBJECT_LIST(76, "object-list"), //
    OBJECT_NAME(77, "object-name"), //
    OBJECT_PROPERTY_REFERENCE(78, "object-property-reference"), //
    OBJECT_TYPE(79, "object-type"), //
    OPTIONAL(80, "optional"), //
    OUT_OF_SERVICE(81, "out-of-service"), //
    OUTPUT_UNITS(82, "output-units"), //
    EVENT_PARAMETERS(83, "event-parameters"), //
    POLARITY(84, "polarity"), //
    PRESENT_VALUE(85, "present-value"), //
    PRIORITY(86, "priority"), //
    PRIORITY_ARRAY(87, "priority-array"), //
    PRIORITY_FOR_WRITING(88, "priority-for-writing"), //
    PROCESS_IDENTIFIER(89, "process-identifier"), //
    PROGRAM_CHANGE(90, "program-change"), //
    PROGRAM_LOCATION(91, "program-location"), //
    PROGRAM_STATE(92, "program-state"), //
    PROPORTIONAL_CONSTANT(93, "proportional-constant"), //
    PROPORTIONAL_CONSTANT_UNITS(94, "proportional-constant-units"), //
    PROTOCOL_OBJECT_TYPES_SUPPORTED(96, "protocol-object-types-supported"), //
    PROTOCOL_SERVICES_SUPPORTED(97, "protocol-services-supported"), //
    PROTOCOL_VERSION(98, "protocol-version"), //
    READ_ONLY(99, "read-only"), //
    REASON_FOR_HALT(100, "reason-for-halt"), //
    RECIPIENT_LIST(102, "recipient-list"), //
    RELIABILITY(103, "reliability"), //
    RELINQUISH_DEFAULT(104, "relinquish-default"), //
    REQUIRED(105, "required"), //
    RESOLUTION(106, "resolution"), //
    SEGMENTATION_SUPPORTED(107, "segmentation-supported"), //
    SETPOINT(108, "setpoint"), //
    SETPOINT_REFERENCE(109, "setpoint-reference"), //
    STATE_TEXT(110, "state-text"), //
    STATUS_FLAGS(111, "status-flags"), //
    SYSTEM_STATUS(112, "system-status"), //
    TIME_DELAY(113, "time-delay"), //
    TIME_OF_ACTIVE_TIME_RESET(114, "time-of-active-time-reset"), //
    TIME_OF_STATE_COUNT_RESET(115, "time-of-state-count-reset"), //
    TIME_SYNCHRONIZATION_RECIPIENTS(116, "time-synchronization-recipients"), //
    UNITS(117, "units"), //
    UPDATE_INTERVAL(118, "update-interval"), //
    UTC_OFFSET(119, "utc-offset"), //
    VENDOR_IDENTIFIER(120, "vendor-identifier"), //
    VENDOR_NAME(121, "vendor-name"), //
    VT_CLASSES_SUPPORTED(122, "vt-classes-supported"), //
    WEEKLY_SCHEDULE(123, "weekly-schedule"), //
    ATTEMPTED_SAMPLES(124, "attempted-samples"), //
    AVERAGE_VALUE(125, "average-value"), //
    BUFFER_SIZE(126, "buffer-size"), //
    CLIENT_COV_INCREMENT(127, "client-cov-increment"), //
    COV_RESUBSCRIPTION_INTERVAL(128, "cov-resubscription-interval"), //
    EVENT_TIME_STAMPS(130, "event-time-stamps"), //
    LOG_BUFFER(131, "log-buffer"), //
    LOG_DEVICE_OBJECT_PROPERTY(132, "log-device-object-property"), //
    ENABLE(133, "enable"), //
    LOG_INTERVAL(134, "log-interval"), //
    MAXIMUM_VALUE(135, "maximum-value"), //
    MINIMUM_VALUE(136, "minimum-value"), //
    NOTIFICATION_THRESHOLD(137, "notification-threshold"), //
    PROTOCOL_REVISION(139, "protocol-revision"), //
    RECORDS_SINCE_NOTIFICATION(140, "records-since-notification"), //
    RECORD_COUNT(141, "record-count"), //
    START_TIME(142, "start-time"), //
    STOP_TIME(143, "stop-time"), //
    STOP_WHEN_FULL(144, "stop-when-full"), //
    TOTAL_RECORD_COUNT(145, "total-record-count"), //
    VALID_SAMPLES(146, "valid-samples"), //
    WINDOW_INTERVAL(147, "window-interval"), //
    WINDOW_SAMPLES(148, "window-samples"), //
    MAXIMUM_VALUE_TIMESTAMP(149, "maximum-value-timestamp"), //
    MINIMUM_VALUE_TIMESTAMP(150, "minimum-value-timestamp"), //
    VARIANCE_VALUE(151, "variance-value"), //
    ACTIVE_COV_SUBSCRIPTIONS(152, "active-cov-subscriptions"), //
    BACKUP_FAILURE_TIMEOUT(153, "backup-failure-timeout"), //
    CONFIGURATION_FILES(154, "configuration-files"), //
    DATABASE_REVISION(155, "database-revision"), //
    DIRECT_READING(156, "direct-reading"), //
    LAST_RESTORE_TIME(157, "last-restore-time"), //
    MAINTENANCE_REQUIRED(158, "maintenance-required"), //
    MEMBER_OF(159, "member-of"), //
    MODE(160, "mode"), //
    OPERATION_EXPECTED(161, "operation-expected"), //
    SETTING(162, "setting"), //
    SILENCED(163, "silenced"), //
    TRACKING_VALUE(164, "tracking-value"), //
    ZONE_MEMBERS(165, "zone-members"), //
    LIFE_SAFETY_ALARM_VALUES(166, "life-safety-alarm-values"), //
    MAX_SEGMENTS_ACCEPTED(167, "max-segments-accepted"), //
    PROFILE_NAME(168, "profile-name"), //
    AUTO_SLAVE_DISCOVERY(169, "auto-slave-discovery"), //
    MANUAL_SLAVE_ADDRESS_BINDING(170, "manual-slave-address-binding"), //
    SLAVE_ADDRESS_BINDING(171, "slave-address-binding"), //
    SLAVE_PROXY_ENABLE(172, "slave-proxy-enable"), //
    LAST_NOTIFY_RECORD(173, "last-notify-record"), //
    SCHEDULE_DEFAULT(174, "schedule-default"), //
    ACCEPTED_MODES(175, "accepted-modes"), //
    ADJUST_VALUE(176, "adjust-value"), //
    COUNT(177, "count"), //
    COUNT_BEFORE_CHANGE(178, "count-before-change"), //
    COUNT_CHANGE_TIME(179, "count-change-time"), //
    COV_PERIOD(180, "cov-period"), //
    INPUT_REFERENCE(181, "input-reference"), //
    LIMIT_MONITORING_INTERVAL(182, "limit-monitoring-interval"), //
    LOGGING_OBJECT(183, "logging-object"), //
    LOGGING_RECORD(184, "logging-record"), //
    PRESCALE(185, "prescale"), //
    PULSE_RATE(186, "pulse-rate"), //
    SCALE(187, "scale"), //
    SCALE_FACTOR(188, "scale-factor"), //
    UPDATE_TIME(189, "update-time"), //
    VALUE_BEFORE_CHANGE(190, "value-before-change"), //
    VALUE_SET(191, "value-set"), //
    VALUE_CHANGE_TIME(192, "value-change-time"), //
    ALIGN_INTERVALS(193, "align-intervals"), //
    INTERVAL_OFFSET(195, "interval-offset"), //
    LAST_RESTART_REASON(196, "last-restart-reason"), //
    LOGGING_TYPE(197, "logging-type"), //
    RESTART_NOTIFICATION_RECIPIENTS(202, "restart-notification-recipients"), //
    TIME_OF_DEVICE_RESTART(203, "time-of-device-restart"), //
    TIME_SYNCHRONIZATION_INTERVAL(204, "time-synchronization-interval"), //
    TRIGGER(205, "trigger"), //
    UTC_TIME_SYNCHRONIZATION_RECIPIENTS(206, "utc-time-synchronization-recipients"), //
    NODE_SUBTYPE(207, "node-subtype"), //
    NODE_TYPE(208, "node-type"), //
    STRUCTURED_OBJECT_LIST(209, "structured-object-list"), //
    SUBORDINATE_ANNOTATIONS(210, "subordinate-annotations"), //
    SUBORDINATE_LIST(211, "subordinate-list"), //
    ACTUAL_SHED_LEVEL(212, "actual-shed-level"), //
    DUTY_WINDOW(213, "duty-window"), //
    EXPECTED_SHED_LEVEL(214, "expected-shed-level"), //
    FULL_DUTY_BASELINE(215, "full-duty-baseline"), //
    REQUESTED_SHED_LEVEL(218, "requested-shed-level"), //
    SHED_DURATION(219, "shed-duration"), //
    SHED_LEVEL_DESCRIPTIONS(220, "shed-level-descriptions"), //
    SHED_LEVELS(221, "shed-levels"), //
    STATE_DESCRIPTION(222, "state-description"), //
    DOOR_ALARM_STATE(226, "door-alarm-state"), //
    DOOR_EXTENDED_PULSE_TIME(227, "door-extended-pulse-time"), //
    DOOR_MEMBERS(228, "door-members"), //
    DOOR_OPEN_TOO_LONG_TIME(229, "door-open-too-long-time"), //
    DOOR_PULSE_TIME(230, "door-pulse-time"), //
    DOOR_STATUS(231, "door-status"), //
    DOOR_UNLOCK_DELAY_TIME(232, "door-unlock-delay-time"), //
    LOCK_STATUS(233, "lock-status"), //
    MASKED_ALARM_VALUES(234, "masked-alarm-values"), //
    SECURED_STATUS(235, "secured-status"), //
    ABSENTEE_LIMIT(244, "absentee-limit"), //
    ACCESS_ALARM_EVENTS(245, "access-alarm-events"), //
    ACCESS_DOORS(246, "access-doors"), //
    ACCESS_EVENT(247, "access-event"), //
    ACCESS_EVENT_AUTHENTICATION_FACTOR(248, "access-event-authentication-factor"), //
    ACCESS_EVENT_CREDENTIAL(249, "access-event-credential"), //
    ACCESS_EVENT_TIME(250, "access-event-time"), //
    ACCESS_TRANSACTION_EVENTS(251, "access-transaction-events"), //
    ACCOMPANIMENT(252, "accompaniment"), //
    ACCOMPANIMENT_TIME(253, "accompaniment-time"), //
    ACTIVATION_TIME(254, "activation-time"), //
    ACTIVE_AUTHENTICATION_POLICY(255, "active-authentication-policy"), //
    ASSIGNED_ACCESS_RIGHTS(256, "assigned-access-rights"), //
    AUTHENTICATION_FACTORS(257, "authentication-factors"), //
    AUTHENTICATION_POLICY_LIST(258, "authentication-policy-list"), //
    AUTHENTICATION_POLICY_NAMES(259, "authentication-policy-names"), //
    AUTHENTICATION_STATUS(260, "authentication-status"), //
    AUTHORIZATION_MODE(261, "authorization-mode"), //
    BELONGS_TO(262, "belongs-to"), //
    CREDENTIAL_DISABLE(263, "credential-disable"), //
    CREDENTIAL_STATUS(264, "credential-status"), //
    CREDENTIALS(265, "credentials"), //
    CREDENTIALS_IN_ZONE(266, "credentials-in-zone"), //
    DAYS_REMAINING(267, "days-remaining"), //
    ENTRY_POINTS(268, "entry-points"), //
    EXIT_POINTS(269, "exit-points"), //
    EXPIRY_TIME(270, "expiry-time"), //
    EXTENDED_TIME_ENABLE(271, "extended-time-enable"), //
    FAILED_ATTEMPT_EVENTS(272, "failed-attempt-events"), //
    FAILED_ATTEMPTS(273, "failed-attempts"), //
    FAILED_ATTEMPTS_TIME(274, "failed-attempts-time"), //
    LAST_ACCESS_EVENT(275, "last-access-event"), //
    LAST_ACCESS_POINT(276, "last-access-point"), //
    LAST_CREDENTIAL_ADDED(277, "last-credential-added"), //
    LAST_CREDENTIAL_ADDED_TIME(278, "last-credential-added-time"), //
    LAST_CREDENTIAL_REMOVED(279, "last-credential-removed"), //
    LAST_CREDENTIAL_REMOVED_TIME(280, "last-credential-removed-time"), //
    LAST_USE_TIME(281, "last-use-time"), //
    LOCKOUT(282, "lockout"), //
    LOCKOUT_RELINQUISH_TIME(283, "lockout-relinquish-time"), //
    MAX_FAILED_ATTEMPTS(285, "max-failed-attempts"), //
    MEMBERS(286, "members"), //
    MUSTER_POINT(287, "muster-point"), //
    NEGATIVE_ACCESS_RULES(288, "negative-access-rules"), //
    NUMBER_OF_AUTHENTICATION_POLICIES(289, "number-of-authentication-policies"), //
    OCCUPANCY_COUNT(290, "occupancy-count"), //
    OCCUPANCY_COUNT_ADJUST(291, "occupancy-count-adjust"), //
    OCCUPANCY_COUNT_ENABLE(292, "occupancy-count-enable"), //
    OCCUPANCY_LOWER_LIMIT(294, "occupancy-lower-limit"), //
    OCCUPANCY_LOWER_LIMIT_ENFORCED(295, "occupancy-lower-limit-enforced"), //
    OCCUPANCY_STATE(296, "occupancy-state"), //
    OCCUPANCY_UPPER_LIMIT(297, "occupancy-upper-limit"), //
    OCCUPANCY_UPPER_LIMIT_ENFORCED(298, "occupancy-upper-limit-enforced"), //
    PASSBACK_MODE(300, "passback-mode"), //
    PASSBACK_TIMEOUT(301, "passback-timeout"), //
    POSITIVE_ACCESS_RULES(302, "positive-access-rules"), //
    REASON_FOR_DISABLE(303, "reason-for-disable"), //
    SUPPORTED_FORMATS(304, "supported-formats"), //
    SUPPORTED_FORMAT_CLASSES(305, "supported-format-classes"), //
    THREAT_AUTHORITY(306, "threat-authority"), //
    THREAT_LEVEL(307, "threat-level"), //
    TRACE_FLAG(308, "trace-flag"), //
    TRANSACTION_NOTIFICATION_CLASS(309, "transaction-notification-class"), //
    USER_EXTERNAL_IDENTIFIER(310, "user-external-identifier"), //
    USER_INFORMATION_REFERENCE(311, "user-information-reference"), //
    USER_NAME(317, "user-name"), //
    USER_TYPE(318, "user-type"), //
    USES_REMAINING(319, "uses-remaining"), //
    ZONE_FROM(320, "zone-from"), //
    ZONE_TO(321, "zone-to"), //
    ACCESS_EVENT_TAG(322, "access-event-tag"), //
    GLOBAL_IDENTIFIER(323, "global-identifier"), //
    VERIFICATION_TIME(326, "verification-time"), //
    BASE_DEVICE_SECURITY_POLICY(327, "base-device-security-policy"), //
    DISTRIBUTION_KEY_REVISION(328, "distribution-key-revision"), //
    DO_NOT_HIDE(329, "do-not-hide"), //
    KEY_SETS(330, "key-sets"), //
    LAST_KEY_SERVER(331, "last-key-server"), //
    NETWORK_ACCESS_SECURITY_POLICIES(332, "network-access-security-policies"), //
    PACKET_REORDER_TIME(333, "packet-reorder-time"), //
    SECURITY_PDU_TIMEOUT(334, "security-pdu-timeout"), //
    SECURITY_TIME_WINDOW(335, "security-time-window"), //
    SUPPORTED_SECURITY_ALGORITHMS(336, "supported-security-algorithms"), //
    UPDATE_KEY_SET_TIMEOUT(337, "update-key-set-timeout"), //
    BACKUP_AND_RESTORE_STATE(338, "backup-and-restore-state"), //
    BACKUP_PREPARATION_TIME(339, "backup-preparation-time"), //
    RESTORE_COMPLETION_TIME(340, "restore-completion-time"), //
    RESTORE_PREPARATION_TIME(341, "restore-preparation-time"), //
    BIT_MASK(342, "bit-mask"), //
    BIT_TEXT(343, "bit-text"), //
    IS_UTC(344, "is-utc"), //
    GROUP_MEMBERS(345, "group-members"), //
    GROUP_MEMBER_NAMES(346, "group-member-names"), //
    MEMBER_STATUS_FLAGS(347, "member-status-flags"), //
    REQUESTED_UPDATE_INTERVAL(348, "requested-update-interval"), //
    COVU_PERIOD(349, "covu-period"), //
    COVU_RECIPIENTS(350, "covu-recipients"), //
    EVENT_MESSAGE_TEXTS(351, "event-message-texts"), //
    EVENT_MESSAGE_TEXTS_CONFIG(352, "event-message-texts-config"), //
    EVENT_DETECTION_ENABLE(353, "event-detection-enable"), //
    EVENT_ALGORITHM_INHIBIT(354, "event-algorithm-inhibit"), //
    EVENT_ALGORITHM_INHIBIT_REF(355, "event-algorithm-inhibit-ref"), //
    TIME_DELAY_NORMAL(356, "time-delay-normal"), //
    RELIABILITY_EVALUATION_INHIBIT(357, "reliability-evaluation-inhibit"), //
    FAULT_PARAMETERS(358, "fault-parameters"), //
    FAULT_TYPE(359, "fault-type"), //
    LOCAL_FORWARDING_ONLY(360, "local-forwarding-only"), //
    PROCESS_IDENTIFIER_FILTER(361, "process-identifier-filter"), //
    SUBSCRIBED_RECIPIENTS(362, "subscribed-recipients"), //
    PORT_FILTER(363, "port-filter"), //
    AUTHORIZATION_EXEMPTIONS(364, "authorization-exemptions"), //
    ALLOW_GROUP_DELAY_INHIBIT(365, "allow-group-delay-inhibit"), //
    CHANNEL_NUMBER(366, "channel-number"), //
    CONTROL_GROUPS(367, "control-groups"), //
    EXECUTION_DELAY(368, "execution-delay"), //
    LAST_PRIORITY(369, "last-priority"), //
    WRITE_STATUS(370, "write-status"), //
    PROPERTY_LIST(371, "property-list"), //
    SERIAL_NUMBER(372, "serial-number"), //
    BLINK_WARN_ENABLE(373, "blink-warn-enable"), //
    DEFAULT_FADE_TIME(374, "default-fade-time"), //
    DEFAULT_RAMP_RATE(375, "default-ramp-rate"), //
    DEFAULT_STEP_INCREMENT(376, "default-step-increment"), //
    EGRESS_TIME(377, "egress-time"), //
    IN_PROGRESS(378, "in-progress"), //
    INSTANTANEOUS_POWER(379, "instantaneous-power"), //
    LIGHTING_COMMAND(380, "lighting-command"), //
    LIGHTING_COMMAND_DEFAULT_PRIORITY(381, "lighting-command-default-priority"), //
    MAX_ACTUAL_VALUE(382, "max-actual-value"), //
    MIN_ACTUAL_VALUE(383, "min-actual-value"), //
    POWER(384, "power"), //
    TRANSITION(385, "transition"), //
    EGRESS_ACTIVE(386, "egress-active");

    private int code;
    private String text;

    private BACnetPropertyIdentifier(final int code, final String text) {
        this.code = code;
        this.text = text;
    }

    /**
     * @return the code
     */
    public int getCode() {
        return code;
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    private static Map<Integer, BACnetPropertyIdentifier> lookupCode = new HashMap<Integer, BACnetPropertyIdentifier>();
    private static Map<String, BACnetPropertyIdentifier> lookupText = new HashMap<String, BACnetPropertyIdentifier>();

    static {
        for (final BACnetPropertyIdentifier pid : values()) {
            lookupCode.put(pid.getCode(), pid);
            lookupText.put(pid.getText(), pid);
        }
    }

    public static BACnetPropertyIdentifier getByCode(final int code) {
        final BACnetPropertyIdentifier result = lookupCode.get(code);
        if (result == null) {
            throw new IllegalArgumentException("Invalid property id: " + code);
        }
        return result;
    }

    public static BACnetPropertyIdentifier getByText(final String text) {
        BACnetPropertyIdentifier result = lookupText.get(text);
        if (result == null) {
            result = valueOf(text);
        }
        return result;
    }

}
