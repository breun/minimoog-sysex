package nl.breun.model_d.ui

import nl.breun.model_d.core.ModelD
import nl.breun.model_d.core.parameter.DeviceId
import nl.breun.model_d.core.parameter.KeyPriority
import java.awt.GridLayout
import javax.sound.midi.MidiDevice
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JFrame

class ModelDWindow(midiOut: MidiDevice, midiIn: MidiDevice) : JFrame("Model D Control") {

    private val modelD = ModelD(midiOut, DeviceId.ALL) // TODO: Device ID?

    init {
        // TODO: Remove
        println("Out: ${midiOut}")
        println("In: ${midiIn}")

        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        layout = GridLayout(0, 1)

        addKeyPriorityControls()

        pack()
        isVisible = true
    }

    private fun addKeyPriorityControls() {
        val comboBox = JComboBox<KeyPriority>(KeyPriority.values())
        add(comboBox)

        val button = JButton("Set priority")
        button.addActionListener { _ ->
            val keyPriority = comboBox.selectedItem as KeyPriority
            modelD.setKeyPriority(keyPriority)
        }
        add(button)
    }
}