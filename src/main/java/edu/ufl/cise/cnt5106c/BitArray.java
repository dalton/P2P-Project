package edu.ufl.cise.cnt5106c;

import edu.ufl.cise.cnt5106c.utils.BitUtils;

/**
 *
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public class BitArray {
    private final byte[] _bytes;

    BitArray (int nBits) {
        if (nBits == 0) {
            throw new ArrayIndexOutOfBoundsException();
        }
        _bytes = new byte[BitUtils.bitsToBytes (nBits)];
        reset (_bytes);
    }

    public BitArray(byte[] bytes) {
        _bytes = bytes;
    }

    @Override
    public BitArray clone() {
        byte[] cpy = new byte[_bytes.length];
        System.arraycopy(_bytes, 0, cpy, 0, _bytes.length);
        return new BitArray (cpy);
    }

    /**
     * Set the specified bit to true
     *
     * @param pos
     * @return true if the bit was previously set to false, and it is not set to
     * true. False is the bit was previously already set to true. Either way,
     * the set() method will set the specified bit to true 
     */
    public boolean setBit (int pos) {
        int idx = BitUtils.getByteIndex (pos);
        if (idx > (_bytes.length - 1)) {
            throw new ArrayIndexOutOfBoundsException();
        }
        final boolean bToggled = !getBit (idx);
        _bytes[idx] = BitUtils.setBit (_bytes[idx], BitUtils.getBitIndex (pos));
        return bToggled;
    }

    public boolean getBit (int pos) {
        int idx = BitUtils.getByteIndex (pos);
        if (idx > (_bytes.length - 1)) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return BitUtils.getBit (_bytes[idx], BitUtils.getBitIndex (pos));
    }

    public byte[] getBytes() {
        return _bytes;
    }

    public void reset() {
        reset (_bytes);
    }

    private static void reset (byte[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = 0;
        }
    }
}
