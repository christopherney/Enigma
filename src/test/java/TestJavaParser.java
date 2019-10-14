import com.chrisney.parser.JavaCode;
import com.chrisney.parser.JavaParser;

public class TestJavaParser {

    public static void main(String[] args) {
        String code;
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

        code = "package com.proto.helloworld.helpers;\n" +
                "\n" +
                "import androidx.annotation.NonNull;\n" +
                "import androidx.annotation.Nullable;\n" +
                "\n" +
                "import javax.crypto.Cipher;\n" +
                "import javax.crypto.SecretKey;\n" +
                "import javax.crypto.spec.IvParameterSpec;\n" +
                "import javax.crypto.spec.SecretKeySpec;\n" +
                "\n" +
                "public class AESUtils {\n" +
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
                "\n" +
                "    public static String decrypt(@NonNull String key, @NonNull String encrypted)\n" +
                "            throws Exception {\n" +
                "        byte[] keyValue  = keyToBytes(key);\n" +
                "        byte[] enc = toByte(encrypted);\n" +
                "        byte[] result = decrypt(keyValue, enc);\n" +
                "        return new String(result);\n" +
                "    }\n" +
                "\n" +
                "    private static byte[] getRawKey(byte[] keyValue) throws Exception {\n" +
                "        SecretKey key = new SecretKeySpec(keyValue, \"AES\");\n" +
                "        byte[] raw = key.getEncoded();\n" +
                "        return raw;\n" +
                "    }\n" +
                "\n" +
                "    private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {\n" +
                "        SecretKey skeySpec = new SecretKeySpec(raw, \"AES\");\n" +
                "        Cipher cipher = Cipher.getInstance(\"AES/CBC/PKCS5Padding\");\n" +
                "        byte[] iv = new byte[cipher.getBlockSize()];\n" +
                "        IvParameterSpec ivParams = new IvParameterSpec(iv);\n" +
                "        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivParams);\n" +
                "        return cipher.doFinal(clear);\n" +
                "    }\n" +
                "\n" +
                "    private static byte[] decrypt(byte[] keyValue, byte[] encrypted)\n" +
                "            throws Exception {\n" +
                "        SecretKey skeySpec = new SecretKeySpec(keyValue, \"AES\");\n" +
                "        Cipher cipher = Cipher.getInstance(\"AES/CBC/PKCS5Padding\");\n" +
                "        byte[] iv = new byte[cipher.getBlockSize()];\n" +
                "        IvParameterSpec ivParams = new IvParameterSpec(iv);\n" +
                "        cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivParams);\n" +
                "        return cipher.doFinal(encrypted);\n" +
                "    }\n" +
                "\n" +
                "    public static byte[] toByte(String hexString) {\n" +
                "        int len = hexString.length() / 2;\n" +
                "        byte[] result = new byte[len];\n" +
                "        for (int i = 0; i < len; i++)\n" +
                "            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2),\n" +
                "                    16).byteValue();\n" +
                "        return result;\n" +
                "    }\n" +
                "\n" +
                "    public static String toHex(byte[] buf) {\n" +
                "        if (buf == null)\n" +
                "            return \"\";\n" +
                "        StringBuffer result = new StringBuffer(2 * buf.length);\n" +
                "        for (int i = 0; i < buf.length; i++) {\n" +
                "            appendHex(result, buf[i]);\n" +
                "        }\n" +
                "        return result.toString();\n" +
                "    }\n" +
                "\n" +
                "    private final static String HEX = \"0123456789ABCDEF\";\n" +
                "\n" +
                "    private static void appendHex(StringBuffer sb, byte b) {\n" +
                "        sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));\n" +
                "    }\n" +
                "}\n";

        code = "package com.proto.helloworld.helpers;\n" +
                "\n" +
                "import android.annotation.SuppressLint;\n" +
                "import android.util.Log;\n" +
                "\n" +
                "/**\n" +
                " * Utilities\n" +
                " * @Author Christopher Ney\n" +
                " */\n" +
                "public class Utils {\n" +
                "\n" +
                "    private static final String TAG = \"Utils\";\n" +
                "\n" +
                "    // Test line comment\n" +
                "\n" +
                "    public Utils() {\n" +
                "        Log.d(TAG, \"initialize utils\");\n" +
                "    }\n" +
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
                "    @Override\n" +
                "    public String toString() {\n" +
                "        return super.toString();\n" +
                "    }\n" +
                "}\n";

        try {
            JavaParser parser = new JavaParser();
            JavaCode c = parser.parse(code);
            String stringCode = c.toCode();
            System.out.println(stringCode);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
