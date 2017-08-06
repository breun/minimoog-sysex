[![Build Status](https://travis-ci.org/breun/minimoog-sysex.svg?branch=master)](https://travis-ci.org/breun/minimoog-sysex)

Model D Control
===============

The 2016 reissue of Moog's [Minimoog Model D](https://www.moogmusic.com/products/minimoog/minimoog-model-d) synthesizer
added MIDI support to this iconic instrument.

The software in this project can be used to modify various

This project contains both a core library to use in your 

Supported Features
------------------

There is support for setting the following global parameters:

* Device ID (0-15 or all)
* MIDI Channel In (1-16)
* MIDI Channel Out (1-16)
* Key Priority (low, last, high)
* Multi Trigger (off, on)
* Bend Semitones (0-12 semitones)
* Output MIDI Pitch Bend (off, on)
* Output MIDI Pressure (off, on)
* Gate/Trigger Sources (external, external+local, external+ MIDI, external+local+MIDI)
* Tuning Error (off, on)
* Tuning Variance (0.0 to 50.0 cents)
* Tuning Program (twelve tone equal temperament and 3 custom MIDI tuning standard programs)
* Velocity Curve (soft, medium, hard)
* MIDI In Transpose (-12 to +12 semitones)
* MIDI Out Transpose (-12 to +12 semitones)
* Pressure CV Range (+5V, +10V)
* MIDI Note Zero Volts (0-127)
* Local Control (off, on)

There is also support for the following additional commands:

* Transmit firmware
* Erase firmware
* Transmit firmware version
* Restore default global settings
* Restore default velocity curves
* Randomize tuning error table
* Save tuning error table to EEPROM

And the following calibration commands are supported:

* Start pitch CV calibration
* Start pitch wheel calibration
* Start pressure calibration

Note that you need a reasonably good digital volt meter to perform these calibration procedures. Please refer to Moog's official data sheet (see below) for full calibration instructions.

Connecting Hardware
-------------------

Don't forget to connect your synth to your computer via MIDI. You'll need to connect your computer's MIDI out to your
synth's MIDI in to be able to send commands to the synth, but some commands also require the synth being able to send
data back to your computer, so also connect your synth's MIDI out to your computer's MIDI in.

Build and Run
-------------
 
This software is written in [Kotlin](https://kotlinlang.org). The only requirement for building from source is [Oracle's Java SE Development Kit 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html).

Build the source:

    $ ./mvnw package
    
On Windows you may be able to use `mvnw.cmd` instead of `./mvnw`.
    
Run the GUI like this:
    
    $ java -jar model-d-control-ui/target/model-d-control-ui-X.Y.Z-SNAPSHOT-jar-with-dependencies.jar

Currently there is no real CLI/GUI yet, so only some information about the MIDI devices on your system is printed.

Library
-------

The core library and GUI are in separate modules, so you can use the functionality of the core library in your own programs as well.

Example usage in Java:

    import ModelD
    import DeviceId
    import KeyPriority
    
    import javax.sound.midi.Receiver
    import javax.sound.midi.Transmitter
    
    class Example {
    
        public static void main(String[] args) {
    
            // Initialize your MIDI receiver (MIDI Out port)
            Receiver receiver = ... 
            
            // Initialize your MIDI transmitter (MIDI In port)
            Transmitter transmitter = ...
    
            // Create a ModelD instance
            ModelD modelD = new ModelD(receiver, transmitter, DeviceId.ALL)
            
            // Change the MIDI input channel
            modelD.setMidiChannelIn(5)
            
            // Change the key priority
            modelD.setKeyPriority(KeyPriority.LAST)
        }
    }
    
    
Data Sheet
----------

The official SysEx Data Sheet for the Minimoog Model D can be found [on the Moog website](https://www.moogmusic.com/products/minimoog/minimoog-model-d#downloads-tab).