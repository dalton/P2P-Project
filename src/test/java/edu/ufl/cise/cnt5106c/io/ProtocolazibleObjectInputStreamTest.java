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
public class ProtocolazibleObjectInputStreamTest {
    
    public ProtocolazibleObjectInputStreamTest() {
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
    public void readObject() throws Exception {
        System.out.println("readObject");
        
        ByteArrayOutputStream bytes = new ByteArrayOutputStream(2048);
        ProtocolazibleObjectOutputStream out = new ProtocolazibleObjectOutputStream (bytes);

        // Test Handshake
        Handshake hs = new Handshake(10);
        out.writeObject (hs);
        Message choke = new Choke();
        out.writeObject (choke);

        // Read and Test
        ProtocolazibleObjectInputStream in = new ProtocolazibleObjectInputStream (new ByteArrayInputStream (bytes.toByteArray()));
        Handshake hResult = (Handshake) in.readObject();
        assertEquals(hs.getPeerId(), hResult.getPeerId());
        Message result = (Message) in.readObject();
        assertEquals (choke.getType(), result.getType());
    } 
}
