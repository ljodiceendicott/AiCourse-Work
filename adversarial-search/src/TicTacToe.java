// File:   TicTacToe.java
// Author: Hank Feild
// Date:   2022-03-10

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Represents a TicTacToe game.
 */
public class TicTacToe {

    /**
     * Represents a specific state of a tic-tac-toe board (the positions of the
     * X's, O's, and open spots).
     */
    public class TicTacToeState {
        public char[] board;
        public char player;
        public int move;

        /**
         * Initializes a tic-tac-toe state.
         * 
         * @param board Should be 9 characters long, each character 
         *              representing one spot on the board. Valid 
         *              characters are: X, O, and ' ' (space).
         * @param player The player who made the most recent move.
         * @param move The spot selected in the most recent move.
         */
        public TicTacToeState(char[] board, char player, int move){
            this.board = board;
            this.player = player;
            this.move = move;
        }

        /**
         * Updates the given spot on the board with the player's mark.
         * 
         * @param player The player who made the most recent move (should be X 
         *               or O)
         * @param move The spot selected in the most recent move, should be in
         *             the range [1,9].
         */
        public void makeMove(char player, int move){
            board[move-1] = player;
            this.move = move;
            this.player = player;
        }
        
        /**
         * @return A summary of this state as a string, including the board, 
         *         player, and action.
         */
        public String toString(){
            return "[\n\tboard: "+ Arrays.toString(board) +",\n\tplayer: "+
                player +",\n\tmove: "+ move +"\n]";
        }

        /**
         * @return A deep copy of this state.
         */
        public TicTacToeState clone(){
            return new TicTacToeState(board.clone(), player, move);
        }
    }

    /**
     * Serves as a wrapper for a tic-tac-toe move and the utility associated
     * with it.
     */
    public class TicTacToeActionUtility  {
        public double utility;
        public int move;

        /**
         * Initializes the action and utility.
         * @param move The spot selected in the most recent move.
         * @param utility The utility value associated with the move.
         */
        public TicTacToeActionUtility(int move, double utility){
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
         * @return A description of this move and utility.
         */
        public String toString(){
            return "[\n\tmove: "+ move +",\n\tutility: "+ utility +"\n]";
        }
    }


    // TicTacToe data members.
    TicTacToeState currentState;
    int loggingDepth;
    Scanner input;
    Minimax minimax;

    /**
     * Initializes helpers for the game.
     */
    public TicTacToe(int loggingDepth) {
        input = new Scanner(System.in);
        minimax = new Minimax(this);
        this.loggingDepth = loggingDepth;
    }

