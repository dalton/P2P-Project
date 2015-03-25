package edu.ufl.cise.cnt5106c.messages;

import java.util.BitSet;

/**
 *
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public class Bitfield extends Message {

    Bitfield (byte[] bitfield) {
        super (Type.BitField, bitfield);
    }

    public Bitfield (BitSet bitset) {
        super (Type.BitField, bitset.toByteArray());
    }

    public BitSet getBitSet() {
        return BitSet.valueOf (_payload);
    }
}
