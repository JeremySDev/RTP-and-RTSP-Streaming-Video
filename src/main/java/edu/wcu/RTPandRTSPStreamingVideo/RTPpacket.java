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
    static int HEADER_SIZE = 12;

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


    //--------------------------
    //Constructor of an RTPpacket object from header fields and payload
    // bitstream
    //--------------------------
    public RTPpacket(int PType, int Framenb, int Time, byte[] data,
                     int data_length)
    {
        //fill by default header fields:
        Version = 2;
        Padding = 0;
        Extension = 0;
        CC = 0;
        Marker = 0;
        Ssrc = 0;

        //fill changing header fields:
        SequenceNumber = Framenb;
        TimeStamp = Time;
        PayloadType = PType;

        //build the header bitstream:
        //--------------------------
        header = new byte[HEADER_SIZE];

        //fill the header array of byte with RTP header fields
        //header[0] = ...
        // .....
        header[0] = (byte) (Version << 6);
        header[0] = (byte) (header[0] | Padding << 5);
        header[0] = (byte) (header[0] | Extension << 4);
        header[0] = (byte) (header[0] | CC);
        header[1] = (byte) (header[1] | Marker << 7);
        header[1] = (byte) (header[1] | PayloadType);
        header[2] = (byte) (SequenceNumber >> 8);
        header[3] = (byte) (SequenceNumber & 0xFF);
        header[4] = (byte) (TimeStamp >> 24);
        header[5] = (byte) (TimeStamp >> 16);
        header[6] = (byte) (TimeStamp >> 8);
        header[7] = (byte) (TimeStamp & 0xFF);
        header[8] = (byte) (Ssrc >> 24);
        header[9] = (byte) (Ssrc >> 16);
        header[10] = (byte) (Ssrc >> 8);
        header[11] = (byte) (Ssrc & 0xFF);

        //fill the payload bitstream:
        //--------------------------
        payload_size = data_length;
        payload = new byte[data_length];

        //fill payload array of byte from data (given in parameter of the
        // constructor)
        System.arraycopy(data, 0, payload, 0, data_length);

    }

    //--------------------------
    //Constructor of an RTPpacket object from the packet bitstream
    //--------------------------
    public RTPpacket(byte[] packet, int packet_size)
    {
        //fill default fields:
        Version = 2;
        Padding = 0;
        Extension = 0;
        CC = 0;
        Marker = 0;
        Ssrc = 0;

        //check if total packet size is lower than the header size
        if (packet_size >= HEADER_SIZE)
        {
            //get the header bitsream:
            header = new byte[HEADER_SIZE];
            System.arraycopy(packet, 0, header, 0, HEADER_SIZE);

            //get the payload bitstream:
            payload_size = packet_size - HEADER_SIZE;
            payload = new byte[payload_size];
            System.arraycopy(packet, HEADER_SIZE, payload,
                    HEADER_SIZE - HEADER_SIZE, packet_size - HEADER_SIZE);

            //interpret the changing fields of the header:
            PayloadType = header[1] & 127;
            SequenceNumber =
                    unsignedInt(header[3]) + 256 * unsignedInt(header[2]);
            TimeStamp =
                    unsignedInt(header[7]) + 256 * unsignedInt(header[6]) +
                            65536 * unsignedInt(header[5]) +
                            16777216 * unsignedInt(header[4]);
        }
    }

    //--------------------------
    //getPayload: return the payload bitstream of the RTPpacket and its size
    //--------------------------
    public int getPayload(byte[] data)
    {

        System.arraycopy(payload, 0, data, 0, payload_size);

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
        System.arraycopy(header, 0, packet, 0, HEADER_SIZE);
        System.arraycopy(payload, 0, packet, 0 + HEADER_SIZE, payload_size);

        //return total size of the packet
        return (payload_size + HEADER_SIZE);
    }

    //--------------------------
    //getTimestamp
    //--------------------------
    public int getTimestamp()
    {
        return (TimeStamp);
    }

    //--------------------------
    //getSequenceNumber
    //--------------------------
    public int getSequenceNumber()
    {
        return (SequenceNumber);
    }

    //--------------------------
    //getPayloadType
    //--------------------------
    public int getPayloadType()
    {
        return (PayloadType);
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

