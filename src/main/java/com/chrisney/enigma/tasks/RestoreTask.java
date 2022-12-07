package com.chrisney.enigma.tasks;

import org.apache.commons.io.FileUtils;
import org.gradle.api.tasks.TaskAction;
import org.gradle.internal.Pair;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;

/**
 * Gradle Task to restore original JAVA files after compilation.
 * @author Christopher Ney
 */
public class RestoreTask extends AbstractTask {

    class RemoveCodeRunnable implements Runnable {
        private final String src;

        RemoveCodeRunnable(String src) {
            this.src = src;
        }

        @Override
        public void run() {
            try {
                removeCode(this.src);
            } catch (IOException e) {
                throw new RuntimeException("Failed to RemoveCode, src: " + src, e);
            }
        }
    }

    @Inject
    public RestoreTask() {
        super();
    }

    @TaskAction
    public void restore() throws IOException {
        if (!enabled) return;
        if (!checkSCM()) return;

        if (backupDirExists()) {
            for (Pair<Integer, File> pair : this.getAllJavaFiles()) {
                File javaFile = pair.right;
                assert javaFile != null;
                this.restoreFile(javaFile);
            }
        } else {
            System.out.println("⚠️ There is no backup to restore!");
        }
        removeEnigmaCode();
    }

    private void removeCode(String src) throws IOException {
        File codePackage = new File(src + File.separator + InjectCodeTask.PACKAGE_NAME.replace(".", File.separator));
        FileUtils.deleteDirectory(codePackage);
        System.out.println("\uD83E\uDDF9 Remove Enigma code: " + codePackage.getAbsolutePath());
    }

    private void removeEnigmaCode() {
        if(pathSrcs.length <= 0) {
            new RemoveCodeRunnable(pathSrc).run();
        }
        else {
            for(String src : pathSrcs) {
                new RemoveCodeRunnable(src).run();
            }
        }
    }

    private void restoreFile(File file) throws IOException {

        String srcFile = file.getAbsolutePath().replace(rootProject, "");
        if (isEnigmaFile(file)) return;

        if (isEnigmatized(file)) {
            File backup = new File(backupDir() + srcFile);
            if (backup.exists() && backup.length() > 0) {
                FileUtils.copyFile(backup, file);
                System.out.println("♻️ Restore: " + srcFile);
            } else {
                System.out.println("⚠️ There is no backup for: " + srcFile);
            }
        } else {
            System.out.println("⚠️ Cannot overwrite an unencrypted file: " + srcFile);
        }
    }

}
