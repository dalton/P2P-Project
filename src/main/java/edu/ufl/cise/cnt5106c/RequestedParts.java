package edu.ufl.cise.cnt5106c;

import java.util.BitSet;

/**
 *
 * @author Giacomo
 */
public class RequestedParts {
    private final BitSet _requestedParts;

    RequestedParts (int nParts) {
        _requestedParts = new BitSet (nParts);
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
            int partId = RandomUtils.pickRandomSetIndexFromBitSet(requestabableParts);
            _requestedParts.set(partId);
            return partId;
        }
        return -1;
    }
}
