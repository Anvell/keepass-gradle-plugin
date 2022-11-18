@file:Suppress("SpellCheckingInspection")

package io.github.anvell.keepass.gradle.plugin.extensions

import okio.ByteString
import kotlin.experimental.xor

private const val Hex = "0123456789abcdef"

/**
 * Applies truncation and XOR-folding to SHA 256 hash.
 *
 * @return Hex string containing 32 chars long hash.
 */
internal fun ByteString.toShortHexHash(): String {
    val halfLength = size / 2
    val bytes = ByteArray(halfLength) { i ->
        this[i] xor this[halfLength + i]
    }

    return buildString(bytes.size * 2) {
        for (byte in bytes) {
            val n = byte.toInt()
            append(Hex[n shr 4 and 0x0f])
            append(Hex[n and 0x0f])
        }
    }
}
