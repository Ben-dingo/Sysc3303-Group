import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.DatagramPacket;
/***
 * This class is responsible for handeling the TFTP errors, with codes 1, 2, 3 and 6
 * 
 * @author Noor Ncho
 *
 */
public class ErrorHandeler {

	private int errorCode = 0;

	public ErrorHandeler() {
		
	}

	/*public void errorExtract(byte[] data) {
		String error = new String(data);
		if (error.equals("1")) {
			System.out.println("Error Code 01: File not found");
			System.out.println("Please try again.");
		} else if (error.equals("2")) {
			System.out.println("Error Code 02: Access Violation");
			System.out.println("Please try again.");
		} else if (error.equals("3")) {
			System.out.println("Error Code 03: Disk full or allocation exceeded");
			System.out.println("Please try again.");
		} else if (error.equals("6")) {
			System.out.println("Error Code 06: File already Exists");
			System.out.println("Please try again.");
		}
	}*/
	
	public String getError(int error) {
		String msg = "";
		if (error == 1) {
			msg = "Error Code 01: File not found";
			//System.out.println("Please try again.");
		} else if (error == 2) {
			msg = "Error Code 02: Access Violation";
			//System.out.println("Please try again.");
		} else if (error == 3) {
			msg = "Error Code 03: Disk full or allocation exceeded";
			//System.out.println("Please try again.");
		} else if (error == 6) {
			msg = "Error Code 06: File already Exists";
			//System.out.println("Please try again.");
		}		
		return msg;
	}

	/***
	 * Checks for any possible errors that may be in relation to the files
	 * 
	 * @param filename
	 * @param request
	 */
	public boolean errorCheck(String filename, String request) {
		File file = new File(filename);

		if (request.equals("read")) {
			// Error Code 1
			if (!file.exists()) {
				errorCode = 1;
				return false;
			}
			// Error Code 2
			else if (!file.canRead()) {
				errorCode = 2;
				return false;
			}
		} else if (request.equals("write")) {
			
			// Error Code 2
			if (!file.canWrite()) {
				errorCode = 2;
				return false;
			}
			// Error Code 3 - no space on disk
			else if(file.getUsableSpace() < file.length()) {
				errorCode = 3;
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Creates an error packet that would be sent back contain the realive error code.
	 * @return
	 */
	public DatagramPacket makeErrorPacket() {
		byte[]error = new byte[1];
		ByteArrayOutputStream errorc = new ByteArrayOutputStream();
		errorc.write(errorCode);
		error = errorc.toByteArray();
		DatagramPacket errorPacket = new DatagramPacket(error, error.length);
		return errorPacket;
	}
	
	public int getErrorCode() {
		return errorCode;
	}
}
