import heapq
import functools

@functools.total_ordering
class SearchNode:
    '''
    Represents a node in the search tree.
    '''
    def __init__(self, state, pathActions, pathStates, pathCost, priority=0):
        '''
        @param state The state.
        @param pathActions A list of actions that led to the state.
        @param pathStates A list of states that led to this state.
        @param pathCost The cost of the path that led to this state.
        @param priority The node's priority in the queue (defaults to 0).
        '''
        self.state = state
        self.pathActions = pathActions
        self.pathStates = pathStates
        self.pathCost = pathCost
        self.priority = priority

    def _is_valid_operand(self, other):
        return hasattr(other, 'priority')

    def __eq__(self, other):
        if not self._is_valid_operand(other):
            return NotImplemented
        return self.priority == other.priority

    def __lt__(self, other):
        if not self._is_valid_operand(other):
            return NotImplemented
        return self.priority < other.priority

class BFS:
    '''
    Breadth first search -- explores states based on distance from the starting
    state (closest first).
    '''
    def __init__(self, problem):
        self.name = "BFS"
        self.problem = problem
        self.maxFringeSize = 0
        self.statesExpanded = 0
        self.fringe = [SearchNode(problem.start, [], [], 0)]
        self.seen = set()

    def nextState(self):
        '''
        Returns the next state to consider, along with its path and total cost.
        Adds all of its successors to the fringe.

        @return The next state, or None if no more states are left to explore.
        '''
        ## Check if there are no more states to explore.
        if len(self.fringe) == 0:
            return None 

        ## Pick next state off the fringe, along with accounting information
        node = self.fringe.pop(0)
        while node.state in self.seen and len(self.fringe) > 0:
            node = self.fringe.pop(0)
        if node.state in self.seen:
            return None

        self.seen.add(node.state)

        self.expandNode(node)

        self.maxFringeSize = max(self.maxFringeSize, len(self.fringe))

        return node


    def expandNode(self, node):
        '''
        Adds each of the unexpanded successors of the given node's state to the 
        fringe.

        @param node The node in the search tree to expand..
        '''
        self.statesExpanded += 1

        for move,successor,cost,dist in self.problem.successors(node.state):
            ## Skip seen states.
            if successor in self.seen:
                continue

            self.fringe.append(SearchNode(
                successor,
                node.pathActions + [move],
                node.pathStates + [node.state], 
                node.pathCost + cost
            ))

    def getName(self):
        '''
        @return The name of this search algorithm.
        '''
        return self.name

    def getMaxFringeSize(self):
        '''
        @return The max fringe size during the search.
        '''
        return self.maxFringeSize

    def getStatesExpanded(self):
        '''
        @return The number of states expanded during the search.
        '''
        return self.statesExpanded

class DFS:
    '''
    Depth first search -- explores states based on distance from the starting
    state (farthest first).
    '''
    def __init__(self, problem):
        self.name = "DFS"
        self.problem = problem
        self.maxFringeSize = 0
        self.statesExpanded = 0
        self.fringe = [SearchNode(problem.start, [], [], 0)]
        self.seen = set()

    def nextState(self):
        '''
        Returns the next state to consider, along with its path and total cost.
        Adds all of its successors to the fringe.

        @return The next state, or None if no more states are left to explore.
        '''
        ## Check if there are no more states to explore.
        if len(self.fringe) == 0:
            return None 

        ## Pick next state off the fringe, along with accounting information
        node = self.fringe.pop()
        while node.state in self.seen and len(self.fringe) > 0:
            node = self.fringe.pop()
        if node.state in self.seen:
            return None

        self.seen.add(node.state)

        ## Exapand the node
        self.expandNode(node)

        self.maxFringeSize = max(self.maxFringeSize, len(self.fringe))

        return node


    def expandNode(self, node):
        '''
        Adds each of the unexpanded successors of the given node's state to the 
        fringe.

        @param node The node in the search tree to expand..
        '''
        self.statesExpanded += 1

        for move,successor,cost,dist in self.problem.successors(node.state):
            ## Skip seen states.
            if successor in self.seen:
                continue

            self.fringe.append(SearchNode(
                successor,
                node.pathActions + [move],
                node.pathStates + [node.state], 
                node.pathCost + cost
            ))

    def getName(self):
        '''
        @return The name of this search algorithm.
        '''
        return self.name

    def getMaxFringeSize(self):
        '''
        @return The max fringe size during the search.
        '''
        return self.maxFringeSize

    def getStatesExpanded(self):
        '''
        @return The number of states expanded during the search.
        '''
        return self.statesExpanded

