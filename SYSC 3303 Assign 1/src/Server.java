import java.io.IOException;
import java.net.*;
import java.util.Arrays;
/***
 * 
 * Server Class
 *
 */
public class Server {

	private DatagramSocket socket;
	private int port = 69;
	private byte[] response = new byte[100];
	private boolean valid = true;
	private String type = "";

	public Server() {
		initalize();
	}

	/****
	 * Sets up the socket the will be used to send and receive data from the host
	 */
	public void initalize() {
		try {
			socket = new DatagramSocket(port);
			System.out.println("Server is on and running!");
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	/***
	 * Receives a request from the host and send back the right type of response
	 * @param receivePacket
	 */
	public void receiveAndResponsed() {

		try {
			//Creates DatagramPacket to receive data from the host
			byte[] data = new byte[100];
			DatagramPacket receivePacket = new DatagramPacket(data, data.length);
			System.out.println("Server: Waiting for Packet!");

			try {
				System.out.println("Waiting...");
				socket.receive(receivePacket);
			} catch (IOException e) {
				System.out.print("IO Exception: likely:");
				System.out.println("Receive Socket Timed Out.\n" + e);
				e.printStackTrace();
				System.exit(1);
			}
			System.out.println("Server: Packet received!");

			// Server prints out what is has received
			System.out.println("Server received (bytes): " + receivePacket.getData());
			System.out.println("Server received (string): " + new String(receivePacket.getData(), 0, receivePacket.getLength()));
			System.out.println();

			// Checks to see if the request is valid.
			valid = isValid(data);
			if (!valid) { // Terminates server if invalid request
				System.out.println("Invalid Request. Server Terminates!");
				throw new IllegalArgumentException("Invalid Request Exception");
			}

			// Sets the response that will be sent back and creates packet
			if (type == "read") {
				response = new byte[] { 0, 3, 0, 1 };
			} else if (type == "write") {
				response = new byte[] { 0, 4, 0, 0 };
			}
			DatagramPacket sendPacket = new DatagramPacket(response, response.length, receivePacket.getAddress(),
					receivePacket.getPort());

			// Prints out response packet that will be sent
			System.out.println("Server: Response to send:");
			System.out.println("Response(byte): " + response);
			System.out.print("Response(String): ");
			for (int i = 0; i < 4; i++) {
				System.out.print(response[i]);
			}
			System.out.println();

			// Sends Response to request back to the host
			DatagramSocket responseSocket = new DatagramSocket();
			responseSocket.send(sendPacket);
			responseSocket.close();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println();
	}

	/***
	 * Checks to see if the request is valid and set the request type
	 * @param data
	 * @return
	 */
	public boolean isValid(byte[] data) {
		byte[] msg = Arrays.copyOf(data, data.length);
		
		//Checks the data byte array that has been received
		if(msg[0] != 48) {		
			return false;
		}
		if(msg[msg.length -1] != 0) {
			return false;
		}
		if(msg[1] != 49 && msg[1] != 50) {
			return false;
		}
		
		//Sets is as a read/ write request
		if(msg[0] == 48 && msg[1] == 49) {
			type = "read";
		}else if(msg[0] == 48 && msg[1] == 50) {
			type = "write";
		}		
		return true;
	}

	/***
	 * Closes all open sockets
	 */
	public void close() {
		socket.close();
		System.out.println("Socket Closed!");
	}

	public static void main(String[] args) {
		Server s = new Server();
		while (true) {
			s.receiveAndResponsed();
		}
	}

}
