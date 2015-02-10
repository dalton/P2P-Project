package edu.ufl.cise.cnt5106c;

/**
 *
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public class FileManager {

    BitArray _receivedParts;

    /**
     *
     * @param peerId the id of this peer
     * @param partsLocation the path of where the file parts will be stored
     * @param fileName the file being downloaded
     * @param fileSize the size of the file being downloaded
     * @param partSize the maximum size of a part
     */
    FileManager (String peerId, String partsLocation, String fileName, int fileSize, int partSize) {
        double dPartSize = partSize;
        _receivedParts = new BitArray ((int) Math.ceil(fileSize/dPartSize));
    }
   
    /**
     * 
     * @param fileName
     * @param partIdx
     * @param part
     * @return true if the added part was missing, false if it had already been
     * received
     */
    public synchronized boolean addPart (String fileName, int partIdx, byte[] part) {
        
        // TODO: write part on file, at the specified directroy
        
        return _receivedParts.setBit(partIdx);
    }

    public synchronized BitArray getReceivedParts () {
        return _receivedParts.clone();
    }
}
