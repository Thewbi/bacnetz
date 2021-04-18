package de.bacnetz.template.callback;

import de.bacnetz.stack.APDU;

public interface BACNetzCallbackHandler {

    void process(APDU apdu);

}
