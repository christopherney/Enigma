package com.chrisney.enigma.parser;

/**
 * Represent a keyword a JAVA code
 */
public class CodeString {

    /**
     * Word value
     */
    public String value;

    /**
     * Word start position
     */
    public int start;

    /**
     * Word end position
     */
    public int end;

    /**
     * Indicate if word is a JAVA keyword (instruction)
     */
    public boolean isInstruction = false;

    /**
     * Indicate if word is a JAVA native Type (int, string...)
     */
    public boolean isType = false;

    /**
     * Indicate if the word is a value of Switch/Case  conditional block
     */
    public boolean isCaseValue = false;

    /**
     * Constructor
     * @param start Word start position
     */
    public CodeString(int start) {
        this.start = start;
    }

    @Override
    public String toString() {
        return "[" + start + ", " + end + "] '" + value + "'";
    }
}
