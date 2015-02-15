package edu.ufl.cise.cnt5106c.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public interface FlatProtocol {

    public void read (DataInputStream in) throws IOException;
    public void write (DataOutputStream out) throws IOException;

}
