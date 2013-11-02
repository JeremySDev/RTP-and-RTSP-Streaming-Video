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

    /* file input stream of the video file */
    FileInputStream fileInputStream;

    /**
     * Constructor for the VideoStream class.
     *
     * @param videoFileName the name of the video file to be used
     * @throws IOException
     */
    public VideoStream(String videoFileName) throws IOException
    {
        fileInputStream = new FileInputStream(videoFileName);
    }

    /**
     * getNextFrame
     *
     * @param frame an array of bytes that will contain the actual frame upon
     *              method completion.
     * @return int
     * @throws IOException
     */
    @Override
    public int getNextFrame(byte[] frame) throws IOException
    {
        int length;
        String length_string;
        byte[] frame_length = new byte[5];

        // read current frame_length
        fileInputStream.read(frame_length, 0, 5);

        // transform frame_length to integer
        length_string = new String(frame_length);
        length = Integer.parseInt(length_string);

        return (fileInputStream.read(frame, 0, length));
    }
}
