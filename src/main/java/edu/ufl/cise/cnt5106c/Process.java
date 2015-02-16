package edu.ufl.cise.cnt5106c;

import edu.ufl.cise.cnt5106c.conf.RemotePeerInfo;
import edu.ufl.cise.cnt5106c.log.EventLogger;
import edu.ufl.cise.cnt5106c.log.LogHelper;
import edu.ufl.cise.cnt5106c.messages.Have;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public class Process implements Runnable, FileManagerListener {
    private final int _peerId;
    private final String _address;
    private final int _port;
    private final boolean _hasFile;
    private final Properties _conf;
    private final FileManager _fileMgr;
    private final PeerManager _peerMgr;
    private final EventLogger _eventLogger;
    private final AtomicBoolean _fileCompleted = new AtomicBoolean (false);
    private final AtomicBoolean _peersFileCompleted = new AtomicBoolean (false);
    private final AtomicBoolean _terminate = new AtomicBoolean (false);
    private final Collection<ConnectionHandler> _connHandlers =
            Collections.newSetFromMap(new ConcurrentHashMap<ConnectionHandler,Boolean>());

    public Process (int peerId, String address, int port, boolean hasFile, Collection<RemotePeerInfo> peerInfo, Properties conf) {
        _peerId = peerId;
        _address = address;
        _port = port;
        _hasFile = hasFile;
        _conf = conf;
        _fileMgr = new FileManager (_peerId, _conf);
        _peerMgr = new PeerManager (peerInfo, _conf);
        _eventLogger = new EventLogger (peerId);
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket (_port);
            while (!_terminate.get()) {
                try {
                    addConnHandler (new ConnectionHandler (_peerId, false,
                            serverSocket.accept(), _fileMgr, _peerMgr));
                }
                catch (Exception e) {
                    LogHelper.getLogger().warning(e.toString());
                }
            }
        }
        catch (IOException ex) {
            LogHelper.getLogger().warning (ex.toString());
        }
    }

    void connectToPeers (Collection<RemotePeerInfo> peersToConnectTo) {
        Iterator<RemotePeerInfo> iter = peersToConnectTo.iterator();
        while (iter.hasNext()) {
            do {
                RemotePeerInfo peer = iter.next();
                try {
                    if (addConnHandler (new ConnectionHandler (peer.getPeerId(), true,
                            new Socket (peer._peerAddress, peer.getPort()), _fileMgr, _peerMgr))) {
                        iter.remove();
                    }
                }
                catch (IOException ex) {
                    LogHelper.getLogger().warning(ex.toString());
                }
            }
            while (iter.hasNext());

            // Keep trying until they all connect
            iter = peersToConnectTo.iterator();
            try { Thread.sleep(5000); }
            catch (InterruptedException ex) {}
        }
    }

    @Override
    public synchronized void fileCompleted() {
        _fileCompleted.set (true);
        if (_fileCompleted.get() && _peersFileCompleted.get()) {
            // The process can quit
            _terminate.set (true);
        }
    }

    @Override
    public synchronized void pieceArrived(int partIdx) {
        for (ConnectionHandler connHanlder : _connHandlers) {
            try {
                connHanlder.send (new Have (partIdx));
            }
            catch (Exception ex) {
                LogHelper.getLogger().warning(ex);
            }
        }
    }

    private synchronized boolean addConnHandler (ConnectionHandler connHandler) {
        if (!_connHandlers.contains(connHandler)) {
            _connHandlers.add(connHandler);
            new Thread (connHandler).run();
        }        
        return true;
    }
}
