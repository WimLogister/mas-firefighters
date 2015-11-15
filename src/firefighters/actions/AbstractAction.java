package firefighters.actions;

public interface AbstractAction {
	
	/**
	 * Have the agent execute this action.
	 */
	void execute();
	
	/**
	 * Check whether this action can be executed.
	 * @return
	 */
	boolean checkPreconditions();
}
