package edu.ufl.cise.cnt5106c.conf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.util.Properties;

/**
 *
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public enum CommonProperties {

    NumberOfPreferredNeighbors,
    UnchokingInterval,
    OptimisticUnchokingInterval,
    FileName,
    FileSize,
    PieceSize;

    public static final String CONFIG_FILE_NAME = "Common.cfg";
    private static final String COMMENT_CHAR = "#";

    public static Properties read (Reader reader) throws Exception {

        final Properties conf = new Properties () {
            @Override
            public synchronized void load(Reader reader)
                    throws IOException {
                BufferedReader in = new BufferedReader(reader);
                int i = 0;
                for (String line; (line = in.readLine()) != null; i++) {
                    line = line.trim();
                    if ((line.length() <= 0) || (line.startsWith (COMMENT_CHAR))) {
                        continue;
                    }
                    // The defaul Properties class uses the '=' character to
                    // separate keys and value, while the project description
                    // requires keys and values being separated by a space.
                    String[] tokens = line.split("\\s+");
                    if (tokens.length != 2) {
                        throw new IOException (new ParseException (line, i));
                    }
                    setProperty(tokens[0].trim(), tokens[1].trim());
                }
            }
        };

        conf.load (reader);

        // Check the config file contains all the needed properties
        for (CommonProperties prop : CommonProperties.values()) {
            if (!conf.containsKey(prop.toString())) {
                throw new Exception ("config file does not contain property " + prop);
            }
        }

        return conf;
    }
}
