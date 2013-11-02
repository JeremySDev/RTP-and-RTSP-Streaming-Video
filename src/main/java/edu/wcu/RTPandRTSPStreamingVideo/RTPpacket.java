package edu.wcu.RTPandRTSPStreamingVideo;

/**
 * RTPpacket handles the creation and use of RTP packets
 *
 * @author Jeremy Stilwell
 * @author Alisha Hayman
 * @version 10/26/13.
 */
//class RTPpacket
public class RTPpacket
{
    //size of the RTP header:
    private static int HEADER_SIZE = 12;

    //Fields that compose the RTP header
    public int Version;
    public int Padding;
    public int Extension;
    public int CC;
    public int Marker;
    public int PayloadType;
    public int SequenceNumber;
    public int TimeStamp;
    public int Ssrc;

    //Bitstream of the RTP header
    public byte[] header;

    //size of the RTP payload
    public int payload_size;

    //Bitstream of the RTP payload
    public byte[] payload;

    /**
     * Default RTPpacket Constructor that sets up certain fields to their
     * default values.
     */
    private RTPpacket()
    {
        //fill by default header fields:
        Version = 2;
        Padding = 0;
        Extension = 0;
        CC = 0;
        Marker = 0;
        Ssrc = 0;
    }

    /**
     * Construct an  RTPpacket object from header fields and payload bitstream.
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
        SequenceNumber = framenb;
        TimeStamp = time;
        PayloadType = pType;

        //build the header bitstream:
        header = new byte[HEADER_SIZE];

        //fill the RTP header and payload
        header[0] = (byte) (Version << 6 | Padding << 5 | Extension << 4 | CC);
        header[1] = (byte) (Marker << 7 | PayloadType);
        header[2] = (byte) (SequenceNumber >> 8);
        header[3] = (byte) (SequenceNumber & 0xFF);
        header[4] = (byte) (TimeStamp >> 24);
        header[5] = (byte) ((TimeStamp >> 16) & 0xFF);
        header[6] = (byte) ((TimeStamp >> 8) & 0xFF);
        header[7] = (byte) (TimeStamp & 0xFF);
        header[8] = (byte) (Ssrc >> 24);
        header[9] = (byte) ((Ssrc >> 16) & 0xFF);
        header[10] = (byte) ((Ssrc >> 8) & 0xFF);
        header[11] = (byte) (Ssrc & 0xFF);

        payload_size = data_length;
        payload = new byte[data_length];

        for (int i = 0; i < payload_size; i++)
        {
            payload[i] = data[i];
        }
    }

    /**
     * Constructor of an RTPpacket object from the packet bitsream
     *
     * @param packet a byte array of packet data
     * @param packet_size the size of the byte array
     */
    public RTPpacket(byte[] packet, int packet_size)
    {
        //fill default fields:
        this();

        if (packet_size >= HEADER_SIZE)
        {
            header = new byte[HEADER_SIZE];

            for (int i = 0; i < HEADER_SIZE; i++)
            {
                header[i] = packet[i];
            }

            payload_size = packet_size - HEADER_SIZE;
            payload = new byte[payload_size];

            for (int i = HEADER_SIZE; i < packet_size; i++)
            {
                payload[i - HEADER_SIZE] = packet[i];
            }

            PayloadType = header[1] & 127;
            SequenceNumber =
                    unsignedInt(header[3]) + 256 * unsignedInt(header[2]);
            TimeStamp =
                    unsignedInt(header[7]) + 256 * unsignedInt(header[6]) +
                            65536 * unsignedInt(header[5]) +
                            16777216 * unsignedInt(header[4]);
        }

        //fill changing header fields:

        //build the header bitstream:

        //fill the RTP header and payload
    }


    //--------------------------
    //getPayload: return the payload bitstream of the RTPpacket and its size
    //--------------------------
    public int getPayload(byte[] data)
    {
        for (int i = 0; i < payload_size; i++)
        {
            data[i] = payload[i];
        }
        return (payload_size);
    }

    //--------------------------
    //getPayload_Length: return the length of the payload
    //--------------------------
    public int getPayload_Length()
    {
        return (payload_size);
    }

    //--------------------------
    //getLength: return the total length of the RTP packet
    //--------------------------
    public int getLength()
    {
        return (payload_size + HEADER_SIZE);
    }

    //--------------------------
    //getPacket: returns the packet bitstream and its length
    //--------------------------
    public int getPacket(byte[] packet)
    {
        //construct the packet = header + payload
        for (int i = 0; i < HEADER_SIZE; i++)
        {
            packet[i] = header[i];
        }
        for (int i = 0; i < payload_size; i++)
        {
            packet[i] = payload[i];
        }
        //return total size of the packet
        return (payload_size + HEADER_SIZE);
    }

    //--------------------------
    //print headers without the SSRC
    //--------------------------
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

    //return the unsigned value of 8-bit integer nb
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
