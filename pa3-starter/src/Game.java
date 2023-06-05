import java.util.ArrayList;

public interface Game {
    public void run();
    public boolean isTerminal(Game.State state);
    public double utility(State s);
    public double eval(State s);
    public ArrayList<State> successors(State s);
    public boolean isMax(Game.State state);
    public Game.ActionUtility getActionUtility(Game.State state, double eval);

    interface State{
      public void makeMove(char player, int move);
      public Game.State clone();
    }
    interface ActionUtility{
      public double getUtility();
      public void setUtility(double num);
    }
   
  
    
}
