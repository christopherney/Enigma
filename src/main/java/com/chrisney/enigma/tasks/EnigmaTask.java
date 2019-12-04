package com.chrisney.enigma.tasks;

import com.chrisney.enigma.parser.JavaCode;
import com.chrisney.enigma.parser.JavaParser;
import com.chrisney.enigma.utils.AESUtils;
import com.chrisney.enigma.utils.TextUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.apache.commons.io.FileUtils;

import javax.inject.Inject;
import java.io.File;

/**
 * Gradle Task to parse JAVA source code and encrypt string values.
 * @author Christopher Ney
 */
public class EnigmaTask extends AbstractTask {

    public String hash;
    public String[] ignoredClasses = null;
    public String[] classes = null;
    public String customFunction = null;
    public DefaultTask customEncryptionTask = null;
    public boolean injectFakeKeys = true;

    @Inject
    public EnigmaTask() {
        super();
    }

    @TaskAction
    public void encrypt() throws Exception {
        if (!enabled) return;
        if (!checkSCM()) return;

        if (TextUtils.isEmpty(this.hash) && this.customEncryptionTask == null) {
            System.out.println("⚠️ Missing Hash value to encrypt files (or Custom Encryption Task)");
            return;
        } else if (!TextUtils.isEmpty(this.hash) && this.hash.length() < AESUtils.MIN_KEY_SIZE) {
            System.out.println("⚠️ The secrete 'com.chrisney.enigma.hash' must at least contains " + AESUtils.MIN_KEY_SIZE + " characters!");
            return;
        }

        if (!backupDirExists()) {
            System.out.println("⚠️ Impossible to execute 'encrypt' task if backup directory not exists!");
            return;
        }

        for (File javaFile : this.getAllJavaFiles()) {
            if (!isSelected(javaFile) || isIgnored(javaFile)) {
                System.out.println("\uD83D\uDEAB️ " + javaFile.getName() + " ignored");
            } else {
                encryptJavaFile(javaFile);
            }
        }
    }

    private boolean isSelected(File javaFile) {
        if (this.classes != null) {
            for (String ignored : this.classes) {
                String path = ignored.replace(".", File.separator)
                        .replace( File.separator + "java", ".java");
                if (javaFile.getAbsolutePath().endsWith(path)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    private boolean isIgnored(File javaFile) {
        if (this.ignoredClasses != null) {
            for (String ignored : this.ignoredClasses) {
                String path = ignored.replace(".", File.separator)
                        .replace( File.separator + "java", ".java");
                if (javaFile.getAbsolutePath().endsWith(path)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void encryptJavaFile(File srcFile) throws Exception {

        if (isEnigmaFile(srcFile)) return;
        if (isEnigmatized(srcFile)) {
            System.out.println("⚠️ Cannot process a file already encrypted: " + srcFile);
            return;
        }

        String contents = FileUtils.readFileToString(srcFile, "UTF-8");

        JavaParser p = new JavaParser();
        JavaCode code = p.parse(contents);

        code.addImport(InjectCodeTask.PACKAGE_NAME + "." + InjectCodeTask.CLASS_NAME);
        code.encryptStrings(hash, InjectCodeTask.FUNCTION_NAME);

        if (injectFakeKeys) code.injectFakeKeys();

        String contentSecured = code.toCode();
        FileUtils.writeStringToFile(srcFile, contentSecured, "UTF-8");

        System.out.println("\uD83D\uDD10 " + srcFile.getName() + " encrypted");
    }
}
