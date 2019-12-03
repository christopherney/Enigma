import com.chrisney.enigma.parser.CodeBlock;
import com.chrisney.enigma.parser.JavaCode;
import com.chrisney.enigma.parser.JavaParser;
import com.chrisney.enigma.tasks.InjectCodeTask;
import com.chrisney.enigma.utils.Utils;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;

import java.io.File;
import java.util.ArrayList;

import static org.gradle.internal.impldep.com.google.common.io.Resources.getResource;

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
        System.out.print(blockImport);
    }

    public static void testInsertArray() {

        ArrayList array1 = Utils.toArrayList(new Integer[]{0, 1, 2, 3, 4, 5, 6});
        ArrayList array2 = Utils.toArrayList(new Integer[]{0, 1, 2, 3, 99, 4, 5, 6});

        Utils.insertInArray(array1, 4, 99);

        Assert.assertEquals(array1, array2);
    }

    public static void testJavaParser() {
        try {

            File javaFile = Utils.getFileResource("fake-java-file.txt");
            String code = FileUtils.readFileToString(javaFile, "UTF-8");

            JavaParser parser = new JavaParser();
            JavaCode c = parser.parse(code);

            String stringCode = c.toCode(false);
            System.out.println(stringCode);

            c.addImport("com.example.package.name");
            c.addFunction("public static boolean test(int value) { return value > 0; }");
            c.addAttribute("public String mValueTest = \"Hello World\";");

            c.injectFakeKeys();

            c.encryptStrings("LXeyH4qdtk2YqNDnLqZzX5HmPEwEwZEN", InjectCodeTask.FUNCTION_NAME);

            stringCode = c.toCode(false);
            System.out.println(stringCode);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
