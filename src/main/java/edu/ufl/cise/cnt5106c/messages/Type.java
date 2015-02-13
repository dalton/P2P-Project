package edu.ufl.cise.cnt5106c.messages;

/**
 *
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public enum Type {
    CHOKE ((byte) 0),
    UNCHOKE ((byte) 1),
    INTERESTED ((byte) 2),
    NOT_INTERESTED ((byte) 3),
    HAVE ((byte) 4),
    BITFIELD ((byte) 5),
    REQUEST ((byte) 6),
    PIECE ((byte) 7);

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
