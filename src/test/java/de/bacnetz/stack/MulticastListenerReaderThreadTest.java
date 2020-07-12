package de.bacnetz.stack;

import org.junit.jupiter.api.Test;

import de.bacnetz.common.Utils;
import de.bacnetz.controller.DefaultMessageController;

public class MulticastListenerReaderThreadTest {

	/**
	 * Unconfirmed request who-is with specific range defined by two service
	 * parameters
	 */
	@Test
	public void testParseBuffer() {

		final byte[] hexStringToByteArray = Utils.hexStringToByteArray("810B00120120FFFF00FF10080A1F471A1F47");

		final MulticastListenerReaderThread multicastListenerReaderThread = new MulticastListenerReaderThread();
		multicastListenerReaderThread.parseBuffer(hexStringToByteArray);
	}

	/**
	 * Unconfirmed request who-is without any ranges. No service parameters are
	 * contained.
	 */
	@Test
	public void testParseBuffer2() {

		final byte[] hexStringToByteArray = Utils.hexStringToByteArray("810B000801001008");

		final MulticastListenerReaderThread multicastListenerReaderThread = new MulticastListenerReaderThread();
		multicastListenerReaderThread.parseBuffer(hexStringToByteArray);
	}

	/**
	 * Unconfirmed request who-is without any ranges. No service parameters are
	 * contained.
	 */
	@Test
	public void testParseBuffer3() {

		final byte[] hexStringToByteArray = Utils.hexStringToByteArray("810B001401001000C4020027102201E0910021B2");

		final MulticastListenerReaderThread multicastListenerReaderThread = new MulticastListenerReaderThread();
		multicastListenerReaderThread.parseBuffer(hexStringToByteArray);
	}

	@Test
	public void testIAM() {

		final byte[] hexStringToByteArray = Utils.hexStringToByteArray("810b001401001000c4020027102201e0910021b2");

		final MulticastListenerReaderThread multicastListenerReaderThread = new MulticastListenerReaderThread();
		multicastListenerReaderThread.parseBuffer(hexStringToByteArray);
	}

	@Test
	public void testConfirmedREQ_ReadPropertyMultiple() {

		final byte[] hexStringToByteArray = Utils
				.hexStringToByteArray("810a0019010c012e030012680243990e0c020027101e09701f");

		final DefaultMessageController defaultMessageController = new DefaultMessageController();

		final MulticastListenerReaderThread multicastListenerReaderThread = new MulticastListenerReaderThread();
		multicastListenerReaderThread.getMessageControllers().add(defaultMessageController);
		multicastListenerReaderThread.parseBuffer(hexStringToByteArray);
	}

}
