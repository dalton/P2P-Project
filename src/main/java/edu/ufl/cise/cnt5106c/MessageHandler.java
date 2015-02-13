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
    private final int _remotePeerId;

    MessageHandler (int remotePeerId, FileManager fileMgr, PeerManager peerMgr)
    {
        _fileMgr = fileMgr;
        _peerMgr = peerMgr;
        _remotePeerId = remotePeerId;
    }

    public Message handle (Handshake handshake) throws Exception {
        BitSet bitset = _fileMgr.getReceivedParts();
        if (!bitset.isEmpty()) {
            return (new Bitfield (bitset));
        }
        return null;
    }

    public Message handle (Message msg) throws Exception {
        switch (msg.getType()) {
            case Choke: {
                return null;
            }
            case Unchoke: {
                BitSet bitset = _peerMgr.getReceivedParts (_remotePeerId);
                bitset.andNot (_fileMgr.getReceivedParts());
                if (!bitset.isEmpty()) {
                    return new Request (RandomUtils.pickRandomSetIndexFromBitSet (bitset));
                }
                break;
            }
            case Interested: {
                _peerMgr.addInterestPeer (_remotePeerId);
                break;
            }
            case NotInterested: {
                return null;
            }
            case Have: {
                Have have = (Have) msg;
                int pieceId = have.getPartId();
                _peerMgr.haveArrived (_remotePeerId, pieceId);

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
                _peerMgr.bitfieldArrived (_remotePeerId, bitset);

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
                if (_peerMgr.canUploadToPeer (_remotePeerId)) {
                    byte[] piece = _fileMgr.getPiece (request.getPartId());
                    if (piece != null) {
                        return new Piece (request.getPartId(), piece);
                    }
                }
                break;
            }
            case Piece: {
                Piece piece = (Piece) msg;
                _fileMgr.addPart (piece.getPieceIndex(), piece.getContent());
            }
        }
        // TODO: implement this
        return null;
    }
}

