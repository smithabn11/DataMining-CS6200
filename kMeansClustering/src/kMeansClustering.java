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
public class kMeansClustering {
	public static BufferedReader readDataFile(String filename) {
		BufferedReader inputReader = null;

		try {
			inputReader = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException ex) {
			System.err.println("File not found: " + filename);
		}

		return inputReader;
	}

	public static void normaliseData(Instances _data, int numOfAttributes, double[] mean, double[] sd) {
		int numOfInstances = _data.numInstances();

		// Initialisation
		double varianceTest[] = new double[numOfAttributes];
		for (int index = 0; index < numOfAttributes; index++) {
			mean[index] = 0.0;
			sd[index] = 0.0;
			varianceTest[index] = 0.0;
		}
		for (int indexOfInstance = 0; indexOfInstance < numOfInstances; indexOfInstance++) {
			Instance tempInstance = _data.instance(indexOfInstance);
			for (int attrIndex = 0; attrIndex < numOfAttributes; attrIndex++) {
				mean[attrIndex] += tempInstance.value(tempInstance.attribute(attrIndex));
			}
		}
		for (int attrIndex = 0; attrIndex < numOfAttributes; attrIndex++) {
			mean[attrIndex] = mean[attrIndex] / numOfInstances;
		}

		// calculate the sum of squares
		for (int indexOfInstance = 0; indexOfInstance < numOfInstances; indexOfInstance++) {
			Instance tempInstance = _data.instance(indexOfInstance);
			for (int attrIndex = 0; attrIndex < numOfAttributes; attrIndex++) {
				varianceTest[attrIndex] += Math.pow(
						(tempInstance.value(tempInstance.attribute(attrIndex)) - mean[attrIndex]), 2);
			}
		}

		for (int attrIndex = 0; attrIndex < numOfAttributes; attrIndex++) {
			sd[attrIndex] = Math.sqrt(varianceTest[attrIndex] / numOfInstances);
		}
	}

