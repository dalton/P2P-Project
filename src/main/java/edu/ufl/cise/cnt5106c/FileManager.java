package edu.ufl.cise.cnt5106c;

import edu.ufl.cise.cnt5106c.conf.CommonProperties;
import edu.ufl.cise.cnt5106c.file.Destination;
import edu.ufl.cise.cnt5106c.log.LogHelper;

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
    private final int _bitsetSize;
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
        _bitsetSize = (int) Math.ceil (fileSize/_dPartSize);
        LogHelper.getLogger().debug ("File size set to " + fileSize +  "\tPart size set to " + _dPartSize + "\tBitset size set to " + _bitsetSize);
        _receivedParts = new BitSet (_bitsetSize);
        _partsBeingReq = new RequestedParts (_bitsetSize, unchokingInterval);
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
            _destination.mergeFile(_receivedParts.cardinality());
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

    synchronized public boolean hasPart(int pieceIndex) {
        return _receivedParts.get(pieceIndex);
    }

    /**
     * Set all parts as received.
     */
    public synchronized void setAllParts()
    {
        for (int i = 0; i < _bitsetSize; i++) {
            _receivedParts.set(i, true);
        }
        LogHelper.getLogger().debug("Received parts set to: " + _receivedParts.toString());
    }

    public synchronized int getNumberOfReceivedParts() {
        return _receivedParts.cardinality();
    }

    byte[] getPiece (int partId) {
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

    public int getBitmapSize() {
        return _bitsetSize;
    }

    private boolean isFileCompleted() {
        for (int i = 0; i < _bitsetSize; i++) {
            if (!_receivedParts.get(i)) {
                return false;
            }
        }
        return true;
    }
}
