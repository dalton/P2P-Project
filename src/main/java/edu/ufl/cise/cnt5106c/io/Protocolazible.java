package edu.ufl.cise.cnt5106c.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Simple interface inspired by Externalizable and Serializable to support the
 * serialization of the classes representing the messages exchanged by the
 * protocol.
 *
 * Differently from Externalizable, Protocolazible does not even add to the
 * stream the identity of the class, since the protocol has its own mechanism
 * to recognize the message type.
 *
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public interface Protocolazible {

    public void read (DataInputStream in) throws IOException;
    public void write (DataOutputStream out) throws IOException;

}
