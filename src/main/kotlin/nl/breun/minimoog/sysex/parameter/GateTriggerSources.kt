package nl.breun.minimoog.sysex.parameter

enum class GateTriggerSources(val value: Int) {
    EXTERNAL(0),
    EXTERNAL_AND_LOCAL(1),
    EXTERNAL_AND_MIDI(2),
    EXTERNAL_AND_LOCAL_AND_MIDI(3)
}