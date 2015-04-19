package edu.ufl.cise.cnt5106c;

import edu.ufl.cise.cnt5106c.log.LogHelper;
import edu.ufl.cise.cnt5106c.messages.Bitfield;
import edu.ufl.cise.cnt5106c.messages.Handshake;
import edu.ufl.cise.cnt5106c.messages.Have;
import edu.ufl.cise.cnt5106c.messages.Interested;
import edu.ufl.cise.cnt5106c.messages.Message;
import edu.ufl.cise.cnt5106c.messages.NotInterested;
import edu.ufl.cise.cnt5106c.messages.Piece;
import edu.ufl.cise.cnt5106c.messages.Request;
import edu.ufl.cise.cnt5106c.log.EventLogger;

import java.util.BitSet;

/**
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public class MessageHandler {

    private final FileManager _fileMgr;
    private final PeerManager _peerMgr;
    private final int _remotePeerId;
    private final EventLogger _eventLogger;

    MessageHandler(int remotePeerId, FileManager fileMgr, PeerManager peerMgr, EventLogger eventLogger) {
        _fileMgr = fileMgr;
        _peerMgr = peerMgr;
        _remotePeerId = remotePeerId;
        _eventLogger = eventLogger;
    }

    public Message handle(Handshake handshake) {
        LogHelper.getLogger().info("Hi There!  I'm getting the received parts");
        BitSet bitset = _fileMgr.getReceivedParts();
        LogHelper.getLogger().info("Hi there! I'm thinking about sending the bitfield");
        if (!bitset.isEmpty()) {
            LogHelper.getLogger().info("Hi there! Sending it!");
            return (new Bitfield(bitset));
        } else {
            LogHelper.getLogger().info("Hi there! We don't have bits");
        }
        return null;
    }

    public Message handle(Message msg) {
        switch (msg.getType()) {
            case Choke: {
                _eventLogger.chokeMessage(_remotePeerId);
                return null;
            }
            case Unchoke: {
                _eventLogger.unchokeMessage(_remotePeerId);
                BitSet bitset = _peerMgr.getReceivedParts(_remotePeerId);
                bitset.andNot(_fileMgr.getReceivedParts());
                if (!bitset.isEmpty()) {
                    return new Request(RandomUtils.pickRandomSetIndexFromBitSet(bitset));
                }
                break;
            }
            case Interested: {
                _eventLogger.interestedMessage(_remotePeerId);
                _peerMgr.addInterestPeer(_remotePeerId);
                break;
            }
            case NotInterested: {
                _eventLogger.notInterestedMessage(_remotePeerId);
                return null;
            }
            case Have: {
                Have have = (Have) msg;
                final int pieceId = have.getPieceIndex();
                _eventLogger.haveMessage(_remotePeerId, pieceId);
                _peerMgr.haveArrived(_remotePeerId, pieceId);

                if (_fileMgr.getReceivedParts().get(pieceId)) {
                    return new NotInterested();
                } else {
                    return new Interested();
                }
            }
            case BitField: {
                Bitfield bitfield = (Bitfield) msg;
                BitSet bitset = bitfield.getBitSet();
                LogHelper.getLogger().info("bitset info below");
                LogHelper.getLogger().info("bitset size: " + bitset.size());
                for (int i = 0; i < bitset.size(); i++) {
                    LogHelper.getLogger().info("bitset: " + i + " is " + bitset.get(i));
                }
                _peerMgr.bitfieldArrived(_remotePeerId, bitset);

                bitset.andNot(_fileMgr.getReceivedParts());
                if (bitset.isEmpty()) {
                    return new NotInterested();
                } else {
                    // the peer has parts that this peer does not have
                    return new Interested();
                }
            }
            case Request: {
                Request request = (Request) msg;
                if (_peerMgr.canUploadToPeer(_remotePeerId)) {
                    byte[] piece = _fileMgr.getPiece(request.getPieceIndex());
                    if (piece != null) {
                        return new Piece(request.getPieceIndex(), piece);
                    }
                }
                break;
            }
            case Piece: {
                Piece piece = (Piece) msg;
                _fileMgr.addPart(piece.getPieceIndex(), piece.getContent());
                _eventLogger.pieceDownloadedMessage(_remotePeerId, piece.getPieceIndex(), _fileMgr.getNumberOfReceivedParts());
            }
        }

        return null;
    }
}

