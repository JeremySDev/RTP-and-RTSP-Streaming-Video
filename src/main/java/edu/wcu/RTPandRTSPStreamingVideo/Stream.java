package edu.wcu.RTPandRTSPStreamingVideo;

import java.io.Reader;
import java.io.Writer;
import java.net.UnknownHostException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.Socket;
import java.net.InetAddress;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.io.InterruptedIOException;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.awt.event.ActionListener;
import javax.swing.Timer;

/**
 * Base class for our Client and Server!
 *
 * @author Jeremy Stilwell
 * @author Alisha Hayman
 * @version 10/26/13.
 */
public class Stream
{

    // RTP variables:
    /**
     * UDP packet received from the server
     */
    private DatagramPacket packet;
    /**
     * socket used to send/receive UDP packets
     */
    private DatagramSocket rtpSocket;
    /**
     * socket used to send/receive UDP packets
     */
    private Socket rtspSocket;
    /**
     * timer used to receive data from the UDP socket
     */
    private Timer timer;
    /**
     * buffer used to store data received from the server
     */
    private byte[] buf;

    // RTSP variables
    /**
     * RTSP state == INIT or READY or PLAYING
     */
    private static State state;
    /**
     * video file to request to the server
     */
    private static String videoFileName;
    /**
     * Sequence number of RTSP messages within the session
     */
    private int rtspSeqNum;
    /**
     * ID of the RTSP session (given by the RTSP Server)
     */
    private int rtspID;

    /**
     * Constant for Carriage Return Line Feed
     */
    public final static String CRLF = "\r\n";
    /**
     * RTP payload type for MJPEG video
     */
    public final static int MJPEG_TYPE = 26;
    /**
     * OKAY status message
     */
    public final static int OKAY = 200;

    /**
     * Constructor for Stream.
     */
    public Stream()
    {
        rtspSeqNum = 0;
        rtspID = 0;
        // memory for the buffer uses to send/receiver tasty data.
        buf = new byte[64000]; // Picked this size for a UDP reason, any
        // guesses?
        // All other fields set to null by init, which is okay in this case.

    }

    /**
     * Get the current datagram packet.
     *
     * @return the current datagram packet.
     */
    public DatagramPacket getDgPacket()
    {
        return new DatagramPacket(buf, buf.length);
    }

    /**
     * Get the name of the videofile we are streaming.
     *
     * @return the name of the video file.
     */
    public String getVideoFileName()
    {
        return videoFileName;
    }

    /**
     * Assign the name of the videofile we are streaming.
     *
     * @param name video file name.
     */
    public void setVideoFileName(String name)
    {
        videoFileName = name;
    }

    /**
     * Get the storage buffer.
     *
     * @return an array of bytes for holding frame data.
     */
    public byte[] getBuffer()
    {
        return buf;
    }

    /**
     * Get the current RTSP sequence number.
     */
    public int getRtspSeqNum()
    {
        return rtspSeqNum;
    }

    /**
     * Increment the RTSP sequence number by one.
     */
    public void incrementRtspSeqNum()
    {
        rtspSeqNum++;
    }

    /**
     * Set the current RTSP Sequence number.
     *
     * @param num new sequence number.
     */
    public void setRtspSeqNum(int num)
    {
        rtspSeqNum = num;
    }

    /**
     * Set the current RTSP ID number.
     *
     * @param id new id number.
     */
    public void setRtspID(int id)
    {
        rtspID = id;
    }

    /**
     * Get the current RTSP ID number.
     *
     * @return the current RTSP ID num.
     */
    public int getRtspID()
    {
        return rtspID;
    }

    /**
     * Set the datagram packet to an already existing one.
     */
    public void setPacket(DatagramPacket dgp)
    {
        packet = dgp;
    }

    /**
     * Create a new datagram packet.
     */
    public void setPacket()
    {
        packet = new DatagramPacket(buf, buf.length);
    }

    /**
     * Get the datagram packet.
     *
     * @return the datagram packet.
     */
    public DatagramPacket getPacket()
    {
        return packet;
    }

    /**
     * Set the value of the RTP socket
     *
     * @param dgs a datagram socket.
     */
    public void setRtpSocket(DatagramSocket dgs)
    {
        rtpSocket = dgs;
    }

    /**
     * Get the RTP socket
     *
     * @return the RTP socket;
     */
    public DatagramSocket getRtpSocket()
    {
        return rtpSocket;
    }

    /**
     * Change the timeout on the RTSP Socket.
     *
     * @param time new timeout interval.
     * @throws SocketException if there is an error in the underlying
     *                         protocol.
     */
    public void setRtpSocketTimeout(int time) throws SocketException
    {
        rtpSocket.setSoTimeout(time);
    }

    /**
     * Set the value of the RTSP socket
     *
     * @param socket a socket.
     */
    public void setRtspSocket(Socket socket)
    {
        rtspSocket = socket;
    }

    /**
     * Get the RTSP socket
     *
     * @return the RTSP socket;
     */
    public Socket getRtspSocket()
    {
        return rtspSocket;
    }

    /**
     * Create the timer and set initial values.
     *
     * @param period   timer interval.
     * @param listener a listener to attach to the timer.
     */
    public void initTimer(int period, ActionListener listener)
    {
        timer = new Timer(period, listener);
        timer.setInitialDelay(0);
        timer.setCoalesce(true);
    }

    /**
     * Start the timer.
     */
    public void startTimer()
    {
        timer.start();
    }

    /**
     * Stop the timer.
     */
    public void stopTimer()
    {
        timer.stop();
    }

    /**
     * Place the process in the init state
     */
    public void setInitState()
    {
        state = State.INIT;
    }

    /**
     * Place the process in the play state
     */
    public void setPlayState()
    {
        state = State.PLAYING;
    }

    /**
     * Place the process in the ready state
     */
    public void setReadyState()
    {
        state = State.READY;
    }

    /**
     * Determine if we are in the play state.
     *
     * @return true if we are in the ready state, false otherwise
     */
    public boolean isPlayState()
    {
        return state == State.PLAYING;
    }

    /**
     * Determine if we are in the init state.
     *
     * @return true if we are in the ready state, false otherwise
     */
    public boolean isInitState()
    {
        return state == State.INIT;
    }

    /**
     * Determine if we are in the ready state.
     *
     * @return true if we are in the ready state, false otherwise
     */
    public boolean isReadyState()
    {
        return state == State.READY;
    }

    /*
     * Enum for readability. 
     */
    private enum State
    {
        INIT, READY, PLAYING
    }
}
