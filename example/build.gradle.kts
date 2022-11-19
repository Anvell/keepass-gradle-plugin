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

println("Value: ${gradleKeePass.entryField("One", BasicField.Password())}")

val sampleFile = gradleKeePass.entryBinary(
    predicate = { fields.title?.content == "Two" },
    parentDir = File(project.buildDir, "binaries"),
    binaryName = "sample.txt"
)

println("File contents: ${sampleFile.readText()}")
