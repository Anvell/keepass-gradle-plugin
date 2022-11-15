# KeePass Gradle Plugin 🔑

Essential [**Gradle**](https://gradle.org/) plugin for managing project secrets using [**KeePass**](https://keepass.info/) format. This allows to store encrypted vaults along with repository which simplifies collaboration between developers as there no need to share secrets separately each time they are changed or populated with new entries.

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

Define path to **KeePass** database file along with password and/or keyfile.

``` kotlin
gradleKeePass {
    sourceFile.set(File(project.rootDir, "assets/test.kdbx"))
    keyfile.set(File(property("myproject.secrets.keyfile").toString()))
    password.set(property("myproject.secrets.password").toString())
}
```

Secrets are extracted from entry fields. Specific entry can be located by title:

``` kotlin
val secret = gradleKeePass.fromEntry(
    withTitle = "One",
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

## Features

- Supports newest KeePass format versions up to **4.1**.
- AES256/ChaCha20 encryption with Argon2/AES KDF.
- Easily organise secrets in groups/entries and edit with any KeePass client of choise.
- Simplify collaboration by avoiding bloated gradle properties files.

## Contributing

Feel free to open a issue or submit a pull request for any bugs/improvements.

## Credits

This project uses [Kotlin gradle plugin template](https://github.com/cortinico/kotlin-gradle-plugin-template).
