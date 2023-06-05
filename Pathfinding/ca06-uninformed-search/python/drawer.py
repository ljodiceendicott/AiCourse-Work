import pygame


BLACK = (0, 0, 0)
WHITE = (255, 255, 255)
GREEN = (0, 255, 0)
RED = (255, 0, 0)
BLUE = (0, 0, 255)
ORANGE = (255, 165, 0)

SPOT_SIZE = 60
MARGIN_SIZE = 5
FPS = 3
ALPHA_STEP = 2
ALPHA_START = 15
 
class Drawer:

    def __init__(self, problem, searchAlg, fps=FPS, spotSize=SPOT_SIZE, 
            marginSize=MARGIN_SIZE):
        '''
        @param problem The search problem object (e.g., of type Maze)
        @param searchAlg A search algorithm object (e.g, of type BFS, DFS, etc.)
        @param fps The frames per second to display the animation at.
        @param spotSize The size of each square in the graphical display.
        @param margineSize The amount of space to leave between spots in the
                           graphica display.
        '''
        pygame.init()

        self.problem = problem
        self.board = problem.drawableBoard()
        self.searchAlg = searchAlg
        self.fps = fps
        self.spotSize = spotSize 
        self.marginSize = marginSize

        self.size = (len(problem.board[0])*(spotSize+marginSize)+marginSize, 
        len(problem.board)*(spotSize+marginSize)+marginSize)
        self.screen = pygame.display.set_mode(self.size)

        self.clock = pygame.time.Clock()


    def internalToExternalIndex(self, x):
        '''
        @param x A spot's row or column index (0, 1, 2, ...).
        @return The horizontal or vertical position of the spot on the graphical
                display.
        '''
        return self.marginSize + x*(self.spotSize+self.marginSize)


    def drawSpot(self, x, y, color):
        '''
        Draws a spot (square) at the given position and of the given color.

        @param x The x location on the board.
        '''
        externalX = self.internalToExternalIndex(x)
        externalY =  self.internalToExternalIndex(y)

        s = pygame.Surface((self.spotSize, self.spotSize))
        if len(color) == 4:
            s.set_alpha(color[3])    # alpha level
        s.fill(color[0:3])           # this fills the entire surface
        self.screen.blit(s, (externalX,externalY))


    def run(self):
        ## This colors the current spot in the plan. Starts off very light blue.
        curSpotColor = [0,0,255,ALPHA_START]
        done = False
        searchOver = False

        while not done:
            # --- Main event loop
            for event in pygame.event.get():
                if event.type == pygame.QUIT:
                    done = True

        
            if not searchOver:
                node = self.searchAlg.nextState()

                if not node:
                    print('No solution found :(')
                    searchOver = True

                elif self.problem.isGoal(node.state):
                    for spot in node.pathStates:
                        if spot != self.problem.start:
                            self.board[spot[0]][spot[1]] = ORANGE
                    searchOver = True

                    ## TODO 2
                    ## Display stats about the solution to stdout, including:
                    ##   Search algorithm: ...
                    ##   States expanded: ...
                    ##   Max fringe size: ...
                    ##   Solution cost: ...
                    ##   Solution path length: ...
                    ##   Solution path (actions):
                    ##     ...
                    ##     ...

                elif node.state != self.problem.start:
                        self.board[node.state[0]][node.state[1]] = curSpotColor.copy()
                        ##  Make it a little bluer the next time around.
                        if curSpotColor[3] <= 245:
                            curSpotColor[3] += ALPHA_STEP

                # --- Screen-clearing code goes here
            
                # Here, we clear the screen to white. Don't put other drawing
                # commands above this, or they will be erased with this command.
            
                # If you want a background image, replace this clear with
                # blit'ing the background image.
                self.screen.fill(WHITE)
            
                # --- Drawing code should go here
            
                for j,row in enumerate(self.board):
                    for i,color in enumerate(row):
                        self.drawSpot(i, j, color)


                # --- Go ahead and update the screen with what we've drawn.
                pygame.display.flip()
            
                # --- Limit to FPS frames per second
                self.clock.tick(self.fps)
        

        # Close the window and quit.
        pygame.quit()