import java.text.DecimalFormat;

import Jama.*;

public class DataTrainAndTest {

	// stores the weight in cols*1 Matrix
	private Matrix mtWeigths;

	/*Used to format decimal places*/
	private DecimalFormat decFormat;


	public DataTrainAndTest(int colsTrain) {
		mtWeigths = new Matrix(colsTrain, 1);
		decFormat = new DecimalFormat("#.######");
	}


	public double computeTrainDataMSE(Matrix mtFeaturesData, Matrix mtOutputVal, int rows, int cols, double lambda) {


		// print(NumberFormat format, int width)
		// mtFeaturesData.print(decFormat, 1);
		// mtOutputVal.print(decFormat, 1);

		// Find the transpose of the matrix
		Matrix mtFD_Transpose = new Matrix(cols, rows);
		mtFD_Transpose = mtFeaturesData.transpose();
		// mtFD_Transpose.print(decFormat, 1);

		// Find multiplication of Transpose and Features Data Matrix
		// mtFD_Transpose*mtFeaturesData
		// (row of mtFD_Transpose * cols mtFeaturesData)
		Matrix mtProdFDTranspose = new Matrix(mtFD_Transpose.getRowDimension(), mtFeaturesData.getColumnDimension());
		mtProdFDTranspose = mtFD_Transpose.times(mtFeaturesData);
		// mtProdFDTranspose.print(decFormat, 1);

		// Find the identity matrix of size [cols][cols]
		// and store it in the mtPartialResult
		Matrix mtPartialResult = new Matrix(cols, cols);
		mtPartialResult = Matrix.identity(cols, cols);
		// mtPartialResult.print(decFormat, 1);

		// Multiply lambda with identity matrix
		mtPartialResult = mtPartialResult.times(lambda);
		// mtPartialResult.print(decFormat, 1);

		// Add (X(transpose)*X + lambda*I) and store in mtPartialResult
		mtPartialResult = mtPartialResult.plus(mtProdFDTranspose);
		// mtPartialResult.print(decFormat, 1);

		// Find the inverse of mtPartialResult computed from last step
		// and store it in mtPartialResult
		mtPartialResult = mtPartialResult.inverse();
		// mtPartialResult.print(decFormat, 1);

		// Multiply mtPartialResult with mtFD_Transpose and mtOutputVal
		// and store it in mtPartialResult
		// After this step mtPartialResult has Weights stored
		mtPartialResult = mtPartialResult.times(mtFD_Transpose);
		mtPartialResult = mtPartialResult.times(mtOutputVal);

		// Copy and store the weights in cols*1
		mtWeigths = mtPartialResult.copy();
		// mtWeigths.print(decFormat, 1);

		// Calculate 1/N(||Xw - y||) = (1/N)*SumOfAll[(X(i)w - Y(i))square]
		double dpartialSum = 0.;
		int inputRows = mtFeaturesData.getRowDimension();
		
		// Array starts from 0 Ex. 0:99
		int inputCols = (mtFeaturesData.getColumnDimension() - 1);
		for (int index = 0; index < inputRows; index++) {
			Matrix temp = new Matrix(1, 1);
			// First extract each X(i) (i.e each row)
			temp = mtFeaturesData.getMatrix(index, index, 0, inputCols);
			// Matrix Multiply X(i) with w
			temp = temp.times(mtWeigths);

			// Subtract X(i)w - y(i)
			// Extract individual row of mtOutputVal(it has only one column)
			temp = temp.minus(mtOutputVal.getMatrix(index, index, 0, 0));

			// Square the obtained result and add it to dpartialSum
			dpartialSum += Math.pow((temp.get(0, 0)), 2);

		}
		// Take the average i.e divide by N => MSE on train data
		dpartialSum = dpartialSum / inputRows;


		// Now final compute MSE = (1/N)*SumOfAll[(X(i)w - Y(i))square]
		return (dpartialSum);

	}


	public double computeTestDataMSE(Matrix mtTestData, Matrix mtActualOutput, int rowsTest) {
		
		//Calculate the predicted output using weights calculated  
		//from training data
		Matrix mtOutputPredicted = new Matrix(rowsTest, 1);
		mtOutputPredicted = mtTestData.times(mtWeigths);
		
		// calculate the difference between actual and predicted
		Matrix mtDiffPredictedActual = new Matrix(rowsTest, 1);
		mtDiffPredictedActual = mtActualOutput.minus(mtOutputPredicted);

		// Calculate the mean square error = (1/N)*SumOfAll[(actual - predicted)square]
		double mse = 0.;
		for (int index = 0; index < rowsTest; index++) {
			mse += Math.pow((mtDiffPredictedActual.get(index, 0)),2);
		}

		return (mse / rowsTest);
	}

}