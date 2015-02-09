package edu.ufl.cise.cnt5106c.cnt5106c.messages;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public class Message {
    private int _length;
    private Type _type;
    protected byte[] _payload;

    Message (Type type) throws Exception {
        this (type, null);
    }

    Message (Type type, byte[] payload) throws Exception {
        _length = (payload == null ? 0 : payload.length) + 1;
        _type = type;
        _payload = payload;
    }

    private void writeObject(ObjectOutputStream oos)
        throws IOException {

        oos.write(_length);
        oos.write(_type.getValue());
        oos.write(_payload, 0, _payload.length);
    }

    private void readObject(ObjectInputStream ois)
        throws ClassNotFoundException, IOException {

        _length = ois.readInt();
        _type = Type.valueOf(ois.readByte());
        if (ois.read(_payload, 0, _length) < _length) {
            throw new IOException("payload bytes read are less than " + _length);
        }
    }
}
