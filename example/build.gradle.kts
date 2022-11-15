import app.keemobile.kotpass.constants.BasicField

plugins {
    java
    id("io.github.anvell.keepass.gradle.plugin")
}

gradleKeePass {
    sourceFile.set(File(project.rootDir, "assets/test.kdbx"))
    keyfile.set(File(project.rootDir, "assets/test.key"))
    password.set("test")
}

println("Value: ${gradleKeePass.fromEntry("One", BasicField.Password())}")
