import "./App.css";
import DeviceList from "../devicelist/DeviceList";
import DeviceListRedux from "../devicelist/DeviceListRedux";
import DeviceDetailsRedux from "../devicedetails/DeviceDetailsRedux";
import { Provider, useSelector, useDispatch } from "react-redux";
import WebSocketProvider, {
  WebSocketContext,
} from "../../contexts/WebSocketContext";
import React from "react";
import { useContext } from "react";

//class App extends React.Component {
function App() {
  return (
    <div className="App">
      <div className="ui container grid">
        <div className="ui row">
          <div className="column eight wide">
            {/* <DeviceList /> */}
            <WebSocketProvider>
              <DeviceListRedux />
            </WebSocketProvider>
          </div>
          <div className="column eight wide">
            <DeviceDetailsRedux />
          </div>
        </div>
      </div>
    </div>
  );
}

export default App;

{
  /* <Provider store={store}>
      <WebSocketProvider></WebSocketProvider>
</WebSocketProvider>
    </Provider>

    <WebSocketContext.Provider value="null">
     </WebSocketContext.Provider>*/
}
