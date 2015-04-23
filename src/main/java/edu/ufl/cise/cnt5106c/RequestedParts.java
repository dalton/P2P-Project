package edu.ufl.cise.cnt5106c;

import edu.ufl.cise.cnt5106c.log.LogHelper;
import java.util.BitSet;

/**
 *
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public class RequestedParts {
    private final BitSet _requestedParts;
    private final long _timeoutInMillis;

    RequestedParts (int nParts, long unchokingInterval) {
        _requestedParts = new BitSet (nParts);
        _timeoutInMillis = unchokingInterval * 2;
    }

    /**
     * @param requestabableParts
     * @return the ID of the part to request, if any, or a negative number in
     * case all the missing parts are already being requested or the file is
     * complete.
     */
    synchronized int getPartToRequest(BitSet requestabableParts) {
        requestabableParts.andNot(_requestedParts);
        if (!requestabableParts.isEmpty()) {
            final int partId = RandomUtils.pickRandomSetIndexFromBitSet(requestabableParts);
            _requestedParts.set(partId);

            // Make the part requestable again in _timeoutInMillis
            new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        synchronized (_requestedParts) {
                            _requestedParts.clear(partId);
                            LogHelper.getLogger().debug("clearing requested parts for pert " + partId);
                        }
                    }
                }, 
                _timeoutInMillis 
            );
            return partId;
        }
        return -1;
    }
}
