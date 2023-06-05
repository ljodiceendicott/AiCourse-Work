package csc460.searchproblems;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;

import csc460.Board;
import csc460.BoardCoordinate;
import csc460.SearchState;

/**
 * Solves a generic CSP. See the loadFile function for more on how
 * it works.
 * 
 * @author Hank Feild (hfeild@endicott.edu)
 */
public class CSP implements SearchProblem {
    private SearchState startState;
    private ArrayList<String> variables;
    private ArrayList<String> domain;
    private ArrayList<ArrayList<String>> domains;
    private ArrayList<ArrayList<String>> sameConstraints;
    private ArrayList<ArrayList<String>> diffConstraints;
    private ArrayList<ArrayList<String>> notAllSameConstraints;
    private HashMap<String, Integer> variableIndexLookup;
    private boolean mrv, lcv, constraintPropagation, forwardChecking;


    /**
     * Represents a CSP state, which is a list of assignments that correspond 
     * to the variable ordering (see the outer CSP class). The value of 
     * unassigned variables is null.
     */
    public class CSPState implements SearchState {
        private ArrayList<String> assignments;
        private ArrayList<ArrayList<String>> domains;

        /**
         * Initializes the state.
         * 
         */
        public CSPState(ArrayList<String> assignments, 
                ArrayList<ArrayList<String>> domains){
            this.assignments = assignments;
            this.domains = domains;
        }

        /**
         * @return The current set of variable assignments.
         */
        public ArrayList<String> getAssignments(){
            return assignments;
        }


        /**
         * @return The current set of variable-specfic domains.
         */
        public ArrayList<ArrayList<String>> getDomains(){
            return domains;
        }

        /**
         * @return A deep copy of this state.
         */
        public CSPState clone(){
            ArrayList<String> clonedAssignments = new ArrayList<String>();
            ArrayList<ArrayList<String>> clonedDomains = new ArrayList<ArrayList<String>>();
            clonedAssignments.addAll(assignments);

            for(ArrayList<String> domain : domains){
                clonedDomains.add(new ArrayList<String>());
                clonedDomains.get(clonedDomains.size()-1).addAll(domain);
            }

            return new CSPState(clonedAssignments, clonedDomains);
        }

        /**
         * Unused.
         */
        @Override
        public BoardCoordinate getAgentCoordinates() {
            return null;
        }

        /**
         * Unused.
         */
        @Override
        public double getCost() {
            return 0;
        }

        /**
         * Unused.
         */
        @Override
        public double getDistance() {
            return 0;
        }

        /**
         * Unused.
         */
        @Override
        public String getAction() {
            return null;
        }

        /**
         * @return True if the two have the same set of assignments.
         */
        public boolean equals(Object other){
            return assignments.equals(((CSPState) other).getAssignments());
        }

        /**
         * @return A number that is distinct to the set of assignments in this
         *         state.
         */
        @Override
        public int hashCode(){
            return assignments.toString().hashCode();
        }
    }

    /**
     * Creates a CSP that uses vanilla backtracking.
     */
    public CSP(){
        this(false, false, false, false);
    }

    /**
     * Creates a CSP that uses backtracking with the specified extensions.
     * 
     * @param fowardChecking Perform forward checking after assignments.
     * @param constraintPropagation Perform constraint propagation after 
     *                              assignments.
     * @param mrv Select variables with the smallest domains first.
     * @param lcv Select the least constraining values first.
     */
    public CSP(boolean forwardChecking, boolean constraintPropagation, 
            boolean mrv, boolean lcv){
        this.forwardChecking = forwardChecking;
        this.constraintPropagation = constraintPropagation;
        this.mrv = mrv;
        this.lcv = lcv;
    }

    /**
     * Unused.
     */
    @Override
    public void setHeuristic(String heuristic) {
    }

    /**
     * @return The starting state (the initial set of assignments).
     */
    @Override
    public SearchState getStartState() {
        return startState;
    }

    /**
     * @param state The state to test.
     * @return True if the agent has reached the exit.
     */
    @Override
    public boolean isGoal(SearchState state) {
        ArrayList<String> assignments = ((CSPState) state).getAssignments();
        return assignments.indexOf(null) < 0 && constraintsSatisfied(assignments);
    }

