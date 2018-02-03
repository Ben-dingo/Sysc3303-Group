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
	String function;
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
			System.out.println("client has failed");
		}
	}
	
	public void ClientPurpose() throws Exception
	{
		DatagramSocket socket = new DatagramSocket();
		InetAddress localHostAddress = InetAddress.getLocalHost();
		
		DatagramPacket packetS = new DatagramPacket(new byte[12],12,localHostAddress,23);
		DatagramPacket packetR = new DatagramPacket(new byte[1],1);
		
		while(true)
		{
			byte[] toSend = new byte[12];//byte array to become packet data
			//String string = "files.txt";
			
			while(true) 
			{
				if(shutoff == true) {break;}
				System.out.println("Would you like to read, write or quit?");
				String temp = reader.next();
				if(temp.toLowerCase().equals("quit")){
					System.out.println("Shutting down server.");
					this.shutoff = true;
					break;
				}
				else if(temp.toLowerCase().equals("read")) {
					function = "read";
					break;
				}
				
				else if(temp.toLowerCase().equals("write")) {
					function = "write";
					break;
				}
				else {
					System.out.println("Must be a read or a write request.");
				}
			}
			
			while(true) 
			{
				if(shutoff == true) {break;}
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
					break;
				}
				
			}
			if(shutoff == true)
			{
				message = "00ShutDown00";
				toSend = message.getBytes();
				packetS.setData(toSend);
				socket.send(packetS);
				Thread.currentThread().interrupt();
				break;
			}
			else
			{
				byte[] file = message.getBytes();
				for(int j = 0; j < file.length; j++) {
					if(function.equals("read")) {
						toSend[1] = 0x01;
					}
					else{
						toSend[1] = 0x02;
					}
					
					toSend[j+2] = file[j];//puts string into correct spot in byte array
					toSend[0] = 0x00;
					toSend[toSend.length-1] = 0x00;
					packetS.setData(toSend);
				}
				if(function.equals("read")) {
					if(this.mode) {packetPrint.Print("Reading packet",packetS);}
				}
				else{
					if(this.mode) {packetPrint.Print("Writing packet",packetS);}
				}
			}
			socket.send(packetS);
			if(shutoff == true) {break;}
			//time passes here while waiting for response from server
			socket.receive(packetR);
			if(this.mode) {packetPrint.Print("Received from Host", packetR);}
		}
	}
}
