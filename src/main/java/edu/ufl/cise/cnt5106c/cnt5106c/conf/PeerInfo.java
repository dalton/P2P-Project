package edu.ufl.cise.cnt5106c.cnt5106c.conf;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.util.Collection;
import java.util.LinkedList;

/**
 *
 * @author Giacomo
 */
public class PeerInfo {

    public static final String CONFIG_FILE_NAME = "PeerInfo.cfg";
    private final Collection<RemotePeerInfo> _peerInfoVector = new LinkedList<>();

    public void read (Reader reader) throws FileNotFoundException, IOException, ParseException {
        BufferedReader in = new BufferedReader(reader);
        int i = 0;
        for (String line; (line = in.readLine()) != null;) {
            String[] tokens = line.split("\\s+");
            if (line.length() != 3) {
                throw new ParseException (line, i);
            }
            _peerInfoVector.add (new RemotePeerInfo(tokens[0], tokens[1], tokens[2], Boolean.parseBoolean(tokens[3])));
            i++;
        }
    }

    public Collection<RemotePeerInfo> getPeerInfo () {
        return new LinkedList<>(_peerInfoVector);
    }
}
