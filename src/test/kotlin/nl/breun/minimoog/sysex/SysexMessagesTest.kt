package nl.breun.minimoog.sysex

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class SysexMessagesTest {

    @Test
    fun hexStringWithSpacesToSysexMessage() {
        val message = message("F0 04 15 7F 15 00 00 00 F7")

        assertThat(message.length).isEqualTo(9)
        assertThat(message.status).isEqualTo(0xF0)
        assertThat(message.data[0]).isEqualTo(0x04.toByte())
        assertThat(message.data[1]).isEqualTo(0x15.toByte())
        assertThat(message.data[2]).isEqualTo(0x7F.toByte())
        assertThat(message.data[3]).isEqualTo(0x15.toByte())
        assertThat(message.data[4]).isEqualTo(0x00.toByte())
        assertThat(message.data[5]).isEqualTo(0x00.toByte())
        assertThat(message.data[6]).isEqualTo(0x00.toByte())
        assertThat(message.data[7]).isEqualTo(0xF7.toByte())
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