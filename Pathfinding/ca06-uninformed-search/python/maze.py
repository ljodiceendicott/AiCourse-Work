import sys
import drawer
import search 

class Maze:

    def __init__(self, mazeFilename=None, heuristic=None):
        if mazeFilename == None:
            self.board = []
            self.start = (-1,-1)
            self.exit = (-1,-1)
        else:
            self.loadFile(mazeFilename)

        if heuristic == None:
            self.heuristic = Maze.defaultHeuristic
        else:
            self.heuristic = heuristic

    def loadFile(self, mazeFilename):
        '''
        Parses a maze file. Should contain one character per spot on the maze.
        Here are the character codes:
        s -- the agent's starting position
        e -- exit (if reached, the goal state has been reached)
        w -- wall (cannot be entered by the agent)
        (blank) -- a spot the agent may enter
        '''
        self.board = []
        for i,row in enumerate(open(mazeFilename)):
            self.board.append([])
            for j,col in enumerate(row.rstrip()):
                self.board[-1].append(col)
                if col == 's':
                    self.start = (i,j)
                elif col == 'e':
                    self.exit = (i,j)
                    

    def drawableBoard(self):
        '''
        @return The maze as a board of colors rather than maze-specific symbols.
        '''
        colorMap = {
            ' ': drawer.WHITE,
            's': drawer.GREEN,
            'e': drawer.RED,
            'w': drawer.BLACK
        }
        boardColors = []

        for row in self.board:
            boardColors.append([])
            for col in row:
                boardColors[-1].append(colorMap[col])

        return boardColors

    def successors(self, state):
        '''
        Produces a list of spots -- (i,j) pairs -- that the agent can move into,
        along with the spot's cost and estimate of how far it is from the exit.
        Returned as a list of tuples:

        [
            (move, (i,j), cost, dist),
            ...
        ]
        '''
        (i,j) = state
        successors = []
        potentialSuccessorSpots = (
            ('left', i,j-1),
            ('up', i-1,j), 
            ('right', i,j+1), 
            ('down', i+1,j) 
        )

        for move,i,j in potentialSuccessorSpots:
            if( i >= 0 and i < len(self.board) and 
                j >= 0 and j <len(self.board[i]) and 
                self.board[i][j] != 'w'):

                successors.append((move, (i,j), 1, self.heuristic((i,j), self)))

        return successors

    def isGoal(self, state):
        return state == self.exit

    @staticmethod
    def defaultHeuristic(state, maze):
        return 0

    @staticmethod
    def manhattenDistance(state, maze):
        return abs(maze.exit[0]-state[0]) + abs(maze.exit[1]-state[1])


if __name__ == "__main__":

    mazeFile = None 
    searchAlgorithm = None 
    fps = drawer.FPS 
    spotSize = drawer.SPOT_SIZE 
    marginSize = drawer.MARGIN_SIZE
    heuristic = None

    ## Read in arguments.
    for arg in sys.argv[1:]:
        ## Maze file.
        if arg.startswith('-f='):
            mazeFile = arg[3:]

        ## Search algorithm
        elif arg.startswith('-a='):
            searchAlgorithm = arg[3:]

        ## Heuristic
        elif arg.startswith('-h='):
            heuristic = arg[3:]

        ## FPS
        elif arg.startswith('-fps='):
            fps = int(arg[5:])

        ## Spot size
        elif arg.startswith('-spotSize='):
            spotSize = int(arg[10:])

        ## Margin size
        elif arg.startswith('-marginSize='):
            marginSize = int(arg[12:])

    if mazeFile == None or searchAlgorithm == None:
        sys.stderr.write('Too few args.\n\n'+
            'Usage: maze.py [arguments]\n\n'+
            'REQUIRED arguments:\n'+
            '   -f=F -- F is the maze file; see the loadFile function for \n'+
            '           details on the format.\n'+
            '   -a=A -- A is the search algorithm. Options:\n'+
            '            * bfs -- breadth first search\n'+
            '            * dfs -- depth first search\n'+
            '            * id  -- iterative deepening\n\n'+
            'OPTIONAL arguments:\n'+
            '   -h=H -- H is the heuristic; currently none supported\n'+
            '   -fps=FPS -- FPS is the frames per second; default: 3\n'+
            '   -spotSize=S -- S is the width and height of the board spots\n'+
            '           drawn to the screen; default is 60\n'+
            '   -marginSize=S -- S is the space between spots; default is 5\n')
        sys.exit()

    maze = Maze(mazeFile, heuristic)

    if searchAlgorithm == 'bfs':
        (drawer.Drawer(maze, search.BFS(maze), fps, spotSize, marginSize)).run()
    elif searchAlgorithm == 'dfs':
        (drawer.Drawer(maze, search.DFS(maze), fps, spotSize, marginSize)).run()
    elif searchAlgorithm == 'id':
        (drawer.Drawer(maze, search.IterativeDeepening(maze), fps, spotSize, marginSize)).run()

