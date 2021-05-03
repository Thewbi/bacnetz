package de.bacnetz.stack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.bacnetz.common.APIUtils;
import de.bacnetz.devices.DevicePropertyType;
import de.bacnetz.stack.exception.BACnetzException;

/**
 * Application Layer Protocol Data Unit (APDU)<br />
 * <br />
 * 
 * 20.1.2.11 Format of the BACnet-Confirmed-Request-PDU (page 617)<br />
 * <br />
 * 
 * The encoding of the payload within a APDU is given in<br />
 * <br />
 * 
 * 20.2 Encoding the Variable Part of BACnet APDUs (page 625)<br />
 * <br />
 */
public class APDU {

    public static final int SYSTEM_STATUS = 112;

    private static final Logger LOG = LogManager.getLogger(APDU.class);

    private PDUType pduType;

    private boolean segmentation;

    private boolean moreSegmentsFollow;

    /** by default allow segmentation */
//    private boolean segmentedResponseAccepted = true;
    private boolean segmentedResponseAccepted = false;

    /**
     * upper nibble = max response segments accepted <br />
     * <br />
     * lower nibble = size of maximum apdu accepted
     * 
     * <pre>
     * //        outApdu.setMaxResponseSegmentsAccepted(30);
     * outApdu.setMaxResponseSegmentsAccepted(16);
     * 
     * // binary 0000b (0d) - MinimumMessageSize (50 Octets)
     * // binary 0001b (1d) - MinimumMessageSize (128 Octets)
     * // binary 0010b (2d) - MinimumMessageSize (206 Octets)
     * // binary 0011b (3d) - MinimumMessageSize (480 Octets)
     * // binary 0100b (4d) - MinimumMessageSize (1024 Octets)
     * // binary 0101b (5d) - MinimumMessageSize (1476 Octets)
     * outApdu.setSizeOfMaximumAPDUAccepted(5);
     * </pre>
     * 
     * As a default use a max response segments accepted of 16 and maximum ADPU size
     * accepted of (1476 Octets) (= 5d)
     */
    private int segmentationControl = (16 << 4) | 5;

    private UnconfirmedServiceChoice unconfirmedServiceChoice;

    private ConfirmedServiceChoice confirmedServiceChoice;

    private final List<ServiceParameter> serviceParameters = new ArrayList<>();

    private int structureLength;

    private int propertyIdentifier;

    /**
     * "Serial number" used by the requester to associate the response with the
     * request. All segments of a segmented request must have the same invokeID. The
     * invokeID should be incremented for each new BACnet-Confirmed-Request-PDU.
     * This facilitates debugging, as it allows responses to be correlated with the
     * requests which caused them. The invokeID will increment in each example which
     * follows.
     */
    private int invokeId = -1;

    private Map<Integer, String> vendorMap = new HashMap<>();

    private int sequenceNumber = -1;

    private int proposedWindowSize = -1;

    private byte[] payload;

    public APDU() {

    }

    public APDU(final APDU other) {
        this.pduType = other.pduType;
        this.segmentation = other.segmentation;
        this.moreSegmentsFollow = other.moreSegmentsFollow;
        this.segmentedResponseAccepted = other.segmentedResponseAccepted;
        this.unconfirmedServiceChoice = other.unconfirmedServiceChoice;
        this.confirmedServiceChoice = other.confirmedServiceChoice;
        this.serviceParameters.clear();
        if (CollectionUtils.isNotEmpty(other.getServiceParameters())) {
            for (final ServiceParameter otherServiceParameter : other.getServiceParameters()) {
                this.serviceParameters.add(new ServiceParameter(otherServiceParameter));
            }
        }
        this.structureLength = other.structureLength;
        this.segmentationControl = other.segmentationControl;
        this.invokeId = other.invokeId;
        this.vendorMap = other.vendorMap;
    }

