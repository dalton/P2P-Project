/*
 *                     CEN5501C Project2
 * This is the program starting remote processes.
 * This program was only tested on CISE SunOS environment.
 * If you use another environment, for example, linux environment in CISE 
 * or other environments not in CISE, it is not guaranteed to work properly.
 * It is your responsibility to adapt this program to your running environment.
 */

package edu.ufl.cise.cnt5106c.conf;

import java.util.BitSet;

public class RemotePeerInfo {
    public final String _peerId;
    public final String _peerAddress;
    public final String _peerPort;
    public final boolean _hasFile;
    public int _bytesDownloadedFrom;
    public BitSet _receivedParts;

    public RemotePeerInfo(String pId, String pAddress, String pPort, boolean hasFile) {
        _peerId = pId;
        _peerAddress = pAddress;
        _peerPort = pPort;
        _hasFile = hasFile;
        _bytesDownloadedFrom = 0;
    }

    public int getPeerId() {
        return Integer.parseInt(_peerId);
    }

    public int getPort() {
        return Integer.parseInt(_peerPort);
    }

    public String getPeerAddress() {
        return _peerAddress;
    }

    public boolean hasFile() {
        return _hasFile;
    }
}
