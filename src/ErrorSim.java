/*****************************************************************************
 * @Author: Ben St.Pierre
 * @Updated: Saturday February 3rd, 2018
 * 
 * @Purpose: This class is meant to function as a middle man between the
 * server class and the client class, it receives packets from the client, 
 * prints the data, and sends the packets to the server. The server then
 * sends a new packet which is to be printed just like the other packet then
 * sent over to the client. For iteration 1 this only passes packets but later
 * it will cause errors in the packets
 */
import java.net.*;
public class ErrorSim extends Thread
{
	boolean mode;
	public ErrorSim(boolean mode)
	{
		this.mode = mode;
	}
	
	//below is the method that runs when the thread starts, it just
	//catches errors in the method it calls
	public void run()
	{
		try {
			ErrorSimPurpose();
		} catch (Exception e) {
			System.out.println("Error Sim has failed");
		}
	}
	
	public void ErrorSimPurpose() throws Exception
	{
			DatagramSocket socketR = new DatagramSocket(23,InetAddress.getLocalHost());
			DatagramSocket socketS = new DatagramSocket();
			InetAddress localHostAddress = InetAddress.getLocalHost();
			DatagramPacket packetR = new DatagramPacket(new byte[512],512);
			
			while(true)
			{
				socketR.receive(packetR);//receives packet from client
				if(this.mode) {packetPrint.Print("Received from Client",packetR);}
				
				DatagramPacket packetS = new DatagramPacket(packetR.getData(),packetR.getLength(),localHostAddress,69);
				if(this.mode) {packetPrint.Print("Sending to Server",packetS);}
				socketS.send(packetS);//passes packet along
				
				String message = new String(packetR.getData());
				if(message.equals("00ShutDown00"))//if the packet was a shutdown this is where the thread ends
				{
					System.out.println("ErrorSim understands");
					socketR.close();
					Thread.currentThread().interrupt();
					break;
				}
				
				DatagramPacket ServerPacketR = new DatagramPacket(new byte[1],1);
				socketR.receive(ServerPacketR);//receives response packet from server
				if(this.mode) {packetPrint.Print("Received from Server", ServerPacketR);}
				
				DatagramPacket ServerPacketS = new DatagramPacket(ServerPacketR.getData(),ServerPacketR.getLength(),localHostAddress,packetR.getPort());
				if(this.mode) {packetPrint.Print("Sending to Client",ServerPacketS);}
				socketS.send(ServerPacketS);//sends response to client
			}
	}
}