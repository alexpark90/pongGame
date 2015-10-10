package pong;

import java.io.*;
import javax.sound.sampled.*;

/**
 *  File name: SoundPlayer.java 
 *  Date: Dec.12.2014
 *
 *  Description: This class is for handling sound I/O used in the game. 
 * 
 * @author Alexis Park
 */

public class Sound
{
    ///////////////////////// FIELDS /////////////////////
    private File file;
    private AudioInputStream inputStream;
    private AudioFormat audioFormat;
    private SourceDataLine sourceDataLine;
    private boolean playing;

    ///////////////////////// METHODS ////////////////////
    // start to play a sound
    public void play(String fileName)
    {
        // stop previous play
        stop();

        // open the file
        open(fileName);

        // create a thread and play the sound async
        SoundPlayThread thread = new SoundPlayThread();
        thread.start();
    }

    // stop playing
    public synchronized void stop()
    {
        playing = false;
    }

    // open a sound file
    private void open(String fileName)
    {
        try
        {
            file = new File(fileName);
            if(!file.exists())
            { 
                System.out.println("[ERROR] Cannot find file: " + fileName);
                return;
            }
            inputStream = AudioSystem.getAudioInputStream(file);
            audioFormat = inputStream.getFormat();

            DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
            sourceDataLine = (SourceDataLine)AudioSystem.getLine(info);
        }
        catch(UnsupportedAudioFileException | IOException | LineUnavailableException ex)
        {
            System.out.println(ex.getMessage());
        }
    }
    //////////////////////////////////Inner Class /////////////////////////////
    // thread for playing audio stream data
    class SoundPlayThread extends Thread
    {
        // field
        private static final int BUFFER_SIZE = 128 * 1024;
        private byte buffer[] = new byte[BUFFER_SIZE];

        // start thread
        @Override
        public void run()
        {
            try
            {
                sourceDataLine.open(audioFormat);
                sourceDataLine.start();

                // to control the loop
                playing = true;
                
                // # of bytes
                int dataCount = 0;
                
                while(dataCount != -1 && playing)
                {
                    dataCount = inputStream.read(buffer, 0, buffer.length);
                    if(dataCount >= 0) 
                        sourceDataLine.write(buffer, 0, dataCount);
                }
            }
            catch(LineUnavailableException | IOException ex)
            {
                System.out.println(ex.getMessage());
            }
            finally
            {
                if(sourceDataLine != null)
                {
                    sourceDataLine.drain();
                    sourceDataLine.close();
                }
                playing = false;
            }
        }
    }
}
