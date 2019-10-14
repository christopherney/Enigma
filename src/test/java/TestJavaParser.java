import com.chrisney.parser.JavaCode;
import com.chrisney.parser.JavaParser;

public class TestJavaParser {

    public static void main(String[] args) {
        String code;
        code = "package com.proto.helloworld.helpers;\n" +
                "\n" +
                "import android.annotation.SuppressLint;\n" +
                "\n" +
                "/**\n" +
                " * Utilities\n" +
                " * @Author Christopher Ney\n" +
                " */\n" +
                "public class Utils {\n" +
                "\n" +
                "    // Test line comment\n" +
                "\n" +
                "    public static int addition(int a, int b) {\n" +
                "        return a + b;\n" +
                "    }\n" +
                "\n" +
                "    @SuppressWarnings(\"unchecked\")\n" +
                "    public static String addQuotes(String value) {\n" +
                "        return \"\\\"\" + value + \"\\\"\";\n" +
                "    }\n" +
                "\n" +
                "    @SuppressLint(\"NewApi\") public static String cleanQuote(String value) {\n" +
                "        // Remove quotes:\n" +
                "        return value.replace(\"\\\"\", \"\");\n" +
                "    }\n" +
                "\n" +
               //  "    @Override\n" +
                "    @Override public String toString() {\n" +
                "        return super.toString();\n" +
                "    }\n" +
                "}\n";

        code =  "public class AESUtils {\n" +
                "\n" +
                "    public static byte[] keyToBytes(String key) {\n" +
                "        int size = 16 * (key.length() / 16);\n" +
                "        String k = key.substring(0, size);\n" +
                "        return k.getBytes();\n" +
                "    }\n" +
                "\n" +
                "    public static String encrypt(@NonNull String key, @NonNull String cleartext)\n" +
                "            throws Exception {\n" +
                "        byte[] keyValue  = keyToBytes(key);\n" +
                "        byte[] rawKey = getRawKey(keyValue);\n" +
                "        byte[] result = encrypt(rawKey, cleartext.getBytes());\n" +
                "        return toHex(result);\n" +
                "    }\n" +
                "}\n";

        try {
            JavaParser parser = new JavaParser();
            parser.parse(code);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
