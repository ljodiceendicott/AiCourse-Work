package csc460.searchalgorithms;

import csc460.searchproblems.SearchProblem;
import csc460.BoardCoordinate;
import csc460.SearchNode;
import csc460.SearchState;

import java.util.LinkedList;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Iterative deepening: states are explored DFS down to a given level; once all
 * states have been explored down to that level, the depth is increased.
 * 
 * @author Hank Feild (hfeild@endicott.edu)
 */
public class IterativeDeepening implements SearchAlgorithm {
    SearchProblem problem;
    LinkedList<SearchNode> fringe;
    HashSet<SearchState> seen;
    int numStatesExpanded;
    int maxFringeSize;
    int depth;
    int depthReached;

    /**
     * Initializes the fringe so it only holds the starting state. Initiazes the 
     * seen set and all the stats.
     * 
     * @param problem The search problem.
     */
    public void init(SearchProblem problem){
        
        this.problem = problem;
        numStatesExpanded = 0;
        maxFringeSize = 1;
        depth = 0;
        fringe= new LinkedList<SearchNode>();
        seen = new HashSet<SearchState>();
        fringe.add(new SearchNode(
            problem.getStartState(), 
            new ArrayList<String>(), 
            new ArrayList<BoardCoordinate>(), 
            0.0));
        depthReached = 0;

    }

    /**
     * Checks if the depth can be increased; if so, gets the first node of 
     * the next pass. Otherwise, all nodes have been explored and there is no
     * solution.
     * 
     * @return Null if no solution; first node of next pass if the depth can
     *         be increased.
     */
    private SearchNode nextNodeOfNextDepth(){
        // There's no more states to explore and there is no solution.
        if(depthReached < depth){
            return null;

        // We ran out of states at the specified depth; increase depth and
        // start over.
        } else {
            // TODO 4 -- Advance to the next depth and reset depthReached.
            //depth...
            //depthReached...

            // SOLUTION
            depth++;
            depthReached = 0;
            // END SOLUTION

            fringe.clear();
            seen.clear();

            fringe.add(new SearchNode(
                problem.getStartState(), 
                new ArrayList<String>(), 
                new ArrayList<BoardCoordinate>(), 
                0.0));

            return nextNode();
        }
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
            return nextNodeOfNextDepth();
        }

        // In ID, we use a stack/LIFO -- last in, first out.
        SearchNode node = fringe.pollLast();
        while(seen.contains(node.state) && !fringe.isEmpty()){
            node = fringe.pollLast();
        }
        if(seen.contains(node.state)){ // There's no more unseen states.
            return nextNodeOfNextDepth();
        }
        seen.add(node.state);

        // TODO 5 -- Only call exandNode on this node if its successors are 
        //  within the current depth limit.
        // SOLUTION
        if(node.pathActions.size() < depth){
            expandNode(node);
        }
        // END SOLUTION

        // Update stats.
        numStatesExpanded++;
        maxFringeSize = Math.max(maxFringeSize, fringe.size());
        depthReached = Math.max(depthReached, node.pathActions.size());

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