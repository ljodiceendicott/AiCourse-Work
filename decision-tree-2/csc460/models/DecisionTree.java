// File:   DecisionTree.java
// Author: Structure and support code by Hank Feild.
package csc460.models;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import csc460.data.Dataset;
import csc460.data.Observation;
import csc460.evaluation.BinaryClassificationEvaluation;

/**
 * Supports training a decision tree using an adapted version of the C4.5
 * algorithm as well as running new observations through the decision tree to
 * predict a label.
 * 
 * @author Hank Feild
 * @author YOU
 */
public class DecisionTree {

    /**
     * Represents a node in the decision tree.
     */
    public class Node {
        HashMap<Double,Integer> labelDistribution;
        int n, featureIndex;
        double threshold;
        Node lessThanEqualChild, greaterThanChild;

        /**
         * Sets the Node's data members for a leaf node.
         * 
         * @param labelDistribution The distribution over labels represented in
         *                          this subtree.
         * @param n The total number of labels represented in this subtree.
         */
        public Node(HashMap<Double,Integer> labelDistribution, int n){
            this(labelDistribution, n, 0, 0, null, null);
        }

        /**
         * Sets the Node's data members for an internal node.
         * 
         * @param labelDistribution The distribution over labels represented in
         *                          this subtree.
         * @param n The total number of labels represented in this subtree.
         * @param featureIndex The index of the feature to split on (only for
         *                     internal nodes).
         * @param threshold The threshold of the feature to use to determine
         *                  which child to direct new observations to (only for
         *                  internal nodes).
         * @param lessThanChild The child to send observations to with a feature
         *                      value less than or equal to the threshold (only
         *                      for internal nodes).
         * @param greatherThanChild The child to send observations to with a
         *                          feature value greater than the threshold
         *                          (only for internal nodes).
         */
        public Node(HashMap<Double,Integer> labelDistribution, int n, 
                    int featureIndex, double threshold, Node lessThanEqualChild, 
                    Node greaterThanChild){
            
            this.labelDistribution = labelDistribution;
            this.n = n;
            this.featureIndex = featureIndex;
            this.threshold = threshold;
            this.lessThanEqualChild = lessThanEqualChild;
            this.greaterThanChild = greaterThanChild;
        }

        /**
         * @return True if this is a leaf node.
         */
        public boolean isLeafNode(){
            return lessThanEqualChild == null;
        }
    }

    Node root;

    /**
     * Initializes the root of the tree to null.
     */
    public DecisionTree() {
        root = null;
    }

    /**
     * Trains a decision tree on the set of observations and stores this in
     * `self.root`.
     * 
     * @param observations A list of Observations with labels.
     */
    public void train(ArrayList<Observation> observations){
        root = build(observations);
        prune();
    }

    /**
     * Builds a decision tree on the set of observations.
     * 
     * @param observations A list of Observations with labels.
     * @return A Node representing a subtree or leaf.
     */
    public Node build(ArrayList<Observation> observations){
        return null;
    }

    /**
     * Prunes the tree rooted at `self.root`.
     */
    public void prune(){

    }

    /**
     * Finds the threshold for the given feature that does the best job 
     * splitting observations into two sets according to the `evaluate` function. 
        
     * @param observations A list of Observation instances, with labels.
     * @param featureIndex The index of the feature in each Obseration to
                            consider a threshold for.
     * @return The best threshold for the specified feature.
     */
    public double findBestThreshold(ArrayList<Observation> observations, 
                             int featureIndex)
    {
        ArrayList<Observation> sp1 = new ArrayList<>();
        ArrayList<Observation> sp2 = new ArrayList<>();
        double best = -1;
        double eval = -1;
        for(int i=1; i<observations.size()-1; i++){
            for(int j=0; j<i;j++){
                sp1.add(observations.get(j));
            }
            for(int j=i;j<observations.size();j++){
                sp2.add(observations.get(j));
            }
            eval = this.evaluate(sp1, sp2);
            if(eval>best){
                best = eval;
            }
        }
        return best;
    }

    /**
     * Evaluates the effectiveness of split1 and split2 for partioning 
     * observations by label.

     * @param split1 A list of Observations.
     * @param split2 A list of Observations.
     * @return A score of how good the splits are. Higher is better.
     */
    public double evaluate(ArrayList<Observation> split1, 
                           ArrayList<Observation> split2){
        ArrayList<Observation> fullList = new ArrayList<Observation>();
        fullList.addAll(split1);
        fullList.addAll(split2);
        
        return entropy(fullList) - 
            split1.size()/fullList.size()*entropy(split1) -
            split2.size()/fullList.size()*entropy(split2);
            
    }

    /**
     * Calculates the entropy of a given set of observations based on labels.
     *      H(S) = \sum_{x \in X} -p(x) \log_2(p(x))
     * 
     * @param split A collection of observation.
     * @return The entropy of the split.
     */
    public double entropy(ArrayList<Observation> split){
        double entropySum = 0;
        HashMap<Double, Integer> distribution = getLabelDistribution(split);
        double total = sumCounts(distribution.values());
        double p;

        for(Double label : distribution.keySet()){
            p = distribution.get(label) / total;
            if(p != 0){
                entropySum -= p * Math.log(p)/Math.log(2);
            }
        }
        return entropySum;
    }

