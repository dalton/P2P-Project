package edu.ufl.cise.cnt5106c.messages;

/**
 *
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public class Request extends MessageWithPayload {

    Request (byte[] pieceIdx) {
        super (Type.Request, pieceIdx);
    }

    public Request (int pieceIdx) {
        this (getPieceIndexBytes (pieceIdx));
    }
}
