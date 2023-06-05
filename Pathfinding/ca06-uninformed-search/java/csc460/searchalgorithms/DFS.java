package csc460.searchalgorithms;

import csc460.searchproblems.SearchProblem;
import csc460.BoardCoordinate;
import csc460.SearchNode;
import csc460.SearchState;

import java.util.LinkedList;
import java.util.ArrayList;
import java.util.HashSet;


// TODO 3 -- Transform this copy of BFS into DFS.

/**
 * Breadth First Search: states are explored based on their distance from the
 * starting state (closer = earlier).
 * 
 * @author Hank Feild (hfeild@endicott.edu)
 */
public class DFS implements SearchAlgorithm {
    SearchProblem problem;
    LinkedList<SearchNode> fringe;
    HashSet<SearchState> seen;
    int numStatesExpanded;
    int maxFringeSize;

    /**
     * Initializes the fringe so it only holds the starting state. Initiazes the 
     * seen set and all the stats.
     */
    public void init(SearchProblem problem){
        fringe= new LinkedList<SearchNode>();
        fringe.add(new SearchNode(
            problem.getStartState(), 
            new ArrayList<String>(), 
            new ArrayList<BoardCoordinate>(), 
            0.0));
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
            node = fringe.pop();
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
            
            fringe.addFirst(new SearchNode(
                successor,
                pathActions,
                pathCoords,
                node.cost + successor.getCost()
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