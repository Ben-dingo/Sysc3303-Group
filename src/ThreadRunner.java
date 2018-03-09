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
		while(true)
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
			else {System.out.println("input must be 'Quiet' or 'Verbose' case sensitive");}
		}
		
		
		
		MasterServer serverThread = new MasterServer(mode);
		ErrorSim ErrorSimThread = new ErrorSim(mode);
		Client clientThread = new Client(mode,shutoff);
		
		serverThread.start();
		ErrorSimThread.start();
		clientThread.start();
	}
}