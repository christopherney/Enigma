package com.chrisney

import com.chrisney.tasks.BackupTask
import com.chrisney.tasks.CleanBackupTask
import com.chrisney.tasks.EnigmaTask
import com.chrisney.tasks.InjectCodeTask
import com.chrisney.tasks.RestoreTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class EnigmaPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        // https://docs.gradle.org/current/userguide/custom_plugins.html
        def extension = project.extensions.create('enigma', EnigmaPluginExtension)

        project.afterEvaluate {

            // Search custom Encryption task:
            def customEncryptTask = null
            if (extension.encryptionTaskName != null) {
                customEncryptTask = project.tasks.getByName(extension.encryptionTaskName)
                if (customEncryptTask != null) {
                    println("⚙️ C️ustom Encryption Task found: ${extension.encryptionTaskName}")
                }
            }

            project.task('cleanBackup', type: CleanBackupTask) {
                enabled = extension.enabled
                rootProject = project.rootDir.absolutePath
                pathSrc = project.rootDir.absolutePath + extension.srcJava
            }

            project.task('backup', type: BackupTask) {
                enabled = extension.enabled
                rootProject = project.rootDir.absolutePath
                pathSrc = project.rootDir.absolutePath + extension.srcJava
            }

            project.task('injectCode', type: InjectCodeTask) {
                enabled = extension.enabled
                rootProject = project.rootDir.absolutePath
                pathSrc = project.rootDir.absolutePath + extension.srcJava
                hash = extension.hash
                customFunction = extension.customFunction
            }

            project.task('encrypt', type: EnigmaTask) {
                enabled = extension.enabled
                rootProject = project.rootDir.absolutePath
                pathSrc = project.rootDir.absolutePath + extension.srcJava
                hash = extension.hash
                ignoredClasses = extension.ignoredClasses
                customFunction = extension.customFunction
                customEncryptionTask = customEncryptTask
            }

            project.task('restore', type: RestoreTask) {
                enabled = extension.enabled
                rootProject = project.rootDir.absolutePath
                pathSrc = project.rootDir.absolutePath + extension.srcJava
            }

            project.tasks.getByName('clean').dependsOn('cleanBackup')

            project.tasks.getByName('preBuild').dependsOn('backup')
            project.tasks.getByName('preBuild').dependsOn('injectCode')
            project.tasks.getByName('preBuild').dependsOn('encrypt')

            project.tasks.getByName('assembleRelease').finalizedBy('restore')
            project.tasks.getByName('assembleDebug').finalizedBy('restore')
            project.tasks.getByName('generateReleaseSources').finalizedBy('restore')
            project.tasks.getByName('generateDebugSources').finalizedBy('restore')
        }
    }
}
