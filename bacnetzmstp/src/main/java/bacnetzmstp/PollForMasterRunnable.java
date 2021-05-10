package bacnetzmstp;

import java.io.IOException;
import java.io.OutputStream;

import de.bacnetz.common.utils.Utils;

public class PollForMasterRunnable implements Runnable {

    private OutputStream outputStream;

    @Override
    public void run() {

        for (int i = 0; i <= 127; i++) {
            final Header responseHeader = new Header();
            responseHeader.setFrameType(FrameType.REPLY_TO_POLL_FOR_MASTER.getNumVal());
            responseHeader.setDestinationAddress(10);
            responseHeader.setSourceAddress(i);

            final byte reply[] = responseHeader.toBytes();

            System.out.println(Utils.bytesToHex(reply));

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

}
