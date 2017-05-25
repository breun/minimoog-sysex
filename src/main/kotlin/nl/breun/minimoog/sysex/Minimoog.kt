package nl.breun.minimoog.sysex

import nl.breun.minimoog.sysex.parameter.*
import nl.breun.minimoog.sysex.parameter.GlobalParameter.*
import javax.sound.midi.Receiver
import javax.sound.midi.SysexMessage
import javax.sound.midi.Transmitter

class Minimoog(val receiver: Receiver, val transmitter: Transmitter, val deviceId: DeviceId) {

    fun setDeviceId(deviceId: DeviceId) {
        setGlobalParameter(DEVICE_ID, deviceId.value)
    }

    fun setMidiChannelIn(channel: Int) {
        if (channel < 0 || channel > 15) throw IllegalArgumentException("channel: [0, 15]")
        setGlobalParameter(MIDI_CHANNEL_IN, channel)
    }

    fun setMidiChannelOut(channel: Int) {
        if (channel < 0 || channel > 15) throw IllegalArgumentException("channel: [0, 15]")
        setGlobalParameter(MIDI_CHANNEL_OUT, channel)
    }

    fun setKeyPriority(priority: KeyPriority) {
        setGlobalParameter(KEY_PRIORITY, priority.value)
    }

    fun setMultiTrigger(status: Status) {
        setGlobalParameter(MULTI_TRIGGER, status.value)
    }

    fun setBendSemitones(semitones: Int) {
        if (semitones < 0 || semitones > 12) throw IllegalArgumentException("semitones: [0, 12]")
        setGlobalParameter(BEND_SEMITONES, semitones + 12)
    }

    fun setOutputMidiPitchBend(status: Status) {
        setGlobalParameter(OUTPUT_MIDI_PITCH_BEND, status.value)
    }

    fun setOutputMidiPressure(status: Status) {
        setGlobalParameter(OUTPUT_MIDI_PRESSURE, status.value)
    }

    fun setGateTriggerSources(sources: GateTriggerSources) {
        setGlobalParameter(GATE_TRIGGER_SOURCES, sources.value)
    }

    fun setTuningError(enabled: Status) {
        setGlobalParameter(TUNING_ERROR, enabled.value)
    }

    fun setTuningVariance(cents: Double) {
        if (cents < 0 || cents > 50) throw IllegalArgumentException("cents: [0.0, 50.0]")
        setGlobalParameter(TUNING_VARIANCE, (cents * 10).toInt()) // unit is 0.1 cent
    }

    fun setTuningProgram(program: TuningProgram) {
        setGlobalParameter(TUNING_PROGRAM, program.value)
    }

    fun setVelocityCurve(curve: VelocityCurve) {
        setGlobalParameter(VELOCITY_CURVE, curve.value)
    }

    fun setMidiInTranspose(semitones: Int) {
        if (semitones < -12 || semitones > 12) throw IllegalArgumentException("semitones: [-12, 12]")
        setGlobalParameter(MIDI_IN_TRANSPOSE, semitones + 12)
    }

    fun setMidiOutTranspose(semitones: Int) {
        if (semitones < -12 || semitones > 12) throw IllegalArgumentException("semitones: [-12, 12]")
        setGlobalParameter(MIDI_OUT_TRANSPOSE, semitones + 12)
    }

    fun setPressureCVRange(range: PressureCVRange) {
        setGlobalParameter(PRESSURE_CV_RANGE, range.value)
    }

    fun setMidiNoteZeroVolts(note: Int) {
        if (note < 0 || note > 127) throw IllegalArgumentException("note: [0, 127]")
        setGlobalParameter(MIDI_NOTE_ZERO_VOLTS, note)
    }

    fun setLocalControl(enabled: Status) {
        setGlobalParameter(LOCAL_CONTROL, enabled.value)
    }

    /* Additional Sysex Commands */

    /**
     * Hardware will output keyscan firmware image in SysEx format;
     * can be saved & used to program another keyscan board if needed
     */
    fun transmitFirmware() {
        send("F0 04 15 7F 16 00 00 00 F7")
    }

    /**
     * Must send this and wait for erase to complete, before sending new firmware
     */
    fun eraseFirmware() {
        send("F0 04 15 7F 11 00 00 00 F7")
    }

    fun transmitFirmwareVersion(): String {
        send("F0 04 15 7F 15 00 00 00 F7")

        /*
         * Hardware will output the following:
         * F0 7E 7F 06 02 04 00 15 00 01 00 00 [v_min] [v_maj] F7
         * Where v_maj is major version number and v_min is minor version number
         */
        val v_maj = -1 // TODO: receive and parse response
        val v_min = -1 // TODO: receive and parse response

        return "$v_maj.$v_min"
    }

    fun restoreDefaultGlobalSettings() {
        send("F0 04 15 7F 13 00 00 00 F7")
    }

    fun restoreDefaultVelocityCurves() {
        send("F0 04 15 7F 0A 02 00 00 F7")
    }

    /**
     * “Tuning Error” must be turned on in order to hear any result from this action
     */
    fun randomizeTuningErrorTable() {
        send("F0 04 15 7F 1A 00 00 00 F7")
    }

    fun saveTuningErrorTableToEEPROM() {
        send("F0 04 15 7F 1A 01 00 00 F7")
    }

    fun startPitchCVCalibration() {
        send("F0 04 15 7F 17 00 00 00 F7")
    }

    fun startPitchWheelCalibration() {
        send("F0 04 15 7F 18 00 00 00 F7")
    }

    fun startPressureCalibration() {
        send("F0 04 15 7F 19 00 00 00 F7")
    }

    private fun setGlobalParameter(globalParameter: GlobalParameter, value: Int) {
        /*
         * Use the following data format to set a global value using a SysEx message:
         * F0 04 15 aa 14 bb cc dd F7
         * aa = Device ID: 00 to 0A (must match hardware device ID), or 7F to address all devices. bb = global parameter number
         * cc = parameter value MSB (will be zero unless parameter value is > 127)
         * dd = parameter value LSB (range is 0 to 127)
         */
        val bytes = byteArrayOf(
                0xF0.toByte(),
                0x04.toByte(),
                0x15.toByte(),
                deviceId.value.toByte(),
                0x14.toByte(),
                globalParameter.number.toByte(),
                msb(value),
                lsb(value),
                0xF7.toByte()
        )

        val message = message(bytes)
        send(message)
    }

    private fun send(hex: String) {
        val message = message(hex)
        send(message)
    }

    private fun send(message: SysexMessage) {
        receiver.send(message, -1)
    }
}
