/*****************************************************************************
 * @Author: Omar Dawoud
 * @Updated: Saturday February 3rd, 2018
 * 
 * @Purpose: This class receives print requests from all the thread classes
 * it prints various information depending on the packet received
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
				packetType = "DATA";//Data block, currently just a packet
				fileprint = true;
			}
				
			else if(received[0] == 0x11)
				packetType = "ACK";//Acknowledge block, currently just a packet
				
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
}
