package firefighters.utility;

import firefighters.actions.Plan;
import firefighters.agent.Agent;

public interface UtilityFunction {

  public double calculateUtility(Plan plan, Agent agent);
}
