# Planning for Decision Tree code

## Training

Classes:
  * DecisionTree
    - build(list of observations) -- algorithm from slides
    - findBestThreshold(list of observations, feature) -- returns the threshold of feature that splits the data best according to evaluate()
    - evaluate(split1, split2) -- two lists of observations, returns a number (bigger is better)
    - saveModel(filename)
    - loadModel(filename)
    - classify(observation) -- produces a prediction for the given observation
    - prune()
  * Node
    - isLeafNode()
    - labelDistribution -- a map of labels and their counts in this and descendent nodes
    - n -- the number of training observations represented in this and descendent nodes
    **Only for internal nodes:**
    - feature -- the feature this node splits on
    - threshold -- the threshold on the feature for splitting
    - lessThanEqualChild -- the Node to traverse for observations whose feature is ≤ threshold
    - greaterThanChild -- the Node to traverse for observations whose feature is > threshold
  * Observation -- same as in kNN

Functions:
  * parseDatasFile(filename) -- same as kNN
  * main() -- handles training and running 


## Pseudo code from videos:

```
BuildTree(observations):   --- C4.5 algorithm
    if observations consists of a single class label C:
        return LeafNode({C: size(observations)})
    end if

    best := FeatureInfo(0, null, null)
    for feature in features(observations):
        threshold := threshold on feature that maximizes eval measure
        evalMeasure = eval(observations <= threshold, observations > threshold)
        if evalMeasure > best.evalMeasure:
            best := FeatureInfo(evalMeasure, feature, threshold) 
        end if
    end for

    if best.evalMeasure is 0: --- this means splitting isn't useful
        return LeafNode(map of each label in observations and corresponding count)
    end if

    return InternalNode(best.feature, best.threshold, 
        BuildTree(observations <= best.threshold),
        BuildTree(observations > best.threshold),
        size(observations)
    )
```

Helper classes from slides:

```
class InternalNode:
    feature
    threshold
    lessThanEqualChild
    greaterThanChild
    n

class LeafNode:
    labelDistribution

class FeatureInfo:
    evalMeasure
    feature
    threshold
```

