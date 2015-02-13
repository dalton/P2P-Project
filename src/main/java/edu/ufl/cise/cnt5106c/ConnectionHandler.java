package edu.ufl.cise.cnt5106c;

import edu.ufl.cise.cnt5106c.log.LogHelper;
import edu.ufl.cise.cnt5106c.messages.Handshake;
import edu.ufl.cise.cnt5106c.messages.Message;
import edu.ufl.cise.cnt5106c.messages.Type;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 *
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public class ConnectionHandler implements Runnable {

    private final int _peerId;
    private final Socket _socket;
    private final ObjectOutputStream _out;
    private final FileManager _fileMgr;
    private final PeerManager _peerMgr;

    public ConnectionHandler (int peerId, Socket socket, FileManager fileMgr, PeerManager peerMgr) throws IOException {
        _socket = socket;
        _peerId = peerId;
        _fileMgr = fileMgr;
        _peerMgr = peerMgr;
        _out = new ObjectOutputStream (_socket.getOutputStream());
    }

    @Override
    public void run() {
        try {
            final DataInputStream bin = new DataInputStream (_socket.getInputStream());
            _out.writeObject (new Handshake (_peerId));
            Handshake handshake = Handshake.readAndCheckMessage (bin);

            // Handshake successful
            final int peerId = handshake.getPeerId();
            final MessageHandler msgHandler = new MessageHandler (peerId, _fileMgr, _peerMgr);
            Thread.currentThread().setName ("ConnHandler-" + peerId);
            sendInternal (msgHandler.handle (handshake));
            while (true) {
                try {
                    sendInternal (msgHandler.handle (receiveMessage (bin)));
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

    private Message receiveMessage (DataInputStream bin) throws Exception {
        int length = bin.readInt();
        return Message.readMessage (length - 1, Type.valueOf(bin.readByte()), bin);
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
}