    /**
     * Sums all of the integers in a list.
     * 
     * @param counts A list of numbers to sum.
     * @return The sum of the intergers in `counts`.
     */
    public int sumCounts(Iterable<Integer> counts){
        int sum = 0;
        for(Integer count : counts){
            sum += count;
        }
        return sum;
    }

    /**
     * Counts the number of each distinct target label in the given set of
     * observations.
     * 
     * @param split A collection of observation.
     * @return A map of target labels and their counts.
     */
    public HashMap<Double, Integer> getLabelDistribution(Iterable<Observation> split){
        HashMap<Double, Integer> distribution = new HashMap<Double, Integer>();
        for(Observation obs : split){
            if(distribution.containsKey(obs.target)){
                distribution.put(obs.target, distribution.get(obs.target)+1);
            } else {
                distribution.put(obs.target, 1);
            }
        }
        return distribution;
    }

    /**
     * Saves a model of the decision tree (`self.root`) to the given file.
     * This file can be used with the `loadModel()` function.
     * 
     * @param filename The name of the file to save the model to.
     */
    public void saveModel(String filename){

    }

    /**
     * Loads a decision tree model into this instance.
     * 
     * @param filename The name of the file to load the model from.
     */
    public void loadModel(String filename){

    }

    /**
     * Runs the observation through the decision tree and produces a class
     * label prediction.
     * 
     * @param observation The Observation to classify.
     * @return The predicted class label of `observation`.
     */
    public double classify(Observation observation){
        Node curNode = root;
        double labelWithMaxCount = -1;
        int maxCount = 0;

        while(!curNode.isLeafNode()){

            if(observation.numericFeatures.get(curNode.featureIndex) <= curNode.threshold){
                curNode = curNode.lessThanEqualChild;
            }
            else{
                curNode = curNode.greaterThanChild;
            }
        }
        //curNode a leaf node
        for(Double label: curNode.labelDistribution.keySet()){
            if(curNode.labelDistribution.get(label)> maxCount){
                maxCount = curNode.labelDistribution.get(label);
                labelWithMaxCount = label;
            }
        }

        return labelWithMaxCount;
    }

    public static void main(String[] args) throws IOException {
        final String USAGE = 
            "There are two modes to this: training and prediction.\n\n"+
            "Training:\n"+
            "Usage: DecisionTree -train <training file> <model output file>\n"+
            "where...\n"+
            "   <training file> is a comma separated table of features and a label\n"+
            "                   in the final column; header included\n"+
            "   <model output file> is the name of the file to write the trained model to\n"+
            "\n"+
            "Prediction:\n"+
            "Usage: decision-tree.py -predict <testing file> <model file> [--eval] [--positive=PC]\n"+
            "where...\n"+
            "   <testing file> is a comma separated table of features in the same\n"+
            "                  order used during training. Optionally, a label can\n"+
            "                  be in the final column; a header should be present\n"+
            "   <model file> should contain the decision tree model to use for prediction\n"+
            "   --eval, if present, causes the program to output evaluation results\n"+
            "                  instead of predictions.\n"+
            "   --positive=PC, if present, will use PC as the positive class (should be a number)\n"+
            "\n"+
            "The output of the of prediction is the <testing file> data with a new\n"+
            "column: predicted_label";
        

        DecisionTree tree = new DecisionTree();
        String trainingFilename, testingFilename, modelFilename;
        Dataset trainData, testData;
        boolean evaluate = false;
        ArrayList<Double> predictions = new ArrayList<Double>(),
                          truth = new ArrayList<Double>();
        double positiveClass = 0;
        

        // Check that enough arguments were specified.
        if(args.length < 3){
            System.err.println("Too few arguments.\n");
            System.err.println(USAGE);
            System.exit(1);
        }

        // Training mode.
        if(args[0].equals("-train")){
            trainingFilename = args[1];
            modelFilename = args[2];

            trainData = Dataset.parseNumericDataFile(trainingFilename, true);
            tree.train(trainData.observations);
            tree.saveModel(modelFilename);

        // Prediction mode.
        } else if(args[0].equals("-predict")){
            testingFilename = args[1];
            modelFilename = args[2];

            for(int i = 3; i < args.length; i++){
                if(args[i].equals("--eval")){
                    evaluate = true;
                } else if(args[i].startsWith("--positive=")){
                    positiveClass = Double.parseDouble(args[i].replace("--positive=", ""));
                }
            }

            testData = Dataset.parseNumericDataFile(testingFilename, true);
            tree.loadModel(modelFilename);

            // Get classification predictions.
            for(Observation observation : testData.observations){
                predictions.add(tree.classify(observation));
                truth.add(observation.target);
            }

            // Either evaluate or print out the results.
            if(evaluate){
                BinaryClassificationEvaluation eval = new BinaryClassificationEvaluation(predictions, truth, positiveClass);
                System.out.println("Accuracy: "+ eval.getAccuracy() +"\n"+
                    "F1: "+ eval.getF1());

            } else {
                System.out.println(testData.columnNamesAsCSV() +",predicted");
                for(int i = 0; i < predictions.size(); i++){
                    System.out.println(testData.observations.get(i).toString() +','+ predictions.get(i));
                }
            }

        // Unknown mode.
        } else {
            System.err.println("Unrecognized mode: "+ args[0] +"\n");
            System.err.println(USAGE);
            System.exit(1);
        }

    }
}