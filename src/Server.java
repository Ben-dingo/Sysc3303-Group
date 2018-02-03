/*****************************************************************************
 * @Author: Ben St.Pierre
 * @Updated: Saturday January 20th, 2018
 * 
 * @Purpose: This class is meant to receive packets through the local hosts.
 * it receives read and write requests, prints them, creates confirmational
 * packets, prints those, and finally sends it back to the host. the final
 * packet that it receives is meant to be invalid and ends the program.
 */
import java.net.*;
public class Server extends Thread
{
	boolean mode;
	int ID;
	DatagramSocket socketR;
	public Server(boolean mode,DatagramSocket socketR,int ID)
	{
		this.mode = mode;
		this.socketR = socketR;
		this.ID = ID;
	}
	
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
		DatagramPacket packetR = new DatagramPacket(new byte[12],12);
		InetAddress localHostAddress = InetAddress.getLocalHost();
		DatagramPacket packetS = new DatagramPacket(new byte[1],1,localHostAddress,23);
		socketR.receive(packetR);
		if(this.mode) {packetPrint.Print("Received from MasterServer", packetR);}
		byte[] received = packetR.getData();
		if(received[1] == 0x01)//if its a reading packet
		{
			byte[] returning = new byte[]{0x00};
			packetS.setData(returning);
		}
		else if(received[1] == 0x02)//if its a writing packet
		{
			byte[] returning = new byte[]{0x01};
			packetS.setData(returning);
		}
		else {throw new Exception("InvalidException");}//if it's invalid
		
		if(this.mode) {packetPrint.Print("Sending to Host", packetS);}
		socketR.send(packetS);
		socketR.close();
		Thread.currentThread().interrupt();
	}
	
	public int getID()
	{
		return this.ID;
	}
}
