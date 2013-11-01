package edu.wcu.RTPandRTSPStreamingVideo;

/**
 * @author Jeremy Stilwell
 * @version 10/30/13.
 */
public class Main
{
    /**
     * main method to run client and server hi cats
     *
     * @param args cats
     */
    public static void main(String args[])
    {
        final String[] argumentA = new String[]{"1024"};
        final String[] argumentB =
                new String[]{"localhost", "1025", argumentA[0], "/home/jstilwell/Videos/farL.Mjpeg"};

        new Thread(new Runnable()
        {
            public void run()
            {
                Server.main(argumentA);
            }
        }).start();

        new Thread(new Runnable()
        {
            public void run()
            {
                Client.main(argumentB);
            }
        }).start();
    }
}
