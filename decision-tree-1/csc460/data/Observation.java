// File:   Observation.java
// Author: Hank Feild

package csc460.data;
import java.util.ArrayList;

/**
 * An observation used for training, evaluating, or using a model. An
 * observation consists of some nubmer of numeric features, some number of 
 * string features, and a numeric target (numeric class label or a dependent
 * variable). 
 */
public class Observation {
    public ArrayList<Double> numericFeatures;
    public ArrayList<String> stringFeatures;
    public double target;

    /**
     * Initializes the object.
     * 
     * @param numericFeatures A list of numeric features (can be null).
     * @param stringFeatures A list of string features (can be null).
     * @param target The target (numeric) -- either a numeric class label or
     *               dependent variable.
     */
    public Observation(ArrayList<Double> numericFeatures, 
            ArrayList<String> stringFeatures, double target) {
        this.numericFeatures = numericFeatures;
        this.stringFeatures = stringFeatures;
        this.target = target;
    }

    /**
     * @return The observation as a CSV string, numeric features followed by
     * string features followed by the target.
     */
    public String toString() {
        StringBuffer output = new StringBuffer();

        // Numeric features.
        if(numericFeatures != null){
            for(int i = 0; i < numericFeatures.size(); i++){
                output.append(numericFeatures.get(i));
                if(i < numericFeatures.size()-1)
                    output.append(",");
            }
        }

        // String features.
        if(stringFeatures != null){
            for(int i = 0; i < stringFeatures.size(); i++){
                output.append(stringFeatures.get(i));
                if(i < stringFeatures.size()-1)
                    output.append(",");
            }
        }

        // Target.
        output.append(",").append(target);

        return output.toString();
    }
}
    
