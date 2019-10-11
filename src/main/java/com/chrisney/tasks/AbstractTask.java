package com.chrisney.tasks;

import com.chrisney.utils.CodeParser;
import org.apache.commons.io.FileUtils;
import org.gradle.api.DefaultTask;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class AbstractTask extends DefaultTask {

    private static final String BACKUP_DIR = "enigma-backup";
    private static final String SVN_FOLDER = ".svn";
    private static final String MERCURIAL_FOLDER = ".hg";
    private static final String GIT_FOLDER = ".git";
    private static final String GIT_IGNORE = "*\n" +
            "*/\n" +
            "!.gitignore";

    public boolean enabled = true;
    public boolean debug = false;
    public String rootProject;
    public String pathSrc;

    public AbstractTask() {
        this.setGroup("enigma");
    }

    protected Collection<File> getAllJavaFiles() {
        return this.listFileTree(new File(pathSrc), ".java");
    }

    protected Collection<File> getAllXmlFiles() {
        return this.listFileTree(new File(pathSrc), ".xml");
    }

    protected Collection<File> listFileTree(File dir, String fileType) {
        Set<File> fileTree = new HashSet<>();
        if(dir == null || dir.listFiles() == null) {
            return fileTree;
        }
        File[] files = dir.listFiles();
        if (files != null) {
            for (File entry : files) {
                if (entry.isFile() && entry.getName().endsWith(fileType)) {
                    fileTree.add(entry);
                } else {
                    fileTree.addAll(listFileTree(entry, fileType));
                }
            }
        }
        return fileTree;
    }

    protected boolean checkSCM() {
        boolean result = hasGit() || hasSubversion() || hasMercurial();
        if (!result) {
            System.out.println("⚠️ The project has no Source Code Management. Please setup one (Git, SVN, Mercurial) before use Enigma plugin!");
        }
        return result;
    }

    private boolean hasSubversion() {
        return new File(rootProject + File.separator + SVN_FOLDER).exists();
    }

    private boolean hasMercurial() {
        return new File(rootProject + File.separator + MERCURIAL_FOLDER).exists();
    }

    private boolean hasGit() {
        return new File(rootProject + File.separator + GIT_FOLDER).exists();
    }

    protected String backupDir() {
        return rootProject + File.separator + BACKUP_DIR + File.separator;
    }

    protected boolean backupDirExists() {
        return new File(backupDir()).exists();
    }

    protected void removeBackupDir() throws IOException {
        File backup = new File(backupDir());
        FileUtils.deleteDirectory(backup);
    }

    protected boolean createBackupDir() throws IOException {
        File backupDir = new File(backupDir());
        File gitIgnore = new File(backupDir() + File.separator + ".gitignore");
        if (!backupDir.exists()) {
            if (backupDir.mkdir())
                FileUtils.writeStringToFile(gitIgnore, GIT_IGNORE, "UTF-8");
        } else if (!gitIgnore.exists()) {
            FileUtils.writeStringToFile(gitIgnore, GIT_IGNORE, "UTF-8");
        }
        return true;
    }

    protected boolean isEnigmaFile(File scrFile) {
        return scrFile.getName().endsWith(InjectCodeTask.CLASS_NAME + ".java");
    }

    protected boolean isEnigmatized(File srcFile) throws IOException {
        String contents = FileUtils.readFileToString(srcFile, "UTF-8");
        return contents.contains(CodeParser.importString) || contents.contains(CodeParser.functionName);
    }
}