	private static void printClusters(HashMap<Integer, ArrayList<Integer>> hmclusters, PrintStream printstr) {
		Iterator<Map.Entry<Integer, ArrayList<Integer>>> iterator = hmclusters.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<Integer, ArrayList<Integer>> entry = iterator.next();
			printstr.println("Key:" + entry.getKey() + "Size:" + entry.getValue().size());

/*			Iterator<Integer> arrLstIter = entry.getValue().iterator();
			while (arrLstIter.hasNext()) {
				printstr.print(arrLstIter.next() + " ");
			}
			printstr.println();*/
		}
	}

	private static void normaliseAndStoreInstances(Instances _data, int numOfAttributes, int numOfDataInstances,
			double[] mean, double[] sd, double[][] normalisedInstance) {

		for (int indexOfDataInstance = 0; indexOfDataInstance < numOfDataInstances; indexOfDataInstance++) {
			Instance dataInstance = _data.instance(indexOfDataInstance);
			for (int attrIndex = 0; attrIndex < numOfAttributes; attrIndex++) {
				/* Normalized value of data */
				double normalisedDataVal = 0.;
				if (sd[attrIndex] != 0) {
					normalisedDataVal = ((dataInstance.value(dataInstance.attribute(attrIndex)) - mean[attrIndex]) / sd[attrIndex]);
				}
				normalisedInstance[indexOfDataInstance][attrIndex] = normalisedDataVal;
			}
		}

	}

	private static void computekMeans(Instances _data, int numOfAttributes, int numOfDataInstances, double[] mean,
			double[] sd) {

		int centroids[] = { 775, 1020, 200, 127, 329, 1626, 1515, 651, 658, 328, 1160, 108, 422, 88, 105, 261, 212,
				1941, 1724, 704, 1469, 635, 867, 1187, 445, 222, 1283, 1288, 1766, 1168, 566, 1812, 214, 53, 423, 50,
				705, 1284, 1356, 996, 1084, 1956, 254, 711, 1997, 1378, 827, 1875, 424, 1790, 633, 208, 1670, 1517,
				1902, 1476, 1716, 1709, 264, 1, 371, 758, 332, 542, 672, 483, 65, 92, 400, 1079, 1281, 145, 1410, 664,
				155, 166, 1900, 1134, 1462, 954, 1818, 1679, 832, 1627, 1760, 1330, 913, 234, 1635, 1078, 640, 833,
				392, 1425, 610, 1353, 1772, 908, 1964, 1260, 784, 520, 1363, 544, 426, 1146, 987, 612, 1685, 1121,
				1740, 287, 1383, 1923, 1665, 19, 1239, 251, 309, 245, 384, 1306, 786, 1814, 7, 1203, 1068, 1493, 859,
				233, 1846, 1119, 469, 1869, 609, 385, 1182, 1949, 1622, 719, 643, 1692, 1389, 120, 1034, 805, 266, 339,
				826, 530, 1173, 802, 1495, 504, 1241, 427, 1555, 1597, 692, 178, 774, 1623, 1641, 661, 1242, 1757, 553,
				1377, 1419, 306, 1838, 211, 356, 541, 1455, 741, 583, 1464, 209, 1615, 475, 1903, 555, 1046, 379, 1938,
				417, 1747, 342, 1148, 1697, 1785, 298, 1485, 945, 1097, 207, 857, 1758, 1390, 172, 587, 455, 1690,
				1277, 345, 1166, 1367, 1858, 1427, 1434, 953, 1992, 1140, 137, 64, 1448, 991, 1312, 1628, 167, 1042,
				1887, 1825, 249, 240, 524, 1098, 311, 337, 220, 1913, 727, 1659, 1321, 130, 1904, 561, 1270, 1250, 613,
				152, 1440, 473, 1834, 1387, 1656, 1028, 1106, 829, 1591, 1699, 1674, 947, 77, 468, 997, 611, 1776, 123,
				979, 1471, 1300, 1007, 1443, 164, 1881, 1935, 280, 442, 1588, 1033, 79, 1686, 854, 257, 1460, 1380,
				495, 1701, 1611, 804, 1609, 975, 1181, 582, 816, 1770, 663, 737, 1810, 523, 1243, 944, 1959, 78, 675,
				135, 1381, 1472 };

		PrintStream printstr = null;
		File file;

		try {

			String opFileName = "./kMeansOutput.txt";

			file = new File(opFileName);
			// if file does not exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			printstr = new PrintStream(file);

			/* pre-compute the normalized values of all instance */
			double normalisedInstance[][] = new double[numOfDataInstances][numOfAttributes];
			normaliseAndStoreInstances(_data, numOfAttributes, numOfDataInstances, mean, sd, normalisedInstance);
			
			double meanK[] = new double[12];
			double sdK[] = new double[12];
			/* Choose different k Values = num of clusters */
			for (int kValue = 1; kValue <= 12; kValue++) {
				/*
				 * for each kValue iterate 25 times choosing different centroid
				 * values
				 */
				int totalIterations = 25;
				double meanfinalSSE[] = new double[totalIterations];
				for (int numOfIterations = 0; numOfIterations < totalIterations; numOfIterations++) {
					printstr.println("kValue:" + kValue + " numOfDifferentCentroidIterations:" + numOfIterations);
					/*
					 * first assign the data instance to nearest cluster then
					 * also find the distance to that cluster and add it to SSE
					 * of that cluster
					 */

					/*
					 * choose a centroid based on kValue and current value of
					 * numOfIteration
					 */
					int kCentroidsStartPos = kValue * numOfIterations;
					int kCentroidsEndPos = kCentroidsStartPos + kValue;
					LinkedHashMap<Integer, ArrayList<Integer>> hmclusters = new LinkedHashMap<Integer, ArrayList<Integer>>();
					ArrayList<ArrayList<Integer>> clusterMembers = new ArrayList<ArrayList<Integer>>(kValue);
					for (int index = 0; index < kValue; index++) {
						ArrayList<Integer> arr = new ArrayList<Integer>();
						clusterMembers.add(arr);
					}
					/*
					 * Iterator<ArrayList<Integer>> arrLstIter1 =
					 * clusterMembers.iterator(); while(arrLstIter1.hasNext()){
					 * System.out.print(arrLstIter1.next()); }
					 */

					/* add centroids as keys to HashMap */
					for (int pos = kCentroidsStartPos; pos < kCentroidsEndPos; pos++) {
						hmclusters.put(centroids[pos], clusterMembers.get(pos - kCentroidsStartPos));
					}

					for (int indexOfDataInstance = 0; indexOfDataInstance < numOfDataInstances; indexOfDataInstance++) {

						double minMeanValue = Double.MAX_VALUE;

						double temp[] = new double[kValue];
						for (int index = 0; index < kValue; index++)
							temp[index] = 0.;

						for (int attrIndex = 0; attrIndex < numOfAttributes; attrIndex++) {
							/* Normalized value of data for that attribute */
							double normalisedDataVal = normalisedInstance[indexOfDataInstance][attrIndex];
							/* For each centroid compute the distance */
							/* do upto kCentroidsStartPos + kValue */
							for (int pos = kCentroidsStartPos; pos < kCentroidsEndPos; pos++) {
								/*
								 * get the instance corresponding to the the
								 * centroids to be choosen
								 */
								double normalisedCentroidVal = normalisedInstance[centroids[pos]][attrIndex];
								double val = Math.pow((normalisedDataVal - normalisedCentroidVal), 2);
								if (!Double.isNaN(val)) {
									temp[pos - kCentroidsStartPos] += val;
								}
								// System.out.println(normalisedDataVal + " " +
								// normalisedCentroidVal + " "+temp[pos]);
							}
						}
						/* put the _dataInstance to the closest cluster */
						int closestCentroid = 0;
						int arrIndex = 0;
						for (int pos = kCentroidsStartPos; pos < kCentroidsEndPos; pos++) {
							if (temp[pos - kCentroidsStartPos] < minMeanValue) {
								minMeanValue = temp[pos - kCentroidsStartPos];
								closestCentroid = centroids[pos];
								arrIndex = pos - kCentroidsStartPos;
							}
						}
						// printstr.println("Data instance " +
						// indexOfDataInstance + "closer to instance " +
						// closestCentroid);
						(clusterMembers.get(arrIndex)).add(indexOfDataInstance);
						hmclusters.put(closestCentroid, clusterMembers.get(arrIndex));
					}

					printClusters(hmclusters, printstr);

					/*
					 * After forming an initial cluster compute final mean values
					 * of each cluster until no change in cluster formation
					 */
					 double computedSSEForKClusters = computeMeanUntilNoChange(hmclusters, normalisedInstance, 
							 kValue, numOfAttributes,
							numOfDataInstances, printstr);

					 meanfinalSSE[numOfIterations] = computedSSEForKClusters/kValue;

				}
				
				/* For each k = kValue compute the mean SSE, which we denote mean(k) and
				   the sample standard deviation of SSE, which we denote sigma(k), over all 25
				   clustering runs for that value of k.*/
				meanK[kValue - 1] = kMeansSSE.computeMeanK(meanfinalSSE, totalIterations);
				sd[kValue - 1] = kMeansSSE.computeStandardDeviationK(meanfinalSSE, meanK[kValue - 1], totalIterations);				
			}
			/*compute the line plot*/
			for(int index = 0; index < 12; index++){
				System.out.println(meanK[index] + " " + sd[index] );
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			if (printstr != null) {
				printstr.close();
			}

		}

	}

	private static double computeMeanUntilNoChange(LinkedHashMap<Integer, ArrayList<Integer>> hmclusters,
			double normalisedInstance[][], int kValue, int numOfAttributes, int numOfDataInstances, PrintStream printstr) {

		
		double curMeanValuesOfCentroid[][] = new double[kValue][numOfAttributes];
		computeMeans(curMeanValuesOfCentroid, hmclusters, normalisedInstance, numOfAttributes, kValue, printstr);
		
		cleanHmCluster(hmclusters);
		
		double meanfinalSSE = 0.;
		/*
		 * add newly calculated centroid means. Just now set the keys from 0 to
		 * k-1
		 */
		int numOfMaxIterationsToStop = 50;
		boolean noChange = true;
		int currIterations = 0;
		while (noChange && (currIterations < numOfMaxIterationsToStop)) {
			
			//printstr.println("currIterations:" + currIterations); 

			ArrayList<ArrayList<Integer>> clusterMembers = new ArrayList<ArrayList<Integer>>(kValue);
			for (int index = 0; index < kValue; index++) {
				ArrayList<Integer> arr = new ArrayList<Integer>();
				clusterMembers.add(arr);
			}
			for (int pos = 0; pos < kValue; pos++) {
				hmclusters.put(pos, clusterMembers.get(pos));
			}

			/* new computed values */
			for (int indexOfDataInstance = 0; indexOfDataInstance < numOfDataInstances; indexOfDataInstance++) {

				double minMeanValue = Double.MAX_VALUE;

				double temp[] = new double[kValue];
				for (int index = 0; index < kValue; index++)
					temp[index] = 0.;

				for (int attrIndex = 0; attrIndex < numOfAttributes; attrIndex++) {
					/* Normalized value of data for that attribute */
					double normalisedDataVal = normalisedInstance[indexOfDataInstance][attrIndex];
					/* For each new centroid compute the distance */
					/* do upto 0 to kValue */
					for (int pos = 0; pos < kValue; pos++) {
						double normalisedCentroidVal = curMeanValuesOfCentroid[pos][attrIndex];
						double val = Math.pow((normalisedDataVal - normalisedCentroidVal), 2);
						if (!Double.isNaN(val)) {
							temp[pos] += val;
						}
					}
				}

				/* put the _dataInstance to the closest cluster */
				int arrIndex = 0;
				for (int pos = 0; pos < kValue; pos++) {
					if (temp[pos] < minMeanValue) {
						minMeanValue = temp[pos];
						arrIndex = pos;
					}
				}
				(hmclusters.get(arrIndex)).add(indexOfDataInstance);
				hmclusters.put(arrIndex, hmclusters.get(arrIndex));

			}
			//printClusters(hmclusters, printstr);
			
			double newMeanValuesOfCentroid[][] = new double[kValue][numOfAttributes];
			computeMeans(newMeanValuesOfCentroid, hmclusters, normalisedInstance, numOfAttributes, kValue, printstr);
			/*if no change in mean values stop*/
			boolean value = checkMeanValues(curMeanValuesOfCentroid , newMeanValuesOfCentroid, kValue, numOfAttributes);
			if(value == true){
				noChange = false;
				/*write the mean value*/
				//System.out.println("currIterations:" + currIterations); 
				
				meanfinalSSE = kMeansSSE.computeSSE(curMeanValuesOfCentroid, hmclusters, 
						normalisedInstance, kValue, numOfAttributes,
						numOfDataInstances, printstr);
			}
			else{
				/*continue for next iteration*/
				copyMeanValues(curMeanValuesOfCentroid , newMeanValuesOfCentroid, kValue, numOfAttributes);
			}
			cleanHmCluster(hmclusters);
			
			currIterations++;
		}
		
		if(currIterations == 50){
			/*Output what values stored in curMeanValuesOfCentroid*/
			meanfinalSSE = kMeansSSE.computeSSE(curMeanValuesOfCentroid, hmclusters, 
					normalisedInstance, kValue, numOfAttributes,
					numOfDataInstances, printstr);
		}
		
		return meanfinalSSE;
	}



	private static void copyMeanValues(double[][] curMeanValuesOfCentroid, double[][] newMeanValuesOfCentroid,
			int kValue, int numOfAttributes) {
		for (int k = 0; k < kValue; k++) {
			for (int attrIndex = 0; attrIndex < numOfAttributes; attrIndex++) {
				curMeanValuesOfCentroid[k][attrIndex] = newMeanValuesOfCentroid[k][attrIndex];
			}
		}
	}
		
	private static boolean checkMeanValues(
			double[][] curMeanValuesOfCentroid, 
			double[][] newMeanValuesOfCentroid,
			int kValue, int numOfAttributes) {
		
		for (int k = 0; k < kValue; k++) {
			for (int attrIndex = 0; attrIndex < numOfAttributes; attrIndex++) {
				
				if(Double.compare(curMeanValuesOfCentroid[k][attrIndex], newMeanValuesOfCentroid[k][attrIndex]) != 0){
					return false;
				}					
			}
		}
		return true;		
	}

	private static void cleanHmCluster(LinkedHashMap<Integer, ArrayList<Integer>> hmclusters) {
		Iterator<Map.Entry<Integer, ArrayList<Integer>>> iterator = hmclusters.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<Integer, ArrayList<Integer>> entry = iterator.next();
			ArrayList<Integer> arrLst = entry.getValue();
			arrLst.clear();
		}
		hmclusters.clear();
	}

	private static void computeMeans(double[][] dMeanValuesOfCentroid,
			LinkedHashMap<Integer, ArrayList<Integer>> hmclusters, double normalisedInstance[][], int numOfAttributes,
			int kValue, PrintStream printstr) {

		initialisemeanValuesOfEachAttribute(dMeanValuesOfCentroid, kValue, numOfAttributes);
		Iterator<Map.Entry<Integer, ArrayList<Integer>>> iterator = hmclusters.entrySet().iterator();
		int kArrayIndex = 0;
		while (iterator.hasNext()) {
			Map.Entry<Integer, ArrayList<Integer>> entry = iterator.next();
			//printstr.println("Key:" + entry.getKey() + " ");

			Iterator<Integer> arrLstIter = entry.getValue().iterator();
			double numOfPoints = entry.getValue().size();
			while (arrLstIter.hasNext()) {
				// printstr.print(arrLstIter.next() + " ");
				int centroidIndex = arrLstIter.next();
				for (int attrIndex = 0; attrIndex < numOfAttributes; attrIndex++) {
					dMeanValuesOfCentroid[kArrayIndex][attrIndex] += normalisedInstance[centroidIndex][attrIndex];
				}
			}

			for (int attrIndex = 0; attrIndex < numOfAttributes; attrIndex++) {
				if(numOfPoints != 0){
					dMeanValuesOfCentroid[kArrayIndex][attrIndex] = (dMeanValuesOfCentroid[kArrayIndex][attrIndex] / numOfPoints);
				}
     			//printstr.print(dMeanValuesOfCentroid[kArrayIndex][attrIndex] + " ");
			}
			kArrayIndex++;
			//printstr.println();
		}

	}

	private static void initialisemeanValuesOfEachAttribute(double[][] dMeanValuesOfCentroid, int kValue,
			int numOfAttributes) {
		for (int k = 0; k < kValue; k++) {
			for (int attrIndex = 0; attrIndex < numOfAttributes; attrIndex++) {
				dMeanValuesOfCentroid[k][attrIndex] = 0.;
			}
		}

	}

	public static void main(String[] args) throws Exception {

		/*
		 * Read the data file using Instances provided from weka
		 */
		BufferedReader datafile = readDataFile("segment.arff");

		Instances data = new Instances(datafile);

		data.setClassIndex(data.numAttributes() - 1);
		int numOfDataInstances = data.numInstances();
		int numOfAttributes = data.numAttributes();

		/* Calculate the mean and SD for normalizing data */
		double meanData[] = new double[numOfAttributes];
		double sdData[] = new double[numOfAttributes];
		normaliseData(data, numOfAttributes, meanData, sdData);
		computekMeans(data, numOfAttributes, numOfDataInstances, meanData, sdData);
	}

}