package de.bacnetz.template;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.bacnetz.controller.Message;
import de.bacnetz.devices.ObjectType;
import de.bacnetz.factory.DefaultMessageFactory;
import de.bacnetz.factory.MessageFactory;

/**
 * Request / Response infrastructure for BACnet RequestObjectList requests. For
 * handling the response implement your own Callback handler and set it into the
 * template before executing the request.
 * 
 * Usage:
 * 
 * <pre>
 * final DefaultBACNetzCallbackHandler defaultBACNetzCallbackHandler = new DefaultBACNetzCallbackHandler();
 * final RequestObjectListTemplate requestObjectListTemplate = new RequestObjectListTemplate(sourceIP, sourcePort,
 *         destinationIP, destinationPort);
 * requestObjectListTemplate.setBacnetzCallbackHandler(defaultBACNetzCallbackHandler);
 * requestObjectListTemplate.send(ObjectType.DEVICE, bacnetID);
 * </pre>
 */
public class RequestObjectListTemplate extends BaseBACnetzTemplate {

    private static final Logger LOG = LogManager.getLogger(RequestObjectListTemplate.class);

    /**
     * ctor
     * 
     * @param sourceIP
     * @param sourcePort
     * @param destinationIP
     * @param destinationPort
     */
    public RequestObjectListTemplate(final String sourceIP, final int sourcePort, final String destinationIP,
            final int destinationPort) {
        super();
        setSourceIP(sourceIP);
        setSourcePort(sourcePort);
        setDestinationIP(destinationIP);
        setDestinationPort(destinationPort);
    }

    /**
     * Request / Response for the specific BACnet message.
     * 
     * @param objectType
     * @param bacnetID
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void send(final ObjectType objectType, final int bacnetID) throws FileNotFoundException, IOException {

        // create the BACnet request message
        final MessageFactory messageFactory = new DefaultMessageFactory();
        final Message outMessage = messageFactory.requestObjectList(objectType, bacnetID);

        sendInternal(outMessage);

        LOG.info("done");
    }

    @Override
    public Logger getLogger() {
        return LOG;
    }

}
