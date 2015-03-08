import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.Map.Entry;

import weka.core.Instance;
import weka.core.Instances;

/*Takes input as an data and test arff file and runs kNN algorithm for values
 * k = 1, 3, 5, 7, 9 and produces output in textfile kNNOutput.txt*/
public class kNNClassifier {
	public static BufferedReader readDataFile(String filename) {
		BufferedReader inputReader = null;

		try {
			inputReader = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException ex) {
			System.err.println("File not found: " + filename);
		}

		return inputReader;
	}
	
	public static void normaliseData(Instances _data, int numOfAttributes, double[] mean, double[] sd){
		int numOfInstances = _data.numInstances();

		for (int indexOfInstance = 0; indexOfInstance < numOfInstances; indexOfInstance++) {
			Instance tempInstance = _data.instance(indexOfInstance);
			for (int attrIndex = 0; attrIndex < numOfAttributes; attrIndex++){
				mean[attrIndex] += tempInstance.value(tempInstance.attribute(attrIndex));
			}				
		}
		for (int attrIndex = 0; attrIndex < numOfAttributes; attrIndex++){
			mean[attrIndex] = mean[attrIndex]/numOfInstances ;
		}	
		
		// calculate the sum of squares 
		double varianceTest[] = {0., 0., 0., 0.};
		for (int indexOfInstance = 0; indexOfInstance < numOfInstances; indexOfInstance++)
		{ 
			Instance tempInstance = _data.instance(indexOfInstance);
			for (int attrIndex = 0; attrIndex < numOfAttributes; attrIndex++){
				varianceTest[attrIndex] += Math.pow(
										  (tempInstance.value(tempInstance.attribute(attrIndex)) - mean[attrIndex]) ,
										  2) ;
			}
		}	
		
		for (int attrIndex = 0; attrIndex < numOfAttributes; attrIndex++){
			sd[attrIndex] = Math.sqrt(varianceTest[attrIndex]/numOfInstances);
		}
	}

