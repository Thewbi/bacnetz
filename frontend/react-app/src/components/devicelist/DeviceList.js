import { useState } from "react";
import "./DeviceList.css";

import DeviceListItem from "../devicelistitem/DeviceListItem.js";

// https://blog.logrocket.com/websockets-tutorial-how-to-go-real-time-with-node-and-react-8e4693fbf843/
//import { w3cwebsocket as W3CWebSocket } from "websocket";

import useWebSocket, { ReadyState } from "react-use-websocket";

function DeviceList() {
  // ws://localhost:8080/bacnetz/push
  //const client = new W3CWebSocket("ws://192.168.0.234:8182/bacnetz/push");

  const [socketUrl, setSocketUrl] = useState(
    "ws://192.168.0.234:8182/bacnetz/push"
  );

  // https://www.npmjs.com/package/react-use-websocket
  const {
    sendMessage,
    sendJsonMessage,
    lastMessage,
    lastJsonMessage,
    readyState,
    getWebSocket,
  } = useWebSocket(socketUrl, {
    onOpen: () => console.log("websocket opened!"),
    // Will attempt to reconnect on all close events, such as server shutting down
    shouldReconnect: (closeEvent) => true,
  });

  const [devices, setDevices] = useState([]);

  function fetchDevicesHandler() {
    fetch("http://192.168.0.234:8182/bacnetz/api/device/all")
      .then((response) => {
        return response.json();
      })
      .then((data) => {
        const listItems = data.map((device) => {
          // map to new simpler object
          const deviceObject = {
            id: device.id,
            name: device.name,
          };

          // create a subcomponent
          return <DeviceListItem key={deviceObject.id} value={deviceObject} />;
        });

        setDevices(listItems);
      });
  }

  return (
    <div id="test" className="ui items">
      {/* <button onClick={connectToWebSocketHandler}>ConnectToWebSocket</button> */}
      <button className="ui button" onClick={fetchDevicesHandler}>
        Fetch Devices
      </button>

      {/* <a href="#" onClick={fetchDevicesHandler}>
        Fetch Devices
      </a> */}

      <h2>Device List ({devices.length})</h2>

      {devices}
    </div>
  );
}

export default DeviceList;
