package edu.ufl.cise.cnt5106c.cnt5106c;

import edu.ufl.cise.cnt5106c.cnt5106c.log.LogHelper;
import edu.ufl.cise.cnt5106c.cnt5106c.messages.Handshake;
import edu.ufl.cise.cnt5106c.cnt5106c.messages.Message;
import edu.ufl.cise.cnt5106c.cnt5106c.messages.Type;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 *
 * @author Giacomo
 */
public class ConnectionHandler implements Runnable {
    private final InputStream _in;
    private final OutputStream _out;
    private final MessageHandler _msgHandler = new MessageHandler();
    private Socket _socket;
    private int _peerId;

    public ConnectionHandler (int peerId, Socket socket) throws IOException {
        this (socket.getInputStream(), socket.getOutputStream());
        _socket = socket;
        _peerId = peerId;
    }

    private ConnectionHandler (InputStream in, OutputStream out) {
        _in = in;
        _out = out;
    }

    @Override
    public void run() {
        try {
            DataInputStream bin = new DataInputStream(_socket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(_socket.getOutputStream());
            out.writeObject (new Handshake (_peerId));
            if (check (Handshake.readMessage (bin))) {
                // Handshake successful
                while (true) {
                    try {
                        _msgHandler.handle(receiveMessage (bin));
                    }
                    catch (Exception ex) {
                        LogHelper.getLogger().warning(ex);
                    }
                }
            }
            else {
                _socket.close();
            }
        }
        catch (Exception ex) {
            LogHelper.getLogger().warning(ex);
            return;
        }
    }

    private boolean check(Handshake handshake) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private Message receiveMessage(DataInputStream bin) throws Exception {
        int length = bin.readInt();
        return Message.readMessage (length - 1, Type.valueOf(bin.readByte()), bin);
    }
}
