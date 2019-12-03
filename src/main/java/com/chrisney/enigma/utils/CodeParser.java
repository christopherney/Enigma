package com.chrisney.enigma.utils;

import com.chrisney.enigma.tasks.InjectCodeTask;
import org.gradle.api.DefaultTask;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeParser {

    private static final String separator = System.lineSeparator();

    private static final char cEscape = '\\';
    private static final char cComment = '/';
    private static final char cCommentBlock = '*';
    private static final char cAnnotation = '@';
    private static final char cStringDelimiter = '"';
    private static final char cCurlyBracketOpen = '{';
    private static final char cCurlyBracketClose = '}';

    private static final String regexImport = "(import\\s+[a-zA-Z0-9\\.]+\\s*\\;)";
    private static final String regexPackage = "(package\\s+[a-zA-Z0-9\\.]+\\s*\\;)";

    public static final String importString = "import " + InjectCodeTask.PACKAGE_NAME + "." + InjectCodeTask.CLASS_NAME + ";";
    public static final String functionName = InjectCodeTask.CLASS_NAME + ".enigmatization";

    private static final String FAKE_KEY_CHARACTERS ="ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789qwertyuiopasdfghjklzxcvbnm#$*!?";
    private static final String FAKE_PARAM_CHARACTERS ="ABCDEFGHIJKLMNOPQRSTUVWXYZ_";

    public DefaultTask encryptTask;
    public String customFunctionName;

    private CodeBlock currentCodeBlock = CodeBlock.Undefined;
    private enum CodeBlock {
        Undefined,
        Class,
        CommentLine,
        CommentBlock,
        Annotation,
        Function,
        StringValue
    }

    private boolean debug = false;

    public CodeParser(boolean debug) {
        this.debug = debug;
    }

    public String encode(String key, String content, boolean injectFakeKeys) throws Exception {
        content = addImport(content);
        content = secureString(key, content);
        if (injectFakeKeys)
            content = injectFakeKeys(content);
        // System.out.println(content);
        return content;
    }



    private String secureString(String key, String content) throws Exception {

        int startString = 0;
        int endString = 0;
        int parenthesisCounter = 0;

        currentCodeBlock = CodeBlock.Undefined;
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < content.length(); i++) {

            // Characters pointers:
            if (i == 0 || i == content.length() - 1) continue;
            Character pC = content.charAt(i - 1);
            Character c = content.charAt(i);
            Character nC = content.charAt(i + 1);

            // Comments detector (escape comments):
            if (currentCodeBlock == CodeBlock.Undefined) {
                if (c.equals(cComment) && nC.equals(cCommentBlock)) {
                    currentCodeBlock = CodeBlock.CommentBlock;
                    // System.out.println(" - Start Comment Block");
                }
            }  else if (currentCodeBlock == CodeBlock.CommentBlock) {
                if (c.equals(cComment) && pC.equals(cCommentBlock)) {
                    currentCodeBlock = CodeBlock.Undefined;
                    // System.out.println(" - End Comment Block");
                }
            }
            if (currentCodeBlock == CodeBlock.CommentBlock) continue;

            // Comments detector (escape comments):
            if (currentCodeBlock == CodeBlock.Undefined) {
                if (c.equals(cComment) && nC.equals(cComment)) {
                    currentCodeBlock = CodeBlock.CommentLine;
                    // System.out.println(" - Start Comment Line");
                }
            } else if (currentCodeBlock == CodeBlock.CommentLine) {
                if (c.equals('\r') || c.equals('\n')) {
                    currentCodeBlock = CodeBlock.Undefined;
                    // System.out.println(" - End Comment Line");
                }
            }
            if (currentCodeBlock == CodeBlock.CommentLine) continue;

            // Annotation detector (escape annotation):
            if (currentCodeBlock == CodeBlock.Undefined) {
                if (c.equals(cAnnotation)) {
                    currentCodeBlock = CodeBlock.Annotation;
                    // System.out.println(" - Start Annotation");
                }
            } else if (currentCodeBlock == CodeBlock.Annotation) {
                if (c.equals('(')) { parenthesisCounter++; }
                else if (c.equals(')')) {
                    parenthesisCounter--;
                    if (parenthesisCounter == 0) {
                        currentCodeBlock = CodeBlock.Undefined;
                        // System.out.println(" - End Annotation");
                    }
                }
            }
            if (currentCodeBlock == CodeBlock.Annotation) continue;

            // Detect start of new String value:
            if (currentCodeBlock == CodeBlock.Undefined
                    && c.equals(cStringDelimiter) && !pC.equals(cEscape)) {

                currentCodeBlock = CodeBlock.StringValue;
                // System.out.println(" - Start String Value"); // (" + pC + c + nC + ")");
                startString = i;
                builder.append(content, endString == 0 ? 0 : endString + 1, startString);

            // Detect end of String value
            } else if (currentCodeBlock == CodeBlock.StringValue &&
                    c.equals(cStringDelimiter) && !pC.equals(cEscape)) {

                endString = i;

                String value = content.substring(startString + 1, endString);
                value = value.replace("\\\"", "\"");
                value = value.replace("\\\\", "\\");

                String encrypted = encryptString(key, value);

                if (this.encryptTask == null) {
                    byte[] enc = AESUtils.toByte(encrypted);
                    builder.append(functionName);
                    builder.append("(new byte[]");
                    builder.append(bytesToCode(enc));
                    builder.append(")");
                } else {
                    builder.append(customFunctionName);
                    builder.append("(\"");
                    builder.append(encrypted);
                    builder.append("\")");
                }

                currentCodeBlock = CodeBlock.Undefined;

                if (this.debug)
                    System.out.println(" - String to encrypt : \"" + value + "\"");
            }
        }

        // Add rest of content file:
        builder.append(content, endString == 0 ? 0 : endString + 1, content.length());

        return builder.toString();
    }

    private String encryptString(String key, String value) throws Exception {
        // System.out.println(value);
        if (this.encryptTask == null) {
            if (key == null) throw new Exception("Hash Key undefined!");
           return AESUtils.encrypt(key, value);
        } else {
            Method encryptMethod = this.encryptTask.getClass().getMethod("encrypt", String.class);
            return (String) encryptMethod.invoke(this.encryptTask, value);
        }
    }

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

    private String injectFakeKeys(String content) {
        StringBuilder builder = new StringBuilder();
        int firstCurlyBracket = -1;
        int curlyBracketsCounter = 0;
        Character lastChar = null;

        String fakeParamName = TextUtils.getRandomString(10, FAKE_PARAM_CHARACTERS);

        for (int i = 0; i < content.length(); i++) {
            Character c =  content.charAt(i);

            // Curly bracket counter:
            if (firstCurlyBracket != -1) {
                if (c.equals(cCurlyBracketOpen)) {
                    curlyBracketsCounter++;
                } else if (c.equals(cCurlyBracketClose)) {
                    curlyBracketsCounter--;
                }
            }

            // Inject attribute right after class declaration:
            if (c.equals(cCurlyBracketOpen) && firstCurlyBracket == -1) {
                firstCurlyBracket = i;
                currentCodeBlock = CodeBlock.Class;
                builder.append(content, 0, i + 1);
                builder.append(System.lineSeparator());
                builder.append(System.lineSeparator());

                builder.append("\t");
                builder.append(getFakeAttribute(fakeParamName));

            // Detect Function code block start:
            } else if (c.equals(cCurlyBracketOpen) && lastChar != null && lastChar.equals(')')) {

                currentCodeBlock = CodeBlock.Function;

            // Inject fake code at the end of the first function:
            } else if (currentCodeBlock == CodeBlock.Function &&
                    c.equals(cCurlyBracketClose) && curlyBracketsCounter == 0) {

                currentCodeBlock = CodeBlock.Class;

                builder.append(content, firstCurlyBracket + 1, i);

                builder.append(System.lineSeparator());
                builder.append("\t\t");
                builder.append(getFakeCode(fakeParamName));

                builder.append(System.lineSeparator());
                builder.append(content, i - 1, content.length());
                return builder.toString();
            }

            if (!TextUtils.isEmptyChar(c))
                lastChar = c;

        } // End for loop

        return content;
    }

    private String getFakeCode(String paramName) {
        return "if (" + paramName + ".isEmpty()) " + paramName + ".getClass().toString();";
    }

    private String getFakeAttribute(String paramName) {
        int sizeValue = Utils.getRandomNumberInRange(10, 30);
        String randomValue = TextUtils.getRandomString(sizeValue, FAKE_KEY_CHARACTERS);
        return "public static final String " + paramName + " = \"" + randomValue + "\";";
    }

    private String addImport(String content) {

        StringBuilder builder = new StringBuilder();

        Pattern pattern = Pattern.compile(regexImport, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(content);

        int i = 0;
        int start = -1;
        int end = -1;
        while (matcher.find()) {
            // System.out.println("Start: " + matcher.start(i) + " End: " + matcher.end(i));
            // System.out.println("Group " + i + ": " + matcher.group(i));
            start = matcher.start(i);
            end = matcher.end(i);
        }

        if (start == -1 || end == -1) {
            pattern = Pattern.compile(regexPackage, Pattern.MULTILINE);
            matcher = pattern.matcher(content);
            while (matcher.find()) {
                start = matcher.start(i);
                end = matcher.end(i);
            }
        }

        if (start > -1 && end > -1) {
            builder.append(content, 0, end);
            builder.append(separator).append(separator)
                    .append(importString)
                    .append(separator).append(separator);
            builder.append(content, end + 1, content.length());
        }

        if (builder.length() > 0) {
            return builder.toString();
        } else {
            return content;
        }
    }

}
