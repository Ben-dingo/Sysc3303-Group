/*****************************************************************************
 * @Author: Ben St.Pierre
 * @Updated: Friday March 9th, 2018 by Jozef Tierney & Ben St.Pierre
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
import java.util.concurrent.Semaphore;

import javax.swing.*;
public class Client extends Thread implements ActionListener 
{
	String function;
	boolean mode;
	String message;
	boolean shutoff = false;
	
	protected Semaphore sema = new Semaphore(0);//used to synchronize packet sending with user input
	
	protected JPanel pane;
	protected JTextField textField = new JTextField(35);
    protected JTextArea textArea = new JTextArea(10, 35);//basic gui components
    
    String input = "";//holds user input in jtextfield
	
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
				sema.acquire();//waits for user input
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
				textArea.append("Enter directory.\n");
				sema.acquire();//waits for user input
				message = input;
				if(message.toLowerCase().equals("quit"))
				{
					textArea.append("Shutting down server.\n");
						shutoff = true;
						break;
				}
				else if(message != "") {
					message = packetFile.importText(message);
					break;
				}
				else {
					textArea.append("A message must be entered to procede.\n");
				}
				
			}
			if(shutoff == true)//performs shutdown for all running threads
			{
				message = "00ShutDown00";
				byte[] toSend = message.getBytes();
				packetS.setData(toSend);
				packetS.setLength(12);
				socket.send(packetS);
				Thread.currentThread().interrupt();
				break;
			}
			else
			{
				byte[] file = message.getBytes();
				byte[] toSend = new byte[file.length + 3];
				for(int j = 0; j < file.length; j++) {
					if(function.equals("read")) {
						toSend[1] = 0x01;
					}
					else{
						toSend[1] = 0x02;
					}
					toSend[j+2] = file[j];//puts string into correct spot in byte array
					toSend[0] = 0x00;
					toSend[toSend.length-1] = 0x00;
					packetS.setData(toSend);
					packetS.setLength(toSend.length);
				}
				if(function.equals("read")) {
					if(this.mode) {textArea.append(packetPrint.Print("Reading packet",packetS));}
				}
				else{
					if(this.mode) {textArea.append(packetPrint.Print("Writing packet",packetS));}
				}
			}
			socket.send(packetS);
			if(shutoff == true) {break;}
			//time passes here while waiting for response from server
			socket.receive(packetR);
			if(this.mode) {textArea.append(packetPrint.Print("Received from Host", packetR));}
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {//runs when 'VK_ENTER' is pressed
		input = textField.getText();
        textArea.append(input + "\n");
        textField.setText("");
        
        sema.release();
        
        textArea.setCaretPosition(textArea.getDocument().getLength());
	}
	
	private void createAndShowGUI() {//sets up a GUI terminal so every thread can have their own input windows
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
