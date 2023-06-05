package csc460.localsearch;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class IterativeImprovement {
    private ArrayList<String> variables;
    private ArrayList<String> initialAssignments;
    private ArrayList<String> assignments;
    private ArrayList<String> domain;
    private ArrayList<ArrayList<String>> sameConstraints;
    private ArrayList<ArrayList<String>> diffConstraints;
    private ArrayList<ArrayList<String>> notAllSameConstraints;
    private HashMap<String, Integer> variableIndexLookup;
    private int numInitializations, numReassignments;
    private boolean solutionFound;

    /**
     * Runst the iterative improvement algorithm. Tracks performance in the
     * `numInitializations` and `numReassignments` data members.
     *
     * @param maxInitializations The maximim number of (re)initializations of
     *                           variable assignments.
     * @param maxReassignments The maximum number of reassignments per 
     *                         initialization.
     * @return True if a solution was found.
     */
    public boolean run(int maxInitializations, int maxReassignments){
        solutionFound = false;
        numInitializations = 0;
        numReassignments = 0;


        while(numInitializations < maxInitializations && !solutionFound){
            makeRandomAssignments();
            numInitializations++;
            solutionFound |= fixViolations(maxReassignments);
            //this.countViolations();
        }
        System.out.println("Number of Reassignments: "+numReassignments);

        return solutionFound;
    }

    /**
     * Randomly assigns values from the domain to variables. If a variable
     * has a value in `initialAssignments`, that value is used.
     */
    public void makeRandomAssignments(){
        for(int i = 0; i<assignments.size(); i++){
            assignments.remove(i);
          if(initialAssignments.get(i)==null){
            assignments.add(i,domain.get(i%domain.size()));
          }
          else{
            assignments.add(i, initialAssignments.get(i));
          }  
        }
        System.out.println(assignments);
        // TODO
    }

    /**
     * Repeatedly finds a variable in `assignments` involved in a violation to
     * reassign, and then assigns it a value that violates the fewest number of
     * constraints (via trial and error). Variables assigned an initial value in
     * the CSP (i.e., in `initialAssignments`) are never reassigned. This
     * process stops when there are no more violations remaining or the maximum
     * number of assignments has been reached.
     *
     * This increments the `numReassignments` data member once for every
     * variable whose value is modified.
     *
     * @param maxReassignments The maximum number of reassignments to make.
     * @return True if no volations remain; false otherwise.
     */
    public boolean fixViolations(int maxReassignments){
        int violations = this.countViolations();
    for(int i = maxReassignments; i>0; i--){    
        if(violations ==0){
            return true;
        }
        //assignment is being tried out if it is right then countViolations() should decrease the amount
        else{
            //checking all of the values that would be able to work
            //the one that would work violates the least amount of constraints
           for(int j=0; j<assignments.size(); j++){
                String leastvalue = assignments.get(i);
                int leastViolations = this.countViolations();
                if(assignments.get(i)==initialAssignments.get(i)){
                    break;
                }
                else if(leastViolations ==0){
                    break;
                }
                else{
                    //the number is not in the inital assignments
                    //search the using the constraints to see if it is there
                    for(int k = 0; k < sameConstraints.size(); k++){
                        int idxa = variableIndexLookup.get(sameConstraints.get(k).get(0));
                        int idxb = variableIndexLookup.get(sameConstraints.get(k).get(1));
                        if(idxa != j && idxb!=j){
                            break;
                        }   
                        if(assignments.get(idxa)!=assignments.get(idxb)){
                            int leastidx = 0;
                            for(int n = 0; n<domain.size();n++){
                                assignments.remove(j);
                                assignments.add(j, domain.get(k+j%domain.size()));
                                int newviolation = this.countViolations();
                                if(newviolation<leastViolations){
                                    leastViolations = newviolation;
                                    leastvalue = assignments.get(j);
                                    leastidx = j;
                                }
                                //check all values going n+i%domain
                            }
                            assignments.remove(leastidx);
                            assignments.add(leastidx,leastvalue);
                            numReassignments++;
                           }
                    }
                    for(int k = 0; k < diffConstraints.size(); k++){
                     int idxa = variableIndexLookup.get(diffConstraints.get(k).get(0));
                     int idxb = variableIndexLookup.get(diffConstraints.get(k).get(1));
                        if(idxa != j && idxb!=j){
                            break;
                        }   
                        if(assignments.get(idxa)==assignments.get(idxb)){
                            int leastidx = 0;
                            for(int n = 0; n<domain.size();n++){
                                assignments.remove(j);
                                assignments.add(j, domain.get(k+j%domain.size()));
                                int newviolation = this.countViolations();
                                if(newviolation<leastViolations){
                                    leastViolations = newviolation;
                                    leastvalue = assignments.get(j);
                                    leastidx = j;
                                }
                                //check all values going n+i%domain
                            }
                            assignments.remove(leastidx);
                            assignments.add(leastidx,leastvalue);
                            numReassignments++;
                        }
                    }
                    for(int k =0; k < notAllSameConstraints.size(); k++){
                        int idxa = variableIndexLookup.get(diffConstraints.get(k).get(0));
                        int idxb = variableIndexLookup.get(diffConstraints.get(k).get(1));
                        int idxc = variableIndexLookup.get(diffConstraints.get(k).get(2));
                        //if all three of these are equal it is a violation
                        if(idxa != j && idxb!=j){
                            break;
                        }   
                        if((assignments.get(idxa)==assignments.get(idxb))&& assignments.get(idxa)==assignments.get(idxc)){
                            int leastidx = 0;
                            for(int n = 0; n<domain.size();n++){
                                assignments.remove(j);
                                assignments.add(j, domain.get(k+j%domain.size()));
                                int newviolation = this.countViolations();
                                if(newviolation<leastViolations){
                                    leastViolations = newviolation;
                                    leastvalue = assignments.get(j);
                                    leastidx = j;
                                }
                                //check all values going n+i%domain
                            }
                            assignments.remove(leastidx);
                            assignments.add(leastidx,leastvalue);
                            numReassignments++;
                        }
                    }
                }
                //Loop through x times as there are x possibilities in the domain
                
            //   System.out.println("assignment-"+assignments.get(j));
           }
        }
    }
    if(violations>0){
        return false;
    }
    else
    {
        return true;
    }
    }
   

    /**
     * @return The number of constraint violations in `assignments`.
     */
    public int countViolations(){
        int violations = 0;
    for(int i = 0; i < sameConstraints.size(); i++){
        int idxa = variableIndexLookup.get(sameConstraints.get(i).get(0));
        int idxb = variableIndexLookup.get(sameConstraints.get(i).get(1));
           if(assignments.get(idxa)!=assignments.get(idxb)){
               violations++;
           }
    }
    for(int i = 0; i < diffConstraints.size(); i++){
     int idxa = variableIndexLookup.get(diffConstraints.get(i).get(0));
     int idxb = variableIndexLookup.get(diffConstraints.get(i).get(1));
        if(assignments.get(idxa)==assignments.get(idxb)){
            violations++;
        }
    }
    for(int i =0; i < notAllSameConstraints.size(); i++){
        int idxa = variableIndexLookup.get(diffConstraints.get(i).get(0));
        int idxb = variableIndexLookup.get(diffConstraints.get(i).get(1));
        int idxc = variableIndexLookup.get(diffConstraints.get(i).get(2));
        //if all three of these are equal it is a violation
        if((assignments.get(idxa)==assignments.get(idxb))&& assignments.get(idxa)==assignments.get(idxc)){
            violations++;
        }
    }
        // TODO
        System.out.println("num Violations:"+violations);
        return violations;
    }


   

    /**
     * Parses a CSP file. It should have three lines:
     *     * a space separated list of variable names and their assignments (if any)
     *         - assignments should be in the form of: x=value, where x is the
     *           variable name
     *     * a sapce separated list of domain values (treated as strings)
     *     * constraints, which are of the format: <name> <sets>
     *          - sets are space seperated from each other, each each set
     *            should consist of two or more comma seperated variables
     *          - supported constraings (values for <name>):
     *              * same -- all variables within each set have the same 
     *                        value
     *              * diff -- all variables within each set have different 
     *                        values from each other
     *              * not-all-same -- at least one of the variables in each 
     *                        set has a different value from other variables
     *                        in the set
     * 
     * For example:
     * 
     *     VT ME=blue NH MA RI CT
     *     blue red green
     *     diff ME,NH VT,NH MA,VT MA,NH CT,RI CT,MA RI,CT
     * 
     * This would give us a CSP with five variables, one of which already has
     * an assignment (ME). There are three values in the domain: blue, red, and
     * green. NH should have a different value from ME, MA, and VT. MA should 
     * also have a different value from VT, CT, and RI. CT and RI should have
     * different values. There are not "same value" constraints.
     * 
     * @param filename The name of the CSP file to load.
     */
    public void loadFile(String filename) throws FileNotFoundException {
        Scanner reader = new Scanner(new File(filename));
        variables = new ArrayList<String>();
        initialAssignments = new ArrayList<String>();
        assignments = new ArrayList<String>();
        domain = new ArrayList<String>();
        sameConstraints = new ArrayList<ArrayList<String>>();
        diffConstraints = new ArrayList<ArrayList<String>>();
        notAllSameConstraints = new ArrayList<ArrayList<String>>();
        variableIndexLookup = new HashMap<String, Integer>();

        String line;

        // Parse variables and their initial assignments.
        for(String variable : reader.nextLine().split(" ")){
            variable = variable.trim();
            String value = null;
            int equalsIndex = variable.indexOf("=");
            if(equalsIndex >= 0){
                if(equalsIndex == variable.length()-1){
                    value = "";
                } else {
                    value = variable.substring(equalsIndex+1);
                }
                variable = variable.substring(0, equalsIndex);
            }
            variables.add(variable);
            initialAssignments.add(value);
            assignments.add(null);
            variableIndexLookup.put(variable, variables.size()-1);
        }

        // Parse domain values.
        //self.domain = [x.strip() for x in fd.readline().split(',')]
        for(String domainValue : reader.nextLine().split(" ")){
            domain.add(domainValue.trim());
        }

        // Parse constraints.
        while(reader.hasNextLine()){
            line = reader.nextLine().trim();
            
            // Parse "diff" constraints.
            if(line.startsWith("diff")){
                line = line.replaceFirst("diff ", "");

                for(String constraintSet : line.split(" ")){
                    constraintSet = constraintSet.trim();
                    if(constraintSet.length() > 0){
                        ArrayList<String> vars = new ArrayList<String>();
                        for(String var : constraintSet.split(",")){
                            vars.add(var.trim());
                        }
                        diffConstraints.add(vars);
                    }
                }

            // Parse "same" constraints.
            } else if(line.startsWith("same")){
                line = line.replaceFirst("same ", "");

                for(String constraintSet : line.split(" ")){
                    constraintSet = constraintSet.trim();
                    if(constraintSet.length() > 0){
                        ArrayList<String> vars = new ArrayList<String>();
                        for(String var : constraintSet.split(",")){
                            vars.add(var.trim());
                        }
                        sameConstraints.add(vars);
                    }
                }

            // Parse "not-all-same" constraints.
            } else if(line.startsWith("not-all-same")){
                line = line.replaceFirst("not-all-same ", "");

                for(String constraintSet : line.split(" ")){
                    constraintSet = constraintSet.trim();
                    if(constraintSet.length() > 0){
                        ArrayList<String> vars = new ArrayList<String>();
                        for(String var : constraintSet.split(",")){
                            vars.add(var.trim());
                        }
                        notAllSameConstraints.add(vars);
                    }
                }
            }
        }

        reader.close();
    }
   
    /**
     * @return A string representation of variables and their assignments, as
     *         specified by the given search state. The format is:
     *         \t[variable]\t[value]
     */
    public String getAssignmentsAsString(){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < variables.size(); i++){
            sb.append('\t').append(variables.get(i))
              .append('\t').append(assignments.get(i))
              .append('\n');
        }

        return sb.toString();
    }
}
