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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public class peerProcess implements Runnable {

    private final static int PORT = 6008;
    private final int _peerId;
    private final boolean _hasFile;
    private final Properties _conf;
    Collection<ConnectionHandler> _connHandlers = Collections.newSetFromMap(new ConcurrentHashMap<ConnectionHandler,Boolean>());

    private peerProcess(int peerId, boolean hasFile, Properties conf) {
        _peerId = peerId;
        _hasFile = hasFile;
        _conf = conf;
    }

    @Override
    public void run() {
        
        // TODO: implement this
        try {
            ServerSocket serverSocket = new ServerSocket (PORT);
            addConnHandler (new ConnectionHandler (_peerId, serverSocket.accept()));
        }
        catch (Exception e) {
            LogHelper.getLogger().warning(e);
        }
    }

    private void connectToPeers(Collection<RemotePeerInfo> peersToConnectTo) {
        Iterator<RemotePeerInfo> iter = peersToConnectTo.iterator();
        while (iter.hasNext()) {
            do {
                RemotePeerInfo peer = iter.next();
                try {
                    if (addConnHandler (new ConnectionHandler (peer.getPeerId(), new Socket (peer._peerAddress, peer.getPort())))) {
                        iter.remove();
                    }
                }
                catch (IOException ex) {
                    LogHelper.getLogger().warning(ex);
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
        if (args.length != 2) {
            LogHelper.getLogger().severe("the number of arguments passed to the program is " + args.length + " while it should be 2.\nUsage: java peerProcess peerId hasPeer");
        }
        final int peerId = Integer.parseInt (args[0]);
        final boolean hasFile = Boolean.parseBoolean (args[1]);

        // Read properties
        Reader commReader = null;
        Reader peerReader = null;
        Properties commProp = null;
        PeerInfo peerInfo = new PeerInfo();
        Collection<RemotePeerInfo> peersToConnectTo = new LinkedList<>();
        try {
            commReader = new FileReader (CommonProperties.CONFIG_FILE_NAME);
            peerReader = new FileReader (PeerInfo.CONFIG_FILE_NAME);
            CommonProperties.read (commReader);
            peerInfo.read (peerReader);
            for (RemotePeerInfo peer : peerInfo.getPeerInfo()) {
                if (peerId == peer.getPeerId()) {
                    break;
                }
                else {
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

        peerProcess peerProc = new peerProcess(peerId, hasFile, commProp);
        Thread t = new Thread (peerProc);
        t.setName ("peerProcess-" + peerId);
        t.start();

        peerProc.connectToPeers (peersToConnectTo);
    }
}
