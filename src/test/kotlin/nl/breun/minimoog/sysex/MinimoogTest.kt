package nl.breun.minimoog.sysex

import com.nhaarman.mockito_kotlin.*
import io.github.benas.randombeans.api.EnhancedRandom.random
import nl.breun.minimoog.sysex.parameter.DeviceId
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.Before
import org.junit.Test

import javax.sound.midi.Receiver
import javax.sound.midi.SysexMessage

class MinimoogTest {

    var deviceId : DeviceId
    var sysexMessage : SysexMessage?
    val receiver : Receiver

    init {
        deviceId = DeviceId._0
        sysexMessage = null
        receiver = mock {
            on { send(any(), any()) }.doAnswer {
                sysexMessage = it.getArgument(0)
                Unit
            }
        }
    }

    @Before
    fun setup() {
        sysexMessage = null
        deviceId = random(DeviceId::class.java)
    }

    @Test
    fun setDeviceId() {
        val minimoog = Minimoog(receiver, mock(), deviceId)
        minimoog.setDeviceId(DeviceId._2)

        assertGlobalParameterMessage(
                expectedDeviceId = deviceId.value.toByte(),
                expectedGlobalParameterNumber = 0x00.toByte(),
                expectedMSB = 0x00.toByte(),
                expectedLSB = 0x02.toByte(),
                actualMessage = sysexMessage
        )
    }

    @Test
    fun setMidiChannelIn() {
        val minimoog = Minimoog(receiver, mock(), deviceId)
        minimoog.setMidiChannelIn(13)

        assertGlobalParameterMessage(
                expectedDeviceId = deviceId.value.toByte(),
                expectedGlobalParameterNumber = 0x01.toByte(),
                expectedMSB = 0x00.toByte(),
                expectedLSB = 0x0D.toByte(),
                actualMessage = sysexMessage
        )
    }

    @Test
    fun illegalMidiChannelIn() {
        val minimoog = Minimoog(receiver, mock(), deviceId)

        assertThatExceptionOfType(IllegalArgumentException::class.java)
                .isThrownBy { minimoog.setMidiChannelIn(-1) }
                .withMessage("channel: [0, 15]")

        assertThatExceptionOfType(IllegalArgumentException::class.java)
                .isThrownBy { minimoog.setMidiChannelIn(17) }
                .withMessage("channel: [0, 15]")
    }

    @Test
    fun setMidiChannelOut() {
        val minimoog = Minimoog(receiver, mock(), deviceId)
        minimoog.setMidiChannelOut(9)

        assertGlobalParameterMessage(
                expectedDeviceId = deviceId.value.toByte(),
                expectedGlobalParameterNumber = 0x02.toByte(),
                expectedMSB = 0x00.toByte(),
                expectedLSB = 0x09.toByte(),
                actualMessage = sysexMessage
        )
    }

    @Test
    fun illegalMidiChannelOut() {
        val minimoog = Minimoog(receiver, mock(), deviceId)

        assertThatExceptionOfType(IllegalArgumentException::class.java)
                .isThrownBy { minimoog.setMidiChannelOut(-1) }
                .withMessage("channel: [0, 15]")

        assertThatExceptionOfType(IllegalArgumentException::class.java)
                .isThrownBy { minimoog.setMidiChannelOut(17) }
                .withMessage("channel: [0, 15]")
    }

    private fun assertGlobalParameterMessage(
            expectedDeviceId: Byte,
            expectedGlobalParameterNumber: Byte,
            expectedMSB: Byte,
            expectedLSB: Byte,
            actualMessage: SysexMessage?
    ) {
        assertThat(actualMessage).isNotNull()

        val bytes = actualMessage!!.message

        assertGlobalParameterMessageBytes(bytes)
        assertDeviceId(expectedDeviceId, bytes)
        assertGlobalParameterNumber(expectedGlobalParameterNumber, bytes)
        assertMSB(expectedMSB, bytes)
        assertLSB(expectedLSB, bytes)
    }

    private fun assertGlobalParameterMessageBytes(bytes: ByteArray) {
        assertThat(bytes[0]).isEqualTo(0xF0.toByte())
        assertThat(bytes[1]).isEqualTo(0x04.toByte())
        assertThat(bytes[2]).isEqualTo(0x15.toByte())
        assertThat(bytes[4]).isEqualTo(0x14.toByte())
        assertThat(bytes[8]).isEqualTo(0xF7.toByte())
    }

    private fun assertDeviceId(expected: Byte, bytes: ByteArray) {
        assertThat(bytes[3]).isEqualTo(expected)
    }

    private fun assertGlobalParameterNumber(expected: Byte, bytes: ByteArray) {
        assertThat(bytes[5]).isEqualTo(expected)
    }

    private fun assertMSB(expected: Byte, bytes: ByteArray) {
        assertThat(bytes[6]).isEqualTo(expected)
    }

    private fun assertLSB(expected: Byte, bytes: ByteArray) {
        assertThat(bytes[7]).isEqualTo(expected)
    }
}