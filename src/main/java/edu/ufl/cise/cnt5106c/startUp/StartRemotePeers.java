/*
 *                     CEN5501C Project2
 * This is the program starting remote processes.
 * This program was only tested on CISE SunOS environment.
 * If you use another environment, for example, linux environment in CISE 
 * or other environments not in CISE, it is not guaranteed to work properly.
 * It is your responsibility to adapt this program to your running environment.
 */

package edu.ufl.cise.cnt5106c.startUp;

import edu.ufl.cise.cnt5106c.conf.PeerInfo;
import edu.ufl.cise.cnt5106c.conf.RemotePeerInfo;
import edu.ufl.cise.cnt5106c.log.LogHelper;
import java.io.*;
import java.text.ParseException;
import java.util.*;

/*
 * The StartRemotePeers class begins remote peer processes. 
 * It reads configuration file PeerInfo.cfg and starts remote peer processes.
 * You must modify this program a little bit if your peer processes are written in C or C++.
 * Please look at the lines below the comment saying IMPORTANT.
 */
public class StartRemotePeers {

    private static void startRemotePeer (String path, Collection<RemotePeerInfo> peers) throws IOException {
        for (RemotePeerInfo peer : peers) {
            System.out.println("Start remote peer " + peer._peerId +  " at " + peer._peerAddress);
            Runtime.getRuntime().exec ("ssh " + peer._peerAddress + " cd " + path + "; java peerProcess " + peer._peerId);
        }
        System.out.println ("Started all peers.");
    }

    public static void main(String[] args) {
        
        final String configFile = (args.length == 0 ? PeerInfo.CONFIG_FILE_NAME : args[0]);
        FileReader in = null;
        try {
            in = new FileReader (configFile);
            PeerInfo peerInfo = new PeerInfo();
            peerInfo.read (in);
            startRemotePeer (System.getProperty("user.dir"), peerInfo.getPeerInfo());
        }
        catch (IOException | ParseException ex) {
            LogHelper.getLogger().severe (ex);
        }
        finally {
            try { in.close(); }
            catch (Exception e) {}
        }
    }
}
