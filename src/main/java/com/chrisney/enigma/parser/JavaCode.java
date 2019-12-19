package com.chrisney.enigma.parser;

import com.chrisney.enigma.utils.AESUtils;
import com.chrisney.enigma.utils.TextUtils;
import com.chrisney.enigma.utils.Utils;
import org.gradle.api.DefaultTask;

import java.lang.reflect.Method;
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
     * Original source code
     */
    private String sourceCode;

    public static final String KEY_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789qwertyuiopasdfghjklzxcvbnm#$*!?";
    public static final String PARAM_CHARACTERS ="ABCDEFGHIJKLMNOPQRSTUVWXYZ_";

    /**
     * Constructor
     * @param blocks Blocks of codes
     * @param strings String values
     * @param sourceCode Original source code
     */
    public JavaCode(ArrayList<CodeBlock> blocks, ArrayList<CodeString> strings, String sourceCode) {
        this.codeBlocks = blocks;
        this.codeStrings = strings;
        this.sourceCode = sourceCode;
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
     * Return of block of code from a start & end characters indexes.
     * @param blocks Blocks to search
     * @param start Start index character
     * @param end End index character
     * @return Block if found, otherwise null
     */
    private CodeBlock getBlockBetween(ArrayList<CodeBlock> blocks, int start, int end) {
        for (CodeBlock block : blocks) {
            if (block.subBlocks != null && block.subBlocks.size() > 0) {
                CodeBlock b = getBlockBetween(block.subBlocks, start, end);
                if (b != null) return b;
            } else if (block.getStart() <= start && end <= block.getEnd()) {
                return block;
            }
        }
        return null;
    }

    /**
     * Return Imports of JAVA file
     * @return Functions (code blocks)
     */
    public ArrayList<CodeBlock> getImports() {
        return getBlocksByType(CodeBlock.BlockType.Import);
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

    /**
     * Add a package name import.
     * @param packageName Package name to import
     */
    public void addImport(String packageName) {

        String code = "\nimport " + packageName.trim() + ";";

        JavaParser javaParser = new JavaParser();
        JavaCode javaCode = javaParser.parse(code);
        CodeBlock blockImport = javaCode.getImports().get(0);

        if (!addBlockAtPosition(this.codeBlocks, blockImport, InsertPosition.AtTheEnd, CodeBlock.BlockType.Import)) {
            addBlockAtPosition(this.codeBlocks, blockImport, InsertPosition.RightAfter, CodeBlock.BlockType.Package);
        }
    }

    /**
     * Add an attribute in the class
     * @param attributeCode Attribute to add
     * @throws ClassNotFoundException No Class found in the soucre code
     */
    public void addAttribute(String attributeCode) throws ClassNotFoundException {
        addAttribute(attributeCode, null);
    }

    public void addAttribute(String attributeCode, String className) throws ClassNotFoundException {
        CodeBlock blockClass = null;
        for (CodeBlock block : getBlocksByType(CodeBlock.BlockType.Class)) {
            if (className == null || className.equals(block.name))
                blockClass = block;
        }
        if (blockClass == null) throw new ClassNotFoundException("Class '" + className + "' not found!");

        JavaParser javaParser = new JavaParser();
        JavaCode javaCode = javaParser.parse("\n\n    " + attributeCode.trim());
        CodeBlock block = javaCode.getAllBlocks().get(0);
        block.hasParent = true;
        block.parentType = CodeBlock.BlockType.Class;

        if (!addBlockAtPosition(blockClass.subBlocks, block, InsertPosition.AtTheEnd, CodeBlock.BlockType.Attribute)) {
            Utils.insertInArray(blockClass.subBlocks, 0, block);
        }
    }

    /**
     * Add a function into the default class
     * @param functionCode Function to add
     * @throws ClassNotFoundException Throw is the main class is not found
     */
    public void addFunction(String functionCode) throws ClassNotFoundException {
        addFunction(functionCode, null);
    }

    /**
     * Add a function into a specific class
     * @param functionCode Function to add
     * @param className Class name where to add the function
     * @throws ClassNotFoundException Throw is the main class is not found
     */
    public void addFunction(String functionCode, String className) throws ClassNotFoundException {
        CodeBlock blockClass = null;
        for (CodeBlock block : getBlocksByType(CodeBlock.BlockType.Class)) {
            if (className == null || className.equals(block.name))
                blockClass = block;
        }
        if (blockClass == null) throw new ClassNotFoundException("Class '" + className + "' not found!");

        JavaParser javaParser = new JavaParser();
        JavaCode javaCode = javaParser.parse("\n\n    " + functionCode.trim());
        CodeBlock block = javaCode.getAllBlocks().get(0);
        block.hasParent = true;
        block.type = CodeBlock.BlockType.Function;
        block.parentType = blockClass.type;

        blockClass.subBlocks.add(block);
    }

    /**
     * Insert positions for block of code
     */
    private enum InsertPosition {
        JustBefore,
        RightAfter,
        AtTheEnd
    }

    /**
     * Add a block of code into the entire code.
     * @param blocks Code to edit
     * @param newBlock Block of code to insert
     * @param position Position to insert new block
     * @param type Type to insert new block
     */
    private boolean addBlockAtPosition(ArrayList<CodeBlock> blocks, CodeBlock newBlock, InsertPosition position, CodeBlock.BlockType type) {
        if (position == InsertPosition.AtTheEnd) {
            for (int i = blocks.size() - 1; i > 0; i--) {
                CodeBlock block = blocks.get(i);
                if (block.type == type) {
                    Utils.insertInArray(blocks, i + 1, newBlock);
                    return true;
                }
            }
        } else {
            for (int i = 0; i < blocks.size(); i++) {
                CodeBlock block = blocks.get(i);
                if (block.type == type) {
                    if (position == InsertPosition.JustBefore) {
                        Utils.insertInArray(blocks, i, newBlock);
                        return true;
                    } else if (position == InsertPosition.RightAfter) {
                        Utils.insertInArray(blocks, i + 1, newBlock);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Inject fake code: fake attribute
     */
    public void injectFakeKeys() {
        try {
            ArrayList<CodeBlock> functions = getFunctions();
            if (functions.size() > 0) {
                String fakeParamName = TextUtils.getRandomString(10, PARAM_CHARACTERS);

                String fakeAttribute = getFakeAttribute(fakeParamName);
                addAttribute(fakeAttribute);

                CodeBlock fakeCode = getFakeCode(fakeParamName);
                CodeBlock blockFunction = functions.get(0);

                CodeBlock lastLineOfCode = blockFunction.subBlocks.get(blockFunction.subBlocks.size() - 1);
                if (lastLineOfCode.type == CodeBlock.BlockType.Return) {
                    Utils.insertInArray(blockFunction.subBlocks, 0, fakeCode);
                } else {
                    blockFunction.subBlocks.add(fakeCode);
                }
            }
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Generate a fake Java code which is calling the fake attribute (avoid code clean by ProGuard)
     * @param paramName  Name of this fake attribute
     * @return Fake Java code
     */
    private CodeBlock getFakeCode(String paramName) {
        String code = "\n        if (" + paramName + ".isEmpty()) " + paramName + ".getClass().toString();";
        JavaParser javaParser = new JavaParser();
        JavaCode javaCode = javaParser.parse(code);
        CodeBlock block = javaCode.getAllBlocks().get(0);
        block.hasParent = true;
        return block;
    }

    /**
     * Generate a fake Java attribute
     * @param paramName Name of this fake attribute
     * @return Fake Java attribute
     */
    private String getFakeAttribute(String paramName) {
        int sizeValue = Utils.getRandomNumberInRange(10, 30);
        String randomValue = TextUtils.getRandomString(sizeValue, KEY_CHARACTERS);
        return "public static final String " + paramName + " = \"" + randomValue + "\";";
    }

    /**
     * Encrypt all string values
     * @param key Secrete key for encryption
     * @param functionName Name of the decryption method
     * @throws Exception Encryption error
     */
    public void encryptStrings(String key, String functionName) throws Exception {

        int stringOffset = 0;
        CodeBlock block = null;

        // For each String value:
        for (CodeString cs : getStringValues()) {

            // Escape switch/case value (not authorized by Java compiler):
            if (cs.isCaseValue) continue;

            // Search the code block which contains the string value:
            if (block == null || !(block.getStart() <= cs.start && cs.end <= block.getEnd())) {
                block = getBlockBetween(this.codeBlocks, cs.start, cs.end);
                stringOffset = 0;
            }

            // If code block found:
            if (block != null) {
                int bStart = cs.start - (block.start + block.offset);
                int bEnd = cs.end - (block.start + block.offset);
                // System.out.println(cs.value + " == " + block.code.substring(bStart, bEnd));

                // Get the string value en encrypt it:
                String value = cs.value.substring(1, cs.value.length() - 1);
                String encrypted = encryptString(value, key, functionName, null);

                // Inject the Enigma signature function:
                StringBuilder builder = new StringBuilder();
                builder.append(block.code, 0, bStart + stringOffset);
                builder.append(encrypted);
                builder.append(block.code, bEnd  + stringOffset, block.code.length());

                // Compute the length difference due to the code modification:
                int originalLength = cs.value.length();
                int encryptedLength = encrypted.length();
                int lengthDiff = (encryptedLength - originalLength);

                // System.out.println(builder.toString());
                block.code = builder.toString();
                // block.end += lengthDiff;

                stringOffset += lengthDiff;
            }
        }
    }

    /**
     * Encrypt a string value, to Enigma ciphering style
     * @param value String value to encrypt
     * @param key Secrete key encryption
     * @param functionName Name of the decryption method
     * @param encryptTask Optional external decryption Task name (not yet implemented)
     * @return String value encrypted with Enigma ciphering style
     * @throws Exception Encryption error
     */
    private String encryptString(String value, String key, String functionName, DefaultTask encryptTask) throws Exception {
        StringBuilder builder = new StringBuilder();
        value = value.replace("\\\"", "\"");
        value = value.replace("\\\\", "\\");

        String encrypted = encryptString(key, value, encryptTask);

        if (encryptTask == null) {
            byte[] enc = AESUtils.toByte(encrypted);
            builder.append(functionName);
            builder.append("(new byte[]");
            builder.append(bytesToCode(enc));
            builder.append(")");
        } else {
            builder.append(functionName);
            builder.append("(\"");
            builder.append(encrypted);
            builder.append("\")");
        }

        return builder.toString();
    }

    /**
     * Encrypt string value
     * @param key Secrete key
     * @param value String value
     * @param encryptTask Experimental !
     * @return Encrypted string
     * @throws Exception Missing encryption secrete key
     */
    private String encryptString(String key, String value, DefaultTask encryptTask) throws Exception {
        // System.out.println(value);
        if (encryptTask == null) {
            if (key == null) throw new Exception("Hash Key undefined!");
            return AESUtils.encrypt(key, value);
        } else {
            Method encryptMethod = encryptTask.getClass().getMethod("encrypt", String.class);
            return (String) encryptMethod.invoke(encryptTask, value);
        }
    }

    /**
     * Convert bytes value, to JAVA code format
     * @param bytes Bytes value
     * @return JAVA code
     */
    private String bytesToCode(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        for (int i = 0; i < bytes.length; i++) {
            byte b = bytes[i];
            builder.append(b);
            if (i < bytes.length - 1) builder.append(", ");
        }
        builder.append("}");
        return builder.toString();
    }

    /**
     * Source code formatted
     * @return Print the source code formatted
     */
    public String toCode() {
        StringBuilder sb = new StringBuilder();
        for(CodeBlock block : getAllBlocks()) {
            if (!block.hasParent) sb.append(block.toCode());
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.codeBlocks != null) {
            for(CodeBlock block : this.codeBlocks) {
                sb.append(block.code);
            }
        }
        return sb.toString();
    }
}
