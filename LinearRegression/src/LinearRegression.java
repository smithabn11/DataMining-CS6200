/*Main Function for LinearRegression
 * Author: Smitha BN*/
import java.io.FileNotFoundException;
import java.util.Random;

import Jama.*;

public class LinearRegression {

	// main function
	// provide command line arguments as
	// args[0] = training filename
	// args[1] = splitToken
	// args[2] = Number of Rows in training file
	// args[3] = Number of Cols in training file
	// args[4] = testing filename
	// args[5] = splitToken
	// args[6] = Number of Rows in testing file
	// args[7] = Number of Cols in testing file
	// args[8] = "Q1" or "Q2"
	// args[9] = lambda Value for Q2 
	//NOTE: Q3 contains args[0], args[1] and args[2] = "Q3" and args[3] = foldVal
	public static void main(String args[]) {

		WriteToFile objwtof = new WriteToFile();
		
		/***********************************************************************/
		/*
		 * Q1 - Compute MSE for training and test data for lambda 0-150 Compute
		 * MSE and store weights for training data for a chosen lambda On using
		 * weights calculated in previous step calculate MSE for testdata
		 */
		if (args.length > 8 && args[8].equalsIgnoreCase("Q1")) {
			
			// Read the training file
			Read_Parse_InputFile trainingFile = new Read_Parse_InputFile(args[0], args[1], 
					args[2], args[3]);

			int rows = Integer.parseInt(args[2]);
			int cols = Integer.parseInt(args[3]);
			Matrix mtTrainData = new Matrix(rows, cols);
			Matrix mtOutputVal = new Matrix(rows, 1);
			if (mtTrainData != null && mtOutputVal != null) {
				trainingFile.readCsvFile(mtTrainData, mtOutputVal);

			}

			/***********************************************************************/
			// Read the testing file
			int rowsTest = Integer.parseInt(args[6]);
			int colsTest = Integer.parseInt(args[7]);

			Read_Parse_InputFile testingFile = new Read_Parse_InputFile(args[4], args[5], 
					args[6], args[7]);

			Matrix mtTestData = new Matrix(rowsTest, colsTest);
			Matrix mtTestOutputVal = new Matrix(rowsTest, 1);
			if (mtTestData != null && mtTestOutputVal != null) {
				testingFile.readCsvFile(mtTestData, mtTestOutputVal);

			}
			
			/***********************************************************************/
			// Compute Train and Test MSE
			DataTrainAndTest dataTT = new DataTrainAndTest(cols);
			double MSETrainingSet[] = new double[151];
			double MSETestingSet[] = new double[151];
			double lambda = 0.;
			for (int index = 0; index <= 150; index++) {

				// Compute Training data MSE
				MSETrainingSet[index] = dataTT.computeTrainDataMSE(mtTrainData, mtOutputVal, 
						rows, cols, lambda);

				/*
				 * Predict the output values on testdata. Compare it with actual
				 * value in testdata. Calculate MSE on testdata
				 */
				MSETestingSet[index] = dataTT.computeTestDataMSE(mtTestData, mtTestOutputVal, 
						rowsTest);

				lambda = lambda + 1;
			}
			
			/*To find the Best Lambda and its value*/
			double arr[] = new double[2];
			arr = CrossValidation.findMinValueWithIndex(MSETestingSet);
			
			/* Write the results to R file to plot the graph */
			objwtof.writeMSEToRFile(args[0], args[4], MSETrainingSet, MSETestingSet,arr);
			
		}

		/***********************************************************************/
		// Q2 - Plot Learning Curve
		// args[8] tells to run Q2 if it contains "Q2"
		// args[9] tells the lambda value (default value is assigned 1)
		if (args.length > 8 && args[8].equalsIgnoreCase("Q2")) {
			
			// Read the training file
			Read_Parse_InputFile trainingFile = new Read_Parse_InputFile(args[0], args[1], 
					args[2], args[3]);

			int rows = Integer.parseInt(args[2]);
			int cols = Integer.parseInt(args[3]);
			Matrix mtTrainData = new Matrix(rows, cols);
			Matrix mtOutputVal = new Matrix(rows, 1);
			if (mtTrainData != null && mtOutputVal != null) {
				trainingFile.readCsvFile(mtTrainData, mtOutputVal);

			}

			/***********************************************************************/
			// Read the testing file
			int rowsTest = Integer.parseInt(args[6]);
			int colsTest = Integer.parseInt(args[7]);

			Read_Parse_InputFile testingFile = new Read_Parse_InputFile(args[4], args[5], 
					args[6], args[7]);

			Matrix mtTestData = new Matrix(rowsTest, colsTest);
			Matrix mtTestOutputVal = new Matrix(rowsTest, 1);
			if (mtTestData != null && mtTestOutputVal != null) {
				testingFile.readCsvFile(mtTestData, mtTestOutputVal);

			}

			/***********************************************************************/
			// Compute the learning curve
			double lambdaFixed = 1;
			if (args.length == 10) {
				lambdaFixed = Double.parseDouble(args[9]);
			}

			Random randomGenerator = new Random();
			/* Used for Matrix access of cols */
			int inputCols = cols;
			final int ITERATIONS = 80;
			final int NUM_TO_SMOOTH = 10;
			final int INCREASE_INPUT_DATA = 10;

			/* Start with size 20 on train data */
			/* Increment it every time by adding 10 more data points */
			int inputRows = 20;
			double MSETrainLC[] = new double[ITERATIONS];
			double MSETestLC[] = new double[ITERATIONS];

			/*
			 * Input data size varies from 20 to ((20 + 10) * ITERATIONS)
			 * incrementing data size 10 each time
			 */
			for (int loop = 0; (loop < ITERATIONS) && (inputRows < rows); loop++) {

				// To get smooth Learning curve run each input 10 times
				double train_sum = 0.;
				double test_sum = 0.;
				DataTrainAndTest obj = new DataTrainAndTest(inputCols);

				/* To store the extracted Rows */
				Matrix mtTrainSampleData = new Matrix(inputRows, inputCols);
				Matrix mtTrainSampleOpVal = new Matrix(inputRows, 1);

				/*
				 * This loop is used for Smoothing for the inputRows fixed in
				 * outer loop, run on that data size for 10 times by sampling on
				 * Training data
				 */
				for (int index = 0; index < NUM_TO_SMOOTH; index++) {
					/* Generate random number such that (1000 - inputRows) */
					int inputStart = randomGenerator.nextInt(rows - inputRows);
					/*
					 * To avoid any Negative Numbers if generated by random
					 * number Just an extra check
					 */
					if (inputStart < 0) {
						inputStart = 0;
					}

					/* Fill in the data from mtTrainData and mtOutputVal */
					mtTrainSampleData = mtTrainData.getMatrix(inputStart, (inputStart + inputRows),
							0, (inputCols - 1));
					mtTrainSampleOpVal = mtOutputVal.getMatrix(inputStart, (inputStart + inputRows),
							0, 0);

					train_sum += obj.computeTrainDataMSE(mtTrainSampleData, mtTrainSampleOpVal, 
							inputRows, inputCols,
							lambdaFixed);
					test_sum += obj.computeTestDataMSE(mtTestData, mtTestOutputVal, rowsTest);

				}
				MSETrainLC[loop] = train_sum / NUM_TO_SMOOTH;
				MSETestLC[loop] = test_sum / NUM_TO_SMOOTH;
				inputRows = inputRows + INCREASE_INPUT_DATA;
			}
			/* Write the output to R file to plot the Learning curve */
			objwtof.writeLearingCurveRFile(args[0], lambdaFixed, MSETrainLC, MSETestLC);

		}
		
		/***********************************************************************/
		// Q3 - Cross Validation
		// args[2] tells to run Q3 if it contains "Q3"
		// args[3] tells the fold value (default value is assigned 5)
		if (args.length > 3 && args[2].equalsIgnoreCase("Q3")) {

			int foldVal = 5;
			if (args.length == 4) {
				foldVal = Integer.parseInt(args[3]);
			}

			CrossValidation cv = new CrossValidation(args[0],args[1],foldVal);
			try {
				cv.splitDataToKFolds();
			} catch (Exception e) {				
				e.printStackTrace();
			}
		}
		


		 
	}
}