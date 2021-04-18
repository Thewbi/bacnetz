package de.bacnetz.template.callback;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.bacnetz.stack.APDU;

public class DefaultBACNetzCallbackHandler implements BACNetzCallbackHandler {

    private static final Logger LOG = LogManager.getLogger(DefaultBACNetzCallbackHandler.class);

    @Override
    public void process(final APDU apdu) {
        LOG.info(apdu);
    }

}
