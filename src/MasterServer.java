/*****************************************************************************
 * @Author: Ben St.Pierre
 * @Updated: Saturday February 3rd, 2018
 * 
 * @Purpose: This class receives all the packets sent by the error sim. It creates
 * a new server thread to handle each incoming packet, once it passes on the packet
 * to the new thread it returns to waiting for the next packet
 */
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class MasterServer extends Thread implements ActionListener
{	
	boolean mode;
	ArrayList<DatagramSocket> servers = new ArrayList<DatagramSocket>();
	
	protected Semaphore sema = new Semaphore(0);
	
	protected JPanel pane;
	protected JTextField textField = new JTextField(35);
    protected JTextArea textArea = new JTextArea(10, 35);
    
    String input = "";
    boolean updated = false;
	
	//creates master server thread
	public MasterServer(boolean mode)
	{
		this.mode = mode;
	}

	//below is the method that runs when the thread starts, it just
	//catches errors in the method it calls
	public void run() {
		try {
			createAndShowGUI();
			MasterPurpose();
		} catch (Exception e) {
			System.out.println("MasterServer has failed");
		}
	}
	
	public void MasterPurpose() throws Exception
	{
		DatagramSocket socketR = new DatagramSocket(69,InetAddress.getLocalHost());
		DatagramPacket packetR = new DatagramPacket(new byte[512],512);
		DatagramPacket packetS = packetR;
		
		while(true)
		{
			socketR.receive(packetR);
			if(this.mode) {textArea.append(packetPrint.Print("Received from Host",packetR));}
			byte[] retreived = packetR.getData();
			if(retreived[1] == '@')
			{
				DatagramSocket newSocket = new DatagramSocket();
				servers.add(newSocket);
				
				int portinfo = newSocket.getLocalPort();
				for(int i = 1; i < 6; i ++){
				    retreived[6-i] = (byte) (Integer.toString(portinfo % 10)).charAt(0);
				    portinfo = (int) Math.floor(portinfo / 10);
				}
				packetS.setData(retreived);
				
				packetS.setPort(newSocket.getLocalPort());
				server newThread = new server(this.mode,newSocket,newSocket.getLocalPort());//creates server thread
				if(mode) {textArea.append("a new server has been made with ID " + newSocket.getLocalPort() + "\n");}
				newThread.start();
				if(this.mode) {textArea.append(packetPrint.Print("Sending to sub server",packetS));}
				socketR.send(packetS);//sends packet to new server thread
			}
			else
			{
				String byteData = new String(retreived,StandardCharsets.UTF_8);
				int porthole = Integer.parseInt(byteData.substring(1, 6));
				boolean found = false;
				for(DatagramSocket socket : servers)
				{
					if(socket.getLocalPort() == porthole)
					{
						found = true;
						packetS.setPort(porthole);
						textArea.append("sending to server with ID " + socket.getLocalPort() + "\n");
						socketR.send(packetS);
					}
				}
				if(!found) {System.out.println("couldn't find anything");}
			}
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
        JFrame frame = new JFrame("Master Server");
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
