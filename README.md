# KeePass Gradle Plugin 🔑

Essential [**Gradle**](https://gradle.org) plugin for managing project secrets with [**KeePass**](https://keepass.info) format. Format IO is provided by ⭐️ [**kotpass**](https://github.com/keemobile/kotpass) libary. Encrypted databases could be stored on desktop, pendrive or remote repository which simplifies collaboration between developers. Plugin allows to extract secrets/binaries and reference them in build scripts.

## Setup

Add Jitpack dependency for Gradle plugins:

``` kotlin
pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

Apply plugin to your module:

``` kotlin
plugins {
    id "io.github.anvell.keepass.gradle.plugin" version "0.1.0"
}
```

## How to use
### Configure extension

Define path to **KeePass** database file along with password and/or keyfile.

``` kotlin
gradleKeePass {
    sourceFile.set(File(project.rootDir, "assets/test.kdbx"))
    keyfile.set(File(property("myproject.secrets.keyfile").toString()))
    password.set(property("myproject.secrets.password").toString())
}
```

### Retrieve entry fields

Secrets are retrieved from entry fields. Specific entry can be located by title:

``` kotlin
val secret = gradleKeePass.fromEntry(
    title = "One",
    field = BasicField.Password()
)
```

or by custom predicate:

``` kotlin
val secret = gradleKeePass.fromEntry(
    predicate = { fields.url?.content == "https://github.com" },
    field = "Some"
)
```

### Retrieve attached files

Attached files are placed under `parentDir`. File name is based on content hash and re-checked every time function is invoked:

``` kotlin
val sampleFile = gradleKeePass.entryBinary(
    title = "Two",
    parentDir = File(project.buildDir, "binaries"),
    binaryName = "sample.txt"
)
```

## Features

- Supports newest KeePass format versions up to **4.1**.
- AES256/ChaCha20 encryption with Argon2/AES KDF.
- Easily organise secrets in groups/entries and edit with any KeePass client of choise.
- Safely store sensitive files and retrieve when needed.
- Simplify collaboration by avoiding bloated gradle properties files.

## Contributing

Feel free to open a issue or submit a pull request for any bugs/improvements.

## Credits

This project uses [Kotlin gradle plugin template](https://github.com/cortinico/kotlin-gradle-plugin-template).
