package csc460.drivers;

import csc460.SearchNode;
import csc460.localsearch.IterativeImprovement;
import csc460.searchalgorithms.*;
import csc460.searchproblems.CSP;

import java.io.FileNotFoundException;

/**
 * A driver for generic constraint satisfaction problems.
 * 
 * @author Hank Feild (hfeild@endicott.edu)
 */
public class CSPDriver {
    public static String outputfile;

    /**
     * Attempts to solve the CSP in the given file using backtracking. The final
     * assignment is displayed to stdout.
     *
     * @param iterativeImprovement The IterativeImprovement CSP  object to sovle.
     * @param maxInitializations The maximimum number of random (re)initiailzations 
     *                          to make to variable assignments.
     * @param maxReassignments The maximum number of variable reassignments
     *                         ("fixes") to make per initialization.
     * @return True if a solution was found.
     */
    public static boolean runIterativeImprovement(
            IterativeImprovement iterativeImprovement, int maxInitializations, 
            int maxReassignments){

        boolean solutionFound = iterativeImprovement.run(maxInitializations, maxReassignments);
        System.out.println("Iterative improvement\n"+
            "   Max initializations = "+ maxInitializations +"\n"+
            "   Max reassignments = "+ maxReassignments);
        
        if(solutionFound){
            System.out.println("Solution found:\n"+
                iterativeImprovement.getAssignmentsAsString());
        } else {
            System.out.println("No solution found :(");
        }

        return solutionFound;
    }

    /**
     * Attempts to solve the CSP in the given file using backtracking. The final
     * assignment is displayed to stdout.
     *
     * @param cspProblem The CSP problem object to sovle.
     * @return True if a solution was found.
     */
    public static boolean runBacktracking(CSP cspProblem) {
        SearchAlgorithm algorithm = new DFS();
        algorithm.init(cspProblem);
        
        System.out.println("Backtracking");
        System.out.println(cspProblem.getOptionsString());
        
        while(true){
            SearchNode searchNode = algorithm.nextNode();

            // See if we've finished searching without finding a solution.
            if(searchNode == null){
                printSearchStats(algorithm);
                System.out.println("No solution found :(");
                return false;
            }

            if(cspProblem.isGoal(searchNode.state)){
                printSearchStats(algorithm);
                System.out.println("Solution found:");
                System.out.println(
                    cspProblem.getAssignmentsAsString(searchNode.state));
                return true;
            }
        }
    }
    /**
     * Displays stats about the search to the terminal.
     * 
     * @param algorithm The algorithm that performed the search.
     */
    public static void printSearchStats(SearchAlgorithm algorithm){
        System.out.println("Stats:");
        System.out.println("\tStates expanded: "+ algorithm.getNumStatesExpanded());
        System.out.println("\tMax fringe size: "+ algorithm.getMaxFringeSize());
    }

    /**
     * Kicks off the solving of the CSP provided by the user.
     * 
     * @param args See "usage" below.
     * @throws FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException {
        CSP cspProblem;
        IterativeImprovement iterativeImprovementCSP;
        boolean mrv = false, 
                lcv = false, 
                forwardChecking = false, 
                constraintPropagation = false,
                backtracking = false,
                iterativeImprovement = false;
        int maxReassignments = 100, maxInitializations = 10;

        String usage = "Usage: CSPDriver <algorithm> [options] <csp file> > <output>\n"+
            "<algorithm> can be one of:\n"+
            "  backtracking -- backtracking; [options] for backtracking:\n"+
            "    Filtering:\n"+
            "      -fc -- forward checking (ignored if -cp option provided)\n"+
            "      -cp -- constraint propagation\n"+
            "    Variable/domain ordering:\n"+
            "      -mrv -- minimum remaining values\n"+
            "      -lcv -- least constraining value\n"+
            "  iterative-improvement -- iterative improvement; [options] for iterative improvement:\n"+
            "    -max-initializations=X -- sets the maximum number of randome (re)initializations of assignments\n"+
            "    -max-reassignments=X -- sets the maximum of variable reassignments per initialization\n\n"
            ;

        if (args.length < 1) {
            System.err.print(usage);
            return;
        }

        // Parse the algorithm.
        if(args[0].equals("backtracking")){
            backtracking = true;
        } else if(args[0].equals("iterative-improvement")){
            iterativeImprovement = true;
        }
            else
            {
            System.err.println("Error: invalid algorithm specified: "+ args[0]);
            System.err.print(usage);
            return;
        }

        // Parse the command line argumnets.
        for(int i = 1; i < args.length-1; i++){
            if(args[i].equals("-mrv")){
                mrv = true;
            } else if(args[i].equals("-lcv")){
                lcv = true;
            } else if(args[i].equals("-fc")){
                forwardChecking = true;
            } else if(args[i].equals("-cp")){
                constraintPropagation = true;
            } else if(args[i].startsWith("-max-initializations=")){
                maxInitializations = Integer.parseInt(args[i].replaceFirst("-max-initializations=", ""));
            } else if(args[i].startsWith("-max-reassignments=")){
                maxReassignments = Integer.parseInt(args[i].replaceFirst("-max-reassignments=", ""));
            } 
            else {
                System.err.println("Error: unrecognized option: "+ args[i]);
                System.err.print(usage);
                return;
            }
        }

        if(!backtracking && !iterativeImprovement){
            System.err.println("Error: must include one of '-backtracking'"+
                "or '-iterative-improvement'.\n\n"+ usage);
            return;
        }

        if(backtracking){
            // Initialize the CSP problem.
            cspProblem = new CSP(forwardChecking, constraintPropagation, mrv, lcv);
            cspProblem.loadFile(args[args.length-1]);

            // Solve.
            CSPDriver.runBacktracking(cspProblem);
        } else if(iterativeImprovement){
            iterativeImprovementCSP = new IterativeImprovement();
            iterativeImprovementCSP.loadFile(args[args.length-1]);
            CSPDriver.runIterativeImprovement(iterativeImprovementCSP, maxInitializations, maxReassignments);
        }
    }

}