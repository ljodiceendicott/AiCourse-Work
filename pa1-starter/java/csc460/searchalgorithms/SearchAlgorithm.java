package csc460.searchalgorithms;

import csc460.SearchNode;
import csc460.searchproblems.SearchProblem;

/**
 * A simple interface for search algorithms.
 * 
 * @author Hank Feild (hfeild@endicott.edu)
 */
public interface SearchAlgorithm {
    public void init(SearchProblem searchProblem);
    public SearchNode nextNode();
    public int getNumStatesExpanded();
    public int getMaxFringeSize();
}


