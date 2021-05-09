package bacnetzmstp;

import de.bacnetz.controller.Message;

public interface MessageListener {

    void message(Header header, byte[] payloadBuffer, int payloadDataRead);

    Message getLastMessage();

}
