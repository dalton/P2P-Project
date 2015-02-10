package edu.ufl.cise.cnt5106c.utils;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public class BitUtilsTest {
    
    public BitUtilsTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of setBit method, of class BitUtils.
     */
    @Test
    public void testSetBit() {
        System.out.println("setBit");
        byte b = 0;
        assertEquals(1,    BitUtils.setBit (b, (byte) 0));  // 00000001
        assertEquals(2,    BitUtils.setBit (b, (byte) 1));  // 00000010
        assertEquals(4,    BitUtils.setBit (b, (byte) 2));  // 00000100
        assertEquals(8,    BitUtils.setBit (b, (byte) 3));  // 00001000
        assertEquals(16,   BitUtils.setBit (b, (byte) 4));  // 00010000
        assertEquals(32,   BitUtils.setBit (b, (byte) 5));  // 00100000
        assertEquals(64,   BitUtils.setBit (b, (byte) 6));  // 01000000
        assertEquals(-128, BitUtils.setBit (b, (byte) 7));  // 10000000 - byte is signed!

        b = 1;
        assertEquals(3,    BitUtils.setBit (b, (byte) 1));  // 00000011
        assertEquals(5,    BitUtils.setBit (b, (byte) 2));  // 00000101
        assertEquals(9,    BitUtils.setBit (b, (byte) 3));  // 00001001
        assertEquals(17,   BitUtils.setBit (b, (byte) 4));  // 00010001
        assertEquals(33,   BitUtils.setBit (b, (byte) 5));  // 00100001
        assertEquals(65,   BitUtils.setBit (b, (byte) 6));  // 01000001
        assertEquals(-127, BitUtils.setBit (b, (byte) 7));  // 10000001 - byte is signed!
    }

    /**
     * Test of getBit method, of class BitUtils.
     */
    @Test
    public void testGetBit() {
        System.out.println("getBit");
        byte b = 33;    // 00100001
        assertEquals(true,  BitUtils.getBit (b, (byte) 0));
        assertEquals(false, BitUtils.getBit (b, (byte) 1));
        assertEquals(false, BitUtils.getBit (b, (byte) 2));
        assertEquals(false, BitUtils.getBit (b, (byte) 3));
        assertEquals(false, BitUtils.getBit (b, (byte) 4));
        assertEquals(false, BitUtils.getBit (b, (byte) 5));
        assertEquals(false, BitUtils.getBit (b, (byte) 6));
        assertEquals(false, BitUtils.getBit (b, (byte) 7));
    }

    /**
     * Test of getByteIndex method, of class BitUtils.
     */
    @Test
    public void testGetByteIndex() {
        System.out.println("getByteIndex");

        assertEquals(0, BitUtils.getByteIndex(0));
        assertEquals(0, BitUtils.getByteIndex(1));
        assertEquals(0, BitUtils.getByteIndex(2));
        assertEquals(0, BitUtils.getByteIndex(3));
        assertEquals(0, BitUtils.getByteIndex(4));
        assertEquals(0, BitUtils.getByteIndex(5));
        assertEquals(0, BitUtils.getByteIndex(6));
        assertEquals(0, BitUtils.getByteIndex(7));

        assertEquals(1, BitUtils.getByteIndex(8));
        assertEquals(1, BitUtils.getByteIndex(9));
        assertEquals(1, BitUtils.getByteIndex(10));
        assertEquals(1, BitUtils.getByteIndex(11));
        assertEquals(1, BitUtils.getByteIndex(12));
        assertEquals(1, BitUtils.getByteIndex(13));
        assertEquals(1, BitUtils.getByteIndex(14));
        assertEquals(1, BitUtils.getByteIndex(15));
    }

    /**
     * Test of getBitIndex method, of class BitUtils.
     */
    @Test
    public void testGetBitIndex() {
        System.out.println("getBitIndex");
        
        assertEquals(0, BitUtils.getBitIndex(0));
        assertEquals(1, BitUtils.getBitIndex(1));
        assertEquals(2, BitUtils.getBitIndex(2));
        assertEquals(3, BitUtils.getBitIndex(3));
        assertEquals(4, BitUtils.getBitIndex(4));
        assertEquals(5, BitUtils.getBitIndex(5));
        assertEquals(6, BitUtils.getBitIndex(6));
        assertEquals(7, BitUtils.getBitIndex(7));

        assertEquals(0, BitUtils.getBitIndex(8));
        assertEquals(1, BitUtils.getBitIndex(9));
        assertEquals(2, BitUtils.getBitIndex(10));
        assertEquals(3, BitUtils.getBitIndex(11));
        assertEquals(4, BitUtils.getBitIndex(12));
        assertEquals(5, BitUtils.getBitIndex(13));
        assertEquals(6, BitUtils.getBitIndex(14));
        assertEquals(7, BitUtils.getBitIndex(15));
    }

    /**
     * Test of bitsToBytes method, of class BitUtils.
     */
    @Test
    public void testBitsToBytes() {
        System.out.println("bitsToBytes");

        assertEquals(0, BitUtils.bitsToBytes(0));

        assertEquals(1, BitUtils.bitsToBytes(1));
        assertEquals(1, BitUtils.bitsToBytes(2));
        assertEquals(1, BitUtils.bitsToBytes(3));
        assertEquals(1, BitUtils.bitsToBytes(4));
        assertEquals(1, BitUtils.bitsToBytes(5));
        assertEquals(1, BitUtils.bitsToBytes(6));
        assertEquals(1, BitUtils.bitsToBytes(7));
        assertEquals(1, BitUtils.bitsToBytes(8));

        assertEquals(2, BitUtils.bitsToBytes(9));
        assertEquals(2, BitUtils.bitsToBytes(10));
        assertEquals(2, BitUtils.bitsToBytes(11));
        assertEquals(2, BitUtils.bitsToBytes(12));
        assertEquals(2, BitUtils.bitsToBytes(13));
        assertEquals(2, BitUtils.bitsToBytes(14));
        assertEquals(2, BitUtils.bitsToBytes(15));
        assertEquals(2, BitUtils.bitsToBytes(16));
    }
    
}
