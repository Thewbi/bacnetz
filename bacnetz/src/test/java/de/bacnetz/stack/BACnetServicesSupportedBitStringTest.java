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

    @Test
    public void testEncode_False() {

        final BACnetServicesSupportedBitString bacnetServicesSupportedBitString = new BACnetServicesSupportedBitString();

        bacnetServicesSupportedBitString.setAcknowledgeAlarm(false);
        bacnetServicesSupportedBitString.setConfirmedCOVNotification(false);
        bacnetServicesSupportedBitString.setConfirmedEventNotification(false);
        bacnetServicesSupportedBitString.setGetAlarmSummary(false);
        bacnetServicesSupportedBitString.setGetEnrollmentSummary(false);
        bacnetServicesSupportedBitString.setSubscribeCOV(false);
        bacnetServicesSupportedBitString.setAtomicReadFile(false);
        bacnetServicesSupportedBitString.setAtomicWriteFile(false);

        bacnetServicesSupportedBitString.setAddListElement(false);
        bacnetServicesSupportedBitString.setRemoveListElement(false);
        bacnetServicesSupportedBitString.setCreateObject(false);
        bacnetServicesSupportedBitString.setDeleteObject(false);
        bacnetServicesSupportedBitString.setReadProperty(false);
        bacnetServicesSupportedBitString.setReadPropertyMultiple(false);
        bacnetServicesSupportedBitString.setWriteProperty(false);

        bacnetServicesSupportedBitString.setWritePropertyMultiple(false);
        bacnetServicesSupportedBitString.setDeviceCommunicationControl(false);
        bacnetServicesSupportedBitString.setConfirmedPrivateTransfer(false);
        bacnetServicesSupportedBitString.setConfirmedTextMessage(false);
        bacnetServicesSupportedBitString.setReinitializeDevice(false);
        bacnetServicesSupportedBitString.setVtOpen(false);
        bacnetServicesSupportedBitString.setVtClose(false);
        bacnetServicesSupportedBitString.setVtData(false);

        bacnetServicesSupportedBitString.setiAm(false);
        bacnetServicesSupportedBitString.setiHave(false);
        bacnetServicesSupportedBitString.setUnconfirmedCOVNotification(false);
        bacnetServicesSupportedBitString.setUnconfirmedEventNotification(false);
        bacnetServicesSupportedBitString.setUnconfirmedPrivateTransfer(false);
        bacnetServicesSupportedBitString.setUnconfirmedTextMessage(false);

        bacnetServicesSupportedBitString.setTimeSynchronization(false);
        bacnetServicesSupportedBitString.setWhoHas(false);
        bacnetServicesSupportedBitString.setWhoIs(false);
        bacnetServicesSupportedBitString.setReadRange(false);
        bacnetServicesSupportedBitString.setUtcTimeSynchronization(false);
        bacnetServicesSupportedBitString.setLifeSafetyOperation(false);
        bacnetServicesSupportedBitString.setSubscribeCOVProperty(false);
        bacnetServicesSupportedBitString.setGetEventInformation(false);

        final BitSet bitSet = bacnetServicesSupportedBitString.getBitSet();

        // https://stackoverflow.com/questions/11209600/how-do-i-convert-a-bitset-initialized-with-false-in-a-byte-containing-0-in-java
        final int desiredLength = 5;
        final byte[] byteArray = Arrays.copyOf(bitSet.toByteArray(), desiredLength);

        final String byteArrayToStringNoPrefix = Utils.byteArrayToStringNoPrefix(byteArray);

        System.out.println(byteArrayToStringNoPrefix);

        final byte[] expectedByteArray = new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 };

        assertTrue(Arrays.equals(byteArray, expectedByteArray));
    }

    @Test
    public void testEncode_True() {

        final BACnetServicesSupportedBitString bacnetServicesSupportedBitString = new BACnetServicesSupportedBitString();

        // byte 1
        bacnetServicesSupportedBitString.setAcknowledgeAlarm(true);
        bacnetServicesSupportedBitString.setConfirmedCOVNotification(true);
        bacnetServicesSupportedBitString.setConfirmedEventNotification(true);
        bacnetServicesSupportedBitString.setGetAlarmSummary(true);
        bacnetServicesSupportedBitString.setGetEnrollmentSummary(true);
        bacnetServicesSupportedBitString.setSubscribeCOV(true);
        bacnetServicesSupportedBitString.setAtomicReadFile(true);
        bacnetServicesSupportedBitString.setAtomicWriteFile(true);

        // byte 2
        bacnetServicesSupportedBitString.setAddListElement(true);
        bacnetServicesSupportedBitString.setRemoveListElement(true);
        bacnetServicesSupportedBitString.setCreateObject(true);
        bacnetServicesSupportedBitString.setDeleteObject(true);
        bacnetServicesSupportedBitString.setReadProperty(true);
        bacnetServicesSupportedBitString.setReadPropertyMultiple(true);
        bacnetServicesSupportedBitString.setWriteProperty(true);
        bacnetServicesSupportedBitString.setWritePropertyMultiple(true);

        // byte 3
        bacnetServicesSupportedBitString.setDeviceCommunicationControl(true);
        bacnetServicesSupportedBitString.setConfirmedPrivateTransfer(true);
        bacnetServicesSupportedBitString.setConfirmedTextMessage(true);
        bacnetServicesSupportedBitString.setReinitializeDevice(true);
        bacnetServicesSupportedBitString.setVtOpen(true);
        bacnetServicesSupportedBitString.setVtClose(true);
        bacnetServicesSupportedBitString.setVtData(true);
        bacnetServicesSupportedBitString.setiAm(true);

        // byte 4
        bacnetServicesSupportedBitString.setiHave(true);
        bacnetServicesSupportedBitString.setUnconfirmedCOVNotification(true);
        bacnetServicesSupportedBitString.setUnconfirmedEventNotification(true);
        bacnetServicesSupportedBitString.setUnconfirmedPrivateTransfer(true);
        bacnetServicesSupportedBitString.setUnconfirmedTextMessage(true);
        bacnetServicesSupportedBitString.setTimeSynchronization(true);
        bacnetServicesSupportedBitString.setWhoHas(true);
        bacnetServicesSupportedBitString.setWhoIs(true);

        bacnetServicesSupportedBitString.setReadRange(true);
        bacnetServicesSupportedBitString.setUtcTimeSynchronization(true);
        bacnetServicesSupportedBitString.setLifeSafetyOperation(true);
        bacnetServicesSupportedBitString.setSubscribeCOVProperty(true);
        bacnetServicesSupportedBitString.setGetEventInformation(true);

        final BitSet bitSet = bacnetServicesSupportedBitString.getBitSet();

        final int desiredLength = 5;
        final byte[] byteArray = Arrays.copyOf(bitSet.toByteArray(), desiredLength);

        final String byteArrayToStringNoPrefix = Utils.byteArrayToStringNoPrefix(byteArray);

        System.out.println(byteArrayToStringNoPrefix);

        final byte[] expectedByteArray = new byte[] { (byte) 0xFF, (byte) 0xFB, (byte) 0xFF, (byte) 0x3F, (byte) 0xFF };

        assertTrue(Arrays.equals(byteArray, expectedByteArray));
    }

    @Test
    public void testEncode_Test() {

        final BACnetServicesSupportedBitString bacnetServicesSupportedBitString = new BACnetServicesSupportedBitString();

        // byte 1
        bacnetServicesSupportedBitString.setAcknowledgeAlarm(true);
        bacnetServicesSupportedBitString.setConfirmedCOVNotification(false);
        bacnetServicesSupportedBitString.setConfirmedEventNotification(false);
        bacnetServicesSupportedBitString.setGetAlarmSummary(true);
        bacnetServicesSupportedBitString.setGetEnrollmentSummary(true);
        bacnetServicesSupportedBitString.setSubscribeCOV(true);
        bacnetServicesSupportedBitString.setAtomicReadFile(true);
        bacnetServicesSupportedBitString.setAtomicWriteFile(true);

        // byte 2
        bacnetServicesSupportedBitString.setAddListElement(true);
        bacnetServicesSupportedBitString.setRemoveListElement(true);
        bacnetServicesSupportedBitString.setCreateObject(false);
        bacnetServicesSupportedBitString.setDeleteObject(false);
        bacnetServicesSupportedBitString.setReadProperty(true);
        bacnetServicesSupportedBitString.setReadPropertyConditional(false);
        bacnetServicesSupportedBitString.setReadPropertyMultiple(true);
        bacnetServicesSupportedBitString.setWriteProperty(true);

        // byte 3
        bacnetServicesSupportedBitString.setWritePropertyMultiple(true);
        bacnetServicesSupportedBitString.setDeviceCommunicationControl(true);
        bacnetServicesSupportedBitString.setConfirmedPrivateTransfer(true);
        bacnetServicesSupportedBitString.setConfirmedTextMessage(false);
        bacnetServicesSupportedBitString.setReinitializeDevice(true);
        bacnetServicesSupportedBitString.setVtOpen(false);
        bacnetServicesSupportedBitString.setVtClose(false);
        bacnetServicesSupportedBitString.setVtData(false);

        // byte 4
        bacnetServicesSupportedBitString.setAuthenticate(false);
        bacnetServicesSupportedBitString.setRequestKey(false);
        bacnetServicesSupportedBitString.setiAm(true);
        bacnetServicesSupportedBitString.setiHave(true);
        bacnetServicesSupportedBitString.setUnconfirmedCOVNotification(false);
        bacnetServicesSupportedBitString.setUnconfirmedEventNotification(false);
        bacnetServicesSupportedBitString.setUnconfirmedPrivateTransfer(true);
        bacnetServicesSupportedBitString.setUnconfirmedTextMessage(false);

        // byte 5
        bacnetServicesSupportedBitString.setTimeSynchronization(true);
        bacnetServicesSupportedBitString.setWhoHas(true);
        bacnetServicesSupportedBitString.setWhoIs(true);
        bacnetServicesSupportedBitString.setReadRange(false);
        bacnetServicesSupportedBitString.setUtcTimeSynchronization(true);
        bacnetServicesSupportedBitString.setLifeSafetyOperation(false);
        bacnetServicesSupportedBitString.setSubscribeCOVProperty(false);
        bacnetServicesSupportedBitString.setGetEventInformation(true);

        final BitSet bitSet = bacnetServicesSupportedBitString.getBitSet();

        final int desiredLength = 5;
        final byte[] byteArray = Arrays.copyOf(bitSet.toByteArray(), desiredLength);

        final String byteArrayToStringNoPrefix = Utils.byteArrayToStringNoPrefix(byteArray);

        System.out.println(byteArrayToStringNoPrefix);

        final byte[] expectedByteArray = new byte[] { (byte) 0x9F, (byte) 0xCB, (byte) 0xE8, (byte) 0x32, (byte) 0xE9 };

        assertTrue(Arrays.equals(byteArray, expectedByteArray));
    }

}
