import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import Jama.*;

//class to read the csv file
public class Read_Parse_InputFile {

	private String file;
	private String splitToken;
	private int rowsOfData;
	private int numFeatures;

	// constructor
	Read_Parse_InputFile(String _file, String _splitToken, String _rowsOfData, String _numFeatures) {
		file = _file;
		splitToken = _splitToken;
		rowsOfData = Integer.parseInt(_rowsOfData);
		numFeatures = Integer.parseInt(_numFeatures);
	}
	
	Read_Parse_InputFile(String _file, String _splitToken) {
		file = _file;
		splitToken = _splitToken;
		rowsOfData = 0;
		numFeatures = 0;
	}

	// read input csv file
	public void readCsvFile(Matrix _mtFeaturesData, Matrix _mtOutputVal) {
		BufferedReader bufrdr = null;
		String line = "";

		try {
			bufrdr = new BufferedReader(new FileReader(file));
			int curRowNum = 0;
			boolean startReading = false;
			while ((line = bufrdr.readLine()) != null) {

				String[] data = line.split(splitToken);
				// this avoids reading first line and reads rest of the data
				// Ex: your file has 1001 rows, skip first line and read 1000
				// lines
				if (startReading && curRowNum < (rowsOfData + 1)) {
					for (int index = 0; index < numFeatures; index++) {
						_mtFeaturesData.set(curRowNum, index, Double.parseDouble(data[index]));
						// System.out.print(" x[" + index + "]"+ data[index]);
					}
					_mtOutputVal.set(curRowNum, 0, Double.parseDouble(data[numFeatures]));
					curRowNum++;
				}
				System.out.println("");
				// set to true after skipping the first line
				startReading = true;

			}// end of while
				// _mt.print(rowsOfData,numFeatures);
		} // end of try block
		catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bufrdr != null) {
				try {
					bufrdr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/*Overloaded function.
	 * Takes List<double[]> to fill the data from csv file*/
	public void readCsvFile(List<double[]> trainData){
		BufferedReader bufrdr = null;
		String line = "";
		
		try {
			bufrdr = new BufferedReader(new FileReader(file));
			boolean startReading = true;

			while ((line = bufrdr.readLine()) != null) {
				/* Skip the first line */
				if (startReading == true) {
					startReading = false;
					continue;
				}

				String tokens[] = line.split(splitToken);
				double each_row[] = new double[tokens.length];

				for (int i = 0; i < tokens.length; ++i) {
					each_row[i] = Double.parseDouble(tokens[i]);

				}
				trainData.add(each_row);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bufrdr != null) {
				try {
					bufrdr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}

