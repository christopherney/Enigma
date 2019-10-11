package com.chrisney.tasks;

import org.apache.commons.io.FileUtils;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;

public class BackupTask extends AbstractTask {

    @Inject
    public BackupTask() {
        super();
    }

    @TaskAction
    public void backup() throws IOException {
        if (!enabled) return;
        if (!checkSCM()) return;
        this.removeBackupDir();
        this.createBackupDir();
        for (File javaFile : this.getAllJavaFiles()) {
            this.backupFile(javaFile);
        }
    }

    private void backupFile(File file) throws IOException {
        String srcFile = file.getAbsolutePath().replace(rootProject, "");
        if (isEnigmaFile(file)) return;

        if (!isEnigmatized(file) && file.length() > 0) {
            File backup = new File(backupDir() + srcFile);
            FileUtils.copyFile(file, backup);
            System.out.println("\uD83D\uDCBE Backup: " + srcFile);
        } else {
            System.out.println("⚠️ Cannot backup an encrypted file: " + srcFile);
        }
    }

}
