export const selectDevice = (device) => {
  return {
    type: "DEVICE_SELECTED",
    payload: device,
  };
};

export const messageReceived = (payload) => {
  return {
    type: "MESSAGE_RECEIVED",
    payload: payload,
  };
};
