package de.bacnetz.factory;

public enum MessageType {

    BOOLEAN(0x01),

    UNSIGNED_INTEGER(0x02),

    SINGED_INTEGER_TWOS_COMPLEMENT_NOTATION(0x03),

    REAL(0x04),

    DOUBLE(0x05),

    OCTET_STRING(0x06),

    CHARACTER_STRING(0x07),

    BIT_STRING(0x08),

    ENUMERATED(0x09),

    DATE(0x0A),

    TIME(0x0B),

    BACNET_OBJECT_IDENTIFIER(0x0C),

    RESERVED_A(0x0D),

    RESERVED_B(0x0E),

    RESERVED_C(0x0F),

    WHO_IS(0xFF);

    private final int id;

    MessageType(final int id) {
        this.id = id;
    }

    public static MessageType fromInt(final int id) {

        switch (id) {

        case 0x01:
            return BOOLEAN;

        case 0x02:
            return UNSIGNED_INTEGER;

        case 0x03:
            return SINGED_INTEGER_TWOS_COMPLEMENT_NOTATION;

        case 0x04:
            return REAL;

        case 0x05:
            return DOUBLE;

        case 0x06:
            return OCTET_STRING;

        case 0x07:
            return CHARACTER_STRING;

        case 0x08:
            return BIT_STRING;

        case 0x09:
            return ENUMERATED;

        case 0x0A:
            return DATE;

        case 0x0B:
            return TIME;

        case 0x0C:
            return BACNET_OBJECT_IDENTIFIER;

        case 0x0D:
            return RESERVED_A;

        case 0x0E:
            return RESERVED_B;

        case 0x0F:
            return RESERVED_C;

        case 0xFF:
            return WHO_IS;

        default:
            throw new RuntimeException("Unknown id " + id);
        }
    }

    public int getValue() {
        return id;
    }

    public int getId() {
        return id;
    }

}
