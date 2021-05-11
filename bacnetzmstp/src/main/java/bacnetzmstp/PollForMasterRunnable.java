package bacnetzmstp;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.bacnetz.devices.DefaultDevice;

public class PollForMasterRunnable implements Runnable {

//    private static final int SLEEP_IN_BETWEEN_IN_MS = 1000;
    private static final int SLEEP_IN_BETWEEN_IN_MS = 500;

    private static final Logger LOG = LogManager.getLogger(PollForMasterRunnable.class);

    private DefaultDevice masterDevice;

    private OutputStream outputStream;

    private int maxMaster;

    @Override
    public void run() {

        while (true) {

            for (int i = 0; i <= maxMaster; i++) {

                LOG.info("<<< Message sent - POLL_FOR_MASTER for foreign id " + i);

                final Header requestHeader = new Header();
                requestHeader.setFrameType(FrameType.POLL_FOR_MASTER.getNumVal());
                requestHeader.setDestinationAddress(i);
                requestHeader.setSourceAddress(masterDevice.getId());
                requestHeader.setLength1(0x00);
                requestHeader.setLength2(0x00);

                final byte requestAsBytes[] = requestHeader.toBytes();

                requestHeader.setCrc(requestAsBytes[7]);

                // System.out.println(Utils.bytesToHex(reply));

                try {
                    outputStream.write(requestAsBytes);
                } catch (final IOException e) {
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(SLEEP_IN_BETWEEN_IN_MS);
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(final OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public int getMaxMaster() {
        return maxMaster;
    }

    public void setMaxMaster(final int maxMaster) {
        this.maxMaster = maxMaster;
    }

    public DefaultDevice getMasterDevice() {
        return masterDevice;
    }

    public void setMasterDevice(final DefaultDevice masterDevice) {
        this.masterDevice = masterDevice;
    }

}
