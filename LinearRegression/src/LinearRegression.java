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
		/*
		 * Q1 - Compute MSE for training and test data for lambda 0-150 Compute
		 * MSE and store weights for training data for a chosen lambda On using
		 * weights calculated in previous step calculate MSE for testdata
		 */
		DataTrainAndTest dataTT = new DataTrainAndTest(cols);
		double MSETrainingSet[] = new double[151];
		double MSETestingSet[] = new double[151];
		double lambda = 0.;
		for (int index = 0; index <= 150; index++) {

			// Compute Training data MSE
			MSETrainingSet[index] = dataTT.computeTrainDataMSE(mtFeaturesData, mtOutputVal, rows, cols, lambda);

			/*
			 * Predict the output values on testdata. Compare it with actual
			 * value in testdata. Calculate MSE on testdata
			 */
			MSETestingSet[index] = dataTT.computeTestDataMSE(mtTestFeaturesData, mtTestOutputVal, rowsTest);

			lambda = lambda + 1;
		}

		/* Write the results to R file to plot the graph */
		WriteToFile objwtof = new WriteToFile();
		objwtof.writeMSEToRFile(args[0], args[4], MSETrainingSet, MSETestingSet);

		/***********************************************************************/
		// Q2 - Plot Learning Curve
		if (args.length > 8 && args[8].equalsIgnoreCase("Q2")) {

			double lambdaFixed = 1;
			if (args.length == 10) {
				lambdaFixed = Double.parseDouble(args[9]);
			}
			/* Used for Matrix access of cols */
			int inputCols = cols;
			int ITERATIONS = 80;
			int NUM_TO_SMOOTH = 25;
			int INCREASE_INPUT_DATA = 10;

			/* Start with size 20 on train data */
			/* Increment it every time by adding 10 more data points */
			int inputRows = 20;
			double MSETrainLC[] = new double[ITERATIONS];
			double MSETestLC[] = new double[ITERATIONS];

			for (int loop = 0; (loop < ITERATIONS) && (inputRows < rows); loop++) {

				Matrix mtTrainData = new Matrix(inputRows, inputCols);
				Matrix mtTrainOpVal = new Matrix(inputRows, 1);
				mtTrainData = mtFeaturesData.getMatrix(0, inputRows, 0, (inputCols - 1));
				mtTrainOpVal = mtOutputVal.getMatrix(0, inputRows, 0, 0);
				// objwtof.writeMatrixToFile("./output.txt", mtTrainData);
				// To get smooth Learning curve run each input 10 times
				double train_sum = 0.;
				double test_sum = 0.;
				DataTrainAndTest obj = new DataTrainAndTest(inputCols);
				for (int index = 0; index < NUM_TO_SMOOTH; index++) {

					train_sum += obj.computeTrainDataMSE(mtTrainData, mtTrainOpVal, 
							inputRows, inputCols, lambdaFixed);
					test_sum += obj.computeTestDataMSE(mtTestFeaturesData, mtTestOutputVal, 
							rowsTest);

				}
				MSETrainLC[loop] = train_sum / NUM_TO_SMOOTH;
				MSETestLC[loop] = test_sum / NUM_TO_SMOOTH;
				inputRows = inputRows + INCREASE_INPUT_DATA;
			}
			objwtof.writeLearingCurveRFile(args[0], lambdaFixed, MSETrainLC, MSETestLC);
		}

		
		 CrossValidation cv = new CrossValidation(rows, cols, 5);
		 cv.splitDataToKFolds(mtFeaturesData, mtOutputVal);
		 
	}
}