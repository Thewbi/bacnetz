package de.bacnetz.stack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import de.bacnetz.common.Utils;
import de.bacnetz.controller.DefaultMessageController;
import de.bacnetz.controller.Message;

public class MulticastListenerReaderThreadTest {

	/**
	 * Unconfirmed request who-is with specific range defined by two service
	 * parameters
	 */
	@Test
	public void testParseBuffer() {

		final byte[] hexStringToByteArray = Utils.hexStringToByteArray("810B00120120FFFF00FF10080A1F471A1F47");

		final MulticastListenerReaderThread multicastListenerReaderThread = new MulticastListenerReaderThread();

		final Message request = multicastListenerReaderThread.parseBuffer(hexStringToByteArray,
				hexStringToByteArray.length);
		final Message response = multicastListenerReaderThread.sendMessageToController(request);
	}

	/**
	 * Unconfirmed request who-is without any ranges. No service parameters are
	 * contained.
	 */
	@Test
	public void testParseBuffer2() {

		final byte[] hexStringToByteArray = Utils.hexStringToByteArray("810B000801001008");

		final MulticastListenerReaderThread multicastListenerReaderThread = new MulticastListenerReaderThread();

		final Message request = multicastListenerReaderThread.parseBuffer(hexStringToByteArray,
				hexStringToByteArray.length);
		final Message response = multicastListenerReaderThread.sendMessageToController(request);
	}

	/**
	 * Unconfirmed request who-is without any ranges. No service parameters are
	 * contained.
	 */
	@Test
	public void testParseBuffer3() {

		final byte[] hexStringToByteArray = Utils.hexStringToByteArray("810B001401001000C4020027102201E0910021B2");

		final MulticastListenerReaderThread multicastListenerReaderThread = new MulticastListenerReaderThread();

		final Message request = multicastListenerReaderThread.parseBuffer(hexStringToByteArray,
				hexStringToByteArray.length);
		final Message response = multicastListenerReaderThread.sendMessageToController(request);
	}

	@Test
	public void testIAM() {

		final byte[] hexStringToByteArray = Utils.hexStringToByteArray("810b001401001000c4020027102201e0910021b2");

		final MulticastListenerReaderThread multicastListenerReaderThread = new MulticastListenerReaderThread();

		final Message request = multicastListenerReaderThread.parseBuffer(hexStringToByteArray,
				hexStringToByteArray.length);
		final Message response = multicastListenerReaderThread.sendMessageToController(request);
	}

	@Test
	public void testConfirmedREQ_ReadPropertyMultiple() {

		final byte[] hexStringToByteArray = Utils
				.hexStringToByteArray("810a0019010c012e030012680243990e0c020027101e09701f");

		final DefaultMessageController defaultMessageController = new DefaultMessageController();

		final MulticastListenerReaderThread multicastListenerReaderThread = new MulticastListenerReaderThread();
		multicastListenerReaderThread.getMessageControllers().add(defaultMessageController);

		final Message request = multicastListenerReaderThread.parseBuffer(hexStringToByteArray,
				hexStringToByteArray.length);
		final Message response = multicastListenerReaderThread.sendMessageToController(request);
	}

	@Test
	public void testConfirmedREQ_WhoIs() {

		final byte[] hexStringToByteArray = Utils.hexStringToByteArray("810B00120120FFFF00FF10080A1F461A1F46");

		final DefaultMessageController defaultMessageController = new DefaultMessageController();

		final MulticastListenerReaderThread multicastListenerReaderThread = new MulticastListenerReaderThread();
		multicastListenerReaderThread.getMessageControllers().add(defaultMessageController);

		final Message request = multicastListenerReaderThread.parseBuffer(hexStringToByteArray,
				hexStringToByteArray.length);
		final Message response = multicastListenerReaderThread.sendMessageToController(request);
	}

	@Test
	public void testConfirmedREQ_WhoIs2() {

		final byte[] hexStringToByteArray = Utils
				.hexStringToByteArray("810B00180128FFFF00012E03001268FE10080A27111A2711");

		final DefaultMessageController defaultMessageController = new DefaultMessageController();

		final MulticastListenerReaderThread multicastListenerReaderThread = new MulticastListenerReaderThread();
		multicastListenerReaderThread.getMessageControllers().add(defaultMessageController);

		final Message request = multicastListenerReaderThread.parseBuffer(hexStringToByteArray,
				hexStringToByteArray.length);
		final Message response = multicastListenerReaderThread.sendMessageToController(request);
	}

	@Test
	public void testConfirmedREQ_WhoIs3() {

		final byte[] hexStringToByteArray = Utils
				.hexStringToByteArray("810A0017010C012E030012680245700C0C020027111961");

		final DefaultMessageController defaultMessageController = new DefaultMessageController();

		final MulticastListenerReaderThread multicastListenerReaderThread = new MulticastListenerReaderThread();
		multicastListenerReaderThread.getMessageControllers().add(defaultMessageController);

		final Message request = multicastListenerReaderThread.parseBuffer(hexStringToByteArray,
				hexStringToByteArray.length);
		final Message response = multicastListenerReaderThread.sendMessageToController(request);
	}

