import java.util.Scanner;

public class ThreadRunner
{
	public static void main(String[] args)
	{
		boolean mode = false;
		Scanner reader = new Scanner(System.in);
		while(true)
		{
			System.out.println("'Quiet' or 'Verbose'?");
			String bitches = reader.next();
			if(bitches.equals("Quiet"))
			{
				break;
			}
			else if(bitches.equals("Verbose"))
			{
				mode = true;
				break;
			}
			else {System.out.println("input must be 'Quiet' or 'Verbose' case sensitive");}
		}
		System.out.println(mode);
		Server serverThread = new Server();
		Client clientThread = new Client();
		InterHost ErrorSimThread = new InterHost();
		
		serverThread.start();
		ErrorSimThread.start();
		clientThread.start();
	}
}