// File:   Minimax.java
// Author: Hank Feild
// Date:   2022-03-10

/**
 * Performs Minimax with tic-tac-toe.
 */
public class Minimax {
    TicTacToe game;

    /**
     * Initializes the game to perform Minimax over.
     * @param game
     */
    public Minimax(TicTacToe game) {
        this.game = game;
    }

    /**
     * Determines the action/move that the next player should make given the
     * current state of the game by running Minimax with the state as the root.
     * 
     * @param state The state to find the next move for.
     * @param loggingDepth How many levels down the Minimax tree to display info
     *                    for; use 0 to disable this feature. 
     * @param depth the depth that it is at which to stop in depth limited Minmax: use -1
     *              conduct a full Minmax
     * @param loggingPrefix Used in conjunction with logging; use this to provide 
     *               additional spacing for each subsequent level of the Minimax
     *               tree for easier reading.
     * @return The action/move the next player should make and the expected
     *         utility of that move.
     */
    public TicTacToe.TicTacToeActionUtility value(TicTacToe.TicTacToeState state, 
            int depth, int loggingDepth, String loggingPrefix) {

        if(game.isTerminal(state)) {
            return game.getActionUtility(state, game.utility(state));

        }else if(depth == 0){
            //return result of eval function
            
            return null;
        } 
        else if(game.isMax(state)) {
            return maxValue(state, loggingDepth, depth, loggingPrefix);

        } else {
            return minValue(state, loggingDepth, depth, loggingPrefix);
        }
    }

    /**
     * Selects the move that maximizes utility over the successors of the
     * given state.
     * 
     * @param state The state to find the next move for.
     * @param loggingDepth How many levels down the Minimax tree to display info
     *                    for; use 0 to disable this feature. 
     * @param depth the depth that it is at which to stop in depth limited Minmax: use -1
     *              conduct a full Minmax
     * @param loggingPrefix Used in conjunction with logging; use this to provide 
     *               additional spacing for each subsequent level of the Minimax
     *               tree for easier reading.
     * @return The action/move the next player should make and the expected
     *         utility of that move.
     */
    public TicTacToe.TicTacToeActionUtility maxValue(TicTacToe.TicTacToeState state, 
            int loggingDepth, int depth, String loggingPrefix){

        TicTacToe.TicTacToeActionUtility actionUtility = null;
        
        for(TicTacToe.TicTacToeState successor : game.successors(state)){
            TicTacToe.TicTacToeActionUtility successorActionUtility = 
                value(successor, loggingDepth-1, depth-1, loggingPrefix+" ");

            // Logging.
            if(loggingDepth > 0) {
                System.out.println(loggingPrefix +"maxValue: "+ 
                    successor.toString().replaceAll("\n", "\n"+loggingPrefix) +"\n"+ 
                    loggingPrefix +successorActionUtility.toString().
                    replaceAll("\n", "\n"+loggingPrefix));
            }   

            if(actionUtility == null || 
                    successorActionUtility.getUtility() > actionUtility.getUtility()) {
                actionUtility = game.getActionUtility(successor, 
                    successorActionUtility.getUtility());
            }
        }
        
        // Logging.
        if(loggingDepth > 0) {
            System.out.println(loggingPrefix+ "maxValue: returning "+ 
                actionUtility.toString().replaceAll("\n", "\n"+loggingPrefix));
        }

        return actionUtility;
    }

    /**
     * Selects the move that minimizes utility over the successors of the
     * given state.
     * 
     * @param state The state to find the next move for.
     * @param loggingDepth How many levels down the Minimax tree to display info
     *                    for; use 0 to disable this feature. 
     * @param depth the depth that it is at which to stop in depth limited Minmax: use -1
     *              conduct a full Minmax
     * @param loggingPrefix Used in conjunction with logging; use this to provide 
     *               additional spacing for each subsequent level of the Minimax
     *               tree for easier reading.
     * @return The action/move the next player should make and the expected
     *         utility of that move.
     */
    public TicTacToe.TicTacToeActionUtility minValue(TicTacToe.TicTacToeState state, 
            int loggingDepth, int depth, String loggingPrefix){

        TicTacToe.TicTacToeActionUtility actionUtility = null;
        
        for(TicTacToe.TicTacToeState successor : game.successors(state)){
            TicTacToe.TicTacToeActionUtility successorActionUtility = 
                value(successor, loggingDepth-1, depth-1, loggingPrefix+" ");

            // Logging.
            if(loggingDepth > 0) {
                System.out.println(loggingPrefix +"minValue: "+ 
                    successor.toString().replaceAll("\n", "\n"+loggingPrefix) +"\n"+ 
                    loggingPrefix + successorActionUtility.toString().
                    replaceAll("\n", "\n"+loggingPrefix));
            }

            if(actionUtility == null || 
                    successorActionUtility.getUtility() < actionUtility.getUtility()){
                actionUtility = game.getActionUtility(
                    successor, successorActionUtility.getUtility());
            }
        }
        
        // Logging.
        if(loggingDepth > 0)
            System.out.println(loggingPrefix +"minValue: returning "+ 
                actionUtility.toString().replaceAll("\n", "\n"+loggingPrefix));
  
        return actionUtility;
    }
}