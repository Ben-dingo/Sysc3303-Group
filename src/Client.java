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
import java.util.Scanner;
public class Client extends Thread
{
	boolean mode;
	String message;
	Scanner reader = new Scanner(System.in);
	boolean shutoff = false;
	
	
	public Client(boolean mode,boolean shutoff)
	{
		this.mode = mode;
		this.shutoff = shutoff;
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
		
		while(true)
		{
			byte[] toSend = new byte[12];//byte array to become packet data
			//String string = "files.txt";
			while(true) 
			{
				System.out.println("Enter message.");
				message = reader.next();
				if(message.toLowerCase().equals("quit"))
				{
						System.out.println("Shutting down server.");
						shutoff = true;
						break;
				}
				else if(message != "") {
					break;
				}
				else {
					System.out.println("A message must be entered to procede.");
				}
				
			}
			if(shutoff == true)
			{
				message = "ShutDown0000";
				toSend = message.getBytes();
				packetS.setData(toSend);
				socket.send(packetS);
				Thread.currentThread().interrupt();
				break;
			}
			else
			{
				byte[] file = message.getBytes();
				for(int j = 0; j < file.length; j++)
				toSend[j+2] = file[j];//puts string into correct spot in byte array
				toSend[0] = 0x00;
				toSend[toSend.length-1] = 0x00;
			}
			if(false){//makes invalid packet
				toSend[1] = 0x00;
				packetS.setData(toSend);
				if(this.mode) {packetPrint.Print("Invalid packet",packetS);}
			}
			else if (false) {//makes a reading packet
				toSend[1] = 0x01;
				packetS.setData(toSend);
				if(this.mode) {packetPrint.Print("Reading packet",packetS);}
			}
			else if (true) {//makes a writing packet
				toSend[1] = 0x02;
				packetS.setData(toSend);
				if(this.mode) {packetPrint.Print("Writing packet",packetS);}
			}
			socket.send(packetS);
			if(shutoff == true) {break;}
			//time passes here while waiting for response from server
			socket.receive(packetR);
			if(this.mode) {packetPrint.Print("Received from Host", packetR);}
		}
	}
}
