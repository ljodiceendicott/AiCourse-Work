// File:   kNN.java
// Author: Structure and support code by Hank Feild;

package csc460.models;

import csc460.data.*;
import csc460.evaluation.*;
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
     * @param weighted Whether to use a weighted or unweighted vote.
     * @return The predicted numeric label of newObservation.
     */
    public double classify(Observation newObservation, 
            ArrayList<Observation> trainingSet, int k, Distance dist, boolean weighted){

        ArrayList<NeighborDistance> knn = getKNearestNeighbors(newObservation, 
            trainingSet, k, dist);

        ArrayList<Double> labels = new ArrayList<Double>();
        ArrayList<Double> counts = new ArrayList<Double>();
        // ArrayList<Integer> counts = new ArrayList<Integer>();
        int classifyIndex = -1;
        double countValue;

        for(NeighborDistance neighbor : knn){
            countValue = 1;
            if(weighted){
                countValue = 1/neighbor.distance;
            }

            if(labels.contains(neighbor.neighbor.target)){
                classifyIndex = labels.indexOf(neighbor.neighbor.target);
                // counts.set(classifyIndex, counts.get(classifyIndex)+1);
                counts.set(classifyIndex, counts.get(classifyIndex)+countValue);
            } else {
                labels.add(neighbor.neighbor.target);
                // counts.add(1);
                counts.add(countValue);
            }
        }

        classifyIndex = 0;
        for(int i = 0; i < counts.size(); i++){
            if(counts.get(i) > counts.get(classifyIndex)){
                classifyIndex = i;
            }
        }

        return labels.get(classifyIndex);
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
            ArrayList<Observation> trainingSet, int k, Distance dist, boolean weighted){

        ArrayList<NeighborDistance> knn = getKNearestNeighbors(newObservation, 
            trainingSet, k, dist);

        double sum = 0;

        for(NeighborDistance neighbor : knn){
            double weighteddistance = 1;
            if(weighted){
                weighteddistance = 1/neighbor.distance;
            }
            sum = weighteddistance * neighbor.neighbor.target;
            sum = sum/weighteddistance;
        }
        return sum/k;
    }

    /**
     * Supports classification and regression using kNN.
     * 
     * @param args Command line arguments; see the inline help message.
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        kNN knn = new kNN();
        String trainingFilename, testingFilename, distanceFunction;
        int k;
        Dataset trainData, testData;
        Distance distance;
        boolean evaluate = false, classification, weighted = false;
        ArrayList<Double> predictions = new ArrayList<Double>(),
                          truth = new ArrayList<Double>();
        double positiveClass = 0;

        if(args.length < 4){
            System.err.println(
                "Usage: kNN <training file> <test file> <k> <distance "+
                    "function> <mode> [--eval [--positive=PC]]\n\n"+
                "<distance function> must be one of:\n"+
                "  * euclidean\n"+
                "  * manhattan\n\n"+
                "<mode> must be one of:\n"+
                "  * classify (for binary or multi-class classification)\n"+
                "  * regression (for continuous dependent variables)\n\n"+
                "--weighted, if included, will use a weighted vote or a weighted average\n\n"+
                "--eval, if included, will evaluate the predictions; otherwise, predictions are printed.\n\n"+
                "--positive=PC, if included, will use PC as the positive class (should be a number); for classification only.\n\n"
                );
            System.exit(1);
        }

        trainingFilename = args[0];
        testingFilename = args[1];
        k = Integer.parseInt(args[2]);
        distanceFunction = args[3];
        classification = args[4].equals("classify");
        
        for(int i = 5; i < args.length; i++){
            if(args[i].equals("--eval")){
                evaluate = true;
            } else if(args[i].startsWith("--positive=")){
                positiveClass = Double.parseDouble(args[i].replace("--positive=", ""));
            } else if(args[i].equals("--weighted")){
                weighted = true;
            }
        }

        // Figure out which distance measure to use.
        distance = knn.new EuclideanDistance();
        if(distanceFunction.equals("manhattan")){
            distance = knn.new ManhattanDistance();
        }

        // Parse input files
        trainData = Dataset.parseNumericDataFile(trainingFilename, true);
        testData = Dataset.parseNumericDataFile(testingFilename, true);

        // Predict the class label for each observation in the test set.

        // Get classification predictions.
        if(classification){
            for(Observation observation : testData.observations){
                predictions.add(knn.classify(observation, trainData.observations, k, distance, weighted));
                truth.add(observation.target);
            }
        // Get regression predictions.
        } else {
            for(Observation observation : testData.observations){
                predictions.add(knn.regression(observation, trainData.observations, k, distance, weighted));
                truth.add(observation.target);
            }  
        }

        // Either evaluate or print out the results.
        if(evaluate){
            if(classification){
                BinaryClassificationEvaluation eval = new BinaryClassificationEvaluation(predictions, truth, positiveClass);
                System.out.println("Accuracy: "+ eval.getAccuracy() +"\n"+
                    "F1: "+ eval.getF1());
            } else {
                RegressionEvaluation eval = new RegressionEvaluation(predictions, truth);
                System.out.println("RMSE: "+ eval.getRMSE());
            }
        } else {
            System.out.println(testData.columnNamesAsCSV() +",predicted");
            for(int i = 0; i < predictions.size(); i++){
                System.out.println(testData.observations.get(i).toString() +','+ predictions.get(i));
            }
        }
    }
}