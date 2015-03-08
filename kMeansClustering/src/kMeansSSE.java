import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import weka.core.Instance;

public class kMeansSSE {

	public static double computeSSE(double[][] curMeanValuesOfCentroid,
			LinkedHashMap<Integer, ArrayList<Integer>> hmclusters, double[][] normalisedInstance, int kValue,
			int numOfAttributes, int numOfDataInstances, PrintStream printstr) {

		double SSE[] = new double[kValue];
		double meanfinalSSE = 0.;
		/* For each cluster calculate SSEi and add each to form SSE */
		Iterator<Map.Entry<Integer, ArrayList<Integer>>> iterator = hmclusters.entrySet().iterator();
		int kArrayIndex = 0;
		while (iterator.hasNext()) {
			Map.Entry<Integer, ArrayList<Integer>> entry = iterator.next();
			// printstr.println("Key:" + entry.getKey() + " ");

			Iterator<Integer> arrLstIter = entry.getValue().iterator();
			while (arrLstIter.hasNext()) {
				// printstr.print(arrLstIter.next() + " ");
				int centroidIndex = arrLstIter.next();
				for (int attrIndex = 0; attrIndex < numOfAttributes; attrIndex++) {
					SSE[kArrayIndex] += Math
							.pow((normalisedInstance[centroidIndex][attrIndex] - curMeanValuesOfCentroid[kArrayIndex][attrIndex]),
									2);
				}
			}
			kArrayIndex++;
		}

		for (int index = 0; index < kValue; index++) {
			printstr.println("SSE[" + index + "]=" + SSE[index]);
			meanfinalSSE += SSE[index];
		}
		printstr.println("meanfinalSSE=" + meanfinalSSE);
		return meanfinalSSE;
	}
	
	public static double computeMeanK(double meanfinalSSE[], int totalIterations){
		double mean = 0.;

		for (int index = 0; index < totalIterations; index++) {
			mean += meanfinalSSE[index];
		}

		mean = mean / totalIterations;		
		return mean;
	}

	public static double computeStandardDeviationK(double meanfinalSSE[], double mean, int totalIterations) {
		// Initialization
		double variance = 0.;
		double sd = 0.;

		for (int index = 0; index < totalIterations; index++) {
			mean += meanfinalSSE[index];
		}

		mean = mean / totalIterations;

		// calculate the sum of squares
		for (int index = 0; index < totalIterations; index++) {
			variance += Math.pow((meanfinalSSE[index] - mean), 2);
		}
		sd = Math.sqrt(variance / totalIterations);

		return sd;
	}
}