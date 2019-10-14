package com.chrisney.parser;

import java.util.ArrayList;

/**
 * Map a Java file
 */
public class JavaCode {

    /**
     * All blocks of codes
     */
    private ArrayList<CodeBlock> codeBlocks;

    /**
     * All String values
     */
    private ArrayList<CodeString> codeStrings;

    /**
     * Constructor
     * @param blocks Blocks of codes
     * @param strings String values
     */
    public JavaCode(ArrayList<CodeBlock> blocks, ArrayList<CodeString> strings) {
        this.codeBlocks = blocks;
        this.codeStrings = strings;
    }

    /**
     * All String values
     * @return String values
     */
    public ArrayList<CodeString> getStringValues() {
        return this.codeStrings;
    }

    /**
     * Return all code blocks
     * @return Code blocks
     */
    public ArrayList<CodeBlock> getAllBlocks() {
        return this.getAllBlocks(this.codeBlocks);
    }

    /**
     * Return all code blocks
     * @param blocks blocks
     * @return Code blocks
     */
    private ArrayList<CodeBlock> getAllBlocks(ArrayList<CodeBlock> blocks) {
        if (blocks == null) return null;
        ArrayList<CodeBlock> result = new ArrayList<>();
        for (CodeBlock block : blocks) {
            result.add(block);
            if (block.subBlocks != null && block.subBlocks.size() > 0) {
                ArrayList<CodeBlock> r = getAllBlocks(block.subBlocks);
                if (r != null && r.size() > 0) result.addAll(r);
            }
        }
        return result;
    }


    /**
     * Return classes of JAVA file
     * @return Classes (code blocks)
     */
    public ArrayList<CodeBlock> getClasses() {
        return getBlocksByType(CodeBlock.BlockType.Class);
    }

    /**
     * Return functions of JAVA file
     * @return Functions (code blocks)
     */
    public ArrayList<CodeBlock> getFunctions() {
        return getBlocksByType(CodeBlock.BlockType.Function);
    }

    /**
     * Return code blocks from type
     * @param type Type of blocks
     * @return Code blocks
     */
    public ArrayList<CodeBlock> getBlocksByType(CodeBlock.BlockType type) {
        return getBlocksByType(type, this.codeBlocks);
    }

    /**
     * Return code blocks from type (recursive)
     * @param type Type of blocks
     * @param blocks block (recursive)
     * @return Code blocks
     */
    private ArrayList<CodeBlock> getBlocksByType(CodeBlock.BlockType type,  ArrayList<CodeBlock> blocks) {
        if (blocks == null) return null;
        ArrayList<CodeBlock> result = new ArrayList<>();
        for (CodeBlock block : blocks) {
            if (block.type == type) {
                result.add(block);
            }
            if (block.subBlocks != null && block.subBlocks.size() > 0) {
                ArrayList<CodeBlock> r = getBlocksByType(type, block.subBlocks);
                if (r != null && r.size() > 0) result.addAll(r);
            }
        }
        return result;
    }

    public String toCode() {
        StringBuilder sb = new StringBuilder();
        for(CodeBlock block : getAllBlocks()) {
            if (!block.hasParent) sb.append(block.toCode());
        }
        return sb.toString();
    }
}
