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
}
