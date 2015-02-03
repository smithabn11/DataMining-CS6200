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
	public void splitDataToKFolds(Matrix mtTrainingData, Matrix mtTrainingOpVal, String ipFname) {

		Matrix testDataCV = new Matrix(numOfChunks, cols);
		Matrix testOutputCV = new Matrix(numOfChunks, 1);

		Matrix trainDataCV = new Matrix(rows - numOfChunks, cols);
		Matrix trainOutputCV = new Matrix(rows - numOfChunks, 1);

		/* Get best lambda out of this */
		int RANGE_LAMBDA_VAL = 10;
		
		double MSETrainingSet[] = new double[foldVal];
		double MSETestingSet[] = new double[foldVal];
		double CV_lambda[] = new double[RANGE_LAMBDA_VAL];

		/*Begin with lambda value = 1.0 and continue up to RANGE_LAMBDA_VAL*/
		double lambda = 1.;
		for (int counter = 0; counter < RANGE_LAMBDA_VAL; counter++) {
			
			/* Initialization */
			CV_lambda[counter] = 0.;
			int rowFetchStart = 0;
			int rowFetchEnd = (numOfChunks - 1);

			/* Iterate foldVal times */
			for (int index = 0; (index < foldVal); index++) {

				// This gives the test set (i.e index th fold)
				testDataCV = mtTrainingData.getMatrix(rowFetchStart, rowFetchEnd, 0, (cols - 1));
				testOutputCV = mtTrainingOpVal.getMatrix(rowFetchStart, rowFetchEnd, 0, 0);

				/*
				 * Update the rowFetchStart and rowFetchEnd to fetch training data
				 */
				rowFetchStart = rowFetchEnd + 1;
				rowFetchEnd = rowFetchEnd + numOfChunks;

				// This gives the training set
				fillTrainData(mtTrainingData, mtTrainingOpVal, trainDataCV, trainOutputCV, 
						rowFetchStart, index);

				DataTrainAndTest dataTT = new DataTrainAndTest(cols);

				try {
					// Compute Training data MSE
					MSETrainingSet[index] = dataTT.computeTrainDataMSE(trainDataCV, trainOutputCV, 
							(rows - numOfChunks), cols, lambda);

					/*
					 * Predict the output values on testdata. Compare it with
					 * actual value in testdata. Calculate MSE on testdata
					 */
					MSETestingSet[index] = dataTT.computeTestDataMSE(testDataCV, testOutputCV, 
							numOfChunks);

					CV_lambda[counter] += MSETestingSet[index];

				} catch (RuntimeException e) {
					/*
					 * To handle the case where inverse cannot be found i.e.
					 * Matrix is singular. set MSETrainingSet and MSETestingSet
					 * to -1 in this case
					 * NOTE : Happens when lambda is set to 0.
					 */
					System.out.println("Inverse cannot be computed for lambda" + counter + "at foldVal" + index);
					e.printStackTrace();
				}
			}
			
			/*Compute the average performance of lambda */
			CV_lambda[counter] = CV_lambda[counter] / foldVal;
			
			/*Proceed to compute CV for next lambda value*/
			lambda = lambda + 1;
		}

		/* Pick the value of lambda with the best average performance*/
		double bestLambdaWithMinErr[] = new double[2];
		bestLambdaWithMinErr = findMinValueWithIndex(CV_lambda);
		
		/*Write the results to the text file*/
		WriteToFile objwtof = new WriteToFile();
		objwtof.writeCVResToTxtFile(ipFname, foldVal, 1, CV_lambda,bestLambdaWithMinErr);
	}

	/*
	 * Rest is the training set Fill in the trainData Matrix leaving the
	 * testData Similarly Fill in the trainOutput Matrix leaving the testOutput
	 */
	private void fillTrainData(Matrix mtTrainData, Matrix mtTrainOpValue, Matrix trainDataCV, Matrix trainOutputCV,
			int rowFetchStart, int index) {

		// Case1 - Beginning
		// 100 rows 0-19 is given to testData
		// give 20-99 to trainData
		if (index == 0) {
			trainDataCV = mtTrainData.getMatrix(rowFetchStart, (rows - 1), 0, (cols - 1));
			trainOutputCV = mtTrainOpValue.getMatrix(rowFetchStart, (rows - 1), 0, 0);
		}
		// Case2 - Ending
		// 100 rows 80-99 is given to testData
		// give 0-79 to trainData
		else if (index == (foldVal - 1)) {
			trainDataCV = mtTrainData.getMatrix(0, (rowFetchStart - 1), 0, (cols - 1));
			trainOutputCV = mtTrainOpValue.getMatrix(0, (rowFetchStart - 1), 0, 0);
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
				Matrix temp = mtTrainData.getMatrix(num, num, 0, (cols - 1));
				trainDataCV.setMatrix(num, num, 0, (cols - 1), temp);
				/* Fill trainOutput */
				Matrix temp1 = mtTrainOpValue.getMatrix(num, num, 0, 0);
				trainOutputCV.setMatrix(num, num, 0, 0, temp1);
			}

			/* fetch (index*numOfChunks + numOfChunks) to (rows - 1) */
			for (int num = (index * numOfChunks + numOfChunks); num < rows; num++) {
				/* Fill trainData */
				Matrix temp = mtTrainData.getMatrix(num, num, 0, (cols - 1));
				trainDataCV.setMatrix((num - numOfChunks), (num - numOfChunks), 0, (cols - 1), temp);
				/* Fill trainOutput */
				Matrix temp1 = mtTrainOpValue.getMatrix(num, num, 0, 0);
				trainOutputCV.setMatrix((num - numOfChunks), (num - numOfChunks), 0, 0, temp1);
			}
		}

	}

	/* Function To find the minimum value in an array
	 * GIVEN: an input array of type double
	 * RETURN : an array of type double containing 2 elements. First one is
	 *  indicating minimum Value and next element is index of minimum value 
	 *  in input array*/
	public static double[] findMinValueWithIndex(double[] array) {
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