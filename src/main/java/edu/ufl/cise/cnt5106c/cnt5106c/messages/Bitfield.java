package edu.ufl.cise.cnt5106c.cnt5106c.messages;

/**
 *
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public class Bitfield extends Message {
    public Bitfield (byte[] bitField) throws Exception {
        super (bitField.length + 1, Type.BITFIELD, bitField);
    }
}
