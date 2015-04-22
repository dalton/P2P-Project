package edu.ufl.cise.cnt5106c.log;

/**
 *
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public class EventLogger {
    private final LogHelper _logHelper;
    private final String _msgHeader; 

    public EventLogger (int peerId) {
        this (peerId, LogHelper.getLogger());
    }

    public EventLogger (int peerId, LogHelper logHelper) {
        _msgHeader = ": Peer " + peerId;
        _logHelper = logHelper;
    }

    public void peerConnection (int peerId, boolean isConnectingPeer) {
        final String msg = getLogMsgHeader() + (isConnectingPeer ? " makes a connection to Peer %d." : " is connected from Peer %d.");
        _logHelper.info (String.format (msg, peerId));
    }

    public void changeOfPrefereedNeighbors (String preferredNeighbors) {
        final String msg = getLogMsgHeader() + " has preferred neighbors %s";
        _logHelper.info (String.format (msg, preferredNeighbors));
    }

    public void changeOfOptimisticallyUnchokedNeighbors (String preferredNeighbors) {
        final String msg = getLogMsgHeader() + " has the optimistically unchoked neighbor %s";
        _logHelper.info (String.format (msg, preferredNeighbors));
    }

    public void chokeMessage (int peerId) {
        final String msg = getLogMsgHeader() + " is choked by %d.";
        _logHelper.info (String.format (msg, peerId));
    }

    public void unchokeMessage (int peerId) {
        final String msg = getLogMsgHeader() + " is unchoked by %d.";
        _logHelper.info (String.format (msg, peerId));
    }

    public void haveMessage (int peerId, int pieceIdx) {
        final String msg = getLogMsgHeader() + " received the 'have' message from %d for the piece %d.";
        _logHelper.info (String.format (msg, peerId, pieceIdx));
    }

    public void interestedMessage (int peerId) {
        final String msg = getLogMsgHeader() + " received the 'interested' message from %d.";
        _logHelper.info (String.format (msg, peerId));
    }

    public void notInterestedMessage (int peerId) {
        final String msg = getLogMsgHeader() + " received the 'not interested' message from %d.";
        _logHelper.info (String.format (msg, peerId));
    }

    public void pieceDownloadedMessage (int peerId, int pieceIdx, int currNumberOfPieces) {
        final String msg = getLogMsgHeader() + " has downloaded the piece %d from peer %d. Now the number of pieces it has is %d.";
        _logHelper.info (String.format (msg, pieceIdx, peerId, currNumberOfPieces));
    }

    public void fileDownloadedMessage () {
        final String msg = getLogMsgHeader() + " has downloaded the complete file.";
        _logHelper.info (String.format (msg));
    }

    private String getLogMsgHeader() {
        return (String.format (_msgHeader));
    }
}


