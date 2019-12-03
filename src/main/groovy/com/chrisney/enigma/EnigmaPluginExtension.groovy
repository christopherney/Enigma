package com.chrisney.enigma

class EnigmaPluginExtension {
    /**
     * Enable or disable the Enigma Plugin
     */
    boolean enabled = true;
    /**
     * Define the Key to encrypt Strings
     */
    String hash = ''
    /**
     * Default source folder (Android Java App)
     */
    String srcJava = "/app/src/main/java"
    /**
     * Define classes to ignore (no encryption)
     */
    String[] ignoredClasses = null
    /**
     * Specified classes to encrypt. If null all classes are encrypted, excepted ignored classes.
     */
    String[] classes =  null;
    /**
     * Custom Encryption function (not implemented)
     * @TODO : implement this option
     */
    String customFunction = null
    /**
     * Custom Encryption task (not implemented)
     * @TODO : implement this option
     */
    String encryptionTaskName = null;
    /**
     * Enable / disable the fake keys injection (honeypot principal)
     */
    boolean injectFakeKeys = true;
    /**
     * Enable / disable the DEBUG (verbose) mode
     */
    boolean debug = false;
}