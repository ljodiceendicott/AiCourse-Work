// File:   kNN.java
// Author: Structure and support code by Hank Feild;
// TODOs completed by: 

package csc460.models;

import csc460.data.*;
import java.util.ArrayList;
import java.io.IOException;


/**
 * Performs classification and regression using the k-Nearest Neighbors 
 * algorithm.
 */
public class kNN {
    /**
     * A wrapper for any metric that measures the distance between two
     * n-dimensional points.
     */
    public interface Distance {
        public double distance(ArrayList<Double> featuresA, ArrayList<Double> featuresB);
    }
    public class Tuple {
        public double distance;
        public Observation observation;

        Tuple(double dist, Observation ob){
            this.distance = dist;
            this.observation= ob;
        }
    }

    /**
     * Calculates the Euclidean distance between two sets of numeric features.
     */
    public class EuclideanDistance implements Distance {
        public double distance(ArrayList<Double> featuresA, ArrayList<Double> featuresB){
            double sum = 0;
            for(int i=0; i<featuresA.size(); i++){
                double product = Math.pow((featuresA.get(i) - featuresB.get(i)), 2);
                sum = sum + product;
            }
            return Math.sqrt(sum);
        }
    }

    /**
     * Calculates the Manhattan distance between two sets of numeric features.
     */
    public class ManhattanDistance implements Distance {
        public double distance(ArrayList<Double> featuresA, ArrayList<Double> featuresB){
            double sum = 0;
            for(int i=0; i<featuresA.size(); i++){
                sum = sum + Math.abs(featuresA.get(i) - featuresB.get(i));
            }
            return sum;
        }
    }

    /**
     * Identifies the k closest neighbors to newObservation in trainingSet 
     * (using only numeric features).
     * 
     * @param newObservation The observation to find neighbors of.
     * @param trainingSet The set of observations to search for neighbors in.
     * @param k The number of nearest neighbors to consider when determining the label.
     * @param dist The distance function to use.
     * @return A list of the k closest observations in trainingSet to 
     *         newObservation.
     */
    public ArrayList<Observation> getKNearestNeighbors(Observation newObservation, 
        ArrayList<Observation> trainingSet, int k, Distance dist) {
       ArrayList<Tuple> nearest = new ArrayList<>();

        for(int i=0; i<trainingSet.size(); i++){
            double d = dist.distance(trainingSet.get(i).numericFeatures, newObservation.numericFeatures);

            if(nearest.size()<= k){
                nearest.add(new Tuple(d,trainingSet.get(i)));
            }
            else{
                double max = d;
                Observation maxob = null;
                int maxloc = 0;
                for(int j=0; j<nearest.size(); j++){
                    if(max < nearest.get(i).distance){
                        maxob = nearest.get(i).observation;
                        max = nearest.get(i).distance;
                        maxloc = j;
                    }
                    break;
                }
                //something was found that was bigger than the value 
                if(maxob != null){
                    nearest.remove(maxloc);
                    nearest.add(new Tuple(d,trainingSet.get(i)));
                }
            }
        }
        // TODO -- Implement this. In the observations, only consider the
        //         numeric features.
        ArrayList<Observation> nearestk = new ArrayList<>();
        for(int i= 0; i<nearest.size();i++){
            nearestk.add(nearest.get(i).observation);
        }

        return nearestk;
    }


    /**
     * Predict the label of newObservation based on the majority label among 
     * the k nearest observations in trainingSet (only uses numeric features).
     * 
     * @param newObservation The observation to classify.
     * @param trainingSet The set of observations to derive the label from.
     * @param k The number of nearest neighbors to consider when determining the label.
     * @param dist The distance function to use.
     * @return The predicted numeric label of newObservation.
     */
    public double classify(Observation newObservation, 
            ArrayList<Observation> trainingSet, int k, Distance dist){
        ArrayList<Observation> knearest = this.getKNearestNeighbors(newObservation, trainingSet, k, dist);
        ArrayList<Tuple> popvals = new ArrayList<>();
        for(int i = 0; i<knearest.size(); i++){
            for(int j = 0; j< popvals.size(); j++){
                if(knearest.get(i).target == popvals.get(j).observation.target){
                    popvals.get(j).distance++;
                    break;
                }
                else if(knearest.get(i).target != popvals.get(j).observation.target){
                    popvals.add(new Tuple(0, knearest.get(i)));
                }
            }
        }
        double largest = Double.MIN_VALUE;
        Observation mostCommon = null;
        for(int i = 0; i<popvals.size(); i++){
            if(popvals.get(i).distance> largest){
                largest = popvals.get(i).distance;
                mostCommon = popvals.get(i).observation;
            }
        }
        // TODO -- Implement this.

        return mostCommon.target;
    }
    /**
     * Predict the dependent variable of newObservation based on the mean of
     * the k nearest observations in trainingSet (only uses numeric features).
     * 
     * @param newObservation The observation to classify.
     * @param trainingSet The set of observations to derive the label from.
     * @param k The number of nearest neighbors to consider when determining the label.
     * @param dist The distance function to use.
     * @return The predicted dependent variable of newObservation.
     */
    public double regression(Observation newObservation, 
            ArrayList<Observation> trainingSet, int k, Distance dist){
        double sum = 0;
        for(int i = 1; i<k; i++){
            //i iterated over will be the K in the algo
            sum = sum + this.classify(newObservation, trainingSet, i, dist);
        }
        // TODO -- Implement this.

        return sum/(k-1);
    }
   /**
     * Predicts the 
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        kNN knn = new kNN();
        String trainingFilename, testingFilename, distanceFunction;
        int k;
        Dataset trainData, testData;
        Distance distance;

        if(args.length < 4){
            System.err.println(
                "Usage: kNN <training file> <test file> <k> <distance "+
                    "function> <mode>\n\n"+
                "<distance function> must be one of:\n"+
                "  * euclidean\n"+
                "  * manhattan\n\n"+
                "<mode> must be one of:\n"+
                "  * classify (for binary or multi-class classification)\n"+
                "  * regression (for continuous dependent variables)"
                );
            System.exit(1);
        }

        trainingFilename = args[0];
        testingFilename = args[1];
        k = Integer.parseInt(args[2]);
        distanceFunction = args[3];

        // Figure out which distance measure to use.
        distance = knn.new EuclideanDistance();
        if(distanceFunction.equals("manhattan")){
            distance = knn.new ManhattanDistance();
        }

        // Parse input files
        trainData = Dataset.parseNumericDataFile(trainingFilename, true);
        testData = Dataset.parseNumericDataFile(testingFilename, true);

        // Predict the class label for each observation in the test set.
        System.out.println(testData.columnNamesAsCSV() +",predicted");
        for(Observation observation : testData.observations){
            observation.target = knn.classify(observation, trainData.observations, k, distance);
            System.out.println(observation);
        }
    }
}