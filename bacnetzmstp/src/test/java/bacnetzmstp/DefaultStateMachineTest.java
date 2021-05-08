package bacnetzmstp;

import org.junit.jupiter.api.Test;

import de.bacnetz.common.utils.Utils;

public class DefaultStateMachineTest {

    /**
     * Incomplete data, sequence does not start at preamble but in between messages
     * 64 (40) 169 (a9) 35 (23) 16 (10) 16 (10) 253 (fd) 85 (55) 255 (ff) 64 (40)
     * 169 (a9) 35 (23)
     */
    @Test
    public void dataTest() {

        final DefaultStateMachine stateMachine = new DefaultStateMachine();

        final byte[] hexStringToByteArray = Utils.hexStringToByteArray("40a9231010fd55ff40a923");

        for (final byte data : hexStringToByteArray) {
            stateMachine.input(data & 0xFF);
        }
    }

    /**
     * 85 (55) 255 (ff) 64 (40) 40 (28) 4 (4) 4 (4) 106 (6a) 255 (ff) 85 (55) 255
     * (ff) 64 (40) 68 (44) 35 (23) 32 (20) 208 (d0) 254 (fe) 85 (55) 255 (ff) 64
     * (40) 68 (44) 35 (23) 32 (20) 144 (90) 251 (fb)
     */
    @Test
    public void dataTest2() {

        final DefaultStateMachine stateMachine = new DefaultStateMachine();

        // header.frametype = 64d = 0x40
        // header.destinationAddress = 0x28
        // header.sourceAddress = 0x04
        // header.length = 0x04 + 0x6a
        final String msg1 = "55ff402804046a";

        final String msg2 = "55ff40442320d0fe";
        final byte[] hexStringToByteArray = Utils.hexStringToByteArray(msg1 + msg2);

        for (final byte data : hexStringToByteArray) {
            stateMachine.input(data & 0xFF);
        }
    }

    /**
     * 
     */
    @Test
    public void pollForMaster() {

        final DefaultStateMachine stateMachine = new DefaultStateMachine();

        // header.frametype = 1d = 0x01 (Poll For Master)
        // header.destinationAddress = 57d = 0x39
        // header.sourceAddress = 26d = 0x19
        // header.length = 0x00 + 0x00
        // header.crc = 229d = 0xe5
        final String msg1 = "55ff0139190000e5"; // 0x55 0xff 0x01 0x39 0x19 0x00 0x00 0xe5

        final byte[] hexStringToByteArray = Utils.hexStringToByteArray(msg1);

        for (final byte data : hexStringToByteArray) {
            stateMachine.input(data & 0xFF);
        }
    }

}
