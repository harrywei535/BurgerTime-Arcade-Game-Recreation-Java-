/*
Leo Lei and Harry Wei
SoundEffect.java
This class is for SoundEffects. It was easier to have a separate class for these even
though it's similar to the sound class, as it separates them as they are used differently.
This sound can be played in conjunction with any Sound class object and the background music,
and the soundeffects are for burger falls, stepping on burgers, etc.
found from:
https://www.youtube.com/watch?v=QlPp7WEnaCI
 */

import java.io.*;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioSystem;

class SoundEffect {
    private Clip c;

    public SoundEffect(String filename) {
        setClip(filename);
    }

    public Clip getC() {
        return c;
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

    //plays. stops the sound when it's at the end
    public void play() {
        if (!atEnd()) {
            c.start();
        } else {
            c.stop();
            c.setFramePosition(0);
        }
    }

    //tells if the sound is playing
    public boolean isPlaying() {
        return c.isRunning();
    }

    //resets the sound beginning
    public void reset() {
        c.setFramePosition(0);
    }

    //at the end
    public boolean atEnd() {
        if (c.getFrameLength() == c.getFramePosition()) {
            return true;
        }
        return false;
    }
}
