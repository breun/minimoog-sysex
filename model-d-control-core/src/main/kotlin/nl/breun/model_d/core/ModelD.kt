package nl.breun.model_d.core

import mu.KotlinLogging.logger
import nl.breun.model_d.core.parameter.*
import nl.breun.model_d.core.parameter.GlobalParameter.*
import javax.sound.midi.MidiDevice
import javax.sound.midi.Receiver
import javax.sound.midi.SysexMessage

class ModelD(
        midiOut: MidiDevice,
        //midiIn: MidiDevice,
        val deviceId: DeviceId) {

    private val log = logger {}

    private val receiver: Receiver
    //private val transmitter: Transmitter

    init {
        midiOut.open()
        receiver = midiOut.receiver
        //transmitter = midiIn.transmitter
    }

    fun setDeviceId(deviceId: DeviceId) {
        setGlobalParameter(DEVICE_ID, deviceId.value)
        log.info { "Set device ID to $deviceId" }
    }

    fun setMidiChannelIn(channel: MidiChannel) {
        setGlobalParameter(MIDI_CHANNEL_IN, channel.value - 1)
        log.info { "Set MIDI input channel to $channel" }
    }

    fun setMidiChannelOut(channel: MidiChannel) {
        setGlobalParameter(MIDI_CHANNEL_OUT, channel.value - 1)
        log.info { "Set MIDI output channel to $channel" }
    }

    fun setKeyPriority(priority: KeyPriority) {
        setGlobalParameter(KEY_PRIORITY, priority.value)
        log.info { "Set key priority to $priority" }
    }

    fun setMultiTrigger(status: Status) {
        setGlobalParameter(MULTI_TRIGGER, status.value)
        log.info { "Set multi-trigger to $status" }
    }

    /**
     * Set the maximum amount of semitones for a pitch bend (0-12).
     */
    fun setBendSemitones(semitones: Int) {
        if (semitones < 0 || semitones > 12) throw IllegalArgumentException("bend semitones: [0, 12]")
        setGlobalParameter(BEND_SEMITONES, semitones)
        log.info { "Set pitch bend to $semitones semi-tones" }
    }

    fun setOutputMidiPitchBend(status: Status) {
        setGlobalParameter(OUTPUT_MIDI_PITCH_BEND, status.value)
        log.info { "Set output MIDI pitch bend to $status" }
    }

    fun setOutputMidiPressure(status: Status) {
        setGlobalParameter(OUTPUT_MIDI_PRESSURE, status.value)
        log.info { "Set output MIDI pressure to $status" }
    }

    fun setGateTriggerSources(sources: GateTriggerSources) {
        setGlobalParameter(GATE_TRIGGER_SOURCES, sources.value)
        log.info { "Set gate trigger sources to $sources" }
    }

    fun setTuningError(status: Status) {
        setGlobalParameter(TUNING_ERROR, status.value)
        log.info("Set tuning error to $status")
    }

    /**
     * Set the tuning variance from 0.0 to 50.0 cents.
     */
    fun setTuningVariance(cents: Double) {
        if (cents < 0 || cents > 50) throw IllegalArgumentException("cents: [0.0, 50.0]")
        setGlobalParameter(TUNING_VARIANCE, (cents * 10).toInt()) // unit is 0.1 cent
        log.info { "Set tuning variance to $cents cents" }
    }

    fun setTuningProgram(program: TuningProgram) {
        setGlobalParameter(TUNING_PROGRAM, program.value)
        log.info { "Set tuning program to $program" }
    }

    fun setVelocityCurve(curve: VelocityCurve) {
        setGlobalParameter(VELOCITY_CURVE, curve.value)
        log.info { "Set velocity curve to $curve" }
    }

    /**
     * Set the number of semitones for MIDI in transpose (-12 to +12).
     */
    fun setMidiInTranspose(semitones: Int) {
        validateTransposeSemitones(semitones)
        setGlobalParameter(MIDI_IN_TRANSPOSE, semitones + 12)
        log.info { "Set MIDI input transpose to $semitones semi-tones" }
    }

    /**
     * Set the number of semitones for MIDI out transpose (-12 to +12).
     */
    fun setMidiOutTranspose(semitones: Int) {
        validateTransposeSemitones(semitones)
        setGlobalParameter(MIDI_OUT_TRANSPOSE, semitones + 12)
        log.info { "Set MIDI output transpose to $semitones semi-tones" }
    }

    fun setPressureCVRange(range: PressureCVRange) {
        setGlobalParameter(PRESSURE_CV_RANGE, range.value)
        log.info { "Set pressure CV range to $range" }
    }

    /**
     * Set the MIDI note for 0 volts from 0 (C0) to 127 (G10).
     */
    fun setMidiNoteZeroVolts(note: MidiNote) {
        setGlobalParameter(MIDI_NOTE_ZERO_VOLTS, note.value)
        log.info { "Set MIDI note zero volts to $note" }
    }

    fun setLocalControl(status: Status) {
        setGlobalParameter(LOCAL_CONTROL, status.value)
        log.info { "Set local control to $status" }
    }

    /* Additional Sysex Commands */

    /**
     * Hardware will output keyscan firmware image in SysEx format;
     * can be saved & used to program another keyscan board if needed
     */
    fun transmitFirmware() = send("F0 04 15 7F 16 00 00 00 F7")

    /**
     * Must send this and wait for erase to complete, before sending new firmware
     */
    fun eraseFirmware() = send("F0 04 15 7F 11 00 00 00 F7")

    fun getFirmwareVersion(): String {
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

    fun restoreDefaultGlobalSettings() = send("F0 04 15 7F 13 00 00 00 F7")

    fun restoreDefaultVelocityCurves() = send("F0 04 15 7F 0A 02 00 00 F7")

    /**
     * “Tuning Error” must be turned on in order to hear any result from this action
     */
    fun randomizeTuningErrorTable()  = send("F0 04 15 7F 1A 00 00 00 F7")

    fun saveTuningErrorTableToEEPROM() = send("F0 04 15 7F 1A 01 00 00 F7")

    fun startPitchCVCalibration() = send("F0 04 15 7F 17 00 00 00 F7")

    fun startPitchWheelCalibration() = send("F0 04 15 7F 18 00 00 00 F7")

    fun startPressureCalibration() = send("F0 04 15 7F 19 00 00 00 F7")

    private fun validateTransposeSemitones(semitones: Int) {
        if (semitones < -12 || semitones > 12) throw IllegalArgumentException("transpose semitones: [-12, 12]")
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

    private fun send(hex: String) = send(message(hex))

    private fun send(message: SysexMessage) {
        receiver.send(message, -1)
        log.info { "Sent SysEx message: ${hexString(message)}" }
    }
}
