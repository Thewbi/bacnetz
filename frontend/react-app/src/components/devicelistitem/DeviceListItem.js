import logo from "../../logo.svg";

// ID: {props.value.id} Name: {props.value.name}
function DeviceListItem(props) {
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
      </div>
    </div>
  );
}

export default DeviceListItem;
