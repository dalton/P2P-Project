package edu.ufl.cise.cnt5106c;

import edu.ufl.cise.cnt5106c.log.LogHelper;
import edu.ufl.cise.cnt5106c.io.ProtocolazibleObjectInputStream;
import edu.ufl.cise.cnt5106c.io.ProtocolazibleObjectOutputStream;
import edu.ufl.cise.cnt5106c.log.EventLogger;
import edu.ufl.cise.cnt5106c.messages.Choke;
import edu.ufl.cise.cnt5106c.messages.Handshake;
import edu.ufl.cise.cnt5106c.messages.Message;
import edu.ufl.cise.cnt5106c.messages.Unchoke;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public class ConnectionHandler implements Runnable {

    private final int _localPeerId;
    private final Socket _socket;
    private final ProtocolazibleObjectOutputStream _out;
    private final FileManager _fileMgr;
    private final PeerManager _peerMgr;
    private final boolean _isConnectingPeer;
    private final int _expectedRemotePeerId;
    private int _remotePeerId;

    public ConnectionHandler (int localPeerId, Socket socket, FileManager fileMgr, PeerManager peerMgr)
            throws IOException {
        this (localPeerId, false, -1, socket, fileMgr, peerMgr);
    }

    public ConnectionHandler (int localPeerId, boolean isConnectingPeer, int expectedRemotePeerId,
            Socket socket, FileManager fileMgr, PeerManager peerMgr) throws IOException {
        _socket = socket;
        _localPeerId = localPeerId;
        _isConnectingPeer = isConnectingPeer;
        _expectedRemotePeerId = expectedRemotePeerId;
        _fileMgr = fileMgr;
        _peerMgr = peerMgr;
        _out = new ProtocolazibleObjectOutputStream (_socket.getOutputStream());
        _remotePeerId = -1;
    }

    public int getRemotePeerId(){
        return _remotePeerId;
    }

    @Override
    public void run() {
        try {
            final ProtocolazibleObjectInputStream in = new ProtocolazibleObjectInputStream (_socket.getInputStream());

            // Send handshake
            _out.writeObject (new Handshake (_localPeerId));

            // Receive and check handshake
            Handshake rcvdHandshake = (Handshake) in.readObject();
            _remotePeerId = rcvdHandshake.getPeerId();
            Thread.currentThread().setName (getClass().getName() + "-" + _remotePeerId);
            final EventLogger eventLogger = new EventLogger (_localPeerId);
            final MessageHandler msgHandler = new MessageHandler (_remotePeerId, _fileMgr, _peerMgr, eventLogger);
            if (_isConnectingPeer && (_remotePeerId != _expectedRemotePeerId)) {
                throw new Exception ("Remote peer id " + _remotePeerId + " does not match with the expected id: " + _expectedRemotePeerId);
            }

            // Handshake successful
            eventLogger.peerConnection(_remotePeerId, _isConnectingPeer);

            sendInternal(msgHandler.handle(rcvdHandshake));
            while (true) {
                try {
                    sendInternal (msgHandler.handle ((Message) in.readObject()));
                }
                catch (Exception ex) {
                    LogHelper.getLogger().warning (ex);
                    break;
                }
            }
        }
        catch (Exception ex) {
            LogHelper.getLogger().warning (ex);
        }
        finally {
            try { _socket.close(); }
            catch (Exception e) {}
        }
        LogHelper.getLogger().warning (Thread.currentThread().getName()
                    + " terminating, messages will no longer be accepted.");
    }

    @Override
    public boolean equals (Object obj) {
        if (obj instanceof ConnectionHandler) {
            return ((ConnectionHandler) obj)._remotePeerId == _remotePeerId;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + _localPeerId;
        return hash;
    }

    public void send (final Message message) throws IOException {
        // TODO: revision this... Spawing a new thread each time may not be
        // very efficient
        new Thread () {
            @Override
            public void run() {
                try {
                    sendInternal (message);
                }
                catch (IOException ex) {
                    LogHelper.getLogger().warning(ex);
                }
            }
        }.start();
    }

    private synchronized void sendInternal (Message message) throws IOException {
        if (message != null) {
            _out.writeObject (message);
        }
    }

    void choke() {
        new Thread () {
            @Override
            public void run() {
                try {
                    sendInternal (new Choke());
                }
                catch (IOException ex) {
                    LogHelper.getLogger().warning(ex);
                }
            }
        }.start();
    }

    void unchoke() {
        new Thread () {
            @Override
            public void run() {
                try {
                    sendInternal (new Unchoke());
                }
                catch (IOException ex) {
                    LogHelper.getLogger().warning(ex);
                }
            }
        }.start();
    }
}
