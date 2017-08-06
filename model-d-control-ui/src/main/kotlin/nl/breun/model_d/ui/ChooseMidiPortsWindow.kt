package nl.breun.model_d.ui

import java.awt.GridLayout
import javax.sound.midi.MidiDevice
import javax.swing.*

class ChooseMidiPortsWindow(midiOuts: List<MidiDevice>, midiIns: List<MidiDevice>) : JFrame("Select MIDI ports") {

    private var selectedMidiOut: MidiDevice? = null
    private var selectedMidiIn: MidiDevice? = null

    init {
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        layout = GridLayout(0, 1)

        add(midiOutPanel(midiOuts))
        add(midiInPanel(midiIns))
        add(connectButton())

        pack()
        isVisible = true
    }

    private fun midiOutPanel(midiOuts: List<MidiDevice>): JPanel {
        val panel = JPanel(GridLayout(0, 1))
        panel.add(JLabel("MIDI out"))

        val buttonGroup = ButtonGroup()

        midiOuts.forEachIndexed { index, midiOut ->
            if (index == 0) {
                selectedMidiOut = midiOut
            }
            val info = midiOut.deviceInfo
            val radioButton = JRadioButton("${info.vendor} ${info.name} ${info.description} (${info.version})", index == 0)
            radioButton.addActionListener { _ -> selectedMidiOut = midiOut }

            buttonGroup.add(radioButton)
            panel.add(radioButton)
        }

        return panel
    }

    private fun midiInPanel(midiIns: List<MidiDevice>): JPanel {
        val panel = JPanel(GridLayout(0, 1))
        panel.add(JLabel("MIDI in"))

        val buttonGroup = ButtonGroup()

        midiIns.forEachIndexed { index, midiIn ->
            if (index == 0) {
                selectedMidiIn = midiIn
            }
            val info = midiIn.deviceInfo
            val radioButton = JRadioButton("${info.vendor} ${info.name} ${info.description} (${info.version})", index == 0)
            radioButton.addActionListener { _ -> selectedMidiIn = midiIn }

            buttonGroup.add(radioButton)
            panel.add(radioButton)
        }

        return panel
    }

    private fun connectButton(): JButton {
        val button = JButton("Connect")
        button.addActionListener { _ ->
            if (selectedMidiOut != null && selectedMidiIn != null) {
                ModelDWindow(selectedMidiOut!!, selectedMidiIn!!)
                isVisible = false
                dispose()
            } else {
                println("Both MIDI out and in are required to connect")
            }
        }
        return button
    }
}