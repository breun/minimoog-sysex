package nl.breun.model_d.core

import org.apache.commons.codec.binary.Hex
import javax.sound.midi.SysexMessage

fun message(hex: String): SysexMessage {
    val chars = hex.replace(" ", "").toUpperCase().toCharArray()
    val bytes = Hex.decodeHex(chars)
    return message(bytes)
}

fun message(bytes: ByteArray): SysexMessage {
    return SysexMessage(bytes, bytes.size)
}

/**
 * Most significant byte for 7-bit MIDI messages.
 */
fun msb(value: Int): Byte {
    return value.shr(7).toByte()
}

/**
 * Least significant byte for 7-bit MIDI messages.
 */
fun lsb(value: Int): Byte {
    return (value % 128).toByte() // 2^7 = 128
}

fun hexString(message: SysexMessage): String {
    val sysexStartArray = ByteArray(1, { _ -> SysexMessage.SYSTEM_EXCLUSIVE.toByte() })
    return Hex.encodeHexString(sysexStartArray + message.data)
            .replace("..".toRegex(), "\$0 ").trim() // Insert spaces between every two chars
            .toUpperCase()
}