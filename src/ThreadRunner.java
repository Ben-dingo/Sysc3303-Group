/*****************************************************************************
 * @Author: Ben St.Pierre
 * @Updated: Saturday April 7th, 2018
 * 
 * @Purpose: This class prompts the user for what mode they would like to use.
 * It then sets up the threads the user wants to use
 */
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

@SuppressWarnings("unused")
public class ThreadRunner
{
	//this is the main method to run for the system, it initializes all the threads used
	public static void main(String[] args)
	{
		boolean mode = false;
		boolean shutoff = false;
		String message;
		@SuppressWarnings("resource")
		Scanner reader = new Scanner(System.in);
		System.out.println("Server started.\nEnter 'Quit' to shut down server.");
		
		while(true)//prompts user for input
		{
			packetFile packet2 = new packetFile();				//create folder to print the results of read transfers
	        packet2.exportText("read results", "");
			
			System.out.println("'Quiet' or 'Verbose'?");
			String selection = reader.next();
			
			if(selection.toLowerCase().equals("quiet"))//quiet prints as little info as possible
			{
				break;
			}
			else if(selection.toLowerCase().equals("verbose"))//verbose prints frequently
			{
				mode = true;
				break;
			}
			else if(selection.toLowerCase().equals("quit"))
			{
				System.out.println("Shutting down server.");
				shutoff = true;
				break;
			}
			else {System.out.println("input must be 'Quiet' or 'Verbose'");}
		}
		
		System.out.println("Client, Server or both?");
		while(true) {
			String selection = reader.next();
			if(selection.toLowerCase().equals("client"))
			{
				Client clientThread = new Client(mode,shutoff);
				clientThread.start();
				ErrorSim ErrorSimThread = new ErrorSim(mode);
				ErrorSimThread.start();//creates and runs errorsim and client
				break;
			}
			else if(selection.toLowerCase().equals("server"))
			{
				MasterServer serverThread = new MasterServer(mode);
				serverThread.start();//only creates and runs master server
				break;
			}	
			else if(selection.toLowerCase().equals("both"))
			{
				MasterServer serverThread = new MasterServer(mode);
				serverThread.start();
				Client clientThread = new Client(mode,shutoff);
				clientThread.start();
				ErrorSim ErrorSimThread = new ErrorSim(mode);//creates 3 threads
				ErrorSimThread.start();//runs threads
				break;
			}
			else if(selection.toLowerCase().equals("quit"))
			{
				break;
			}
			else {
				System.out.println("invalid input");
			}
		}
	}
}