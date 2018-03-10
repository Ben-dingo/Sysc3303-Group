/*****************************************************************************
 * @Author: Omar Dawoud
 * @Updated: Friday March 9th, 2018 by Ben St.Pierre
 * 
 * @Purpose: This class receives print requests from all the thread classes
 * it prints various information depending on the packet received. As of the latest
 * iteration, the Print method instead returns a string of what it was going to print
 * to return the the GUI of the thread that called it.
 */
import java.net.DatagramPacket;
import java.nio.charset.StandardCharsets;

public class packetPrint
{
		public static String Print(String info, DatagramPacket packet)
		{	
			String returns = "\n";
			
			boolean fileprint = false;
			byte[] received = packet.getData();
			
			String filename = new String(packet.getData(),StandardCharsets.UTF_8);
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
			
			
			if(received.length == 1) {
				if(received[0] == 0x00) 
					packetType = "DATA";//Data block, currently just a packet
				
				else if(received[0] == 0x01)
					packetType = "ACK";//Acknowledge block, currently just a packet
			}
			else if(received[1] == 0x01) {
					packetType = "RRQ";//Read request
					fileprint = true;
			}
			else if(received[1] == 0x02) {
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
		
		//this method checks for the end of a packet to adjust the length of data sent
		public static void packetLength(DatagramPacket packet)
		{
			boolean one = false;
			byte[] received = packet.getData();
			for(int j = 0; j < received.length; j++)
			{
				if(received[j] == 0x00 && one == false)
				{
					one = true;
				}
				else if (received[j] == 0x00 && one == true)
				{
					packet.setLength(j);
					break;
				}
				else
				{
					one = false;
				}
			}
		}
}
