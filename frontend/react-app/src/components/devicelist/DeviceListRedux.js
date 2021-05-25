import React, { Component } from "react";
import { connect } from "react-redux";
import DeviceListItem from "../devicelistitem/DeviceListItem";
import DeviceListItemRedux from "../devicelistitem/DeviceListItemRedux";
import { selectDevice } from "../../actions";
import { useContext } from "react";

import {
  useWebSocket,
  WebSocketContext,
} from "../../contexts/WebSocketContext";

class DeviceListRedux extends Component {
  //function DeviceListRedux {

  //ws = useContext(WebSocketContext);

  // https://stackoverflow.com/questions/61498035/react-usecontext-inside-class
  //static ws = WebSocketContext;

  constructor() {
    super();
    this.number = 0;
  }

  componentDidMount() {
    this.ws = this.context;
    console.log("componentDidMount: " + this.ws);
    console.log(this.ws);
  }

  testFunction = () => {
    console.log("testFunction: " + this.ws + " " + JSON.stringify(this.ws));

    this.number++;
    let payload = {
      number: this.number,
      messageText: "flup",
    };
    this.ws.socket.send(JSON.stringify(payload));
  };

  renderDevicesList() {
    const tempDeviceListItemComponents = this.props.devices.map((device) => {
      // create a subcomponent
      return <DeviceListItemRedux key={device.id} value={device} />;
    });

    return tempDeviceListItemComponents;
  }

  render() {
    const deviceListItems = this.renderDevicesList();
    return (
      <div id="test" className="ui divided items">
        <button onClick={this.testFunction}>test</button>
        <h2>Device List ({deviceListItems.length})</h2>
        {deviceListItems}
      </div>
    );
  }
}
DeviceListRedux.contextType = WebSocketContext;

const mapStateToProps = (state) => {
  return { devices: state.devices };
};

export default connect(mapStateToProps, { selectDevice: selectDevice })(
  DeviceListRedux
);
