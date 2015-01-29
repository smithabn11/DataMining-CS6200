import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/*Used to split .csv files*/
public class SplitCsvFile {

	public void splitCsvFile(String ipfilename, String opfilename, int linesToWrite) throws FileNotFoundException {

		BufferedReader bufrdr = null;
		BufferedWriter bufwtr = null;
		
		File ipfile;
		File opfile;

		try {

			ipfile = new File(ipfilename);
			opfile = new File(opfilename);
			bufrdr = new BufferedReader(new FileReader(ipfilename));
			bufwtr = new BufferedWriter(new FileWriter(opfilename));

			// if output file does not exists, then create it
			if (ipfile.exists() && !opfile.exists()) {
				opfile.createNewFile();
			}
		
			String line = "";
			int curLineNum = 0;
			
			/*write upto linesToWrite - this includes first row of 
			 * .csv then followed by data */
			while (((line = bufrdr.readLine()) != null) && 
					curLineNum <= linesToWrite) {
				bufwtr.write(line);	
				bufwtr.write("\r\n");
				curLineNum++;
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bufrdr != null) {
					bufrdr.close();
				}
				if (bufwtr != null) {
					bufwtr.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}
}