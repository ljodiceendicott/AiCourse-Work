package csc460;

/**
 * Stores an (x,y) coordinate.
 * 
 * @author Hank Feild (hfeild@endicott.edu)
 */
public class BoardCoordinate {
    public int x, y;

    /**
     * Initializes the coordinates.
     * @param x
     * @param y
     */
    public BoardCoordinate(int x, int y){
        this.x = x;
        this.y = y;
    }

    /**
     * @param other Another BoardCoordinate.
     * @return True if the x and y coordinates of this and other are the same.
     */
    public boolean equals(Object other){
        BoardCoordinate o = (BoardCoordinate) other;
        return other != null && o.x == x && o.y == y;
    }

    /**
     * @return The coordinate in the form: (x,y).
     */
    public String toString(){
        return "("+ x +","+ y +")";
    }
}