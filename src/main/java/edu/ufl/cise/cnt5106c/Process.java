package edu.ufl.cise.cnt5106c;

import edu.ufl.cise.cnt5106c.conf.RemotePeerInfo;
import edu.ufl.cise.cnt5106c.log.EventLogger;
import edu.ufl.cise.cnt5106c.log.LogHelper;
import edu.ufl.cise.cnt5106c.messages.Have;
import edu.ufl.cise.cnt5106c.messages.Piece;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public class Process implements Runnable, FileManagerListener, PeerManagerListener {
    private final int _peerId;
    private final String _address;
    private final int _port;
    private final boolean _hasFile;
    private final Properties _conf;
    private final FileManager _fileMgr;
    private final PeerManager _peerMgr;
    private final EventLogger _eventLogger;
    private final AtomicBoolean _fileCompleted = new AtomicBoolean(false);
    private final AtomicBoolean _peersFileCompleted = new AtomicBoolean(false);
    private final AtomicBoolean _terminate = new AtomicBoolean(false);
    private final Collection<ConnectionHandler> _connHandlers =
            Collections.newSetFromMap(new ConcurrentHashMap<ConnectionHandler, Boolean>());

    public Process(int peerId, String address, int port, boolean hasFile, Collection<RemotePeerInfo> peerInfo, Properties conf) {
        _peerId = peerId;
        _address = address;
        _port = port;
        _hasFile = hasFile;
        _conf = conf;
        _fileMgr = new FileManager(_peerId, _conf);
        ArrayList<RemotePeerInfo> remotePeers = new ArrayList<>(peerInfo);
        for (RemotePeerInfo ri : remotePeers) {
            if (Integer.parseInt(ri._peerId) == peerId) {
                remotePeers.remove(ri);
                break;
            }
        }
        _peerMgr = new PeerManager(remotePeers, _conf);
        _eventLogger = new EventLogger(peerId);
    }

    void init() {
        _fileMgr.registerListener(this);
        _peerMgr.registerListener(this);

        if (_hasFile) {
            _fileMgr.splitFile();
            _fileMgr.setAllParts();
        }

        // Start PeerMnager Thread
        Thread t = new Thread(_peerMgr);
        t.setName(_peerMgr.getClass().getName());
        t.start();
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(_port);
            while (!_terminate.get()) {
                try {
                    LogHelper.getLogger().debug(Thread.currentThread().getName() + ": Peer " + _peerId + " listening on port " + _port + ".");
                    addConnHandler(new ConnectionHandler(_peerId, serverSocket.accept(), _fileMgr, _peerMgr));

                } catch (Exception e) {
                    LogHelper.getLogger().warning(e);
                }
            }
        } catch (IOException ex) {
            LogHelper.getLogger().warning(ex);
        } finally {
            LogHelper.getLogger().warning(Thread.currentThread().getName()
                    + " terminating, TCP connections will no longer be accepted.");
        }
    }

    public void testStuff(){
        LogHelper.getLogger().info("done waiting");
        // FIXME: just checking to see if we can send files
        if (_hasFile) {
            LogHelper.getLogger().info("has file");
            byte[][] pieces = _fileMgr.getAllPieces();
            for (ConnectionHandler ch : _connHandlers) {
                LogHelper.getLogger().info("has connection handlers");
                for (int i = 0; i < pieces.length; i++) {
                    LogHelper.getLogger().info("sending part: " + i);
                    try {
                        Piece piece = new Piece(i, pieces[i]);
                        ch.send(piece);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            LogHelper.getLogger().info("Finished");
            LogHelper.getLogger().info("Connection Handler Count: " + _connHandlers.size());
            LogHelper.getLogger().info("Pieces Count: " + pieces.length);
        }
    }

    void connectToPeers(Collection<RemotePeerInfo> peersToConnectTo) {
        Iterator<RemotePeerInfo> iter = peersToConnectTo.iterator();
        while (iter.hasNext()) {
            do {
                RemotePeerInfo peer = iter.next();
                try {
                    if (addConnHandler(new ConnectionHandler(_peerId, true, peer.getPeerId(),
                            new Socket(peer._peerAddress, peer.getPort()), _fileMgr, _peerMgr))) {
                        iter.remove();
                        LogHelper.getLogger().debug(" Connecting to peer: " + peer.getPeerId()
                                + " (" + peer._peerAddress + ":" + peer.getPort() + ")");

                    }
                } catch (IOException ex) {
                    LogHelper.getLogger().warning(ex);
                }
            }
            while (iter.hasNext());

            // Keep trying until they all connect
            iter = peersToConnectTo.iterator();
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
            }
        }

    }

    @Override
    public void neighborsCompletedDownload() {
        _peersFileCompleted.set(true);
        if (_fileCompleted.get() && _peersFileCompleted.get()) {
            // The process can quit
            _terminate.set(true);
        }
    }

    @Override
    public synchronized void fileCompleted() {
        _eventLogger.fileDownloadedMessage();
        _fileCompleted.set(true);
        if (_fileCompleted.get() && _peersFileCompleted.get()) {
            // The process can quit
            _terminate.set(true);
        }
    }

    @Override
    public synchronized void pieceArrived(int partIdx) {
        for (ConnectionHandler connHanlder : _connHandlers) {
            try {
                connHanlder.send(new Have(partIdx));
            } catch (Exception ex) {
                LogHelper.getLogger().warning(ex);
            }
        }
    }

    private synchronized boolean addConnHandler(ConnectionHandler connHandler) {
        if (!_connHandlers.contains(connHandler)) {
            _connHandlers.add(connHandler);
            new Thread(connHandler).run();
            try {
                wait(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        return true;
    }
}
