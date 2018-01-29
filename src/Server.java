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
	public void run()
	{
		try {
			ServerPurpose();
		} catch (Exception e) {
			System.out.println("Server fucked up");
		}
	}

	public void ServerPurpose() throws Exception
	{
		DatagramSocket socketR = new DatagramSocket(69,InetAddress.getLocalHost());
		DatagramPacket packetR = new DatagramPacket(new byte[12],12);
		DatagramPacket packetS = new DatagramPacket(new byte[4],4);
		
		while(true)
		{
			socketR.receive(packetR);
			packetPrint.Print("Received from Host", packetR);
			byte[] received = packetR.getData();
			
			if(received[1] == 0x01)//if its a reading packet
			{
				byte[] returning = new byte[]{0x00,0x03,0x00,0x01};
				packetS.setData(returning);
			}
			else if(received[1] == 0x02)//if its a writing packet
			{
				byte[] returning = new byte[]{0x00,0x04,0x00,0x01};
				packetS.setData(returning);
			}
			else {throw new Exception("InvalidException");}//if it's invalid
			
			packetPrint.Print("Returning to Host", packetS);
			packetS.setPort(packetR.getPort());
			packetS.setAddress(InetAddress.getLocalHost());//gets info on how to reach host from packet received prior
			DatagramSocket socketS = new DatagramSocket();
			socketS.send(packetS);
			socketS.close();//closes the port
		}
	}
}
