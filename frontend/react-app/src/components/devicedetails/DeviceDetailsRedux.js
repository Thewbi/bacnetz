import { selectDeviceActionCreator } from "../../actions";
import { connect } from "react-redux";

// ID: {props.value.id} Name: {props.value.name}
function DeviceDetailsRedux(props) {
  if (!props.selectedDevice) {
    return <div>Please Select a Device!</div>;
  } else {
    return (
      <div>
        <div>{JSON.stringify(props)}</div>
        <div>{props.selectedDevice.id}</div>
        <div>{props.selectedDevice.name}</div>
      </div>
    );
  }
}

const mapStateToProps = (state) => {
  //   console.log(state);
  return { selectedDevice: state.selectedDevice };
};

export default connect(mapStateToProps, {
  selectDevice: selectDeviceActionCreator,
})(DeviceDetailsRedux);
