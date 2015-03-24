package edu.ufl.cise.cnt5106c.messages;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 *
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public class Piece extends Message {

    Piece (byte[] payload) {
        super (Type.Piece, payload);
    }

    public Piece (int pieceIdx, byte[] content) {
        super (Type.Piece, join (pieceIdx, content));
    }

    public int getPieceIndex() {
        return ByteBuffer.wrap(Arrays.copyOfRange(_payload, 0, 4)).order(ByteOrder.BIG_ENDIAN).getInt();
    }

    public byte[] getContent() {
        return Arrays.copyOfRange(_payload, 4, _payload.length);
    }

    private static byte[] join (int pieceIdx, byte[] second) { 
        byte[] concat = new byte[4 + (second == null ? 0 : second.length)];
        System.arraycopy(ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(pieceIdx).array(), 0, concat, 0, 4);
        System.arraycopy(second, 0, concat, 4, second.length);
        return concat;
    }
}
