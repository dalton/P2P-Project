package edu.ufl.cise.cnt5106c;

import edu.ufl.cise.cnt5106c.conf.CommonProperties;
import edu.ufl.cise.cnt5106c.conf.RemotePeerInfo;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public class PeerManager implements Runnable {
    private List<RemotePeerInfo> _peers = new ArrayList<>();
    private final int _numberOfPreferredNeighbors;
    private final int _unchokingInterval;
    private final int _optimisticUnchokingInterval;
    
    PeerManager (Collection<RemotePeerInfo> peers, Properties conf) {
        _peers.addAll (peers);
        _numberOfPreferredNeighbors = Integer.parseInt(
                conf.getProperty (CommonProperties.NumberOfPreferredNeighbors.toString()));
        _unchokingInterval = Integer.parseInt(
                conf.getProperty (CommonProperties.NumberOfPreferredNeighbors.toString())) * 1000;
        _optimisticUnchokingInterval = Integer.parseInt(
                conf.getProperty (CommonProperties.NumberOfPreferredNeighbors.toString())) * 1000;
    }

    synchronized void receivedPart (int peerId, int size) {
        for (RemotePeerInfo peer : _peers) {
            if (peer.getPeerId() == peerId) {
                peer._bytesDownloadedFrom += size;
            }
        }
    }

    synchronized boolean canUploadToPeer (int iPeer) {
        // TODO: implement this
        return false;
    }

    synchronized void bitfieldArrived (int peerId, BitSet bitfield) {
        searchPeer (peerId)._receivedParts.or (bitfield);
    }

    synchronized void haveArrived (int peerId, int partId) {
        searchPeer (peerId)._receivedParts.set (partId);
    }

    synchronized BitSet getReceivedParts(int peerId) {
        return (BitSet) searchPeer (peerId)._receivedParts.clone();
    }

    synchronized private RemotePeerInfo searchPeer (int peerId) {
        for (RemotePeerInfo peer : _peers) {
            if (peer.getPeerId() == peerId) {
                return peer;
            }
        }
        throw new RuntimeException  ("Peeer " + peerId + " not found");
    }

    @Override
    public void run() {
        while (true) {
            try { Thread.sleep (_unchokingInterval); }
            catch (InterruptedException ex) {}
            synchronized (this) {
                // Sort the peers in order of preference
                Collections.sort(_peers, new Comparator() {
                    @Override
                    public int compare(Object o1, Object o2) {
                        RemotePeerInfo ri1 = (RemotePeerInfo) (o1);
                        RemotePeerInfo ri2 = (RemotePeerInfo) (o2);
                        // Sort in decreasing order
                        return (ri2._bytesDownloadedFrom - ri1._bytesDownloadedFrom);
                    }
                });
                // Reset downloaded bytes
                for (RemotePeerInfo peer : _peers) {
                    peer._bytesDownloadedFrom = 0;
                }
            }
        }
    }
}
