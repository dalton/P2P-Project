package edu.ufl.cise.cnt5106c.messages;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 *
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public class Piece extends Message {

    Piece (byte[] payload) throws Exception {
        super (Type.Piece, payload);
    }

    public Piece (int pieceIdx, byte[] content) throws Exception {
        super (Type.Piece, join (pieceIdx, content));
    }

    public int getPieceIndex() {
        return ByteBuffer.wrap(Arrays.copyOfRange(_payload, 0, 3)).order(ByteOrder.BIG_ENDIAN).getInt();
    }

    public byte[] getContent() {
        return Arrays.copyOfRange(_payload, 4, _payload.length-1);
    }

    private static byte[] join (int pieceIdx, byte[] second) {
        ByteArrayOutputStream bof = new ByteArrayOutputStream (4 + (second == null ? 0 : second.length));
        bof.write(pieceIdx);
        if ((second != null) && (second.length > 0)) {
            bof.write(second, 0, second.length);
        }
        return bof.toByteArray();
    }
}
