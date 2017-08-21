package nl.breun.model_d.core

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import javax.sound.midi.ShortMessage
import javax.sound.midi.SysexMessage

class SysexMessagesTest {

    @Test
    fun hexStringWithSpacesToSysexMessage() {
        val message = message("F0 04 15 7F 15 00 00 00 F7")

        assertThat(message.length).isEqualTo(9)
        assertThat(message.status).isEqualTo(SysexMessage.SYSTEM_EXCLUSIVE)

        // Data does not include status byte
        val data = message.data
        assertThat(data.size).isEqualTo(8)
        assertThat(data[0]).isEqualTo(0x04.toByte())
        assertThat(data[1]).isEqualTo(0x15.toByte())
        assertThat(data[2]).isEqualTo(0x7F.toByte())
        assertThat(data[3]).isEqualTo(0x15.toByte())
        assertThat(data[4]).isEqualTo(0x00.toByte())
        assertThat(data[5]).isEqualTo(0x00.toByte())
        assertThat(data[6]).isEqualTo(0x00.toByte())
        assertThat(data[7]).isEqualTo(ShortMessage.END_OF_EXCLUSIVE.toByte())

        // Message includes status byte
        val bytes = message.message
        assertThat(bytes.size).isEqualTo(9)
        assertThat(bytes[0]).isEqualTo(SysexMessage.SYSTEM_EXCLUSIVE.toByte())
        assertThat(bytes[1]).isEqualTo(0x04.toByte())
        assertThat(bytes[2]).isEqualTo(0x15.toByte())
        assertThat(bytes[3]).isEqualTo(0x7F.toByte())
        assertThat(bytes[4]).isEqualTo(0x15.toByte())
        assertThat(bytes[5]).isEqualTo(0x00.toByte())
        assertThat(bytes[6]).isEqualTo(0x00.toByte())
        assertThat(bytes[7]).isEqualTo(0x00.toByte())
        assertThat(bytes[8]).isEqualTo(ShortMessage.END_OF_EXCLUSIVE.toByte())
    }

    @Test
    fun lsb() {
                                                            //   MSB     LSB
        assertThat(lsb(0)).isEqualTo(0x00.toByte())   // 0000000 0000000
        assertThat(lsb(127)).isEqualTo(0x7F.toByte()) // 0000000 1111111
        assertThat(lsb(128)).isEqualTo(0x00.toByte()) // 0000001 0000000
        assertThat(lsb(500)).isEqualTo(0x74.toByte()) // 0000011 1110100
    }

    @Test
    fun msb() {
                                                            //   MSB     LSB
        assertThat(msb(0)).isEqualTo(0x00.toByte())   // 0000000 0000000
        assertThat(msb(127)).isEqualTo(0x00.toByte()) // 0000000 1111111
        assertThat(msb(128)).isEqualTo(0x01.toByte()) // 0000001 0000000
        assertThat(msb(500)).isEqualTo(0x03.toByte()) // 0000011 1110100
    }
}