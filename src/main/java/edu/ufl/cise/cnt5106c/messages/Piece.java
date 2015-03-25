package edu.ufl.cise.cnt5106c.messages;

import java.util.Arrays;

/**
 *
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public class Piece extends MessageWithPayload {

    Piece (byte[] payload) {
        super (Type.Piece, payload);
    }

    public Piece (int pieceIdx, byte[] content) {
        super (Type.Piece, join (pieceIdx, content));
    }

    public byte[] getContent() {
        if ((_payload == null) || (_payload.length <= 4)) {
            return null;
        }
        return Arrays.copyOfRange(_payload, 4, _payload.length);
    }

    private static byte[] join (int pieceIdx, byte[] second) { 
        byte[] concat = new byte[4 + (second == null ? 0 : second.length)];
        System.arraycopy(getPieceIndexBytes (pieceIdx), 0, concat, 0, 4);
        System.arraycopy(second, 0, concat, 4, second.length);
        return concat;
    }
}