	public static void main(String[] args) throws Exception {
		
		/*Read the data file and testfile using Instances 
		 * provided from weka*/
		BufferedReader datafile = readDataFile("train.arff");
		BufferedReader testfile = readDataFile("test.arff");

		Instances data = new Instances(datafile);
		Instances test = new Instances(testfile);

		data.setClassIndex(data.numAttributes() - 1);
		int numOfDataInstances = data.numInstances();
		int numOfTestInstances = test.numInstances();
		int kValues[] = { 1, 3, 5, 7, 9 };
		
		//int kValues[] = { 3 };
		//System.out.println(numOfDataInstances + " " + numOfTestInstances);
		
		PrintStream printstr = null;
		File file;

		try {
			
			/*Calculate the mean and SD for normalizing data*/
			double meanData[] = {0., 0., 0., 0.};
			double sdData[] = {0., 0., 0., 0.};
			normaliseData(data,  data.numAttributes() - 1, meanData, sdData);

			
			/*Calculate the mean and SD for normalizing test*/
			double meanTest[] = {0., 0., 0., 0.};
			double sdTest[] = {0., 0., 0., 0.};
			normaliseData(test, test.numAttributes(), meanTest, sdTest);

				
			/*Print the output in text file
			 * For exampke, if xi = (6.7, 3.1, 4.4, 1.4) is of class setosa when 
			 * k = 1, 3, 5 but predicts class versicolor when k = 7, 9, then 
			 * output line for x should be: 
			 * 6.7, 3.1, 4.4, 1.4, xi, setosa, setosa, setosa, versicolor, versicolor
			 * NOTE : xi is not in accordance to test file. Just printed sequentially
			 * to identify the rows easily*/
			String opFileName = "./kNNOutput.txt";

			file = new File(opFileName);
			// if file does not exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			printstr = new PrintStream(file);

			/*For each of test instance calculate the minimum distance for each of the train instance
			 * and store in TreeMap. TreeMap is a where keys are sorted in min to max.
			 * I use distance calculated for a test instance and a training instance as key
			 * and the value store is the CLASS label corresponding to that training instance*/
			for (int indexOfTestInstance = 0; indexOfTestInstance < numOfTestInstances; indexOfTestInstance++) {
				/*Stores the sorted minimum distances*/
				TreeMap<Double, String> tm = new TreeMap<Double, String>();

				Instance testInstance = test.instance(indexOfTestInstance);
				/*calculate the distance between testInstance and each of the data instances*/
				for (int indexOfDataInstance = 0; indexOfDataInstance < numOfDataInstances; indexOfDataInstance++) {
					Instance dataInstance = data.instance(indexOfDataInstance);

					double temp = 0;
					for (int attrIndex = 0; attrIndex < testInstance.numAttributes(); attrIndex++) {
						double normalisedDataVal = ((dataInstance.value(dataInstance.attribute(attrIndex)) -
								meanData[attrIndex])/sdData[attrIndex]);
						double normalisedTestVal = ((testInstance.value(testInstance.attribute(attrIndex)) -
								meanTest[attrIndex])/sdTest[attrIndex]);
						temp += Math.pow((normalisedDataVal - normalisedTestVal), 2);
					}
					tm.put(Math.sqrt(temp), (dataInstance.stringValue(dataInstance.attribute(4))));
				}

				int kIndex = 0;
				/*Used to store Class label output for each of the kValues*/
				List<String> lstOutputKNN = new ArrayList<String>();
				
				/*Iterates through each of the given kValues*/
				while (kIndex < kValues.length) {
					// Get a set of the entries
					Set<Entry<Double, String>> set = tm.entrySet();
					// Get an iterator
					Iterator<Entry<Double, String>> itr = set.iterator();
					int counter = 0;
					Hashtable<String, Integer> htOutputClass = new Hashtable<String, Integer>();
					Hashtable<String, Double> httypeMinDist = new Hashtable<String, Double>();
					/*Initializes values of hashtables to contain 0*/
					for (int i = 0; i < data.numClasses(); i++) {
						htOutputClass.put(data.classAttribute().value(i), 0);
						httypeMinDist.put(data.classAttribute().value(i), 0.);
					}

					Map.Entry me;
					// count the number of instances for given k
					while (itr.hasNext() && (counter < kValues[kIndex])) {
						me = (Map.Entry) itr.next();
						
						//System.out.print(me.getKey() + ": ");
						//System.out.println(me.getValue());					
						
						int val = (int) htOutputClass.get(me.getValue());
						htOutputClass.put((String) me.getValue(), val + 1);
						
						/*Store the total minimum value of a given type used when we encountered ties*/
						double minDist = (double) httypeMinDist.get(me.getValue());
						httypeMinDist.put((String) me.getValue(), ((double)me.getKey() + minDist));
						
						counter++;
					}
					
					//System.out.println(httypeMinDist.keySet() + " " + httypeMinDist.values());
					//System.out.println(indexOfTestInstance + " " + htOutputClass.keySet() + " " + htOutputClass.values());
										
					Set<String> keys = htOutputClass.keySet();
					String output = "";
					int maxVal = Integer.MIN_VALUE;
					for (String key : keys) {
						int tempVal = htOutputClass.get(key);
						if (tempVal > maxVal) {
							maxVal = tempVal;
							output = key;
						}else if(tempVal == maxVal && output != ""){
							/*If two or more classes receive the same (winning) number 
							 * of votes, break the tie by choosing the class with the 
							 * minimum total distance from the test point to its voting examples.*/
							/*output contains previous type
							 * key contains current type
							 * now find which has total minimum distance using httypeMinDist */
							double temp1MinDist = httypeMinDist.get(output);
							double temp2MinDist = httypeMinDist.get(key);
							if(temp1MinDist  > temp2MinDist){
								output = key;
								//System.out.println(output + " " + temp2MinDist);
								//System.out.println(httypeMinDist.keySet() + " " + httypeMinDist.values());
							}							
						}
					}
					//System.out.println(output + " " + maxVal);
					lstOutputKNN.add(output);
					
					/*process next kth value*/
					kIndex++;

				}

				printstr.print(testInstance + "," + "x".concat(Integer.toString(indexOfTestInstance + 1) + ","));

				for (int i = 0; i < (lstOutputKNN.size() - 1); i++) {
					printstr.print(lstOutputKNN.get(i) + ",");
				}
				printstr.print(lstOutputKNN.get(lstOutputKNN.size() - 1));
				printstr.println("");
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			if (printstr != null) {
				printstr.close();
			}

		}
	}
}