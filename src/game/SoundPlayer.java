package game;

import javax.sound.sampled.*;
import java.io.File;

//soundplayer class
public class SoundPlayer {

    private Clip bgmClip; //background music clip
    private float masterVolume = 1;  //0 = silent, 1 = full

    //bkg music

    //stop any current BGM and start a new looping track
    public void playBKG_music(String filepath) {
        stopBGM();
        try {
            File file = new File(filepath);
            if (!file.exists()) { System.out.println("BGM not found" + filepath); return; }
            AudioInputStream stream = AudioSystem.getAudioInputStream(file);
            bgmClip = AudioSystem.getClip();
            bgmClip.open(stream);
            applyVolume(bgmClip, masterVolume);
            bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
            bgmClip.start();
        } catch (Exception e) {
            System.out.println("music bkg not working" + e.getMessage());
        }
    }

    //stop bkg music method
    public void stopBGM() {
        if (bgmClip != null && bgmClip.isRunning()) {
            bgmClip.stop();
            bgmClip.close();
        }
        bgmClip = null;
    }

    public boolean isBGMPlaying() {  //sets vals to be checked if playing
        return bgmClip != null && bgmClip.isRunning();
    }


    public void stopAudio() { stopBGM(); }

    //sound effects

    //play a short one-shot sound effect
    public void playSFX(String filepath) {
        try {
            File file = new File(filepath);
            if (!file.exists()) { System.out.println("1 cant find" + filepath); return; }
            AudioInputStream stream = AudioSystem.getAudioInputStream(file);
            Clip sfx = AudioSystem.getClip();
            sfx.open(stream);
            applyVolume(sfx, masterVolume);
            //auto close when done
            sfx.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    sfx.close();
                }
            });
            sfx.start();
        } catch (Exception e) {
            System.out.println("cant find" + e.getMessage());
        }
    }

    //volume

    //set volume for all future clips and the currently playing BGM

    public void setVolume(float vol) {
        masterVolume = Math.max(0f, Math.min(1f, vol));
        if (bgmClip != null) {
            applyVolume(bgmClip, masterVolume);
        }
    }

    public float getVolume() {
        return masterVolume;
    }  //volume getter

    //apply a linear 0-1 volume to a clip using its master_gain control
    private void applyVolume(Clip clip, float vol) {
        try {
            FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            //convert linear 0-1 to dB: gain = 20 * log10(vol), clamp to control range
            float dB = vol <= 0f ? gain.getMinimum() : (float)(20.0 * Math.log10(vol)); //condition ? valueIfTrue : valueIfFalse
            dB = Math.max(gain.getMinimum(), Math.min(gain.getMaximum(), dB));
            gain.setValue(dB);
        }
        catch (IllegalArgumentException e) {

        }
    }
}
