package bacnetzmstp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.openmuc.jrxtx.DataBits;
import org.openmuc.jrxtx.Parity;
import org.openmuc.jrxtx.SerialPort;
import org.openmuc.jrxtx.SerialPortBuilder;
import org.openmuc.jrxtx.StopBits;

import bacnetzmstp.messages.DefaultMessageListener;
import bacnetzmstp.messages.MessageListener;
import de.bacnetz.App;
import de.bacnetz.common.utils.NetworkUtils;
import de.bacnetz.configuration.ConfigurationManager;
import de.bacnetz.controller.DefaultMessageController;
import de.bacnetz.devices.DefaultDeviceFactory;
import de.bacnetz.devices.DefaultDeviceService;
import de.bacnetz.devices.Device;
import de.bacnetz.devices.DeviceService;
import de.bacnetz.factory.DefaultMessageFactory;
import de.bacnetz.factory.MessageFactory;
import de.bacnetz.stack.LinkLayerType;
import de.bacnetz.stack.VendorType;
import de.bacnetz.vendor.VendorMap;

/**
 * Installation of jrxtx
 */
public class Main {

//    private static final boolean PASSIVE_MODE = true;
    private static final boolean PASSIVE_MODE = false;

    private static final int MASTER_DEVICE_ID = 2;

//    private static final int MAX_MASTER = 127;
    private static final int MAX_MASTER = 10;
    // private static final int MAX_MASTER = 9;

    // private static final String COM_PORT = "COM8";
//    private static final String COM_PORT = "COM27";

    private static final String COM_PORT = "/dev/tty.usbserial-AR0KCOCB";
//    private static final String COM_PORT = "/dev/cu.usbserial-AR0KCOCB";

    private static final int MSTP_BAUD_RATE = 76800;

    /**
     * http://kargs.net/BACnet/BACnet_MSTP.pdf
     * 
     * http://www.bacnet.org/Addenda/Add-135-2012an-PPR2-draft-rc4_chair_approved.pdf
     * 
     * @param args
     * @throws IOException
     * @throws ParseException
     */
    public static void main(final String[] args) throws IOException, ParseException {

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
        } else if (StringUtils.isBlank(comPort) && portNames.length != 1) {
            System.out.println("Please select a COM port!");
            return;
        }

        System.out.println("Using COM port: " + comPort);

        final SerialPort serialPort = SerialPortBuilder.newBuilder(comPort).setParity(Parity.NONE)
                .setDataBits(DataBits.DATABITS_8).setStopBits(StopBits.STOPBITS_1).setBaudRate(MSTP_BAUD_RATE).build();

        final InputStream inputStream = serialPort.getInputStream();
        final OutputStream outputStream = serialPort.getOutputStream();

        final ConfigurationManager configurationManager = App.createConfigurationManager(args);

        final MessageFactory messageFactory = new DefaultMessageFactory();
        messageFactory.setLinkLayerType(LinkLayerType.MSTP);

        final DefaultDeviceFactory deviceFactory = new DefaultDeviceFactory();
        deviceFactory.setConfigurationManager(configurationManager);
        deviceFactory.setMessageFactory(messageFactory);

        final DeviceService deviceService = new DefaultDeviceService();
        deviceService.setDeviceFactory(deviceFactory);

        final DefaultMessageController defaultMessageController = new DefaultMessageController();
        defaultMessageController.setLinkLayerType(LinkLayerType.MSTP);
        defaultMessageController.setDeviceService(deviceService);
        defaultMessageController.setMessageFactory(messageFactory);

//        final Map<ObjectIdentifierServiceParameter, Device> deviceMap = new HashMap<>();

//        final DefaultDevice masterDevice = new DefaultDevice();
        final Device masterDevice = deviceFactory.create(deviceService.getDeviceMap(), VendorMap.processVendorMap(),
                MASTER_DEVICE_ID, NetworkUtils.OBJECT_NAME, VendorType.GEZE_GMBH.getCode());
//        masterDevice.setId(MASTER_DEVICE_ID);

        if (!PASSIVE_MODE) {

            //
            // POLL_FOR_MASTER
            //

            final PollForMasterRunnable pollForMasterRunnable = new PollForMasterRunnable();
            pollForMasterRunnable.setMasterDevice(masterDevice);
            pollForMasterRunnable.setMaxMaster(MAX_MASTER);
            pollForMasterRunnable.setOutputStream(outputStream);
            pollForMasterRunnable.setOnceOnly(true);

            final Thread pollForMasterThread = new Thread(pollForMasterRunnable);
            pollForMasterThread.start();

            //
            // testing requests
            //

            final TestRequestRunnable testRequestRunnable = new TestRequestRunnable();
            testRequestRunnable.setMessageController(defaultMessageController);
            testRequestRunnable.setMasterDevice(masterDevice);
//        testRequestRunnable.setMaxMaster(MAX_MASTER);
            testRequestRunnable.setOutputStream(outputStream);

            final Thread testRequestRunnableThread = new Thread(testRequestRunnable);
//            testRequestRunnableThread.start();
        }

        final MessageListener messageListener = new DefaultMessageListener();
        messageListener.setMessageController(defaultMessageController);
        messageListener.setOutputStream(outputStream);
//        messageListener.getMasterDevices().put(masterDevice.getId(), masterDevice);
        messageListener.setDeviceService(deviceService);
        messageListener.setPassiveMode(PASSIVE_MODE);

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
