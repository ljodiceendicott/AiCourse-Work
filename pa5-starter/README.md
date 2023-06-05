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
javac -d bin csc460/models/DecisionTree.java csc460/data/Dataset.java csc460/data/Observation.java csc460/evaluation/BinaryClassificationEvaluation.java csc460/evaluation/RegressionEvaluation.java
```

Note that for the latter, you'll need to augment the list of .java files if you
add new ones.

## Running
After you've compiled, there are two steps that need to be taken to use
decision trees. First, you must build (train) a model, like this:

```
java -cp bin csc460.models.DecisionTree -train data/sms/train.csv sms-model.dat
```

Then you need to make predictions, like this:

```
java -cp bin csc460.models.DecisionTree -predict data/sms/dev.csv sms-model.dat
```

To evaluate prediction, use the `--eval` and `--positive=PC` flags:

```
java -cp bin csc460.models.DecisionTree -predict data/sms/dev.csv sms-model.dat --eval --positive=1
```

To see all options, run the following:

```
java -cp bin csc460.models.DecisionTree
```