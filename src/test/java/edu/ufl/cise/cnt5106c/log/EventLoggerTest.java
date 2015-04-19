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

}