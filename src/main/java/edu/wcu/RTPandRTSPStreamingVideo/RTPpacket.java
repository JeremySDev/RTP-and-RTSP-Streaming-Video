package edu.wcu.RTPandRTSPStreamingVideo;

/**
 * RTPpacket handles the creation and use of RTP packets
 *
 * @author Jeremy Stilwell
 * @author Alisha Hayman
 * @version 10/26/13.
 */
public class RTPpacket
{

    //size of the RTP header:
    private static int HEADER_SIZE = 12;
    private int[] header = {4};

    //--------------------------
    // Construct an  RTPpacket object from header fields and payload bitstream
    //--------------------------
    public RTPpacket(int pType, int framenb, int time, byte[] data,
                     int data_length)
    {
        //fill by default header fields:

        //fill changing header fields:

        //build the header bitstream:

        //fill the RTP header and payload
    }

    //--------------------------
    //Constructor of an RTPpacket object from the packet bitsream
    //--------------------------
    public RTPpacket(byte[] packet, int packet_size)
    {
        //fill default fields:

        //fill changing header fields:

        //build the header bitstream:

        //fill the RTP header and payload
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

    public void getpayload(byte[] payload)
    {
    }

    public int getpayload_length()
    {
        return 4;
    }
    public int getlength()
    {
        return 4;
    }
    public int getpacket(byte[] packetBits)
    {
        return 4;
    }

}
