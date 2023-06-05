package csc460;

import java.util.ArrayList;

/**
 * A wrapper for a search state, the path of actions that led to that state,
 * the coordinates of each spot along that path, and the cost of that path.
 * 
 * @author Hank Feild (hfeild@endicott.edu)
 */
public class SearchNode implements Comparable {
    public SearchState state;
    public ArrayList<String> pathActions;
    public ArrayList<BoardCoordinate> pathCoords;
    public double cost;
    public double priority;

    /**
     * @param state The search state.
     * @param pathActions The path of actions that led the agent to the state.
     * @param pathCoords The coordinates of each path that led to the state.
     * @param cost The cost of the path that led to the state.
     */
    public SearchNode(SearchState state, ArrayList<String> pathActions, 
            ArrayList<BoardCoordinate> pathCoords, double cost){
        this(state, pathActions, pathCoords, cost, 0);
    }

    /**
     * @param state The search state.
     * @param pathActions The path of actions that led the agent to the state.
     * @param pathCoords The coordinates of each path that led to the state.
     * @param cost The cost of the path that led to the state.
     */
    public SearchNode(SearchState state, ArrayList<String> pathActions, 
            ArrayList<BoardCoordinate> pathCoords, double cost, double priority){
        this.state = state;
        this.pathActions = pathActions;
        this.pathCoords = pathCoords;
        this.cost = cost;
        this.priority = priority;
    }

    /**
     * Compares two SearchNodes based on their priority.
     */
    @Override
    public int compareTo(Object o) {
        return Double.valueOf(priority).compareTo(
            Double.valueOf(((SearchNode) o).priority));
    }


}