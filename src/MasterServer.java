import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;

public class MasterServer extends Thread
{	
	boolean mode;
	public MasterServer(boolean mode)
	{
		this.mode = mode;
	}

	
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
			if(message.equals("00ShutDown00"))
			{
				System.out.println("MasterServer understands");
				socketR.close();
				Thread.currentThread().interrupt();
				break;
			}
			
			DatagramSocket newSocket = new DatagramSocket();
			packetS.setPort(newSocket.getLocalPort());
			Server newThread = new Server(this.mode,newSocket,newSocket.getLocalPort());
			if(mode) {System.out.println("a new server has been made with ID " + newSocket.getLocalPort());}
			newThread.start();
			if(this.mode) {packetPrint.Print("Sending to sub server",packetS);}
			socketR.send(packetS);
		}
	}
}
