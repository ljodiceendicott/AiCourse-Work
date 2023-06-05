package csc460.searchproblems;

import csc460.searchproblems.SearchProblem;
import csc460.SearchState;
import csc460.Board;

import java.io.FileNotFoundException;
import java.lang.Iterable;

public interface SearchProblem {
    public void setHeuristic(String heuristic);
    public SearchState getStartState();
    public boolean isGoal(SearchState state);
    public Iterable<SearchState> getSuccessors(SearchState state);
    public Board loadBoardFile(String filename) throws FileNotFoundException;
}