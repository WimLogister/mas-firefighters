package firefighters.actions;

import java.util.List;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import firefighters.agent.Agent;

@RequiredArgsConstructor
public class Plan {
	@NonNull @Getter private List<AbstractAction> steps;
	
  public void executeNextStep(Agent agent) {

	  steps.remove(0).execute(agent);
	}
	
  public boolean isFinished() {
    return steps.isEmpty();
  }

}
