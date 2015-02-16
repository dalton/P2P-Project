package edu.ufl.cise.cnt5106c.messages;

import edu.ufl.cise.cnt5106c.log.LogHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ProtocolException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 *
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public class Handshake implements Serializable {
    private final static String _protocolId = "P2PFILESHARINGPROJ";
    private final byte[] _zeroBits = new byte[10];
    private final byte[] _peerId = new byte[4];
    private  byte[] _handshake_message;

    private Handshake() {
    }

    public Handshake (int peerId) {
        this (ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(peerId).array());
    }

    private Handshake (byte[] peerId) {
        if (peerId.length > 4) {
            throw new ArrayIndexOutOfBoundsException("peerId max length is 4, while "
                    + Arrays.toString (peerId) + "'s length is "+ peerId.length);
        }
        int i = 0;
        for (byte b : peerId) {
            _peerId[i++] = b;    
        }
        _handshake_message = new byte[_protocolId.length() + _zeroBits.length + _peerId.length];
        LogHelper.getLogger().info("Copying protocol id" );
        System.arraycopy(_protocolId.getBytes(), 0, _handshake_message, 0, _protocolId.length());
        LogHelper.getLogger().info("Copying zero bits");
        System.arraycopy(_zeroBits, 0, _handshake_message, _protocolId.length(), _zeroBits.length);
        LogHelper.getLogger().info("Copying peer id");
        System.arraycopy(_peerId,0,_handshake_message,_protocolId.length()+_zeroBits.length,_peerId.length);
    }

    private void writeObject(ObjectOutputStream oos)
        throws IOException {
        byte[] peerId = _protocolId.getBytes(Charset.forName("US-ASCII"));
        if (peerId.length > _protocolId.length()) {
            throw new IOException("protocol id length is " + peerId.length + " instead of " + _protocolId.length());
        }
        LogHelper.getLogger().info("Peer ID: " + Arrays.toString(peerId ));
        LogHelper.getLogger().info("zero bits: " + Arrays.toString( _zeroBits ));
        LogHelper.getLogger().info("other peer id: " + Arrays.toString(_peerId));
        LogHelper.getLogger().info("Handshake message: " + Arrays.toString(_handshake_message));
        LogHelper.getLogger().info("Peer ID: " + peerId + " zero bits: " + _zeroBits + " other peer id: " + _peerId);
        oos.write(_handshake_message, 0, _handshake_message.length);
//        oos.write(peerId, 0, peerId.length);
//        oos.write(_zeroBits, 0, _zeroBits.length);
//        oos.write(_peerId, 0, _peerId.length);
    }

    private void readObject(InputStream ois)
        throws ClassNotFoundException, IOException {
        // Read and check protocol Id
        byte[] protocolId = new byte[_protocolId.length()];

        LogHelper.getLogger().info("Reading: protocol id");
        if (ois.read(protocolId, 0, _protocolId.length()) < _protocolId.length()) {
            throw new ProtocolException ("protocol id is " + Arrays.toString (protocolId) + " instead of " + _protocolId);
        }
        LogHelper.getLogger().info("Read: protocol id");
        LogHelper.getLogger().info("Read: " + Arrays.toString(protocolId));
        if (!_protocolId.equals (new String(protocolId, "US-ASCII"))) {
            throw new ProtocolException ("protocol id is " + Arrays.toString (protocolId) + " instead of " + _protocolId);
        }

        // Read and check zero bits
        if (ois.read(_zeroBits, 0, _zeroBits.length) <  _zeroBits.length) {
            throw new ProtocolException ("zero bit bytes read are less than " + _zeroBits.length);
        }

        // Read and check peer id
        if (ois.read(_zeroBits, 0, _peerId.length) <  _peerId.length) {
            throw new ProtocolException ("peer id bytes read are less than " + _peerId.length);
        }
    }

    public static Handshake readMessage (InputStream in) throws Exception {
        Handshake handshake = new Handshake();
        handshake.readObject(in);
        return handshake;
    }

    public int getPeerId() {
        return ByteBuffer.wrap(_peerId).order(ByteOrder.BIG_ENDIAN).getInt();
    }

    public byte[] get_handshake_message() {
        return _handshake_message;
    }
}
