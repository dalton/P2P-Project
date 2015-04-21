package edu.ufl.cise.cnt5106c;

import edu.ufl.cise.cnt5106c.conf.CommonProperties;
import edu.ufl.cise.cnt5106c.conf.RemotePeerInfo;
import edu.ufl.cise.cnt5106c.log.EventLogger;
import edu.ufl.cise.cnt5106c.log.LogHelper;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public class PeerManager implements Runnable {

    class OptimisticUnchoker extends Thread {
        private final int _numberOfOptimisticallyUnchokedNeighbors;
        private final int _optimisticUnchokingInterval;
        private final List<RemotePeerInfo> _chokedNeighbors = new ArrayList<>();
        final Collection<RemotePeerInfo> _optmisticallyUnchokedPeers =
                Collections.newSetFromMap(new ConcurrentHashMap<RemotePeerInfo, Boolean>());

        OptimisticUnchoker(Properties conf) {
            super("OptimisticUnchoker");
            _numberOfOptimisticallyUnchokedNeighbors = 1;
            _optimisticUnchokingInterval = Integer.parseInt(
                    conf.getProperty(CommonProperties.NumberOfPreferredNeighbors.toString())) * 1000;
        }

        synchronized void setChokedNeighbors(Collection<RemotePeerInfo> chokedNeighbors) {
            _chokedNeighbors.clear();
            _chokedNeighbors.addAll(chokedNeighbors);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(_optimisticUnchokingInterval);
                } catch (InterruptedException ex) {
                }
                synchronized (this) {
                    // Randomly shuffle the remaining neighbors, and select some to optimistically unchoke
                    if (!_chokedNeighbors.isEmpty()) {
                        Collections.shuffle(_chokedNeighbors);
                        _optmisticallyUnchokedPeers.addAll(_chokedNeighbors.subList(0,
                                Math.min(_numberOfOptimisticallyUnchokedNeighbors, _chokedNeighbors.size())));
                    }
                }

                if (_optmisticallyUnchokedPeers.size() > 0) {
                    _eventLogger.changeOfOptimisticallyUnchokedNeighbors(LogHelper.getPeerIdsAsString (_optmisticallyUnchokedPeers));
                }
                for (PeerManagerListener listener : _listeners) {
                    listener.unchockedPeers(RemotePeerInfo.toIdSet(_optmisticallyUnchokedPeers));
                }
            }
        }
    }

    private final int _numberOfPreferredNeighbors;
    private final int _unchokingInterval;
    private final int _bitmapsize;
    private final EventLogger _eventLogger;
    private final List<RemotePeerInfo> _peers = new ArrayList<>();
    private final Collection<RemotePeerInfo> _preferredPeers = new HashSet<>();
    private final OptimisticUnchoker _optUnchoker;
    private final Collection<PeerManagerListener> _listeners = new LinkedList<>();
    private final AtomicBoolean _randomlySelectPreferred = new AtomicBoolean(false);

    PeerManager(int peerId, Collection<RemotePeerInfo> peers, int bitmapsize, Properties conf) {
        _peers.addAll(peers);
        _numberOfPreferredNeighbors = Integer.parseInt(
                conf.getProperty(CommonProperties.NumberOfPreferredNeighbors.toString()));
        _unchokingInterval = Integer.parseInt(
                conf.getProperty(CommonProperties.UnchokingInterval.toString())) * 1000;
        _optUnchoker = new OptimisticUnchoker(conf);
        _bitmapsize = bitmapsize;
        _eventLogger = new EventLogger (peerId);
    }

    synchronized void addInterestPeer(int remotePeerId) {
        RemotePeerInfo peer = searchPeer(remotePeerId);
        if (peer != null) {
            peer.setInterested();
        }
    }

    synchronized void removeInterestPeer(int remotePeerId) {
        RemotePeerInfo peer = searchPeer(remotePeerId);
        if (peer != null) {
            peer.setNotIterested();
        }
    }

    synchronized List<RemotePeerInfo> getInterestedPeers() {
        ArrayList<RemotePeerInfo> interestedPeers = new ArrayList<>();
        for (RemotePeerInfo peer : _peers){
            if(peer.isInterested()){
                interestedPeers.add(peer);
            }
        }
        return interestedPeers;
    }

    synchronized boolean isInteresting(int peerId, BitSet bitset) {
        RemotePeerInfo peer  = searchPeer(peerId);
        if (peer != null) {
            BitSet pBitset = (BitSet) peer._receivedParts.clone();
            pBitset.andNot(bitset);
            return ! pBitset.isEmpty();
        }
        return false;
    }

    synchronized void receivedPart(int peerId, int size) {
        RemotePeerInfo peer  = searchPeer(peerId);
        if (peer != null) {
            peer._bytesDownloadedFrom += size;
        }
    }

    synchronized boolean canUploadToPeer(int peerId) {
        RemotePeerInfo peerInfo = new RemotePeerInfo(peerId);
        return (_preferredPeers.contains(peerInfo) ||
                _optUnchoker._optmisticallyUnchokedPeers.contains(peerInfo));
    }

    synchronized void fileCompleted() {
        _randomlySelectPreferred.set (true);
    }

    synchronized void bitfieldArrived(int peerId, BitSet bitfield) {
        RemotePeerInfo peer  = searchPeer(peerId);
        if (peer != null) {
            peer._receivedParts = bitfield;
        }
        neighborsCompletedDownload();
    }

    synchronized void haveArrived(int peerId, int partId) {
        RemotePeerInfo peer  = searchPeer(peerId);
        if (peer != null) {
            peer._receivedParts.set(partId);
        }
        neighborsCompletedDownload();
    }

    synchronized BitSet getReceivedParts(int peerId) {
        RemotePeerInfo peer  = searchPeer(peerId);
        if (peer != null) {
            return (BitSet) peer._receivedParts.clone();
        }
        return new BitSet();  // empry bit set
    }

    synchronized private RemotePeerInfo searchPeer(int peerId) {
        for (RemotePeerInfo peer : _peers) {
            if (peer.getPeerId() == peerId) {
                return peer;
            }
        }
        LogHelper.getLogger().severe("Peer " + peerId + " not found");
        return null;
    }

    synchronized private void neighborsCompletedDownload() {
        for (RemotePeerInfo peer : _peers) {
            if (peer._receivedParts.cardinality() < _bitmapsize) {
                // at least one neighbor has not completed
                LogHelper.getLogger().debug("Peer " + peer.getPeerId() + " has not completed yet");
                return;
            }
        }
        for (PeerManagerListener listener : _listeners) {
            listener.neighborsCompletedDownload();
        }
    }

    public synchronized void registerListener(PeerManagerListener listener) {
        _listeners.add(listener);
    }

    @Override
    public void run() {

        _optUnchoker.start();

        while (true) {
            try {
                Thread.sleep(_unchokingInterval);
            } catch (InterruptedException ex) {
            }
            synchronized (this) {

                List<RemotePeerInfo> interestedPeers = getInterestedPeers();

                if (_randomlySelectPreferred.get()) {
                    // Randomly shuffle the neighbors
                    LogHelper.getLogger().debug("selecting preferred peers randomly");
                    Collections.shuffle(interestedPeers);
                }
                else {
                    // Sort the peers in order of preference
                    Collections.sort(interestedPeers, new Comparator() {
                        @Override
                        public int compare(Object o1, Object o2) {
                            RemotePeerInfo ri1 = (RemotePeerInfo) (o1);
                            RemotePeerInfo ri2 = (RemotePeerInfo) (o2);
                            // Sort in decreasing order
                            return (ri2._bytesDownloadedFrom - ri1._bytesDownloadedFrom);
                        }
                    });
                }

                // Reset downloaded bytes
                for (RemotePeerInfo peer : _peers) {
                    String PREFERRED = _preferredPeers.contains(peer) ? " *" : "";
                    LogHelper.getLogger().severe("BYTES DOWNLOADED FROM  PEER " + peer._peerId + ": "
                            + peer._bytesDownloadedFrom + " (INTERESTED PEERS: "
                            + interestedPeers.size()+ ": " + LogHelper.getPeerIdsAsString (interestedPeers)
                            + ")\t" + PREFERRED);
                    peer._bytesDownloadedFrom = 0;
                }

                // Select the highest ranked neighbors as "preferred"
                _preferredPeers.clear();
                _preferredPeers.addAll(interestedPeers.subList(0, Math.min(_numberOfPreferredNeighbors, interestedPeers.size())));

                if (_preferredPeers.size() > 0) {
                    _eventLogger.changeOfPrefereedNeighbors(LogHelper.getPeerIdsAsString (_preferredPeers));
                }

                Collection<RemotePeerInfo> chokedPeers = new LinkedList<>(interestedPeers);
                chokedPeers.removeAll(_preferredPeers);

                interestedPeers.removeAll(_preferredPeers);
                for (PeerManagerListener listener : _listeners) {
                    listener.chockedPeers(RemotePeerInfo.toIdSet(chokedPeers));
                    listener.unchockedPeers(RemotePeerInfo.toIdSet(_preferredPeers));
                }
                
                // Select the remaining neighbors for choking
                if (_numberOfPreferredNeighbors >= interestedPeers.size()) {
                    _optUnchoker.setChokedNeighbors(new ArrayList<RemotePeerInfo>());
                }
                else {
                    _optUnchoker.setChokedNeighbors(interestedPeers.subList(_numberOfPreferredNeighbors, interestedPeers.size()));
                }
            }
        }
    }
}
