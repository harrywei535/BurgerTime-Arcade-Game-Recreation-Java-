/*
Leo Lei & Harry Wei. This class is for all the sounds in the game.
The following websites are where we found the sound files:
https://www.zophar.net/music/nintendo-nes-nsf/burgertime

*/

import java.io.*;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioSystem;

class Sound {
    private Clip c;
    public Sound(String filename) {
        setClip(filename);
    }

    public void setClip(String filename) {
        try {
            File f = new File(filename);
            c = AudioSystem.getClip();
            c.open(AudioSystem.getAudioInputStream(f));

        } catch (Exception e) {
            System.out.println("error");
        }
    }

    //this play method makes sure the sound only ever plays once.
    public void play() {
        if(!isPlaying()) {
            c.start();
        }
        if(atEnd()){
           stop();
        }
    }


    //tells if the sound is playing
    public boolean isPlaying() {
        return c.isRunning();
    }

    // starts at beginning
    public void startFromBeginning() {
        c.setFramePosition(0);
    }

    // stops
    public void stop() {
        c.stop();
    }

    public boolean atEnd(){
        if(c.getFrameLength()==c.getFramePosition()){
            return true;
        }
        return false;
    }
}
