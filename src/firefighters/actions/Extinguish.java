package firefighters.actions;

import firefighters.agent.Agent;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Extinguish implements PrimitiveAction {

	@NonNull private Agent agent;
	
	@Override
	public void execute() {
		agent.hose();
	}
	
	@Override
	public boolean checkPreconditions() {
		return true;
	}

}
