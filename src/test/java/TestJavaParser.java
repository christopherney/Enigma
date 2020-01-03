import com.chrisney.enigma.parser.CodeBlock;
import com.chrisney.enigma.parser.CodeString;
import com.chrisney.enigma.parser.JavaCode;
import com.chrisney.enigma.parser.JavaParser;
import com.chrisney.enigma.tasks.InjectCodeTask;
import com.chrisney.enigma.utils.Utils;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

public class TestJavaParser {

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

        } catch (Exception ex) {
            System.out.println(ex.getClass().getName() + ": " + javaFile.getAbsolutePath());
            throw ex;
        }
    }

}
