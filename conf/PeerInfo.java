package edu.ufl.cise.cnt5106c.conf;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.util.Collection;
import java.util.LinkedList;

/**
 *
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public class PeerInfo {

    public static final String CONFIG_FILE_NAME = "PeerInfo.cfg";
    private final String COMMENT_CHAR = "#";
    private final Collection<RemotePeerInfo> _peerInfoVector = new LinkedList<>();

    public void read (Reader reader) throws FileNotFoundException, IOException, ParseException {
        BufferedReader in = new BufferedReader(reader);
        int i = 0;
        for (String line; (line = in.readLine()) != null;) {
            line = line.trim();
            if ((line.length() <= 0) || (line.startsWith (COMMENT_CHAR))) {
                continue;
            }
            String[] tokens = line.split("\\s+");
            if (tokens.length != 4) {
                throw new ParseException (line, i);
            }
            final boolean bHasFile = (tokens[3].trim().compareTo("1") == 0);
            _peerInfoVector.add (new RemotePeerInfo(tokens[0].trim(), tokens[1].trim(),
                    tokens[2].trim(), bHasFile));
            i++;
        }
    }

    public Collection<RemotePeerInfo> getPeerInfo () {
        return new LinkedList<>(_peerInfoVector);
    }
}
