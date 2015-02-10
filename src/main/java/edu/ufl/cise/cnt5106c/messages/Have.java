package edu.ufl.cise.cnt5106c.messages;

/**
 *
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public class Have extends Message {

    public Have (byte[] pieceIdx) throws Exception {
        super (Type.HAVE, pieceIdx);
    }
}
