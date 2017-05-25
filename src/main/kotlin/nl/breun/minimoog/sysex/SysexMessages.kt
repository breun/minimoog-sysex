package nl.breun.minimoog.sysex

import javax.sound.midi.SysexMessage

fun message(hex: String): SysexMessage {
    val bytes = hexStringToByteArray(hex)
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

private fun hexStringToByteArray(hex: String): ByteArray {
    val stripped = hex.replace(" ", "").toUpperCase()
    val result = ByteArray(stripped.length / 2)
    val HEX_CHARS = "0123456789ABCDEF"
    for (i in 0 until stripped.length step 2) {
        val firstIndex = HEX_CHARS.indexOf(stripped[i])
        val secondIndex = HEX_CHARS.indexOf(stripped[i + 1])

        val octet = firstIndex.shl(4).or(secondIndex)
        result[i.shr(1)] = octet.toByte()
    }
    return result
}