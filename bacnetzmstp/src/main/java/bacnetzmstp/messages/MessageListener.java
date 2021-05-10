package bacnetzmstp.messages;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import bacnetzmstp.Header;
import de.bacnetz.controller.Message;
import de.bacnetz.devices.Device;

public interface MessageListener {

    void message(Header header, byte[] payloadBuffer, int payloadDataRead) throws IOException;

    void setOutputStream(OutputStream outputStream);

    Message getLastMessage();

    Map<Integer, Device> getMasterDevices();

}
