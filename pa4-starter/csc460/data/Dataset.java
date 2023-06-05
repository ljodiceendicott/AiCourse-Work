// File:   Dataset.java
// Author: Hank Feild

package csc460.data;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Represents a dataset, which consists of a list of column names and a list
 * of observations (the rows of the data set). Also includes operations for
 * reading and printing datasets.
 */
public class Dataset {
    public ArrayList<String> columnNames;
    public ArrayList<Observation> observations;

    /**
     * Initializes the dataset.
     * 
     * @param columnNames The label for each of the columns.
     * @param observations The data in the rows of the dataset.
     */
    public Dataset(ArrayList<String> columnNames, ArrayList<Observation> observations) {
        this.columnNames = columnNames;
        this.observations = observations;
    }

    /**
     * @return A CSV formatted string of the column names for this dataset.
     */
    public String columnNamesAsCSV() {
        StringBuffer output = new StringBuffer();

        for(int i = 0; i < columnNames.size(); i++){
            output.append(columnNames.get(i));
            if(i < columnNames.size()-1)
                output.append(",");
        }
            
        return output.toString();
    }

    /**
     * Generate a list of observations from the data file. All data (aside from
     * the column headers) must be numeric.
     * 
     * @param filename The name of the file to parse. Should have a header and be in
     *                 comma separated value (CSV) format.
     * @param hasTarget If true, the last column will be used as the target for each
     *                 observation and all other columns will be features. If false,
     *                 *all* columns will be used as features.
     * @return The observations and header (column names).
     * @throws IOException
     */
    public static Dataset parseNumericDataFile(String filename, boolean hasTarget) throws IOException {
        ArrayList<String> columnNames = new ArrayList<String>();
        ArrayList<Observation> observations = new ArrayList<Observation>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));

        for(String col : reader.readLine().split(",")){
            columnNames.add(col);
        }

        while(reader.ready()){
            String[] columns = reader.readLine().split(",");
            ArrayList<Double> features = new ArrayList<Double>();
            double target = -1;
            if(hasTarget){
                for(int i = 0; i < columns.length-1; i++){
                    features.add(Double.parseDouble(columns[i]));
                }
                target = Double.parseDouble(columns[columns.length-1]);
            } else {
                for(int i = 0; i < columns.length; i++){
                    features.add(Double.parseDouble(columns[i]));
                }
            }
            
            observations.add(new Observation(features, null, target));
        }
        reader.close();

        return new Dataset(columnNames, observations);
    }

    public static ArrayList<Observation> scaleDataset(ArrayList<Observation> observations){
        ArrayList<Observation> obs = new ArrayList<>();

        //find the min and max
        for(int i=0; i<observations.size(); i++){
            double maxVal = Double.MIN_VALUE;
            double minVal = Double.MAX_VALUE;
            Observation observ = observations.get(i);
            for(int j = 0; j<observ.numericFeatures.size();j++){
                double value = observ.numericFeatures.get(j);
                if(value>maxVal){
                    maxVal = value;
                }
                if(value<minVal){
                    minVal = value;
                }
            }
            //with the min/max found, scale 
            for(int j = 0; j<observ.numericFeatures.size();j++){
            }
        }

        return obs;
    }
}