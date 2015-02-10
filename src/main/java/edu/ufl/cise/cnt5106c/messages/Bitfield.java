package edu.ufl.cise.cnt5106c.messages;

import edu.ufl.cise.cnt5106c.BitArray;

/**
 *
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public class Bitfield extends Message {

    public Bitfield (BitArray bitArray) throws Exception {
        super (Type.BITFIELD, bitArray.getBytes());
    }

    public Bitfield (byte[] bitfield) throws Exception {
        super (Type.BITFIELD, bitfield);
    }

    public BitArray getBitArray() {
        return new BitArray (_payload);
    }
}
