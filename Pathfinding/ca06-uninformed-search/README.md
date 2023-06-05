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

## Maze file format

Mazes are defined using the following characters:

    w -- wall; the agent may not pass through these spots
    s -- the start of the maze (entry point)
    e -- the end of the maze (exit point)
      (space) -- a open spot that the agent may freely move into


## Python

To run the python version, do the following (if you have multiple version of 
python installed, use the one for which you've installed the `pygame` library,
e.g., that might be `python3.8`):

    cd python
    python maze.py -a=bfs -f=../mazes/maze01.txt

To see all the options, do:

    python maze.py



## Java

First, navigate to the `java/` folder:

    cd java

Then compile:

    javac -d bin csc460/*.java csc460/*/*.java

To run, do:

    java -cp bin csc460.drivers.SearchDriver -p=maze -a=bfs -f=../mazes/maze01.txt


To see a list of all options, do:

    java -cp bin csc460.drivers.SearchDriver

