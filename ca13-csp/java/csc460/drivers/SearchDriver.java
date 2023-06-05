package csc460.drivers;

import csc460.Drawer;
import csc460.Board;
import csc460.BoardCoordinate;
import csc460.SearchNode;
import csc460.searchproblems.*;
import csc460.searchalgorithms.*;

import java.awt.Color;
import java.awt.EventQueue;
import java.io.FileNotFoundException;

import javax.swing.JFrame;

/**
 * A driver for generic search problems.
 * 
 * @author Hank Feild (hfeild@endicott.edu)
 */
public class SearchDriver implements Driver {
    public static final int SPOT_SIZE = 60;
    public static final int MARGIN_SIZE = 5;
    public static final int FPS = 3;
    private final Color SELECTED_PLAN_COLOR = Color.ORANGE;
    private Board board;
    private SearchProblem problem;
    private SearchAlgorithm algorithm;
    private boolean solutionFound;
    private final int ALPHA_STEP = 2;
    private final int ALPHA_START = 15;
    private int alpha;
    private Color searchColor;
    private String boardFile;

    /**
     * Initializes the driver. This calls the init() method of the given 
     * algorithm, so that doesn't need to be done before hand. It also takes 
     * care of loading the starting board.
     * 
     * @param problem The search problem to solve.
     * @param algorithm The search algorithm to use to find a solution to the
     *                  problem.
     * @param boardFile The filename of the starting state. This is given to the
     *                  search problem's loadBoardFile() method.
     */
    public SearchDriver(SearchProblem problem, SearchAlgorithm algorithm,
            String boardFile) throws FileNotFoundException {

        solutionFound = false;
        this.problem = problem;
        this.algorithm = algorithm;
        alpha = ALPHA_START;
        this.boardFile = boardFile;
        loadBoardFile();
        algorithm.init(problem);
    }

    /**
     * Gets the next search node to expand from the search algorithm and updates
     * the board. When a solution is found, it highlights the solution. Stats
     * about the solution and search algorithm are displayed to stdout.
     *
     * @return True if there's more work to be done. False if the solution was
     *         previously found or an error occured (e.g., there are no more
     *         states to expand). Essentially indicate if there are board
     *         changes that require repainting the screen.
     */
    public boolean step(){
        if(solutionFound){
            return false;
        }

        // Get the next state to explore.
        SearchNode searchNode = algorithm.nextNode();

        // This should happen if tere is no solution (the entire state space 
        // has been explored).
        if(searchNode == null){
            return false;
        }

        // We reached a goal state.
        if(problem.isGoal(searchNode.state)){
            for(BoardCoordinate coord : searchNode.pathCoords){
                if(!coord.equals(problem.getStartState().getAgentCoordinates())){
                    board.setColor(coord, SELECTED_PLAN_COLOR);
                }
            }
            solutionFound = true;

            // TODO 2
            // Print stats about the solution:
            //   Search algorithm: ...
            //   States expanded: ...
            //   Max fringe size: ...
            //   Solution cost: ...
            //   Solution path length: ...
            //   Solution path (actions):
            //     ...
            //     ...
            // SOLUTION
            System.out.println(
                "\nSearch algorithm: "+ algorithm.getClass().getCanonicalName() +
                "\nStates expanded: "+ algorithm.getNumStatesExpanded() +
                "\nMax fringe size: "+ algorithm.getMaxFringeSize() +
                "\nSolution cost: "+ searchNode.cost +
                "\nSolution path length: "+ searchNode.pathActions.size() +
                "\nSolution path (actions):");
            for(String action : searchNode.pathActions){
                System.out.println("\t"+ action);
            }
            // END SOLUTION

        // Just another move; paint the spot on the board with the next shade
        // of blue.
        } else if(!searchNode.state.getAgentCoordinates().equals(
                problem.getStartState().getAgentCoordinates())){
            searchColor = new Color(0, 0, 255, alpha);
            board.setColor(searchNode.state.getAgentCoordinates(), searchColor);
            alpha = Math.min(alpha+ALPHA_STEP, 255);
        }

        return true;
    }

    /**
     * Loads the initial board (i.e., the starting state). The parsing is 
     * offloaded to the search problem's loadboardFile mathod.
     */
    public void loadBoardFile() throws FileNotFoundException {
        board = problem.loadBoardFile(boardFile);
    }

    /**
     * @return The current board colors.
     */
    public Board getBoard(){
        return board;
    }