	@Test
	public void testObjectList() {

		final byte[] hexStringToByteArray = Utils.hexStringToByteArray("810a0016012403e70119ff0245780c0c02000019194c");

		final DefaultMessageController defaultMessageController = new DefaultMessageController();

		final MulticastListenerReaderThread multicastListenerReaderThread = new MulticastListenerReaderThread();
		multicastListenerReaderThread.getMessageControllers().add(defaultMessageController);

		final Message request = multicastListenerReaderThread.parseBuffer(hexStringToByteArray,
				hexStringToByteArray.length);
		final Message response = multicastListenerReaderThread.sendMessageToController(request);
	}

	@Test
	public void testDeserialize_PropertyList() {

		final byte[] hexStringToByteArray = Utils.hexStringToByteArray("810a001201040275590c0c020027111a0173");

		final DefaultMessageController defaultMessageController = new DefaultMessageController();

		final MulticastListenerReaderThread multicastListenerReaderThread = new MulticastListenerReaderThread();
		multicastListenerReaderThread.getMessageControllers().add(defaultMessageController);

		final Message request = multicastListenerReaderThread.parseBuffer(hexStringToByteArray,
				hexStringToByteArray.length);
//		final Message response = multicastListenerReaderThread.sendMessageToController(request);

		assertEquals(371, request.getApdu().getPropertyIdentifier());
	}

	@Test
	public void testDeserialize_PropertyList_2() {

		final byte[] hexStringToByteArray = Utils.hexStringToByteArray("810a001201040275540c0c00011a0173");

		final DefaultMessageController defaultMessageController = new DefaultMessageController();

		final MulticastListenerReaderThread multicastListenerReaderThread = new MulticastListenerReaderThread();
		multicastListenerReaderThread.getMessageControllers().add(defaultMessageController);

		final Message request = multicastListenerReaderThread.parseBuffer(hexStringToByteArray,
				hexStringToByteArray.length);
//		final Message response = multicastListenerReaderThread.sendMessageToController(request);

		assertEquals(371, request.getApdu().getPropertyIdentifier());
	}

	@Test
	public void testDeserialize_ReadPropertyMultiple() {

		final byte[] hexStringToByteArray = Utils.hexStringToByteArray("810a001301040275530e0c00c000011e09081f");

		final DefaultMessageController defaultMessageController = new DefaultMessageController();

		final MulticastListenerReaderThread multicastListenerReaderThread = new MulticastListenerReaderThread();
		multicastListenerReaderThread.getMessageControllers().add(defaultMessageController);

		final Message request = multicastListenerReaderThread.parseBuffer(hexStringToByteArray,
				hexStringToByteArray.length);
//		final Message response = multicastListenerReaderThread.sendMessageToController(request);

		assertEquals(0, request.getApdu().getPropertyIdentifier());
		assertEquals(3, request.getApdu().getServiceParameters().size());

		// opening tag
		final ServiceParameter openingTagServiceParameter = request.getApdu().getServiceParameters().get(0);
		assertEquals(6, openingTagServiceParameter.getLengthValueType());

		// property identifier
		final ServiceParameter propertyIdentifierServiceParameter = request.getApdu().getServiceParameters().get(1);
		final byte[] expected = new byte[] { 0x08 };
		assertTrue(Arrays.equals(propertyIdentifierServiceParameter.getPayload(), expected));

		// closing tag
		final ServiceParameter closeingTagServiceParameter = request.getApdu().getServiceParameters().get(2);
		assertEquals(7, closeingTagServiceParameter.getLengthValueType());
	}

	@Test
	public void testDeserialize_ReadPropertyMultiple2() {

		final byte[] hexStringToByteArray = Utils.hexStringToByteArray("810A0012010402757A0C0C020027111A0173");

		final DefaultMessageController defaultMessageController = new DefaultMessageController();

		final MulticastListenerReaderThread multicastListenerReaderThread = new MulticastListenerReaderThread();
		multicastListenerReaderThread.getMessageControllers().add(defaultMessageController);

		final Message request = multicastListenerReaderThread.parseBuffer(hexStringToByteArray,
				hexStringToByteArray.length);
//		final Message response = multicastListenerReaderThread.sendMessageToController(request);

		assertEquals(371, request.getApdu().getPropertyIdentifier());
		assertEquals(0, request.getApdu().getServiceParameters().size());

//		// opening tag
//		final ServiceParameter openingTagServiceParameter = request.getApdu().getServiceParameters().get(0);
//		assertEquals(6, openingTagServiceParameter.getLengthValueType());
//
//		// property identifier
//		final ServiceParameter propertyIdentifierServiceParameter = request.getApdu().getServiceParameters().get(1);
//		final byte[] expected = new byte[] { 0x08 };
//		assertTrue(Arrays.equals(propertyIdentifierServiceParameter.getPayload(), expected));
//
//		// closing tag
//		final ServiceParameter closeingTagServiceParameter = request.getApdu().getServiceParameters().get(2);
//		assertEquals(7, closeingTagServiceParameter.getLengthValueType());
	}

}
