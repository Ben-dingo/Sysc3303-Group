/*****************************************************************************
 * @Author: Omar Dawoud
 * @Updated: Saturday April 7th, 2018
 * 
 * @Purpose: This class receives print requests from all the thread classes
 * it prints various information depending on the packet received
 */
import java.net.DatagramPacket;
import java.nio.charset.StandardCharsets;

public class packetPrint
{
	//this method prints all relevant information about a received packet. It also prints the string it was passed to specify where the packet came from
	public static String Print(String info, DatagramPacket packet)
	{	
		String returns = "\n";

		boolean fileprint = false;
		byte[] received = packet.getData();

		String filename = new String(packet.getData(),StandardCharsets.UTF_8);
		if(filename.length() >= 9) {filename = filename.substring(9);}
		packetLength(packet);

		returns = (info + " as Bytes: ");
		for(int j = 0; j < packet.getLength(); j++)
		{
			returns += (received[j] + " ");
		}
		returns += ("\n");

		returns += ("IP Address: " + packet.getAddress() + "\n");
		returns += ("Port number: " + packet.getPort() + "\n");

		String packetType = "";



		if(received[0] == 0x10){ 
			packetType = "DATA";//Data packet
			fileprint = true;
		}

		else if(received[0] == 0x11)
			packetType = "ACK";//Acknowledge packet

		else if(received[0] == 0x01) {
			packetType = "RRQ";//Read request
			fileprint = true;
		}
		else if(received[0] == 0x02) {
			packetType = "WRQ";//write request
			fileprint = true;
		}

		else
			packetType = "ERROR";

		returns += ("Packet type: " + packetType + "\n");
		returns += ("Packet Length: " + packet.getLength() + "\n");
		if(fileprint){returns += ("Text in file: " + filename + "\n");}
		returns += ("Mode: Verbose\n");

		return returns;
	}

	//this sets the packet length to the first 0x00 byte that appears in the packet, 0x00 only appears as the termination character in this system
	public static void packetLength(DatagramPacket packet)
	{
		byte[] received = packet.getData();
		for(int j = 0; j < received.length; j++)
		{
			if(received[j] == 0x00)
			{
				packet.setLength(j+1);
				break;
			}
		}
	}
	
	//this class returns an int count of the data bytes, This is used to create a substring of just the important data in server and client
	public static int filenameLength(DatagramPacket packet)
	{
		byte[] received = packet.getData();
		for(int j = 0; j < received.length; j++)
		{
			if(received[j] == 0x00)
			{
				packet.setLength(j);
				return(j);
			}
		}
		return 512;
	}
}
