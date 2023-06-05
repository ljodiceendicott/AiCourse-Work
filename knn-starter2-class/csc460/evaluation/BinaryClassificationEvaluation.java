// File:   BinaryClassificationEvaluation.java
// Author: Structure and support code by Hank Feild;
// TODOs completed by: 

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
        int tp=0;
        int fp=0;
        int tn=0;
        int fn=0;

        for(int i = 0; i<truthLabels.size(); i++){
            if(truthLabels.get(i)==predictedLabels.get(i)){
                if(truthLabels.get(i) == positiveLabel){
                    tp++;
                }
                else{
                    tn++;
                }
            }
            else{
                if(truthLabels.get(i)==positiveLabel){
                    fn++;
                }
                else{
                    fp++;
                }
            }
        }
        // TODO: Compute TP, FP, TN, FN.
        truePositives = tp;
        falsePositives = fp;
        trueNegatives = tn;
        falseNegatives = fn;
    }

    /**
     * @return The accuracy of the predicted labels.
     */
    public double getAccuracy(){
        // TODO: return the accuracy using the confusion matrix variables.
        return (truePositives+trueNegatives)/(truePositives+trueNegatives+falsePositives+falseNegatives);
    }

    /**
     * @return The F1 score of the predicted labels.
     */
    public double getF1(){
        // TODO: return the F1 score using the confusion matrix variables.
        return truePositives/truePositives+falseNegatives;
    }
}
