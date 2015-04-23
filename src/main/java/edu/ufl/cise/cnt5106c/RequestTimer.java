package edu.ufl.cise.cnt5106c;

import edu.ufl.cise.cnt5106c.io.ProtocolazibleObjectOutputStream;
import edu.ufl.cise.cnt5106c.log.LogHelper;
import edu.ufl.cise.cnt5106c.messages.Message;
import edu.ufl.cise.cnt5106c.messages.Request;

import java.io.IOException;
import java.util.Collection;
import java.util.TimerTask;

/**
 *
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public class RequestTimer extends TimerTask {
    private final Request _request;
    private final FileManager _fileMgr;
    private final  ProtocolazibleObjectOutputStream _out;
    private final int _remotePeerId;
    private final Message _message;

    RequestTimer (Request request, FileManager fileMgr, ProtocolazibleObjectOutputStream out, Message message, int remotePeerId) {
        super();
        _request = request;
        _fileMgr = fileMgr;
        _out = out;
        _remotePeerId = remotePeerId;
        _message = message;
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
            try {
                _out.writeObject(_message);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
