package edu.ufl.cise.cnt5106c;

import edu.ufl.cise.cnt5106c.messages.Message;
import edu.ufl.cise.cnt5106c.messages.Request;
import java.util.Collection;
import java.util.TimerTask;

/**
 *
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public class RequestTimer extends TimerTask {
    private final Request _request;
    private final FileManager _fileMgr;
    private final Collection<Message> _queue;

    RequestTimer (Request request, FileManager fileMgr, Collection<Message> queue) {
        super();
        _request = request;
        _fileMgr = fileMgr;
        _queue = queue;
    }

    @Override
    public void run() {
        if (!_fileMgr.hasPart(_request.getPieceIndex())) {
            _queue.add(_request);
        }
    }
}