    /**
     * Checks all of the constraints between *assigned variable*.
     * @param assignments The assignments to check.
     * @return True if no constraints are violated.
     */
    public boolean constraintsSatisfied(ArrayList<String> assignments){
        // Check sameness constraints.
        for(ArrayList<String> constraintSet : sameConstraints){
            String targetValue = null;
            for(String variable : constraintSet){
                String value = assignments.get(variableIndexLookup.get(variable));
                if(value != null){
                    if(targetValue == null){
                        targetValue = value;
                    } else if(!targetValue.equals(value)){
                        return false;
                    }
                }
            }
        }
        // Check difference constraints.
        for(ArrayList<String> constraintSet : diffConstraints){
            String targetValue = null;
            for(String variable : constraintSet){
                String value = assignments.get(variableIndexLookup.get(variable));
                if(value != null){
                    if(targetValue == null){
                        targetValue = value;
                    } else if(targetValue.equals(value)){
                        return false;
                    }
                }
            }
        }

        // Check not-all-same constraints.
        for(ArrayList<String> constraintSet : notAllSameConstraints){
            String targetValue = null;
            boolean atLeastOneDifferent = false;
            boolean hasUnassigned = false;
            for(String variable : constraintSet){
                String value = assignments.get(variableIndexLookup.get(variable));
                if(value != null){
                    if(targetValue == null){
                        targetValue = value;
                    } else if(!targetValue.equals(value)){
                        atLeastOneDifferent = true;
                        break;
                    }
                } else {
                    hasUnassigned = true;
                    break;
                }
            }
            // Unless at least one of the values in the set is different,
            // we found a violation.
            if(!hasUnassigned && !atLeastOneDifferent){
                return false;
            }
        }
        return true;
    }

    /**
     * Implements backtracking; only possible assignment to the next variable
     * are emitted, and those that are must be consistent with previous
     * assignments.
     * 
     * @param state The state to find successors of.
     * @return A collection of successor states.
     */
    @Override
    public Iterable<SearchState> getSuccessors(SearchState state) {
        ArrayList<SearchState> successors = new ArrayList<SearchState>();

        CSPState cspState = (CSPState) state;
        ArrayList<String> assignments = cspState.getAssignments();
        // If there are no missing assignments, there are no successors, so 
        // stop here.
        if(assignments.indexOf(null) < 0){
            return successors;
        }

        int i = getNextVariable(cspState);

        // Add any consistent assignment to that variable to the list of
        // successors.
        for(String value : getOrderedValues(cspState, i)){
            CSPState successorState = cspState.clone();
            successorState.getAssignments().set(i, value);

            // Replace domain with singleton consisting of the assignment.
            successorState.getDomains().get(i).clear();
            successorState.getDomains().get(i).add(value);

            // Only add the successor if all constraints pass and forward
            // checking / constraint propagation (if enabled) are successful.
            if(constraintsSatisfied(successorState.getAssignments())){
                if(constraintPropagation){
                    if(!propogateConstraints(successorState, i)){
                        continue;
                    }
                } else if(forwardChecking){
                    if(!forwardCheck(successorState, i)){
                        continue;
                    }
                }

                successors.add(successorState);
            }
        }
        return successors;
    }

    /**
     * Gets the index of the next variable to assign. The method used (next
     * unassigned variable or MRV) is based on the object settings (see the
     * constructor).
     * 
     * @param state The CSP state to select a variable for.
     * @return The index of the next variable to assign.
     */
    public int getNextVariable(CSPState state){
        if(mrv){
            return minimumRemainingValues(state);
        } else {
            // Get the index of the next empty assignment.
            return state.getAssignments().indexOf(null);
        }
    }

    /**
     * Implement the minimum renmaning values (RMV) algorithm for variable
     * selection.
     * 
     * @param state The CSP state to select a variable for.
     * @return The index of the unassigned varaible with the smallest domain.
     */
    public int minimumRemainingValues(CSPState state){
        int smallestidx = -1;
        int smallestSize = Integer.MAX_VALUE;
        for(int i = 0; i<state.domains.size(); i++){
            if(state.domains.get(i).size() <smallestSize){
                smallestidx = i;
                smallestSize = state.domains.get(i).size();
            }
        }
        return smallestidx;
    }

    /**
     * Gets an ordering over domain values for the given variable. The method
     * used (default vs. LCV) is based on the settings for this CSP instance.
     * 
     * @param state The current CSP state.
     * @param variableIndex The index of the variable whose value is being 
     *                      assigned.
     * @return An ordering over the domain values for this variable.
     */
    public Iterable<String> getOrderedValues(CSPState state, int variableIndex){
        if(lcv){
            return leastConstrainingValue(state, variableIndex);
        } else {
            // Default: values are in the order they're listed in the domain.
            return state.getDomains().get(variableIndex);
        }
    }

    /**
     * Implements the least constraining value (LCV) algorithm for ordering
     * domains values.
     * 
     * @param state
     * @param variableIndex
     * @return
     */
    public Iterable<String> leastConstrainingValue(CSPState state, int variableIndex){
        // TODO 
        String assigned = state.assignments.get(variableIndex);
        ArrayList<String> next = state.domains.get(getNextVariable(state));
        for(int i= 0; i<next.size();i++){
            if(next.get(i)==assigned){
                next.remove(i);
                break;
            }
        }
        return next;
    }

