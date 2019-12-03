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

    /**
     * Return the collection of all JAVA files (*.java) found in the app source folder
     * @return Collection of all JAVA files (*.java)
     */
    protected Collection<File> getAllJavaFiles() {
        return this.listFileTree(new File(pathSrc), ".java");
    }

    /**
     *
     * Return the collection of all XML files (*.xml) found in the app source folder
     * @return Collection of all XML files (*.xml)
     */
    protected Collection<File> getAllXmlFiles() {
        return this.listFileTree(new File(pathSrc), ".xml");
    }

    /**
     * Recursive algorithm to list all files in directory
     * @param dir Root directory to scan
     * @param fileType Filter of file type to search (extension)
     * @return All files found
     */
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

    /**
     * Check if an SCM (Source Code Management) tool is setup or not not.
     * @return True if an SCM tool is found
     */
    protected boolean checkSCM() {
        boolean result = hasGit() || hasSubversion() || hasMercurial();
        if (!result) {
            System.out.println("⚠️ The project has no Source Code Management. Please setup one (Git, SVN, Mercurial) before use Enigma plugin!");
        }
        return result;
    }

    /**
     * Check if an Subversion tool is setup or not not.
     * @return True if an Subversion tool is found
     */
    private boolean hasSubversion() {
        return new File(rootProject + File.separator + SVN_FOLDER).exists();
    }

    /**
     * Check if an Mercurial tool is setup or not not.
     * @return True if an Mercurial tool is found
     */
    private boolean hasMercurial() {
        return new File(rootProject + File.separator + MERCURIAL_FOLDER).exists();
    }

    /**
     * Check if an Git tool is setup or not not.
     * @return True if an Git tool is found
     */
    private boolean hasGit() {
        return new File(rootProject + File.separator + GIT_FOLDER).exists();
    }

    /**
     * Return the file path of 'backup' directory of the current project
     * @return File path of 'backup' directory
     */
    protected String backupDir() {
        return rootProject + File.separator + BACKUP_DIR + File.separator;
    }

    /**
     * Check if 'backup' directory exists or not
     * @return True if exists
     */
    protected boolean backupDirExists() {
        return new File(backupDir()).exists();
    }

    /**
     * Remove the existing 'backup' directory
     * @throws IOException If an I/O exception
     */
    protected void removeBackupDir() throws IOException {
        File backup = new File(backupDir());
        FileUtils.deleteDirectory(backup);
    }

    /**
     * Create the 'backup' directory (with .gitignore config file)
     * @return True if success
     * @throws IOException If an I/O exception
     */
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

    /**
     * Check if file if Enigma file
     * @param srcFile JAVA file to test
     * @return True if file is enigma file
     */
    protected boolean isEnigmaFile(File srcFile) {
        return srcFile.getName().endsWith(InjectCodeTask.CLASS_NAME + ".java");
    }
    /**
     * Check if the JAVA file contains Enigma code
     * @param srcFile JAVA file to test
     * @throws IOException If an I/O exception
     * @return True if the file contains Enigma code
     */
    protected boolean isEnigmatized(File srcFile) throws IOException {
        String contents = FileUtils.readFileToString(srcFile, "UTF-8");
        return contents.contains(CodeParser.importString) || contents.contains(CodeParser.functionName);
    }
}