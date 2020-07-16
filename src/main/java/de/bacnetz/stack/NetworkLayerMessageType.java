package de.bacnetz.stack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 6.2.4 Network Layer Message Type
 * 
 * <ul>
 * <li />X'00': Who-Is-Router-To-Network
 * <li />X'01': I-Am-Router-To-Network
 * <li />X'02': I-Could-Be-Router-To-Network
 * <li />X'03': Reject-Message-To-Network
 * <li />X'04': Router-Busy-To-Network
 * <li />X'05': Router-Available-To-Network
 * <li />X'06': Initialize-Routing-Table
 * <li />X'07': Initialize-Routing-Table-Ack
 * <li />X'08': Establish-Connection-To-Network
 * <li />X'09': Disconnect-Connection-To-Network
 * <li />X'0A': Challenge-Request
 * <li />X'0B': Security-Payload
 * <li />X'0C': Security-Response
 * <li />X'0D': Request-Key-Update
 * <li />X'0E': Update-Key-Set
 * <li />X'0F': Update-Distribution-Key
 * <li />X'10': Request-Master-Key
 * <li />X'11': Set-Master-Key
 * <li />X'12': What-Is-Network-Number
 * <li />X'13': Network-Number-Is
 * <li />X'14' to X'7F': Reserved for use by ASHRAE
 * <li />X'80' to X'FF': Available for vendor proprietary messages
 * </ul>
 */
public enum NetworkLayerMessageType {

	WHO_IS_ROUTER_TO_NETWORK(0x00),

	I_AM_ROUTER_TO_NETWORK(0x01),

	WHAT_IS_NETWORK_NUMBER(0x12),

	UNKNOWN_NETWORK_LAYER_MESSAGE_TYPE(0xFFFFFFFF);

	private static final Logger LOG = LogManager.getLogger(NetworkLayerMessageType.class);

	private final int id;

	NetworkLayerMessageType(final int id) {
		this.id = id;
	}

	public static final int WHO_IS_ROUTER_TO_NETWORK_CODE = 0x00;

	public static final int I_AM_ROUTER_TO_NETWORK_CODE = 0x01;

	public static final int WHAT_IS_NETWORK_NUMBER_CODE = 0x12;

	public static final int UNKNOWN_NETWORK_LAYER_MESSAGE_TYPE_CODE = 0xFFFFFFFF;

	public static NetworkLayerMessageType fromInt(final int id) {

		switch (id) {

		case WHO_IS_ROUTER_TO_NETWORK_CODE:
			return WHO_IS_ROUTER_TO_NETWORK;

		case I_AM_ROUTER_TO_NETWORK_CODE:
			return I_AM_ROUTER_TO_NETWORK;

		case WHAT_IS_NETWORK_NUMBER_CODE:
			return WHAT_IS_NETWORK_NUMBER;

		default:
			LOG.warn("Unknown id " + id);
			return UNKNOWN_NETWORK_LAYER_MESSAGE_TYPE;
		}
	}

	public int getId() {
		return id;
	}

}
