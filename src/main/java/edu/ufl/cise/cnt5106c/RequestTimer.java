package edu.ufl.cise.cnt5106c;

import edu.ufl.cise.cnt5106c.log.LogHelper;
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
    private final int _remotePeerId;

    RequestTimer (Request request, FileManager fileMgr, Collection<Message> queue, int remotePeerId) {
        super();
        _request = request;
        _fileMgr = fileMgr;
        _queue = queue;
        _remotePeerId = remotePeerId;
    }

    @Override
    public void run() {
        if (_fileMgr.hasPart(_request.getPieceIndex())) {
            LogHelper.getLogger().debug("Not rerequesting piece " + _request.getPieceIndex()
                    + " to peer " + _remotePeerId);
        }
        else {
            LogHelper.getLogger().debug("Rerequesting piece " + _request.getPieceIndex()
                    + " to peer " + _remotePeerId);
            _queue.add(_request);
            
        }
    }
}
