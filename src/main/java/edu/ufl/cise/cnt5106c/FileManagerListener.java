package edu.ufl.cise.cnt5106c;

/**
 *
 *  @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public interface FileManagerListener {

    public void fileCompleted();
    public void pieceArrived (int partIdx);
}
