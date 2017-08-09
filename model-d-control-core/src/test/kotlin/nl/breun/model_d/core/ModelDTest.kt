package nl.breun.model_d.core

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import io.github.benas.randombeans.api.EnhancedRandom.random
import nl.breun.model_d.core.parameter.DeviceId
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.Before
import org.junit.Test
import javax.sound.midi.MidiDevice
import javax.sound.midi.Receiver
import javax.sound.midi.SysexMessage

class ModelDTest {

    private val modelD: ModelD
    private var deviceId: DeviceId
    private var sysexMessage: SysexMessage? = null

    init {
        val r: Receiver = mock {
            on { send(any(), any()) }.doAnswer {
                sysexMessage = it.getArgument(0)
                Unit
            }
        }

        val midiOut: MidiDevice = mock {
            on { receiver }.doReturn(r)
        }

        deviceId = random(DeviceId::class.java)

        modelD = ModelD(midiOut, deviceId)
    }

    @Before
    fun setup() {
        sysexMessage = null
    }

    @Test
    fun setDeviceId() {
        modelD.setDeviceId(DeviceId._2)

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
        modelD.setMidiChannelIn(14)

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
        assertThatExceptionOfType(IllegalArgumentException::class.java)
                .isThrownBy { modelD.setMidiChannelIn(-1) }
                .withMessage("channel: [1, 16]")

        assertThatExceptionOfType(IllegalArgumentException::class.java)
                .isThrownBy { modelD.setMidiChannelIn(17) }
                .withMessage("channel: [1, 16]")
    }

    @Test
    fun setMidiChannelOut() {
        modelD.setMidiChannelOut(10)

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
        assertThatExceptionOfType(IllegalArgumentException::class.java)
                .isThrownBy { modelD.setMidiChannelOut(-1) }
                .withMessage("channel: [1, 16]")

        assertThatExceptionOfType(IllegalArgumentException::class.java)
                .isThrownBy { modelD.setMidiChannelOut(17) }
                .withMessage("channel: [1, 16]")
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