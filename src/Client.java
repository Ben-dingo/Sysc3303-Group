/*****************************************************************************
 * @Author: Ben St.Pierre
 * @Updated: Saturday January 20th, 2018
 * 
 * @Purpose: This class is meant to send 11 datagramPackets to the local
 * host class who then sends it to the server, then receive packets back
 * from the server. 5 read packets, 5 write packets, and 1 invalid packet
 * are to be sent.
 */

import java.net.*;
public class Client extends Thread
{
	boolean mode;
	String message;
	public Client(boolean mode, String message)
	{
		this.mode = mode;
		this.message = message;
	}
	
	public void run()
	{
		try {
			ClientPurpose();
		} catch (Exception e) {
			System.out.println("client fucked up");
		}
	}
	
	public void ClientPurpose() throws Exception
	{
		DatagramSocket socket = new DatagramSocket();
		InetAddress localHostAddress = InetAddress.getLocalHost();
		
		DatagramPacket packetS = new DatagramPacket(new byte[12],12,localHostAddress,23);
		DatagramPacket packetR = new DatagramPacket(new byte[4],4);
		
		for(int i = 1; i < 12; i++)
		{
			byte[] toSend = new byte[12];//byte array to become packet data
			//String string = "files.txt";
			byte[] file = message.getBytes();
			for(int j = 0; j < file.length; j++)
				toSend[j+2] = file[j];//puts string into correct spot in byte array
			toSend[0] = 0x00;
			toSend[toSend.length-1] = 0x00;
			
			if(i == 11){//makes invalid packet
				toSend[1] = 0x00;
				packetS.setData(toSend);
				if(this.mode) {packetPrint.Print("Invalid packet",packetS);}
			}
			else if (i%2 == 0) {//makes a reading packet
				toSend[1] = 0x01;
				packetS.setData(toSend);
				if(this.mode) {packetPrint.Print("Reading packet",packetS);}
			}
			else if (i%2 == 1) {//makes a writing packet
				toSend[1] = 0x02;
				packetS.setData(toSend);
				if(this.mode) {packetPrint.Print("Writing packet",packetS);}
			}
			
			socket.send(packetS);
			//time passes here while waiting for response from server
			socket.receive(packetR);
			if(this.mode) {packetPrint.Print("Received from Host", packetR);}
		}
	}
}