class IterativeDeepening:
    '''
    Iterative deepening (ID) -- explores states using DFS at increasing depths.
    '''
    def __init__(self, problem):
        self.name = "Iterative deepening"
        self.problem = problem
        self.maxFringeSize = 0
        self.statesExpanded = 0
        self.fringe = [SearchNode(problem.start, [], [], 0)]
        self.seen = set()
        self.depth = 0
        self.depthReached = 0


    def nextNodeOfNextDepth(self):
        '''
        Checks if the depth can be increased; if so, gets the first node of 
        the next pass. Otherwise, all nodes have been explored and there is no
        solution.
        
        @return None if no solution; first node of next pass if the depth can
                be increased.
        '''
        ## No more states to explore in the entire problem state space.
        if self.depthReached < self.depth:
            return None 

        ## No more states to explore at the current depth, but there may be
        ## more at the next depth level.
        else:
            self.depth += 1
            self.depthReached = 0
            self.fringe = [SearchNode(self.problem.start, [], [], 0)]
            self.seen = set()
            return self.nextState()

    def nextState(self):
        '''
        Returns the next state to consider, along with its path and total cost.
        Adds all of its successors to the fringe.

        @return The next state, or None if no more states are left to explore.
        '''
        ## Check if no more states; progress to next depth level if necessary.
        if len(self.fringe) == 0:
            return self.nextNodeOfNextDepth()

        ## Pick next state off the fringe, along with accounting information
        node = self.fringe.pop()
        while node.state in self.seen and len(self.fringe) > 0:
            node = self.fringe.pop()
        ## Check if no more states; progress to next depth level if necessary.
        if node.state in self.seen: 
            return self.nextNodeOfNextDepth()

        self.seen.add(node.state)

        if len(node.pathActions) < self.depth:
            self.expandNode(node)

        self.maxFringeSize = max(self.maxFringeSize, len(self.fringe))
        self.depthReached = max(self.depthReached, len(node.pathActions))

        return node


    def expandNode(self, node):
        '''
        Adds each of the unexpanded successors of the given node's state to the 
        fringe.

        @param node The node in the search tree to expand..
        '''
        self.statesExpanded += 1

        for move,successor,cost,dist in self.problem.successors(node.state):
            ## Skip seen states.
            if successor in self.seen:
                continue

            self.fringe.append(SearchNode(
                successor,
                node.pathActions + [move],
                node.pathStates + [node.state], 
                node.pathCost + cost
            ))

    def getName(self):
        '''
        @return The name of this search algorithm.
        '''
        return self.name

    def getMaxFringeSize(self):
        '''
        @return The max fringe size during the search.
        '''
        return self.maxFringeSize

    def getStatesExpanded(self):
        '''
        @return The number of states expanded during the search.
        '''
        return self.statesExpanded


class UCS:
    '''
    Uniform cost search -- explores states based on cost from the starting
    state (lowest cost first).
    '''
    def __init__(self, problem):
        self.name = "UCS"
        self.problem = problem
        self.maxFringeSize = 0
        self.statesExpanded = 0
        self.fringe = [SearchNode(problem.start, [], [], 0)]
        self.seen = set()

    def nextState(self):
        '''
        Returns the next state to consider, along with its path and total cost.
        Adds all of its successors to the fringe.

        @return The next state, or None if no more states are left to explore.
        '''
        ## Check if there are no more states to explore.
        if len(self.fringe) == 0:
            return None 

        ## Pick next state off the fringe, along with accounting information
        node = heapq.heappop(self.fringe)
        while node.state in self.seen and len(self.fringe) > 0:
            node = heapq.heappop(self.fringe)
        if node.state in self.seen:
            return None

        self.seen.add(node.state)

        self.expandNode(node)

        self.maxFringeSize = max(self.maxFringeSize, len(self.fringe))

        return node


    def expandNode(self, node):
        '''
        Adds each of the unexpanded successors of the given node's state to the 
        fringe.

        @param node The node in the search tree to expand..
        '''
        self.statesExpanded += 1

        for move,successor,cost,dist in self.problem.successors(node.state):
            ## Skip seen states.
            if successor in self.seen:
                continue

            heapq.heappush(self.fringe, SearchNode(
                successor,
                node.pathActions + [move],
                node.pathStates + [node.state], 
                node.pathCost + cost,
                node.pathCost + cost
            ))

    def getName(self):
        '''
        @return The name of this search algorithm.
        '''
        return self.name

    def getMaxFringeSize(self):
        '''
        @return The max fringe size during the search.
        '''
        return self.maxFringeSize

    def getStatesExpanded(self):
        '''
        @return The number of states expanded during the search.
        '''
        return self.statesExpanded



