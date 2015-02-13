package edu.ufl.cise.cnt5106c;

import java.util.BitSet;

/**
 *
 * @author Giacomo Benincasa    (giacomo@cise.ufl.edu)
 */
public class RandomUtils {
    public static int pickRandomSetIndexFromBitSet (BitSet bitset) {
        if (bitset.isEmpty()) {
            throw new RuntimeException ("The bitset is empty, cannot find a set element");
        }
        // Generate list of set elements in the format that follows: { 2, 4, 5, ...}
        String set = bitset.toString(); 
        // Separate the elements, and pick one randomly
        String[] indexes = set.substring(1, set.length()-1).split(",");
        return Integer.parseInt(indexes[(int)(Math.random()*(indexes.length-1))].trim());
    }
}