    public int getDataLength() {

        int dataLength = 0;

        // 1 byte: PDU type + PDU flags
        dataLength++;

        // 1 Byte: segmentation information
        if (segmentedResponseAccepted || pduType == PDUType.CONFIRMED_SERVICE_REQUEST_PDU) {
            dataLength++;
        }

        // invoke id
        if (invokeId >= 0) {
            dataLength++;
        }

        // 1 Byte: sequence number
        if (sequenceNumber >= 0) {
            dataLength++;
        }

        // 1 Byte: proposed window size
        if (proposedWindowSize >= 0) {
            dataLength++;
        }

        // service choice
        if ((unconfirmedServiceChoice != null) || (confirmedServiceChoice != null)) {
            dataLength++;
        }

        // service parameters
        if (CollectionUtils.isNotEmpty(serviceParameters)) {

            int i = 0;
            for (final ServiceParameter serviceParameter : serviceParameters) {

                final int tempDataLength = serviceParameter.getDataLength();
                dataLength += tempDataLength;

                LOG.trace("ServiceParameter {}) DataLength: {}", i, tempDataLength);
                i++;
            }
        }

        LOG.trace("APDU DataLength: {}", dataLength);

        return dataLength;
    }

    public byte[] getBytes() throws BACnetzException {

        final int dataLength = getDataLength();

        final byte[] result = new byte[dataLength];
        toBytes(result, 0);

        return result;
    }

    public static int log2nlz(final int bits) {
        if (bits == 0) {
            // or throw exception
            return 0;
        }
        return 31 - Integer.numberOfLeadingZeros(bits);
    }

    public void setMaxResponseSegmentsAccepted(final int max) {

        final int tempMax = log2nlz(max);

        // clear the three 'max response segments accepted' - bits
        segmentationControl &= 0x8F;

        // set the bits
        segmentationControl |= (byte) (tempMax << 4) & 0xFF;
    }

    public void setSizeOfMaximumAPDUAccepted(final int maxAPDUSizeType) {

        // clear the lower nibble
        segmentationControl &= 0xF0;

        // set the bits
        segmentationControl |= (byte) (maxAPDUSizeType) & 0xFF;
    }

    public void toBytes(final byte[] data, final int offset) throws BACnetzException {

        int index = 0;

        // 1 Byte: APDU Type and APDU Flags
        int apduTypeAndFlags = pduType.getId();
        apduTypeAndFlags <<= 4;
        apduTypeAndFlags |= segmentedResponseAccepted ? 0x02 : 0x00; // when missing -> abort:
                                                                     // segmentation-not-supported
        data[offset + index++] = (byte) (apduTypeAndFlags & 0xFF);

        // 1 Byte: segmentation information
        if (segmentedResponseAccepted || pduType == PDUType.CONFIRMED_SERVICE_REQUEST_PDU) {
            data[offset + index++] = (byte) (segmentationControl & 0xFF);
        }

        // 1 Byte: invoke ID
        if (invokeId >= 0) {
            data[offset + index++] = (byte) invokeId;
        }

        // 1 Byte: sequence number
        if (sequenceNumber >= 0) {
            data[offset + index++] = (byte) sequenceNumber;
        }

        // 1 Byte: proposed window size
        if (proposedWindowSize >= 0) {
            data[offset + index++] = (byte) proposedWindowSize;
        }

        // 1 Byte: service choice
        if (unconfirmedServiceChoice != null) {
            data[offset + index++] = (byte) unconfirmedServiceChoice.getId();
        } else if (confirmedServiceChoice != null) {
            data[offset + index++] = (byte) confirmedServiceChoice.getId();
        } else {
            throw new BACnetzException("Either unconfirmedServiceChoice or confirmedServiceChoice is required!");
        }

        // service parameters (such as ObjectIdentifierServiceParameter)
        if (CollectionUtils.isNotEmpty(serviceParameters)) {

            for (final ServiceParameter serviceParameter : serviceParameters) {

                serviceParameter.toBytes(data, offset + index);
                index += serviceParameter.getDataLength();
            }
        }
    }

