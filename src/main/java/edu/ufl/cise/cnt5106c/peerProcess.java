package edu.ufl.cise.cnt5106c;

import edu.ufl.cise.cnt5106c.conf.CommonProperties;
import edu.ufl.cise.cnt5106c.conf.PeerInfo;
import edu.ufl.cise.cnt5106c.conf.RemotePeerInfo;
import edu.ufl.cise.cnt5106c.log.LogHelper;
import java.io.FileReader;
import java.io.Reader;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Properties;

/**
 *
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public class peerProcess {

    public static void main (String[] args) {
        if (args.length != 1) {
            LogHelper.getLogger().severe("the number of arguments passed to the program is " + args.length + " while it should be 1.\nUsage: java peerProcess peerId");
        }
        final int peerId = Integer.parseInt(args[0]);
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
                    // A peer connects only to the previously defined peers,
                    // therefore I can stop parsing here.
                    break;
                }
                else {
                    peersToConnectTo.add (peer);
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

        Process peerProc = new Process (peerId, address, port, hasFile, peerInfo.getPeerInfo(), commProp);
        Thread t = new Thread (peerProc);
        t.setName ("peerProcess-" + peerId);
        t.start();

        peerProc.connectToPeers (peersToConnectTo);
    }
}
