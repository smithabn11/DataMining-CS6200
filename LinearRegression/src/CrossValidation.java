import java.text.DecimalFormat;

import Jama.*;

public class CrossValidation {

	private int rows;
	private int cols;
	private int foldVal;
	private int numOfChunks;

	public CrossValidation(int _rows, int _cols, int _foldVal) {
		rows = _rows;
		cols = _cols;
		foldVal = _foldVal;
		numOfChunks = (rows / foldVal);
	}

	// split training data into kfoldVal chunks
	// here since we have separately read the data
	// and output value we need to divide each rpt.
	// Set aside each chunk for testing and perform
	// training on rest of the chunks.
	public void splitDataToKFolds(Matrix mtFeaturesData, Matrix mtOutputValue) {

		int rowFetchStart = 0;
		int rowFetchEnd = (numOfChunks - 1);

		Matrix testData = new Matrix(numOfChunks, cols);
		Matrix testOutput = new Matrix(numOfChunks, 1);

		Matrix trainData = new Matrix(rows - numOfChunks, cols);
		Matrix trainOutput = new Matrix(rows - numOfChunks, 1);

		int RANGE_LAMBDA_VAL = 10;
		/* Get best lambda out of this */
		double MSETrainingSet[][] = new double[foldVal][RANGE_LAMBDA_VAL];
		double MSETestingSet[][] = new double[foldVal][RANGE_LAMBDA_VAL];

		DecimalFormat decFormat = new DecimalFormat("#.######");

		/* Iterate foldVal times */
		for (int index = 0; (index < foldVal); index++) {
			// This gives the test set
			testData = mtFeaturesData.getMatrix(rowFetchStart, rowFetchEnd, 0, (cols - 1));
			testOutput = mtOutputValue.getMatrix(rowFetchStart, rowFetchEnd, 0, 0);
			// testData.print(decFormat, 1);
			rowFetchStart = rowFetchEnd + 1;
			rowFetchEnd = rowFetchEnd + numOfChunks;

			// This gives the training set
			fillTrainData(mtFeaturesData, mtOutputValue, trainData, trainOutput, rowFetchStart, index);

			DataTrainAndTest dataTT = new DataTrainAndTest(cols);

			double lambda = 0.;
			for (int counter = 0; counter < RANGE_LAMBDA_VAL; counter++) {

				try {
					// Compute Training data MSE
					MSETrainingSet[index][counter] = dataTT.computeTrainDataMSE(trainData, trainOutput,
							(rows - numOfChunks), cols, lambda);

					/*
					 * Predict the output values on testdata. Compare it with
					 * actual value in testdata. Calculate MSE on testdata
					 */
					MSETestingSet[index][counter] = dataTT.computeTestDataMSE(testData, testOutput, numOfChunks);

				} catch (RuntimeException e) {
					/*
					 * To handle the case where inverse cannot be found i.e.
					 * Matrix is singular. set MSETrainingSet and MSETestingSet
					 * to -1 in this case
					 */
					// e.printStackTrace();
					MSETrainingSet[index][counter] = -1.;
					MSETestingSet[index][counter] = -1.;
				}
				lambda = lambda + 1;
			}
		}

		WriteToFile objwtof = new WriteToFile();
		double min_index[] = new double[2];
		for (int inc = 0; (inc < foldVal); inc++) {
			min_index = findMinValue(MSETrainingSet[inc]);
			System.out.println(inc + " " + min_index[0] + " " + min_index[1]);
			min_index = findMinValue(MSETestingSet[inc]);
			System.out.println(inc + " " + min_index[0] + " " + min_index[1]);
			objwtof.writeMSEToRFile("CV_", Integer.toString(inc), MSETrainingSet[inc], MSETestingSet[inc]);
		}
		
		
		
	}

	/*
	 * Rest is the training set Fill in the trainData Matrix leaving the
	 * testData Similarly Fill in the trainOutput Matrix leaving the testOutput
	 */
	private void fillTrainData(Matrix mtFeaturesData, Matrix mtOutputValue, Matrix trainData, Matrix trainOutput,
			int rowFetchStart, int index) {

		// Case1 - Beginning
		// 100 rows 0-19 is given to testData
		// give 20-99 to trainData
		if (index == 0) {
			trainData = mtFeaturesData.getMatrix(rowFetchStart, (rows - 1), 0, (cols - 1));
			trainOutput = mtOutputValue.getMatrix(rowFetchStart, (rows - 1), 0, 0);
		}
		// Case2 - Ending
		// 100 rows 80-99 is given to testData
		// give 0-79 to trainData
		else if (index == (foldVal - 1)) {
			trainData = mtFeaturesData.getMatrix(0, (rowFetchStart - 1), 0, (cols - 1));
			trainOutput = mtOutputValue.getMatrix(0, (rowFetchStart - 1), 0, 0);
		}
		// Case3 - Middle
		// 100 rows 20-39 is given to testData
		// give 0-19 to trainData and 40-99 to trainData
		else {
			/*
			 * fetch 0 to (index*numOfChunks - 1) fetch index*numOfChunks +
			 * numOfChunks to (rows - 1)
			 */

			/* fetch 0 to (index*numOfChunks - 1) */
			for (int num = 0; num < (index * numOfChunks); num++) {
				/* Fill trainData */
				Matrix temp = mtFeaturesData.getMatrix(num, num, 0, (cols - 1));
				trainData.setMatrix(num, num, 0, (cols - 1), temp);
				/* Fill trainOutput */
				Matrix temp1 = mtOutputValue.getMatrix(num, num, 0, 0);
				trainOutput.setMatrix(num, num, 0, 0, temp1);
			}

			/* fetch (index*numOfChunks + numOfChunks) to (rows - 1) */
			for (int num = (index * numOfChunks + numOfChunks); num < rows; num++) {
				/* Fill trainData */
				Matrix temp = mtFeaturesData.getMatrix(num, num, 0, (cols - 1));
				trainData.setMatrix((num - numOfChunks), (num - numOfChunks), 0, (cols - 1), temp);
				/* Fill trainOutput */
				Matrix temp1 = mtOutputValue.getMatrix(num, num, 0, 0);
				trainOutput.setMatrix((num - numOfChunks), (num - numOfChunks), 0, 0, temp1);
			}
		}

	}

	public static double[] findMinValue(double[] array) {
		/* To return MinValue and Index corresponding to MinValue */
		double min_index[] = new double[2];

		min_index[0] = 999999;
		min_index[1] = 0;
		for (int i = 0; i < array.length; i++) {
			if ((array[i] < min_index[0]) && (array[i] != -1.0)) {
				min_index[0] = array[i];
				min_index[1] = i;
			}
		}

		return min_index;
	}
}