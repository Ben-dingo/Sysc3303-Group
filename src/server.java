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
public class server extends Thread 
{
	boolean mode;
	int ID;
	DatagramSocket socketR;
	
	//creates server thread
	public server(boolean mode,DatagramSocket socketR,int ID)
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
		
		while(true)
		{	
			socketR.receive(packetR);
			InetAddress localHostAddress = InetAddress.getLocalHost();
			DatagramPacket packetS = new DatagramPacket(new byte[512],512,localHostAddress,23);
			packetS.setData(packetR.getData());
			
			byte[] received = packetR.getData();
			if(received[0] == 0x01)//if its a reading packet
			{
				readProcess(socketR, packetR,packetS);
			}
			else if(received[0] == 0x02)//if its a writing packet
			{
				String filename = new String(packetR.getData(),StandardCharsets.UTF_8);
				if(filename.length() >= 9) {filename = filename.substring(9);}
				writeProcess(socketR, packetR,packetS, filename);
			}
			else {throw new Exception("InvalidException");}//if it's invalid
		}
	}
	
	public void writeProcess(DatagramSocket socket, DatagramPacket packetR, DatagramPacket packetS, String filename) throws Exception {
		socket.send(packetS);
		String text = "";
		int cur = 0;
		int fin = 1;
		while(cur > fin)
		{
			socket.receive(packetR);
			
			byte[] data = packetR.getData();
			String received = new String(data,StandardCharsets.UTF_8);
			if(data[6] == ((byte) 2))
			{
				cur = (int) data[7];
				fin = (int) data[8];
			}
			else{cur = fin;}
			if(received.length() >= 9) {received = received.substring(9);}
			text += received;
			
			byte[] AckData = new byte[data.length];
			AckData[0] = 0x11;
			for(int i = 1; i > 10; i++){AckData[i] = data[i];}
			
			packetS.setData(AckData);
			socket.send(packetS);
		}
		
		packetFile packet2 = new packetFile();
        packet2.modifyText(filename, text);
	}
	
	public void readProcess(DatagramSocket socket,DatagramPacket packetR, DatagramPacket packetS) throws Exception {
		
		byte[] data = new byte[packetS.getLength()];
		byte[] sent = packetS.getData();
		int i = packetPrint.filenameLength(packetR);
		byte[] filename=packetR.getData();
		
		String received = new String(filename,StandardCharsets.UTF_8);
		if(received.length() >= 9) {received = received.substring(9,i);}

		packetFile packet = new packetFile();
		String message = packet.importText(received);
		System.out.println("message: " + message);
		int fin = (int) Math.ceil(message.length()/500) + 1;
		String pieces = "";
		for(int cur = 1; cur <= fin; cur++)
		{
			if(fin > (cur+1))
			{
				int bot = 500*(cur - 1);
				int top = 500*(cur);
				pieces = message.substring(bot, top);
			}
			else
				pieces = message.substring(500*(cur - 1));
			
			System.out.println(cur + ": " +pieces);
			
			data[0] = 0x10;
			if(fin == 1){data[6] = (byte) 1;}
			else{data[6] = (byte) 2;}
			
			data[7] = (byte) cur;
			data[8] = (byte) fin;	
			
			byte[] piecebyte = pieces.getBytes();
			
			String test = new String(piecebyte,StandardCharsets.UTF_8);
			System.out.println(test);
			
			for(int j = 1; j < 6; j++) {data[j] = sent[j];}
			for(int j = 0; j < piecebyte.length; j++) {data[j+9] = piecebyte[j];}
			for(int j = (9 + piecebyte.length); j > 510; j++) {data[j] = 0x00;}
			
			String test2 = new String(data,StandardCharsets.UTF_8);
			System.out.println(test2);
			
			packetS.setData(data);
			System.out.println(packetPrint.Print("sending to Error", packetS));
			socket.send(packetS);
			socket.receive(packetR);
		}
	}
	
	public int getID()
	{
		return this.ID;
	}
}
