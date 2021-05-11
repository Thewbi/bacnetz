package bacnetzmstp;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bacnetzmstp.messages.DefaultMessageListener;
import de.bacnetz.common.utils.Utils;
import de.bacnetz.controller.DefaultMessage;
import de.bacnetz.controller.Message;
import de.bacnetz.controller.MessageController;
import de.bacnetz.devices.Device;
import de.bacnetz.devices.DevicePropertyType;
import de.bacnetz.devices.ObjectType;
import de.bacnetz.stack.APDU;
import de.bacnetz.stack.ConfirmedServiceChoice;
import de.bacnetz.stack.ObjectIdentifierServiceParameter;
import de.bacnetz.stack.exception.BACnetzException;

public class TestRequestRunnable implements Runnable {

    private static final int SLEEP_IN_BETWEEN_IN_MS = 10000;
//    private static final int SLEEP_IN_BETWEEN_IN_MS = 500;

    private static final Logger LOG = LogManager.getLogger(TestRequestRunnable.class);

    private Device masterDevice;

    private OutputStream outputStream;

    private MessageController messageController;

    @Override
    public void run() {

        while (true) {

            LOG.info("<<< TestRequest");

//            final byte[] requestAsBytes = DefaultMessageListener.createObjectListRequest(25, masterDevice.getId(), 25);
//            try {
//                outputStream.write(requestAsBytes);
//            } catch (final IOException e) {
//                e.printStackTrace();
//            }

            final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = ObjectIdentifierServiceParameter
                    .createFromTypeAndInstanceNumber(ObjectType.DEVICE, 2);

            final APDU apdu = new APDU();
            apdu.setConfirmedServiceChoice(ConfirmedServiceChoice.READ_PROPERTY);
            apdu.setPropertyIdentifier(DevicePropertyType.OBJECT_LIST.getCode());
            apdu.getServiceParameters().add(objectIdentifierServiceParameter);

            final DefaultMessage defaultMessage = new DefaultMessage();
            defaultMessage.setApdu(apdu);

            final List<Message> processMessage = messageController.processMessage(defaultMessage);
            for (final Message message : processMessage) {
                try {
//                    final byte[] bytes = message.getBytes();
////                    LOG.info(Utils.bytesToHex(bytes));
//
//                    final byte[] testBytes = { 0x01, 0x02, 0x03, 0x04, 0x05 };
//                    LOG.info(Utils.bytesToHex(testBytes));
//
//                    outputStream.write(testBytes);

                    final byte[] frame = DefaultMessageListener.createFrame(1, 2, message);
                    LOG.info(Utils.bytesToHex(frame));

                    outputStream.write(frame);
                    outputStream.flush();
                } catch (final BACnetzException | IOException e) {
                    LOG.error(e.getMessage(), e);
                }
            }

            try {
                Thread.sleep(SLEEP_IN_BETWEEN_IN_MS);
            } catch (final InterruptedException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    public Device getMasterDevice() {
        return masterDevice;
    }

    public void setMasterDevice(final Device masterDevice) {
        this.masterDevice = masterDevice;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(final OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void setMessageController(final MessageController messageController) {
        this.messageController = messageController;
    }

}
