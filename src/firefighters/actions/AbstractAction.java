package firefighters.actions;

import firefighters.agent.Agent;

public interface AbstractAction {
	
	/**
	 * Have the agent execute this action.
	 */
  void execute(Agent agent);
	
	  /**
   * Check whether this action can be executed by the specified agent.
   * 
   * @return
   */
  boolean checkPreconditions(Agent agent);
}
