package edu.ufl.cise.cnt5106c;

import java.util.Collection;

/**
 *
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public interface PeerManagerListener {
    public void neighborsCompletedDownload();

    public void chockedPeers (Collection<Integer> chokedPeersIds);
    public void unchockedPeers (Collection<Integer> unchokedPeersIds);
}
