package com.geze.gmu.services.bacnet;

import java.util.HashMap;
import java.util.Map;

public enum BACnetObjectType {

  ANALOG_INPUT(0, "analog-input"), //
  ANALOG_OUTPUT(1, "analog-output"), //
  ANALOG_VALUE(2, "analog-value"), //
  BINARY_INPUT(3, "binary-input"), //
  BINARY_OUTPUT(4, "binary-output"), //
  BINARY_VALUE(5, "binary-value"), //
  CALENDAR(6, "calendar"), //
  COMMAND(7, "command"), //
  DEVICE(8, "device"), //
  EVENT_ENROLLMENT(9, "event-enrollment"), //
  FILE(10, "file"), //
  GROUP(11, "group"), //
  LOOP(12, "loop"), //
  MULTI_STATE_INPUT(13, "multi-state-input"), //
  MULTI_STATE_OUTPUT(14, "multi-state-output"), //
  NOTIFICATION_CLASS(15, "notification-class"), //
  PROGRAM(16, "program"), //
  SCHEDULE(17, "schedule"), //
  AVERAGING(18, "averaging"), //
  MULTI_STATE_VALUE(19, "multi-state-value"), //
  TREND_LOG(20, "trend-log"), //
  LIFE_SAFETY_POINT(21, "life-safety-point"), //
  LIFE_SAFETY_ZONE(22, "life-safety-zone"), //
  ACCUMULATOR(23, "accumulator"), //
  PULSE_CONVERTER(24, "pulse-converter"), //
  EVENT_LOG(25, "event-log"), //
  GLOBAL_GROUP(26, "global-group"), //
  TREND_LOG_MULTIPLE(27, "trend-log-multiple"), //
  LOAD_CONTROL(28, "load-control"), //
  STRUCTURED_VIEW(29, "structured-view"), //
  ACCESS_DOOR(30, "access-door"), //
  ACCESS_CREDENTIAL(32, "access-credential"), //
  ACCESS_POINT(33, "access-point"), //
  ACCESS_RIGHTS(34, "access-rights"), //
  ACCESS_USER(35, "access-user"), //
  ACCESS_ZONE(36, "access-zone"), //
  CREDENTIAL_DATA_INPUT(37, "credential-data-input"), //
  NETWORK_SECURITY(38, "network-security"), //
  BITSTRING_VALUE(39, "bitstring-value"), //
  CHARACTERSTRING_VALUE(40, "characterstring-value"), //
  DATE_PATTERN_VALUE(41, "date-pattern-value"), //
  DATE_VALUE(42, "date-value"), //
  DATETIME_PATTERN_VALUE(43, "datetime-pattern-value"), //
  DATETIME_VALUE(44, "datetime-value"), //
  INTEGER_VALUE(45, "integer-value"), //
  LARGE_ANALOG_VALUE(46, "large-analog-value"), //
  OCTETSTRING_VALUE(47, "octetstring-value"), //
  POSITIVE_INTEGER_VALUE(48, "positive-integer-value"), //
  TIME_PATTERN_VALUE(49, "time-pattern-value"), //
  TIME_VALUE(50, "time-value"), //
  NOTIFICATION_FORWARDER(51, "notification-forwarder"), //
  ALERT_ENROLLMENT(52, "alert-enrollment"), //
  CHANNEL(53, "channel"), //
  LIGHTING_OUTPUT(54, "lighting-output");

  private int    code;
  private String text;

  private BACnetObjectType(int code, String text) {
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

  private static Map<Integer, BACnetObjectType> lookupCode = new HashMap<Integer, BACnetObjectType>();
  private static Map<String, BACnetObjectType>  lookupText = new HashMap<String, BACnetObjectType>();

  static {
    for (BACnetObjectType objType : values()) {
      lookupCode.put(objType.getCode(), objType);
      lookupText.put(objType.getText(), objType);
    }
  }

  public static BACnetObjectType getByCode(int code) {
    BACnetObjectType result = lookupCode.get(code);
    if (result == null) {
      throw new IllegalArgumentException("Invalid object type: " + code);
    }
    return result;

  }

  public static BACnetObjectType getByText(String text) {
    BACnetObjectType result = lookupText.get(text);
    if (result == null) {
      result = valueOf(text);
    }
    return result;
  }

}
