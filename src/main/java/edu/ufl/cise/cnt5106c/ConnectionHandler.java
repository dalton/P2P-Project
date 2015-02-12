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

    private final MessageHandler _msgHandler;
    private final Socket _socket;
    private final int _peerId;

    public ConnectionHandler (int peerId, Socket socket, FileManager fileMgr) throws IOException {
        _socket = socket;
        _peerId = peerId;
        _msgHandler = new MessageHandler (fileMgr);
    }

    @Override
    public void run() {
        try {
            DataInputStream bin = new DataInputStream (_socket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream (_socket.getOutputStream());
            out.writeObject (new Handshake (_peerId));
            Handshake handshake = Handshake.readMessage (bin);
            send (_msgHandler.handle (handshake), out);

            // Handshake successful
            Thread.currentThread().setName ("ConnHandler-" + handshake.getPeerId());
            while (true) {
                try {
                    send (_msgHandler.handle(receiveMessage (bin)), out);
                }
                catch (Exception ex) {
                    LogHelper.getLogger().warning(ex);
                }
            }
        }
        catch (Exception ex) {
            LogHelper.getLogger().warning(ex);
        }
        finally {
            try { _socket.close(); } catch (Exception e) {}
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

    private static void send (Message message, ObjectOutputStream out) throws IOException {
        if (message != null) {
            out.writeObject (message);
        }
    }
}
