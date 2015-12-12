package search;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Set;

import firefighters.agent.Agent;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Class that performs A* search
 * 
 * @author Giannis Papadopoulos
 */

//@formatter:off
@RequiredArgsConstructor
public class AstarSearch<State extends SearchState, 
          					     Action extends SearchAction<State>,
          						   S extends SuccessorFunction<State, Action>,
          						   H extends HeuristicFunction<State>,
          						   G extends GoalTest<State>> {
	//@formatter:on

	@NonNull
	private SuccessorFunction<State, Action> successorFunction;
	/** The assumption is that the heuristic function is admissible but not necessarily consistent. */
  @NonNull
	private HeuristicFunction<State> heuristicFunction;
	@NonNull
	private GoalTest<State> goalTest;

  /** The search will be terminated if the cost exceeds this value */
  private double costCutoffThreshold = Double.POSITIVE_INFINITY;

  @Getter
  private boolean cutoffOccured;

	@Getter
	private int nodesExpanded;
	
	@Getter
	private Agent agent;

  public AstarSearch(Agent agent, SuccessorFunction<State, Action> successorFunction,
                     HeuristicFunction<State> heuristicFunction,
                     GoalTest<State> goalTest,
                     double costCutoffThreshold) {
	this.agent = agent;
    this.successorFunction = successorFunction;
    this.heuristicFunction = heuristicFunction;
    this.goalTest = goalTest;
    this.costCutoffThreshold = costCutoffThreshold;
  }


	public SearchNode<State, Action> search(State startState) {
		SearchNode<State, Action> start = new SearchNode<State, Action>(null, startState, null, 0);
		return search(start);
	}

	private SearchNode<State, Action> search(SearchNode<State, Action> startNode) {
		nodesExpanded = 0;
		PriorityQueue<SearchNode<State, Action>> frontier = new PriorityQueue<SearchNode<State, Action>>(
																											10,
																											new AstarComparator());
		frontier.add(startNode);
		Set<State> explored = new HashSet<State>();
		while (!frontier.isEmpty()) {
			SearchNode<State, Action> current = frontier.poll();
			if (goalTest.check(current.getState())) {
				return current;
			}
      if (current.getCost() > costCutoffThreshold) {
        cutoffOccured = true;
        return null;
      }
			if (!explored.contains(current.getState())) {
				nodesExpanded++;
				explored.add(current.getState());
				for (ImmutableTriple<State, Action, Double> pair : successorFunction.apply(current.getState())) {
					SearchNode<State, Action> n = new SearchNode<State, Action>(current, pair.left, pair.middle,
																				current.getCost() + pair.right);
					frontier.add(n);
				}
			}
		}
		return null;
	}

	/** Returns a path from the startState to a state that passes the goal test, or null */
	public Path<State, Action> findPath(State startState) {
		SearchNode<State, Action> result = search(startState);
		if (result == null) {
			return new Path<State, Action>(startState, null, null, -1);
		}
		State goalState = result.getState();
		double cost = result.getCost();
		LinkedList<Action> route = new LinkedList<Action>();
		while (!result.isRoot()) {
			route.addFirst(result.getAction());
			result = result.getParent();
		}
		Path<State, Action> path = new Path<State, Action>(startState, goalState, route, cost);
		return path;
	}

  private class AstarComparator
			implements Comparator<SearchNode<State, Action>> {

		@Override
		public int compare(SearchNode<State, Action> n1, SearchNode<State, Action> n2) {
			//@formatter:off
			double diff = ((n1.getCost() + heuristicFunction.evaluate(n1.getState()) 
						   - (n2.getCost() + heuristicFunction.evaluate(n2.getState()))));
			if(diff != 0 ) 
				return (int) Math.signum(diff);
			// Else return the one with the highest cost
			return (int) Math.signum(n2.getCost() - n1.getCost());
		}

	}
}
