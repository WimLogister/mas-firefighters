package search;

/**
 * Abstraction of a successor function for search algorithms. used to obtain all the possible successors states of a
 * given state, along with then action leading to them and the cost of the action
 */
public abstract class SuccessorFunction<State extends SearchState, Action extends SearchAction<State>> {

  /** Returns the successors of state in the form of [state, action, cost] triplets */
  public abstract Iterable<ImmutableTriple<State, Action, Double>> apply(State state);

}
