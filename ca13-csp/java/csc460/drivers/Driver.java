package csc460.drivers;

import java.io.FileNotFoundException;
import csc460.Board;

/**
 * Simple interface for driving board (any grid) updates step-by-step.
 * 
 * @author Hank Feild (hfeild@endicott.edu)
 */
public interface Driver {
    public void loadBoardFile() throws FileNotFoundException;
    public boolean step();
    public Board getBoard();
}