    public void fromBytes(final byte[] data, final int startIndex, final int payloadLength) {

        int offset = 0;
        structureLength = 0;

        //
        // PDU type

        // bits 7-4 are the PDU type
        final int pduTypeBits = (data[startIndex + offset] & 0xF0) >> 4;
        pduType = PDUType.fromInt(pduTypeBits);

        //
        // PDU flags

        // bit 3 is the segmentation bit
        segmentation = 0 < (data[startIndex + offset] & 0x08);
//        if (segmentation) {
//            // TODO: if there is segmentation, there are special segmentation bytes present
//            // in the APDU that have to be parsed.
//            throw new RuntimeException("Not implemented yet!");
//        }

        // bit 2 is the moreSegmentsFollow bit
        moreSegmentsFollow = 0 < (data[startIndex + offset] & 0x04);
//        if (moreSegmentsFollow) {
//            throw new RuntimeException("Not implemented yet!");
//        }

        // bit 1 is the segmentedResponseAccepted bit
        segmentedResponseAccepted = 0 < (data[startIndex + offset] & 0x02);
        if (segmentedResponseAccepted) {
            LOG.info("segmentedResponseAccepted bit");
        }

        offset++;
        structureLength++;

        // Response Information
        //
        // The ReadProperty request for max-apdu-length-accepted does not set the bit 2
        // but still contains segmentation information
        if (segmentedResponseAccepted || pduType == PDUType.CONFIRMED_SERVICE_REQUEST_PDU) {

            segmentationControl = data[startIndex + offset] & 0xFF;
            offset++;
            structureLength++;

            // invoke ID
            invokeId = data[startIndex + offset] & 0xFF;
            offset++;
            structureLength++;

            // unconfirmed service choice
            final int serviceChoiceCode = data[startIndex + offset] & 0xFF;
            confirmedServiceChoice = ConfirmedServiceChoice.fromInt(serviceChoiceCode);

        } else if (pduType == PDUType.SIMPLE_ACK_PDU) {

            // invoke ID
            invokeId = data[startIndex + offset] & 0xFF;
            offset++;
            structureLength++;

            // confirmed service choice
            final int serviceChoiceCode = data[startIndex + offset] & 0xFF;
            confirmedServiceChoice = ConfirmedServiceChoice.fromInt(serviceChoiceCode);

        } else if (pduType == PDUType.COMPLEX_ACK_PDU) {

            // this branch was introduced for parsing the response message of a
            // read-property request towards a bacnet device object

            // invoke ID
            invokeId = data[startIndex + offset] & 0xFF;
            offset++;
            structureLength++;

            //
            // SEGMENTATION SPECIFIC - START
            //

            if (segmentation || moreSegmentsFollow) {

                // sequence number
                sequenceNumber = data[startIndex + offset] & 0xFF;
                offset++;
                structureLength++;

                // proposed window size
                proposedWindowSize = data[startIndex + offset] & 0xFF;
                offset++;
                structureLength++;
            }

            //
            // SEGMENTATION SPECIFIC - STOP
            //

            // service choice
            final int serviceChoiceCode = data[startIndex + offset] & 0xFF;
            confirmedServiceChoice = ConfirmedServiceChoice.fromInt(serviceChoiceCode);

        } else if (pduType == PDUType.ERROR_PDU) {

            // invokeid
            invokeId = data[startIndex + offset] & 0xFF;
            offset++;
            structureLength++;

            // service choice
            final int serviceChoiceCode = data[startIndex + offset] & 0xFF;
            confirmedServiceChoice = ConfirmedServiceChoice.fromInt(serviceChoiceCode);
            offset++;
            structureLength++;

            // read ServiceParameter ErrorClass
            final ServiceParameter errorClassServiceParameter = new ServiceParameter();
            int delta = errorClassServiceParameter.fromBytes(data, startIndex + offset);
            offset += delta;
            structureLength += delta;
            serviceParameters.add(errorClassServiceParameter);

            // read ServiceParameter ErrorCode
            final ServiceParameter errorCodeServiceParameter = new ServiceParameter();
            delta = errorCodeServiceParameter.fromBytes(data, startIndex + offset);
            offset += delta;
            structureLength += delta;
            serviceParameters.add(errorCodeServiceParameter);

            final ErrorClass errorClass = ErrorClass.fromInt(errorClassServiceParameter.getPayload()[0] & 0xFF);
            final ErrorCode errorCode = ErrorCode.fromInt(errorCodeServiceParameter.getPayload()[0] & 0xFF);

            LOG.error("Error detected! ErrorClass: " + errorClass + " ErrorCode: " + errorCode);

            // no further processing
            return;

        } else if (pduType == PDUType.SIMPLE_ACK_PDU) {

            // invokeid
            invokeId = data[startIndex + offset] & 0xFF;
            offset++;
            structureLength++;

            // sequence number
            sequenceNumber = data[startIndex + offset] & 0xFF;
            offset++;
            structureLength++;

            // proposedWindowSize
            proposedWindowSize = data[startIndex + offset] & 0xFF;
            offset++;
            structureLength++;

        } else {

            // unconfirmed service choice
            final int serviceChoiceCode = data[startIndex + offset] & 0xFF;
            unconfirmedServiceChoice = UnconfirmedServiceChoice.fromInt(serviceChoiceCode);

        }

        offset++;
        structureLength++;

        payload = Arrays.copyOfRange(data, startIndex + offset, payloadLength);

        // LOG.info(Utils.bytesToHex(payload));

//        processPayload(data, startIndex, payloadLength, offset);
    }

