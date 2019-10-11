# Enigma
Gradle Plugin - Obfuscator String Encryption (Android/Java)

This project is a simple Gradle plugin to help you to encrypt all String values of your Android Java code at the compilation time.

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

And Build you App !
```sh
$ ./gradlew assembleRelease 
```
