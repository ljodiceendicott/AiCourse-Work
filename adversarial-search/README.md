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
javac -d bin src/TicTacToe.java src/Minimax.java
```

Note that for the latter, you'll need to augment the list of .java files if you
add new ones.

## Running
After you've compiled, you can play a game of tic-tac-toe by doing:

```
java -cp bin TicTacToe
```

Follow the instructions.

### Logging
The Mimimax implementaion has a logging feature that allows you to see what's
happening down to a pre-specified depth. To use this, just specify the depth
you'd like to see logging turned on for when you run the program:

```
java -cp bin TicTacToe 5
```

This will log down to ply 5 of the Minimax search tree.
