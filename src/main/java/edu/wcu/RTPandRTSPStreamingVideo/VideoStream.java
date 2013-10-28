package edu.wcu.RTPandRTSPStreamingVideo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 *
 * @author Jeremy Stilwell
 * @author Alisha Hayman
 * @version 10/26/13.
 */
public class VideoStream implements VideoInterface
{
    public VideoStream(String videoFileName) throws FileNotFoundException
    {
        File file = new File(videoFileName);
        FileInputStream fileInputStream = new FileInputStream(file);

    }

    public int getNextFrame(byte[] buffer)
    {
        return 0;
    }
}
