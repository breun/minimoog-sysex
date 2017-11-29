package nl.breun.model_d.core

import org.apache.commons.codec.binary.Hex
import javax.sound.midi.SysexMessage

internal fun message(hex: String): SysexMessage {
    val chars = hex.replace(" ", "").toUpperCase().toCharArray()
    val bytes = Hex.decodeHex(chars)
    return message(bytes)
}

internal fun message(bytes: ByteArray) = SysexMessage(bytes, bytes.size)

/**
 * Most significant byte for 7-bit MIDI messages.
 */
internal fun msb(value: Int) = value.shr(7).toByte()

/**
 * Least significant byte for 7-bit MIDI messages.
 */
internal fun lsb(value: Int) = (value % 128).toByte() // 2^7 = 128

internal fun hexString(message: SysexMessage) =
        Hex.encodeHexString(message.message)
                .replace("..".toRegex(), "\$0 ").trim() // Insert spaces between every two chars
                .toUpperCase()