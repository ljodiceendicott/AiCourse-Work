# Endicott CSC460 Search Codebase

This folder contains the following subfolders:

    csps/        -- holds CSP description files
        new-england1.csp -- a map coloring problem
        schedule1.csp -- a small room scheduling problem
        schedule2.csp -- same as schedule1.csp, but with more readable variable 
                         names
        sudoku1.csp -- an example Sudoku puzzle

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

## CSP file format

CSP description files must have the following format:

  * the first line is a space separated list of variable names and their 
    assignments (if any)
    - assignments should be in the form of: x=value, where x is the variable name
  * the second line is a sapce separated list of domain values (treated as strings)
  * each subsequent line represents a set of constraints, which are of the 
    format: <name> <sets>
    - sets are space seperated from each other, each each set should consist of
      two or more comma seperated variables
    - supported constraings (values for <name>):
      * same -- all variables within each set have the same value
      * diff -- all variables within each set have different values from each other
      * not-all-same -- at least one of the variables in each set has a 
        different value from other variables in the set

See the files in `csps/` for examples.

## Maze file format

Mazes are defined using the following characters:

    w -- wall; the agent may not pass through these spots
    s -- the start of the maze (entry point)
    e -- the end of the maze (exit point)
      (space) -- a open spot that the agent may freely move into


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

The Python implementation does not currently support CSPs.

## Java

This section assumes you're using a bash-like shell (GitBash on Windows or 
bash or zsh on macOS). PowerShell and CMD will fail due to the 
wildcards (*) in the paths of the `javac` command.

First, navigate to the `java/` folder:

    cd java

Then compile:

    javac -d bin csc460/*.java csc460/*/*.java

### Search problems

To solve a search problem in Java, do something like:

    java -cp bin csc460.drivers.SearchDriver -p=maze -a=bfs -f=../mazes/maze01.txt

To see a list of all options, do:

    java -cp bin csc460.drivers.SearchDriver

### CSP problems

To solve a CSP problem in Java, do something like:

    java -cp bin csc460.drivers.CSPDriver backtracking ../csps/schedule1.csp

To see a list of all options, do:

    java -cp bin csc460.drivers.CSPDriver

## CSP Translator
to Use the CSP Translator in Java do something like:

 java -cp bin csc460.drivers.SudokuTranslator ../csps/sudokuBoard.txt

to see an explaination of the command do:
 java -cp bin csc460.drivers.SudokuTranslator