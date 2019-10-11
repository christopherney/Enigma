package com.chrisney.parser;

import java.util.ArrayList;

public class CodeBlock {

    /**
     * Indicate the star position of block in the source code
     */
    public int start = -1;

    /**
     * Indicate the end position of block in the source code
     */
    public int end = -1;

    /**
     * Source code
     */
    public String code;

    /**
     * Black name for Class or function
     */
    public String name;

    /**
     * Define type of block
     */
    public BlockType type = BlockType.Undefined;

    /**
     * define modifier type (public, private, protected)
     */
    public Modifier modifier = Modifier.Public;

    /**
     * Sub blocks of code contains by the current block
     */
    public ArrayList<CodeBlock> subBlocks = new ArrayList<>();

    /**
     * Source code of the block split in words (useful for processing)
     */
    public ArrayList<CodeString> words = new ArrayList<>();

    public enum Modifier {
        Public,
        Private,
        Protected
    }

    public enum BlockType {
        Undefined,
        Package,
        Import,
        Class,
        Interface,
        Attribute,
        CommentLine,
        CommentBlock,
        Annotation,
        Function,
        Constructor,
        Condition,
        Loop,
        TryCatch,
        LineOfCode,
        Return,
        StringValue
    }

    public String wordsToString() {
        StringBuilder sb = new StringBuilder();
        for (CodeString s : words) sb.append(s.value);
        return sb.toString();
    }

    @Override
    public String toString() {
        return "[" + start + ", " + end + "] (" + type + ":" + name + ") " + wordsToString();
    }
}
