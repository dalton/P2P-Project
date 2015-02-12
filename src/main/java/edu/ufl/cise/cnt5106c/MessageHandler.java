package edu.ufl.cise.cnt5106c;

import edu.ufl.cise.cnt5106c.messages.Bitfield;
import edu.ufl.cise.cnt5106c.messages.Handshake;
import edu.ufl.cise.cnt5106c.messages.Message;
import java.util.BitSet;

/**
 *
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public class MessageHandler {

    private final FileManager _fileMgr;

    MessageHandler (FileManager fileMgr)
    {
        _fileMgr = fileMgr;
    }

    public Message handle (Handshake handshake) throws Exception {
        BitSet bitset = _fileMgr.getReceivedParts();
        if (!bitset.isEmpty()) {
            return (new Bitfield (bitset));
        }
        return null;
    }

    public Message handle (Message msg) {
        switch (msg.getType()) {
            case BITFIELD: {
                Bitfield bitfield = (Bitfield) msg;
                BitSet bitset = bitfield.getBitSet();
                bitset.andNot (_fileMgr.getReceivedParts());
                if (!bitset.isEmpty()) {
                    // the peer has parts that this peer does not have
                    
                }
            }
        }
        // TODO: implement this
        return null;
    }

}
