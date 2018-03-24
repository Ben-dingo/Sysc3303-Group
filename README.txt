Group 6 project iteration 4


Instructions for running the program:

1.	Launch project in eclipse.
2.	Execute the Main function in threadRunner.
3.	Run the project and follow its prompts.
4.	Quit can be entered at any time to shut down servers and close the program.
4.	Select a mode 'Verbose' or 'Quiet:
	Verbose: Outputs the details of the packet during execution of the program
	Quiet: Details remain hidden in the program.
	
5.	If quiet is selected datagram details will not be printed, if verbose is selected it will.

6.	Next you're asked how many clients you would like to initialize. A minimum of one is needed to function.
7.	At this point n windows will open (1 server, 1 errorSim and an inputted number of clients.)
	The client(s) will continue to prompt inputs from the user for specifying the packet type (read/write/quit).
	The second is the error sim that prints out details of packets passing through it with a specified mode (Normal/ErrorSim/Quit). 	Finally, The server functions the same function as errorSim.
8.	Next you're prompted to declare whether you desire a read or write packet.
9.	When prompted, input the directory including the file name (a .txt file) into the text window. The file must be in the the 		project directory. A sample file called test.txt has been created with the message hellow world in it.
	ex. [project folder path]	\test.txt
10.	After a transfer is done the process begins again.
