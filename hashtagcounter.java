import java.io.IOException;

/**
 * Main Class for hash tag counter
 * @author RITVIK
 *
 */
public class hashtagcounter {
	/**
	 * The main function initiates the program 
	 * and calls the file handler to read from the file
	 * @param args
	 */
	public static void main(String args[]){
		FileHandler fileHandler = new FileHandler();

		try {
			fileHandler.readWriteFile(args[0],"Output.txt"); 
		} catch(IOException io){
			System.out.println("IO Exception");
			io.printStackTrace();
		}catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}
