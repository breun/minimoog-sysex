[![Build Status](https://travis-ci.org/breun/minimoog-sysex.svg?branch=master)](https://travis-ci.org/breun/minimoog-sysex)

Minimoog Model D SysEx Tool
===========================

The 2016 reissue of the [Minimoog Model D](https://www.moogmusic.com/products/minimoog/minimoog-model-d) synthesizer added MIDI support to this iconic instrument.

This software supports changing global parameters via SysEx and who knows, maybe some day I'll add a CLI or GUI. Currently it is mostly a library that can be used on the Java Virtual Machine (JVM).

Supported Features
------------------

There is support for setting the following global parameters:

* Device ID (0-15)
* MIDI Channel In (0-15)
* MIDI Channel Out (0-15)
* Key Priority (low, last, high)
* Multi Trigger (off, on)
* Bend Semitones (0-12 semitones)
* Output MIDI Pitch Bend (off, on)
* Output MIDI Pressure (off, on)
* Gate/Trigger Sources (external, external+local, external+MIDI, external+local+MIDI)
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

Build and Run
-------------

For now this a source-only repository, so you will not find any ready to go releases.
 
This software is written in [Kotlin](https://kotlinlang.org), but the only requirement for building from source is [Oracle's Java SE Development Kit 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html).

Build the source:

    $ ./mvnw package
    
On Windows you may be able to use `mvnw.cmd` instead of `./mvnw`.
    
Run it like this:
    
    $ java -jar target/minimoog-sysex-X.Y.Z-SNAPSHOT-jar-with-dependencies.jar

Currently there is no real CLI/GUI yet, so only some information about the MIDI devices on your system is printed.

Library
-------

I plan on keeping the library and CLI/GUI separate, so you can use the functionality in your own programs as well.

Example usage in Java:

    import nl.breun.minimoog.sysex.Minimoog
    import nl.breun.minimoog.sysex.parameter.DeviceId
    import nl.breun.minimoog.sysex.parameter.KeyPriority
    
    import javax.sound.midi.Receiver
    import javax.sound.midi.Transmitter
    
    class Example {
    
        public static void main(String[] args) {
    
            // Initialize your MIDI receiver (MIDI Out port)
            Receiver receiver = ... 
            
            // Initialize your MIDI transmitter (MIDI In port)
            Transmitter transmitter = ...
    
            // Create a Minimoog instance
            Minimoog minimoog = new Minimoog(receiver, transmitter, DeviceId.ALL)
            
            // Change the MIDI input channel
            minimoog.setMidiChannelIn(5)
            
            // Change the key priority
            minimoog.setKeyPriority(KeyPriority.LAST)
        }
    }
    
    
Data Sheet
----------

The official SysEx Data Sheet for the Minimoog Model D can be found [on the Moog website](https://www.moogmusic.com/products/minimoog/minimoog-model-d#downloads-tab).