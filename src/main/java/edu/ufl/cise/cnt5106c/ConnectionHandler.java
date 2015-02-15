package edu.ufl.cise.cnt5106c;

import edu.ufl.cise.cnt5106c.log.LogHelper;
import edu.ufl.cise.cnt5106c.io.FlatProtocolInputStream;
import edu.ufl.cise.cnt5106c.io.FlatProtocolOutputStream;
import edu.ufl.cise.cnt5106c.messages.Handshake;
import edu.ufl.cise.cnt5106c.messages.Message;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public class ConnectionHandler implements Runnable {

    private final int _peerId;
    private final Socket _socket;
    private final FlatProtocolOutputStream _out;
    private final FileManager _fileMgr;
    private final PeerManager _peerMgr;

    public ConnectionHandler (int peerId, Socket socket, FileManager fileMgr, PeerManager peerMgr) throws IOException {
        _socket = socket;
        _peerId = peerId;
        _fileMgr = fileMgr;
        _peerMgr = peerMgr;
        _out = new FlatProtocolOutputStream (_socket.getOutputStream());
    }

    @Override
    public void run() {
        try {
            final FlatProtocolInputStream bin = new FlatProtocolInputStream (_socket.getInputStream());
            _out.writeHandshake (new Handshake (_peerId));
            Handshake handshake = bin.readHandshake();

            // Handshake successful
            final int peerId = handshake.getPeerId();
            final MessageHandler msgHandler = new MessageHandler (peerId, _fileMgr, _peerMgr);
            Thread.currentThread().setName ("ConnHandler-" + peerId);
            sendInternal (msgHandler.handle (handshake));
            while (true) {
                try {
                    sendInternal (msgHandler.handle (bin.readMessage()));
                }
                catch (Exception ex) {
                    LogHelper.getLogger().warning(ex.toString());
                }
            }
        }
        catch (Exception ex) {
            LogHelper.getLogger().warning(ex.toString());
        }
        finally {
            try { _socket.close(); }
            catch (Exception e) {}
        }
    }

    @Override
    public boolean equals (Object obj) {
        if (obj instanceof ConnectionHandler) {
            return ((ConnectionHandler) obj)._peerId == _peerId;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + this._peerId;
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
            _out.writeMessage (message);
        }
    }
}
