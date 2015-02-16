package edu.ufl.cise.cnt5106c.conf;

import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class PeerInfoTest {
    
    public PeerInfoTest() {
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
     * Test of read method, of class PeerInfo.
     */
    @Test
    public void testRead() throws Exception {        
        System.out.println("testRead");
        FileReader reader = null;
        try {
            System.out.println("read");
            reader = new FileReader (PeerInfo.CONFIG_FILE_NAME + ".test");
            PeerInfo peerInfo = new PeerInfo();
            peerInfo.read(reader);
            Collection<RemotePeerInfo> infos = peerInfo.getPeerInfo();
            
            int i = 0;
            for (RemotePeerInfo info : infos) {
                switch (i) {
                    case 0:
                        compareRemoteInfo (1001, "lin114-00.cise.ufl.edu", 6008, true, info);
                        break;
                    case 1:
                        compareRemoteInfo (1002, "lin114-01.cise.ufl.edu", 6008, false, info);
                        break;
                    case 2:
                        compareRemoteInfo (1003, "lin114-02.cise.ufl.edu", 6008, false, info);
                        break;
                    case 3:
                        compareRemoteInfo (1004, "lin114-03.cise.ufl.edu", 6008, false, info);
                        break;
                    case 4:
                        compareRemoteInfo (1005, "lin114-04.cise.ufl.edu", 6008, false, info);
                        break;
                    case 5:
                        compareRemoteInfo (1006, "lin114-05.cise.ufl.edu", 6008, false, info);
                        break;
                    default:
                        fail("more lines than expected");
                }
                i++;
            }

        }
        catch (Exception ex) {
            fail(ex.getMessage());
            Logger.getLogger(CommonPropertiesTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            try { reader.close(); }
            catch (IOException ex) {}
        }
    }

    /**
     * Test of getPeerInfo method, of class PeerInfo.
     */
    @Test
    public void testGetPeerInfo() {
        
    }
    
    private void compareRemoteInfo (int peerId, String host, int port, boolean bHasFile, RemotePeerInfo result) {
        assertEquals(result.getPeerId(), peerId);
        assertEquals(result._peerAddress, host);
        assertEquals(result.getPort(), port);
        assertEquals(result._hasFile, bHasFile);
    }
}