class Greedy:
    '''
    Greedy search -- explores states based on their estimated distance to the
    goal state (closer to the goal = expanded sooner)
    '''
    def __init__(self, problem):
        self.name = "Greedy"
        self.problem = problem
        self.maxFringeSize = 0
        self.statesExpanded = 0
        ## Set the priority of the start state to h(x) -- we're calling it the
        ## "distance" to the goal in this impelementation.
        self.fringe = [SearchNode(problem.start, [], [], 0, problem.getDistance(problem.start))]
        self.seen = set()

    def nextState(self):
        '''
        Returns the next state to consider, along with its path and total cost.
        Adds all of its successors to the fringe.

        @return The next state, or None if no more states are left to explore.
        '''
        ## Check if there are no more states to explore.
        if len(self.fringe) == 0:
            return None 

        ## Pick next state off the fringe, along with accounting information
        node = heapq.heappop(self.fringe)
        while node.state in self.seen and len(self.fringe) > 0:
            node = heapq.heappop(self.fringe)
        if node.state in self.seen:
            return None

        self.seen.add(node.state)

        self.expandNode(node)

        self.maxFringeSize = max(self.maxFringeSize, len(self.fringe))

        return node


    def expandNode(self, node):
        '''
        Adds each of the unexpanded successors of the given node's state to the 
        fringe.

        @param node The node in the search tree to expand..
        '''
        self.statesExpanded += 1

        ## dist is h(x) (the estimated distance to the goal state)
        for move,successor,cost,dist in self.problem.successors(node.state):
            ## Skip seen states.
            if successor in self.seen:
                continue

            heapq.heappush(self.fringe, SearchNode(
                successor,
                node.pathActions + [move],
                node.pathStates + [node.state], 
                node.pathCost + cost, ## Backward cost: g(x)
                dist ## Forward cost: h(x)
            ))

    def getName(self):
        '''
        @return The name of this search algorithm.
        '''
        return self.name

    def getMaxFringeSize(self):
        '''
        @return The max fringe size during the search.
        '''
        return self.maxFringeSize

    def getStatesExpanded(self):
        '''
        @return The number of states expanded during the search.
        '''
        return self.statesExpanded



class AStar:
    '''
    Greedy search -- explores states based on their estimated distance to the
    goal state (closer to the goal = expanded sooner)
    '''
    def __init__(self, problem):
        self.name = "Greedy"
        self.problem = problem
        self.maxFringeSize = 0
        self.statesExpanded = 0
        ## Set the priority of the start state to h(x) -- we're calling it the
        ## "distance" to the goal in this impelementation.
        self.fringe = [SearchNode(problem.start, [], [], 0, problem.getDistance(problem.start))]
        self.seen = set()

    def nextState(self):
        '''
        Returns the next state to consider, along with its path and total cost.
        Adds all of its successors to the fringe.

        @return The next state, or None if no more states are left to explore.
        '''
        ## Check if there are no more states to explore.
        if len(self.fringe) == 0:
            return None 

        ## Pick next state off the fringe, along with accounting information
        node = heapq.heappop(self.fringe)
        while node.state in self.seen and len(self.fringe) > 0:
            node = heapq.heappop(self.fringe)
        if node.state in self.seen:
            return None

        self.seen.add(node.state)

        self.expandNode(node)

        self.maxFringeSize = max(self.maxFringeSize, len(self.fringe))

        return node


    def expandNode(self, node):
        '''
        Adds each of the unexpanded successors of the given node's state to the 
        fringe.

        @param node The node in the search tree to expand..
        '''
        self.statesExpanded += 1

        ## dist is h(x) (the estimated distance to the goal state)
        for move,successor,cost,dist in self.problem.successors(node.state):
            ## Skip seen states.
            if successor in self.seen:
                continue

            heapq.heappush(self.fringe, SearchNode(
                successor,
                node.pathActions + [move],
                node.pathStates + [node.state], 
                node.pathCost + cost, ## Backward cost: g(x)
                dist ## Forward cost: h(x)
            ))

    def getName(self):
        '''
        @return The name of this search algorithm.
        '''
        return self.name

    def getMaxFringeSize(self):
        '''
        @return The max fringe size during the search.
        '''
        return self.maxFringeSize

    def getStatesExpanded(self):
        '''
        @return The number of states expanded during the search.
        '''
        return self.statesExpanded