    /**
     * Performs forward checking on the given variable, updating the domains
     * of all unassigned varaibles that share a constraint with the variable of
     * interest. All inconsistent values are removed from those states' domains.
     * Only binary constraints are considered.
     * 
     * @param state The current CSP state.
     * @param variableIndex The index of the variable of interest.
     * @return True if all of the domains were updated successfully; false if
     *         one or more of them was an empty set.
     */
    public boolean forwardCheck(CSPState state, int variableIndex){
        int stateZero = 0;
        for(int i =0; i<state.domains.size(); i++){
            for(int j=0; j<state.domains.get(i).size(); j++){
                if(state.domains.get(i).get(j)== state.assignments.get(variableIndex))
                {   
                    state.domains.get(i).remove(j);
                }
            }
            if(state.domains.get(i).isEmpty()){
                stateZero++;
            }
        }
        return stateZero >=1;
    }

    /**
     * Performs constraint propogation to make the contraints arc consistent.
     * Uses the AC-3 algorithm. Only binary constraints are considered.
     * 
     * @param state The current CSP state.
     * @param variableIndex The index of the variable of interest.
     * @return True if all of the domains were updated successfully; false if
     *         one or more of them was an empty set.
     */
    public boolean propogateConstraints(CSPState state, int variableIndex){
        // String assign = state.assignments.get(variableIndex);
        // for(int i= 0; i< state.domains.size(); i++){
        //     for(int j = 0; j<state.domains.get(i).size(); j++){
        //         if(state.domains.get(i).get(j)==assign){
        //             state.domains.get(i).remove(j);
        //         }
        //         if(state.domains.get(i).isEmpty()){
        //             return false;
        //         }
        //     }
        // }
        String value = state.assignments.get(variableIndex);
       Queue<ArrayList<String>> queue = new LinkedList<>();
       queue.add(state.domains.get(0));
       while(!queue.isEmpty()){
        ArrayList<String> s = queue.poll();
        for(int i = 0; i<s.size();i++){
            if(s.get(i) == value){
                s.remove(i);
            }
        }
        if(s.isEmpty()){
            return false;
        }
       }
        return true;

    }

    /**
     * A wrapper for loadFile; returns null.
     * 
     * @param filename The name of the file to load.
     * @return null.
     * 
     * @throws FileNotFoundException
     */
    @Override 
    public Board loadBoardFile(String filename) throws FileNotFoundException {
        loadFile(filename);
        return null;
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
        ArrayList<String> assignments = new ArrayList<String>();
        variables = new ArrayList<String>();
        domain = new ArrayList<String>();
        domains = new ArrayList<ArrayList<String>>();
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
            assignments.add(value);
            variableIndexLookup.put(variable, variables.size()-1);
        }

        // Parse domain values.
        //self.domain = [x.strip() for x in fd.readline().split(',')]
        for(String domainValue : reader.nextLine().split(" ")){
            domain.add(domainValue.trim());
        }

        // Add in variable-specific domains.
        for(String assignment : assignments){
            // Unassigned variables have the full domain initially.
            if(assignment == null){
                domains.add(new ArrayList<String>(domain));
            // Assigned variables have a domain of one.
            } else {
                domains.add(new ArrayList<String>());
                domains.get(domains.size()-1).add(assignment);
            }
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

        // Initialize the start state.
        startState = new CSPState(assignments, domains);
    }

    /**
     * @return A string representation of variables and their assignments, as
     *         specified by the given search state. The format is:
     *         \t[variable]\t[value]
     */
    public String getAssignmentsAsString(SearchState state){
        ArrayList<String> assignments = ((CSPState) state).getAssignments();
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < variables.size(); i++){
            sb.append('\t').append(variables.get(i))
              .append('\t').append(assignments.get(i))
              .append('\n');
        }

        return sb.toString();
    }


    /**
     * @return Backtracking enhancements as a string.
     */
    public String getOptionsString(){
        StringBuilder sb = new StringBuilder();

        if(constraintPropagation){
            sb.append("\n\t-constraint propagation");
        } else if(forwardChecking){
            sb.append("\n\t-forward checking");
        } 

        if(mrv){
            sb.append("\n\t-mrv");
        }

        if(lcv){
            sb.append("\n\t-lcv");
        }

        if(sb.length() == 0){
            sb.append("\n\t-vanilla backtracking");
        }
        return "Options:"+ sb.toString();
    }
    
}