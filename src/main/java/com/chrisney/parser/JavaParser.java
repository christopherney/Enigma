package com.chrisney.parser;

import com.chrisney.utils.TextUtils;

import java.util.ArrayList;

public class JavaParser {

    private static final char cCurlyBracketOpen = '{';
    private static final char cCurlyBracketClose = '}';
    private static final char cParenthesisOpen = '(';
    private static final char cParenthesisClose = ')';
    private static final char cBracketOpen = '[';
    private static final char cBracketClose = ']';
    private static final char cDoubleQuote = '"';
    private static final char cComma = ',';
    private static final char cSemicolon = ';';

    private static final char cSlash = '/';
    private static final char cStar = '*';
    private static final char cEscape = '\\';
    private static final char cAnnotation = '@';

    private static final String sLineComment = "//";
    private static final String sBlockCommentStart = "/*";
    private static final String sBlockCommentEnd = "*/";

    private static final char[] charBreaks = {cCurlyBracketOpen, cCurlyBracketClose, cParenthesisOpen,
            cParenthesisClose, cBracketOpen, cBracketClose, cDoubleQuote, cComma, cSemicolon};

    private static final String sPackage = "package";
    private static final String sImport = "import";
    private static final String sClass = "class";
    private static final String sAbstract = "abstract";
    private static final String sInterface = "interface";
    private static final String sExtends = "extends";
    private static final String sImplements = "implements";
    private static final String sPublic = "public";
    private static final String sPrivate = "private";
    private static final String sProtected = "protected";
    private static final String sVoid = "void";
    private static final String sStatic = "static";
    private static final String sFinal = "final";
    private static final String sNew = "new";
    private static final String sSuper = "super";
    private static final String sTry = "try";
    private static final String sCatch = "catch";
    private static final String sIf = "if";
    private static final String sElse = "else";
    private static final String sSwitch = "switch";
    private static final String sCase = "case";
    private static final String sBreak = "break";
    private static final String sContinue = "continue";
    private static final String sFor = "for";
    private static final String sWhile = "while";
    private static final String sReturn = "return";
    private static final String sEnum = "enum";
    private static final String sTransient = "transient";
    private static final String sStrictfp = "strictfp";
    private static final String sSynchronized = "synchronized";
    private static final String sVolatile = "volatile";
    private static final String sNative = "native";

    private static final String[] keywords = {
            sPackage, sImport, sClass, sAbstract, sInterface, sExtends, sImplements, sPublic, sPrivate, sProtected,
            sVoid, sStatic, sFinal, sNew, sSuper, sTry, sCatch, sIf, sElse, sSwitch, sCase, sBreak, sContinue, sFor,
            sWhile, sReturn, sEnum, sTransient, sStrictfp, sSynchronized, sVolatile, sNative
    };

    private static final String tBool = "bool";
    private static final String tBoolObject = "Boolean";
    private static final String tByte = "byte";
    private static final String tByteObject = "Byte";
    private static final String tShort = "short";
    private static final String tShortObject = "Short";
    private static final String tInt = "int";
    private static final String tIntObject = "Integer";
    private static final String tDouble = "double";
    private static final String tDoubleObject = "Double";
    private static final String tLong = "long";
    private static final String tLongObject = "Long";
    private static final String tFloat = "float";
    private static final String tFloatObject = "Float";
    private static final String tChar = "char";
    private static final String tCharObject = "Character";
    private static final String tString = "String";
    private static final String tStringBuffer = "StringBuffer";
    private static final String tDate = "Date";
    private static final String tArray = "Array";
    private static final String tList = "List";
    private static final String tArrayList = "ArrayList";
    private static final String tMap = "Map";
    private static final String tHashMap = "HashMap";
    private static final String tHashSet = "HashSet";
    private static final String tHashTable = "HashTable";
    private static final String tStringBuilder = "StringBuilder";
    private static final String tObject = "Object";
    private static final String tNumber = "Number";
    private static final String tEnum = "Enum";

    private static final String[] types = {
            tBool, tBoolObject, tByte, tByteObject, tShort, tShortObject, tInt, tIntObject, tDouble, tDoubleObject,
            tLong, tLongObject, tFloat, tFloatObject, tChar, tCharObject, tString, tStringBuffer, tDate, tArray,
            tList, tArrayList, tMap, tHashMap, tHashSet, tHashTable, tStringBuilder, tObject, tNumber, tEnum
    };

    /**
     * Constructor
     */
    public JavaParser() {}

    /**
     * Parse JAVA File
     * @param sourceCode JAVA source code
     * @return Code blocks
     */
    public JavaCode parse(String sourceCode) {
        ArrayList<CodeString> strings = new ArrayList<>();
        ArrayList<CodeBlock> blocks = this.parse(sourceCode, null, strings);

        JavaCode javaClass =  new JavaCode(blocks, strings);
        // TEST:

        for (CodeBlock b : javaClass.getAllBlocks()) {
            System.out.println(b.toString());
        }
        /*
        for (CodeString s : javaClass.getStringValues()) {
            System.out.println(s.toString());
        }
        */
        return javaClass;
    }

