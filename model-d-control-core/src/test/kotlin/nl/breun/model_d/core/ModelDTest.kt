package nl.breun.model_d.core

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import io.github.benas.randombeans.api.EnhancedRandom.random
import nl.breun.model_d.core.parameter.*
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.assertj.core.description.TextDescription
import org.junit.Before
import org.junit.Test
import javax.sound.midi.MidiDevice
import javax.sound.midi.Receiver
import javax.sound.midi.ShortMessage
import javax.sound.midi.SysexMessage

class ModelDTest {

    private val modelD: ModelD
    private var deviceId: DeviceId
    private var lastMessage: SysexMessage? = null

    init {
        val r: Receiver = mock {
            on { send(any(), any()) }.doAnswer {
                lastMessage = it.getArgument(0)
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
        lastMessage = null
    }

    @Test
    fun `Set device ID to 0`() {
        modelD.setDeviceId(DeviceId._0)
        assertGlobalParameterMessage(0, 0, 0)
    }

    @Test
    fun `Set device ID to 15`() {
        modelD.setDeviceId(DeviceId._15)
        assertGlobalParameterMessage(0, 0, 15)
    }

    @Test
    fun `Set device ID to ALL`() {
        modelD.setDeviceId(DeviceId.ALL)
        assertGlobalParameterMessage(0, 0, 127)
    }

    @Test
    fun `Set MIDI channel in to 1`() {
        modelD.setMidiChannelIn(1)
        assertGlobalParameterMessage(1, 0, 0)
    }

    @Test
    fun `Set MIDI channel in to 16`() {
        modelD.setMidiChannelIn(16)
        assertGlobalParameterMessage(1, 0, 15)
    }

    @Test
    fun `Throw exception when trying to set MIDI channel in to zero`() {
        assertThatExceptionOfType(IllegalArgumentException::class.java)
                .isThrownBy { modelD.setMidiChannelIn(-1) }
                .withMessage("channel: [1, 16]")
    }

    @Test
    fun `Throw exception when trying to set MIDI channel in to 17`() {
        assertThatExceptionOfType(IllegalArgumentException::class.java)
                .isThrownBy { modelD.setMidiChannelIn(17) }
                .withMessage("channel: [1, 16]")
    }

    @Test
    fun `Set MIDI channel out to 1`() {
        modelD.setMidiChannelOut(1)
        assertGlobalParameterMessage(2, 0, 0)
    }

    @Test
    fun `Set MIDI channel out to 16`() {
        modelD.setMidiChannelOut(16)
        assertGlobalParameterMessage(2, 0, 15)
    }

    @Test
    fun `Throw exception when trying to set MIDI channel out to 0`() {
        assertThatExceptionOfType(IllegalArgumentException::class.java)
                .isThrownBy { modelD.setMidiChannelOut(0) }
                .withMessage("channel: [1, 16]")
    }

    @Test
    fun `Throw exception when trying to set MIDI channel out to 17`() {
        assertThatExceptionOfType(IllegalArgumentException::class.java)
                .isThrownBy { modelD.setMidiChannelOut(17) }
                .withMessage("channel: [1, 16]")
    }

    @Test
    fun `Set key priority to lowest key`() {
        modelD.setKeyPriority(KeyPriority.LOW)
        assertGlobalParameterMessage(3, 0, 0)
    }

    @Test
    fun `Set key priority to last key`() {
        modelD.setKeyPriority(KeyPriority.LAST)
        assertGlobalParameterMessage(3, 0, 1)
    }

    @Test
    fun `Set key priority to highest key`() {
        modelD.setKeyPriority(KeyPriority.HIGH)
        assertGlobalParameterMessage(3, 0, 2)
    }

    @Test
    fun `Disable multi-trigger`() {
        modelD.setMultiTrigger(Status.OFF)
        assertGlobalParameterMessage(4, 0, 0)
    }

    @Test
    fun `Enable multi-trigger`() {
        modelD.setMultiTrigger(Status.ON)
        assertGlobalParameterMessage(4, 0, 1)
    }

    @Test
    fun `Set bend to 0 semitones`() {
        modelD.setBendSemitones(9)
        assertGlobalParameterMessage(5, 0, 9)
    }

    @Test
    fun `Set bend to 12 semitones`() {
        modelD.setBendSemitones(12)
        assertGlobalParameterMessage(5, 0, 12)
    }

    @Test
    fun `Throw exception when trying to set ben semitones to -1`() {
        assertThatExceptionOfType(IllegalArgumentException::class.java)
                .isThrownBy { modelD.setBendSemitones(-1) }
                .withMessage("bend semitones: [0, 12]")
    }

    @Test
    fun `Throw exception when trying to set ben semitones to 13`() {
        assertThatExceptionOfType(IllegalArgumentException::class.java)
                .isThrownBy { modelD.setBendSemitones(13) }
                .withMessage("bend semitones: [0, 12]")
    }

    @Test
    fun `Disable output MIDI pitch bend`() {
        modelD.setOutputMidiPitchBend(Status.OFF)
        assertGlobalParameterMessage(8, 0, 0)
    }

    @Test
    fun `Enable output MIDI pitch bend`() {
        modelD.setOutputMidiPitchBend(Status.ON)
        assertGlobalParameterMessage(8, 0, 1)
    }

    @Test
    fun `Disable output MIDI pressure`() {
        modelD.setOutputMidiPressure(Status.OFF)
        assertGlobalParameterMessage(9, 0, 0)
    }

    @Test
    fun `Enable output MIDI pressure`() {
        modelD.setOutputMidiPressure(Status.ON)
        assertGlobalParameterMessage(9, 0, 1)
    }

    @Test
    fun `Set gate trigger sources to external only`() {
        modelD.setGateTriggerSources(GateTriggerSources.EXTERNAL)
        assertGlobalParameterMessage(10, 0, 0)
    }

    @Test
    fun `Set gate trigger sources to external and local only`() {
        modelD.setGateTriggerSources(GateTriggerSources.EXTERNAL_AND_LOCAL)
        assertGlobalParameterMessage(10, 0, 1)
    }

    @Test
    fun `Set gate trigger sources to external and MIDI only`() {
        modelD.setGateTriggerSources(GateTriggerSources.EXTERNAL_AND_MIDI)
        assertGlobalParameterMessage(10, 0, 2)
    }

    @Test
    fun `Set gate trigger sources to external, local and MIDI`() {
        modelD.setGateTriggerSources(GateTriggerSources.EXTERNAL_AND_LOCAL_AND_MIDI)
        assertGlobalParameterMessage(10, 0, 3)
    }

    @Test
    fun `Disable tuning error`() {
        modelD.setTuningError(Status.OFF)
        assertGlobalParameterMessage(11, 0, 0)
    }

    @Test
    fun `Enable tuning error`() {
        modelD.setTuningError(Status.ON)
        assertGlobalParameterMessage(11, 0, 1)
    }

    @Test
    fun `Set tuning variance to 0 cents`() {
        modelD.setTuningVariance(0.0)
        assertGlobalParameterMessage(12, 0, 0)
    }

    @Test
    fun `Set tuning variance to 50 cents`() {
        modelD.setTuningVariance(50.0)
        assertGlobalParameterMessage(12, 3, 116)
    }

    @Test
    fun `Throw exception when trying to set negative tuning variance`() {
        assertThatExceptionOfType(IllegalArgumentException::class.java)
                .isThrownBy { modelD.setTuningVariance(-0.1) }
                .withMessage("cents: [0.0, 50.0]")
    }

    @Test
    fun `Throw exception when trying to set tuning variance higher than 50 cents`() {
        assertThatExceptionOfType(IllegalArgumentException::class.java)
                .isThrownBy { modelD.setTuningVariance(50.1) }
                .withMessage("cents: [0.0, 50.0]")
    }

    @Test
    fun `Set tuning program to twelve tone equal temparament`() {
        modelD.setTuningProgram(TuningProgram.TWELVE_TONE_EQUAL_TEMPERAMENT)
        assertGlobalParameterMessage(13, 0, 0)
    }

    @Test
    fun `Set tuning program to custom MIDI 1`() {
        modelD.setTuningProgram(TuningProgram.CUSTOM_MIDI_1)
        assertGlobalParameterMessage(13, 0, 1)
    }

    @Test
    fun `Set tuning program to custom MIDI 2`() {
        modelD.setTuningProgram(TuningProgram.CUSTOM_MIDI_2)
        assertGlobalParameterMessage(13, 0, 2)
    }

    @Test
    fun `Set tuning program to custom MIDI 3`() {
        modelD.setTuningProgram(TuningProgram.CUSTOM_MIDI_3)
        assertGlobalParameterMessage(13, 0, 3)
    }

    @Test
    fun `Set velocity curve to soft`() {
        modelD.setVelocityCurve(VelocityCurve.SOFT)
        assertGlobalParameterMessage(14, 0, 0)
    }

    @Test
    fun `Set velocity curve to medium`() {
        modelD.setVelocityCurve(VelocityCurve.MEDIUM)
        assertGlobalParameterMessage(14, 0, 1)
    }

    @Test
    fun `Set velocity curve to hard`() {
        modelD.setVelocityCurve(VelocityCurve.HARD)
        assertGlobalParameterMessage(14, 0, 2)
    }

    @Test
    fun `Set MIDI in transpose to -12 semitones`() {
        modelD.setMidiInTranspose(-12)
        assertGlobalParameterMessage(15, 0, 0)
    }

    @Test
    fun `Set MIDI in transpose to 0 semitones`() {
        modelD.setMidiInTranspose(0)
        assertGlobalParameterMessage(15, 0, 12)
    }

    @Test
    fun `Set MIDI in transpose to +12 semitones`() {
        modelD.setMidiInTranspose(12)
        assertGlobalParameterMessage(15, 0, 24)
    }

    @Test
    fun `Throw exception when trying to set MIDI in transpose lower than -12 semitones`() {
        assertThatExceptionOfType(IllegalArgumentException::class.java)
                .isThrownBy { modelD.setMidiInTranspose(-13) }
                .withMessage("transpose semitones: [-12, 12]")
    }

    @Test
    fun `Throw exception when trying to set MIDI in transpose higher than +12 semitones`() {
        assertThatExceptionOfType(IllegalArgumentException::class.java)
                .isThrownBy { modelD.setMidiInTranspose(13) }
                .withMessage("transpose semitones: [-12, 12]")
    }

    @Test
    fun `Set MIDI out transpose to -12 semitones`() {
        modelD.setMidiOutTranspose(-12)
        assertGlobalParameterMessage(16, 0, 0)
    }

    @Test
    fun `Set MIDI out transpose to 0 semitones`() {
        modelD.setMidiOutTranspose(0)
        assertGlobalParameterMessage(16, 0, 12)
    }

    @Test
    fun `Set MIDI out transpose to +12 semitones`() {
        modelD.setMidiOutTranspose(12)
        assertGlobalParameterMessage(16, 0, 24)
    }

    @Test
    fun `Throw exception when trying to set MIDI out transpose lower than -12 semitones`() {
        assertThatExceptionOfType(IllegalArgumentException::class.java)
                .isThrownBy { modelD.setMidiOutTranspose(-13) }
                .withMessage("transpose semitones: [-12, 12]")
    }

    @Test
    fun `Throw exception when trying to set MIDI out transpose higher than +12 semitones`() {
        assertThatExceptionOfType(IllegalArgumentException::class.java)
                .isThrownBy { modelD.setMidiOutTranspose(13) }
                .withMessage("transpose semitones: [-12, 12]")
    }

    @Test
    fun `Set pressure CV range to +5 volts`() {
        modelD.setPressureCVRange(PressureCVRange.PLUS_FIVE_VOLTS)
        assertGlobalParameterMessage(17, 0, 0)
    }

    @Test
    fun `Set pressure CV range to +10 volts`() {
        modelD.setPressureCVRange(PressureCVRange.PLUS_TEN_VOLTS)
        assertGlobalParameterMessage(17, 0, 1)
    }

    @Test
    fun `Set MIDI note 0 volts to C0 (MIDI note 0)`() {
        modelD.setMidiNoteZeroVolts(0)
        assertGlobalParameterMessage(18, 0, 0)
    }

    @Test
    fun `Set MIDI note 0 volts to G10 (MIDI note 127)`() {
        modelD.setMidiNoteZeroVolts(127)
        assertGlobalParameterMessage(18, 0, 127)
    }

    @Test
    fun `Throw exception when trying to set MIDI note 0 volts to negative MIDI note`() {
        assertThatExceptionOfType(IllegalArgumentException::class.java)
                .isThrownBy { modelD.setMidiNoteZeroVolts(-1) }
                .withMessage("note: [0, 127]")
    }

    @Test
    fun `Throw exception when trying to set MIDI note 0 volts to MIDI note higher than 127`() {
        assertThatExceptionOfType(IllegalArgumentException::class.java)
                .isThrownBy { modelD.setMidiNoteZeroVolts(128) }
                .withMessage("note: [0, 127]")
    }

    @Test
    fun `Disable local control`() {
        modelD.setLocalControl(Status.OFF)
        assertGlobalParameterMessage(19, 0, 0)
    }

    @Test
    fun `Enable local control`() {
        modelD.setLocalControl(Status.ON)
        assertGlobalParameterMessage(19, 0, 1)
    }

    private fun assertGlobalParameterMessage(
            expectedGlobalParameterNumber: Int,
            expectedMSB: Int,
            expectedLSB: Int
    ) {
        assertThat(lastMessage).isNotNull()

        val bytes = lastMessage!!.message

        assertGlobalParameterMessageBytes(bytes)
        assertDeviceId(bytes)
        assertGlobalParameterNumber(expectedGlobalParameterNumber.toByte(), bytes)
        assertMSB(expectedMSB.toByte(), bytes)
        assertLSB(expectedLSB.toByte(), bytes)
    }

    private fun assertGlobalParameterMessageBytes(bytes: ByteArray) {
        assertThat(bytes[0])
                .describedAs(TextDescription("First byte (status byte, constant)"))
                .isEqualTo(SysexMessage.SYSTEM_EXCLUSIVE.toByte())

        assertThat(bytes[1])
                .describedAs(TextDescription("Second byte (constant)"))
                .isEqualTo(0x04.toByte())

        assertThat(bytes[2])
                .describedAs(TextDescription("Third byte (constant)"))
                .isEqualTo(0x15.toByte())

        assertThat(bytes[4])
                .describedAs(TextDescription("Fifth byte (constant)"))
                .isEqualTo(0x14.toByte())

        assertThat(bytes[8])
                .describedAs(TextDescription("Ninth byte (end byte, constant)"))
                .isEqualTo(ShortMessage.END_OF_EXCLUSIVE.toByte())
    }

    private fun assertDeviceId(bytes: ByteArray) {
        assertThat(bytes[3])
                .describedAs(TextDescription("Device ID (fourth byte)"))
                .isEqualTo(deviceId.value.toByte())
    }

    private fun assertGlobalParameterNumber(expectedGlobalParameterNumber: Byte, bytes: ByteArray) {
        assertThat(bytes[5])
                .describedAs(TextDescription("Global parameter number (sixth byte)"))
                .isEqualTo(expectedGlobalParameterNumber)
    }

    private fun assertMSB(expectedMSB: Byte, bytes: ByteArray) {
        assertThat(bytes[6])
                .describedAs(TextDescription("MSB (seventh byte)"))
                .isEqualTo(expectedMSB)

    }

    private fun assertLSB(expected: Byte, bytes: ByteArray) {
        assertThat(bytes[7])
                .describedAs(TextDescription("LSB (eighth byte)"))
                .isEqualTo(expected)
    }
}