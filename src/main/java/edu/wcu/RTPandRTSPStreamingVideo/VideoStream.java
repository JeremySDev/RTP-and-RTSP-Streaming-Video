package edu.wcu.RTPandRTSPStreamingVideo;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author Jeremy Stilwell
 * @author Alisha Hayman
 * @version 10/26/13.
 */
public class VideoStream implements VideoInterface
{
    FileInputStream fileInputStream;

    int frame;

    public VideoStream(String videoFileName) throws IOException
    {
        fileInputStream = new FileInputStream(videoFileName);
        frame = 0;

        /*File file = new File(videoFileName);
        FileInputStream fileInputStream = new FileInputStream(file);
        fileInputStream.read();*/
    }

    public int getNextFrame(byte[] frame) throws IOException
    {
        int length = 0;
        String length_string;
        byte[] frame_length = new byte[5];

        fileInputStream.read(frame_length, 0, 5);
        length_string = new String(frame_length);
        length = Integer.parseInt(length_string);
        return(fileInputStream.read(frame, 0, length));
    }
}
