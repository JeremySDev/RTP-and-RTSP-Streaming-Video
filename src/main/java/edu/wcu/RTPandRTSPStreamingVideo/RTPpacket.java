package edu.wcu.RTPandRTSPStreamingVideo;

/**
 * RTPpacket this class handles creating and a RTPpacket, RTP header, and
 * payload
 */
public class RTPpacket
{

    //size of the RTP header:
    private static int HEADER_SIZE = 12;

    //Fields that compose the RTP header
    private int version;
    private int padding;
    private int extension;
    private int cc;
    private int marker;
    private int payloadType;
    private int sequenceNumber;
    private int timeStamp;
    private int ssrc;

    //Bitstream of the RTP header
    private byte[] header;

    //size of the RTP payload
    private int payloadSize;

    //Bitstream of the RTP payload
    private byte[] payload;

    /**
     * Default RTPpacket Constructor that sets up certain fields to their
     * default values.
     */
    private RTPpacket()
    {
        //fill the default header fields:
        version = 2;
        padding = 0;
        extension = 0;
        cc = 0;
        marker = 0;
        ssrc = 0;
    }


    /**
     * Construct an  RTPpacket object from header fields and a payload bitstream
     *
     * @param pType       the payload type
     * @param framenb     the frame number
     * @param time        the time for the timestamp
     * @param data        a byte array of data
     * @param data_length the length of data
     */
    public RTPpacket(int pType, int framenb, int time, byte[] data,
                     int data_length)
    {
        //fill by default header fields:
        this();

        //fill changing header fields:
        payloadType = pType;
        sequenceNumber = framenb;
        timeStamp = time;

        //build the header bitstream:
        header = new byte[HEADER_SIZE];

        //fill the RTP header and payload
        header[0] = (byte) (version << 6 | padding << 5 | extension << 4 | cc);
        header[1] = (byte) (marker << 7 | payloadType);
        header[2] = (byte) (sequenceNumber >> 8);
        header[3] = (byte) (sequenceNumber & 0xFF);
        header[4] = (byte) (timeStamp >> 24);
        header[5] = (byte) ((timeStamp >> 16) & 0xFF);
        header[6] = (byte) ((timeStamp >> 8) & 0xFF);
        header[7] = (byte) (timeStamp & 0xFF);
        header[8] = (byte) (ssrc >> 24);
        header[9] = (byte) ((ssrc >> 16) & 0xFF);
        header[10] = (byte) ((ssrc >> 8) & 0xFF);
        header[11] = (byte) (ssrc & 0xFF);

        payloadSize = data_length;
        payload = new byte[data_length];

        //copy over data to payload
        System.arraycopy(data, 0, payload, 0, payloadSize);
    }

    /**
     * Constructor of an RTPpacket object from the packet bitsream
     *
     * @param packet     a byte array of packet data
     * @param packetSize the size of the byte array
     */
    public RTPpacket(byte[] packet, int packetSize)
    {
        // fill default fields:
        this();

        if (packetSize >= HEADER_SIZE)
        {
            // create a header array
            header = new byte[HEADER_SIZE];

            // copying over the header data
            System.arraycopy(packet, 0, header, 0, HEADER_SIZE);

            payloadSize = packetSize - HEADER_SIZE;
            payload = new byte[payloadSize];

            // copying over the payload
            for (int i = HEADER_SIZE; i < packetSize; i++)
            {
                payload[i - HEADER_SIZE] = packet[i];
            }

            payloadType = header[1] & 127;

            sequenceNumber =
                    unsignedInt(header[3]) + 256 * unsignedInt(header[2]);

            timeStamp = unsignedInt(header[7]) + 256 * unsignedInt(header[6])
                    + 65536 * unsignedInt(header[5])
                    + 16777216 * unsignedInt(header[4]);
        }
    }

    /**
     * getPayload creates a copy of payload.
     *
     * @param data an array of bytes that becomes a copy of payload.
     * @return payloadSize the length of the payload.
     */
    public int getPayload(byte[] data)
    {
        System.arraycopy(payload, 0, data, 0, payloadSize);
        return (payloadSize);
    }

    /**
     * getPayloadLength returns the length of the payload.
     *
     * @return the size of the payload.
     */
    public int getPayloadLength()
    {
        return (payloadSize);
    }

    /**
     * getLength returns the payload length
     *
     * @return the length of the payload
     */
    public int getLength()
    {
        return (payloadSize + HEADER_SIZE);
    }

    /**
     * getPacket returns the length of the payload. Also copies over the header
     * data and payload data to a packet.
     *
     * @param packet a byte array of packet data
     * @return int returns the length of the payload.
     */
    public int getPacket(byte[] packet)
    {
        //construct the packet = header + payload
        System.arraycopy(header, 0, packet, 0, HEADER_SIZE);
        System.arraycopy(payload, 0, packet, HEADER_SIZE, payloadSize);
        //return total size of the packet
        return (payloadSize + HEADER_SIZE);
    }

    /**
     * printHeader prints out the header data of the RTP packet.
     */
    public void printHeader()
    {
        for (int i = 0; i < (HEADER_SIZE - 4); i++)
        {
            for (int j = 7; j >= 0; j--)
            {
                if (((1 << j) & header[i]) != 0)
                {
                    System.out.print("1");
                }
                else
                {
                    System.out.print("0");
                }
            }
            System.out.print(" ");
        }
        System.out.println();
    }

    /**
     * unsignedInt return the unsigned value of 8-bit integer number.
     *
     * @param num the number to be "unsigned"
     * @return an unsigned integer
     */
    static int unsignedInt(int num)
    {
        if (num >= 0)
        {
            return (num);
        }
        else
        {
            return (256 + num);
        }
    }
}
