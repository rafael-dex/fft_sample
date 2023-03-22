import javax.sound.sampled.*;
import java.io.*;
 
/**
 * A sample program is to demonstrate how to record sound in Java
 * author: www.codejava.net
 */
public class JavaSoundRecorder {
    // record duration, in milliseconds
    static final long RECORD_TIME = 60000;  // 1 minute
 
    // path of the wav file
    File wavFile = new File("./RecordAudio.wav");
 
    // format of audio file
    AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
 
    // the line from which audio data is captured
    TargetDataLine line;
 
    /**
     * Defines an audio format
     */
    AudioFormat getAudioFormat() {
        float sampleRate = 22050;
        int sampleSizeInBits = 8;
        int channels = 1;
        boolean signed = false;
        boolean bigEndian = false;
        AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits,
                                             channels, signed, bigEndian);
        return format;
    }

    public static void displayMixerInfo()
	{
	    Mixer.Info [] mixersInfo = AudioSystem.getMixerInfo();
	
        for (Mixer.Info mixerInfo : mixersInfo)
        {
            System.out.println("Mixer: " + mixerInfo.getName());
        
            Mixer mixer = AudioSystem.getMixer(mixerInfo);
        
            Line.Info [] sourceLineInfo = mixer.getSourceLineInfo();
            for (Line.Info info : sourceLineInfo)
            showLineInfo(info);
        
            Line.Info [] targetLineInfo = mixer.getTargetLineInfo();
            for (Line.Info info : targetLineInfo)
            showLineInfo(info);
        }
	}
	
	
	private static void showLineInfo(final Line.Info lineInfo)
	{
        System.out.println("  " + lineInfo.toString());
        
        if (lineInfo instanceof DataLine.Info)
        {
            DataLine.Info dataLineInfo = (DataLine.Info)lineInfo;
        
            AudioFormat [] formats = dataLineInfo.getFormats();
            for (final AudioFormat format : formats)
            System.out.println("    " + format.toString());
        }
	}
 
    /**
     * Captures the sound and record into a WAV file
     */
    void start() {
        try {
            AudioFormat format = getAudioFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
 
	    System.out.println("Mixer info: ");
	    displayMixerInfo();
	    System.out.println("Line info: " + info);

            // checks if system supports the data line
            if (!AudioSystem.isLineSupported(info)) {
                System.out.println("Line not supported");
                System.exit(0);
            }
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();   // start capturing
 
            System.out.println("Start capturing...");
 
            AudioInputStream ais = new AudioInputStream(line);
 
            System.out.println("Start recording...");
 
            // start recording
            AudioSystem.write(ais, fileType, wavFile);
 
        } catch (LineUnavailableException ex) {
            ex.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
 
    /**
     * Closes the target data line to finish capturing and recording
     */
    void finish() {
        line.stop();
        line.close();
        System.out.println("Finished");
    }
 
    /**
     * Entry to run the program
     */
    public static void main(String[] args) {
        final JavaSoundRecorder recorder = new JavaSoundRecorder();
 
        // creates a new thread that waits for a specified
        // of time before stopping
        Thread stopper = new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(RECORD_TIME);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                recorder.finish();
            }
        });
 
        stopper.start();
 
        // start recording
        recorder.start();
    }
}
