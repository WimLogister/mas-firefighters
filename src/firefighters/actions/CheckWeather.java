package firefighters.actions;

import firefighters.agent.Agent;

public class CheckWeather implements PrimitiveAction {

	@Override
	public void execute(Agent agent) {
		agent.checkWeather();		
	}

	@Override
	/** Agent can always check the weather */
	public boolean checkPreconditions(Agent agent) {
		return true;
	}
}
