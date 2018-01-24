import java.io.IOException;
import java.net.*;

/***
 * 
 * Host Class
 *
 */
public class Host {

	private DatagramSocket clientSocket, sendReceiveSocket;
	private int clientPort = 23;
	private int serverPort = 69;

	public Host() {
		initalize();
	}

	public void initalize() {
		try {
			clientSocket = new DatagramSocket(clientPort);
			sendReceiveSocket = new DatagramSocket();
			System.out.println("Host is on and running!");
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	/***
	 * Receives data from the client, sends it to the server and vice versa
	 */
	public void sendReceive() {
		try {
			byte[] data = new byte[100];
			InetAddress cAddress;
			int cPort;
			
			//Receives information from the client
			DatagramPacket receivePacket = new DatagramPacket(data, data.length);
			clientSocket.receive(receivePacket);
			cAddress = receivePacket.getAddress();
			cPort = receivePacket.getPort();
					
			//Prints out Request received from client that will be sent to server
			System.out.println("Host:  Received from Client: ");
			System.out.println("Request (String): " + new String(data,0,receivePacket.getLength()));
			System.out.println("Request (byte): " + receivePacket.getData());
			System.out.println();
			
			//Creates Packet to be sent to the server
			InetAddress sAddress = InetAddress.getLocalHost();
			DatagramPacket sendPacket = new DatagramPacket(receivePacket.getData(), receivePacket.getLength(),sAddress, serverPort);
			sendReceiveSocket.send(sendPacket);
			System.out.println("Host: Sent packet to server!");
			System.out.println();
			
			//Receives a packet from the server
			data = new byte[100];
			receivePacket  = new DatagramPacket(data, data.length);
			sendReceiveSocket.receive(receivePacket);
			
			//Prints out the response received from the Server
			System.out.println("Host: Response from Sever: ");
			System.out.println("Response (byte): " + receivePacket.getData());
			System.out.print("Response (String): ");
			for(int i = 0; i < 4; i++) {
				System.out.print(data[i]);
			}
			System.out.println();
			
			//Creates Packet to send response to client, creates a new Socket
			sendPacket = new DatagramPacket(receivePacket.getData(), receivePacket.getLength(), cAddress, cPort);
			DatagramSocket sendSocket = new DatagramSocket();
			sendSocket.send(sendPacket);
			System.out.println("Host: Sent Packet to Client!");
			System.out.println();
			sendSocket.close();			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		sendReceiveSocket.close();
		clientSocket.close();
		System.out.println("Socket Closed!");
	}
	
	public static void main(String[] args) {
		Host h = new Host();
		while(true) {
			h.sendReceive();
		}
	}
}