    public void processPayload(final byte[] data, final int startIndex, final int payloadLength, final int offset) {

        if (unconfirmedServiceChoice != null) {

            switch (unconfirmedServiceChoice) {

            case WHO_IS:
                structureLength += processWhoIs(startIndex + offset, data, payloadLength);
                break;

            case I_AM:
                structureLength += processIAm(startIndex + offset, data);
                break;

            case UNCONFIRMED_COV_NOTIFICATION:
                structureLength += processUnconfirmedCOVNotification(startIndex + offset, data, payloadLength);
                break;

            default:
                LOG.warn("Not implemented: " + unconfirmedServiceChoice.name());
            }

        }

        if (confirmedServiceChoice != null) {

            switch (confirmedServiceChoice) {

            case READ_PROPERTY:
                structureLength += readServiceParameters(startIndex + offset, data, payloadLength);
                break;

            case READ_PROPERTY_MULTIPLE:
                structureLength += processReadPropertyMultiple(startIndex + offset, data, payloadLength);
                break;

            case WRITE_PROPERTY:
                structureLength += processObjectAndPropertyIdentifier(startIndex + offset, data);
                break;

            case REINITIALIZE_DEVICE:
                structureLength += processReinitialize(startIndex + offset, data);
                break;

            case SUBSCRIBE_COV:
                structureLength += processSubscribeCOV(startIndex + offset, data, payloadLength);
                break;

            case ADD_LIST_ELEMENT:
                structureLength += processAddListElement(startIndex + offset, data);
                break;

            default:
                LOG.info("Not implemented: " + confirmedServiceChoice.name());

            }
        }
    }

    private int processAddListElement(final int offset, final byte[] data) {

        int tempOffset = offset;

        // objectIdentifier
        final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
        tempOffset += objectIdentifierServiceParameter.fromBytes(data, tempOffset);
        serviceParameters.add(objectIdentifierServiceParameter);

        // property identifier service parameter
        final ServiceParameter propertyIdentifierServiceParameter = new ServiceParameter();
        tempOffset += propertyIdentifierServiceParameter.fromBytes(data, tempOffset);
        serviceParameters.add(propertyIdentifierServiceParameter);

        // {[?] bracket open
        final ServiceParameter bracketOpenServiceParameter = new ServiceParameter();
        serviceParameters.add(bracketOpenServiceParameter);
        tempOffset += bracketOpenServiceParameter.fromBytes(data, tempOffset);
//        serviceParameters.add(bracketOpenServiceParameter);

        ServiceParameter serviceParameter = null;
        while (true) {

            // {[?] bracket open
            final ServiceParameter tempBracketOpenServiceParameter = new ServiceParameter();
            serviceParameters.add(tempBracketOpenServiceParameter);
            tempOffset += tempBracketOpenServiceParameter.fromBytes(data, tempOffset);
//            serviceParameters.add(tempBracketOpenServiceParameter);

            // if the outer closing bracket was read, abort
            if (APIUtils.isClosingServiceParameter(tempBracketOpenServiceParameter)) {
                break;
            }

            // network number
            serviceParameter = new ServiceParameter();
            serviceParameters.add(serviceParameter);
            tempOffset += serviceParameter.fromBytes(data, tempOffset);
//            serviceParameters.add(serviceParameter);

            // mac address
            serviceParameter = new ServiceParameter();
            serviceParameters.add(serviceParameter);
            tempOffset += serviceParameter.fromBytes(data, tempOffset);
//            serviceParameters.add(serviceParameter);

            // }[?] bracket close
            final ServiceParameter tempBracketCloseServiceParameter = new ServiceParameter();
            serviceParameters.add(tempBracketCloseServiceParameter);
            tempOffset += tempBracketCloseServiceParameter.fromBytes(data, tempOffset);
//            serviceParameters.add(tempBracketCloseServiceParameter);
        }

//        // }[?] bracket close
//        final ServiceParameter tempBracketCloseServiceParameter = new ServiceParameter();
//        getServiceParameters().add(tempBracketCloseServiceParameter);
//        tempOffset += tempBracketCloseServiceParameter.fromBytes(data, offset + tempOffset);

        return tempOffset - offset;
    }

