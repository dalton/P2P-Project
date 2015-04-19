package edu.ufl.cise.cnt5106c.log;

import java.io.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public class LogHelper {
    private static final LogHelper _log = new LogHelper (Logger.getLogger("CNT5106C"));
    static {
        // FIXME: configure logger here
        _log._l.setLevel(Level.ALL);
        Properties _preferences=new Properties();
//        try{
//            FileInputStream _configFile=new FileInputStream("/conf/ConfigureLogger.properties");
//            _preferences.load(_configFile);
//            LogManager.getLogManager().readConfiguration(_configFile);
//        }catch (IOException _exceptionConfigure)
//        {
//            System.out.println("WARNING: Could not open configuration file");
//            System.out.println("WARNING: Logging could not configure(Console Output)");
//        }
    }
    private final Logger _l;

    LogHelper (Logger log) {
        _l = log;
    }

    public static LogHelper getLogger () {
        return _log;
    }

    public synchronized void conf (String msg) {
        _l.log(Level.CONFIG, msg);
    }

    public synchronized void debug (String msg) {
        _l.log(Level.FINE, msg);
    }

    public synchronized void info (String msg) {
        _l.log (Level.INFO, msg);
    }

    public synchronized void severe (String msg) {
        _l.log(Level.SEVERE, msg);
    }

    public synchronized void warning (String msg) {
        _l.log(Level.WARNING, msg);
    }

    public synchronized void severe (Throwable e) {
        _l.log(Level.SEVERE, stackTraceToString (e));
    }

    public synchronized void warning (Throwable e) {
        _l.log(Level.WARNING, stackTraceToString (e));
    }

    private static String stackTraceToString (Throwable t) {
        final Writer sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
