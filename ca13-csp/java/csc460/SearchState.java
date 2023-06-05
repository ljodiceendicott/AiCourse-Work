package csc460;

/**
 * Represents a generic search states.
 * 
 * @author Hank Feild (hfeild@endicott.edu)
 */
public interface SearchState {
    public BoardCoordinate getAgentCoordinates();
    public double getCost();
    public double getDistance();
    public String getAction();
}