package edu.ufl.cise.cnt5106c.messages;

/**
 *
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public class Request extends MessageWithPayload {

    public Request (int pieceIdx) {
        this (getPieceIndexBytes (pieceIdx));
    }

    public Request (byte[] pieceIdx) {
        super (Type.Request, pieceIdx);
    }
}
