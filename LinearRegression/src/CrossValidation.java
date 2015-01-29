import java.text.DecimalFormat;

import Jama.*;

public class CrossValidation {

	// split training data into kfoldVal chunks
	// here since we have separately read the data
	// and output value we need to divide each rpt.
	// Set aside each chunk for testing and perform
	// training on rest of the chunks.
	public void splitDataToKFolds(Matrix mtFeaturesData, Matrix mtOutputValue, int rows, int cols, int foldVal) {

		int numOfChunks = (rows / foldVal);
		int rowFetchStart = 0;
		int rowFetchEnd = (numOfChunks - 1);

		Matrix testData = new Matrix(numOfChunks, cols);
		Matrix testOutput = new Matrix(numOfChunks, 1);
		DecimalFormat decFormat = new DecimalFormat("#.######");

		for (int index = 0; (index < foldVal); index++) {
			// System.out.println(rowFetchStart + " " + rowFetchEnd);

			// This gives the test set
			testData = mtFeaturesData.getMatrix(rowFetchStart, rowFetchEnd, 0, (cols - 1));
			testOutput = mtOutputValue.getMatrix(rowFetchStart, rowFetchEnd, 0, 0);
			rowFetchStart = rowFetchEnd + 1;
			rowFetchEnd = rowFetchEnd + numOfChunks;

			Matrix trainData = new Matrix(rows - numOfChunks, cols);
			Matrix trainOutput = new Matrix(rows - numOfChunks, 1);
			// Rest is the training set
			// Fill in the trainData Matrix leaving the testData
			// Similarly Fill in the trainOutput Matrix leaving the testOutput

			//Case1 - Beginning
			// 100 rows 0-19 is given to testData
			// give 20-99 to trainData
			if (index == 0) {
				trainData = mtFeaturesData.getMatrix(rowFetchStart, (rows - 1), 0, (cols - 1));
			}
			//Case2 - Ending
			// 100 rows 80-99 is given to testData
			// give 0-79 to trainData
			else if (index == (foldVal - 1)) {
				trainData = mtFeaturesData.getMatrix(0, (rowFetchStart - 1), 0, (cols - 1));
				trainData.print(decFormat, 1);
			}
			//Case3 - Middle
			// 100 rows 20-39 is given to testData
			// give 0-19 to trainData and 40-99 to trainData
			else {
				/*fetch 0 to (index*numOfChunks - 1)
				fetch index*numOfChunks + numOfChunks to (rows - 1)*/
				System.out.println(0 + " " + (index*numOfChunks - 1));
				System.out.println((index*numOfChunks + numOfChunks) + " " + (rows - 1));
				mtFeaturesData.setMatrix(0, (index*numOfChunks - 1), 0, (cols - 1), trainData);
				mtFeaturesData.setMatrix((index*numOfChunks + numOfChunks),
						(rows - 1), 0, (cols - 1), trainData);
				//trainData.print(decFormat, 1);
				
			}
		}

	}
}