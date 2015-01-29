import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;

import Jama.*;

public class WriteToFile {
	
	private DecimalFormat decFormat;
	
	public WriteToFile(){
		decFormat = new DecimalFormat("#.######");
	}

	public void writeMatrixToFile(String opfilename, Matrix mtData) {
		PrintWriter printWriter = null;
		File file;

		try {

			file = new File(opfilename);
			// if file does not exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			printWriter = new PrintWriter(file);
			mtData.print(printWriter, decFormat, 1);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			if (printWriter != null) {
				printWriter.close();
			}

		}

	}
}