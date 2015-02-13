package edu.ufl.cise.cnt5106c;

import edu.ufl.cise.cnt5106c.conf.CommonProperties;
import java.util.BitSet;
import java.util.Properties;

/**
 *
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public class FileManager {

    private static final String partsLocation = "";
    private BitSet _receivedParts;
    private final int _peerId;

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
        _receivedParts = new BitSet ((int) Math.ceil(fileSize/dPartSize));
    }

    /**
     * 
     * @param fileName
     * @param partIdx
     * @param part
     */
    public synchronized void addPart (String fileName, int partIdx, byte[] part) {
        
        // TODO: write part on file, at the specified directroy
        
        _receivedParts.set (partIdx);
    }

    public synchronized BitSet getReceivedParts () {
        return (BitSet) _receivedParts.clone();
    }

    byte[] getPiece(int partId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
