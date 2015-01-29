import java.text.DecimalFormat;

import Jama.*;

public class LinearRegression {

	// main function
	// provide command line arguments as
	// filename
	// splitToken
	// Number of Features
	public static void main(String args[]) {

		// Read the training file
		Read_Parse_InputFile trainingFile = new Read_Parse_InputFile(args[0], args[1], args[2], args[3]);

		int rows = Integer.parseInt(args[2]);
		int cols = Integer.parseInt(args[3]);
		Matrix mtFeaturesData = new Matrix(rows, cols);
		Matrix mtOutputVal = new Matrix(rows, 1);
		if (mtFeaturesData != null && mtOutputVal != null) {
			trainingFile.readCsvFile(mtFeaturesData, mtOutputVal);

		}

		/***********************************************************************/
		// Read the testing file
		int rowsTest = Integer.parseInt(args[6]);
		int colsTest = Integer.parseInt(args[7]);
		
		
		Read_Parse_InputFile testingFile = new Read_Parse_InputFile(args[4], args[5], args[6], args[7]);

		Matrix mtTestFeaturesData = new Matrix(rowsTest, colsTest);
		Matrix mtTestOutputVal = new Matrix(rowsTest, 1);
		if (mtTestFeaturesData != null && mtTestOutputVal != null) {
			testingFile.readCsvFile(mtTestFeaturesData, mtTestOutputVal);

		}

		/***********************************************************************/
		// Compute E(w) and store weights for training data for a choosen lambda
		// On using weights calculated in previous step calculate MSE for testdata
		DataTrainAndTest dataTT = new DataTrainAndTest(rows, cols, 
				rowsTest, colsTest);
		double MSETrainingSet[] = new double[150];
		double MSETestingSet[] = new double[150];
		double lambda = 1.;
		for (int index = 0; index < 150; index++) {
			dataTT.computeL2Regression(mtFeaturesData, mtOutputVal, rows, cols, 
					lambda);
			MSETrainingSet[index] = dataTT.getMSETrainData(); 
			
			//Predict the output values on testdata
			//Compare it with actual value in testdata
			//calculate MSE on testdata
			dataTT.outputPredictor(mtTestFeaturesData);
			MSETestingSet[index] = dataTT.MSETestData(mtTestOutputVal, rowsTest);
			
			/*System.out.println("lambda=" + lambda + " "+  
			MSETrainingSet[index] + " " + MSETestingSet[index]);*/
			lambda = lambda + 1;
		}
		WriteToFile objwtof = new WriteToFile();
		objwtof.writeMSEToFile(args[0], args[4], MSETrainingSet, MSETestingSet);
		
/*		DecimalFormat decFormat = new DecimalFormat("#.######");
		for (int index = 0; index < 150; index++){
			System.out.print(decFormat.format(MSETrainingSet[index]) +",");
		}
		
		for (int index = 0; index < 150; index++){
			System.out.print(decFormat.format(MSETestingSet[index]) +",");
		}*/





		/*
		 * SplitCsvFile splitFile = new SplitCsvFile(); try {
		 * splitFile.splitCsvFile("./train-1000-100.csv",
		 * "./50(1000)_100_train.csv", 50);
		 * splitFile.splitCsvFile("./train-1000-100.csv",
		 * "./100(1000)_100_train.csv", 50);
		 * splitFile.splitCsvFile("./train-1000-100.csv",
		 * "./150(1000)_100_train.csv", 50); } catch (FileNotFoundException e) {
		 * // TODO Auto-generated catch block e.printStackTrace(); }
		 */
		
/*		CrossValidation cv = new CrossValidation();
		cv.splitDataToKFolds(mtFeaturesData, mtOutputVal, rows, cols, 5);*/
	}
}