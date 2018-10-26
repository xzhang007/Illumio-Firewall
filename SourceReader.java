
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
*  This class is to help Firewall to read the file.
*  Since in the java there might be some "try catch" cases that may throw exceptions 
*  as well as all readers should be closed finally,
*  I define a seperate class to deal with all of these situations, 
*  then in other parts of the programs, we don't need to pay attention to those stuff.
*/
class SourceReader {
	private FileReader fr;
	private BufferedReader br;
	private String line;
	
	/**
	 *  Construct a new SourceReader
	 *  @param fileName the String describing the user's source file
	*/
	SourceReader(String fileName) {
		try {
			fr = new FileReader(fileName);
			br = new BufferedReader(fr);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			closeReaders();
		} catch (IOException e) {
			e.printStackTrace();
			closeReaders();
		} 
	}
	
	/**
	 * a function to read each line
	 * @return the current line
	 */
	String readLine() {
		try {
			line = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			closeReaders();
		}
		
		return line;
	}
	
	/**
	 * a function to close file reader and buffered reader no mater when we finished
	 * or when we meet with an exception
	 */
	void closeReaders() {
		try {
			br.close();
			fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
