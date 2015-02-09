package edu.ufl.cise.cnt5106c.cnt5106c.messages;

/**
 *
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public class Have extends Message {

    public Have (byte[] pieceIdx) throws Exception {
        super (pieceIdx.length + 1, Type.HAVE, pieceIdx);
        if (pieceIdx.length > 4) {
            throw new ArrayIndexOutOfBoundsException("pieceIdx max length is 4, while "
                    + pieceIdx + "'s length is "+ pieceIdx.length);
        }
    }
}
