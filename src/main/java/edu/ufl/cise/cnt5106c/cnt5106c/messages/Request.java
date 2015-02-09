package edu.ufl.cise.cnt5106c.cnt5106c.messages;

/**
 *
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public class Request extends Message {

    public Request (byte[] pieceIdx) throws Exception {
        super (pieceIdx.length + 1, Type.REQUEST, pieceIdx);
        if (pieceIdx.length > 4) {
            throw new ArrayIndexOutOfBoundsException("pieceIdx max length is 4, while "
                    + pieceIdx + "'s length is "+ pieceIdx.length);
        }
    }
}
