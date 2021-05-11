package bacnetzmstp.messages;

import java.io.IOException;
import java.io.OutputStream;

import bacnetzmstp.Header;
import de.bacnetz.controller.Message;
import de.bacnetz.controller.MessageController;
import de.bacnetz.devices.DeviceService;

public interface MessageListener {

    void message(Header header, byte[] payloadBuffer, int payloadDataRead) throws IOException;

    void setOutputStream(OutputStream outputStream);

    Message getLastMessage();

//    Map<Integer, Device> getMasterDevices();

    void setPassiveMode(boolean passiveMode);

    void setMessageController(MessageController messageController);

    void setDeviceService(DeviceService deviceService);

}
