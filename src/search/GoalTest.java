package search;

/** Abstraction of a goal test for search algorithms, used to determine if the search should be terminated */
public abstract class GoalTest<State extends SearchState> {

	/** Goal-test for a given state */
	public abstract boolean check(State s);
}
