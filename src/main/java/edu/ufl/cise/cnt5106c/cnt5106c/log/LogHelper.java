package edu.ufl.cise.cnt5106c.cnt5106c.log;

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

    private LogHelper (Logger log) {
        _l = log;
    }

    public static LogHelper getLogger () {
        return _log;
    }

    public void info (String msg) {
        _l.log(Level.INFO, msg);
    }

    public void severe (String msg) {
        _l.log(Level.SEVERE, msg);
    }

    public void warning (String msg) {
        _l.log(Level.WARNING, msg);
    }

    public void severe (Throwable e) {
        _l.log(Level.SEVERE, stackTraceToString (e));
    }

    public void warning (Throwable e) {
        _l.log(Level.WARNING, stackTraceToString (e));
    }

    private static String stackTraceToString (Throwable t) {
        // TODO: imlement this
        return new String ();
    }
}
