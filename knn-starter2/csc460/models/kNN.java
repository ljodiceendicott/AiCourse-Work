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

    /**
     * Calculates the Euclidean distance between two sets of numeric features.
     */
    public class EuclideanDistance implements Distance {
        public double distance(ArrayList<Double> featuresA, ArrayList<Double> featuresB){
            // TODO -- Implement this.
            double sum = 0;

            for(int i = 0; i < featuresA.size(); i++){
                sum += Math.pow(featuresA.get(i) - featuresB.get(i), 2);
            }

            return Math.sqrt(sum);
        }
    }

    /**
     * Calculates the Manhattan distance between two sets of numeric features.
     */
    public class ManhattanDistance implements Distance {
        public double distance(ArrayList<Double> featuresA, ArrayList<Double> featuresB){
            // TODO -- Implement this.
            double sum = 0;

            for(int i = 0; i < featuresA.size(); i++){
                sum += Math.abs(featuresA.get(i) - featuresB.get(i));
            }

            return sum;
        }
    }

    /**
     * Encapsulates the distance to a neighboring observation.
     */
    public class NeighborDistance {
        Observation neighbor;
        double distance;

        public NeighborDistance(Observation neighbor, double distance){
            this.neighbor = neighbor;
            this.distance = distance;
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
    public ArrayList<NeighborDistance> getKNearestNeighbors(Observation newObservation, 
        ArrayList<Observation> trainingSet, int k, Distance dist) {

        ArrayList<NeighborDistance> nearest = new ArrayList<NeighborDistance>();
        double curDistance;
        int maxValueIndex = -1;

        for(Observation neighbor : trainingSet){
            curDistance = dist.distance(neighbor.numericFeatures, newObservation.numericFeatures);
     
            // Case 1: there's still room in nearest.
            if(nearest.size() < k) {
                nearest.add(new NeighborDistance(neighbor, curDistance));
            
            // Case 2: check if it should replace something already in nearest.
            } else {
                // Find the maximum distance in our list of nearest neighbors.
                maxValueIndex = 0;
                for(int i = 1; i < nearest.size(); i++){
                    if(nearest.get(i).distance > nearest.get(maxValueIndex).distance){
                        maxValueIndex = i;
                    }
                }

                // If the current neighbor is closer, swap it with the farthest
                // neighbor that we're tracking.
                if(curDistance < nearest.get(maxValueIndex).distance){
                    nearest.get(maxValueIndex).distance = curDistance;
                    nearest.get(maxValueIndex).neighbor = neighbor;
                }
            }
        }

        return nearest;
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

        // TODO -- Implement this.

        return -1;
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

        // TODO -- Implement this.

        return -1;
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