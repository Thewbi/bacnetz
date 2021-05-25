import React, { createContext } from "react";
//import io from "socket.io-client";
//import { WS_BASE } from "./config";
import { useDispatch } from "react-redux";
import { messageReceived } from "../actions";
//import WebSocket from "ws";
import { w3cwebsocket as W3CWebSocket } from "websocket";
import { devicesReducer } from "../reducers";
import { deviceStateChangedActionCreator } from "../actions";

const WebSocketContext = createContext(null);

export { WebSocketContext };

// this is the websocket provider
export default ({ children }) => {
  console.log("WebsocketProvider");
  //   let socket;
  //   let ws;

  //let websocketUrl = "ws://127.0.0.1:8182/bacnetz/push";
  let websocketUrl = "ws://192.168.0.234:8182/bacnetz/push";

  let socket;
  let ws;

  const dispatch = useDispatch();

  const sendMessage = (message) => {
    const payload = {
      data: message,
    };
    socket.send(payload);
    //socket.emit("event://send-message", JSON.stringify(payload));
    // dispatch(updateChatLog(payload));
  };

  if (!socket) {
    // socket = io.connect("ws://127.0.0.1:8182/bacnetz/push");

    // socket.on("event://get-message", (msg) => {
    //   const payload = JSON.parse(msg);
    //   dispatch(messageReceived(payload));
    // });

    socket = new W3CWebSocket(websocketUrl);

    socket.onmessage = (message) => {
      console.log(message);
      console.log(message.origin);
      console.log(message.data);
      console.log("Context, message: " + message);
      //console.log("Context, message: " + JSON.stringify(message));

      let msgAsJson = JSON.parse(message.data);
      console.log(JSON.stringify(msgAsJson));

      dispatch(deviceStateChangedActionCreator(msgAsJson));
    };

    ws = {
      socket: socket,
      sendMessage,
    };
  }

  console.log("Context: " + JSON.stringify(ws));
  console.log(ws);

  return (
    <WebSocketContext.Provider value={ws}>{children}</WebSocketContext.Provider>
  );
};

export function useWebSocket() {
  console.log("useWebSocket");
  const context = React.useContext(WebSocketContext);
  if (context === undefined) {
    throw new Error(
      "useWebSocket must be used within a WebSocketContextProvider"
    );
  }
  return context;
}
