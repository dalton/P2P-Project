package edu.ufl.cise.cnt5106c.io;

import edu.ufl.cise.cnt5106c.messages.Handshake;
import edu.ufl.cise.cnt5106c.messages.Message;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.OutputStream;

/**
 * A ProtocolazibleObjectOutputStream writes primitive data types and
 * Protocolazible objects to an OutputStream. The objects can be read
 * (reconstituted) using a ProtocolazibleObjectInputStream.
 *
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public class ProtocolazibleObjectOutputStream extends DataOutputStream implements ObjectOutput {

    public ProtocolazibleObjectOutputStream(OutputStream out) {
        super(out);
    }

    @Override
    public void writeObject (Object obj) throws IOException {
        if (obj instanceof Handshake) {
            ((Handshake) obj).write(this);
        }
        else if (obj instanceof Message) {
            ((Message) obj).write (this);
        }
        else if (obj instanceof Protocolazible) {
            throw new UnsupportedOperationException ("Message of type " + obj.getClass().getName() + " not yet supported.");
        }
        else {
            throw new UnsupportedOperationException ("Message of type " + obj.getClass().getName() + " not supported.");
        }
    }
}
