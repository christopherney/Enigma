import com.chrisney.parser.JavaCode;
import com.chrisney.parser.JavaParser;

public class TestJavaParser {

    public static void main(String[] args) {
        String code = "package com.proto.helloworld.helpers;\n" +
                "\n" +
                "import android.annotation.SuppressLint;\n" +
                "\n" +
                "/**\n" +
                " * Utilities\n" +
                " * @Author Christopher Ney\n" +
                " */\n" +
                "public class Utils {\n" +
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
                "    @SuppressLint(\"NewApi\")\n" +
                "    public static String cleanQuote(String value) {\n" +
                "        // Remove quotes:\n" +
                "        return value.replace(\"\\\"\", \"\");\n" +
                "    }\n" +
                "}\n";

        code = "@SuppressWarnings(\"unchecked\")\n" +
                "    public static String addQuotes(String value) {\n" +
                "        return \"\\\"\" + value + \"\\\"\";\n" +
                "    }";

        JavaParser parser = new JavaParser();
        parser.parse(code);
    }
}
