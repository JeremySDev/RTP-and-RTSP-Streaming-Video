package edu.wcu.RTPandRTSPStreamingVideo;


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
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.ImageIcon;
import javax.swing.Timer;


/** 
 * Client to play a movie stream from an RTSP server.
 * @author William Kreahling, based on code from Kurose/Ross
 * @version October 11, 2013
 */
public class Client extends Stream {

    // GUI
    /** The main UI window */
    private JFrame      frame;
    /** Setup button */
    private JButton     setupButton;
    /** Play button */
    private JButton     playButton;
    /** Pause button */
    private JButton     pauseButton;
    /** Teardown Button */
    private JButton     tearButton;
    /** The main panel, duh! */
    private JPanel      mainPanel;
    /** Panel to hold all our buttons */
    private JPanel      buttonPanel;
    /** Label for our Window */
    private JLabel      iconLabel;
    /** Place where the video will live */
    private ImageIcon   icon;


    /** UDP packet received from the server */
    private DatagramPacket receivePacket;
    /** Receive port for the RTP packets */
    private final int rtpReceivePort;
    /** Input stream */
    private Scanner scanIn;
    /** Output stream */
    private BufferedWriter scanOut;

    /** Image width                                 */
    public final static int WIDTH           = 380;
    /** Image Height                                */
    public final static int HEIGHT          = 280;
    /** Buffer around the video image               */
    public final static int MARGIN          = 10;
    /** Size of the buttons                         */
    public final static int BHEIGHT         = 50;
    /** Default RTP port                            */
    public final static int RTP_PORT        = 25000;
    /** Default RTSP port                           */
    public final static int RTSP_PORT       = 9999;

 
    /**
     * constructor.
     * @param args[0] Hostname running the RTP server
     * @param args[1] RTP receive port!
     * @param args[2] Server port!
     * @param args[3] Name of the video file to play
     */
    public Client(String[] args) throws UnknownHostException, IOException {
        createUI();
        int value = RTP_PORT; 
        try {
            value = Integer.parseInt(args[1]);
        } catch (NumberFormatException nfe) {
            System.out.println("RTP port argument invalid " + nfe.getMessage());
            System.out.println("RTP port defaulting to " + value);
        }
        rtpReceivePort = value;
        int rtspServerPort  = RTSP_PORT;

        // Get server hostname
        String serverHost           = args[0];
        // Get video filename to request:
        if (args.length == 4)
            setVideoFileName(args[3]);
        else
            setVideoFileName(args[2]);

        try {
            rtspServerPort              = Integer.parseInt(args[2]);
        } catch (NumberFormatException nfe) {
            System.out.println("RTSP port argument invalid " + 
                               nfe.getMessage());
            System.out.println("RTSP Port defaulting to " + RTSP_PORT);
        }

        InetAddress serverIpAddr    = InetAddress.getByName(serverHost);
        // Establish a TCP connection with the server 
        setRtspSocket(new Socket(serverIpAddr, rtspServerPort));

        // Set input and output stream filters:
        scanIn = new Scanner(new 
                 InputStreamReader(getRtspSocket().getInputStream()));
        scanOut = new BufferedWriter(new 
                 OutputStreamWriter(getRtspSocket().getOutputStream()) );

        initTimer(20, new timerListener()); // small timeout
    }

    /**
     * Create the client User Interface. Its pretty sweet (not).
     */
    private void createUI() {
        frame       = new JFrame ("Client");
        setupButton = new JButton("Setup");
        playButton  = new JButton("Play");
        pauseButton = new JButton("Pause");
        tearButton  = new JButton("Teardown");
        mainPanel   = new JPanel();
        buttonPanel = new JPanel();
        iconLabel   = new JLabel();
        // Create a new Frame
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        // Create and add all the shiney buttons
        buttonPanel.setLayout(new GridLayout(1,0));
        buttonPanel.add(setupButton);
        buttonPanel.add(playButton);
        buttonPanel.add(pauseButton);
        buttonPanel.add(tearButton);
        setupButton.addActionListener(new setupButtonListener());
        playButton.addActionListener (new playButtonListener());
        pauseButton.addActionListener(new pauseButtonListener());
        tearButton.addActionListener (new tearButtonListener());

        // Image display label
        iconLabel.setIcon(null);

        // frame layout
        mainPanel.setLayout(null);
        mainPanel.add(iconLabel);
        mainPanel.add(buttonPanel);
        iconLabel.setBounds(Client.MARGIN / 2, 0, 
                            Client.WIDTH, Client.HEIGHT);
        buttonPanel.setBounds(Client.MARGIN / 2, Client.HEIGHT, 
                            Client.WIDTH, Client.BHEIGHT);

        frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
        // Pack it so I can get the inset values to make everything nicely
        // layed out.
        frame.pack();
        
        frame.setLocation(new Point(200, 100)); // Magic numbers :(
        Insets border = frame.getInsets();
        frame.setSize(new Dimension(Client.WIDTH + Client.MARGIN,
                                    Client.HEIGHT + Client.MARGIN +
                                    Client.BHEIGHT + border.top +
                                    border.bottom));
        frame.setVisible(true);

    }

