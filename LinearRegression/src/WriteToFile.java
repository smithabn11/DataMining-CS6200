import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Arrays;

import Jama.*;

public class WriteToFile {

	private DecimalFormat decFormat;

	public WriteToFile() {
		decFormat = new DecimalFormat("#.######");
	}

	/*
	 * EXAMPLE: WriteToFile objwtof = new WriteToFile();
	 * objwtof.writeMatrixToFile("./mtActualOutput.txt", mtActualOutput);
	 */
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

	public void writeMSEToRFile(String trainfn, String testfn, double dTrainData[], double dTestData[]) {
		PrintStream printstr = null;
		File file;

		try {

			String temp1 = trainfn;
			if (trainfn.startsWith("./")) {
				temp1 = trainfn.substring(2, trainfn.lastIndexOf('.'));
			}

			String temp2 = testfn;
			if (testfn.startsWith("./")) {
				temp2 = testfn.substring(2, testfn.lastIndexOf('.'));
			}
			String opFileName = "./MSE_".concat(temp1).concat("_").concat(temp2).concat(".R");

			file = new File(opFileName);
			// if file does not exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			printstr = new PrintStream(file);

			printstr.println("jpeg(\"C:/graph.jpg\")");

			DecimalFormat decFormat = new DecimalFormat("#.######");

			printstr.println("lambda<-0:" + Double.toString(dTrainData.length - 1) + "");
			printstr.print("traindata<-c(");

			int Trainrows = dTrainData.length;
			for (int index = 0; index < (Trainrows - 1); index++) {
				printstr.print(decFormat.format(dTrainData[index]) + ",");
			}
			printstr.print(decFormat.format(dTrainData[(Trainrows - 1)]) + ")");
			printstr.println("");
			printstr.print("testdata<-c(");

			int Testrows = dTestData.length;
			for (int index = 0; index < (Testrows - 1); index++) {
				printstr.print(decFormat.format(dTestData[index]) + ",");
			}
			printstr.print(decFormat.format(dTestData[(Testrows - 1)]) + ")");
			printstr.println("");

			/* Find the range for ylim (as we have to plot 2 curves */
			Arrays.sort(dTrainData);
			double ylimMin = dTrainData[0];
			double ylimMax = dTrainData[(dTrainData.length - 1)];
			Arrays.sort(dTestData);
			if (dTestData[0] < ylimMin) {
				ylimMin = dTestData[0];
			}
			if (dTestData[(dTestData.length - 1)] > ylimMax) {
				ylimMax = dTestData[(dTestData.length - 1)];
			}

			printstr.println("plot(lambda,traindata,type=\"l\",col=\"red\"," + "ylab=\"MSE\"," + "ylim=c("
					+ decFormat.format(ylimMin) + "," + decFormat.format(ylimMax) + "))");
			printstr.println("par(new=TRUE)");
			printstr.println("plot(lambda,testdata,type=\"l\",col=\"blue\","
					+ "ylab=\"MSE\",sub=\"Red=Training Blue=Test\"," + "ylim=c(" + decFormat.format(ylimMin) + ","
					+ decFormat.format(ylimMax) + "))");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			if (printstr != null) {
				printstr.close();
			}

		}
	}

	/* Function to write Learning Curve to R file */
	public void writeLearingCurveRFile(String trainfn, double lambda, double dTrainData[], double dTestData[]) {
		PrintStream printstr = null;
		File file;

		try {

			String temp1 = trainfn;
			if (trainfn.startsWith("./")) {
				temp1 = trainfn.substring(2, trainfn.lastIndexOf('.'));
			}
			String temp2 = Double.toString(lambda);

			String opFileName = "./LearningCurve_".concat(temp1).concat("_").concat(temp2).concat(".R");

			file = new File(opFileName);
			// if file does not exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			printstr = new PrintStream(file);

			printstr.println("jpeg(\"C:/graph.jpg\")");

			DecimalFormat decFormat = new DecimalFormat("#.######");
			int boundary = dTrainData.length * 10 + 10;
			printstr.println("dataSize<-seq(20," + Integer.toString(boundary) + ",10)");

			printstr.print("traindata<-c(");
			int Trainrows = dTrainData.length;
			for (int index = 0; index < (Trainrows - 1); index++) {
				printstr.print(decFormat.format(dTrainData[index]) + ",");
			}
			/* Last item should not contain , but should end with ) */
			printstr.print(decFormat.format(dTrainData[(Trainrows - 1)]) + ")");
			printstr.println("");

			printstr.print("testdata<-c(");
			int Testrows = dTestData.length;
			for (int index = 0; index < (Testrows - 1); index++) {
				printstr.print(decFormat.format(dTestData[index]) + ",");
			}
			/* Last item should not contain , but should end with ) */
			printstr.print(decFormat.format(dTestData[(Testrows - 1)]) + ")");
			
			/* Find the range for ylim (as we have to plot 2 curves */
			Arrays.sort(dTrainData);
			double ylimMin = dTrainData[0];
			double ylimMax = dTrainData[(dTrainData.length - 1)];
			Arrays.sort(dTestData);
			if (dTestData[0] < ylimMin) {
				ylimMin = dTestData[0];
			}
			if (dTestData[(dTestData.length - 1)] > ylimMax) {
				ylimMax = dTestData[(dTestData.length - 1)];
			}

			printstr.println("");
			printstr.println("plot(dataSize,traindata,type=\"l\",col=\"red\"," + "ylab=\"Ein\"," + "ylim=c(" + decFormat.format(ylimMin) + ","
					+ decFormat.format(ylimMax) + "))");
			printstr.println("par(new=TRUE)");
			printstr.println("plot(dataSize,testdata,type=\"l\",col=\"blue\","
					+ "ylab=\"Eout\",sub=\"Red=Training Blue=Test\"," + "ylim=c(" + decFormat.format(ylimMin) + ","
					+ decFormat.format(ylimMax) + "))");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			if (printstr != null) {
				printstr.close();
			}

		}
	}
	

	/*Write CV results in a Text file (i.e.) Best lambda value chosen is written
	 *  with its corresponding E_out Val*/
	public void writeCVResToTxtFile(String fname, int foldVal, double lambdaBeginVal,
			double dCVlambda[], double bestLambdaWithMinErr[]) {
		PrintStream printstr = null;
		File file;

		try {

			String temp1 = fname;
			if (fname.startsWith("./")) {
				temp1 = fname.substring(2, fname.lastIndexOf('.'));
			}
			String temp2 = Integer.toString(foldVal);

			String opFileName = "./CVResult_".concat(temp1).concat("_FoldVal_").concat(temp2).concat(".txt");

			file = new File(opFileName);
			// if file does not exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			printstr = new PrintStream(file);

			DecimalFormat decFormat = new DecimalFormat("#.######");

			printstr.println("lambdaValuesChoosen:(" + Double.toString(lambdaBeginVal) + ":" + Double.toString(dCVlambda.length) + ")");
			printstr.print("E_out Values:(");

			int lambdaVals = dCVlambda.length;
			for (int index = 0; index < (lambdaVals - 1); index++) {
				printstr.print(decFormat.format(dCVlambda[index]) + ",");
			}
			printstr.print(decFormat.format(dCVlambda[(lambdaVals - 1)]) + ")");
			printstr.println("");

			printstr.println("Best Lambda:"
					+ decFormat.format(bestLambdaWithMinErr[1] + lambdaBeginVal) +
					" E_out Corresponding to it:" + decFormat.format(bestLambdaWithMinErr[0])); 


		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			if (printstr != null) {
				printstr.close();
			}

		}
	}
	

}