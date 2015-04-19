package edu.ufl.cise.cnt5106c.log;

import static org.junit.Assert.*;
import org.junit.*;

/**
 * Created by shivansh on 4/19/15.
 */
public class EventLoggerTest {

    public EventLoggerTest(){
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
     * Test all
     */
    @Test
    public void testAll(){
        String peerConnectionMsg1 = "1 makes a connection to Peer 2";
        assertEquals("Peer 1 must be making connection", peerConnectionMsg1, );
        String peerConnectionMsg2 = "1 is connected from Peer 2";
        assertEquals("Peer 1 must be connected", peerConnectionMsg2, );

        String chokeMessage = "1 is choked by 2";
        assertEquals("Peer 1 must be choked by 2", chokeMessage, );

        String unchokeMessage = "1 is unchoked by 2";
        assertEquals("Peer 1 must be unchoked by 2", unchokeMessage, );

        String haveMessage = "1 received the 'have' message from 2 for the piece 1.";
        assertEquals("Peer 1 must receive the 'have' message from 2 for the piece 1", haveMessage, );

        String interestedMessage = "1 received the 'interested' message from 2.";
        assertEquals("Peer 1 must receive the 'interested' message from 2", interestedMessage, );

        String notInterestedMessage = "1 received the 'not interested' message from 2.";
        assertEquals("Peer 1 must receive the 'not interested' message from 2", notInterestedMessage, );

        String pieceDownloadedMessage = "1 has downloaded the piece 1 from peer 2. Now the number of pieces it has is 1.";
        assertEquals("Peer 1 must downloade the piece 1 from peer 2. Now the number of pieces it will have is 1", pieceDownloadedMessage, );

        String fileDownloadedMessage = "1 has downloaded the complete file";
        assertEquals("Peer 1 must download the complete file", fileDownloadedMessage, );
    }

}