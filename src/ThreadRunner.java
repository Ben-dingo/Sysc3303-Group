
public class ThreadRunner
{
	public static void main(String[] args)
	{
		Server serverThread = new Server();
		Client clientThread = new Client();
		InterHost ErrorSimThread = new InterHost();
		
		serverThread.start();
		ErrorSimThread.start();
		clientThread.start();
	}
}
