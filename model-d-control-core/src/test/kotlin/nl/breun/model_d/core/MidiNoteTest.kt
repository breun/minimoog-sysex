package nl.breun.model_d.core

import nl.breun.model_d.core.parameter.MidiNote
import org.assertj.core.api.Assertions
import org.junit.Test

class MidiNoteTest {
    @Test
    fun `Throw exception when trying to create MIDI note with note value lower than 0`() {
        Assertions.assertThatExceptionOfType(IllegalArgumentException::class.java)
                .isThrownBy { MidiNote(-1) }
                .withMessage("note: [0, 127]")
    }

    @Test
    fun `Throw exception when trying to create MIDI note with note value higher than 127`() {
        Assertions.assertThatExceptionOfType(IllegalArgumentException::class.java)
                .isThrownBy { MidiNote(128) }
                .withMessage("note: [0, 127]")
    }
}