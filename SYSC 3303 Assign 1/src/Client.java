import java.io.IOException;
import java.net.*;

/***
 * 
 * Client Class
 *
 */
public class Client {

	private DatagramSocket socket;
	private int hostPort = 23;
	private String mode = "netascii";
	private String filename = "myfile.txt";

	public Client() {
		initalize();
	}

	/**
	 * 
	 */
	public void initalize() {
		try {
			socket = new DatagramSocket();
			System.out.println("Client is on and running!");
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	/***
	 * Creates the read/write/invalid request and sends it to the host
	 * @param i
	 */
	public void sendRequest(int i) {
		try {
			String request = "";
			String type = "";

			/** Creates the request **/
			// Invalid Request
			if (i == 11) {
				request += "34";
				request += filename;
				request += "5";
				request += mode;
				request += "6";
				type = "invalid";
			}
			// Read Request
			else if (i % 2 == 1) {
				request += "01";
				request += filename;
				request += "0";
				request += mode;
				request += "0";
				type = "Read";

			}
			// Write Request
			else if (i % 2 == 0) {
				request += "02";
				request += filename;
				request += "0";
				request += mode;
				request += "0";
				type = "Write";
			}
			byte[] msg = request.getBytes();

			//Creates and sends the packet
			DatagramPacket sendPacket = new DatagramPacket(msg, msg.length, InetAddress.getLocalHost(), hostPort);

			System.out.println("Client: Packet sent:");
			System.out.println(i + ") " + type + " request");
			System.out.println("Containing (bytes): " + msg);
			System.out.println("Contraining (String): " + new String(sendPacket.getData(), 0, sendPacket.getLength()));
			
			socket.send(sendPacket);

			//Receive Confirmation
			receiveConfirmation();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/***
	 * Receives the response from the server via the host and displays it
	 */
	public void receiveConfirmation() {
		try {
			byte[] msg = new byte[100];
			DatagramPacket receivePacket = new DatagramPacket(msg, msg.length);
			socket.receive(receivePacket);

			// Prints out what is has receives from Host
			System.out.println("Client: Packet received:");
			System.out.println("Client received (bytes): " + receivePacket.getData());
			System.out.print("Client received (String): ");
			for (int i = 0; i < 4; i++) {
				System.out.print(msg[i]);
			}
			System.out.println();
			
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println();
	}

	/***
	 * 
	 */
	public void close() {
		socket.close();
		System.out.println("Socket Closed!");
	}

	/***
	 * Main method to run the Client
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Client c = new Client();

		for (int i = 1; i <= 11; i++) {
			c.sendRequest(i);
		}
		c.close();
	}

}
