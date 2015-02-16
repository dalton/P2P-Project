package edu.ufl.cise.cnt5106c.conf;

import java.io.FileReader;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import static org.junit.Assert.*;

/**
 *
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public class CommonPropertiesTest {
    
    public CommonPropertiesTest() {
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
     * Test of valueOf method, of class CommonProperties.
     */
    @org.junit.Test
    public void testValueOf() {
        FileReader reader = null;
        try {
            System.out.println("valueOf");
            reader = new FileReader (CommonProperties.CONFIG_FILE_NAME + ".test");
            Properties result = CommonProperties.read(reader);
            for (Entry<Object, Object> entry : result.entrySet()) {
                CommonProperties.valueOf ((String) entry.getKey());
            }
        }
        catch (Exception ex) {
            Logger.getLogger(CommonPropertiesTest.class.getName()).log(Level.SEVERE, null, ex);
            fail(ex.getMessage());
        }
        finally {
            try { reader.close(); }
            catch (IOException ex) {}
        }
    }

    /**
     * Test of read method, of class CommonProperties.
     */
    @org.junit.Test
    public void testRead() {
        System.out.println("testRead");
        FileReader reader = null;
        try {
            System.out.println("read");
            reader = new FileReader (CommonProperties.CONFIG_FILE_NAME + ".test");
            Properties result = CommonProperties.read(reader);
            assertEquals(result.getProperty(CommonProperties.FileName.toString()), "TheFile.dat");
            assertEquals(result.getProperty(CommonProperties.FileSize.toString()), "10000232");
            assertEquals(result.getProperty(CommonProperties.NumberOfPreferredNeighbors.toString()), "2");
            assertEquals(result.getProperty(CommonProperties.OptimisticUnchokingInterval.toString()), "15");
            assertEquals(result.getProperty(CommonProperties.PieceSize.toString()), "32768");
            assertEquals(result.getProperty(CommonProperties.UnchokingInterval.toString()), "5");
        }
        catch (Exception ex) {
            Logger.getLogger(CommonPropertiesTest.class.getName()).log(Level.SEVERE, null, ex);
            fail(ex.getMessage());
        }
        finally {
            try { reader.close(); }
            catch (IOException ex) {}
        }
    }
    
}
