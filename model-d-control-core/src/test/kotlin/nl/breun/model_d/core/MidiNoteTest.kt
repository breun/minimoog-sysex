package nl.breun.model_d.core

import nl.breun.model_d.core.parameter.MidiNote
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.Test

class MidiNoteTest {

    @Test
    fun `Throw exception when trying to create MIDI note with note value lower than 0`() {
        assertThatExceptionOfType(IllegalArgumentException::class.java)
                .isThrownBy { MidiNote(-1) }
                .withMessage("note: [0, 127]")
    }

    @Test
    fun `Creating valid MIDI note 0 should not throw`() {
        assertThat(MidiNote(0)).isInstanceOf(MidiNote::class.java)
    }

    @Test
    fun `Creating valid MIDI note 127 should not throw`() {
        assertThat(MidiNote(127)).isInstanceOf(MidiNote::class.java)
    }

    @Test
    fun `Throw exception when trying to create MIDI note with note value higher than 127`() {
        assertThatExceptionOfType(IllegalArgumentException::class.java)
                .isThrownBy { MidiNote(128) }
                .withMessage("note: [0, 127]")
    }
}