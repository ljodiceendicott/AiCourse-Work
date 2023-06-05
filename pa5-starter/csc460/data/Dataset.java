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
     * Generate a list of observations from the data file.
     * 
     * @param filename The name of the file to parse. Should have a header and be in
     *                 comma separated value (CSV) format.
     * @param hasTarget If true, the last column will be used as the target for each
     *                 observation and all other columns will be features. If false,
     *                 *all* columns will be used as features.
     * @return The observations and header (column names).
     * @throws IOException
     */
    public static Dataset parseDataFile(String filename, boolean hasTarget) throws IOException {
        ArrayList<String> columnNames = new ArrayList<String>();
        ArrayList<Observation> observations = new ArrayList<Observation>();
        boolean[] columnIsNumeric = findNumericColumns(filename);
        BufferedReader reader = new BufferedReader(new FileReader(filename));

        for(String col : reader.readLine().split(",")){
            columnNames.add(col);
        }

        while(reader.ready()){
            String[] columns = reader.readLine().split(",");
            ArrayList<Double> numericFeatures = new ArrayList<Double>();
            ArrayList<String> stringFeatures = new ArrayList<String>();
            double target = -1;
            if(hasTarget){
                for(int i = 0; i < columns.length-1; i++){
                    if(columnIsNumeric[i]){
                        numericFeatures.add(Double.parseDouble(columns[i]));
                    } else {
                        stringFeatures.add(columns[i]);
                    }
                }
                target = Double.parseDouble(columns[columns.length-1]);
            } else {
                for(int i = 0; i < columns.length; i++){
                    if(columnIsNumeric[i]){
                        numericFeatures.add(Double.parseDouble(columns[i]));
                    } else {
                        stringFeatures.add(columns[i]);
                    }
                }
            }
            
            observations.add(new Observation(numericFeatures, stringFeatures, target));
        }
        reader.close();

        return new Dataset(columnNames, observations);
    }

    /**
     * Makes a pass across an entire CSV file in order to acertain which columns
     * are numeric and which are not.
     * 
     * @param filename The name of the CSV file to process.
     * @return An array indicate which columns are numeric (true) or not (false).
     * @throws IOException
     */
    public static boolean[] findNumericColumns(String filename) throws IOException {
        
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        
        // Find out how many columns there are based on the header.
        int cols = reader.readLine().split(",").length;
        boolean[] columnIsNumeric = new boolean[cols];

        // Assume all columns are numeric unless we cannot parse one of the values.
        for(int i = 0; i < cols; i++){
            columnIsNumeric[i] = true;
        }

        while(reader.ready()){
            String[] columns = reader.readLine().split(",");
            for(int i = 0; i < columns.length; i++){
                try{
                    Double.parseDouble(columns[i]);
                } catch(NumberFormatException e){
                    columnIsNumeric[i] = false;
                }
            }
        }

        reader.close();

        return columnIsNumeric;
    }
}