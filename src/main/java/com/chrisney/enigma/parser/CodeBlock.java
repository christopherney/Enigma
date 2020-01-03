package com.chrisney.enigma.parser;

import com.chrisney.enigma.utils.SmartArrayList;
import com.chrisney.enigma.utils.TextUtils;
import com.chrisney.enigma.utils.Utils;

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
    public SmartArrayList<CodeBlock> subBlocks = new SmartArrayList<>();

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
    public SmartArrayList<CodePosition> subIndexes = null;

    /**
     * Type of the parent block
     */
    public CodeBlock.BlockType parentType = BlockType.Undefined;

    /**
     * Indicate if the code block is injected by programming code.
     */
    public boolean injected = false;

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
     * Update the offset and position of the block from the previous block of the list.
     * @param previousBlock Previous block of the list
     */
    public void updatePosition(CodeBlock previousBlock) {
        this.start = previousBlock.end;
        this.end = this.start + this.code.length();
        this.offset = previousBlock.offset;
        this.innerOffset = previousBlock.innerOffset;
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
     * Indicate if the block has children or not
     * @return True if the block has children
     */
    public boolean hasChildren() {
        return Utils.arrayNotEmpty(subBlocks);
    }

    /**
     * Return the first sub block none injected by programming
     * @return First sub block none injected by programming
     */
    private CodeBlock getFirstNoneInjectedBlock() {
        if (Utils.arrayNotEmpty(this.subBlocks)) {
            for (int i = 0; i < this.subBlocks.size(); i++) {
                CodeBlock block = this.subBlocks.get(i);
                if (!block.injected) return block;
            }
        }
        return null;
    }

    /**
     * Return the latest sub block none injected by programming
     * @return Latest sub block none injected by programming
     */
    private CodeBlock getLastNoneInjectedBlock() {
        if (Utils.arrayNotEmpty(this.subBlocks)) {
            for (int i = this.subBlocks.size() - 1; i >= 0; i--) {
                CodeBlock block = this.subBlocks.get(i);
                if (!block.injected) return block;
            }
        }
        return null;
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

            // Add signature code:
            CodeBlock firstBLock = getFirstNoneInjectedBlock();
            if (firstBLock != null) {
                int prefixEnd = firstBLock.innerOffset + firstBLock.start;
                String prefix = TextUtils.safeSubstring(code, 0, prefixEnd);
                if (prefix != null) sb.append(prefix);
            }

            // For each sub block:
            for (int i = 0; i < subBlocks.size(); i++) {

                CodeBlock subBlock = subBlocks.get(i);
                CodeBlock nextSubBlock = subBlocks.next(i);

                // Add sub code:
                sb.append(subBlock.toCode(level + 1));

                // Add code between current sub block and next one:
                if (!subBlock.injected && i < subBlocks.lastIndex()) {
                    int suffixStart = subBlock.innerOffset + subBlock.end;
                    int suffixEnd = (nextSubBlock != null) ? nextSubBlock.innerOffset + nextSubBlock.start : code.length();
                    String suffix = TextUtils.safeSubstring(code, suffixStart, suffixEnd);
                    if (suffix != null) sb.append(suffix);
                }
            }

            // Close the block:
            CodeBlock latestBlock = getLastNoneInjectedBlock();
            if (latestBlock != null) {
                int suffixStart = latestBlock.innerOffset + latestBlock.end;
                int suffixEnd = code.length();
                String suffix = TextUtils.safeSubstring(code, suffixStart, suffixEnd);
                if (suffix != null) sb.append(suffix);
            }

        }

        return sb.toString();
    }
}
