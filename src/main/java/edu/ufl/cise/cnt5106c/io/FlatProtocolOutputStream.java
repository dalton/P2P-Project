package edu.ufl.cise.cnt5106c.io;

import edu.ufl.cise.cnt5106c.messages.Handshake;
import edu.ufl.cise.cnt5106c.messages.Message;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public class FlatProtocolOutputStream extends DataOutputStream {

    public FlatProtocolOutputStream(OutputStream out) {
        super(out);
    }

    public void writeHandshake (Handshake handshake) throws IOException {
        handshake.write(this);
    }

    public void writeMessage (Message message) throws IOException {
        message.write (this);
    }
}
