package edu.ufl.cise.cnt5106c;

import edu.ufl.cise.cnt5106c.conf.CommonProperties;
import edu.ufl.cise.cnt5106c.file.Destination;

import java.util.BitSet;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Properties;

/**
 *
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public class FileManager {

    private BitSet _receivedParts;
    private final Collection<FileManagerListener> _listeners = new LinkedList<>();
    private Destination _destination;
    private final double _dPartSize;
    private final RequestedParts _partsBeingReq;

    FileManager (int peerId, Properties conf) {
        this (peerId, conf.getProperty (CommonProperties.FileName.toString()),
                Integer.parseInt(conf.getProperty(CommonProperties.FileSize.toString())), 
                Integer.parseInt(conf.getProperty(CommonProperties.PieceSize.toString())),
                Integer.parseInt(conf.getProperty(CommonProperties.UnchokingInterval.toString())) * 1000);
    }

    /**
     *
     * @param peerId the id of this peer
     * @param fileName the file being downloaded
     * @param fileSize the size of the file being downloaded
     * @param partSize the maximum size of a part
     */
    FileManager (int peerId, String fileName, int fileSize, int partSize, long unchokingInterval) {
        _dPartSize = partSize;
        final int bitsetSize = (int) Math.ceil (fileSize/_dPartSize);
        _receivedParts = new BitSet (bitsetSize);
        _partsBeingReq = new RequestedParts (bitsetSize, unchokingInterval);
        _destination = new Destination(peerId, fileName);
    }

    /**
     *
     * @param partIdx
     * @param part
     */
    public synchronized void addPart (int partIdx, byte[] part) {
        
        // TODO: write part on file, at the specified directroy
        final boolean isNewPiece = !_receivedParts.get(partIdx);
        _receivedParts.set (partIdx);

        if (isNewPiece) {
            _destination.writeByteArrayAsFilePart(part, partIdx);
            for (FileManagerListener listener : _listeners) {
                listener.pieceArrived (partIdx);
            }
        }
        if (isFileCompleted()) {
            for (FileManagerListener listener : _listeners) {
                listener.fileCompleted();
            }
        }
    }

    /**
     * @param availableParts parts that are available at the remote peer
     * @return the ID of the part to request, if any, or a negative number in
     * case all the missing parts are already being requested or the file is
     * complete.
     */
    synchronized int getPartToRequest(BitSet availableParts) {
        availableParts.andNot(getReceivedParts());
        return _partsBeingReq.getPartToRequest (availableParts);
    }

    public synchronized BitSet getReceivedParts () {
        return (BitSet) _receivedParts.clone();
    }

    /**
     * Set all parts as received.
     */
    public synchronized void setAllParts()
    {
        for (int i = 0; i < _receivedParts.size(); i++) {
            _receivedParts.set(i,true);
        }
    }

    public synchronized int getNumberOfReceivedParts() {
        return _receivedParts.cardinality();
    }

    byte[] getPiece (int partId) {
        // TODO: implement this: we can decide whether to load the file in memory,
        // or whether to read it from file each time we receive a request.
        // The first case may be faster, but for very large files it may not be
        // suitable...  Open for discussion though, I don't think it really matters
        // for the project, so we may as well implement the simpler strategy
        // (which probably is loading the whole thing into a byte array...)

        // FIXME: Adam says: I'm leaning towards getting it from the file.
        // I don't think we're going to have a performance bottleneck and it
        // simplifies the design (in my opinion)

        byte[] piece = _destination.getPartAsByteArray(partId);
        return piece;
    }

    public void registerListener (FileManagerListener listener) {
        _listeners.add (listener);
    }

    public void splitFile(){
        _destination.splitFile((int) _dPartSize);
    }

    public byte[][] getAllPieces(){
        return _destination.getAllPartsAsByteArrays();
    }

    private boolean isFileCompleted() {
        final int nextClearIdx = _receivedParts.nextClearBit(0);
        return ((nextClearIdx >= _receivedParts.length()) || (nextClearIdx < 0));
    }

}
