// File:   DecisionTree.java
// Author: Structure and support code by Hank Feild.

package csc460.models;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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

    // A helper class for feature/threshold selection.
    public class FeatureInfo {
        public double threshold, evalMeasure;
        public int featureIndex;

        public FeatureInfo(double threshold, double evalMeasure, int featureIndex){
            this.threshold = threshold;
            this.evalMeasure = evalMeasure;
            this.featureIndex = featureIndex;
        }
    }

    // A helper class to help sort a list of observations based on a given feature.
    public class ObservationSorter implements Comparator<Observation>{
        int featureIndex;
        public ObservationSorter(int featureIndex){
            this.featureIndex = featureIndex;
        }

        /**
         * Compares a and b based on the value of the numeric feature with index
         * `featureIndex`.
         * 
         * @param a An observation.
         * @param b An observation.
         * @return <0 if a is less than b, 0 if a and b are equal, and >0 if a is
         *          greater than b.
         */
        public int compare(Observation a, Observation b){
            return a.numericFeatures.get(featureIndex).compareTo(b.numericFeatures.get(featureIndex));
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
        HashMap<Double,Integer> labelDist = getLabelDistribution(observations);
        if(labelDist.keySet().size() == 1){
            return new Node(labelDist, observations.size());
        }

        FeatureInfo featureInfo = null, bestFeatureInfo = null;
        ArrayList<Observation> lessThanEqualSplit, greaterThanSplit;

        for(int i = 0; i < observations.get(0).numericFeatures.size(); i++){
            featureInfo = findBestThreshold(observations, i);
            if(bestFeatureInfo == null || featureInfo.evalMeasure > featureInfo.evalMeasure){
                bestFeatureInfo = featureInfo;
            }
        }

        if(bestFeatureInfo.evalMeasure == 0){
            return new Node(labelDist, observations.size());
        }

        lessThanEqualSplit = new ArrayList<Observation>();
        greaterThanSplit = new ArrayList<Observation>();
        for(Observation obs : observations){
            if(obs.numericFeatures.get(bestFeatureInfo.featureIndex) <= bestFeatureInfo.threshold){
                lessThanEqualSplit.add(obs);
            } else {
                greaterThanSplit.add(obs);
            }
        }

        return new Node(labelDist, observations.size(), bestFeatureInfo.featureIndex,
            bestFeatureInfo.threshold, 
            build(lessThanEqualSplit),
            build(greaterThanSplit));
    }

    /**
     * Prunes the tree rooted at `self.root`.
     */
    public void prune(){
        Node iter = root;
        
        //loop through till get to the end
        //doing dfs but using the entire tree
        //calculate the information gain
        //prune the subtrees that have a low information gain

        //continue this until the accuracy does not change past the threshhold
    }

    /**
     * Finds the threshold for the given feature that does the best job 
     * splitting observations into two sets according to the `evaluate` function. 
        
     * @param observations A list of Observation instances, with labels.
     * @param featureIndex The index of the feature in each Obseration to
                            consider a threshold for.
     * @return The best threshold for the specified feature.
     */
    public FeatureInfo findBestThreshold(ArrayList<Observation> observations, 
                             int featureIndex)
    {
        double lastFeatureVal = observations.get(0).numericFeatures.get(featureIndex);
        FeatureInfo bestFeature = new FeatureInfo(lastFeatureVal, 0, featureIndex);
        Collections.sort(observations, new ObservationSorter(featureIndex));
        double evalMeasure, currentFeatureVal;

        for(int i = 0; i < observations.size(); i++){
            currentFeatureVal = observations.get(i).numericFeatures.get(featureIndex);
            if(lastFeatureVal < currentFeatureVal){
                evalMeasure = evaluate(observations.subList(0,i), observations.subList(i,observations.size()));
                if(evalMeasure > bestFeature.evalMeasure){
                    bestFeature = new FeatureInfo(
                        (currentFeatureVal+lastFeatureVal)/2, 
                        evalMeasure, featureIndex);
                }
            }

            if(i < observations.size()-1){
                lastFeatureVal = 
                    (observations.get(i).numericFeatures.get(featureIndex) +
                    observations.get(i+1).numericFeatures.get(featureIndex))/2;
            }
        }

        return bestFeature;
    }

    /**
     * Evaluates the effectiveness of split1 and split2 for partioning 
     * observations by label.

     * @param split1 A list of Observations.
     * @param split2 A list of Observations.
     * @return A score of how good the splits are. Higher is better.
     */
    public double evaluate(Collection<Observation> split1, 
                           Collection<Observation> split2){
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
    public double entropy(Collection<Observation> split){
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
     * Saves a model of the decision tree (`self.root`) to the given file. This file
     * can be used with the `loadModel()` function. The format of this file is:
     * 
     *    id
     *    comma-separated list of label:count pairs (label distribution)
     *    n,featureIndex,threshold,lessThanEqualChildID,greaterThanChildID
     * 
     * These three lines are repeated once per node in the decision tree.
     * For leaf nodes, everything after 'n' on the last line is missing.
     * 
     * @param filename The name of the file to save the model to.
     * @throws IOException
     */
    public void saveModel(String filename) throws IOException {
        BufferedWriter output = new BufferedWriter(new FileWriter(filename));
        ArrayList<Node> queue = new ArrayList<Node>();
        queue.add(root);

        for(int i = 0; i < queue.size(); i++){
            Node node = queue.get(i);

            // Print id.
            output.write(i + "\n");

            // Print label distribution.
            for(double label : node.labelDistribution.keySet())
                output.write(label +":"+ node.labelDistribution.get(label) +",");
            output.write("\n");

            // Print n.
            output.write(node.n+"");

            // Print the other fields if nod is internal and add the children to
            // the queue.
            if(!node.isLeafNode()){
                output.write(","+ node.featureIndex +","+ node.threshold +
                    ","+ queue.size() +","+ (queue.size()+1));
                queue.add(node.lessThanEqualChild);
                queue.add(node.greaterThanChild);
            }

            output.write("\n");
        }

        output.close();
    }

    /**
     * Loads a decision tree model into this instance. The format of this file
     * should be:
     * 
     *    id
     *    comma-separated list of label:count pairs (label distribution)
     *    n,featureIndex,threshold,lessThanEqualChildID,greaterThanChildID
     * 
     * These three lines are repeated once per node in the decision tree. For leaf
     * nodes, everything after 'n' on the last line is missing.
     * 
     * @param filename The name of the file to load the model from.
     * @throws IOException
     */
    public void loadModel(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        ArrayList<Node> nodes = new ArrayList<Node>();
        // Holds 3-tuples: (parentId, lessThanEqualChildId, greaterThanChildId)
        ArrayList<ArrayList<Integer>> ids = new ArrayList<ArrayList<Integer>>();

        // Read in all the nodes.
        while(reader.ready()){
            try{
                Node node = new Node(new HashMap<Double,Integer>(), 0);
                int id = Integer.parseInt(reader.readLine().strip());
                String columns[];

                // Read in and process the label distribution. 
                String labelCountPairs[] = reader.readLine().strip().split(",");
                for(int i = 0; i < labelCountPairs.length; i++){
                    columns = labelCountPairs[i].split(":");
                    node.labelDistribution.put(Double.parseDouble(columns[0]), Integer.parseInt(columns[1]));
                }

                columns = reader.readLine().strip().split(",");
                node.n = Integer.parseInt(columns[0]);

                // Internal nodes have additional data.
                if(columns.length > 1){
                    node.featureIndex = Integer.parseInt(columns[1]);
                    node.threshold = Double.parseDouble(columns[2]);

                    ArrayList<Integer> idTriple = new ArrayList<Integer>();
                    idTriple.add(id); // This node's id.
                    idTriple.add(Integer.parseInt(columns[3])); // The id of the lessThanEqualChild
                    idTriple.add(Integer.parseInt(columns[4])); // The id of the greaterThanChild
                    ids.add(idTriple);
                }

                nodes.add(node);

            } catch(Exception e){
            }
        }

        // Connect the nodes together.
        for(ArrayList<Integer> idTriple : ids){
            nodes.get(idTriple.get(0)).lessThanEqualChild = 
                nodes.get(idTriple.get(1));

            nodes.get(idTriple.get(0)).greaterThanChild = 
                nodes.get(idTriple.get(2));
        }

        root = nodes.get(0);

        reader.close();
    }

    /**
     * Runs the observation through the decision tree and produces a class
     * label prediction.
     * 
     * @param observation The Observation to classify.
     * @return The predicted class label of `observation`.
     */
    public double classify(Observation observation){
        // Can't continue if there's not tree loaded.
        if(root == null){
            return -1;
        }

        Node curNode = root;
        double labelWithMaxCount = -1;
        int maxCount = 0;

        while(!curNode.isLeafNode()){
            if(observation.numericFeatures.get(curNode.featureIndex) <= curNode.threshold){
                curNode = curNode.lessThanEqualChild;
            } else {
                curNode = curNode.greaterThanChild;
            }
        }

        // curNode is a leaf node.
        for(Double label : curNode.labelDistribution.keySet()){
            if(curNode.labelDistribution.get(label) > maxCount){
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

            trainData = Dataset.parseDataFile(trainingFilename, true);
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

            testData = Dataset.parseDataFile(testingFilename, true);
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