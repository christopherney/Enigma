# Enigma
Gradle Plugin - Obfuscator String Encryption (Android/Java)

This project is a simple Gradle plugin to help you to encrypt all String values of your Android Java code at the compilation time.

**Important: Enigma plugin won't execute if your project is not managed by a SCM tool such as git or SVN.**

**Recommended: commit your changes before compile your app with Enigma activated**

## How integrate it?

build.gradle (project)
```Gradle
buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:3.5.1'
        // Add the Enigma classpath
        classpath 'enigma:enigma:1.0-SNAPSHOT'
    }
}
```

build.gradle (app)
```java
apply plugin: 'com.android.application'

// Add Enigma Plugin
apply plugin: 'com.chrisney.enigma'

// Set Enigma options:
enigma.enabled = true
enigma.hash = "LXeyH4qdtk2YqNDnLqZzX5HmPEwEwZEN"
enigma.ignoredClasses = ["com.my.packagename.MainActivity.java"]

android {
    buildTypes {
        release {
            // Don't forget to enable Progard !
            minifyEnabled true
        }
    }
}
```

## Compile your App

During the compilation process, Enigma plugin will:
- Backup all Java files in backup directory **enigma-backup**
- Parse and encrypt all String values for each Java file
- Inject Enigma source code 
- Inject fake secrete keys (optional - check *enigma.injectFakeKeys* option)
- Complile YourApp (classic process)
- Restore your original Java files

```sh
$ ./gradlew assembleRelease
```
```sh
> Task :app:backup
💾 Backup: /app/src/main/java/com/app/helloworld/MainActivity.java
💾 Backup: /app/src/main/java/com/app/helloworld/helpers/IResponse.java
💾 Backup: /app/src/main/java/com/app/helloworld/helpers/Utils.java
💾 Backup: /app/src/main/java/com/app/helloworld/helpers/ATest.java
💾 Backup: /app/src/main/java/com/app/helloworld/helpers/TestImpl.java
💾 Backup: /app/src/main/java/com/app/helloworld/Constants.java

> Task :app:encrypt
🔐 MainActivity.java encrypted
🔐 IResponse.java encrypted
🔐 Utils.java encrypted
🔐 ATest.java encrypted
🔐 TestImpl.java encrypted
🔐 Constants.java encrypted

> Task :app:injectCode
✏️ Add Enigma code

> Task :app:restore
♻️ Restore: /app/src/main/java/com/proto/helloworld/MainActivity.java
♻️ Restore: /app/src/main/java/com/app/helloworld/helpers/IResponse.java
♻️ Restore: /app/src/main/java/com/app/helloworld/helpers/Utils.java
♻️ Restore: /app/src/main/java/com/app/helloworld/helpers/ATest.java
♻️ Restore: /app/src/main/java/com/app/helloworld/helpers/TestImpl.java
♻️ Restore: /app/src/main/java/com/app/helloworld/Constants.java
🧹 Remove Enigma code: ~/HelloWorld/app/src/main/java/com/app

```
