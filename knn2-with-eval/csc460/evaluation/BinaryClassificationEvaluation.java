// File:   BinaryClassificationEvaluation.java
// Author: Structure and support code by Hank Feild;

package csc460.evaluation;

import java.util.List;

/**
 * Provides support for evaluating binary classification predictions. In the
 * event of multi-class classification, all labels that are not the positive
 * class are collectively considered "not the positive class".
 */
public class BinaryClassificationEvaluation {
    protected int truePositives, falsePositives, trueNegatives, falseNegatives;

    /**
     * Initializes the object; this requires the evaluate method to be called
     * before any of the metrics getters.
     */
    public BinaryClassificationEvaluation(){
        truePositives = 0;
        falsePositives = 0;
        trueNegatives = 0;
        falseNegatives = 0;
    }

    /**
     * Initializes the object and evaluates the given labels. Use the metrics
     * getters to get individual metrics.
     * 
     * @param predictedLabels A list of predicted (numeric) labels (e.g. 0, 1, 2).
     * @param truthLabels A parallel list of true label values.
     * @param positiveLabel The label to use as the positive class; all other
     *                      labels are considered collectively to be the 
     *                      non-positive class (even in multi-class situations).
     */
    public BinaryClassificationEvaluation(List<Double> predictedLabels, 
            List<Double> truthLabels, double positiveLabel){

        evaluate(predictedLabels, truthLabels, positiveLabel);
    }

    /**
     * Evaluates the given labels. Use the metrics
     * getters to get individual metrics.
     * 
     * @param predictedLabels A list of predicted (numeric) labels (e.g. 0, 1, 2).
     * @param truthLabels A parallel list of true label values.
     * @param positiveLabel The label to use as the positive class; all other
     *                      labels are considered collectively to be the 
     *                      non-positive class (even in multi-class situations).
     */
    public void evaluate(List<Double> predictedLabels, 
            List<Double> truthLabels, double positiveLabel){

        int predicted, truth;
        truePositives = 0;
        trueNegatives = 0;
        falsePositives = 0;
        falseNegatives = 0;

        for(int i = 0; i < predictedLabels.size(); i++){
            predicted = (int) Math.round(predictedLabels.get(i));
            truth = (int) Math.round(truthLabels.get(i));

            if(predicted == truth){
                if(predicted == positiveLabel){
                    truePositives++;
                } else {
                    trueNegatives++;
                }
            } else {
                if(predicted == positiveLabel){
                    falsePositives++;
                } else {
                    falseNegatives++;
                }
            }
        }
    }

    /**
     * @return The accuracy of the predicted labels.
     */
    public double getAccuracy(){
        return (1.0 * truePositives + trueNegatives)/
            (truePositives + trueNegatives + falsePositives + falseNegatives);
    }

    /**
     * @return The F1 score of the predicted labels.
     */
    public double getF1(){
        return (2.0*truePositives) / 
            (2.0*truePositives + falsePositives + falseNegatives);
    }
}
