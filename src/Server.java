/*****************************************************************************
 * @Author: Ben St.Pierre
 * @Updated: Saturday January 20th, 2018
 * 
 * @Purpose: This class is meant to receive packets through the master server.
 * it receives read and write requests, prints them, creates confirmational
 * packets, prints those, and finally sends it back to the host. currently the
 * server thread closes itself once it's handled one packet
 */
import java.net.*;
import java.nio.charset.StandardCharsets;
public class Server extends Thread 
{
	boolean mode;
	int ID;
	DatagramSocket socketR;
	
	//creates server thread
	public Server(boolean mode,DatagramSocket socketR,int ID)
	{
		this.mode = mode;
		this.socketR = socketR;
		this.ID = ID;
	}
	
	//below is the method that runs when the thread starts, it just
	//catches errors in the method it calls
	public void run()
	{
		try {
			ServerPurpose();
		} catch (Exception e) {
			System.out.println("Server has failed");
		}
	}

	public void ServerPurpose() throws Exception
	{
		DatagramPacket packetR = new DatagramPacket(new byte[512],512);
		InetAddress localHostAddress = InetAddress.getLocalHost();
		DatagramPacket packetS = new DatagramPacket(new byte[1],1,localHostAddress,23);
		
		while(true)
		{	
			socketR.receive(packetR);
			byte[] received = packetR.getData();
			if(received[0] == 0x01)//if its a reading packet
			{
				byte[] returning = new byte[]{0x00};
				packetS.setData(returning);
			}
			else if(received[0] == 0x02)//if its a writing packet
			{
				byte[] returning = new byte[]{0x01};
				packetS.setData(returning);
			}
			else {throw new Exception("InvalidException");}//if it's invalid
			
			
			socketR.send(packetS);
		}
	}
	
	public int getID()
	{
		return this.ID;
	}
}
