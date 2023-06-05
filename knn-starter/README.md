# Machine Learning

This project contains code and data to perform machine learning.

## Directory contents

    bin/ -- initially empty; after compilation, this contains all of the .class
            files.
    csc460/ -- contains the Java source files organized in packages.
    data/ -- CSV files used for training and testing models.

## Building

### Bash-like shells
If using a bash-like shell (GitBash, zsh, etc.), do:

```
javac -d bin csc460/*/*.java
```

The above command will work even if you add new .java files, as long as they
aren't in sub-subdirectories.

### Windows w/ PowerShell or Command Prompt
If using PowerShell or Command Prompt, do:

```
javac -d bin csc460/models/kNN.java csc460/data/Dataset.java csc460/data/Observation.java
```

Note that for the latter, you'll need to augment the list of .java files if you
add new ones.

## Running
After you've compiled, you can run the kNN classifier by doing:

```
java -cp bin csc460.models.kNN
```

Follow the instructions.