    /**
     * Kicks of the processing of a search problem using a user-specified 
     * search algorithm and starting state.
     * 
     * @param args See "usage" below.
     */
    public static void main(String[] args){
        String heuristic = "";
        String boardFile = null;
        SearchAlgorithm searchAlgorithm = null;
        SearchProblem searchProblem = null;
        SearchDriver searchDriver = null;
        int spotSize = SearchDriver.SPOT_SIZE;
        int marginSize = SearchDriver.MARGIN_SIZE;
        int fps = SearchDriver.FPS;
        String title = "";

        String usage = "Usage: SearchDriver [options]\n\n"+
            "REQUIRED Arguments:\n"+
            "   -p=P -- P is the search problem; current options:\n"+
            "             * maze (find path from entrance to exit)\n"+
            "   -a=A -- A is the search algorithm:\n"+
            "             * bfs -- Breadth First Search\n"+
            "             * dfs -- Depth First Search\n"+
            "             * id  -- Iterative Deepening\n"+
            "             * ucs -- Uniform Cost Search\n"+
            "   -f=F -- F is the filename of the board to read in; settings:\n"+
            "           maze:\n"+
            "             s -- the agent's starting position\n"+
            "             e -- the exit from the maze (only one)\n"+
            "             w -- a wall (agent cannot enter)\n"+
            "             [space] -- an open spot where the agent is allowed to enter\n"+
            "OPTIONAL arguments\n"+
            "   -h=H -- H is the distance heuristic:\n"+
            "           for p=maze:\n"+
            "             [blank] -- no heuristic\n"+
            "             manhattan -- Manhattan distance from a state to the exit\n"+
            "             euclidean -- Euclidean distance from a state to the exit\n"+
            "   -fps=FPS -- FPS is the frames per second; default is 3\n"+
            "   -spotSize=S -- S is the size of squares; default is 60\n"+
            "   -marginSize=S -- S is he size of the gap between spots;\n"+ 
            "             default is 5\n"+
            "\n\n";

        if(args.length < 3){
            System.err.print(usage);
            return;
        }

        // Parse options.
        for(String arg : args){
            // Search problem (-p=).
            if(arg.startsWith("-p=")){
                String problemCode = arg.substring(3);
                title += problemCode +"|";

                // Maze.
                if(problemCode.equals("maze")){
                    searchProblem = new Maze();

                // Invalid option.
                } else {
                    System.err.print("Unrecognized search problem: "+ 
                        problemCode +"\n\n"+ usage);
                    return;
                }

            // Search algorithm (-a=).
            } else if(arg.startsWith("-a=")) {
                String algorithmCode = arg.substring(3);
                title += algorithmCode +"|";

                // Breadth first search
                if(algorithmCode.equals("bfs")){
                    searchAlgorithm = new BFS();

                // Depth first search
                } else if(algorithmCode.equals("dfs")){
                    searchAlgorithm = new DFS();

                // Iterative deepening
                } else if(algorithmCode.equals("id")){
                    searchAlgorithm = new IterativeDeepening();

                // Uniform cost search
                } else if(algorithmCode.equals("ucs")){
                    searchAlgorithm = new UCS();

                // Greedy search
                } else if(algorithmCode.equals("greedy")){
                    searchAlgorithm = new Greedy();
            
                // A* search
                } else if(algorithmCode.equals("astar")){
                    searchAlgorithm = new AStar();
            

                    // Invalid option.
                } else {
                    System.err.print("Unrecognized search algorithm: "+ 
                        algorithmCode +"\n\n"+ usage);
                    return;
                }

            // Board file (-f=).
            } else if(arg.startsWith("-f=")){
                boardFile = arg.substring(3);
                title += boardFile +"|";

            // Heuristic (-h=).
            } else if(arg.startsWith("-h=")){
                heuristic = arg.substring(3);
                title += heuristic +"|";

            // Frames per second (-fps=).
            } else if(arg.startsWith("-fps=")){
                fps = Integer.parseInt(arg.substring(5));

            // Spot size (-spotSize=).
            } else if(arg.startsWith("-spotSize=")){
                spotSize = Integer.parseInt(arg.substring(10));
            
            // Margin size (-marginSize=).
            } else if(arg.startsWith("-marginSize=")){
                spotSize = Integer.parseInt(arg.substring(12));


            // Invalid option.
            } else {
                System.err.print("Unrecognized option: "+ arg +"\n\n"+ usage);
                return;
            }
        }

        // Make sure we have each of the required components.
        if(searchProblem == null || searchAlgorithm == null || boardFile == null){
                System.err.print("The following arguments are missing: ");
                if(searchProblem == null)
                    System.err.print("-p ");
                if(searchAlgorithm == null)
                    System.err.print("-a ");
                if(boardFile == null)
                    System.err.print("-f ");
                System.err.print("\n\n"+ usage);
                return;
        }

        searchProblem.setHeuristic(heuristic);

        try{
            searchDriver = new SearchDriver(
                searchProblem, searchAlgorithm, boardFile);

            final JFrame ex = new Drawer(searchDriver, title, spotSize, 
                marginSize, fps);

            EventQueue.invokeLater(() -> {
                ex.setVisible(true);
            });
        } catch(FileNotFoundException ex){
            System.err.println("Couldn't open board file '"+ boardFile +"': "+ 
                ex.getLocalizedMessage());
        }
    }
}