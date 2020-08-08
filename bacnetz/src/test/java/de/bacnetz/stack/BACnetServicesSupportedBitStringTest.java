package de.bacnetz.stack;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.BitSet;

import org.junit.jupiter.api.Test;

import de.bacnetz.common.utils.Utils;

public class BACnetServicesSupportedBitStringTest {

    @Test
    public void testEncode() {

        final BACnetServicesSupportedBitString bacnetServicesSupportedBitString = new BACnetServicesSupportedBitString();

        bacnetServicesSupportedBitString.setAcknowledgeAlarm(false);
        bacnetServicesSupportedBitString.setConfirmedCOVNotification(true);
        bacnetServicesSupportedBitString.setConfirmedEventNotification(false);
        bacnetServicesSupportedBitString.setGetAlarmSummary(false);
        bacnetServicesSupportedBitString.setGetEnrollmentSummary(false);
        bacnetServicesSupportedBitString.setSubscribeCOV(false);
        bacnetServicesSupportedBitString.setAtomicReadFile(true);
        bacnetServicesSupportedBitString.setAtomicWriteFile(true);

        bacnetServicesSupportedBitString.setAddListElement(true);
        bacnetServicesSupportedBitString.setRemoveListElement(true);
        bacnetServicesSupportedBitString.setCreateObject(false);
        bacnetServicesSupportedBitString.setDeleteObject(false);
        bacnetServicesSupportedBitString.setReadProperty(true);
        bacnetServicesSupportedBitString.setReadPropertyMultiple(true);
        bacnetServicesSupportedBitString.setWriteProperty(true);

        bacnetServicesSupportedBitString.setWritePropertyMultiple(true);
        bacnetServicesSupportedBitString.setDeviceCommunicationControl(true);
        bacnetServicesSupportedBitString.setConfirmedPrivateTransfer(false);
        bacnetServicesSupportedBitString.setConfirmedTextMessage(false);
        bacnetServicesSupportedBitString.setReinitializeDevice(true);
        bacnetServicesSupportedBitString.setVtOpen(false);
        bacnetServicesSupportedBitString.setVtClose(false);
        bacnetServicesSupportedBitString.setVtData(false);

        bacnetServicesSupportedBitString.setiAm(true);
        bacnetServicesSupportedBitString.setiHave(false);
        bacnetServicesSupportedBitString.setUnconfirmedCOVNotification(true);
        bacnetServicesSupportedBitString.setUnconfirmedEventNotification(false);
        bacnetServicesSupportedBitString.setUnconfirmedPrivateTransfer(false);
        bacnetServicesSupportedBitString.setUnconfirmedTextMessage(false);

        bacnetServicesSupportedBitString.setTimeSynchronization(true);
        bacnetServicesSupportedBitString.setWhoHas(true);
        bacnetServicesSupportedBitString.setWhoIs(true);
        bacnetServicesSupportedBitString.setReadRange(true);
        bacnetServicesSupportedBitString.setUtcTimeSynchronization(true);
        bacnetServicesSupportedBitString.setLifeSafetyOperation(false);
        bacnetServicesSupportedBitString.setSubscribeCOVProperty(true);
        bacnetServicesSupportedBitString.setGetEventInformation(false);

        final BitSet bitSet = bacnetServicesSupportedBitString.getBitSet();
        final byte[] byteArray = bitSet.toByteArray();

        final String byteArrayToStringNoPrefix = Utils.byteArrayToStringNoPrefix(byteArray);

        System.out.println(byteArrayToStringNoPrefix);

        final byte[] expectedByteArray = new byte[] { (byte) 0x43, (byte) 0xCB, (byte) 0xC8, (byte) 0x28, (byte) 0xFA };

        assertTrue(Arrays.equals(byteArray, expectedByteArray));
    }
}
