package edu.ufl.cise.cnt5106c;

import edu.ufl.cise.cnt5106c.utils.BitUtils;

/**
 *
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public class BitArray {
    private final byte[] _bytes;

    BitArray (int nBits) {
        _bytes = new byte[BitUtils.bitsToBytes (nBits)];
    }

    public BitArray(byte[] bytes) {
        _bytes = bytes;
    }

    public void setBit (int pos) {
        int idx = BitUtils.getByteIndex (pos);
        if (idx > (_bytes.length - 1)) {
            throw new ArrayIndexOutOfBoundsException();
        }
        _bytes[idx] = BitUtils.setBit (_bytes[idx], BitUtils.getBitIndex (pos));
    }

    public boolean getBit (int pos) {
        int idx = BitUtils.getByteIndex (pos);
        if (idx > (_bytes.length - 1)) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return BitUtils.getBit (_bytes[idx], BitUtils.getBitIndex (pos));
    }

    public void reset() {
        for (int i = 0; i < _bytes.length; i++) {
            _bytes[i] = 0;
        }
    }

    public byte[] getBytes() {
        return _bytes;
    }
}
