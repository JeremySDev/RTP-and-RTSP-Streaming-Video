import java.io.IOException;

public interface VideoInterface {

    /** Size of the header field before each frame of video in an MJPEG file */ 
    public static final int HEADER = 5;

    /**
     * Get the next frame from a file.
     * @param frame an array of bytes that will contain the actual frame upon
     * method completion.
     * @return size of the frame
     * @throws IOException if we cannot read from the video file.
     */
    public int getNextFrame(byte[] frame) throws IOException;
}
