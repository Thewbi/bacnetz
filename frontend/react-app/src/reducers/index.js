import { combineReducers } from "redux";

const devicesReducer = () => {
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
  return [
    { id: 1, name: "TZ320-1" },
    { id: 2, name: "TZ320-2" },
    { id: 3, name: "TZ320-3" },
    { id: 4, name: "TZ320-4" },
  ];
};

const selectedDeviceReducer = (selectedDevice = null, action) => {
  if (action.type === "DEVICE_SELECTED") {
    return action.payload;
  }
  return selectedDevice;
};

export default combineReducers({
  devices: devicesReducer,
  selectedDevice: selectedDeviceReducer,
});
