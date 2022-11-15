@file:Suppress("UnnecessaryAbstractClass", "MemberVisibilityCanBePrivate")

package io.github.anvell.keepass.gradle.plugin

import app.keemobile.kotpass.cryptography.EncryptedValue
import app.keemobile.kotpass.database.Credentials
import app.keemobile.kotpass.database.KeePassDatabase
import app.keemobile.kotpass.database.decode
import app.keemobile.kotpass.database.findEntryBy
import app.keemobile.kotpass.models.Entry
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
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

    fun fromEntry(
        withTitle: String,
        field: String
    ) = fromEntry(
        predicate = { fields.title?.content == withTitle },
        field = field
    )

    fun fromEntry(
        predicate: Entry.() -> Boolean,
        field: String
    ): String {
        val entry = database
            .findEntryBy(predicate)
            ?: error("Cannot find entry.")

        return entry
            .fields[field]
            ?.content
            ?: error("Cannot find field with key: $field.")
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
