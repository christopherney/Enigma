import com.chrisney.enigma.parser.CodeBlock;
import com.chrisney.enigma.parser.CodeString;
import com.chrisney.enigma.parser.JavaCode;
import com.chrisney.enigma.parser.JavaParser;
import com.chrisney.enigma.tasks.InjectCodeTask;
import com.chrisney.enigma.utils.Utils;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;

import java.io.File;
import java.util.ArrayList;

public class TestJavaParser {

    public static void main(String[] args) {
        testImportParser();
        testInsertArray();
        testJavaParser();
    }

    public static void testImportParser() {
        String code = "\nimport com.chrisney.enigma.parser.JavaParser;";
        JavaParser javaParser = new JavaParser();
        JavaCode javaCode = javaParser.parse(code);
        CodeBlock blockImport = javaCode.getAllBlocks().get(0);

        Assert.assertEquals(blockImport.type, CodeBlock.BlockType.Import);
        Assert.assertEquals(blockImport.name, "com.chrisney.enigma.parser.JavaParser");
    }

    public static void testInsertArray() {

        ArrayList<Object> array1 = Utils.toArrayList(new Integer[]{0, 1, 2, 3, 4, 5, 6});
        Utils.insertInArray(array1, 4, 99);
        ArrayList<Object> array2 = Utils.toArrayList(new Integer[]{0, 1, 2, 3, 99, 4, 5, 6});

        Assert.assertEquals(array1, array2);
    }

    public static void testJavaParser() {
        try {

            // File javaFile = Utils.getFileResource("Utils.java");
            // File javaFile = Utils.getFileResource("FlingAnimation.java");
            // File javaFile = Utils.getFileResource("Workspace.java");
            // File javaFile = Utils.getFileResource("fadeAndRemoveEmptyScreen.java");
            // File javaFile = Utils.getFileResource("BaseFlags.java");
            // File javaFile = Utils.getFileResource("ToggleFlag.java");
            // File javaFile = Utils.getFileResource("GridBackupTable.java");
            // File javaFile = Utils.getFileResource("Folder.java");
            // File javaFile = Utils.getFileResource("Annotations.java");
            // File javaFile = Utils.getFileResource("AnonymousInnerClass.java");
            // File javaFile = Utils.getFileResource("WorkspaceLayoutManager.java");
            File javaFile = Utils.getFileResource("Launcher.java");

            String originalCode = FileUtils.readFileToString(javaFile, "UTF-8");

            JavaParser parser = new JavaParser();
            JavaCode c = parser.parse(originalCode);

            String generatedCode = c.toCode();

            ArrayList<CodeString> stringValues = c.getStringValues();
            if (stringValues != null) {
                System.out.println(stringValues.size());
            }

            if (generatedCode.trim().equals(originalCode.trim())) {
                System.out.println("Parsing with success!");
            } else {
                System.out.println("Failed parsing!");
            }

            c.addImport("com.example.name");
            c.addFunction("public static boolean test(int value) { return value > 0; }");
            c.addAttribute("public String mValueTest = \"Hello World\";");

            c.injectFakeKeys();

            c.encryptStrings("LXeyH4qdtk2YqNDnLqZzX5HmPEwEwZEN", InjectCodeTask.FUNCTION_NAME);

            String securedCode = c.toCode();
            System.out.println(securedCode);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
