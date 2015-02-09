package edu.ufl.cise.cnt5106c.cnt5106c.messages;

/**
 *
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public class Request extends Message {

    public Request (byte[] pieceIdx) throws Exception {
        super (Type.REQUEST, pieceIdx);
    }
}
