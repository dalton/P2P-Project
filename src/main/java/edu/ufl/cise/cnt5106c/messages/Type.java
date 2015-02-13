package edu.ufl.cise.cnt5106c.messages;

/**
 *
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public enum Type {
    Choke ((byte) 0),
    Unchoke ((byte) 1),
    Interested ((byte) 2),
    NotInterested ((byte) 3),
    Have ((byte) 4),
    BitField ((byte) 5),
    Request ((byte) 6),
    Piece ((byte) 7);

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
