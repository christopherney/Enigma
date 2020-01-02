package com.chrisney.enigma.parser;

/**
 * This class represent the position (indexes) of the block into his parent block
 */
public class CodePosition {

    /**
     * Start index
     */
    public int start = -1;

    /**
     * End index
     */
    public int end = -1;

    /**
     * Constructor
     * @param s Start index
     */
    public CodePosition(int s) {
        start = s;
    }

    /**
     * Constructor
     * @param s Start index
     * @param e End index
     */
    public CodePosition(int s, int e) {
        start = s;
        end = e;
    }

    /**
     * Indicate if the position is valid or not
     * @return True if position seems top be valid
     */
    public boolean isValid() {
        if (start == -1 || end == -1) return false;
        return start < end;
    }

    @Override
    public String toString() {
        return isValid() ? "[" + start + ", " + end + "]" : super.toString();
    }
}
