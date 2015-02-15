package edu.ufl.cise.cnt5106c.io;

import edu.ufl.cise.cnt5106c.messages.Handshake;
import edu.ufl.cise.cnt5106c.messages.Message;
import edu.ufl.cise.cnt5106c.messages.Type;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public class FlatProtocolInputStream extends DataInputStream {

    public FlatProtocolInputStream(InputStream in) {
        super(in);
    }
    
    public Handshake readHandshake() throws IOException {
        Handshake handshake = new Handshake();
        handshake.read(this);
        return handshake;
    }

    public Message readMessage() throws IOException, ClassNotFoundException {
        int length = readInt();
        Message message = Message.readMessage (length - 1, Type.valueOf(readByte()));
        message.read(this);
        return message;
    }
}
