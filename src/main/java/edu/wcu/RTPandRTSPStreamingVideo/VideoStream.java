package edu.wcu.RTPandRTSPStreamingVideo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author Jeremy Stilwell
 * @author Alisha Hayman
 * @version 10/26/13.
 */
public class VideoStream implements VideoInterface
{
    public VideoStream(String videoFileName) throws IOException
    {
        File file = new File(videoFileName);
        FileInputStream fileInputStream = new FileInputStream(file);
        fileInputStream.read();

    }

    public int getNextFrame(byte[] buffer)
    {
        return 0;
    }
}
