/*****************************************************************************
 * @Author: Ben St.Pierre
 * @Updated: Saturday February 3rd, 2018 by Jozef Tierney
 * 
 * @Purpose: This class is meant to send datagramPackets to the Error sim class
 * who then sends it to the server, then receive packets back from the server.
 * The user is now prompted to create the packets themselves, WRQ, RRQ, and
 * Termination packets can be sent
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
	
	//creates client thread
	public Client(boolean mode,boolean shutoff)
	{
		this.mode = mode;
		this.shutoff = shutoff;
	}
	
	//below is the method that runs when the thread starts, it just
	//catches errors in the method it calls
	public void run()
	{
		try {
			ClientPurpose();
		} catch (Exception e) {
			System.out.println("client has failed");
		}
	}
	
	/**
	 * Returns a string representation of the client data.
	 */
	public String toString() {
		String s = "Function: " + "\n" + function + "\n";
		s = s + "Message: " + "\n" + message + "\n";
		return s;
	}
	
	public void ClientPurpose() throws Exception
	{
		DatagramSocket socket = new DatagramSocket();
		InetAddress localHostAddress = InetAddress.getLocalHost();
		
		DatagramPacket packetS = new DatagramPacket(new byte[512],512,localHostAddress,23);
		DatagramPacket packetR = new DatagramPacket(new byte[1],1);//all packets and sockets created
		
		while(true)
		{
			//byte array to become packet data
			//currently byte array is only 12 bytes long this is due to issues with
			//this will be dealt with in iteration 2
			
			while(true) 
			{
				if(shutoff == true) {break;}
				System.out.println("Would you like to read, write or quit?");
				String temp = reader.next();//prompts user for input
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
				System.out.println("Enter directory.");
				message = reader.next();
				
				if(message.toLowerCase().equals("quit"))
				{
						System.out.println("Shutting down server.");
						shutoff = true;
						break;
				}
				else if(message != "") {
					message = (packetFile.importText(message));
					break;
				}
				else {
					System.out.println("A message must be entered to procede.");
				}
				
			}
			if(shutoff == true)//performs shutdown for all running threads
			{
				message = "00ShutDown00";
				byte[] toSend = message.getBytes();
				packetS.setData(toSend);
				packetS.setLength(12);
				socket.send(packetS);
				Thread.currentThread().interrupt();
				break;
			}
			else
			{
				byte[] file = message.getBytes();
				byte[] toSend = new byte[file.length + 3];
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
					packetS.setLength(toSend.length);
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
