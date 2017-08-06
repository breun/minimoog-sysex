package nl.breun.model_d.ui

import nl.breun.model_d.core.ModelD
import nl.breun.model_d.core.parameter.DeviceId
import java.awt.GridLayout
import javax.sound.midi.MidiDevice
import javax.swing.JFrame
import javax.swing.JLabel

class ModelDWindow(midiOut: MidiDevice, midiIn: MidiDevice) : JFrame("Model D Control") {

    private val modelD: ModelD

    init {
        modelD = ModelD(midiOut, DeviceId.ALL) // TODO: Device ID?

        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        layout = GridLayout(0, 1)

        add(JLabel("Out: ${midiOut}"))
        add(JLabel("In: ${midiIn}"))
        add(JLabel("TODO"))

        pack()
        isVisible = true
    }
}