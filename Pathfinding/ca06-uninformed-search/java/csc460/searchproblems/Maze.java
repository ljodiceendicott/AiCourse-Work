package csc460.searchproblems;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import csc460.Board;
import csc460.BoardCoordinate;
import csc460.SearchState;
import java.awt.Color;

/**
 * Solves a maze, where the agen starts at a given spot and has to find a path 
 * to the exit. See the loadBoardFile method for the board representation.
 * 
 * @author Hank Feild (hfeild@endicott.edu)
 */
public class Maze implements SearchProblem {
    private SearchState startState;
    private BoardCoordinate exit;
    private BoardCoordinate start;
    // Used for determining next moves.
    private ArrayList<ArrayList<Character>> internalBoard;
    private HashMap<Character,Color> colorMap;
    private String heuristic;

    /**
     * Represents a maze state, where we only need to know the location of
     * the agent as well as details about that location (what it costs,
     * estimated distance to the exit, how the agent moved into the spot).
     */
    public class MazeState implements SearchState {
        private BoardCoordinate agentCoord;
        private String action;
        private double cost, distance;

        /**
         * Initializes the maze state.
         * 
         * @param agentCoord Agent's location on the board. 
         * @param action The action that led to this state.
         * @param cost The cost of moving to this state.
         * @param distance The estimated distance to the exit.
         */
        public MazeState(BoardCoordinate agentCoord, String action, double cost,
                double distance){
            this.agentCoord = agentCoord;
            this.action = action;
            this.cost = cost;
            this.distance = distance;
        }

        @Override
        public BoardCoordinate getAgentCoordinates() {
            return agentCoord;
        }

        @Override
        public double getCost() {
            return cost;
        }

        @Override
        public double getDistance() {
            return distance;
        }

        @Override
        public String getAction() {
            return action;
        }

        public boolean equals(Object other){
            return agentCoord.equals(((MazeState) other).getAgentCoordinates());
        }

        @Override
        public int hashCode(){
            return agentCoord.toString().hashCode();
        }

    }

    /**
     * Creates a map of character codes (from the board file) to colors.
     */
    public Maze() {
        colorMap = new HashMap<Character,Color>();
        colorMap.put(' ', Color.WHITE);
        colorMap.put('s', Color.GREEN);
        colorMap.put('e', Color.RED);
        colorMap.put('w', Color.BLACK);
    }

    /**
     * Sets the heuristic to use to estimate the distance to a goal state. 
     * The following are currently supported:
     * 
     *   - manhattan
     *   - euclidean
     */
    @Override
    public void setHeuristic(String heuristic) {
        this.heuristic = heuristic;
    }

    /**
     * @return The starting state.
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
        return state.getAgentCoordinates().equals(exit);
    }

    /**
     * @param coord A spot on the board.
     * @return An estimate of the distance from the given coordinate to the 
     *         exit.
     */
    public double getDistance(BoardCoordinate coord){
        if(heuristic.equals("manhattan")){
            return Math.abs(coord.x - exit.x) + Math.abs(coord.y - exit.y);
        } else if(heuristic.equals("euclidean")){
            return Math.sqrt(Math.pow(coord.x-exit.x, 2) + 
                             Math.pow(coord.y-exit.y, 2));
        } else {
            return 0;
        }
    }

    /**
     * @param state The state to find successors of.
     * @return A collection of the states to the left, up, right, and down of 
     *         the given state.
     */
    @Override
    public Iterable<SearchState> getSuccessors(SearchState state) {
        ArrayList<SearchState> successors = new ArrayList<SearchState>();

        BoardCoordinate curCoords = state.getAgentCoordinates();

        // This speeds up checking the four directions (so we can use a loop).
        String[] actions = {"left", "up", "right", "down"};
        BoardCoordinate[] successorCoords = {
            new BoardCoordinate(curCoords.x-1, curCoords.y), // left
            new BoardCoordinate(curCoords.x, curCoords.y-1), // up
            new BoardCoordinate(curCoords.x+1, curCoords.y), // right
            new BoardCoordinate(curCoords.x, curCoords.y+1) // down
        };

        // Look in each direction around the agent. If there isn't a wall
        // (indicated by 'w'), then it's a valid move, so add it to the list
        // of successors.
        for(int i = 0; i < actions.length; i++){
            if(successorCoords[i].y >= 0 && 
                    successorCoords[i].y < internalBoard.size() &&
                successorCoords[i].x >= 0 && 
                    successorCoords[i].x < internalBoard.get(0).size() && 
                internalBoard.get(successorCoords[i].y)
                    .get(successorCoords[i].x) != 'w'){

                successors.add(new MazeState(successorCoords[i], actions[i], 
                    1, getDistance(successorCoords[i])));
            }
        }

        return successors;
    }

    /**
     * Parses a maze file. Should contain one character per spot on the maze.
     * Here are the character codes:
     *   s -- the agent's starting position
     *   e -- exit (if reached, the goal state has been reached)
     *   w -- wall (cannot be entered by the agent)
     *   (blank) -- a spot the agent may enter
     * 
     * @param filename The name of the maze file to load.
     * @return A color mapped version of the maze.
     */
    @Override
    public Board loadBoardFile(String filename) throws FileNotFoundException {
        Scanner reader = new Scanner(new File(filename));
        String line;
        char spot;
        int x = 0, y = 0;
        Board board;
        internalBoard = new ArrayList<ArrayList<Character>>();

        while(reader.hasNextLine()){
            line = reader.nextLine();
            ArrayList<Character> row = new ArrayList<Character>();

            for(x = 0; x < line.length(); x++){
                spot = line.charAt(x);

                // Add the spot to the internal representation.
                row.add(spot);
                if(spot == 's'){
                    start = new BoardCoordinate(x, y);
                    startState = new MazeState(
                        start, "", 0.0, getDistance(start));
                } else if(spot == 'e') {
                    exit = new BoardCoordinate(x, y);
                }
            }
            internalBoard.add(row);
            y++;
        }
        reader.close();


        board = new Board(internalBoard.size(), internalBoard.get(0).size());
        // Map the spot to a color and add that to the external
        // board representation.

        y = 0;
        for(ArrayList<Character> row : internalBoard){
            x = 0;
            for(Character c : row){
                board.setColor(new BoardCoordinate(x,y), colorMap.get(c.charValue()));
                System.out.print(c);
                x++;
            }
            System.out.println();
            y++;
        }

        return board;
    }
    
}