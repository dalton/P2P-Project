package edu.ufl.cise.cnt5106c.messages;

/**
 *
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public enum Type {
    CHOKE ((byte) 0),
    UNCHOKE ((byte) 0),
    INTERESTED ((byte) 0),
    NOT_INTERESTED ((byte) 0),
    HAVE ((byte) 0),
    BITFIELD ((byte) 0),
    REQUEST ((byte) 0),
    PIECE ((byte) 0);

    private final byte _type;
    
    Type (byte type) {
        _type = type;
    }

    public byte getValue() {
        return _type;
    }

    public static Type valueOf (byte b) {
        for (Type t : Type.values()) {
            if (t._type == b) {
                return t;
            }
        }
        throw new IllegalArgumentException();
    }
}
