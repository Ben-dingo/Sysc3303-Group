
/*****************************************************************************
 * @Author: Ben St.Pierre & Noor Ncho
 * @Updated: March 9th, 2018
 * 
 * @Purpose: This class is meant to function as a middle man between the
 * server class and the client class, it receives packets from the client, 
 * prints the data, and sends the packets to the server. The server then
 * sends a new packet which is to be printed just like the other packet then
 * sent over to the client. 
 */
import java.net.*;
import java.util.*;

public class ErrorSim extends Thread {
	boolean mode;
	Random rand;

	public ErrorSim() {

	}

	public ErrorSim(boolean mode) {
		this.mode = mode;
	}

	public void run() {
		try {
			ui();
			//ErrorSimPurpose();
		} catch (Exception e) {
			System.out.println("Error Sim has failed");
		}
	}

	public void ErrorSimPurpose() throws Exception {
		DatagramSocket socketR = new DatagramSocket(23, InetAddress.getLocalHost());
		DatagramSocket socketS = new DatagramSocket();
		InetAddress localHostAddress = InetAddress.getLocalHost();
		DatagramPacket packetR = new DatagramPacket(new byte[12], 12);

		while (true) {
			socketR.receive(packetR);
			if (this.mode) {
				packetPrint.Print("Received from Client", packetR);
			}

			DatagramPacket packetS = new DatagramPacket(packetR.getData(), packetR.getLength(), localHostAddress, 69);
			if (this.mode) {
				packetPrint.Print("Sending to Server", packetS);
			}
			socketS.send(packetS);

			String message = new String(packetR.getData());
			if (message.equals("00ShutDown00")) {
				System.out.println("ErrorSim understands");
				socketR.close();
				Thread.currentThread().interrupt();
				break;
			}

			DatagramPacket ServerPacketR = new DatagramPacket(new byte[1], 1);
			socketR.receive(ServerPacketR);
			if (this.mode) {
				packetPrint.Print("Received from Server", ServerPacketR);
			}

			DatagramPacket ServerPacketS = new DatagramPacket(ServerPacketR.getData(), ServerPacketR.getLength(),
					localHostAddress, packetR.getPort());
			DatagramSocket HostSocketS = new DatagramSocket();// makes socket specifically for next send
			if (this.mode) {
				packetPrint.Print("Sending to Client", ServerPacketS);
			}
			HostSocketS.send(ServerPacketS);

		}
	}

	public void errorInterface() {
		System.out.println("What do you want to simulate?");
		System.out.println("1: lose a packet     2: delay a packet    3: duplicate a packet      0: quit");
		Scanner s = new Scanner(System.in);
		String input = s.nextLine();

		switch (input) {
		case "1":
			int type = rand.nextInt((3 - 1) + 1) + 1;
			lostSimError(type);
			break;
		case "2":
			System.out.println("Not yet implemented");
			// delaySimError();
			break;
		case "3":
			System.out.println("Not yet implemented");
			break;
		case "0":
			System.out.println("\n Error Simulator Shutting down. GoodBye!");
			System.exit(0);
			break;
		}
	}

	public void lostSimError(int type) {
		DatagramPacket simServer, simClient;
		DatagramSocket simSocket;
		try {
			simSocket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
			System.exit(1);
		}

		System.out.println("A packet has been lost");

		switch (type) {
		case 1:
			System.out.println("Request type Packet seems to have been lost.");
			break;
		case 2:
			System.out.println("An ACK packet seems to have been lost.");
			break;
		case 3:
			System.out.println("A DATA packet seems to have been lost.");
			break;
		}

	}

	public void delaySimError() {
		System.out.println("Not yet implemented");
	}
	
	public void duplicateSimError() {
		System.out.println("Not yet implemented");
	}

	/**************************************************************************/
	public  void ui() {
		//ErrorSim e = this;
		Scanner scan = new Scanner(System.in);
		String s = scan.nextLine();

		System.out.println("What function do you want to operate in?");
		System.out.println("(N)ormal mode or (E)rror Sim mode or (Q)uit.");
		if (s.equalsIgnoreCase("N")) {
			System.out.println("Waiting to receive packets for forwarding.. ");
			try {
				ErrorSimPurpose();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (s.equalsIgnoreCase("E")) {
			errorInterface();
		}else if(s.equalsIgnoreCase("Q")) {
			System.out.println("\n Error Simulator Shutting down. GoodBye!");
			System.exit(0);
		}
	}
}