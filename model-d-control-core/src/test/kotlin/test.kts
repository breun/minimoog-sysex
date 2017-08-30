import javax.sound.midi.MidiSystem
import javax.sound.midi.ShortMessage

val midiDevices = MidiSystem.getMidiDeviceInfo().map { MidiSystem.getMidiDevice(it) }
val midiOuts = midiDevices.filter { it.maxReceivers != 0 }

val midiOut = midiOuts.find { it.deviceInfo.vendor == "Presonus" }

if (midiOut != null) {
    midiOut.open()

    val receiver = midiOut.receiver

    repeat(5) {
        receiver.send(ShortMessage(ShortMessage.NOTE_ON, 60, 60), -1)
        Thread.sleep(100)
        receiver.send(ShortMessage(ShortMessage.NOTE_OFF, 60, 0), -1)
        Thread.sleep(100)
    }

    receiver.close()
    midiOut.close()

} else {
    println("Presonus MIDI out not found")
}