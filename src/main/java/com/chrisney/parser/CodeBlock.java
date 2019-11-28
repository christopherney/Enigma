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
     * Sub source code (nested by function, class...)
     */
    public String subCode;

    /**
     * Black name for Class or function or Annotation
     */
    public String name;

    /**
     * Define type of block
     */
    public BlockType type = BlockType.Undefined;

    /**
     * define modifier type (public, private, protected)
     */
    public Modifier modifier = null;

    /**
     * Type name of the object returned by the function (optional)
     */
    public String returnType = null;

    /**
     * Sub blocks of code contains by the current block
     */
    public ArrayList<CodeBlock> subBlocks = new ArrayList<>();

    /**
     * Source code of the block split in words (useful for processing)
     */
    public ArrayList<CodeString> words = new ArrayList<>();

    /**
     * Indicate if the code block is nested or not.
     */
    public boolean hasParent = false;

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
        String m = "";
        if (modifier != null) m = Modifier.Public + ": ";
        return "[" + m + start + ", " + end + "] (" + type + ":" + name + ") " + wordsToString();
    }

    public String toCode() {
        return toCode(0);
    }

    private String toCode(int tab) {

        StringBuilder sbTab = new StringBuilder();
        for(int i = 0; i < tab; i++ ) sbTab.append('\t');

        StringBuilder sb = new StringBuilder();
        if (hasParent) sb.append(sbTab.toString());
        if (subBlocks == null || subBlocks.size() == 0) {
            sb.append(code);
            if(code.endsWith(";") || code.endsWith("*/") || code.endsWith(")")) sb.append("\n");
        } else {
            if (code.endsWith("}")) {
                int i = 0;
                while (code.charAt(i) != '{') {
                    sb.append(code.charAt(i));
                    i++;
                }
            }
            sb.append("{\n");
            for(CodeBlock subBlock : subBlocks) {
                sb.append(subBlock.toCode(tab + 1));
            }
            if (code.endsWith("}")) {
                if (hasParent) sb.append(sbTab.toString());
                sb.append("}\n\n");
            }
        }
        return sb.toString();
    }
}
