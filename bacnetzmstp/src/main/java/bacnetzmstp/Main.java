package bacnetzmstp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.openmuc.jrxtx.DataBits;
import org.openmuc.jrxtx.Parity;
import org.openmuc.jrxtx.SerialPort;
import org.openmuc.jrxtx.SerialPortBuilder;
import org.openmuc.jrxtx.StopBits;

import bacnetzmstp.messages.DefaultMessageListener;
import bacnetzmstp.messages.MessageListener;
import de.bacnetz.devices.DefaultDevice;

/**
 * Installation of jrxtx
 *
 * @author U5353
 *
 */
public class Main {

    private static final int MASTER_DEVICE_ID = 2;

    // private static final int MAX_MASTER = 9;
    private static final int MAX_MASTER = 127;

    // private static final String COM_PORT = "COM8";
    private static final String COM_PORT = "COM27";

//    private static final String COM_PORT = "/dev/tty.usbserial-AR0KCOCB";

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

//        System.out.println(System.getProperty("java.library.path"));

        System.out.println("Listing Port Names ...");
        final String[] portNames = SerialPortBuilder.getSerialPortNames();
        if (portNames.length > 0) {
            for (final String portName : portNames) {
                System.out.println(portName);
            }
        } else {
            System.out.println("No COM ports found!");
            return;
        }
        System.out.println("Listing Port Names done.");

        String comPort = COM_PORT;
        if (portNames.length == 1) {
            comPort = portNames[0];
            System.out.println("Using COM port: " + comPort);
        } else {
            System.out.println("Please select a COM port!");
            return;
        }

        final SerialPort serialPort = SerialPortBuilder.newBuilder(comPort).setParity(Parity.NONE)
                .setDataBits(DataBits.DATABITS_8).setStopBits(StopBits.STOPBITS_1).setBaudRate(MSTP_BAUD_RATE).build();

        final InputStream inputStream = serialPort.getInputStream();
        final OutputStream outputStream = serialPort.getOutputStream();

        final DefaultDevice masterDevice = new DefaultDevice();
        masterDevice.setId(MASTER_DEVICE_ID);

        final PollForMasterRunnable pollForMasterRunnable = new PollForMasterRunnable();
        pollForMasterRunnable.setMasterDevice(masterDevice);
        pollForMasterRunnable.setMaxMaster(MAX_MASTER);
        pollForMasterRunnable.setOutputStream(outputStream);

        final Thread pollForMasterThread = new Thread(pollForMasterRunnable);
//        pollForMasterThread.start();

        final MessageListener messageListener = new DefaultMessageListener();
        messageListener.setOutputStream(outputStream);
        messageListener.getMasterDevices().put(masterDevice.getId(), masterDevice);

        final DefaultStateMachine stateMachine = new DefaultStateMachine();
        stateMachine.setMessageListener(messageListener);

        int data = -1;
        while ((data = inputStream.read()) != -1) {
//            System.out.println(data + " (" + Integer.toHexString(data) + ")");

            stateMachine.input(data);
        }

        if (serialPort != null) {
            try {
                serialPort.close();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }

}
