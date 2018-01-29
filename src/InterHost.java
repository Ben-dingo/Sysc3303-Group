/*****************************************************************************
 * @Author: Ben St.Pierre
 * @Updated: Saturday January 20th, 2018
 * 
 * @Purpose: This class is meant to function as a middle man between the
 * server class and the client class, it receives packets from the client, 
 * prints the data, and sends the packets to the server. The server then
 * sends a new packet which is to be printed just like the other packet then
 * sent over to the client. 
 */
import java.net.*;
public class InterHost extends Thread
{
	public void run()
	{
		try {
			ErrorSimPurpose();
		} catch (Exception e) {
			System.out.println("Error Sim fucked up");
		}
	}
	
	public void ErrorSimPurpose() throws Exception
	{
			DatagramSocket socketR = new DatagramSocket(23,InetAddress.getLocalHost());
			DatagramSocket socketS = new DatagramSocket();
			InetAddress localHostAddress = InetAddress.getLocalHost();
			DatagramPacket packetR = new DatagramPacket(new byte[12],12);
			
			while(true)
			{
				socketR.receive(packetR);
				packetPrint.Print("Received from Client",packetR);
				
				DatagramPacket packetS = new DatagramPacket(packetR.getData(),packetR.getLength(),localHostAddress,69);
				packetPrint.Print("Sending to Server",packetS);
				socketS.send(packetS);
				
				DatagramPacket ServerPacketR = new DatagramPacket(new byte[4],4);
				socketS.receive(ServerPacketR);
				packetPrint.Print("Received from Server", ServerPacketR);
				
				DatagramPacket ServerPacketS = new DatagramPacket(ServerPacketR.getData(),ServerPacketR.getLength(),localHostAddress,packetR.getPort());
				DatagramSocket HostSocketS = new DatagramSocket();//makes socket specifically for next send
				packetPrint.Print("Sending to Client",ServerPacketS);
				HostSocketS.send(ServerPacketS);
				socketR.close();
			}
	}
}