package edu.ufl.cise.cnt5106c;

import edu.ufl.cise.cnt5106c.log.LogHelper;
import edu.ufl.cise.cnt5106c.io.ProtocolazibleObjectInputStream;
import edu.ufl.cise.cnt5106c.io.ProtocolazibleObjectOutputStream;
import edu.ufl.cise.cnt5106c.log.EventLogger;
import edu.ufl.cise.cnt5106c.messages.Handshake;
import edu.ufl.cise.cnt5106c.messages.Message;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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
    private final BlockingQueue<Message> _queue = new LinkedBlockingQueue<>();

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
        new Thread () {

            private boolean _isChoked = true;

            @Override
            public void run() {
                Thread.currentThread().setName (getClass().getName() + "-" + _remotePeerId + "-sending thread");
                while (true) {
                    try {
                        Message message = _queue.take();
                        switch (message.getType()) {
                            case Choke:
                                if (!_isChoked) {
                                    _isChoked = true;
                                    sendInternal (message);
                                }
                                break;

                            case Unchoke:
                                if (_isChoked) {
                                    _isChoked = false;
                                    sendInternal (message);
                                }
                                break;

                            default:
                                sendInternal (message);
                        }
                    }
                    catch (IOException ex) {
                        LogHelper.getLogger().warning(ex);
                    } catch (InterruptedException ex) {
                    }
                }
            }
        }.start();

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

    public void send (final Message message) {
        _queue.add(message);
    }

    private synchronized void sendInternal (Message message) throws IOException {
        if (message != null) {
            _out.writeObject (message);
        }
    }
}
