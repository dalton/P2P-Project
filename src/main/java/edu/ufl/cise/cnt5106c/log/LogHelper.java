package edu.ufl.cise.cnt5106c.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public class LogHelper {
    private static final LogHelper _log = new LogHelper (Logger.getLogger("CNT5106C"));
    static {
        // TODO: configure logger here
    }
    private final Logger _l;

    LogHelper (Logger log) {
        _l = log;
    }

    public static LogHelper getLogger () {
        return _log;
    }

    public synchronized void info (String msg) {
        _l.log(Level.INFO, msg);
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