    /**
     * Parse JAVA File
     * @param source JAVA source code
     * @param parent Parent block (optional)
     * @return Code blocks
     */
    private ArrayList<CodeBlock> parse(String source, CodeBlock parent, ArrayList<CodeString> strings) {

        ArrayList<CodeString> words = new ArrayList<>();
        ArrayList<CodeBlock> blocks = new ArrayList<>();

        int counterCurlyBrackets = 0;
        int counterParenthesis = 0;
        int counterBrackets = 0;

        CodeBlock.BlockType currentBlock = CodeBlock.BlockType.Undefined;

        CodeString word = null;
        CodeString string = null;
        CodeBlock block = null;

        for (int i = 0; i < source.length(); i++) {

            Character prevC = (i > 0) ? source.charAt(i - 1) : ' ';
            Character c = source.charAt(i);
            Character nextC = (i < source.length() - 1) ? source.charAt(i + 1) : ' ';

            // Track String values and comments blocks:
            if (currentBlock == CodeBlock.BlockType.Undefined) {
                if (c.equals(cDoubleQuote) && !prevC.equals(cEscape)) {
                    currentBlock = CodeBlock.BlockType.StringValue;
                } else if (c.equals(cSlash) && nextC.equals(cSlash)) {
                    currentBlock = CodeBlock.BlockType.CommentLine;
                } else if (c.equals(cSlash) && nextC.equals(cStar)) {
                    currentBlock = CodeBlock.BlockType.CommentBlock;
                } else if (c.equals(cAnnotation) && word == null) {
                    currentBlock = CodeBlock.BlockType.Annotation;
                }
            }

            // Brackets & parenthesis counters:
            if (currentBlock != CodeBlock.BlockType.CommentLine && currentBlock != CodeBlock.BlockType.CommentBlock) {
                if (c.equals(cCurlyBracketOpen)) counterCurlyBrackets++;
                if (c.equals(cCurlyBracketClose)) counterCurlyBrackets--;
                if (c.equals(cParenthesisOpen)) counterParenthesis++;
                if (c.equals(cParenthesisClose)) counterParenthesis--;
                if (c.equals(cBracketOpen)) counterBrackets++;
                if (c.equals(cBracketClose)) counterBrackets--;
            }

            // String value detection
            if  (parent == null && strings != null) {
                if (currentBlock == CodeBlock.BlockType.StringValue && string == null) {
                    string = new CodeString(i);
                } else if (string != null && currentBlock == CodeBlock.BlockType.Undefined) {
                    string.end = i + 1;
                    string.value = source.substring(string.start, string.end);
                    strings.add(string);
                    string = null;
                }
            }

            // Start new word:
            if (word == null && !TextUtils.isEmptyChar(c) && !TextUtils.inCharactersList(charBreaks, c)) {
                word = new CodeString(i);

            } else if (TextUtils.isEmptyChar(c) || TextUtils.inCharactersList(charBreaks, c) ||
                    (currentBlock == CodeBlock.BlockType.CommentBlock && c.equals(cSlash) && prevC.equals(cStar))) {

                // End of current word, then create word object:
                if (word != null) {
                    word.end = i;
                    word.value = source.substring(word.start, word.end);
                    if (currentBlock != CodeBlock.BlockType.StringValue
                            && currentBlock != CodeBlock.BlockType.CommentBlock
                            && currentBlock != CodeBlock.BlockType.CommentLine) {
                        word.isInstruction = isInstruction(word.value);
                        word.isType = isType(word.value);
                    }
                    words.add(word);

                    // New Block detection:
                    if (block == null) {
                        block = new CodeBlock();
                        block.start = i - word.value.length();
                    }

                    // Add word to current block:
                    block.words.add(word);
                }

                // Start new word:
                word = new CodeString(i);
                word.end = i;
                word.value = String.valueOf(c);
                words.add(word);
                // Add words to current block:
                if (block != null) block.words.add(word);
                word = null;
            }

            // Detect END of block
            if (block != null) {

                if ((
                        (c.equals(cSemicolon) || c.equals(cCurlyBracketClose)) // End of Line of code, End of Class, Function, Condition, Loop...
                        || (currentBlock == CodeBlock.BlockType.Annotation && (TextUtils.isEmptyChar(c) || c.equals(cParenthesisClose))) // End of Annotation
                        || (currentBlock == CodeBlock.BlockType.CommentLine && TextUtils.isReturnChar(c)) // End of Comment line
                        || (currentBlock == CodeBlock.BlockType.CommentBlock && c.equals(cSlash) && prevC.equals(cStar)) // End of Comment Block
                ) &&  counterCurlyBrackets == 0 && counterParenthesis == 0 && counterBrackets == 0) {

                    block.end = i + 1;
                    block.code = source.substring(block.start, block.end);
                    System.out.println(block.code);

                    // Analyze sub source code:
                    if (block.code.endsWith(String.valueOf(cCurlyBracketClose))) {
                        int j;
                        for (j = 0; j < block.code.length(); j++) {
                            if (block.code.charAt(j) == cCurlyBracketOpen) break;
                        }
                        String subCode = block.code.substring(j + 1, block.code.length() - 1);
                        // System.out.println(subCode);
                        block.subBlocks = this.parse(subCode, block, null);
                    }

                    // Set block type:
                    block.type = getBlockType(block);

                    // Search block name (function or classname) :
                    if (block.type == CodeBlock.BlockType.Class || block.type == CodeBlock.BlockType.Function
                            || block.type == CodeBlock.BlockType.Interface) {
                        block.name = getBlockName(block);
                    } else if (block.type == CodeBlock.BlockType.Package || block.type == CodeBlock.BlockType.Import) {
                        block.name = getPackageOrImportName(block);
                    }

                    blocks.add(block);
                    block = null;
                }
            }

            // Detect end block type:
            if (currentBlock == CodeBlock.BlockType.StringValue) {
                if (c.equals(cDoubleQuote) && !prevC.equals(cEscape))
                    currentBlock = CodeBlock.BlockType.Undefined;
            } else if (currentBlock == CodeBlock.BlockType.CommentLine) {
                if (TextUtils.isReturnChar(c))
                    currentBlock = CodeBlock.BlockType.Undefined;
            } else if (currentBlock == CodeBlock.BlockType.CommentBlock) {
                if (c.equals(cSlash) && prevC.equals(cStar))
                    currentBlock = CodeBlock.BlockType.Undefined;
            } else if (currentBlock == CodeBlock.BlockType.Annotation) {
                if ((c.equals(cParenthesisClose) || TextUtils.isReturnChar(c)) && counterParenthesis == 0)
                    currentBlock = CodeBlock.BlockType.Undefined;
            }

        } // End for loop

        return blocks;
    }

