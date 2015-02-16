package edu.ufl.cise.cnt5106c.messages;

import edu.ufl.cise.cnt5106c.io.Protocolazible;
import java.io.IOException;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 *
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public class Message implements Protocolazible  {

    private int _length;
    private final Type _type;
    protected byte[] _payload;

    Message (Type type) {
        this (type, null);
    }

    Message (Type type, byte[] payload) {
        _length = (payload == null ? 0 : payload.length) + 1;
        _type = type;
        _payload = payload;
    }

    public Type getType() {
        return _type;
    }

    @Override
    public void read (DataInputStream in) throws IOException {
        byte[] payload = null;
        if ((_length -1 )> 0) { // Subtract the length of the _type field
            if (in.read(payload, 0, _length) < _length) {
                throw new IOException("payload bytes read are less than " + _length);
            }
        }
    }

    @Override
    public void write (DataOutputStream out) throws IOException {
        out.writeInt (_length);
        out.writeByte (_type.getValue());
        if ((_payload != null) && (_payload.length > 0)) {
            out.write (_payload, 0, _payload.length);
        }
    }

    public static Message getInstance (int length, Type type) throws ClassNotFoundException, IOException {
        switch (type) {
            case Choke:
                return new Choke();

            case Unchoke:
                return new Unchoke();

            case Interested:
                return new Interested();

            case NotInterested:
                return new NotInterested();

            case Have:
                return new Have (new byte[length]);

            case BitField:
                return new Bitfield (new byte[length]);

            case Request:
                return new Request (new byte[length]);

            case Piece:
                return new Piece (new byte[length]);

            default:
                throw new ClassNotFoundException ("message type not handled: " + type.toString());
        }
    }
}
