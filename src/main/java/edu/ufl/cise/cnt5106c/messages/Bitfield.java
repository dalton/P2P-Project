package edu.ufl.cise.cnt5106c.messages;

import java.util.BitSet;

/**
 *
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public class Bitfield extends Message {

    public Bitfield (BitSet bitset) throws Exception {
        super (Type.BITFIELD, bitset.toByteArray());
    }

    public Bitfield (byte[] bitfield) throws Exception {
        super (Type.BITFIELD, bitfield);
    }

    public BitSet getBitSet() {
        return BitSet.valueOf (_payload);
    }
}
