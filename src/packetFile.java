import java.io.*;
import java.util.Scanner;
/**
 * 
 * @author Omar Dawoud
 * Student #: 101006779
 * Last Updated: 2/17/2018
 * Class: This class contains methods which save and load data in the form of .txt files.
 *
 */
public class packetFile{
	
	public packetFile() {}

	/**
	 * Imports data from an existing textfile.
	 * @param directory --> The location of the text file.
	 * @return the data within the text file.
	 */
	public String importText(String directory) {

		File f = new File(directory); //Obtain the file from the directory.
		
		try {
			BufferedReader out = new BufferedReader(new FileReader(f)); //Create an output reader using the FileReader format.
			Scanner input = new Scanner(f); //Used to check if there are extra lines within the text document.
			String s = ""; //Saves the data into this string.
			boolean check = true; 
			
			while (check) { //If true, continue extracting data from text file.
				if (input.hasNextLine()) { //If there is a next line in the text document, move to it.
					String line = input.nextLine();
					s = s + line + "\n";
							
				} else
					check = false; //Flag this when the end line of the document is reached.
			}
			return s; //Return the data extracted.
			
		} catch (IOException e1) {
			System.out.println("error");
		}
		return ""; //Returns nothing if failed to extract data.

	}

	/**
	 * Exports the data in the form of a text document.
	 */
	public void exportText(String s) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("myFile.txt"));
			out.write(s);
			out.close();
		} catch (IOException e1) {
			System.out.println("error exporting file!");
		}
	}
}