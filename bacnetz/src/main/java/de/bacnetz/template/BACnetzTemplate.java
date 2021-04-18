package de.bacnetz.template;

import java.io.FileNotFoundException;
import java.io.IOException;

import de.bacnetz.devices.ObjectType;
import de.bacnetz.template.callback.BACNetzCallbackHandler;

public interface BACnetzTemplate {

    void send(ObjectType objectType, int bacnetID) throws FileNotFoundException, IOException;

    void setBacnetzCallbackHandler(BACNetzCallbackHandler bacnetzCallbackHandler);

}
