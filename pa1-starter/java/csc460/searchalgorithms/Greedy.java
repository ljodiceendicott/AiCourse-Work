package csc460.searchalgorithms;

import csc460.searchproblems.SearchProblem;
import csc460.BoardCoordinate;
import csc460.SearchNode;
import csc460.SearchState;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;

/**
 * Greedy Search: states are explored based on their distance from a goal state
 * (states closer to the goal are favored).
 * 
 * @author Hank Feild (hfeild@endicott.edu)
 */
public class Greedy implements SearchAlgorithm {
    SearchProblem problem;
    PriorityQueue<SearchNode> fringe;
    HashSet<SearchState> seen;
    int numStatesExpanded;
    int maxFringeSize;

    /**
     * Initializes the fringe so it only holds the starting state. Initiazes the 
     * seen set and all the stats.
     */
    public void init(SearchProblem problem){
        fringe= new PriorityQueue<SearchNode>();
        fringe.add(new SearchNode(
            problem.getStartState(), 
            new ArrayList<String>(), 
            new ArrayList<BoardCoordinate>(), 
            0.0,
             // Priority: h(x) -- we call it the "distance" to a goal state in
             // this implementation.
            problem.getStartState().getDistance()));
        seen = new HashSet<SearchState>();
        this.problem = problem;
        numStatesExpanded = 0;
        maxFringeSize = 1;
    }

    /**
     * Finds the next node to expand. Previusly expanded nodes are ignored.
     * Adds unexplored successor states to the fringe.
     * 
     * @return The next node to expand. Null if there are no more nodes left 
     *         to explore.
     */
    public SearchNode nextNode(){
        if(fringe.isEmpty()){
            return null;
        }

        SearchNode node = fringe.poll();
        while(seen.contains(node.state) && !fringe.isEmpty()){
            node = fringe.poll();
        }
        if(seen.contains(node.state)){
            return null;
        }

        seen.add(node.state);

        expandNode(node);

        // Update stats.
        numStatesExpanded++;
        maxFringeSize = Math.max(maxFringeSize, fringe.size());

        return node;
    }

    /**
     * Adds the unexplored successor states of the given node's state to the 
     * fringe.
     * 
     * @param node The state whose successors should be added to the fringe.
     */
    public void expandNode(SearchNode node){
        for(SearchState successor : problem.getSuccessors(node.state)){
            if(seen.contains(successor)){
                continue;
            }

            ArrayList<String> pathActions = 
                new ArrayList<String>(node.pathActions);
            pathActions.add(successor.getAction());

            ArrayList<BoardCoordinate> pathCoords = 
                new ArrayList<BoardCoordinate>(node.pathCoords);
            pathCoords.add(successor.getAgentCoordinates());

            fringe.add(new SearchNode(
                successor,
                pathActions,
                pathCoords,
                node.cost + successor.getCost(), // Backward cost: g(x)
                // Priority: h(x) (we call it the "distance" to a goal state 
                // in this implementation).
                successor.getDistance()
            ));
        }
    }

    /**
     * @return The current number of states expanded.
     */
    @Override
    public int getNumStatesExpanded() {
        return numStatesExpanded;
    }

    /**
     * @return The maximum size of the fringe so far.
     */
    @Override
    public int getMaxFringeSize() {
        return maxFringeSize;
    }
}