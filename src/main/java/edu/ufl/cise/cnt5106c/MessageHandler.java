package edu.ufl.cise.cnt5106c;

import edu.ufl.cise.cnt5106c.messages.Bitfield;
import edu.ufl.cise.cnt5106c.messages.Handshake;
import edu.ufl.cise.cnt5106c.messages.Have;
import edu.ufl.cise.cnt5106c.messages.Interested;
import edu.ufl.cise.cnt5106c.messages.Message;
import edu.ufl.cise.cnt5106c.messages.NotInterested;
import edu.ufl.cise.cnt5106c.messages.Piece;
import edu.ufl.cise.cnt5106c.messages.Request;
import java.util.BitSet;

/**
 *
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public class MessageHandler {

    private final FileManager _fileMgr;
    private final PeerManager _peerMgr;

    MessageHandler (FileManager fileMgr, PeerManager peerMgr)
    {
        _fileMgr = fileMgr;
        _peerMgr = peerMgr;
    }

    public Message handle (Handshake handshake) throws Exception {
        BitSet bitset = _fileMgr.getReceivedParts();
        if (!bitset.isEmpty()) {
            return (new Bitfield (bitset));
        }
        return null;
    }

    public Message handle (int peerId, Message msg) throws Exception {
        switch (msg.getType()) {
            case Choke: {
                
            }
            case Unchoke: {
                BitSet bitset = _peerMgr.getReceivedParts (peerId);
                bitset.andNot (_fileMgr.getReceivedParts());
                if (!bitset.isEmpty()) {
                    return new Request (RandomUtils.pickRandomSetIndexFromBitSet(bitset));
                }
                break;
            }
            case Interested: {
                
            }
            case NotInterested: {
                return null;
            }
            case Have: {
                Have have = (Have) msg;
                int pieceId = have.getPartId();
                _peerMgr.haveArrived (peerId, pieceId);

                if (_fileMgr.getReceivedParts().get(pieceId)) {
                    return new NotInterested();
                }
                else {
                    return new Interested();
                }
            }
            case BitField: {
                Bitfield bitfield = (Bitfield) msg;
                BitSet bitset = bitfield.getBitSet();
                _peerMgr.bitfieldArrived (peerId, bitset);

                bitset.andNot (_fileMgr.getReceivedParts());
                if (bitset.isEmpty()) {
                    return new NotInterested();
                }
                else {
                    // the peer has parts that this peer does not have
                    return new Interested();
                }
            }
            case Request: {
                Request request = (Request) msg;
                if (_peerMgr.canUploadToPeer(peerId)) {
                    byte[] piece = _fileMgr.getPiece (request.getPartId());
                    if (piece != null) {
                        return new Piece (request.getPartId(), piece);
                    }
                }
                break;
            }
            case Piece: {
                
            }
        }
        // TODO: implement this
        return null;
    }

}