    private boolean isType(String word) {
        for (String t : types) {
            if (t.equals(word)) return true;
        }
        return false;
    }

    private boolean isInstruction(String word) {
        for (String k : keywords) {
            if (k.equalsIgnoreCase(word)) return true;
        }
        return false;
    }

    private boolean isBreakCharacter(String word) {
        if (word.length() == 1)
            return TextUtils.inCharactersList(charBreaks, word.charAt(0));
        return false;
    }

    private String getPackageOrImportName(CodeBlock block) {
        StringBuilder sb = new StringBuilder();
        for(CodeString word : block.words) {
            if (!TextUtils.isEmpty(word.value.trim()) && !isBreakCharacter(word.value) && !isBreakCharacter(word.value))
                sb.append(word.value);
        }
        return sb.toString();
    }

    private String getBlockName(CodeBlock block) {
        for(CodeString word : block.words) {
            if (!word.isInstruction && !word.isType
                    && !TextUtils.isEmpty(word.value.trim())
                    && !isBreakCharacter(word.value))
                return word.value;
        }
        return null;
    }

    private CodeString getFirstWord(ArrayList<CodeString> words) {
        for (CodeString word : words) {
            if (!TextUtils.isEmpty(word.value.trim())) return word;
        }
        return null;
    }

    private CodeBlock.BlockType getBlockType(CodeBlock block) {
        if (block.subBlocks != null && block.subBlocks.size() > 0) {
            CodeString firstWord = getFirstWord(block.words);
            if (firstWord.value.startsWith(String.valueOf(cAnnotation))) return CodeBlock.BlockType.Annotation;
            if (firstWord.value.equals(sIf) || firstWord.value.equals(sElse) || firstWord.value.equals(sSwitch))
                return CodeBlock.BlockType.Condition;
            if (firstWord.value.equals(sFor) || firstWord.value.equals(sWhile)) return CodeBlock.BlockType.Loop;
            if (firstWord.value.equals(sTry) || firstWord.value.equals(sCatch)) return CodeBlock.BlockType.TryCatch;
            for (CodeString word : block.words) {
                if (word.value.equals(sClass)) return CodeBlock.BlockType.Class;
                if (word.value.equals(sInterface)) return CodeBlock.BlockType.Interface;
                if (word.value.equals(sVoid)) return CodeBlock.BlockType.Function;
            }
            return CodeBlock.BlockType.Function;
        } else {
            for (CodeString word : block.words) {
                if (word.value.equals(sPackage)) return CodeBlock.BlockType.Package;
                if (word.value.equals(sImport)) return CodeBlock.BlockType.Import;
                if (word.value.equals(sStatic)) return CodeBlock.BlockType.Attribute;
                if (word.value.equals(sIf) || word.value.equals(sElse) ||  word.value.equals(sSwitch))
                    return CodeBlock.BlockType.Condition;
                if (word.value.equals(sFor) || word.value.equals(sWhile)) return CodeBlock.BlockType.Loop;
                if (word.value.equals(sReturn)) return CodeBlock.BlockType.Return;
                if (word.value.equals(sClass)) return CodeBlock.BlockType.Class;
                if (word.value.startsWith(String.valueOf(cAnnotation))) return CodeBlock.BlockType.Annotation;
                if (word.value.startsWith(sBlockCommentStart)) return CodeBlock.BlockType.CommentBlock;
                if (word.value.startsWith(sLineComment)) return CodeBlock.BlockType.CommentLine;
            }
        }

        return CodeBlock.BlockType.LineOfCode;
    }

}
