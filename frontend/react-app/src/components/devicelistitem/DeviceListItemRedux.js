import logo from "../../logo.svg";
import { selectDeviceActionCreator } from "../../actions";
import { connect } from "react-redux";

// ID: {props.value.id} Name: {props.value.name}
function DeviceListItemRedux(props) {
  return (
    <div id={props.value.id} className="item">
      <div className="image">
        <img src={logo} className="App-logo" alt="logo" />
      </div>
      <div className="content">
        <a className="header">{props.value.name}</a>
        <div className="meta">
          <span className="cinema">ID: {props.value.id}</span>
        </div>
        <div className="description">
          <p>BACnet Device</p>
        </div>
        <div className="description">
          <p>State: {JSON.stringify(props.value.state)}</p>
        </div>
        <button
          className="ui button primary"
          onClick={() => props.selectDevice(props.value)}
        >
          Select
        </button>
      </div>
    </div>
  );
}

const mapStateToProps = (state) => {
  console.log(state);
  return { devices: state.devices };
};

export default connect(mapStateToProps, {
  selectDevice: selectDeviceActionCreator,
})(DeviceListItemRedux);
