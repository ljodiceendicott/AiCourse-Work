package csc460;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JFrame;
import java.util.ArrayList;

import csc460.drivers.Driver;

/**
 * Handles drawing the board on the screen and calling the step() method of the
 * given driver. The screen is updated after each call to step().
 * 
 * @author Hank Feild (hfeild@endicott.edu)
 */
public class Drawer extends JFrame {

    /**
     * Initializes the board.
     * 
     * @param title The title to display at the top of the window.
     */
    public Drawer(Driver driver, String title, int spotSize, int marginSize, 
            int framesPerSecond) {
        add(new GraphicalBoard(driver, spotSize, marginSize, framesPerSecond));
        setResizable(false);
        pack();
        setTitle(title);    
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        
    }

    // Draws the board and also calls the necessary updates along the way.
    class GraphicalBoard extends JPanel
            implements Runnable {

        Driver driver;
        int delay;
        int width;
        int height;
        int marginSize;
        int spotSize;

        private Thread animator;

        /**
         * Initializes the window. The window size is based on the size of the 
         * board retrieved from the driver. The board consists of a grid of 
         * spots and maps directly from the board retrieved from the driver.
         * 
         * @param driver The object responsible for making updates to the board.
         * @param spotSize The height and width of each spot.
         * @param marginSize The space to either side of each spot.
         * @param framesPerSecond The freqency to call the step() method of the
         *                        driver.
         */
        public GraphicalBoard(Driver driver, int spotSize, int marginSize, 
                int framesPerSecond) {
            delay = 1000/framesPerSecond;
            this.driver = driver;
            this.spotSize = spotSize;
            this.marginSize = marginSize;

            // Compute the window dimensions based on the board size.
            height = driver.getBoard().numRows()*(spotSize+marginSize) + 
                     marginSize;
            width = driver.getBoard().numCols()*(spotSize+marginSize) + 
                     marginSize;

            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(width, height));
        }

        @Override
        public void addNotify() {
            super.addNotify();
            animator = new Thread(this);
            animator.start();
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawBoard(g);
        }

        /**
         * Draws the board to the screen.
         * 
         * @param g The drawing interface.
         */
        private void drawBoard(Graphics g) {
            Graphics2D g2D = (Graphics2D) g;
            int x = 0, y = 0;
            for(ArrayList<Color> entries : driver.getBoard().board){
                x = 0;
                for(Color color : entries){
                    drawSpot(g2D, x, y, color);
                    x++;
                }
                y++;
            }

            Toolkit.getDefaultToolkit().sync();
        }

        /**
         * Draws a square on the board at the given coordinate with the given
         * color.
         * 
         * @param g2D The drawing interface.
         * @param x The x value of the internal spot to draw (from board).
         * @param y The y value of the internal spot to draw (from board).
         * @param color The color to paint the spot.
         */
        private void drawSpot(Graphics2D g2D, int x, int y, Color color){
            int externalX = internalToExternalIndex(x);
            int externalY = internalToExternalIndex(y);

            Rectangle spot = new Rectangle(externalX, externalY, 
                spotSize, spotSize);
            g2D.setPaint(color);
            g2D.draw(spot);
            g2D.fill(spot);
        }

        /**
         * Converts a logical (internal) coordinate value from the board to a 
         * physical (external) coordinate value on the screen.
         * @param n The coordinate value (x or y) from the internal representation.
         * @return The external mapping of n.
         */
        private int internalToExternalIndex(int n){
            return marginSize + n*(spotSize+marginSize);
        }

        private BoardCoordinate externalToInternalIndex(BoardCoordinate coord){
            return new BoardCoordinate(
                (coord.x - marginSize)/(spotSize+marginSize),
                (coord.y - marginSize)/(spotSize+marginSize));
        }

        /**
         * Continues to run the next driver step and update the display until
         * step returns false. 
         */
        @Override
        public void run() {
            boolean keepGoing = true;
            long beforeTime, timeDiff, sleep;
            beforeTime = System.currentTimeMillis();

            while (keepGoing) {
                keepGoing = driver.step();
                repaint();

                timeDiff = System.currentTimeMillis()-beforeTime;
                sleep = delay - timeDiff;

                if (sleep < 0) {
                    sleep = 2;
                }

                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException e) {
                    
                    String msg = String.format("Thread interrupted: %s", e.getMessage());
                    
                    JOptionPane.showMessageDialog(this, msg, "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }

                beforeTime = System.currentTimeMillis();
            }
        }
    }

}