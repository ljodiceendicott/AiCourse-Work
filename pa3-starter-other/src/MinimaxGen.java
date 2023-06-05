

// File:   Minimax.java
// Author: Hank Feild, CSC460 class
// Date:   2022-03-10

/**
 * Performs Minimax with tic-tac-toe.
 */

public class MinimaxGen{
    Game game;

    /**
     * Initializes the game to perform Minimax over.
     * @param game
     */
    public MinimaxGen(Game game) {
        this.game = game;
    }

    public Game.ActionUtility value(Game.State state, 
            int depth, int loggingDepth, String loggingPrefix){
                return value(state, depth, loggingDepth, loggingPrefix, Double.MIN_VALUE, Double.MAX_VALUE);
            }

    /**
     * Determines the action/move that the next player should make given the
     * current state of the game by running Minimax with the state as the root.
     * 
     * @param state The state to find the next move for.
     * @param depth The depth at which to stop in depth-limited Minimax; use -1
     *              to conduct a full Minimax search.
     * @param loggingDepth How many levels down the Minimax tree to display info
     *                    for; use 0 to disable this feature. 
     * @param loggingPrefix Used in conjunction with logging; use this to provide 
     *               additional spacing for each subsequent level of the Minimax
     *               tree for easier reading.
     * @return The action/move the next player should make and the expected
     *         utility of that move.
     */
    public Game.ActionUtility value(Game.State state, 
            int depth, int loggingDepth, String loggingPrefix, double alpha, double beta) {

        if(game.isTerminal(state)) {
            return game.getActionUtility(state, game.utility(state));

        } 
        else if(depth == 0) {
            // return the result of evaluation function.
            return game.getActionUtility(state, game.eval(state));
        }
        if(game.isMax(state)) {
            return maxValue(state, depth, loggingDepth, loggingPrefix, alpha, beta);

        } else {
            return minValue(state, depth, loggingDepth, loggingPrefix, alpha, beta);
        }
    }

    /**
     * Selects the move that maximizes utility over the successors of the
     * given state.
     * 
     * @param state The state to find the next move for.
     * @param depth The depth at which to stop in depth-limited Minimax; use -1
     *              to conduct a full Minimax search.
     * @param loggingDepth How many levels down the Minimax tree to display info
     *                    for; use 0 to disable this feature. 
     * @param loggingPrefix Used in conjunction with logging; use this to provide 
     *               additional spacing for each subsequent level of the Minimax
     *               tree for easier reading.
     * @return The action/move the next player should make and the expected
     *         utility of that move.
     */
    public Game.ActionUtility maxValue(Game.State state, 
            int depth, int loggingDepth, String loggingPrefix, double alpha, double beta){
        Game.ActionUtility actionUtility = null;
        for(Game.State successor : game.successors(state)){
            Game.ActionUtility successorActionUtility = 
                value(successor, depth-1, loggingDepth-1, loggingPrefix+" ", alpha, beta);
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
            //successorActionUtility.setUtility(Math.max(successorActionUtility.getUtility(),  value(successor,depth-1,loggingDepth-1,loggingPrefix+" ",alpha, beta).getUtility()));
            if(actionUtility.getUtility()>=beta){
                return actionUtility;
            }
            alpha = Math.max(alpha, actionUtility.getUtility());
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
     * @param depth The depth at which to stop in depth-limited Minimax; use -1
     *              to conduct a full Minimax search.
     * @param loggingDepth How many levels down the Minimax tree to display info
     *                    for; use 0 to disable this feature. 
     * @param loggingPrefix Used in conjunction with logging; use this to provide 
     *               additional spacing for each subsequent level of the Minimax
     *               tree for easier reading.
     * @return The action/move the next player should make and the expected
     *         utility of that move.
     */
    public Game.ActionUtility minValue(Game.State state, 
            int depth, int loggingDepth, String loggingPrefix, double alpha, double beta){
        Game.ActionUtility actionUtility = null;
       
        for(Game.State successor : game.successors(state)){
            Game.ActionUtility successorActionUtility = 
               value(successor,depth-1,loggingDepth-1,loggingPrefix+" ",alpha, beta);
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
            //successorActionUtility.setUtility(Math.min(actionUtility.getUtility(),  value(successor,depth-1,loggingDepth-1,loggingPrefix+" ",alpha, beta).getUtility()));
            if(actionUtility.getUtility()<=alpha){
                return actionUtility;
                }
                beta = Math.min(beta, actionUtility.getUtility());
        }

        return actionUtility;
    }
}