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
     * Start/End offset (from start of file)
     */
    public int offset = 0;


    /**
     * Inner offset (from start of parent)
     */
    public int innerOffset = 0;

    /**
     * Source code
     */
    public String code;

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
     * Sub block position indexes
     */
    public ArrayList<CodePosition> subIndexes = null;

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

    /**
     * Return a String version of the object
     * @return String which represent the code block
     */
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
     * @param level Level of the block
     * @return Source code of the block
     */
    private String toCode(int level) {

        StringBuilder sb = new StringBuilder();

        if (!hasChildren()) {
            sb.append(code);
        } else {
            // For each sub block:
            for (int i = 0; i < subBlocks.size(); i++) {

                CodeBlock prevSubBlock = (i > 0) ? subBlocks.get(i - 1) : null;
                CodeBlock subBlock = subBlocks.get(i);
                CodeBlock nextSubBlock = (subBlocks.size() > i + 1) ? subBlocks.get(i + 1) : null;

                // Add code before sub block:
                int prefixStart = (prevSubBlock != null) ? prevSubBlock.innerOffset + prevSubBlock.end : 0;
                int prefixEnd = subBlock.innerOffset + subBlock.start;
                String prefix = TextUtils.safeSubstring(code, prefixStart, prefixEnd);
                if (prefix != null) sb.append(prefix);

                // Add sub code:
                sb.append(subBlock.toCode(level + 1));

                // Add code between current sub block and next one:
                int suffixStart = subBlock.innerOffset + subBlock.end;
                int suffixEnd = (nextSubBlock != null) ? nextSubBlock.innerOffset + nextSubBlock.start : code.length();
                String suffix = TextUtils.safeSubstring(code, suffixStart, suffixEnd);
                if (suffix != null) sb.append(suffix);

            }

            // End Of File
            if (!hasParent) sb.append("\n");
        }

        return sb.toString();
    }
}
