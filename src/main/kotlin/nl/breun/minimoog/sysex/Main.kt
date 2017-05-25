package nl.breun.minimoog.sysex

import nl.breun.minimoog.sysex.parameter.DeviceId
import javax.sound.midi.MidiDevice
import javax.sound.midi.MidiSystem

fun main(args: Array<String>) {
    val midiDeviceInfo = MidiSystem.getMidiDeviceInfo()

    print(midiDeviceInfo)

    val transmitterInfo = midiDeviceInfo[1] // TODO: Select transmitter through GUI or startup arguments?
    val receiverInfo = midiDeviceInfo[2] // TODO: Select receiver through GUI or startup arguments?

    val transmitter = MidiSystem.getMidiDevice(transmitterInfo).transmitter
    val receiver = MidiSystem.getMidiDevice(receiverInfo).receiver

    val minimoog = Minimoog(receiver, transmitter, DeviceId._0) // TODO: Select device ID through GUI or startup arguments?
    val firmwareVersion = minimoog.transmitFirmwareVersion()

    println("Firmware version: $firmwareVersion")
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
