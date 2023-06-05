// File:   RegressionEvaluation.java
// Author: Structure and support code by Hank Feild;
// TODOs completed by: 

package csc460.evaluation;

import java.util.List;

/**
 * Provides support for evaluating regression predictions.
 */
public class RegressionEvaluation {
    protected List<Double> predictedValues, truthValues;

    /**
     * Sets the prdicted and truth values.
     *  
     * @param predictedValues A list of predicted values (real numbers).
     * @param truthValues A parallel list of true values (real numbers).
     */
    public RegressionEvaluation(List<Double> predictedValues, 
            List<Double> truthValues){
        this.predictedValues = predictedValues;
        this.truthValues = truthValues;
    }

    /**
     * @return The root mean squared error (RMSE) of the given predictions. 
     */
    public  double getRMSE(){
        double sum =0;

        for(int i = 0; i < predictedValues.size(); i++){
            sum = sum + ((predictedValues.get(i)+truthValues.get(i))*(predictedValues.get(i)+truthValues.get(i)));
        }
        // TODO: Compute RMSE using predictedValues and truthValues.

        return Math.sqrt(sum/predictedValues.size());
    }

}
