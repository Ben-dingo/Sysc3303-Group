/*****************************************************************************
 * @Author: Ben St.Pierre
 * @Updated: Saturday February 3rd, 2018
 * 
 * @Purpose: This class receives all the packets sent by the error sim. It creates
 * a new server thread to handle each incoming packet, once it passes on the packet
 * to the new thread it returns to waiting for the next packet
 */
import java.net.*;

public class MasterServer extends Thread
{	
	boolean mode;
	
	//creates master server thread
	public MasterServer(boolean mode)
	{
		this.mode = mode;
	}

	//below is the method that runs when the thread starts, it just
	//catches errors in the method it calls
	public void run() {
		try {
			MasterPurpose();
		} catch (Exception e) {
			System.out.println("MasterServer has failed");
		}
	}
	
	public void MasterPurpose() throws Exception
	{
		DatagramSocket socketR = new DatagramSocket(69,InetAddress.getLocalHost());
		DatagramPacket packetR = new DatagramPacket(new byte[12],12);
		DatagramPacket packetS = packetR;
		
		while(true)
		{
			socketR.receive(packetR);
			if(this.mode) {packetPrint.Print("Received from Host",packetR);}
			
			String message = new String(packetR.getData());
			if(message.equals("00ShutDown00"))//shutdown thread
			{
				System.out.println("MasterServer understands");
				socketR.close();
				Thread.currentThread().interrupt();
				break;
			}
			
			DatagramSocket newSocket = new DatagramSocket();
			packetS.setPort(newSocket.getLocalPort());
			Server newThread = new Server(this.mode,newSocket,newSocket.getLocalPort());//creates server thread
			if(mode) {System.out.println("a new server has been made with ID " + newSocket.getLocalPort());}
			newThread.start();
			if(this.mode) {packetPrint.Print("Sending to sub server",packetS);}
			socketR.send(packetS);//sends packet to new server thread
		}
	}
}
