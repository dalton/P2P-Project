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
        LogHelper.getLogger().info("somthinjpogjrehy[ot");
        
    }

    /**
     * Test of peerConnection method, of class EventLogger.
     */
    @Test
    public void testPeerConnection() {
        System.out.println("peerConnection");
    }

    /**
     * Test of changeOfPrefereedNeighbors method, of class EventLogger.
     */
    @Test
    public void testChangeOfPrefereedNeighbors() {
        System.out.println("changeOfPrefereedNeighbors");
        String preferredNeighbors = "1002, 1003, 1004";
        EventLogger instance = new EventLogger(1001);
        instance.changeOfPrefereedNeighbors(preferredNeighbors);
        instance.changeOfPrefereedNeighbors("");
    }

    /**
     * Test of changeOfOptimisticallyUnchokedNeighbors method, of class EventLogger.
     */
    @Test
    public void testChangeOfOptimisticallyUnchokedNeighbors() {
        System.out.println("changeOfOptimisticallyUnchokedNeighbors");
        String preferredNeighbors = "1002, 1003, 1004";
        EventLogger instance = new EventLogger(1001);
        instance.changeOfOptimisticallyUnchokedNeighbors(preferredNeighbors);
        instance.changeOfOptimisticallyUnchokedNeighbors("");
    }

    /**
     * Test of chokeMessage method, of class EventLogger.
     */
    @Test
    public void testChokeMessage() {
        System.out.println("chokeMessage");
    }

    /**
     * Test of unchokeMessage method, of class EventLogger.
     */
    @Test
    public void testUnchokeMessage() {
        System.out.println("unchokeMessage");
    }

    /**
     * Test of haveMessage method, of class EventLogger.
     */
    @Test
    public void testHaveMessage() {
        System.out.println("haveMessage");
    }

    /**
     * Test of interestedMessage method, of class EventLogger.
     */
    @Test
    public void testInterestedMessage() {
        System.out.println("interestedMessage");
    }

    /**
     * Test of notInterestedMessage method, of class EventLogger.
     */
    @Test
    public void testNotInterestedMessage() {
        System.out.println("notInterestedMessage");
    }

    /**
     * Test of pieceDownloadedMessage method, of class EventLogger.
     */
    @Test
    public void testPieceDownloadedMessage() {
        System.out.println("pieceDownloadedMessage");
    }

    /**
     * Test of fileDownloadedMessage method, of class EventLogger.
     */
    @Test
    public void testFileDownloadedMessage() {
        System.out.println("fileDownloadedMessage");
    }

}