    /**
     * Prints out a usage message and exits. The end!
     */
    public static void printUsageAndExit() {
        // This is static because sometimes we need to check command line
        // arguments BEFORE we create a client object!
        System.out.println("Client <host> <rtpPort> [<port>] <videoFile>");
        System.exit(1);
    }

    /** 
     * Starting point of the program.
     * @param args[0] Hostname running the RTP server
     * @param args[1] RTP receive port!
     * @param args[2] Server port!
     * @param args[3] Name of the video file to play
     */
    public static void main(String args[]) {

        // Check for number of args.
        if (args.length < 3 || args.length > 4) {
            Client.printUsageAndExit();
        }

        Client client = null;
        try {
            client = new Client(args);
        } catch (UnknownHostException uhe) {
            System.out.println(uhe.getMessage());
            System.exit(2);
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
            System.exit(3);
        }

        // Go, go, go!
        client.setInitState();
    }


    /**
     * Handler for the 'setup' button.
     */
    class setupButtonListener implements ActionListener{
        /**
         * Perform an action when an event occurs!
         * @param e the event that started this whole mess!
         */
        public void actionPerformed(ActionEvent e){
            // TODO
        }
    }

    /**
     * Handler for Play Button
     */
    class playButtonListener implements ActionListener {
        /**
         * Perform an action when an event occurs!
         * @param e the event that started this whole mess!
         */
        public void actionPerformed(ActionEvent e) {
            // TODO
        }
    }

    /**
     * Handler for Pause Button
     */
    class pauseButtonListener implements ActionListener {
        /**
         * Perform an action when an event occurs!
         * @param e the event that started this whole mess!
         */
        public void actionPerformed(ActionEvent e){

            // TODO
    }


    /**
     * Handler for Teardown Button
     */
    class tearButtonListener implements ActionListener {
        /**
         * Perform an action when an event occurs!
         * @param e the event that started this whole mess!
         */
        public void actionPerformed(ActionEvent e){

            // TODO: Teardown request!!
            System.exit(0);
        }
    }



    /**
     * Handler for the Timer. Gets RTP packets and displays them in the UI.
     */
    class timerListener implements ActionListener {
        /**
         * Perform an action when an event occurs!
         * @param e the event that started this whole mess!
         */
        public void actionPerformed(ActionEvent e) {
      
            //Construct a DatagramPacket to receive data from the UDP socket
            receivePacket = getDgPacket();
            try{
                getRtpSocket().receive(receivePacket);
                //create an RTPpacket object from the Datagram packet.
                RTPpacket rtpPacket = new RTPpacket(receivePacket.getData(),
                                                    receivePacket.getLength());

                // Get the payload bitstream from the RTPpacket object
                int payload_length = rtpPacket.getpayload_length();
                byte [] payload = new byte[payload_length];
                rtpPacket.getpayload(payload);

                // Get an Image object from the payload bitstream
                Toolkit toolkit = Toolkit.getDefaultToolkit();
                Image image = toolkit.createImage(payload, 0, payload_length);
    
                // Display the image as an ImageIcon object
                icon = new ImageIcon(image);
                iconLabel.setIcon(icon);
            } catch (InterruptedIOException iioe){
                //System.out.println("Nothing to read");
            } catch (IOException ioe) {
                System.out.println("IOException caught: " + ioe.getMessage());
            }
        }
    }

    /**
     * Parse Server Response. 
     */
    private int parseServerResponse() {
        int reply_code = 0;

        try {
            // Parse status line and extract the reply_code:
            String StatusLine = scanIn.nextLine();
    
            StringTokenizer tokens = new StringTokenizer(StatusLine);
            tokens.nextToken(); //skip over the RTSP version number
            reply_code = Integer.parseInt(tokens.nextToken());
      
            // If reply code is OK get and print the 2 other lines
            if (reply_code == Stream.OKAY) {
                String SeqNumLine = scanIn.nextLine();
                System.out.println(SeqNumLine);
      
                String SessionLine = scanIn.nextLine();
                System.out.println(SessionLine);
    
                // If state == State.INIT get the Session Id from SessionLine
                tokens = new StringTokenizer(SessionLine);
                tokens.nextToken(); // Skip over the Session:
                setRtspID(Integer.parseInt(tokens.nextToken()));
            }
        } catch(IllegalStateException | NumberFormatException |
                NoSuchElementException ex) {
            System.out.println("Error Parsing the server response: " + 
                               ex.getMessage());
            System.exit(5);
        }
        return(reply_code);
    }


    /**
     * Write a request to the RTSP socket.
     * @param requestType the type of request we are making.
     */
    private void sendRtspRequest(String requestType) {
        try {
            // TODO
            // Write the request line!
            // Write the CSeq line: 

            /*
             * Check if requestType is equal to "SETUP" and  write the
             * transport line advertising to the server the port used to 
             * receive the RTP packets rtpReceivePort
             *
             * Otherwise write the Sesion line from the rtspID field
             */
            }
            scanOut.flush();
        } catch(IOException ioe) {
            System.out.println("IOException caught : " + ioe);
            System.exit(1);
        }
    }
}
