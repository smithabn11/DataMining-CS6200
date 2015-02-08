import java.util.ArrayList;
import java.util.List;

import Jama.*;

public class CrossValidation {

	private String trainfname;
	private int rows;
	private int cols;
	private int foldVal;
	private int numOfChunks;
	private String splitToken;

	public CrossValidation(String _trainfname, String _splitToken, int _foldVal) {
		trainfname = _trainfname;
		rows = 0;
		cols = 0;
		foldVal = _foldVal;
		numOfChunks = 0;
		splitToken = _splitToken;
	}

	public void splitDataToKFolds() {

		List<double[]> trainData = new ArrayList<double[]>();
		/*Read the training data file*/
		Read_Parse_InputFile trainingFile = new Read_Parse_InputFile(trainfname, splitToken);
		trainingFile.readCsvFile(trainData);

		/* Training Data row and cols length */
		rows = trainData.size();
		cols = trainData.get(0).length;
		numOfChunks = rows / foldVal;

		Matrix testDataCV = new Matrix(numOfChunks, cols - 1);
		Matrix testOutputCV = new Matrix(numOfChunks, 1);

		Matrix trainDataCV = new Matrix(rows - numOfChunks, cols - 1);
		Matrix trainOutputCV = new Matrix(rows - numOfChunks, 1);

		/* Get best lambda out of this */
		int RANGE_LAMBDA_VAL = 151;

		double MSETrainingSet[] = new double[foldVal];
		double MSETestingSet[] = new double[foldVal];
		double CV_lambda[] = new double[RANGE_LAMBDA_VAL];

		double lambda = 0;
		for (int counter = 0; counter < RANGE_LAMBDA_VAL; counter++) {
			CV_lambda[counter] = 0;

			for (int index = 0; index < foldVal; index++) {

				/* Fill test data and train data */
				fillTestData(trainData, testDataCV, testOutputCV, index);
				fillTrainData(trainData, trainDataCV, trainOutputCV, index);

				/* Compute MSE */
				DataTrainAndTest dataTT = new DataTrainAndTest(cols - 1);

				try {
					// Compute Training data MSE
					MSETrainingSet[index] = dataTT.computeTrainDataMSE(trainDataCV, trainOutputCV,
							(rows - numOfChunks), cols - 1, lambda);

					/*
					 * Predict the output values on testdata. Compare it with
					 * actual value in testdata. Calculate MSE on testdata
					 */
					MSETestingSet[index] = dataTT.computeTestDataMSE(testDataCV, testOutputCV, numOfChunks);

					CV_lambda[counter] += MSETestingSet[index];

				} catch (RuntimeException e) {
					/*
					 * To handle the case where inverse cannot be found i.e.
					 * Matrix is singular. set MSETrainingSet and MSETestingSet
					 * to -1 in this case NOTE : Happens when lambda is set to
					 * 0.
					 */
					System.out.println("Inverse cannot be computed for lambda" + lambda + "at foldVal" + index);
					e.printStackTrace();
				}
			}

			/* Compute the average performance of lambda */
			CV_lambda[counter] = CV_lambda[counter] / foldVal;
			//System.out.println(CV_lambda[counter]);

			/* Proceed to compute CV for next lambda value */
			lambda = lambda + 1;
		}

		/* Pick the value of lambda with the best average performance */
		double bestLambdaWithMinErr[] = new double[2];
		bestLambdaWithMinErr = findMinValueWithIndex(CV_lambda);

		/* Write the results to the text file */
		WriteToFile objwtof = new WriteToFile();
		objwtof.writeCVResToTxtFile(trainfname, foldVal, 0, CV_lambda, bestLambdaWithMinErr);
	}

	/*
	 * Fill the test data from trainData
	 */
	private void fillTestData(List<double[]> trainData, Matrix testDataCV, Matrix testOutputCV, int index) {
		for (int rowNum = 0; rowNum < numOfChunks; rowNum++) {
			for (int colNum = 0; colNum < cols - 1; colNum++) {
				testDataCV.set(rowNum, colNum, trainData.get((numOfChunks * index) + rowNum)[colNum]);
				testOutputCV.set(rowNum, 0, trainData.get((numOfChunks * index) + rowNum)[cols - 1]);
			}
		}

	}

	/*
	 * Rest is the training set Fill in the trainData Matrix leaving the
	 * testData Similarly Fill in the trainOutput Matrix leaving the testOutput
	 */
	private void fillTrainData(List<double[]> trainData, Matrix trainDataCV, Matrix trainOutputCV, int index) {

		// Case1 - Beginning
		// 100 rows 0-19 is given to testData
		// give 20-99 to _trainData

		// Case2 - Ending
		// 100 rows 80-99 is given to testData
		// give 0-79 to _trainData

		// Case3 - Middle
		// 100 rows 20-39 is given to testData
		// give 0-19 to _trainData and 40-99 to _trainData

		int foldsEncountered = 0;
		for (int fold = 0; fold < foldVal; fold++) {
			/* Skip the test fold */
			if (fold == index)
				continue;

			for (int rowNum = 0; rowNum < numOfChunks; rowNum++) {
				for (int colNum = 0; colNum < cols - 1; colNum++) {
					trainDataCV.set(rowNum + (foldsEncountered * numOfChunks), colNum,
							trainData.get((numOfChunks * fold) + rowNum)[colNum]);
					trainOutputCV.set(rowNum + (foldsEncountered * numOfChunks), 0,
							trainData.get((numOfChunks * fold) + rowNum)[cols - 1]);
				}
			}
			foldsEncountered++;
		}

	}

	/*
	 * Function To find the minimum value in an array GIVEN: an input array of
	 * type double RETURN : an array of type double containing 2 elements. First
	 * one is indicating minimum Value and next element is index of minimum
	 * value in input array
	 */
	public static double[] findMinValueWithIndex(double[] array) {
		/* To return MinValue and Index corresponding to MinValue */
		double min_index[] = new double[2];

		min_index[0] = 999999;
		min_index[1] = 0;
		for (int i = 0; i < array.length; i++) {
			if ((array[i] < min_index[0])) {
				min_index[0] = array[i];
				min_index[1] = i;
			}
		}		
		return min_index;
	}
}