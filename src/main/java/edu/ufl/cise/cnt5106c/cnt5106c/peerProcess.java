package edu.ufl.cise.cnt5106c.cnt5106c;

import edu.ufl.cise.cnt5106c.cnt5106c.conf.CommonProperties;
import edu.ufl.cise.cnt5106c.cnt5106c.conf.PeerInfo;
import edu.ufl.cise.cnt5106c.cnt5106c.log.LogHelper;
import java.io.FileReader;
import java.io.Reader;
import java.net.ServerSocket;
import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;

/**
 *
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public class peerProcess implements Runnable {

    private final static int PORT = 56489;
    private final int _peerId;
    private final boolean _hasFile;
    private final Properties _conf;
    Collection<ConnectionHandler> _connHandlers = new HashSet<>();

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
            ConnectionHandler connHandler = new ConnectionHandler (_peerId, serverSocket.accept());
            if (!_connHandlers.contains(connHandler)) {
                _connHandlers.add(connHandler);
                new Thread (connHandler).run();
            }
        }
        catch (Exception e) {
            LogHelper.getLogger().warning(e);
        }
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
        try {
            commReader = new FileReader (CommonProperties.CONFIG_FILE_NAME);
            peerReader = new FileReader (PeerInfo.CONFIG_FILE_NAME);
            CommonProperties.read (commReader);
            peerInfo.read (peerReader);
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

        Thread t = new Thread (new peerProcess(peerId, hasFile, commProp));
        t.setName ("peerProcess-" + peerId);
        t.start();
    }
}
