@file:Suppress("UnnecessaryAbstractClass", "MemberVisibilityCanBePrivate")

package io.github.anvell.keepass.gradle.plugin

import app.keemobile.kotpass.constants.BasicField
import app.keemobile.kotpass.cryptography.EncryptedValue
import app.keemobile.kotpass.database.Credentials
import app.keemobile.kotpass.database.KeePassDatabase
import app.keemobile.kotpass.database.decode
import app.keemobile.kotpass.database.findEntryBy
import app.keemobile.kotpass.database.modifiers.binaries
import app.keemobile.kotpass.models.Entry
import io.github.anvell.keepass.gradle.plugin.extensions.toShortHexHash
import okio.ByteString.Companion.toByteString
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import java.io.File
import java.io.FileInputStream
import javax.inject.Inject

abstract class GradleKeePassExtension @Inject constructor(
    project: Project
) {
    private val objects = project.objects
    private val database by lazy { loadVault() }

    val sourceFile: RegularFileProperty = objects.fileProperty()
    val keyfile: RegularFileProperty = objects.fileProperty()
    val password: Property<String> = objects.property(String::class.java)

    /**
     * Searches for [Entry] with [title] and extracts binary data
     * with [binaryName] to [parentDir].
     * File name is based on SHA256 hash of the file.
     *
     * @param title [Entry] field with [BasicField.Title] key.
     * @param parentDir new directory will be created if it does not exist.
     * @param binaryName file name of binary data attached to the [Entry].
     * @return [File] object referencing extracted file.
     */
    fun entryBinary(
        title: String,
        parentDir: File,
        binaryName: String
    ): File = database
        .findEntryBy { fields.title?.content == title }
        ?.let { entryBinary(it, parentDir, binaryName) }
        ?: error("Cannot find entry with title: $title.")

    /**
     * Searches for [Entry] which matches a given [predicate] and extracts
     * binary data with [binaryName] to [parentDir].
     * File name is based on SHA256 hash of the file.
     *
     * @param predicate allows to match [Entry] using custom checks.
     * @param parentDir new directory will be created if it does not exist.
     * @param binaryName file name of binary data attached to the [Entry].
     * @return [File] object referencing extracted file.
     */
    fun entryBinary(
        predicate: Entry.() -> Boolean,
        parentDir: File,
        binaryName: String
    ): File = database
        .findEntryBy(predicate)
        ?.let { entryBinary(it, parentDir, binaryName) }
        ?: error("Cannot find entry by predicate.")

    private fun entryBinary(
        entry: Entry,
        parentDir: File,
        binaryName: String
    ): File {
        val binary = entry
            .binaries
            .find { it.name == binaryName }
            ?: error("Cannot find binary: $binaryName.")
        if (!parentDir.exists()) parentDir.mkdirs()

        val outputFile = File(parentDir, binary.hash.toShortHexHash())
        if (outputFile.exists()) {
            val hash = outputFile
                .inputStream()
                .use(FileInputStream::readBytes)
                .toByteString()
                .sha256()

            if (hash == binary.hash) {
                return outputFile
            }
        }

        val rawData = database
            .binaries[binary.hash]
            ?.getContent()
            ?: error("Database does not contain binary: $binaryName.")

        outputFile
            .outputStream()
            .use { it.write(rawData) }

        return outputFile
    }

    /**
     * Searches for [Entry] with [title] and attempts
     * to retrieve [field] content.
     *
     * @return [field] content.
     */
    fun entryField(
        title: String,
        field: String
    ): String = database
        .findEntryBy { fields.title?.content == title }
        ?.let { entryField(it, field) }
        ?: error("Cannot find entry with title: $title.")

    /**
     * Searches for [Entry] which matches a given [predicate]
     * and attempts to retrieve [field] content.
     *
     * @return [field] content.
     */
    fun entryField(
        predicate: Entry.() -> Boolean,
        field: String
    ): String = database
        .findEntryBy(predicate)
        ?.let { entryField(it, field) }
        ?: error("Cannot find entry by predicate.")

    private fun entryField(
        entry: Entry,
        field: String
    ): String = with(entry) {
        requireNotNull(fields[field]?.content) {
            "Entry '${fields.title?.content}' does not have field with key: $field."
        }
    }

    private fun loadVault(): KeePassDatabase {
        check(sourceFile.isPresent) {
            "KeePass source file is not defined."
        }
        val file = sourceFile.asFile.get()
        val credentials = getCredentials()

        return file.inputStream().use {
            KeePassDatabase.decode(it, credentials)
        }
    }

    private fun getCredentials(): Credentials = when {
        keyfile.isPresent && password.isPresent -> {
            Credentials.from(
                passphrase = EncryptedValue.fromString(password.get()),
                keyData = readKeyfile()
            )
        }
        password.isPresent -> {
            Credentials.from(
                passphrase = EncryptedValue.fromString(password.get())
            )
        }
        keyfile.isPresent -> {
            Credentials.from(keyData = readKeyfile())
        }
        else -> error("No credentials specified.")
    }

    private fun readKeyfile() = keyfile
        .asFile
        .get()
        .inputStream()
        .use(FileInputStream::readBytes)
}
