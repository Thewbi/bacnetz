package bacnetzmstp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.openmuc.jrxtx.DataBits;
import org.openmuc.jrxtx.Parity;
import org.openmuc.jrxtx.SerialPort;
import org.openmuc.jrxtx.SerialPortBuilder;
import org.openmuc.jrxtx.StopBits;

/**
 * Installation of jrxtx
 *
 * @author U5353
 *
 */
public class Main {

    private static final int MSTP_BAUD_RATE = 76800;

    /**
     * http://kargs.net/BACnet/BACnet_MSTP.pdf
     * 
     * http://www.bacnet.org/Addenda/Add-135-2012an-PPR2-draft-rc4_chair_approved.pdf
     * 
     * @param args
     * @throws IOException
     */
    public static void main(final String[] args) throws IOException {

        final DefaultStateMachine stateMachine = new DefaultStateMachine();

//        System.out.println(System.getProperty("java.library.path"));

        final String[] portNames = SerialPortBuilder.getSerialPortNames();

        for (final String portName : portNames) {
            System.out.println(portName);
        }

        final SerialPort serialPort = SerialPortBuilder.newBuilder("COM8").setParity(Parity.NONE)
                .setDataBits(DataBits.DATABITS_8).setStopBits(StopBits.STOPBITS_1).setBaudRate(MSTP_BAUD_RATE).build();

        final InputStream inputStream = serialPort.getInputStream();

        int data = -1;
        while ((data = inputStream.read()) != -1) {
//            System.out.println(data + " (" + Integer.toHexString(data) + ")");

            stateMachine.input(data);
        }

        final OutputStream outputStream = serialPort.getOutputStream();

        if (serialPort != null) {
            try {
                serialPort.close();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }

}