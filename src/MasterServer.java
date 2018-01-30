import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MasterServer {
	
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
		DatagramPacket packetS = new DatagramPacket(new byte[4],4);
		
		while(true)
		{
			socketR.receive(packetR);
			Server newThread = new Server(this.mode);
			
			
			socketS.close();//closes the port
		}
	}
}
