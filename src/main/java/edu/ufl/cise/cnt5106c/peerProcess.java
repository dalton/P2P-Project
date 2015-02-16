package edu.ufl.cise.cnt5106c;

import edu.ufl.cise.cnt5106c.conf.CommonProperties;
import edu.ufl.cise.cnt5106c.conf.PeerInfo;
import edu.ufl.cise.cnt5106c.conf.RemotePeerInfo;
import edu.ufl.cise.cnt5106c.log.LogHelper;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public class peerProcess implements Runnable {

    private final int _peerId;
    private final String _address;
    private final int _port;
    private final boolean _hasFile;
    private final Properties _conf;
    private final FileManager _fileMgr;
    private final PeerManager _peerMgr;
    Collection<ConnectionHandler> _connHandlers = Collections.newSetFromMap(new ConcurrentHashMap<ConnectionHandler,Boolean>());

    private peerProcess(int peerId, String address, int port, boolean hasFile, Collection<RemotePeerInfo> peerInfo, Properties conf) {
        _peerId = peerId;
        _address = address;
        _port = port;
        _hasFile = hasFile;
        _conf = conf;
        _fileMgr = new FileManager (_peerId, _conf);
        _peerMgr = new PeerManager (peerInfo, _conf);
    }

    @Override
    public void run() {

        try {
            ServerSocket serverSocket = new ServerSocket (_port);
            while (true) {
                try {
                    addConnHandler (new ConnectionHandler (_peerId,
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

    private void connectToPeers(Collection<RemotePeerInfo> peersToConnectTo) {
        Iterator<RemotePeerInfo> iter = peersToConnectTo.iterator();
        while (iter.hasNext()) {
            do {
                RemotePeerInfo peer = iter.next();
                try {
                    LogHelper.getLogger().info("Connecting to: " + peer.getPeerId() + "on: " + peer.getPeerAddress()+ ":" + peer.getPort());
                    if (addConnHandler (new ConnectionHandler (peer.getPeerId(),
                            new Socket (peer.getPeerAddress(), peer.getPort()), _fileMgr, _peerMgr))) {
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

    private synchronized boolean addConnHandler (ConnectionHandler connHandler) {
        if (!_connHandlers.contains(connHandler)) {
            _connHandlers.add(connHandler);
            new Thread (connHandler).run();
        }        
        return true;
    }

    public static void main (String[] args) {
        if (args.length != 1) {
            LogHelper.getLogger().severe("the number of arguments passed to the program is " + args.length + " while it should be 1.\nUsage: java peerProcess peerId");
        }
        int peerId = Integer.parseInt (args[0]);
        String address = "localhost";
        int port = 6008;
        boolean hasFile = false;

        // Read properties
        Reader commReader = null;
        Reader peerReader = null;
        Properties commProp = null;
        PeerInfo peerInfo = new PeerInfo();
        Collection<RemotePeerInfo> peersToConnectTo = new LinkedList<>();
        try {
            commReader = new FileReader (CommonProperties.CONFIG_FILE_NAME);
            peerReader = new FileReader (PeerInfo.CONFIG_FILE_NAME);
            commProp = CommonProperties.read (commReader);
            peerInfo.read (peerReader);
            for (RemotePeerInfo peer : peerInfo.getPeerInfo()) {
                if (peerId == peer.getPeerId()) {
                    address = peer.getPeerAddress();
                    port = peer.getPort();
                    hasFile = peer.hasFile();
                }
                else {
                    LogHelper.getLogger().info("Adding peer: " + peer.getPeerId());
                    peersToConnectTo.add(peer);
                }
            }
        }
        catch (Exception ex) {
            LogHelper.getLogger().severe(ex);
            return;
        }
        finally {
            try { commReader.close(); }
            catch (Exception e) {}
            try { peerReader.close(); }
            catch (Exception e) {}
        }

        peerProcess peerProc = new peerProcess (peerId, address, port, hasFile, peerInfo.getPeerInfo(), commProp);
        Thread t = new Thread (peerProc);
        t.setName ("peerProcess-" + peerId);
        t.start();
        LogHelper.getLogger().info("STARTED: " + peerId + "on: " + address + ":" + port);

        peerProc.connectToPeers (peersToConnectTo);
    }
}
