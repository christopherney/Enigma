package com.chrisney.enigma;

import com.chrisney.enigma.parser.CodeBlock;
import com.chrisney.enigma.parser.CodeString;
import com.chrisney.enigma.parser.JavaCode;
import com.chrisney.enigma.parser.JavaParser;
import com.chrisney.enigma.tasks.InjectCodeTask;
import com.chrisney.enigma.utils.AESUtils;
import com.chrisney.enigma.utils.TextUtils;
import com.chrisney.enigma.utils.Utils;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

public class UnitTests {

    @Test
    public void encryption() throws Exception {

        String key = Utils.randomHashKey();

        for (int i = 0; i < 100; i++) {

            int valueSize = Utils.getRandomNumberInRange(10, 100);

            String value = TextUtils.getRandomString(valueSize, TextUtils.KEY_CHARACTERS);

            String encrypted = AESUtils.encrypt(key, value);

            String decrypted = AESUtils.decrypt(key, encrypted);

            Assert.assertEquals(value, decrypted);
        }
    }

    @Test
    public void testEnigmatization() throws Exception {

        File javaFile = Utils.getFileResource("Utils.java");
        String originalCode = FileUtils.readFileToString(javaFile, "UTF-8");

        JavaParser parser = new JavaParser();
        JavaCode c = parser.parse(originalCode);

        c.addImport(InjectCodeTask.IMPORT_NAME);

        c.injectFakeKeys("DMNGZONJKU", "moyvMeX1ESB3Q");

        c.encryptStrings("LXeyH4qdtk2YqNDnLqZzX5HmPEwEwZEN", InjectCodeTask.FUNCTION_NAME);

        String securedCode = c.toCode();

        File securedJavaFile = Utils.getFileResource("Utils-secured.java");
        String targetCode = FileUtils.readFileToString(securedJavaFile, "UTF-8");

        Assert.assertEquals(targetCode, securedCode);
    }

    @Test
    public void testImportParser() {
        String code = "\nimport com.chrisney.enigma.parser.JavaParser;";
        JavaParser javaParser = new JavaParser();
        JavaCode javaCode = javaParser.parse(code);
        CodeBlock blockImport = javaCode.getAllBlocks().get(0);

        Assert.assertEquals(blockImport.type, CodeBlock.BlockType.Import);
        Assert.assertEquals(blockImport.name, "com.chrisney.enigma.parser.JavaParser");
    }

    @Test
    public void testInsertArray() {

        ArrayList<Object> array1 = Utils.toArrayList(new Integer[]{0, 1, 2, 3, 4, 5, 6});
        Utils.insertInArray(array1, 4, 99);
        ArrayList<Object> array2 = Utils.toArrayList(new Integer[]{0, 1, 2, 3, 99, 4, 5, 6});

        Assert.assertEquals(array1, array2);
    }

    @Test
    public void testJavaParser() throws Exception {

        try {
            String userHome = System.getProperty("user.home");

            String repoPath = userHome + File.separator + "android-aosp-launcher3" + File.separator;
            File repo = new File(repoPath);
            if (!repo.exists()) repo.mkdir();

            File src = new File(repoPath + File.separator + "src" + File.separator);
            if (!src.exists()) {
                Git git = Git.cloneRepository()
                        .setURI("https://android.googlesource.com/platform/packages/apps/Launcher3")
                        .setDirectory(repo)
                        .call();
            }

            Collection<File> javaFiles = Utils.listFileTree(src, ".java");

            for (File javaFile : javaFiles) {
                testParseJavaFile(javaFile);
            }

        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param javaFile Java file to parse
     */
    private void testParseJavaFile(File javaFile) throws Exception {
        try {

            String originalCode = FileUtils.readFileToString(javaFile, "UTF-8");

            JavaParser parser = new JavaParser();
            JavaCode c = parser.parse(originalCode);

            String generatedCode = c.toCode();
            Assert.assertNotNull(generatedCode);

            ArrayList<CodeString> stringValues = c.getStringValues();

            if (generatedCode.trim().equals(originalCode.trim())) {
                System.out.println("Parsing with success : " + javaFile.getAbsolutePath());
            } else {
                System.out.println("Failed parsing: " + javaFile.getAbsolutePath());
                Assert.assertEquals(originalCode.trim(), generatedCode.trim());
            }

            c.addImport("com.example.name");
            c.addFunction("public static boolean test(int value) { return value > 0; }");
            c.addAttribute("public String mValueTest = \"Hello World\";");

            c.injectFakeKeys();

            c.encryptStrings("LXeyH4qdtk2YqNDnLqZzX5HmPEwEwZEN", InjectCodeTask.FUNCTION_NAME);

            String securedCode = c.toCode();
            Assert.assertNotNull(securedCode);

        } catch (Exception ex) {
            System.out.println(ex.getClass().getName() + ": " + javaFile.getAbsolutePath());
            throw ex;
        }
    }

    @Test
    public void testRandomNumber() {
        int value;
        int min = 0, max = 10;
        for (int i = 0; i < 100; i++) {
            value = Utils.getRandomNumberInRange(min, max);
            Assert.assertTrue(value >= min);
            Assert.assertTrue(value <= max);
        }
    }

    @Test
    public void testArrayContains() {
        Integer[] arrayInt = new Integer[] {9, 32, 2324, 10, 90, 0, 3};
        boolean result;

        result = Utils.arrayContains(arrayInt, 2324);
        Assert.assertTrue(result);

        result = Utils.arrayContains(arrayInt, 11);
        Assert.assertFalse(result);

        result = Utils.arrayContains(arrayInt, 3);
        Assert.assertTrue(result);

        result = Utils.arrayContains(arrayInt, 2);
        Assert.assertFalse(result);
    }

    @Test
    public void testTextIsEmptyChar() {
        Assert.assertTrue(TextUtils.isEmptyChar(' '));
        Assert.assertTrue(TextUtils.isEmptyChar('\r'));
        Assert.assertTrue(TextUtils.isEmptyChar('\n'));
        Assert.assertTrue(TextUtils.isEmptyChar('\t'));

        Assert.assertFalse(TextUtils.isEmptyChar('a'));
        Assert.assertFalse(TextUtils.isEmptyChar('0'));
        Assert.assertFalse(TextUtils.isEmptyChar('&'));
        Assert.assertFalse(TextUtils.isEmptyChar('.'));
        Assert.assertFalse(TextUtils.isEmptyChar('.'));
        Assert.assertFalse(TextUtils.isEmptyChar(';'));
        Assert.assertFalse(TextUtils.isEmptyChar('`'));
    }

}
