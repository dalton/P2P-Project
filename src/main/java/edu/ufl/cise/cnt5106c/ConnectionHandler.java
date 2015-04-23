package edu.ufl.cise.cnt5106c;

import edu.ufl.cise.cnt5106c.log.LogHelper;
import edu.ufl.cise.cnt5106c.io.ProtocolazibleObjectInputStream;
import edu.ufl.cise.cnt5106c.io.ProtocolazibleObjectOutputStream;
import edu.ufl.cise.cnt5106c.log.EventLogger;
import edu.ufl.cise.cnt5106c.messages.Handshake;
import edu.ufl.cise.cnt5106c.messages.Message;
import edu.ufl.cise.cnt5106c.messages.Request;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public class ConnectionHandler implements Runnable {

    private static final int PEER_ID_UNSET = -1;

    private final int _localPeerId;
    private final Socket _socket;
    private final ProtocolazibleObjectOutputStream _out;
    private final FileManager _fileMgr;
    private final PeerManager _peerMgr;
    private final boolean _isConnectingPeer;
    private final int _expectedRemotePeerId;
    private final AtomicInteger _remotePeerId;
    private final BlockingQueue<Message> _queue = new LinkedBlockingQueue<>();

    public ConnectionHandler(int localPeerId, Socket socket, FileManager fileMgr, PeerManager peerMgr)
            throws IOException {
        this(localPeerId, false, -1, socket, fileMgr, peerMgr);
    }

    public ConnectionHandler(int localPeerId, boolean isConnectingPeer, int expectedRemotePeerId,
                             Socket socket, FileManager fileMgr, PeerManager peerMgr) throws IOException {
        _socket = socket;
        _localPeerId = localPeerId;
        _isConnectingPeer = isConnectingPeer;
        _expectedRemotePeerId = expectedRemotePeerId;
        _fileMgr = fileMgr;
        _peerMgr = peerMgr;
        _out = new ProtocolazibleObjectOutputStream(_socket.getOutputStream());
        _remotePeerId = new AtomicInteger(PEER_ID_UNSET);
    }

    public int getRemotePeerId() {
        return _remotePeerId.get();
    }

    @Override
    public void run() {
        new Thread() {

            private boolean _remotePeerIsChoked = true;

            @Override
            public void run() {
                Thread.currentThread().setName(getClass().getName() + "-" + _remotePeerId + "-sending thread");
                while (true) {
                    try {
                        final Message message = _queue.take();
                        if (message == null) {
                            continue;
                        }
                        if (_remotePeerId.get() != PEER_ID_UNSET) {
                            switch (message.getType()) {
                                case Choke: {
                                    if (!_remotePeerIsChoked) {
                                        _remotePeerIsChoked = true;
                                        sendInternal(message);
                                    }
                                    break;
                                }

                                case Unchoke: {
                                    if (_remotePeerIsChoked) {
                                        _remotePeerIsChoked = false;
                                        sendInternal(message);
                                    }
                                    break;
                                }

                                default:
                                    sendInternal(message);
                            }
                        } else {
                            LogHelper.getLogger().debug("cannot send message of type "
                                    + message.getType() + " because the remote peer has not handshaked yet.");
                        }
                    } catch (IOException ex) {
                        LogHelper.getLogger().warning(ex);
                    } catch (InterruptedException ex) {
                    }
                }
            }
        }.start();

        try {
            final ProtocolazibleObjectInputStream in = new ProtocolazibleObjectInputStream(_socket.getInputStream());

            // Send handshake
            _out.writeObject(new Handshake(_localPeerId));

            // Receive and check handshake
            Handshake rcvdHandshake = (Handshake) in.readObject();
            _remotePeerId.set(rcvdHandshake.getPeerId());
            Thread.currentThread().setName(getClass().getName() + "-" + _remotePeerId.get());
            final EventLogger eventLogger = new EventLogger(_localPeerId);
            final MessageHandler msgHandler = new MessageHandler(_remotePeerId.get(), _fileMgr, _peerMgr, eventLogger);
            if (_isConnectingPeer && (_remotePeerId.get() != _expectedRemotePeerId)) {
                throw new Exception("Remote peer id " + _remotePeerId + " does not match with the expected id: " + _expectedRemotePeerId);
            }

            // Handshake successful
            eventLogger.peerConnection(_remotePeerId.get(), _isConnectingPeer);

            sendInternal(msgHandler.handle(rcvdHandshake));
            while (true) {
                try {
                    sendInternal(msgHandler.handle((Message) in.readObject()));
                } catch (Exception ex) {
                    LogHelper.getLogger().warning(ex);
                    break;
                }
            }
        } catch (Exception ex) {
            LogHelper.getLogger().warning(ex);
        } finally {
            try {
                _socket.close();
            } catch (Exception e) {
            }
        }
        LogHelper.getLogger().warning(Thread.currentThread().getName()
                + " terminating, messages will no longer be accepted.");
    }

    @Override
    public boolean equals(Object obj) {
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

    public void send(final Message message) {
        _queue.add(message);
    }

    private synchronized void sendInternal(Message message) throws IOException {
        if (message != null) {
            _out.writeObject(message);
            switch (message.getType()) {
                case Request: {
                    new java.util.Timer().schedule(
                            new RequestTimer((Request) message, _fileMgr, _out, message, _remotePeerId.get()),
                            _peerMgr.getUnchokingInterval() * 2
                    );
                }
            }
        }
    }
}
