package com.chrisney.enigma.parser;

import com.chrisney.enigma.utils.TextUtils;

import java.util.ArrayList;

/**
 * Represent a block of JAVA code
 */
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
     * Start/End offset
     */
    public int offset = 0;

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

    /**
     * Type of the parent block
     */
    public CodeBlock.BlockType parentType = BlockType.Undefined;

    /**
     * Modifiers (for class attributes and functions)
     */
    public enum Modifier {
        Public,
        Private,
        Protected
    }

    /**
     * All JAVA types of code blocks
     */
    public enum BlockType {
        Undefined,
        Package,
        Import,
        Class,
        AnonymousInnerClass,
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

    /**
     * Return the start position (character index) with offset
     * @return Start position (character index) with offset
     */
    public int getStart() {
        return start + offset;
    }

    /**
     * Return the end position (character index) with offset
     * @return End position (character index) with offset
     */
    public int getEnd() {
        return end + offset;
    }

    /**
     * Convert all words to a String value (source code)
     * @return Source code generate from words contains by the block
     */
    public String wordsToString() {
        StringBuilder sb = new StringBuilder();
        for (CodeString s : words) sb.append(s.value);
        return sb.toString();
    }

    /**
     * Indicate if the block is a line or block of comment
     * @return True if the block is a line or block of comment
     */
    public boolean isComment() {
        return CodeBlock.isComment(type);
    }

    /**
     * Indicate if the block is a Function or a Class
     * @param blockType Type of block
     * @return True if the block is a Function or a Class
     */
    public boolean isFunctionOrClass(CodeBlock.BlockType blockType) {
        return blockType == BlockType.Class || blockType == BlockType.Function;
    }

    /**
     * Indicate if the block is a line or block of comment
     * @param blockType Type of block
     * @return True if the block is a line or block of comment
     */
    public static boolean isComment(BlockType blockType) {
        return blockType == BlockType.CommentBlock || blockType == BlockType.CommentLine;
    }

    /**
     * Indicate if the block is a Class or Anonymous Inner Class
     * @param blockType Type of block
     * @return True if the block is a Class or Anonymous Inner Class
     */
    public static boolean isClass(BlockType blockType) {
        return blockType == BlockType.Class || blockType == BlockType.AnonymousInnerClass;
    }

    @Override
    public String toString() {
        String m = "";
        if (modifier != null) m = Modifier.Public + ": ";
        return "[" + m + (start + offset) + ", " + (end + offset) + "] (" + type + ":" + name + ") " + wordsToString();
    }

    /**
     * Indicate if the block has children or not.
     * @return True if the block has children
     */
    public boolean hasChildren() {
        return subBlocks != null && subBlocks.size() > 0;
    }

    /**
     * Generate the source code of the block
     * @return Source code of the block
     */
    public String toCode() {
        return toCode(0);
    }

    /**
     * Generate the source code of the block
     * @param tab Number of tab space to prepend
     * @return Source code of the block
     */
    private String toCode(int tab) {

        StringBuilder sb = new StringBuilder();

        if (!hasChildren()) {
            sb.append(code);
        } else {

            boolean hasSemicolonBlock = code.trim().endsWith("}");

            // Print function (or class) signature:
            if (hasSemicolonBlock) {
                char c;
                int i;
                char prevNoneEmptyChar = ' ';
                for (i = 0; i < code.length(); i++) {
                    c = code.charAt(i);
                    if (c == '{' && prevNoneEmptyChar != ']') break;
                    if (!TextUtils.isEmptyChar(c)) prevNoneEmptyChar = c;
                }
                sb.append(code, 0, i + 1);
            }

            // Print content of function (or class):
            for (CodeBlock subBlock : subBlocks) {
                sb.append(subBlock.toCode(tab + 1));
            }

            // Close function (or class):
            if (hasSemicolonBlock) {

                int max = code.length() - 1;
                int i = max;
                boolean foundSemicolon  = false;
                for (; i > 0; i--) {
                    char c = code.charAt(i);
                    if (!TextUtils.isEmptyChar(c) && foundSemicolon && i < max) break;
                    if (c == '}') foundSemicolon = true;
                }
                String end = code.substring(i + 1);
                sb.append(end);

                // End Of File
                if (!hasParent) sb.append("\n");
            }
        }
        return sb.toString();
    }
}
