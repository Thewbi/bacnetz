package bacnetzmstp;

import java.io.IOException;
import java.io.OutputStream;

import de.bacnetz.devices.DefaultDevice;

public class PollForMasterRunnable implements Runnable {

    private DefaultDevice masterDevice;

    private OutputStream outputStream;

    private int maxMaster;

    @Override
    public void run() {

        for (int i = 0; i <= maxMaster; i++) {

            final Header responseHeader = new Header();
            responseHeader.setFrameType(FrameType.POLL_FOR_MASTER.getNumVal());
            responseHeader.setDestinationAddress(i);
            responseHeader.setSourceAddress(masterDevice.getId());
            responseHeader.setLength1(0x00);
            responseHeader.setLength2(0x00);

            final byte reply[] = responseHeader.toBytes();

            responseHeader.setCrc(reply[7]);

//            System.out.println(Utils.bytesToHex(reply));

            try {
                outputStream.write(reply);
            } catch (final IOException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(1000);
            } catch (final InterruptedException e) {
                e.printStackTrace();
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
