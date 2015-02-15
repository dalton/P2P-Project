/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ufl.cise.cnt5106c.io;

import edu.ufl.cise.cnt5106c.messages.Choke;
import edu.ufl.cise.cnt5106c.messages.Handshake;
import edu.ufl.cise.cnt5106c.messages.Message;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Giacomo
 */
public class FlatProtocolInputStreamTest {
    
    public FlatProtocolInputStreamTest() {
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

    @Test
    public void testReadHandshake() throws Exception {
        System.out.println("readHandshake");
        
        ByteArrayOutputStream bytes = new ByteArrayOutputStream(2048);
        FlatProtocolOutputStream out = new FlatProtocolOutputStream (bytes);
        Handshake hs = new Handshake(10);
        out.writeHandshake (hs);

        byte[] b = bytes.toByteArray();
        FlatProtocolInputStream in = new FlatProtocolInputStream (new ByteArrayInputStream (b, 0, b.length));
        Handshake result = in.readHandshake();

        assertEquals(hs.getPeerId(), result.getPeerId());
    }

    /**
     * Test of readMessage method, of class FlatProtocolInputStream.
     */
    @Test
    public void testReadMessage() throws Exception {
        System.out.println("readMessage");

        ByteArrayOutputStream bytes = new ByteArrayOutputStream(2048);
        FlatProtocolOutputStream out = new FlatProtocolOutputStream (bytes);

        // Test Choke
        Message msg = new Choke();
        out.writeMessage (msg);

        byte[] b = bytes.toByteArray();
        FlatProtocolInputStream in = new FlatProtocolInputStream (new ByteArrayInputStream (b, 0, b.length));
        Message result = in.readMessage();

        assertEquals(msg.getType(), result.getType());
    }    
}
