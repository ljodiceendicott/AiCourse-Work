# Adversarial Search

## Directory contents

    bin/ -- initially empty; after compilation, this contains all of the .class
            files.
    src/ -- contains the Java source files.

## Building

### Bash-like shells
If using a bash-like shell (GitBash, zsh, etc.), do:

```
javac -d bin src/*.java
```

The above command will work even if you add new .java files, as long as they
aren't in subdirectories.

### Windows w/ PowerShell or Command Prompt
If using PowerShell or Command Prompt, do:

```
javac -d bin src/TicTacToe.java src/MinimaxGen.java src/Game.java
```

or for Mancala

```
javac -d bin src/Mancala.java src/MinimaxGen.java src/Game.java
```

Note that for the latter, you'll need to augment the list of .java files if you
add new ones.

## Running
After you've compiled, you can play a game of tic-tac-toe by doing:

```
java -cp bin TicTacToe
```
After you've compiled, you can play a game of Mancala by doing:

```
java -cp bin Mancala
```


Follow the instructions.

### Logging
The Mimimax implementaion has a logging feature that allows you to see what's
happening down to a pre-specified depth. To use this, just specify the depth
you'd like to see logging turned on for when you run the program:

```
java -cp bin TicTacToe 5
```
or 
```
java -cp bin Mancala 5
```
This will log down to ply 5 of the Minimax search tree.

### Depth define
the Minimax implentation has a depth defining feature that allows you to determine 
how far down the ai will look. to you this, just specify the depth you would like to use
when running the program. NOTE: you must also put in a value for logging( can put 0 in if you do not want logging)

```
java -cp bin TicTacToe 5 3
```

```
java -cp bin Mancala 5 3
```
This will log down to ply 5 of the Minimax search tree. using a depth for the AI of 3
