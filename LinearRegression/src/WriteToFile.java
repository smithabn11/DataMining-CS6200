import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;

import Jama.*;

public class WriteToFile {

	private DecimalFormat decFormat;

	public WriteToFile() {
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

	public void writeMSEToFile(String trainfn, String testfn, double dTrainData[], double dTestData[]) {
		PrintStream printstr = null;
		File file;

		try {

			String temp1 = trainfn;
			if (trainfn.startsWith("./")) {
				temp1 = trainfn.substring(2, trainfn.lastIndexOf('.'));
			}

			String temp2 =testfn;
			if (testfn.startsWith("./")) {
				temp2 = testfn.substring(2, testfn.lastIndexOf('.'));
			}
			String opFileName = "./MSE".concat(temp1).concat("_").concat(temp2).concat(".R");

			
			file = new File(opFileName);
			// if file does not exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			printstr = new PrintStream(file);
			/*printstr.println("Training File:" + trainfn);
			printstr.println("Testing File:" + testfn);*/
			printstr.println("jpeg(\"C:/graph.jpg\")");

			DecimalFormat decFormat = new DecimalFormat("#.######");
			/*printstr.println("Training MSE for lamdba 1-150");*/
			
			printstr.println("lambda<-1:150");
			printstr.print("traindata<-c(");
			
			for (int index = 0; index < 149; index++) {
				printstr.print(decFormat.format(dTrainData[index]) + ",");
			}
			printstr.print(decFormat.format(dTrainData[149]) + ")");
			printstr.println("");
			/*printstr.println("");
			printstr.println("Testing MSE for lamdba 1-150");*/
			printstr.print("testdata<-c(");
			
			for (int index = 0; index < 149; index++) {
				printstr.print(decFormat.format(dTestData[index]) + ",");
			}
			printstr.print(decFormat.format(dTestData[149]) + ")");
			printstr.println("");
			printstr.println("plot(lambda,traindata,type=\"l\",col=\"red\")");
			printstr.println("par(new=TRUE)");
			printstr.println("plot(lambda,testdata,type=\"l\",col=\"green\")");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			if (printstr != null) {
				printstr.close();
			}

		}
	}
}