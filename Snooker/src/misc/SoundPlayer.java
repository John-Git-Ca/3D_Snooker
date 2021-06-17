package misc;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Class for playing certain sounds.
 * Really bare becuase it only plays one sound.
 */
public class SoundPlayer {
    /** Can only stack up to this many sounds of a pool ball at a time */
    public static int maxPoolBall = 4;
    /** The pool ball clips to use */
    public static Clip[] poolBalls = new Clip [maxPoolBall];
    static{ for(int i=0;i<maxPoolBall;i++) poolBalls[i] = getClip("PoolBall.wav");}
    /** Current sound clip to play next */
    public static int curPoolBall = 0;
    
    /**
     * Private static method for creating a Clip object from an audio file name
     * @param fileName Name of the audio file to open
     * @return The newly created Clip object to use
     */
    private static Clip getClip (String fileName) {
        String filePath = "assets/audio/"+fileName;
        File f = new File(filePath);
        AudioInputStream ais;
        try { ais = AudioSystem.getAudioInputStream(f);
        } catch (IOException | UnsupportedAudioFileException e) {
            e.printStackTrace(System.err);
            return null;
        }
        AudioFormat af = ais.getFormat();
        DataLine.Info info = new DataLine.Info(Clip.class, af);
        Clip aclip;
        try { aclip = (Clip) AudioSystem.getLine(info);
        } catch (LineUnavailableException e) {
            e.printStackTrace(System.err);
            return null;
        }
        try { aclip.open(ais);
        } catch (LineUnavailableException | IOException e) {
            e.printStackTrace(System.err);
            return null;
        }
        return aclip;
    }

    /**
     * Plays a pool ball collision sound, can stack up to {@link #maxPoolBall} at a time
     */
    public static void playPoolBallColl () {
        Clip pb = poolBalls[curPoolBall];
        pb.stop();
        pb.setMicrosecondPosition(0);
        pb.start();
        curPoolBall = (curPoolBall + 1) % maxPoolBall;
    }

}
