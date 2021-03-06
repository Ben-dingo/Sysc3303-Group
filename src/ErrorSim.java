/*****************************************************************************
 * @Author: Ben St.Pierre & Noor Ncho
 * @Updated: Saturday February 3rd, 2018
 * 
 * @Purpose: This class is meant to function as a middle man between the
 * server class and the client class, it receives packets from the client, 
 * prints the data, and sends the packets to the server. The server then
 * sends a new packet which is to be printed just like the other packet then
 * sent over to the client.
 */

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.*;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ErrorSim extends Thread implements ActionListener
{
	boolean mode;
	
	DatagramSocket simErrorSocket;
	protected Semaphore sema = new Semaphore(0);
	
	protected JPanel pane;
	protected JTextField textField = new JTextField(35);
    protected JTextArea textArea = new JTextArea(10, 35);
    
    String input = "";
    
	public ErrorSim(boolean mode)
	{
		this.mode = mode;
	}
	
	//below is the method that runs when the thread starts, it just
	//catches errors in the method it calls
	public void run()
	{
		try {
			simErrorSocket = new DatagramSocket(50);
			createAndShowGUI();
			ui();
		} catch (Exception e) {
			System.out.println("Error Sim has failed");
		}
	}
	
	//this is the normal mode method of ErrorSim, it receives the IP of the other computer then it waits for client to send it a packet
	//once a packet is received it passes it over to server and awaits a response
	public void ErrorSimPurpose() throws Exception
	{
		textArea.append("please enter server IP\n");
		sema.acquire();
		String IP = input;
		InetAddress serverHost = InetAddress.getByName(IP);

		DatagramSocket socketR = new DatagramSocket(23,InetAddress.getLocalHost());
		DatagramSocket socketS = new DatagramSocket();
		InetAddress localHostAddress = InetAddress.getLocalHost();
		DatagramPacket packetR = new DatagramPacket(new byte[512],512);

		while(true)
		{
			socketR.receive(packetR);//receives packet from client
			if(this.mode) {textArea.append(packetPrint.Print("Received from Client",packetR));}

			DatagramPacket packetS = new DatagramPacket(packetR.getData(),packetR.getLength(),serverHost,69);
			if(this.mode) {textArea.append(packetPrint.Print("Sending to Server",packetS));}
			socketS.send(packetS);//passes packet along

			String message = new String(packetR.getData());

			DatagramPacket ServerPacketR = new DatagramPacket(new byte[512],512);
			socketR.receive(ServerPacketR);//receives response packet from server
			if(this.mode) {textArea.append(packetPrint.Print("Received from Server", ServerPacketR));}

			DatagramPacket ServerPacketS = new DatagramPacket(ServerPacketR.getData(),ServerPacketR.getLength(),localHostAddress,packetR.getPort());
			if(this.mode) {textArea.append(packetPrint.Print("Sending to Client",ServerPacketS));}
			socketS.send(ServerPacketS);//sends response to client
		}
	}
	
	//this method is run if error mode was selected
	public void errorInterface() {
		byte[]data = "Error Simulator".getBytes();
		DatagramPacket simPacket = new DatagramPacket(data, data.length);
		
		
		textArea.append("What do you want to simulate?\n");
		textArea.append("1: lose a packet\n");
		textArea.append("2: delay a packet\n");
		textArea.append("3: duplicate a packet\n");
		textArea.append("4: Illegal TIP Orperation\n");
		textArea.append("5: Unknown TID\n");
		textArea.append("0: quit\n");
		
		try {sema.acquire();}
		catch (InterruptedException e1) {}
		String s = input;
		int type = (int)(Math.random() * 3 + 1);
		switch (input) {
		case "1":
			lostSimError(type);
			break;
		case "2":
			delaySimError(type);
			break;
		case "3":
			duplicateSimError();
			break;
		case "4":
			packetError(simPacket, type);
			break;
		case "5":
			tidError(simPacket);
			break;
		case "0":
			textArea.append("Error Simulator Shutting down. GoodBye!\n");
			Thread.currentThread().interrupt();
			break;
		}
	}

	//simulates a packet being lost between client and server
	public void lostSimError(int type) {

		textArea.append("A packet has been lost\n");

		switch (type) {
		case 1:
			textArea.append("Request type Packet seems to have been lost...\n");
			break;
		case 2:
			textArea.append("An ACK packet seems to have been lost...\n");
			break;
		case 3:
			textArea.append("A DATA packet seems to have been lost...\n");
			break;
		}
		
		ui();
	}

	//Takes is a random number to select what type of packet would be delayed,
	//and simulates the thread waiting for 10secs before receiving the packet and sending it on.
	public void delaySimError(int type) {
		textArea.append("Duplicate Packets have been dected!\n");
		String msg = "";
		switch(type) {
		case 1:	
			msg = "ACK packet is delayed";
			break;
		case 2 :
			msg = "DATA packet is delayed";
			break;
		case 3:
			msg = "Request packet is delayed";
			break;
		}
		
		textArea.append("ErrorSim: "+ msg +". Waiting for packet...\n");
		try {
			TimeUnit.MILLISECONDS.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		textArea.append("ErrorSim: Packet recieved and sent!\n");
		ui();
	}
	
	//simulates duplicate packet
	public void duplicateSimError() {
		
		textArea.append("Duplicating packets....\n");
		try {
			textArea.append("First Packet Sent...\n");
			TimeUnit.MILLISECONDS.sleep(5000);
			textArea.append("Duplicate Packet Sent...\n");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		textArea.append("\n");
		ui();
	}

	//simulates changing the TID of the packet
	public void tidError(DatagramPacket p) {
		textArea.append("Sending packet from different Port: " + simErrorSocket.getLocalPort() + "\n");
		try {
			simErrorSocket.send(p);
		} catch (IOException e) {
			//e.printStackTrace();
			textArea.append("Unable to send, packet, to unknown TID.");
		}
		textArea.append("\n");
		ui();
	}
	
	//simulates corrupting data in packet
	public void packetError(DatagramPacket p, int type) {
		textArea.append("Packet has been Corrupted \n");
		
		switch(type) {
		case 1:
			textArea.append("Opcode has been changed\n");
			break;
		case 2:
			textArea.append("Block number has been changed\n");
			break;
		case 3:
			textArea.append("The last bytes have been changed\n");
			break;
		}
		
		textArea.append("\n");
		ui();
	}
	
	//this method is called during set up, it asks the user what mode it should run in
	public void ui() {
		
		textArea.append("What function do you want to operate in?\n");
		textArea.append("(N)ormal mode or (E)rror Sim mode or (Q)uit.\n");
		
		try {sema.acquire();}
		catch (InterruptedException e1) {}
		String s = input;
		if (s.equalsIgnoreCase("N")) {
			textArea.append("Waiting to receive packets for forwarding.. \n");
			try {
				ErrorSimPurpose();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (s.equalsIgnoreCase("E")) {
			errorInterface();
		}else if(s.equalsIgnoreCase("Q")) {
			textArea.append("\n Error Simulator Shutting down. GoodBye!\n");
			Thread.currentThread().interrupt();
		}
	}
	
	@Override
	//this method receives the text the user inputs and releases the semaphore, the released semaphore tells the thread that there is new user input 
	public void actionPerformed(ActionEvent arg0) {
		input = textField.getText();
        textArea.append(input + "\n");
        textField.setText("");
        
        sema.release();
        
        textArea.setCaretPosition(textArea.getDocument().getLength());
	}
	
	//creates the GUI for the thread
	private void createAndShowGUI() {
        JFrame frame = new JFrame("ErrorSim");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        
        textField.addActionListener(this);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        
        pane = new JPanel();
        pane.setLayout(new BorderLayout());
        pane.add(textField, BorderLayout.NORTH);
        pane.add(scrollPane, BorderLayout.CENTER);
        frame.add(pane);
 
        frame.pack();
        frame.setVisible(true);
        frame.toFront();
    }
}