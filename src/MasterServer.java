import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

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
			System.out.println("MasterServer fucked up");
		}
	}
	
	public void MasterPurpose() throws Exception
	{
		DatagramSocket socketR = new DatagramSocket(69,InetAddress.getLocalHost());
		DatagramPacket packetR = new DatagramPacket(new byte[12],12);
		
		while(true)
		{
			socketR.receive(packetR);
			
			String message = new String(packetR.getData());
			if(message.equals("ShutDown0000"))
			{
				System.out.println("MasterServer understands");
				socketR.close();
				break;
			}
			
			int random =(int)(Math.random()*100);
			Server newThread = new Server(this.mode,packetR);
			newThread.start();
			
			socketR.close();//closes the port
		}
	}
}
