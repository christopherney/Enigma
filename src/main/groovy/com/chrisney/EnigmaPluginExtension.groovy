package com.chrisney

import org.gradle.api.DefaultTask

class EnigmaPluginExtension {
    boolean enabled = true;
    String hash = ''
    String srcJava = "/app/src/main/java"
    String[] ignoredClasses = null
    String customFunction = null
    String encryptionTaskName = null;
    boolean injectFakeKeys = true;
    boolean debug = false;
}