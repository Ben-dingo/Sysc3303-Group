/*****************************************************************************
 * @Author: Ben St.Pierre
 * @Updated: Saturday February 3rd, 2018
 * 
 * @Purpose: This class prompts the user for what mode they would like to use.
 * It then sets up the 3 main threads used in this iteration
 */
import java.util.Scanner;

public class ThreadRunner
{
	public static void main(String[] args)
	{
		boolean mode = false;
		boolean shutoff = false;
		String message;
		Scanner reader = new Scanner(System.in);
		System.out.println("Server started.\nEnter 'Quit' to shut down server.");
		//String test = "test message";
		packetFile packet = new packetFile();
		packet.exportText("");
		//packet.modifyText("myFile.txt", "message3");
		//packetFile.modifyText("myFile.txt", "message2");
		while(true)//prompts user for input
		{
			
			System.out.println("'Quiet' or 'Verbose'?");
			String selection = reader.next();
			
			if(selection.toLowerCase().equals("quiet"))
			{
				break;
			}
			else if(selection.toLowerCase().equals("verbose"))
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
		
		System.out.println("How many Clients would you like to start?");
		int selection = reader.nextInt();
		for(int i = 1; i <= selection; i++)
		{
			Client clientThread = new Client(mode,shutoff);
			clientThread.start();
		}
		
		MasterServer serverThread = new MasterServer(mode);
		ErrorSim ErrorSimThread = new ErrorSim(mode);//creates 3 threads
		
		serverThread.start();
		ErrorSimThread.start();//runs threads
	}
}