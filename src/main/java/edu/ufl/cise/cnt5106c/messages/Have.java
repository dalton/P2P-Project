package edu.ufl.cise.cnt5106c.messages;

/**
 *
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public class Have extends MessageWithPayload {

    Have (byte[] pieceIdx) {
        super (Type.Have, pieceIdx);
    }

    public Have (int pieceIdx) {
        this (getPieceIndexBytes (pieceIdx));
    }
}
