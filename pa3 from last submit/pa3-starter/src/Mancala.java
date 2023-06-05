import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Mancala implements Game {

    public class MancalaState implements State {
        public int[] board;
        public char player;
        public int move;

        /**
         * Initializes a Mancala state.
         * 
         * @param board  Should be 9 characters long, each character
         *               representing one spot on the board. Valid
         *               characters are: X, O, and ' ' (space).
         * @param player The player who made the most recent move.
         * @param move   The spot selected in the most recent move.
         */
        public MancalaState(int[] board, char player, int move) {
            this.board = board;
            this.player = player;
            this.move = move;
        }

        /**
         * Updates the given spot on the board with the player's mark.
         * 
         * @param player The player who made the most recent move
         * @param move   The spot selected in the most recent move, should be in
         *               the range [0,13].
         */
        public void makeMove(char player, int move) {
            this.move = move;
            int numpel = board[move];
            board[move] = 0;
            System.out.println(numpel +" "+move);
            for(int i=1; i<numpel+1; i++){
                int loc =move+i;
                loc= loc%14;
                board[loc]++;
            }




        }

        /**
         * @return A summary of this state as a string, including the board,
         *         player, and action.
         */
        public String toString() {
            return "[\n\tboard: " + Arrays.toString(board) + ",\n\tplayer: " +
                    player + ",\n\tmove: " + move + "\n]";
        }

        /**
         * @return A deep copy of this state.
         */
        public MancalaState clone() {
            return new MancalaState(board.clone(), player, move);
        }
    }

    public class MancalaActionUtility implements ActionUtility {
        public double utility;
        public int move;

        /**
         * Initializes the action and utility.
         * 
         * @param move    The spot selected in the most recent move.
         * @param utility The utility value associated with the move.
         */
        public MancalaActionUtility(int move, double utility) {
            this.move = move;
            this.utility = utility;
        }

        /**
         * @return The utility of this move.
         */
        public double getUtility() {
            return utility;
        }
        /**
         * sets the value of utility
         */
        public void setUtility(double num) {
            this.utility = num;
        }


        /**
         * @return A description of this move and utility.
         */
        public String toString() {
            return "[\n\tmove: " + move + ",\n\tutility: " + utility + "\n]";
        }
    }

        /**
     * @return Returns true if the given Mancala state is terminal:
     *         - three Xs or Os in a row (vertically, horizontally, or diagonally)
     *         - all 9 spots are filled
     */
    public boolean isTerminal(Game.State state) {
        MancalaState s = (MancalaState)state;
        int[] board = s.board;
        if(board[0]==0 && board[1]==0 && board[2]==0 && board[3]==0 && board[4]==0 && board[5]==0){
            return true;
        }
        else if(board[7]==0 && board[8]==0 && board[9]==0 && board[10]==0 && board[11]==0 && board[12]==0){
            return true;
        }
        return false;

    }
    // Mancala data Members
    MancalaState currentState;
    int loggingDepth;
    Scanner input;
    MinimaxGen minimax;

    /**
     * Initializes helpers for the game.
     */
    public Mancala(int loggingDepth) {
        input = new Scanner(System.in);
        minimax = new MinimaxGen(this);
        this.loggingDepth = loggingDepth;
    }

    /**
     * Starts a Mancala game between the user and the computer.
     */
    public void run() {
        int userMove, aiMove;
        int[] board = {4,4,4,4,4,4,0,4,4,4,4,4,4,0};
        currentState = new MancalaState(board, '?',0);
        System.out.println("Key: 1,2,3,4,5,6");
        System.out.println();
        printBoard(currentState.board);

        // Until the game is over.
        while (true) {
           // Human's turn.
           System.out.print("Your turn; enter the space # where you'd like to use as your move: ");
           userMove = input.nextInt();
           while(currentState.board[userMove+6] == 0||userMove>6){
               System.out.print("That spot is invalid; try again: ");
               userMove = input.nextInt();
           }
           //calculated the offset for the players move in the algo
           userMove = userMove+6;
           currentState.makeMove('a', userMove);
           printBoard(currentState.board);

           // Check if the human won.
           if(isTerminal(currentState))
               break;

           // Computer's turn.
           System.out.println("\nComputer's turn:");
           // currentState = new MancalaState(currentState.board, 'b',userMove);
           currentState.move=userMove;
          
           MancalaActionUtility s = (MancalaActionUtility)minimax.value(currentState, 3, loggingDepth, "", Integer.MAX_VALUE, Integer.MIN_VALUE);
           aiMove = s.move;
           currentState.makeMove('b', aiMove);
           
           printBoard(currentState.board);

           // Check if the AI won.
           if(isTerminal(currentState))
               break;
       }

       System.out.println("Game over. Utility: "+ utility(currentState));

    }

    /**
     * Prints the board as a 3x3 table.
     * 
     * @param board The tic-tac-toc board to print.
     */
    public void printBoard(int[] board) {
        String output=
            "    6        5        4        3       2        1\n"+
            "    |        |        |        |       |        |\n"+
            "   ("+board[5]+")      ("+board[4]+")      ("+board[3]+")      ("+board[2]+")     ("+board[1]+")      ("+board[0]+")\n"+
  board[6]+"                                                   "+board[13]+"\n"+
            "   ("+board[7]+")      ("+board[8]+")      ("+board[9]+")      ("+board[10]+")     ("+board[11]+")      ("+board[12]+")\n"+
            "    1        2        3        4       5        6\n"+
            "    |        |        |        |       |        |\n";
            
        System.out.print(output);
        System.out.println();

    }



    /**
     * Finds the utility of a terminal state. This 1 if X has three in a row,
     * -1 if O has three in a row, and 0 otherwise.
     * 
     * @param state The state of the board to assess the utility of. This is
     *              assumed to be a terminal state, but that is not verified.
     * @return 1 if X wins, -1 if O wins, 0 if a draw.
     */
    public double utility(Game.State state) {
        MancalaState s = (MancalaState)state;
        int[] board = s.board; // Just to make things a little easier.

        if(board[13] == board[6]){
            return 0;
        }
        else if(board[13] < board[6])
        {
            return -1;
        }
        else if(board[13]> board[6])
        {
            return 1;
        }
        return 0;
    }

    /**
     * Evaluates a non-terminal state. Each column that has two X's or two O's
     * and one open space yields a value of 1 (for X) or -1 (for O). This is
     * summed across the three columns for the final value.
     * 
     * @param state The non-terminal state to evaluate.
     * @return A positive return value favors X's chances of winning, negative
     *         favors O's, an 0 is an expected draw.
     */
    public double eval(Game.State state) {
        MancalaState s = (MancalaState)state;
        int[] board = s.board; // Just to make things a little easier.
        double total = 0;
        if(s.player == 'b'){
            //it is the computers turn
            for(int i = 0; i<6; i++){
                if(board[i]>=i+1){
                    total--;
                }
            }
        }
        else if(s.player == 'a'){
            //this is eval for the player's turn
            for(int i = 0; i<6; i++){
                if(board[12-i] >= 6-i){
                    total++;
                }
            }
        }

        // If nothing else, draw.
        return total;
    }

    /**
     * Generates a list of successors for the given state. These are all
     * possible moves of the next player into blank spaces, where the "next
     * player" is whichever player isn't state.player.
     * 
     * @param state The state to generate successors for.
     * @return A list of all possible successor of the given state for the next
     *         player.
     */
    public ArrayList<Game.State> successors(Game.State s) {
        ArrayList<Game.State> successorStates = new ArrayList<Game.State>();
        MancalaState state = (Mancala.MancalaState)s;
        MancalaState successorState;
        char nextplayer = 'a';
        if( state.player== 'b'){
            nextplayer = 'a';
        }
        if(nextplayer == 'a'){
            for(int i =6; i>0; i--){
                if(state.board[i]>= i){
                    successorState = state.clone();
                    successorState.makeMove(nextplayer, i);
                    successorStates.add(successorState);
                }
            }
            for(int i =6; i>0; i--){
                if(state.board[i]<i){
                    successorState = state.clone();
                    successorState.makeMove(nextplayer, i);
                    successorStates.add(successorState);
                }
            }

        }
        else if(nextplayer == 'b'){
            for(int i =13; i>7; i--){
                if(state.board[i]>= i){
                    successorState = state.clone();
                    successorState.makeMove(nextplayer, i);
                    successorStates.add(successorState);
                }
            }
            for(int i =13; i>7; i--){
                if(state.board[i]<i){
                    successorState = state.clone();
                    successorState.makeMove(nextplayer, i);
                    successorStates.add(successorState);
                }
            }
        }

        return successorStates;
    }

    /**
     * A helper function that simply wraps a move (extracted from the given
     * state) and a utility value.
     * 
     * @param state   The state to extract the move from.
     * @param utility The utility to use.
     * @return state.move and utility wrapped up in an easy to use object.
     */
    public Game.ActionUtility getActionUtility(Game.State state, double utility) {
        MancalaState s = (MancalaState)state;
        return new MancalaActionUtility(s.move, utility);
    }

    /**
     * Determines if this is a "max" state, meaning a state for which Minimax
     * should be maximizing over values of successors, meaning it's X's turn
     * next.
     * 
     * @param state The state to assess.
     * @return True if the next player is X.
     */
    public boolean isMax(Game.State s) {
        MancalaState state = (MancalaState)s;
        // If the last player is O, then X is now going and therefore we're in
        // a max node.
        return state.player == 'a';
    }

    public static void main(String[] args) {
        int loggingDepth = 0;
        if (args.length > 0) {
            loggingDepth = Integer.parseInt(args[0]);
        }

        Mancala mancala = new Mancala(loggingDepth);
        mancala.run();
    }

}