    /**
     * Read in data from the incoming byte array into the APDU. The APDU is later
     * put into a message object.<br />
     * <br />
     * 
     * At this point the APDU structure has been parsed up to the Service Choice.
     * 
     * @param offset
     * @param data
     * 
     * @return
     */
    private int processSubscribeCOV(final int offset, final byte[] data, final int payloadLength) {

        if (payloadLength == offset) {
            return offset;
        }

        int tempOffset = offset;

        // subscriber process id - the correlation id used inside the client that wants
        // to subscribe for COV
        final ServiceParameter subscriberProcessIdServiceParameter = new ServiceParameter();
        tempOffset += subscriberProcessIdServiceParameter.fromBytes(data, tempOffset);
        serviceParameters.add(subscriberProcessIdServiceParameter);

        // objectIdentifier - which object to install a change-of-value (COV) listener
        // for
        final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
        tempOffset += objectIdentifierServiceParameter.fromBytes(data, tempOffset);
        serviceParameters.add(objectIdentifierServiceParameter);

        // issue confirmed notifications - whether or not to send COV updates as
        // confirmed or unconfirmed messages
        final ServiceParameter issueConfirmedNotificationsServiceParameter = new ServiceParameter();
        tempOffset += issueConfirmedNotificationsServiceParameter.fromBytes(data, tempOffset);
        serviceParameters.add(issueConfirmedNotificationsServiceParameter);

        // life-time of this subscription
        final ServiceParameter lifetimeServiceParameter = new ServiceParameter();
        tempOffset += lifetimeServiceParameter.fromBytes(data, tempOffset);
        serviceParameters.add(lifetimeServiceParameter);

        return tempOffset - offset;
    }

    private int processReinitialize(final int offset, final byte[] data) {

        int tempOffset = offset;

        // payload contains type of requested initialization (1 = warmstart)
        final ServiceParameter reinitializeStateOfDeviceServiceParameter = new ServiceParameter();
        tempOffset += reinitializeStateOfDeviceServiceParameter.fromBytes(data, tempOffset);
        serviceParameters.add(reinitializeStateOfDeviceServiceParameter);

        // payload contains the password
        final ServiceParameter passwordServiceParameter = new ServiceParameter();
        tempOffset += passwordServiceParameter.fromBytes(data, tempOffset);
        serviceParameters.add(passwordServiceParameter);

        return tempOffset - offset;
    }

