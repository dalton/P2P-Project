package edu.ufl.cise.cnt5106c.messages;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 *
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public class Have extends Message {

    public Have (int pieceIdx) {
        this (ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(pieceIdx).array());
    }

    public Have (byte[] pieceIdx) {
        super (Type.Have, pieceIdx);
    }

    public int getPartId() {
        return ByteBuffer.wrap(Arrays.copyOfRange(_payload, 0, 3)).order(ByteOrder.BIG_ENDIAN).getInt();
    }
}
