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
			String bitches = reader.next();
			
			if(bitches.toLowerCase().equals("quiet"))
			{
				break;
			}
			else if(bitches.toLowerCase().equals("verbose"))
			{
				mode = true;
				break;
			}
			else if(bitches.toLowerCase().equals("quit"))
			{
				System.out.println("Shutting down server.");
				shutoff = true;
				mode = true;
				break;
			}
			else {System.out.println("input must be 'Quiet' or 'Verbose' case sensitive");}
			
			
		}
		while(true) {
			if(shutoff == false) {
				System.out.println("Enter message.");
				message = reader.next();
				if(message != "") {
					break;
				}
					else if(message.toLowerCase().equals("quit"))
				{
						System.out.println("Shutting down server.");
				}
				else {
					System.out.println("A message must be entered to procede.");
				}
			}
			else{
				message = "shutoff";
				break;
			}
			
		}
		
		MasterServer serverThread = new MasterServer(mode);
		Client clientThread = new Client(mode, message);
		ErrorSim ErrorSimThread = new ErrorSim(mode);
		
		serverThread.start();
		ErrorSimThread.start();
		clientThread.start();
	}
}