/*****************************************************************************
 * @Author: Ben St.Pierre
 * @Updated: Saturday April 7th, 2018
 * 
 * @Purpose: This class is meant to send datagramPackets to the Error sim class
 * who then sends it to the server, then receive packets back from the server.
 * The user is now prompted to create the packets themselves, WRQ, RRQ, and
 * Termination packets can be sent
 */

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
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
	
	ErrorHandeler eh;
	boolean errorFree = true;
	
	protected Semaphore sema = new Semaphore(0);
	
	protected JPanel pane;
	protected JTextField textField = new JTextField(35);
    protected JTextArea textArea = new JTextArea(10, 35);
    
    String input = "";
    boolean updated = false;
	
	//creates client thread
	public Client(boolean mode,boolean shutoff)
	{
		eh = new ErrorHandeler();
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
	
	//Returns a string representation of the client data.
	public String toString() {
		String s = "Function: " + "\n" + function + "\n";
		s = s + "Message: " + "\n" + message + "\n";
		return s;
	}
	
	//client purpose receives user input and sets up packet format, then it splits into either readProcess or writeProcess
	public void ClientPurpose() throws Exception
	{
		DatagramSocket socket = new DatagramSocket();
		InetAddress localHostAddress = InetAddress.getLocalHost();
		textArea.append("Your IP is: " + localHostAddress.getHostAddress() + "\n");
		
		
		DatagramPacket packetS = new DatagramPacket(new byte[512],512,localHostAddress,23);
		DatagramPacket packetR = new DatagramPacket(new byte[512],512);//all packets and sockets created
		
		while(true)//this loop should continue until client is closed
		{
			while(true)//this loop continues until proper input is given
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
				if(function.equals("read")) {
					textArea.append("Enter source directory.\n");
				}
				if(function.equals("write")) {
					textArea.append("Enter destination directory.\n");
				}
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
				//checkfile(message, function);
				
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
					readProcess(socket,packetR,packetS);
				}
				else{
					if(this.mode) {textArea.append(packetPrint.Print("Writing packet",packetS));}
					writeProcess(socket,packetR,packetS);
				}
			}
			if(shutoff == true) {break;}
			//time passes here while waiting for response from server
		}
	}

	
	public static String getDest() {
		return destination;
	}

	//requests server to read from a file server side, then writes the received data to a file client side
	public String readProcess(DatagramSocket socket, DatagramPacket packetR, DatagramPacket packetS) throws Exception {
		textArea.append("packet being sent to error sim\n");
		socket.send(packetS);
		String text = "";
		int cur = 0;
		int fin = 1;
		
		while(cur < fin)
		{
			socket.receive(packetR);
			byte[] data = packetR.getData();
			byte[] AckData = new byte[512];
			for(int i = 0; i > 10; i++){AckData[i] = data[i]; System.out.println(data[i]);}
			AckData[0] = 0x11;
			
			textArea.append(packetPrint.Print("Received from ErrorSim",packetR));
			String received = new String(data,StandardCharsets.UTF_8);
			if(data[6] == ((byte) 2))
			{
				cur = (int) data[7];
				fin = (int) data[8];
			}
			else{cur = fin;}
			text += received;
			int l = packetPrint.filenameLength(packetR);
			if(received.length() >= 9) {received = received.substring(9,l);}
			
			packetS.setData(packetR.getData());
			socket.send(packetS);
			textArea.append(packetPrint.Print("Sending to ErrorSim",packetS));
		}
		textArea.append(text + "\n");
		return text;
	}
	
	//requests server create a new file, then sends text to put into the file
	public void writeProcess(DatagramSocket socket, DatagramPacket packetR,DatagramPacket packetS) throws Exception {
		socket.send(packetS);
		socket.receive(packetR);
		
		
		byte[] data = new byte[512];
		byte[] sent = packetR.getData();
		
		
		textArea.append(packetPrint.Print("Received from ErrorSim",packetR));
		
		textArea.append("Text to put in file.\n");
		sema.acquire();
		message = input;
		int fin = (int) Math.ceil(message.length()/500)+ 1;
		String pieces = "";
		for(int cur = 1; cur <= fin; cur++)
		{
			if(fin > cur)
			{
				int bot = 500*(cur-1);
				int top = 500*(cur);
				pieces = message.substring(bot, top);
			}
			else
				pieces = message.substring(500*(cur-1));
			
			System.out.println(cur + ": " +pieces);

			data[0] = 0x10;
			for(int j = 1; j < 6; j++) {data[j] = sent[j];}
			if(fin == 1){data[6] = (byte) 1;}
			else{data[6] = (byte) 2;}
			
			data[7] = (byte) cur;
			data[8] = (byte) fin;
			
			byte[] piecebyte = pieces.getBytes();
			for(int j = 0; j < piecebyte.length; j++) {data[j+9] = piecebyte[j];}
			
			packetS.setData(data);
			socket.send(packetS);
			textArea.append(packetPrint.Print("Sending to ErrorSim",packetS));
			socket.receive(packetR);
			textArea.append(packetPrint.Print("Received from ErrorSim",packetR));
		}

	}
	
	//Check for any possible error that may occur due to the type of file
	public void checkfile(String msg, String function) throws Exception {
		File f = new File(msg);
		String filename = f.getName();
		errorFree = eh.errorCheck(filename, function);
		
		if(!errorFree) {
			int error = eh.getErrorCode();
			textArea.append(eh.getError(error) + "\n");
			textArea.append("Please try again...\n");
			ClientPurpose();
		}
	}
	
	@Override
	//this method receives the text the user inputs and releases the semaphore, the released semaphore tells the thread that there is new user input 
	public void actionPerformed(ActionEvent arg0) {
		input = textField.getText();
        textArea.append(input + "\n");
        textField.setText("");
        updated = true;
        
        sema.release();
        
        textArea.setCaretPosition(textArea.getDocument().getLength());
	}
	
	//creates the GUI for the thread
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
