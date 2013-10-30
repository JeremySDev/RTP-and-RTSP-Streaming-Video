package edu.wcu.RTPandRTSPStreamingVideo;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    public static void main(String args[])
    {
        String[] arguments = new String[] {"1024"};
        Server.main(arguments);
        String[] arguments2 = new String[] {"localhost", "1025", "1024", "/home/jstilwell/Videos/movie.Mjpeg"};
        Client.main(arguments2);
    }
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigorous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }
}
