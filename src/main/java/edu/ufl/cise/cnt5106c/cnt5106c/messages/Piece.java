package edu.ufl.cise.cnt5106c.cnt5106c.messages;

/**
 *
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public class Piece extends Message {
    
    public Piece (byte[] pieceIdx, byte[] content) throws Exception {
        super (pieceIdx.length + content.length + 1, Type.PIECE, pieceIdx);
        if (pieceIdx.length > 4) {
            throw new ArrayIndexOutOfBoundsException("pieceIdx max length is 4, while "
                    + pieceIdx + "'s length is "+ pieceIdx.length);
        }
    }
}
