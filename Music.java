/*
Leo Lei & Harry Wei
Music.java
This class is for the background music in the game. midi files are played for the background,
so the class has one Sequencer field.

music is from
https://www.khinsider.com/midi/nes/burgertime
*/

import java.io.*;
import javax.sound.midi.*;

class Music {
    private Sequencer midiPlayer;

    public Music(String midFilename) {
        try {
            File midiFile = new File(midFilename);
            Sequence song = MidiSystem.getSequence(midiFile);
            midiPlayer = MidiSystem.getSequencer();
            midiPlayer.open();
            midiPlayer.setSequence(song);
            midiPlayer.setLoopCount(1);// normally plays just once

        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //loops the file forever
    public void LoopContinuously() {
        midiPlayer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
    }

    //play and stop
    public void play() {
        midiPlayer.start();
    }

    public void stop() {
        midiPlayer.stop();
    }

    //if the sound is playing
    public boolean isPlaying(){
        return midiPlayer.isRunning();
    }
    //resets the sound to the beginning
    public void startAtBeginning(){
        midiPlayer.setMicrosecondPosition(0);
    }

}
