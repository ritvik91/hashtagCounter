

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;
/**
 * The class handles all IO operations for file handling
 * @author RITVIK
 *
 */
public class FileHandler {

	/**
	 * method to read from and write to file
	 * @param filePath
	 * @return
	 * @throws Exception 
	 */
	public void readWriteFile(String filePath, String outputFilePath) throws Exception{

		FileInputStream inputStream = null;
		Scanner sc = null;
		FibonacciHeapManager manager = new FibonacciHeapManager();
		String value;
		int count;
		ArrayList<String> output;

		try {
			inputStream = new FileInputStream(filePath);
			sc = new Scanner(inputStream, "UTF-8");
			File file = new File(outputFilePath);
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);

			if (!file.exists()) {
				file.createNewFile();
			}
			while (sc.hasNextLine()) {
				String readLine = sc.nextLine();
				String[] input = readLine.split(" ");
				if(input.length==1 && input[0].equalsIgnoreCase("stop"))
					break;
				else if(input.length==1){
					count = Integer.parseInt(input[0]);
					output = manager.removeMax(count);
					String delim="";
					for(String o : output){
						bw.write(delim + o.substring(1));
						delim=", ";
					}
					bw.write("\r\n");
				}else if(input.length == 2){
					value = input[0];
					count = Integer.parseInt(input[1]);
					manager.insert(value, count);
				}else{
					throw new Exception("error in input");
				}
			}
			if (sc.ioException() != null) {
				throw sc.ioException();
			}
			
			bw.close();
			
		} catch(Exception e){
			throw new Exception(e.getMessage());
		}finally {
			if (inputStream != null) {
				inputStream.close();
			}
			if (sc != null) {
				sc.close();
			}
		}

	}

}
