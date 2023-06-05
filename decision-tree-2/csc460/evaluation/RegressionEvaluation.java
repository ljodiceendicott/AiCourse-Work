// File:   RegressionEvaluation.java
// Author: Structure and support code by Hank Feild;

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

        double squaredSum = 0;
        for(int i = 0; i < predictedValues.size(); i++){
            squaredSum += Math.pow(predictedValues.get(i) - truthValues.get(i), 2);
        }

        return Math.sqrt(squaredSum/predictedValues.size());
    }

}
