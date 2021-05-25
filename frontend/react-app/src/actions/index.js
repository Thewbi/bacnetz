export const selectDeviceActionCreator = (device) => {
  return {
    type: "DEVICE_SELECTED",
    payload: device,
  };
};

export const messageReceivedActionCreator = (payload) => {
  return {
    type: "MESSAGE_RECEIVED",
    payload: payload,
  };
};

export const deviceStateChangedActionCreator = (payload) => {
  return {
    type: "DEVICE_STATE_CHANGED",
    payload: payload,
  };
};
