import { combineReducers } from "redux";

export const devicesReducer = (devices, action) => {
  console.log(
    "devicesReducer devices: " +
      JSON.stringify(devices) +
      " action: " +
      JSON.stringify(action)
  );

  //   const devices = fetch("http://192.168.0.234:8182/bacnetz/api/device/all")
  //     .then((response) => {
  //       return response.json();
  //     })
  //     .then((data) => {
  //       const listItems = data.map((device) => {
  //         // map to new simpler object
  //         const deviceObject = {
  //           id: device.id,
  //           name: device.name,
  //         };
  //       });

  //       return listItems;
  //     });
  //   return devices;

  if (action) {
    console.log("Action is defined!");
    if (action.type === "DEVICE_STATE_CHANGED") {
      console.log("Action is DEVICE_STATE_CHANGED!");
      return [
        { id: 1, name: "TZ320-1", state: action.payload },
        { id: 2, name: "TZ320-2", state: action.payload },
        { id: 3, name: "TZ320-3", state: action.payload },
        { id: 4, name: "TZ320-4", state: action.payload },
      ];
    }
    if (action.type === "DEVICE_SELECTED") {
      console.log("Action is DEVICE_SELECTED!");
      return devices;
    } else {
      console.log("Action is unknown, maybe init");
      return [
        { id: 1, name: "TZ320-1", state: "Door Closed" },
        { id: 2, name: "TZ320-2", state: "Door Closed" },
        { id: 3, name: "TZ320-3", state: "Door Closed" },
        { id: 4, name: "TZ320-4", state: "Door Closed" },
      ];
    }
  } else {
    console.log("Action is NOT defined!");
    return devices;
  }
};

export const selectedDeviceReducer = (selectedDevice = null, action) => {
  if (action.type === "DEVICE_SELECTED") {
    return action.payload;
  }
  return selectedDevice;
};

export default combineReducers({
  devices: devicesReducer,
  selectedDevice: selectedDeviceReducer,
});
