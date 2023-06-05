# Endicott CSC460 Search Codebase

This folder contains the following subfolders:

    mazes/       -- holds all the mazes
        maze01.txt -- an example maze input file.

    python/      -- python implementation
        (several python files)

    java/        -- Java implementation
        bin/     -- folder for compiled Java class files (initially empty)
        csc460/
            (several java files and directories)

Both codebases contain programs to load a maze file from disk and find a path
for the agent to take from a starting spot to the exit using on of several
search algorithms.

## Who Worked on the Codebase
	It was only me(Luke Jodice) and the inclusion of the starter code from Professor Field

## Maze file format

Mazes are defined using the following characters:

    w -- wall; the agent may not pass through these spots
    s -- the start of the maze (entry point)
    e -- the end of the maze (exit point)
      (space) -- a open spot that the agent may freely move into

# CatMaze
## CatMaze file format
	
	In CatMaze file, on the first line it should state the size of the map
		-EX. 6 
		This means that the map is a 6x6
	
	same as regular maze characters plus
	
	m -- mouse, this is a 'pellet that needs to be picked up'
	h -- hazard, this costs 5 to go through it

## CatMaze Heuristic Implementations

 ### Distance to End (dte)
	This heuristic uses the total distance to the remaining pellets. This is an estimate of how far the agent has to move in order to get to the end. the distance should reduce while the agent is moving toward the remaining pellets

### Number of Remaining Pellets (nrp) 
	This heuristic uses the total remainder of number of pellets that are still on the board it is not until there are zero pellets remaining does the agent start to go towards the exit. when it does it would start to do somthing similar to a manhattan distance

## Python

To run the Python version, you will need Python 3 installed along with the 
`pygame` library (which you can install via `pip`). If you have multiple versions of 
Python installed, use the one for which you've installed the `pygame` library,
e.g., that might be `python3.10`. use `python -V` to find out what version of
Python you're using. To run, do:

    cd python
    python maze.py -a=bfs -f=../mazes/maze01.txt

To see all the options, do:

    python maze.py



## Java

This section assumes you're using a bash-like shell (GitBash on Windows or 
bash or zsh on macOS). PowerShell and CMD will fail due to the 
wildcards (*) in the paths of the `javac` command.

First, navigate to the `java/` folder:

    cd java

Then compile:

    javac -d bin csc460/*.java csc460/*/*.java

To run maze problem, do:

    java -cp bin csc460.drivers.SearchDriver -p=maze -a=bfs -f=../mazes/maze01.txt

to run catmaze problem, do:
	
 java -cp bin csc460.drivers.CatDriver -p=catmaze -a=bfs -f=..mouse-and-meows/map01.txt


To see a list of all options, do:

    java -cp bin csc460.drivers.SearchDriver