    /**
     * Starts a tic-tac-toe game between the user and the computer.
     */
    public void run(){
        int userMove, aiMove;
        currentState = new TicTacToeState("         ".toCharArray(), '?', 0);
        System.out.println("Key: ");
        printBoard("123456789".toCharArray());
        System.out.println();
        printBoard(currentState.board);

        // Until the game is over.
        while(true){
            // Human's turn.
            System.out.println("Your turn; enter the space # where you'd like to put your X: ");
            userMove = input.nextInt();
            currentState.makeMove('X', userMove);

            printBoard(currentState.board);

            // Check if the human won.
            if(isTerminal(currentState))
                break;

            // System.out.println(currentState);
            // break;

            // Computer's turn.
            System.out.println("\nComputer's turn:");
            aiMove = minimax.value(currentState, loggingDepth,3 , "").move;
            currentState.makeMove('O', aiMove);

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
    public void printBoard(char[] board){
        for(int i = 0; i < board.length; i++){
            if(i > 0 && i % 3 == 0){
                System.out.println("\n---+---+---");
            }
            System.out.print(" "+ board[i] + " ");
            if(i % 3 < 2){
                System.out.print("|");
            }
        }
        System.out.println();
    }

    /**
     * @return Returns true if the given tic-tac-toe state is terminal:
     *      - three Xs or Os in a row (vertically, horizontally, or diagonally)
     *      - all 9 spots are filled
     */
    public boolean isTerminal(TicTacToeState state) {
        char[] board = state.board;
        boolean isFilled = true;

        // Check if the board if filled.
        for(int i = 0; i < board.length; i++){
            if(board[i] == ' '){
                isFilled = false;
                break;
            }
        }

        // True if the board is filled or there are there X's or O's in a row.
        return
            // If we don't have three in a row, but the whole board is full, then
            // there's a draw.
            isFilled || 

            // Three in a row by row.
            (board[0] == board[1] && board[0] == board[2] && board[0] != ' ') ||
            (board[3] == board[4] && board[3] == board[5] && board[3] != ' ') ||
            (board[6] == board[7] && board[6] == board[8] && board[6] != ' ') ||

            // Three in a row by column.
            (board[0] == board[3] && board[0] == board[6] && board[0] != ' ') ||
            (board[1] == board[4] && board[1] == board[7] && board[1] != ' ') ||
            (board[2] == board[5] && board[2] == board[8] && board[2] != ' ') ||

            // Three in a row by diagonal.
            (board[0] == board[4] && board[0] == board[8] && board[0] != ' ') ||
            (board[2] == board[4] && board[2] == board[6] && board[2] != ' ');
    }

    /**
     * Finds the utility of a terminal state. This 1 if X has three in a row,
     * -1 if O has three in a row, and 0 otherwise.
     * 
     * @param state The state of the board to assess the utility of. This is
     *              assumed to be a terminal state, but that is not verified.
     * @return 1 if X wins, -1 if O wins, 0 if a draw.
     */
    public double utility(TicTacToeState state) {
        char[] board = state.board; // Just to make things a little easier.
        double total = 0;

        // Two in a row by row.
        if(board[0] == board[1] && board[2] != ' ' || board[0] == board[2] && board[0] != ' ')
            total += board[0] == 'X' ? 1 : -1;
        ///OTHERS ARE NOT SET UP FOR TWO IN A ROW
        if(board[3] == board[4] && board[3] == board[5] && board[3] != ' ')
            return board[3] == 'X' ? 1 : -1;

        if(board[6] == board[7] && board[6] == board[8] && board[6] != ' ')
            return board[6] == 'X' ? 1 : -1;


        // Three in a row by column.
        if(board[0] == board[3] && board[0] == board[6] && board[0] != ' ')
            return board[0] == 'X' ? 1 : -1;

        if(board[1] == board[4] && board[1] == board[7] && board[1] != ' ')
            return board[1] == 'X' ? 1 : -1;

        if(board[2] == board[5] && board[2] == board[8] && board[2] != ' ')
            return board[2] == 'X' ? 1 : -1;


        // Three in a row by diagonal.
        if(board[0] == board[4] && board[0] == board[8] && board[0] != ' ')
            return board[0] == 'X' ? 1 : -1;

        if(board[2] == board[4] && board[2] == board[6] && board[2] != ' ')
            return board[2] == 'X' ? 1 : -1;

        // If nothing else, draw.
        return 0;
    }

    public double eval(TicTacToeState state){
    char[] board = state.board; // Just to make things a little easier.    
        // Three in a row by row.
            if(board[0] == board[1] && board[0] == board[2] && board[0] != ' ')
            return board[0] == 'X' ? 1 : -1;
        
        if(board[3] == board[4] && board[3] == board[5] && board[3] != ' ')
            return board[3] == 'X' ? 1 : -1;

        if(board[6] == board[7] && board[6] == board[8] && board[6] != ' ')
            return board[6] == 'X' ? 1 : -1;


        // Three in a row by column.
        if(board[0] == board[3] && board[0] == board[6] && board[0] != ' ')
            return board[0] == 'X' ? 1 : -1;

        if(board[1] == board[4] && board[1] == board[7] && board[1] != ' ')
            return board[1] == 'X' ? 1 : -1;

        if(board[2] == board[5] && board[2] == board[8] && board[2] != ' ')
            return board[2] == 'X' ? 1 : -1;


        // Three in a row by diagonal.
        if(board[0] == board[4] && board[0] == board[8] && board[0] != ' ')
            return board[0] == 'X' ? 1 : -1;

        if(board[2] == board[4] && board[2] == board[6] && board[2] != ' ')
            return board[2] == 'X' ? 1 : -1;

        // If nothing else, draw.
        return 0;
    }
    /**
     * Generates a list of successors for the given state. These are all 
     * possible moves of the next player into blank spaces, where the "next player" is
     * whichever player isn't state.player.
     * 
     * @param state The state to generate successors for.
     * @return A list of all possible successor of the given state for the next
     *         player.
     */
    public ArrayList<TicTacToeState> successors(TicTacToeState state) {
        ArrayList<TicTacToeState> successorStates = new ArrayList<TicTacToeState>();
        TicTacToeState successorState;
        char nextPlayer = 'X';
        if(state.player == 'X')
            nextPlayer = 'O';

        for(int i = 0; i < state.board.length; i++){
            if(state.board[i] == ' '){
                successorState = state.clone();
                successorState.makeMove(nextPlayer, i+1);
                successorStates.add(successorState);
            }
        }
        
        return successorStates;
    }

    /**
     * Determines if this is a "max" state, meaning a state for which Minimax
     * should be maximizing over values of successors, meaning it's X's turn
     * next.
     * 
     * @param state The state to assess.
     * @return True if the next player is X.
     */
    public boolean isMax(TicTacToeState state) {
        // If the last player is O, then X is now going and therefore we're in
        // a max node.
        return state.player == 'O'; 
    }

    /**
     * A helper function that simply wraps a move (extracted from the given 
     * state) and a utility value.
     * 
     * @param state The state to extract the move from.
     * @param utility The utility to use.
     * @return state.move and utility wrapped up in an easy to use object.
     */
    public TicTacToeActionUtility getActionUtility(TicTacToeState state, double utility) {
        return new TicTacToeActionUtility(state.move, utility);
    }

    /**
     * Starts a game of tic-tac-toe between the user and the computer.
     * @param args Ignored.
     */
    public static void main(String[] args) {
        int loggingDepth = 0;
        if(args.length > 0){
            loggingDepth = Integer.parseInt(args[0]);
        }

        TicTacToe tictactoe = new TicTacToe(loggingDepth);
        tictactoe.run();
    }

}