package de.bacnetz.stack;

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

}
