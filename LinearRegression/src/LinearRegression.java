import Jama.*;

public class LinearRegression{
	
	// main function
	// provide command line arguments as
	// filename
	// splitToken
	// Number of Features
	public static void main(String args[]) {
		
		//Read the training file
		Read_Parse_InputFile trainingFile = new Read_Parse_InputFile(args[0], args[1], args[2], args[3]);

		int rows = Integer.parseInt(args[2]);
		int cols = Integer.parseInt(args[3]);
		Matrix mtFeaturesData = new Matrix(rows, cols);
		Matrix mtOutputVal = new Matrix(rows, 1);
		if (mtFeaturesData != null && mtOutputVal != null) {
			trainingFile.readCsvFile(mtFeaturesData, mtOutputVal);

		}
		
		int rowsTest = Integer.parseInt(args[6]);
		int colsTest = Integer.parseInt(args[7]);
		
		//Compute E(w) and store weights 
		DataTrainAndTest dataTT = new DataTrainAndTest(rows, cols,rowsTest, colsTest);
		dataTT.computeL2Regression(mtFeaturesData, mtOutputVal, rows, cols);
		
		//Read the testing file
		Read_Parse_InputFile testingFile = new Read_Parse_InputFile(args[4], args[5], args[6], args[7]);


		Matrix mtTestFeaturesData = new Matrix(rowsTest, colsTest);
		Matrix mtTestOutputVal = new Matrix(rowsTest, 1);
		if (mtTestFeaturesData != null && mtTestOutputVal != null) {
			testingFile.readCsvFile(mtTestFeaturesData, mtTestOutputVal);

		}	
		
		dataTT.outputPredictor(mtTestFeaturesData);
		dataTT.diffPredictedActual(mtTestOutputVal);	
		
/*		SplitCsvFile splitFile = new SplitCsvFile();
		try {
			splitFile.splitCsvFile("./train-1000-100.csv", "./50(1000)_100_train.csv", 50);
			splitFile.splitCsvFile("./train-1000-100.csv", "./100(1000)_100_train.csv", 50);
			splitFile.splitCsvFile("./train-1000-100.csv", "./150(1000)_100_train.csv", 50);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
}