    private int readServiceParameters(final int offset, final byte[] data, final int payloadLength) {

//        // DEBUG
//        LOG.trace("Offset: {}", offset);
//        LOG.trace("PayloadLength: {}", payloadLength);
//        LOG.trace("data: {}", Utils.byteArrayToStringNoPrefix(data));

        int tempOffset = offset;

        try {

            // bacnet object identifier
            final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
            tempOffset += objectIdentifierServiceParameter.fromBytes(data, tempOffset);
            serviceParameters.add(objectIdentifierServiceParameter);

            // property identifier service parameter
            final ServiceParameter propertyIdentifierServiceParameter = new ServiceParameter();
            tempOffset += propertyIdentifierServiceParameter.fromBytes(data, tempOffset);
            serviceParameters.add(propertyIdentifierServiceParameter);

            // extract property identifier and store it into the dedicated member variable
            final int contextLength = propertyIdentifierServiceParameter.getLengthValueType();
            propertyIdentifier = 0;
            for (int i = 0; i < contextLength; i++) {
                propertyIdentifier <<= 8;
                propertyIdentifier += propertyIdentifierServiceParameter.getPayload()[i] & 0xFF;
            }

        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
        }

        // rest of the service parameters
        while (tempOffset < payloadLength) {

            final ServiceParameter serviceParameter = new ServiceParameter();
            tempOffset += serviceParameter.fromBytes(data, tempOffset);

            serviceParameters.add(serviceParameter);
        }

        return tempOffset - offset;
    }

    /**
     * Read in data from the incoming byte array into the APDU. The APDU is later
     * put into a message object.<br />
     * <br />
     * 
     * At this point the APDU structure has been parsed up to the Service
     * Choice.<br />
     * <br />
     * 
     * TODO: this should be put into a converter.
     * 
     * @param offset
     * @param data
     * @return
     */
    private int processObjectAndPropertyIdentifier(final int offset, final byte[] data) {

        int index = 0;

        // bacnet object identifier
        final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
        index += objectIdentifierServiceParameter.fromBytes(data, offset + index);
        serviceParameters.add(objectIdentifierServiceParameter);

        final int context = data[offset + index++];
        final int contextLength = context & 7;

        // property identifier
        propertyIdentifier = 0;
        for (int i = 0; i < contextLength; i++) {
            propertyIdentifier <<= 8;
            propertyIdentifier += data[offset + index + i] & 0xFF;
        }

        return index;
    }

    /**
     * Read in data from the incoming byte array into the APDU. The APDU is later
     * put into a message object.<br />
     * <br />
     * 
     * At this point the APDU structure has been parse up to the Service
     * Choice.<br />
     * <br />
     * 
     * TODO: this should be put into a converter.<br />
     * <br />
     * 
     * <pre>
     * ReadPropertyMultiple-Request ::= SEQUENCE {
     *   listOfReadAccessSpecs SEQUENCE OF ReadAccessSpecification
     * }
     * 
     * ReadAccessSpecification ::= SEQUENCE {
     *   objectIdentifier [0] BACnetObjectIdentifier,
     *   listOfPropertyReferences [1] SEQUENCE OF BACnetPropertyReference
     * }
     * 
     * BACnetPropertyReference ::= SEQUENCE {
     *   propertyIdentifier [0] BACnetPropertyIdentifier,
     *   propertyArrayIndex [1] Unsigned OPTIONAL --used only with array datatype
     *   -- if omitted with an array the entire array is referenced
     * }
     * 
     * BACnetPropertyIdentifier is ???
     * </pre>
     * 
     * @param offset
     * @param data
     * 
     * @return the length of the parsed structure
     */
    private int processReadPropertyMultiple(final int offset, final byte[] data, final int payloadLength) {

        int index = 0;

        // this entire thing can be contained several times! See image
        // ReadPropertyMultiple_ExtendedRequest.PNG
        // and bacnet_active_cov_subscriptions_real_answer.pcapng, message 2694

        while ((offset + index) < payloadLength) {

            // bacnet object identifier
            final ObjectIdentifierServiceParameter objectIdentifierServiceParameter = new ObjectIdentifierServiceParameter();
            index += objectIdentifierServiceParameter.fromBytes(data, offset + index);
            serviceParameters.add(objectIdentifierServiceParameter);

            // {[1] bracket open
            final ServiceParameter bracketOpenServiceParameter = new ServiceParameter();
            getServiceParameters().add(bracketOpenServiceParameter);
            index += bracketOpenServiceParameter.fromBytes(data, offset + index);

            ServiceParameter serviceParameter = null;
            do {

                // read all requested device property identifiers
                serviceParameter = new ServiceParameter();
                getServiceParameters().add(serviceParameter);
                index += serviceParameter.fromBytes(data, offset + index);

            } while (!APIUtils.isClosingServiceParameter(serviceParameter, 1));
        }

        return index;
    }

