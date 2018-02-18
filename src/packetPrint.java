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
		public static void Print(String info, DatagramPacket packet)
		{	
			boolean fileprint = false;
			byte[] received = packet.getData();
			
			String filename = new String(packet.getData(),StandardCharsets.UTF_8);
			packetLength(packet);
			
			System.out.print(info + " as Bytes: ");
			for(int j = 0; j < packet.getLength(); j++)
			{
				System.out.format("%02X ", received[j]);
			}
			System.out.print("\n");
			
			System.out.println("IP Address: " + packet.getAddress());
			System.out.println("Port number: " + packet.getPort());
			
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
			
			System.out.println("Packet type: " + packetType);
			System.out.println("Packet Length: " + packet.getLength());
			if(fileprint){System.out.println("Filename: " + filename);}
			System.out.println("Mode: Verbose");
		}
		
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
