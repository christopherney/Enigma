package com.chrisney.enigma.parser;

import com.chrisney.enigma.utils.SmartArrayList;
import com.chrisney.enigma.utils.TextUtils;
import com.chrisney.enigma.utils.Utils;

import java.util.ArrayList;

/**
 * JAVA Simple Parser: parse JAVA source code and detects String values
 * @author Christopher Ney
 */
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
    private static final char cColon = ':';

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
    private static final String sThrows = "throws";

    private static final String[] keywords = {
            sPackage, sImport, sClass, sAbstract, sInterface, sExtends, sImplements, sPublic, sPrivate, sProtected,
            sVoid, sStatic, sFinal, sNew, sSuper, sTry, sCatch, sIf, sElse, sSwitch, sCase, sBreak, sContinue, sFor,
            sWhile, sReturn, sEnum, sTransient, sStrictfp, sSynchronized, sVolatile, sNative, sThrows
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
    private static final String tException = "Exception";

    private static final String[] types = {
            tBool, tBoolObject, tByte, tByteObject, tShort, tShortObject, tInt, tIntObject, tDouble, tDoubleObject,
            tLong, tLongObject, tFloat, tFloatObject, tChar, tCharObject, tString, tStringBuffer, tDate, tArray,
            tList, tArrayList, tMap, tHashMap, tHashSet, tHashTable, tStringBuilder, tObject, tNumber, tEnum, tException
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
        ArrayList<CodeBlock> blocks = this.parse(sourceCode, null, strings, 0);
        return new JavaCode(blocks, strings, sourceCode);
    }

    /**
     * Parse JAVA File
     * @param source JAVA source code
     * @param parent Parent block (optional)
     * @return Code blocks
     */
    private ArrayList<CodeBlock> parse(String source, CodeBlock parent, ArrayList<CodeString> strings, int offset) {

        ArrayList<CodeBlock> blocks = new ArrayList<>();

        int counterCurlyBrackets = 0;
        int counterParenthesis = 0;
        int counterBrackets = 0;
        int counterAnnotationParenthesis = 0;

        CodeBlock.BlockType currentBlock = CodeBlock.BlockType.Undefined;

        CodeString word = null;
        CodeString string = null;
        CodeBlock block = null;

        for (int i = 0; i < source.length(); i++) {

            Character prevChar = (i > 0) ? source.charAt(i - 1) : ' ';
            Character curChar = source.charAt(i);
            Character nextChar = (i < source.length() - 1) ? source.charAt(i + 1) : ' ';
            Character nextNoneEmptyChar = getNextNoneEmptyChar(source, i + 1);

            // Track String values and comments blocks:
            if (currentBlock == CodeBlock.BlockType.Undefined) {
                if (curChar.equals(cDoubleQuote) && !prevChar.equals(cEscape)) {
                    currentBlock = CodeBlock.BlockType.StringValue;
                } else if (curChar.equals(cSlash) && nextChar.equals(cSlash)) {
                    currentBlock = CodeBlock.BlockType.CommentLine;
                } else if (curChar.equals(cSlash) && nextChar.equals(cStar)) {
                    currentBlock = CodeBlock.BlockType.CommentBlock;
                } else if (curChar.equals(cAnnotation) && counterAnnotationParenthesis == 0 && word == null) {
                    currentBlock = CodeBlock.BlockType.Annotation;
                }
            } else if (currentBlock == CodeBlock.BlockType.StringValue) {
                if (curChar.equals(cDoubleQuote) && !prevChar.equals(cEscape))
                    currentBlock = CodeBlock.BlockType.Undefined;
            }

            // Brackets & parenthesis counters:
            if (currentBlock != CodeBlock.BlockType.CommentLine && currentBlock != CodeBlock.BlockType.CommentBlock) {
                if (curChar.equals(cCurlyBracketOpen)) counterCurlyBrackets++;
                if (curChar.equals(cCurlyBracketClose)) counterCurlyBrackets--;
                if (curChar.equals(cParenthesisOpen)) counterParenthesis++;
                if (curChar.equals(cParenthesisClose)) counterParenthesis--;
                if (curChar.equals(cBracketOpen)) counterBrackets++;
                if (curChar.equals(cBracketClose)) counterBrackets--;
                if (currentBlock == CodeBlock.BlockType.Annotation) {
                    if (curChar.equals(cParenthesisOpen)) counterAnnotationParenthesis++;
                    if (curChar.equals(cParenthesisClose)) counterAnnotationParenthesis--;
                }
            }

            // String value detection
            if  (parent == null && strings != null) {
                if (currentBlock == CodeBlock.BlockType.StringValue && string == null) {
                    string = new CodeString(i);
                } else if (string != null && currentBlock != CodeBlock.BlockType.StringValue) {
                    string.end = i + 1;
                    string.value = source.substring(string.start, string.end);
                    string.isCaseValue = (block != null) && isSwitchCaseValue(block.words, string.value, nextNoneEmptyChar);
                    strings.add(string);
                    string = null;
                }
            }

            // Start new word:
            if (word == null && !TextUtils.inCharactersList(charBreaks, curChar)) {
                word = new CodeString(i);

            }
            if (TextUtils.isEmptyChar(curChar) || TextUtils.inCharactersList(charBreaks, curChar) ||
                    isEndBlockComment(currentBlock, curChar, prevChar)
            ) {

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

                    // New Block detection:
                    if (block == null) {
                        block = new CodeBlock();
                        block.offset = offset;
                        if  (parent != null) {
                            block.hasParent = true;
                            block.parentType = parent.type;
                        }
                        block.start = i - word.value.length();
                    }

                    // Add word to current block:
                    if (!word.value.isEmpty()) block.words.add(word);
                }

                // Start new word:
                word = new CodeString(i);
                word.end = i;
                word.value = String.valueOf(curChar);

                // Add words to current block:
                if (block != null && !word.value.isEmpty()) block.words.add(word);
                word = null;
            }

            // Detect END of block
            if (block != null) {

                if ((
                        isEndOfCodeBlock(currentBlock, curChar, nextNoneEmptyChar) // End of Line of code, End of Class, Function, Condition, Loop...
                        || isEndAnnotation(currentBlock, curChar, nextNoneEmptyChar, counterParenthesis) // End of Annotation
                        || (isEndCommentLine(currentBlock, curChar)) // End of Comment line
                        || isEndBlockComment(currentBlock, curChar, prevChar) // End of Comment Block
                ) && counterCurlyBrackets == 0 && counterParenthesis == 0 && counterBrackets == 0) {

                    block.end = i + 1;
                    block.code = source.substring(block.start, block.end);

                    // Analyze sub source code:
                    if (!CodeBlock.isComment(currentBlock))
                        block.subIndexes = getSubBlockIndexes(block);

                    if (Utils.arrayNotEmpty(block.subIndexes)) {

                        for (CodePosition subIndexes : block.subIndexes) {

                            // Safe substring: important if method doesn't contains any sub code
                            // Example:
                            //      public void onStateTransitionStart(LauncherState toState) {}
                            String subCode = TextUtils.safeSubstring(block.code, subIndexes.start, subIndexes.end);

                            // Set block type:
                            block.type = getBlockType(block, true);
                            // Search block name and properties:
                            parseBlockProperties(block);

                            // Compute the sub block offset (chars index):
                            int subBlockOffset = block.start + block.offset + subIndexes.start;

                            // Parse the sub block:
                            if (subCode != null) {
                                ArrayList<CodeBlock> subBlocks = this.parse(subCode, block, null, subBlockOffset);
                                for (CodeBlock subBlock : subBlocks) {
                                    subBlock.innerOffset = subIndexes.start;
                                }
                                block.subBlocks.addAll(subBlocks);
                            } else {
                                // If no sub code, then remove sub indexes:
                                block.subIndexes = null;
                            }
                        }

                    } else {
                        // Set block type:
                        block.type = getBlockType(block, false);
                        // Search block name and properties:
                        parseBlockProperties(block);
                    }

                    // Detect 'constructor':
                    if (block.type == CodeBlock.BlockType.Function && parent != null
                            && parent.type == CodeBlock.BlockType.Class
                            && block.name != null && block.name.equals(parent.name)) {
                        block.type = CodeBlock.BlockType.Constructor;
                    }

                    // Detect empty spaces (line breaks...)
                    if (blocks.size() > 0) {
                        CodeBlock lastBlock = blocks.get(blocks.size() - 1);
                        CodeBlock emptyBlock = new CodeBlock();
                        emptyBlock.start = lastBlock.end;
                        emptyBlock.end = block.start;
                        emptyBlock.code = source.substring(emptyBlock.start, emptyBlock.end);
                        System.out.print(emptyBlock.code);
                    }

                    blocks.add(block);
                    block = null;
                }
            }

            // Detect end block type:
            if (currentBlock == CodeBlock.BlockType.CommentLine) {
                if (TextUtils.isReturnChar(curChar))
                    currentBlock = CodeBlock.BlockType.Undefined;
            } else if (currentBlock == CodeBlock.BlockType.CommentBlock) {
                if (curChar.equals(cSlash) && prevChar.equals(cStar))
                    currentBlock = CodeBlock.BlockType.Undefined;
            } else if (currentBlock == CodeBlock.BlockType.Annotation) {
                if (isEndAnnotation(currentBlock, curChar, nextNoneEmptyChar, counterAnnotationParenthesis))
                    currentBlock = CodeBlock.BlockType.Undefined;
            }

        } // End for loop

        return blocks;
    }

    private SmartArrayList<CodePosition> getSubBlockIndexes(CodeBlock block) {
        SmartArrayList<CodePosition> indexes = new SmartArrayList<>();
        int j;
        int counterCurlyBracket = 0;
        char previousNoneEmptyChar = ' ';
        for (j = 0; j < block.code.length(); j++) {

            char c = block.code.charAt(j);

            if (c == cCurlyBracketOpen) counterCurlyBracket++;
            if (c == cCurlyBracketClose) counterCurlyBracket--;

            if (c == cCurlyBracketOpen && counterCurlyBracket == 1 && previousNoneEmptyChar != cBracketClose) {
                indexes.add(new CodePosition(j + 1));
            } else if (c == cCurlyBracketClose && counterCurlyBracket == 0 && indexes.size() > 0) {
                indexes.get(indexes.size() -1).end = j - 1;
            }

            if (!TextUtils.isEmptyChar(c)) previousNoneEmptyChar = c;
        }
        return indexes;
    }

    /**
     * Detect end of Line of code, End of Class, Function, Condition, Loop...
     * @param currentBlock Current block type
     * @param curChar Current character
     * @param nextNoneEmptyChar Next character non empty (if exists)
     * @return True if end of block
     */
    private boolean isEndOfCodeBlock(CodeBlock.BlockType currentBlock, Character curChar, Character nextNoneEmptyChar) {
        return !CodeBlock.isComment(currentBlock)
            && currentBlock != CodeBlock.BlockType.StringValue
            && (
                    curChar.equals(cSemicolon) ||
                    (curChar.equals(cCurlyBracketClose) && !nextNoneEmptyChar.equals(cSemicolon))
            );
    }

    /**
     * Detect end of block annotation
     * @param currentBlock Current block type
     * @param curChar Current character
     * @param nextNoneEmptyChar Next character non empty (if exists)
     * @param parenthesisCounter Parenthesis counter
     * @return True if end of block annotation is detected
     */
    private boolean isEndAnnotation(CodeBlock.BlockType currentBlock, Character curChar, Character nextNoneEmptyChar, int parenthesisCounter) {
        if (currentBlock == CodeBlock.BlockType.Annotation && parenthesisCounter == 0) {
            if (curChar.equals(cParenthesisClose) || (TextUtils.isEmptyChar(curChar) && Character.isAlphabetic(nextNoneEmptyChar))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Detect end of line of Comment
     * @param currentBlock Current block type
     * @param curChar Current character
     * @return True if end of line of Comment is detected
     */
    private boolean isEndCommentLine(CodeBlock.BlockType currentBlock, Character curChar) {
        return currentBlock == CodeBlock.BlockType.CommentLine && TextUtils.isReturnChar(curChar);
    }

    /**
     * Detect end of block Comment
     * @param currentBlock Current block type
     * @param curChar Current character
     * @param prevChar Previous character
     * @return True if end of block comment is detected
     */
    private boolean isEndBlockComment(CodeBlock.BlockType currentBlock, Character curChar, Character prevChar) {
        return currentBlock == CodeBlock.BlockType.CommentBlock && curChar.equals(cSlash) && prevChar.equals(cStar);
    }

    /**
     * Return the none empty character
     * @param data String data
     * @param index  current index
     * @return Next none empty character
     */
    private char getNextNoneEmptyChar(String data, int index) {
        char c = ' ';
        while (index < data.length()) {
            if (!TextUtils.isEmptyChar(data.charAt(index))) {
                c = data.charAt(index);
                break;
            }
            index++;
        }
        return c;
    }

    /**
     * Return True is the keyword is a native Java type
     * @param word Keyword
     * @return True is the keyword is a native Java type
     */
    private boolean isType(String word) {
        for (String t : types) {
            if (t.equals(word)) return true;
        }
        return false;
    }

    /**
     * Check if the string value is a switch/case value condition, example: case "RoundedSquare":
     * @param words Words before the String value to test
     * @param value String value to test
     * @param nextNoneEmptyChar Next none empty character
     * @return True if the string value is a switch/case value condition
     */
    private boolean isSwitchCaseValue(ArrayList<CodeString> words, String value, char nextNoneEmptyChar) {
        String sDoubleQuote = String.valueOf(cDoubleQuote);
        if (nextNoneEmptyChar != cColon || !value.startsWith(sDoubleQuote) || !value.endsWith(sDoubleQuote)) return false;

        CodeString prevNonEmptyWord = null;
        for (int i = words.size() - 1; i > 0; i--) {
            prevNonEmptyWord = words.get(i);
            String v = prevNonEmptyWord.value;
            if (!TextUtils.isEmpty(v) && !TextUtils.isSpace(v) && !TextUtils.isEqualsToChar(v, cDoubleQuote)) break;
        }

        if (prevNonEmptyWord == null) return false;
        return sCase.equals(prevNonEmptyWord.value);
    }

    /**
     * Return True is the keyword is a Java instruction
     * @param word Keyword
     * @return True is the keyword is a Java instruction
     */
    private static boolean isInstruction(String word) {
        for (String k : keywords) {
            if (k.equalsIgnoreCase(word)) return true;
        }
        return false;
    }

    private static boolean isBreakCharacter(String word) {
        if (word.length() == 1)
            return TextUtils.inCharactersList(charBreaks, word.charAt(0));
        return false;
    }

    private void parseBlockProperties(CodeBlock block) {
        if (block.type == CodeBlock.BlockType.Class || block.type == CodeBlock.BlockType.Interface) {
            parseClassName(block);
        } else if (block.type == CodeBlock.BlockType.Function) {
            parseFunction(block);
        } else if (block.type == CodeBlock.BlockType.Attribute) {
            parseModifier(block);
        } else if (block.type == CodeBlock.BlockType.Package || block.type == CodeBlock.BlockType.Import) {
            parsePackageOrImportName(block);
        } else if (block.type == CodeBlock.BlockType.Annotation) {
            parseAnnotation(block);
        }
    }

    private static void parseAnnotation(CodeBlock block) {
        String annotationName = null;
        String at = String.valueOf(cAnnotation);
        for(CodeString word : block.words) {
            if (word.value.startsWith(at)) {
                annotationName = word.value.replace(at, "");
                break;
            }
        }
        block.name = annotationName;
    }

    private void parseFunction(CodeBlock block) {
        String typeName = null;
        for(CodeString word : block.words) {
            if (isBreakCharacter(word.value)) break;
            parseModifier(block, word.value);
            if (!word.isInstruction && !TextUtils.isEmpty(word.value.trim())) {
                if (typeName == null) {
                    typeName = word.value;
                } else {
                    block.name = word.value;
                    block.returnType = typeName;
                    return;
                }
            }
        }
        block.name = typeName;
        block.returnType = null;
    }

    private static void parsePackageOrImportName(CodeBlock block) {
        StringBuilder sb = new StringBuilder();
        for(CodeString word : block.words) {
            if (isBreakCharacter(word.value)) break;
            if (!TextUtils.isEmpty(word.value.trim()) && !isBreakCharacter(word.value) && !isInstruction(word.value))
                sb.append(word.value);
        }
        block.name = sb.toString();
    }

    private boolean hasModifier(ArrayList<CodeString> words) {
        if (words == null) return false;
        CodeString firstWord = getFirstNoneEmptyWord(words);
        if (firstWord == null) return false;
        return (sPublic.equals(firstWord.value) || sPrivate.equals(firstWord.value) || sProtected.equals(firstWord.value));
    }

    private void parseModifier(CodeBlock block) {
        CodeString firstWord = getFirstNoneEmptyWord(block.words);
        if (firstWord != null) parseModifier(block, firstWord.value);
    }

    private void parseModifier(CodeBlock block, String word) {
        if (sPublic.equals(word)) block.modifier = CodeBlock.Modifier.Public;
        if (sPrivate.equals(word)) block.modifier = CodeBlock.Modifier.Private;
        if (sProtected.equals(word)) block.modifier = CodeBlock.Modifier.Protected;
    }

    private void parseClassName(CodeBlock block) {
        for(CodeString word : block.words) {
            if (isBreakCharacter(word.value)) break;
            parseModifier(block, word.value);
            if (!word.isInstruction && !word.isType && !TextUtils.isEmpty(word.value.trim()))
                block.name = word.value;
        }
    }

    private boolean isEmptyWord(CodeString word) {
        return word.value == null || TextUtils.isEmpty(word.value.trim());
    }

    private CodeString getFirstNoneEmptyWord(ArrayList<CodeString> words) {
        for (CodeString word : words) {
            if (!isEmptyWord(word)) return word;
        }
        return null;
    }

    private CodeBlock.BlockType getBlockType(CodeBlock block, boolean hasSubCode) {
        // has nested code (function, condition, class...)
        if (hasSubCode) {

            CodeString firstWord = getFirstNoneEmptyWord(block.words);
            if (firstWord == null) return CodeBlock.BlockType.Undefined;

            if (firstWord.value.startsWith(String.valueOf(cAnnotation))) return CodeBlock.BlockType.Annotation;
            if (firstWord.value.equals(sIf) || firstWord.value.equals(sElse) || firstWord.value.equals(sSwitch))
                return CodeBlock.BlockType.Condition;
            if (firstWord.value.equals(sFor) || firstWord.value.equals(sWhile)) return CodeBlock.BlockType.Loop;
            if (firstWord.value.equals(sTry) || firstWord.value.equals(sCatch)) return CodeBlock.BlockType.TryCatch;

            for (CodeString word : block.words) {
                if (word.value.equals(sClass)) return CodeBlock.BlockType.Class;
                if (word.value.equals(sInterface)) return CodeBlock.BlockType.Interface;
            }

            if (!CodeBlock.isClass(block.parentType))
                return CodeBlock.BlockType.AnonymousInnerClass;

            return CodeBlock.BlockType.Function;
        } else {

            CodeString firstWord = getFirstNoneEmptyWord(block.words);
            if (firstWord == null) return CodeBlock.BlockType.Undefined;

            if (firstWord.value.equals(sPackage)) return CodeBlock.BlockType.Package;
            if (firstWord.value.equals(sImport)) return CodeBlock.BlockType.Import;
            if (firstWord.value.startsWith(String.valueOf(cAnnotation))) return CodeBlock.BlockType.Annotation;
            if (firstWord.value.equals(sIf) || firstWord.value.equals(sElse) || firstWord.value.equals(sSwitch))
                return CodeBlock.BlockType.Condition;
            if (firstWord.value.equals(sFor) || firstWord.value.equals(sWhile)) return CodeBlock.BlockType.Loop;
            if (firstWord.value.equals(sReturn)) return CodeBlock.BlockType.Return;
            if (firstWord.value.startsWith(sBlockCommentStart)) return CodeBlock.BlockType.CommentBlock;
            if (firstWord.value.startsWith(sLineComment)) return CodeBlock.BlockType.CommentLine;

            for (CodeString word : block.words) {
                if (isEmptyWord(word)) continue;
                if (word.value.equals(sStatic)) return CodeBlock.BlockType.Attribute;
            }
        }

        return (hasModifier(block.words)) ? CodeBlock.BlockType.Attribute : CodeBlock.BlockType.LineOfCode;
    }

}
