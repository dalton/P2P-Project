package edu.ufl.cise.cnt5106c;

import edu.ufl.cise.cnt5106c.conf.CommonProperties;
import java.util.BitSet;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Properties;

/**
 *
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public class FileManager {

    private static final String partsLocation = "";
    private final int _peerId;
    private BitSet _receivedParts;
    private final Collection<FileManagerListener> _listeners = new LinkedList<>();

    FileManager (int peerId, Properties conf) {
        this (peerId, partsLocation,
                conf.getProperty (CommonProperties.FileName.toString()),
                Integer.parseInt(conf.getProperty(CommonProperties.FileSize.toString())), 
                Integer.parseInt(conf.getProperty(CommonProperties.PieceSize.toString())));
    }

    /**
     *
     * @param peerId the id of this peer
     * @param partsLocation the path of where the file parts will be stored
     * @param fileName the file being downloaded
     * @param fileSize the size of the file being downloaded
     * @param partSize the maximum size of a part
     */
    FileManager (int peerId, String partsLocation, String fileName, int fileSize, int partSize) {
        _peerId = peerId;
        final double dPartSize = partSize;
        _receivedParts = new BitSet ((int) Math.ceil (fileSize/dPartSize));
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

    public synchronized BitSet getReceivedParts () {
        return (BitSet) _receivedParts.clone();
    }

    public synchronized int getNumberOfReceivedParts() {
        return _receivedParts.cardinality();
    }

    byte[] getPiece (int partId) {
        // TODO: implement this: we can decide whether to load the file in memory,
        // or whether to read it from file each time we receive a request.
        // The firt case may be faster, but for very large files it may not be
        // suitable...  Open for discussion though, I don't think it really matters
        // for the project, so we may as well implement the simpler strategy
        // (which probably is loading the whole thing into a byte array...)
        return null;
    }

    public void registerListener (FileManagerListener listener) {
        _listeners.add (listener);
    }

    private boolean isFileCompleted() {
        final int nextClearIdx = _receivedParts.nextClearBit(0);
        return ((nextClearIdx >= _receivedParts.length()) || (nextClearIdx < 0));
    }
}
