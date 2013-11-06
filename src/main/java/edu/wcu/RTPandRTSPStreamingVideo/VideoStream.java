package edu.wcu.RTPandRTSPStreamingVideo;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Video Stream this class handles reading the file.
 *
 * @author Jeremy Stilwell
 * @author Alisha Hayman
 * @version 10/26/13.
 */
public class VideoStream implements VideoInterface
{

    // file input stream of the video file
    private FileInputStream fileInputStream;

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
     * getNextFrame - gets the next frame of video from the file.
     *
     * @param frame an array of bytes that will contain the actual frame upon
     *              method completion.
     * @return int
     * @throws IOException
     */
    public int getNextFrame(byte[] frame) throws IOException
    {
        // integer representation of the length of the frame
        int length = 0;
        // String representation of the length of the frame
        String lengthString;

        byte[] frameLength = new byte[HEADER];

        // read current frameLength
        fileInputStream.read(frameLength, 0, HEADER);

        // transform frameLength to integer
        lengthString = new String(frameLength);

        try
        {
            length = Integer.parseInt(lengthString);
        }
        catch (NumberFormatException nfe)
        {
            System.out.println("Your video has ended");
        }

        return (fileInputStream.read(frame, 0, length));
    }
}