package firefighters.actions;

import java.util.List;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import firefighters.agent.Agent;

@RequiredArgsConstructor
public class CompositeAction implements AbstractAction {

	@NonNull private List<PrimitiveAction> actionSequence;
	
	@Override
  public void execute(Agent agent) {
    actionSequence.remove(0).execute(agent);
	}

	@Override
  public boolean checkPreconditions(Agent agent) {
    return actionSequence.get(0).checkPreconditions(agent);
	}
}
