/*****************************************************************************
 * @Author: Ben St.Pierre
 * @Updated: Saturday February 3rd, 2018 by Jozef Tierney
 * 
 * @Purpose: This class is meant to send datagramPackets to the Error sim class
 * who then sends it to the server, then receive packets back from the server.
 * The user is now prompted to create the packets themselves, WRQ, RRQ, and
 * Termination packets can be sent
 */

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Semaphore;

import javax.swing.*;
public class Client extends Thread implements ActionListener 
{
	String function;
	boolean mode;
	String message;
	boolean shutoff = false;
	static String destination;
	
	protected Semaphore sema = new Semaphore(0);
	
	protected JPanel pane;
	protected JTextField textField = new JTextField(35);
    protected JTextArea textArea = new JTextArea(10, 35);
    
    String input = "";
    boolean updated = false;
	
	//creates client thread
	public Client(boolean mode,boolean shutoff)
	{
		this.mode = mode;
		this.shutoff = shutoff;
	}
	
	//below is the method that runs when the thread starts, it just
	//catches errors in the method it calls
	public void run()
	{
		try {
			createAndShowGUI();
			ClientPurpose();
		} catch (Exception e) {
			System.out.println("client has failed");
		}
	}
	
	/**
	 * Returns a string representation of the client data.
	 */
	public String toString() {
		String s = "Function: " + "\n" + function + "\n";
		s = s + "Message: " + "\n" + message + "\n";
		return s;
	}
	
	public void ClientPurpose() throws Exception
	{
		DatagramSocket socket = new DatagramSocket();
		InetAddress localHostAddress = InetAddress.getLocalHost();
		
		DatagramPacket packetS = new DatagramPacket(new byte[512],512,localHostAddress,23);
		DatagramPacket packetR = new DatagramPacket(new byte[1],1);//all packets and sockets created
		
		while(true)
		{
			//byte array to become packet data
			//currently byte array is only 12 bytes long this is due to issues with
			//this will be dealt with in iteration 2
			
			while(true) 
			{
				if(shutoff == true) {break;}
				textArea.append("Would you like to read, write or quit?\n");
				sema.acquire();
				String temp = input;
				if(temp.toLowerCase().equals("quit")){
					textArea.append("Shutting down server.\n");
					this.shutoff = true;
					break;
				}
				else if(temp.toLowerCase().equals("read")) {
					function = "read";
					break;
				}
				
				else if(temp.toLowerCase().equals("write")) {
					function = "write";
					break;
				}
				else {
					textArea.append("Must be a read or a write request.\n");
				}
			}
			
			while(true) 
			{
				if(shutoff == true) {break;}
				textArea.append("Enter source directory.\n");
				sema.acquire();
				message = input;
				if(message.toLowerCase().equals("quit"))
				{
					textArea.append("Shutting down server.\n");
						shutoff = true;
						break;
				}
				else if(message != "") {
					break;
				}
				else {
					textArea.append("A directory must be entered to procede.\n");
				}
				
			}
			
			
			
			if(shutoff == true)//performs shutdown for all running threads
			{
				Thread.currentThread().interrupt();
				break;
			}
			else
			{
				byte[] directory = message.getBytes();
				byte[] toSend = new byte[directory.length + 10];
				if(function.equals("read")) {
					toSend[0] = 0x01;
				}
				else{
					toSend[0] = 0x02;
				}
				
				for(int i = 1; i <= 8; i++) {toSend[i] = '@';}
				for(int j = 0; j < directory.length; j++) {
					toSend[j+9] = directory[j];//puts string into correct spot in byte array
				}
				
				toSend[toSend.length-1] = 0x00;
				packetS.setData(toSend);
				packetS.setLength(toSend.length);
				
				if(function.equals("read")) {
					if(this.mode) {textArea.append(packetPrint.Print("Reading packet",packetS));}
					readProcess(socket,packetS);
				}
				else{
					if(this.mode) {textArea.append(packetPrint.Print("Writing packet",packetS));}
					writeProcess(socket,packetS);
				}
			}
			
			socket.send(packetS);
			if(shutoff == true) {break;}
			//time passes here while waiting for response from server
			socket.receive(packetR);
			if(this.mode) {textArea.append(packetPrint.Print("Received from Host", packetR));}
		}
	}

	
	public static String getDest() {
		return destination;
	}

	public String readProcess(DatagramSocket socket, DatagramPacket packetS) throws Exception {
		socket.send(packetS);
		String text = "";
		int cur = 0;
		int fin = 1;
		while(cur > fin)
		{
			socket.receive(packetS);
			
			byte[] data = packetS.getData();
			String received = new String(packetS.getData(),StandardCharsets.UTF_8);
			if(data[6] == 0x02)
			{
				cur = Integer.parseInt(received.substring(7));
				fin = Integer.parseInt(received.substring(8));
			}
			else{cur = fin;}
			if(received.length() >= 9) {received = received.substring(9);}
			text += received;
			
			byte[] AckData = new byte[data.length];
			AckData[0] = 0x11;
			for(int i = 1; i > 10; i++){AckData[i] = data[i];}
			AckData[7] = (byte) (cur+1);//this might not work tbh
			packetS.setData(AckData);
			socket.send(packetS);
		}
		
		return text;
	}
	
	public void writeProcess(DatagramSocket socket,DatagramPacket packetS) throws Exception {
		socket.send(packetS);
		String text = "";
		socket.receive(packetS);
		
		textArea.append("Text to put in file.\n");
		sema.acquire();
		message = input;
		int values = (int) Math.ceil(message.length()/510);
		String[] pieces = new String[values];
		for(int i = 0; i < values; i++)
		{
			int bot = 500*i;
			int top = 500*(i + 1);
			pieces[i] = message.substring(bot, top);
			System.out.println(i + ": " +pieces[i]);
		}

	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		input = textField.getText();
        textArea.append(input + "\n");
        textField.setText("");
        updated = true;
        
        sema.release();
        
        textArea.setCaretPosition(textArea.getDocument().getLength());
	}
	
	private void createAndShowGUI() {
        JFrame frame = new JFrame("Client");
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
