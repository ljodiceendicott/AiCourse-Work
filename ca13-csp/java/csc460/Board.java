package csc460;

import java.awt.Color;
import java.util.ArrayList;

/**
 * Stores the color that should be stored at each spot in the board.
 * 
 * @author Hank Feild (hfeild@endicott.edu)
 */
public class Board {
    public static final Color DEFAULT_COLOR = Color.WHITE;
    public ArrayList<ArrayList<Color>> board;
    private int numRows;
    private int numCols;

    /**
     * Initializes the board to the given size. Uses the DEFAULT_COLOR for 
     * all spots.
     * 
     * @param numRows The number of rows the board should be.
     * @param numCols The number of columns the board should be.
     */
    public Board(int numRows, int numCols){
        board = new ArrayList<ArrayList<Color>>();
        this.numRows = numRows;
        this.numCols = numCols;

        for(int i = 0; i < numRows; i++){
            ArrayList<Color> row = new ArrayList<Color>();
            for(int j = 0; j < numCols; j++){
                row.add(DEFAULT_COLOR);
            }
            board.add(row);
        }
    }

    /**
     * Sets the color of the given spot on the board.
     * 
     * @param coord The spot of the board whose color should be set.
     * @param color The color to set that spot.
     */
    public void setColor(BoardCoordinate coord, Color color){
        board.get(coord.y).set(coord.x, color);
    }

    /**
     * @return The number of rows of the board.
     */
    public int numRows(){
        return numRows;
    }

    /**
     * @return The number of columns of the board.
     */
    public int numCols(){
        return numCols;
    }
}