    /**
     * Read in data from the incoming byte array into the APDU. The APDU is later
     * put into a message object.
     * 
     * At this point the APDU structure has been parse up to the Service Choice.
     * 
     * TODO: this should be put into a converter.
     * 
     * ANSI/ASHRAE Standard 135-2012 (page 651)
     * 
     * The grammar defines the I-Am Request as a sequence of four Service
     * Parameters:
     * 
     * <pre>
     * ******************* Unconfirmed Remote Device Management Services ********************
     *     I-Am-Request ::= SEQUENCE {
     *     iAmDeviceIdentifier BACnetObjectIdentifier,
     *     maxAPDULengthAccepted Unsigned,
     *     segmentationSupported BACnetSegmentation,
     *     vendorID Unsigned16
     * }
     * </pre>
     * 
     * @param offset
     * @param data
     * 
     * @return
     */
    private int processIAm(int offset, final byte[] data) {

        final int structureLength = 0;
        ServiceParameter serviceParameter = null;

        // iAmDeviceIdentifier
        serviceParameter = new ServiceParameter();
        offset += serviceParameter.fromBytes(data, offset);
        serviceParameters.add(serviceParameter);

        // maxAPDULengthAccepted
        serviceParameter = new ServiceParameter();
        offset += serviceParameter.fromBytes(data, offset);
        serviceParameters.add(serviceParameter);

//		// DEBUG
//		LOG.trace("maxAPDULengthAccepted: "
//				+ Utils.bytesToUnsignedShort(serviceParameter.getPayload()[0], serviceParameter.getPayload()[1], true));

        // segmentationSupported
        serviceParameter = new ServiceParameter();
        offset += serviceParameter.fromBytes(data, offset);
        serviceParameters.add(serviceParameter);

        // vendorID
        serviceParameter = new ServiceParameter();
        offset += serviceParameter.fromBytes(data, offset);
        serviceParameters.add(serviceParameter);

        if (ArrayUtils.isNotEmpty(serviceParameter.getPayload())) {
            final int vendorId = (serviceParameter.getPayload()[0] & 0xFF);

            // DEBUG
            if (MapUtils.isNotEmpty(vendorMap) && vendorMap.containsKey(vendorId)) {
                LOG.trace("VendorId: " + vendorMap.get(vendorId));
            }
        }

        return structureLength;
    }

    /**
     * Read in data from the incoming byte array into the APDU. The APDU is later
     * put into a message object.
     *
     * At this point the APDU structure has been parse up to the Service Choice.
     * 
     * TODO: this should be put into a converter.
     * 
     * @param offset
     * @param data
     * @param payloadLength
     * @return
     */
    private int processWhoIs(int offset, final byte[] data, final int payloadLength) {

        int structureLength = 0;

        if (offset < payloadLength) {

            // parse optional service parameters
            boolean parameterParsingDone = false;
            while (!parameterParsingDone) {

                final ServiceParameter serviceParameter = new ServiceParameter();

                // parse service parameters until the list ends
                final int bytesProcessed = serviceParameter.fromBytes(data, offset);
                structureLength += bytesProcessed;
                if (0 == bytesProcessed) {
                    parameterParsingDone = true;
                    break;
                }

                serviceParameters.add(serviceParameter);

                offset += bytesProcessed;
                if (offset >= payloadLength) {
                    parameterParsingDone = true;
                    break;
                }
            }
        }

        return structureLength;
    }

    private int processUnconfirmedCOVNotification(int offset, final byte[] data, final int payloadLength) {
        int structureLength = 0;

        if (offset < payloadLength) {

            // parse optional service parameters
            boolean parameterParsingDone = false;
            while (!parameterParsingDone) {

                final ServiceParameter serviceParameter = new ServiceParameter();

                // parse service parameters until the list ends
                final int bytesProcessed = serviceParameter.fromBytes(data, offset);
                structureLength += bytesProcessed;
                if (0 == bytesProcessed) {
                    parameterParsingDone = true;
                    break;
                }

                serviceParameters.add(serviceParameter);

                offset += bytesProcessed;
                if (offset >= payloadLength) {
                    parameterParsingDone = true;
                    break;
                }
            }
        }

        return structureLength;
    }

