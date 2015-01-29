import java.text.DecimalFormat;

import Jama.*;

public class DataTrainAndTest {
	
	//stores the weight in cols*1 Matrix
	private Matrix mtWeigths;
	
	private DecimalFormat decFormat;
	
	//stores the predicted output on test data
	//using mtWeigths
	private Matrix mtOutputPredicted;
	
	private Matrix mtDiffPredictedActual;
	
	public DataTrainAndTest(int rowsTrain, int colsTrain, 
			int rowsTest, int colsTest){
		mtWeigths = new Matrix(colsTrain,1);
		mtOutputPredicted = new Matrix(rowsTest, 1);
		mtDiffPredictedActual = new Matrix(rowsTest, 1);
		decFormat = new DecimalFormat("#.######");
	}
	
	public Matrix getWeigths(){
		return mtWeigths;
	}

	public double computeL2Regression(Matrix mtFeaturesData, Matrix mtOutputVal, int rows, int cols, double lambda) {

		//double lambda = 5.;
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
		mtPartialResult = mtPartialResult.identity(cols, cols);
		// mtPartialResult.print(decFormat, 1);

		// Multiply lamba with identity matrix
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
		
		//Copy and store the weights in cols*1
		mtWeigths = mtPartialResult.copy();
		//mtWeigths.print(decFormat, 1);
		
		// Compute w(transpose)*w
		// store in mtWeigthsMagintude
		Matrix mtWeightsTranspose = new Matrix(mtPartialResult.getColumnDimension(), mtPartialResult.getRowDimension());
		// Compute w(transpose)
		mtWeightsTranspose = mtPartialResult.transpose();

		Matrix mtWeigthsMagnitude = new Matrix(mtWeightsTranspose.getRowDimension(),
				mtPartialResult.getColumnDimension());

		mtWeigthsMagnitude = mtWeightsTranspose.times(mtPartialResult);

		// Multiply mtWeigthsMagnitude by lambda
		// store the result in mtWeigthsMagnitude
		double dlambdaWeigthsMagnitude = lambda * mtWeigthsMagnitude.get(0, 0);

		// Calculate 1/2(||Xw - y||) = (1/N)*SumOfAll[(X(i)w - Y(i))square]
		double dpartialSum = 0.;
		int inputRows = mtFeaturesData.getRowDimension();
		// Array starts from 0 Ex. 0:99
		int inputCols = (mtFeaturesData.getColumnDimension() - 1);
		for (int index = 0; index < inputRows; index++) {
			Matrix temp = new Matrix(1, 1);
			// First extract each X(i) (i.e each row)
			temp = mtFeaturesData.getMatrix(index, index, 0, inputCols);
			// Matrix Multiply X(i) with w
			temp = temp.times(mtPartialResult);

			// Subtract X(i)w - y(i)
			// Extract individual row of mtOutputVal(it has only one column)
			temp = temp.minus(mtOutputVal.getMatrix(index, index, 0, 0));

			// Square the obtained result and add it to dpartialSum
			dpartialSum += Math.pow((temp.get(0, 0)), 2);

		}
		// Take the average i.e divide by N
		dpartialSum = dpartialSum / inputRows;

		//System.out.println(dpartialSum + " " + dlambdaWeigthsMagnitude);

		// Now final compute E(w) = (1/N)*SumOfAll[(X(i)w - Y(i))square]
		return (dpartialSum + dlambdaWeigthsMagnitude);
		
	}
	
	public void outputPredictor(Matrix mtTestData){
		mtOutputPredicted = mtTestData.times(mtWeigths);			
	}
	
	public void diffPredictedActual(Matrix mtActualOutput){
		mtDiffPredictedActual = mtActualOutput.minus(mtOutputPredicted); 
		
		WriteToFile objwtof = new WriteToFile();
		objwtof.writeMatrixToFile("./mtActualOutput.txt", mtActualOutput);
		objwtof.writeMatrixToFile("./mtOutputPredicted.txt", mtOutputPredicted);
		objwtof.writeMatrixToFile("./mtDiffPredictedActual.txt", mtDiffPredictedActual);
	}
	
	
}