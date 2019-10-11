package com.chrisney.parser;

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
     * Indicate if word is a JAVA native Type
     */
    public boolean isType = false;

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
