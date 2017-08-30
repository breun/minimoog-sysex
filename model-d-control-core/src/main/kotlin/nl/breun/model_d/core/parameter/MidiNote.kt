package nl.breun.model_d.core.parameter

class MidiNote(val value: Int) {
    init {
        if (value < 0 || value > 127) {
            throw IllegalArgumentException("note: [0, 127]")
        }
    }
}