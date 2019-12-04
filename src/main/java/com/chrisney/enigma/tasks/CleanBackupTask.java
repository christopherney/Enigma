package com.chrisney.enigma.tasks;

import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;
import java.io.IOException;

/**
 * Gradle Task to clean Backup directory.
 * @author Christopher Ney
 */
public class CleanBackupTask extends AbstractTask {

    @Inject
    public CleanBackupTask() {
        super();
    }

    @TaskAction
    public void cleanBackup() throws IOException {
        if (!enabled) return;
        if (!checkSCM()) return;
        this.removeBackupDir();
        System.out.println("\uD83E\uDDF9 Backup clean: " + backupDir());
    }

}
