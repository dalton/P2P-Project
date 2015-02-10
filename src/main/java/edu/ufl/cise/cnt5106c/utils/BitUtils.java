package edu.ufl.cise.cnt5106c.utils;

/**
 *
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public class BitUtils {

    public static byte setBit (byte b, byte pos) {
        if (pos < 0 || pos > 7) {
            throw new ArrayIndexOutOfBoundsException();
        }
        final byte one = 1;
        return (byte) (b | (one << pos));
    }

    public static boolean getBit (byte b, byte pos) {
        if (pos < 0 || pos > 7) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return (b & (0x01 << pos)) == 1;
    }

    public static int getByteIndex (int nBits) {
        return (int) Math.floor (nBits/8.0);
    }

    public static byte getBitIndex (int nBits) {
        return (byte) (nBits % 0x8);
    }

    public static int bitsToBytes (int nBits) {
        return (int) Math.ceil (nBits/8.0);
    }
}
