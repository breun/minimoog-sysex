package nl.breun.model_d.ui

import javax.sound.midi.MidiDevice
import javax.sound.midi.MidiSystem

fun main(args: Array<String>) {
    val midiDeviceInfo = MidiSystem.getMidiDeviceInfo()

    print(midiDeviceInfo)

    /*
    val transmitterInfo = midiDeviceInfo[1] // TODO: Select midiIn through GUI or startup arguments?
    val receiverInfo = midiDeviceInfo[2] // TODO: Select midiOut through GUI or startup arguments?

    val midiIn = MidiSystem.getMidiDevice(transmitterInfo).midiIn
    val midiOut = MidiSystem.getMidiDevice(receiverInfo).midiOut

    val modelD = ModelD(midiOut, midiIn, DeviceId.ALL) // TODO: Select device ID through GUI or startup arguments?
    val firmwareVersion = minimoog.getFirmwareVersion()

    println("Firmware version: $firmwareVersion")
    */
}

fun print(midiDeviceInfo: Array<MidiDevice.Info>) {
    midiDeviceInfo.forEachIndexed { index, info ->
        println("Index: $index")
        println("Name: ${info.name}")
        println("Vendor: ${info.vendor}")
        println("Description: ${info.description}")
        println("Version: ${info.version}")
        println("")
    }
}
