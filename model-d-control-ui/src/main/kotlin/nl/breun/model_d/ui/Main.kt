package nl.breun.model_d.ui

import javax.sound.midi.MidiSystem

fun main(args: Array<String>) {
    val midiDevices = MidiSystem.getMidiDeviceInfo()
            .map { MidiSystem.getMidiDevice(it) }

    // TODO: Remove this debug info
    midiDevices.forEachIndexed { index, device ->
        val info = device.deviceInfo
        println("Index: $index")
        println("Name: ${info.name}")
        println("Vendor: ${info.vendor}")
        println("Description: ${info.description}")
        println("Version: ${info.version}")
        println("Max receivers: ${device.maxReceivers}")
        println("Max transmitters: ${device.maxTransmitters}")
        println("")
    }

    val midiOuts = midiDevices.filter { it.maxReceivers != 0 }

    val midiIns = midiDevices.filter { it.maxTransmitters != 0 }

    if (midiOuts.isNotEmpty()) {
        ChooseMidiPortsWindow(midiOuts, midiIns)
    } else {
        println("No MIDI outs available, exiting...")
    }
}