    @Override
    public String toString() {

        final StringBuffer stringBuffer = new StringBuffer();

        stringBuffer.append("PDU Type: ").append(pduType.toString()).append("\n");

        if (confirmedServiceChoice != null) {
            stringBuffer.append(confirmedServiceChoice);
            stringBuffer.append("ConfirmedServiceChoice: ").append(confirmedServiceChoice).append("\n");
        }
        if (unconfirmedServiceChoice != null) {
            stringBuffer.append("UnconfirmedServiceChoice: ").append(unconfirmedServiceChoice).append("\n");
        }

        if (propertyIdentifier >= 0) {
            stringBuffer.append("Property: ").append(DevicePropertyType.getByCode(propertyIdentifier)).append(" (")
                    .append(propertyIdentifier).append(")\n");
        }

        for (final ServiceParameter serviceParameter : serviceParameters) {
            stringBuffer.append(serviceParameter.toString()).append("\n");
        }

        return stringBuffer.toString();
    }

    public PDUType getPduType() {
        return pduType;
    }

    public void setPduType(final PDUType pduType) {
        this.pduType = pduType;
    }

    public boolean isSegmentation() {
        return segmentation;
    }

    public void setSegmentation(final boolean segmentation) {
        this.segmentation = segmentation;
    }

    public boolean isMoreSegmentsFollow() {
        return moreSegmentsFollow;
    }

    public void setMoreSegmentsFollow(final boolean moreSegmentsFollow) {
        this.moreSegmentsFollow = moreSegmentsFollow;
    }

    public boolean isSegmentedResponseAccepted() {
        return segmentedResponseAccepted;
    }

    public void setSegmentedResponseAccepted(final boolean segmentedResponseAccepted) {
        this.segmentedResponseAccepted = segmentedResponseAccepted;
    }

    public List<ServiceParameter> getServiceParameters() {
        return serviceParameters;
    }

    public int getStructureLength() {
        return structureLength;
    }

    public Map<Integer, String> getVendorMap() {
        return vendorMap;
    }

    public void setVendorMap(final Map<Integer, String> vendorMap) {
        this.vendorMap = vendorMap;
    }

    public ObjectIdentifierServiceParameter getFirstObjectIdentifierServiceParameter() {

        final Optional<ServiceParameter> findFirstOptional = serviceParameters.stream()
                .filter(sp -> sp instanceof ObjectIdentifierServiceParameter).findFirst();

        if (findFirstOptional.isPresent()) {
            return (ObjectIdentifierServiceParameter) findFirstOptional.get();
        }

        return null;
    }

    public int getInvokeId() {
        return invokeId;
    }

    public void setInvokeId(final int invokeId) {
        this.invokeId = invokeId;
    }

    public int getPropertyIdentifier() {
        return propertyIdentifier;
    }

    public void setPropertyIdentifier(final int propertyIdentifier) {
        this.propertyIdentifier = propertyIdentifier;
    }

    public UnconfirmedServiceChoice getUnconfirmedServiceChoice() {
        return unconfirmedServiceChoice;
    }

    public void setUnconfirmedServiceChoice(final UnconfirmedServiceChoice unconfirmedServiceChoice) {
        this.unconfirmedServiceChoice = unconfirmedServiceChoice;
    }

    public ConfirmedServiceChoice getConfirmedServiceChoice() {
        return confirmedServiceChoice;
    }

    public void setConfirmedServiceChoice(final ConfirmedServiceChoice confirmedServiceChoice) {
        this.confirmedServiceChoice = confirmedServiceChoice;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(final int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public int getProposedWindowSize() {
        return proposedWindowSize;
    }

    public void setProposedWindowSize(final int proposedWindowSize) {
        this.proposedWindowSize = proposedWindowSize;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(final byte[] payload) {
        this.payload = payload;
    }

}
