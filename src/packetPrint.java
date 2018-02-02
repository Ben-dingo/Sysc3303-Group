import java.net.DatagramPacket;

public class packetPrint
{
	//basic method to system.out.print() packet info in both string and byte form
		public static void Print(String info, DatagramPacket packet)
		{
			byte[] received = packet.getData();
			System.out.println(info + " as String: " + new String(received));
			//above print causes issue where bytes for 0,1, and 2 show as squares but seems better than alternative
			//System.out.println(info + " as String: " + received);//the alternative
			System.out.print(info + " as Bytes: ");
			for(int j = 0; j < received.length; j++)
			{
				System.out.format("%02X ", received[j]);
			}
			System.out.print("\n");
		}
		
		public void printPacket(String info, DatagramPacket packet) {
			
			byte[] received = packet.getData();
			String filename =  new String(received);
			//above print causes issue where bytes for 0,1, and 2 show as squares but seems better than alternative
			//System.out.println(info + " as String: " + received);//the alternative
			System.out.print(info + " as Bytes: ");
			for(int j = 0; j < received.length; j++)
			{
				System.out.format("%02X ", received[j]);
			}
			System.out.print("\n");
			
			System.out.println("IP Address: " + packet.getAddress());
			System.out.println("Port number: " + packet.getPort());
			
			String packetType = "";
			String packetName = "NaN";
			
			
			if(received.length == 1) {
				if(received[0] == 0x00) 
					packetType = "DATA";
				
				else if(received[0] == 0x01)
					packetType = "ACK";
			}
			else if(received[1] == 0x00) {
					packetType = "RRQ";
					byte[] filenameArray = new byte [received.length-2];
					System.arraycopy(received, 2, filenameArray, 0, received.length-2);
					packetName = new String(filenameArray);
			}
			else if(received[1] == 0x01) {
					packetType = "WRQ";
					byte[] filenameArray = new byte [received.length-2];
					packetName = new String(filenameArray);
			}
		
			else
				packetType = "ERROR";
			
			System.out.println("Packet type: " + packetType);
			System.out.println("Filename: " + packetName);
			
		}
}
