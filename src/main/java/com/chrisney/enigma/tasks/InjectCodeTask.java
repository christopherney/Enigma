package com.chrisney.enigma.tasks;

import com.chrisney.enigma.utils.TextUtils;
import org.apache.commons.io.FileUtils;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;

/**
 * Gradle Task to inject Enigma source code.
 * @author Christopher Ney
 */
public class InjectCodeTask extends AbstractTask {

    class AddFileRunnable implements Runnable {
        private final String src;
        private final int iteration;

        AddFileRunnable(int iteration, String src) {
            this.iteration = iteration;
            this.src = src;
        }

        @Override
        public void run() {
            try {
                addFile(this.iteration, this.src);
            } catch (IOException e) {
                throw new RuntimeException("Failed to AddFile, src: " + src, e);
            }
        }
    }

    public static final String PACKAGE_NAME = "com.chrisney.enigma";
    public static final String CLASS_NAME = "EnigmaUtils";
    public static final String FUNCTION_NAME = InjectCodeTask.CLASS_NAME + ".enigmatization";
    public static final String IMPORT_NAME = "import " + InjectCodeTask.PACKAGE_NAME + "." + InjectCodeTask.CLASS_NAME + ";";

    public static final String SOURCE_CODE_PACKAGE_STATEMENT = "package " + PACKAGE_NAME;
    public static final String getSourceCodePackageStatement(int iteration) {
        return SOURCE_CODE_PACKAGE_STATEMENT + "_" + iteration + ";\n\n";
    }

    public static final String SOURCE_CODE = "import javax.crypto.Cipher;\n" +
            "import javax.crypto.SecretKey;\n" +
            "import javax.crypto.spec.SecretKeySpec;\n" +
            "import javax.crypto.spec.IvParameterSpec;\n" +
            "\n" +
            "public class " + CLASS_NAME + " {\n" +
            "   private final static int[] data = {0, 0};\n" +
            "   public static String enigmatization(byte[] enc) {\n" +
            "        try {\n" +
            "            byte[] keyValue  = keyToBytes(data);\n" +
            "            byte[] result = decrypt(keyValue, enc);\n" +
            "            return new String(result);\n" +
            "        } catch (Exception e) {\n" +
            "            e.printStackTrace();\n" +
            "        }\n" +
            "        return null;\n" +
            "    }\n" +
            "    private static byte[] decrypt(byte[] keyValue, byte[] encrypted)\n" +
            "            throws Exception {\n" +
            "        SecretKey skeySpec = new SecretKeySpec(keyValue, \"AES\");\n" +
            "        Cipher cipher = Cipher.getInstance(\"AES/CBC/PKCS5Padding\");\n" +
            "        byte[] iv = new byte[cipher.getBlockSize()];\n" +
            "        IvParameterSpec ivParams = new IvParameterSpec(iv);\n" +
            "        cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivParams);\n" +
            "        return cipher.doFinal(encrypted);\n" +
            "    }\n" +
            "    private static byte[] keyToBytes(int[] key) {\n" +
            "        int size = 16 * (key.length / 16);\n" +
            "        StringBuilder builder = new StringBuilder();\n" +
            "        for (int i = 0; i < size; i++) {\n" +
            "            builder.append((char) key[i]);\n" +
            "        }\n" +
            "        return builder.toString().getBytes();\n" +
            "    }\n" +
            "}\n";

    public String hash;
    public String customFunction = null;

    @Inject
    public InjectCodeTask() {
        super();
    }

    private void addFile(int iteration, String src) throws IOException {
        File packageName = new File(src + File.separator + PACKAGE_NAME.replace(".",  File.separator) + "_" + iteration);
        if (!packageName.exists()) packageName.mkdir();

        File codeFile = new File(packageName.getAbsolutePath() + File.separator + CLASS_NAME + ".java");
        String data = encodeHash(getSourceCodePackageStatement(iteration) + SOURCE_CODE);
        System.out.println("addFile, adding with package statement: " + getSourceCodePackageStatement(iteration));
        FileUtils.writeStringToFile(codeFile, data, "UTF-8");
    }

    @TaskAction
    public void addCode() throws IOException {
        if (!enabled) return;
        if (!checkSCM()) return;

        if (!TextUtils.isEmpty(customFunction)) return;

        if (TextUtils.isEmpty(this.hash)) {
            System.out.println("⚠️ Missing Hash value to inject Enigma code");
            return;
        }

        if(pathSrcs.length <= 0) {
            new AddFileRunnable(0, pathSrc).run();
        }
        else {
            int index = 0;
            for(String src : pathSrcs) {
                new AddFileRunnable(index, src).run();
                index++;
            }
        }

        System.out.println("✏️ Add Enigma code");
    }

    private String encodeHash(String source) {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        for (int i = 0; i < this.hash.length(); i++) {
            char c = this.hash.charAt(i);
            builder.append((int)c);
            if (i < hash.length() - 1) builder.append(", ");
        }
        builder.append("}");
        return source.replace("{0, 0}", builder.toString());
    }
}
